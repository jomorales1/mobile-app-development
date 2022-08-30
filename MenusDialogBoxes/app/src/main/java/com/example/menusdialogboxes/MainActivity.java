package com.example.menusdialogboxes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private TicTacToeGame game;
    // Buttons making up the board
    private Button[] mBoardButtons;
    // Various text displayed
    private TextView mInfoTextView;
    private TextView mHumanWinsTextView;
    private TextView mTiesTextView;
    private TextView mAndroidWinsTextView;

    private boolean mGameOver = false;
    private int human_wins = 0;
    private int ties = 0;
    private int android_wins = 0;

    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;

    // Handles clicks on the game board buttons
    private class ButtonClickListener implements View.OnClickListener {
        int location;
        public ButtonClickListener(int location) {
            this.location = location;
        }
        public void onClick(View view) {
            if (mBoardButtons[location].isEnabled() && !mGameOver) {
                setMove(TicTacToeGame.HUMAN_PLAYER, location);
// If no winner yet, let the computer make a move
                int winner = game.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_computer);
                    int move = game.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    winner = game.checkForWinner();
                }
                if (winner != 0) {
                    mGameOver = true;
                }
                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_human);

                } else if (winner == 1) {
                    mInfoTextView.setText(R.string.result_tie);
                    ties += 1;
                    mTiesTextView.setText("Ties: " + ties);
                } else if (winner == 2) {
                    mInfoTextView.setText(R.string.result_human_wins);
                    human_wins += 1;
                    mHumanWinsTextView.setText("Human: " + human_wins);
                } else {
                    mInfoTextView.setText(R.string.result_computer_wins);
                    android_wins += 1;
                    mAndroidWinsTextView.setText("Android: " + android_wins);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];
        mBoardButtons[0] = (Button) findViewById(R.id.one);
        mBoardButtons[1] = (Button) findViewById(R.id.two);
        mBoardButtons[2] = (Button) findViewById(R.id.three);
        mBoardButtons[3] = (Button) findViewById(R.id.four);
        mBoardButtons[4] = (Button) findViewById(R.id.five);
        mBoardButtons[5] = (Button) findViewById(R.id.six);
        mBoardButtons[6] = (Button) findViewById(R.id.seven);
        mBoardButtons[7] = (Button) findViewById(R.id.eight);
        mBoardButtons[8] = (Button) findViewById(R.id.nine);
        mInfoTextView = (TextView) findViewById(R.id.information);
        mHumanWinsTextView = (TextView) findViewById(R.id.human_wins);
        mTiesTextView = (TextView) findViewById(R.id.ties);
        mAndroidWinsTextView = (TextView) findViewById(R.id.android_wins);
        game = new TicTacToeGame();

        startNewGame();
    }

    private void startNewGame() {
        game.clearBoard();
        mGameOver = false;
        // Reset all buttons
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
        }
        // Human goes first
        if (Math.random() <= 0.5) {
            mInfoTextView.setText(R.string.first_human);
        } else {
            mInfoTextView.setText(R.string.turn_computer);
            int move = game.getComputerMove();
            setMove(TicTacToeGame.COMPUTER_PLAYER, move);
            mInfoTextView.setText(R.string.turn_human);
        }
    }

    private void setMove(char player, int location) {
        game.setMove(player, location);
        mBoardButtons[location].setEnabled(false);
        mBoardButtons[location].setText(String.valueOf(player));
        if (player == TicTacToeGame.HUMAN_PLAYER)
            mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));
        else
            mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.ai_difficulty:
                showDialog(DIALOG_DIFFICULTY_ID);
                return true;
            case R.id.quit:
                showDialog(DIALOG_QUIT_ID);
                return true;
        }
        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch(id) {
            case DIALOG_DIFFICULTY_ID:
                builder.setTitle(R.string.difficulty_choose);
                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert)};
                int selected = 0;
                builder.setSingleChoiceItems(levels, selected,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss(); // Close dialog
                                if (item == 0) {
                                    game.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
                                } else if (item == 1) {
                                    game.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
                                } else {
                                    game.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
                                }
                                Toast.makeText(getApplicationContext(), levels[item],
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog = builder.create();
                break;
            case DIALOG_QUIT_ID:
                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MainActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();
                break;
        }
        return dialog;
    }

}