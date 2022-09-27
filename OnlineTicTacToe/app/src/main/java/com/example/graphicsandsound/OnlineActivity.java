package com.example.graphicsandsound;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class OnlineActivity extends AppCompatActivity {
    private TicTacToeGame game;
    // Buttons making up the board
    private Button mReturn;
    // Various text displayed
    private TextView mInfoTextView;
    private BoardView mBoardView;
    private SharedPreferences mPrefs;
    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;

    private boolean mGameOver = false;
    private boolean humanTurn = false;
    private int difficulty = 0;

    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;
    static final int DIALOG_RESET_SCORES = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_game);

        mReturn = findViewById(R.id.go_back4);
        mReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
        mInfoTextView = (TextView) findViewById(R.id.information_online);
        game = new TicTacToeGame();
        mBoardView = (BoardView) findViewById(R.id.board_online);
        mBoardView.setGame(game);
        // Listen for touches on the board
        mBoardView.setOnTouchListener(mTouchListener);
        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
        difficulty = mPrefs.getInt("difficulty", 0);

        if (savedInstanceState == null) {
            startNewGame();
        }
        else {
            // Restore the game's state
            game.setBoardState(savedInstanceState.getCharArray("board"));
            mGameOver = savedInstanceState.getBoolean("mGameOver");
            mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
            humanTurn = savedInstanceState.getBoolean("humanTurn");
            difficulty = savedInstanceState.getInt("difficulty");
        }
        if (difficulty == 0) {
            game.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
        } else if (difficulty == 1) {
            game.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
        } else {
            game.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
        }
    }

    private void startNewGame() {
        game.clearBoard();
        mGameOver = false;
        game.clearBoard();
        mBoardView.invalidate();
        humanTurn = false;
        // Human goes first
        if (Math.random() <= 0.5) {
            humanTurn = true;
            mInfoTextView.setText(R.string.first_human);
        } else {
            mInfoTextView.setText(R.string.turn_computer);
            int move = game.getComputerMove();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (game.setMove(TicTacToeGame.COMPUTER_PLAYER, move)) {
                        mBoardView.invalidate(); // Redraw the board
                    }
                    mComputerMediaPlayer.start();
                    mInfoTextView.setText(R.string.turn_human);
                    humanTurn = true;
                }
            }, 2000);
        }
    }

    private boolean setMove(char player, int location) {
        if (game.setMove(player, location)) {
            mBoardView.invalidate(); // Redraw the board
            return true;
        }
        return false;
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
            case R.id.reset_scores:
                showDialog(DIALOG_RESET_SCORES);
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
                int selected = difficulty;
                builder.setSingleChoiceItems(levels, selected,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss(); // Close dialog
                                if (item == 0) {
                                    difficulty = 0;
                                    game.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
                                } else if (item == 1) {
                                    difficulty = 1;
                                    game.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
                                } else {
                                    difficulty = 2;
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
                                OnlineActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();
                break;
        }
        return dialog;
    }

    // Listen for touches on the board
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;
            if (humanTurn) {
                if (!mGameOver && setMove(TicTacToeGame.HUMAN_PLAYER, pos)){
                    setMove(TicTacToeGame.HUMAN_PLAYER, pos);
                    mHumanMediaPlayer.start();
                    humanTurn = false;
                    // If no winner yet, let the computer make a move
                    int winner = game.checkForWinner();
                    if (winner == 0) {
                        mInfoTextView.setText(R.string.turn_computer);
                        int move = game.getComputerMove();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                                if (game.setMove(TicTacToeGame.COMPUTER_PLAYER, move)) {
                                    mBoardView.invalidate(); // Redraw the board
                                }
                                mComputerMediaPlayer.start();
                                humanTurn = true;
                                int winner = game.checkForWinner();
                                if (winner != 0) {
                                    mGameOver = true;
                                }
                                if (winner == 0) {
                                    mInfoTextView.setText(R.string.turn_human);
                                } else if (winner == 1) {
                                    mInfoTextView.setText(R.string.result_tie);
                                } else if (winner == 2) {
                                    mInfoTextView.setText(R.string.result_human_wins);
                                } else {
                                    mInfoTextView.setText(R.string.result_computer_wins);
                                }
                            }
                        }, 1500);
                    }
                    if (winner != 0) {
                        mGameOver = true;
                    }
                    if (winner == 0 && humanTurn) {
                        mInfoTextView.setText(R.string.turn_human);

                    } else if (winner == 1) {
                        mInfoTextView.setText(R.string.result_tie);
                    } else if (winner == 2) {
                        mInfoTextView.setText(R.string.result_human_wins);
                    } else if (winner == 3) {
                        mInfoTextView.setText(R.string.result_computer_wins);
                    }
                }
            }
            // So we aren't notified of continued events when finger is moved
            return false;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.human);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.computer);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Save the current scores
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("difficulty", difficulty);
        ed.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharArray("board", game.getBoardState());
        outState.putBoolean("mGameOver", mGameOver);
        outState.putCharSequence("info", mInfoTextView.getText());
        outState.putBoolean("humanTurn", humanTurn);
        outState.putInt("difficulty", difficulty);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        game.setBoardState(savedInstanceState.getCharArray("board"));
        mGameOver = savedInstanceState.getBoolean("mGameOver");
        mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
        humanTurn = savedInstanceState.getBoolean("humanTurn");
        difficulty = savedInstanceState.getInt("difficulty");
    }
}
