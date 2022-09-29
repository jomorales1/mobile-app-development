package com.example.graphicsandsound;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OnlineActivity extends AppCompatActivity {
    private TicTacToeGame game;
    // Buttons making up the board
    private Button mReturn;
    // Various text displayed
    private TextView mInfoTextView;
    private BoardView mBoardView;
    private SharedPreferences mPrefs;
    MediaPlayer mHostMediaPlayer;
    MediaPlayer mSPlayerMediaPlayer;

    DatabaseReference reference;
    DatabaseReference gameReference;
    private String gameName;
    private boolean host;

    private boolean mGameOver = false;
    private String next = "";
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_game);
        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
        host = mPrefs.getBoolean("host", false);
        gameName = mPrefs.getString("currentGame", "");

        reference = FirebaseDatabase.getInstance().getReference("games/" + gameName + "/state");

        mReturn = findViewById(R.id.go_back4);
        mReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mGameOver) {
                    reference.setValue("canceled");
                }
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
        if (host) {
            mInfoTextView.setText("Waiting for second player...");
        } else {
            mInfoTextView.setText("Waiting for host to start the game...");
        }
        gameReference = FirebaseDatabase.getInstance().getReference("games/" + gameName);
        gameReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!mGameOver) {
                    if (host) {
                        if (snapshot.child("state").getValue().toString().equals("ready")) {
                            startNewGame();
                            gameReference.child("state").setValue("started");
                        } else if (snapshot.child("state").getValue().toString().equals("started")) {
                            next = (String) snapshot.child("next").getValue();
                            if (next.equals("host") && snapshot.child("second_move").exists()) {
                                Long data = (Long) snapshot.child("second_move").getValue();
                                Integer secondMove = data.intValue();
                                if (game.freeCell(secondMove)) {
                                    if (game.setMove(TicTacToeGame.COMPUTER_PLAYER, secondMove)) {
                                        mBoardView.invalidate(); // Redraw the board
                                    }
                                    mSPlayerMediaPlayer.start();
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
                                        mInfoTextView.setText(R.string.result_splayer_wins);
                                    }
                                    if (winner != 0) {
                                        gameReference.child("state").setValue("finished");
                                    }
                                }
                            }
                        }
                    } else if (snapshot.child("state").getValue().toString().equals("started")) {
                        next = (String) snapshot.child("next").getValue();
                        if (next.equals("second") && snapshot.child("host_move").exists()) {
                            Long data = (Long) snapshot.child("host_move").getValue();
                            Integer hostMove = data.intValue();
                            if (game.freeCell(hostMove)) {
                                if (game.setMove(TicTacToeGame.HUMAN_PLAYER, hostMove)) {
                                    mBoardView.invalidate(); // Redraw the board
                                }
                                mHostMediaPlayer.start();
                                int winner = game.checkForWinner();
                                if (winner != 0) {
                                    mGameOver = true;
                                }
                                if (winner == 0) {
                                    mInfoTextView.setText(R.string.turn_human);
                                } else if (winner == 1) {
                                    mInfoTextView.setText(R.string.result_tie);
                                } else if (winner == 2) {
                                    mInfoTextView.setText(R.string.result_host_wins);
                                } else {
                                    mInfoTextView.setText(R.string.result_human_wins);
                                }
                                if (winner != 0) {
                                    gameReference.child("state").setValue("finished");
                                }
                            }
                        } else if (next.equals("second") && !host && !snapshot.child("host_move").exists() && !snapshot.child("second_move").exists()) {
                            mInfoTextView.setText(R.string.turn_human);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void startNewGame() {
        game.clearBoard();
        mGameOver = false;
        game.clearBoard();
        mBoardView.invalidate();
        // Host goes first
        if (Math.random() <= 0.5) {
            mInfoTextView.setText(R.string.first_human);
            gameReference.child("next").setValue("host");
        } else {
            mInfoTextView.setText(R.string.turn_splayer);
            gameReference.child("next").setValue("second");
        }
    }

    private boolean setMove(char player, int location) {
        if (game.setMove(player, location)) {
            mBoardView.invalidate(); // Redraw the board
            return true;
        }
        return false;
    }

    // Listen for touches on the board
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;
            if (host && next.equals("host")) {
                if (!mGameOver && setMove(TicTacToeGame.HUMAN_PLAYER, pos)){
                    setMove(TicTacToeGame.HUMAN_PLAYER, pos);
                    mHostMediaPlayer.start();
                    // If no winner yet, let the computer make a move
                    int winner = game.checkForWinner();
                    if (winner != 0) {
                        mGameOver = true;
                    }
                    if (winner == 0) {
                        mInfoTextView.setText(R.string.turn_splayer);
                    } else if (winner == 1) {
                        mInfoTextView.setText(R.string.result_tie);
                    } else if (winner == 2) {
                        mInfoTextView.setText(R.string.result_human_wins);
                    } else if (winner == 3) {
                        mInfoTextView.setText(R.string.result_splayer_wins);
                    }
                    gameReference.child("host_move").setValue(pos);
                    gameReference.child("next").setValue("second");
                    if (winner != 0) {
                        gameReference.child("state").setValue("finished");
                    }
                }
            } else if (!host && next.equals("second")) {
                if (!mGameOver && setMove(TicTacToeGame.COMPUTER_PLAYER, pos)){
                    setMove(TicTacToeGame.COMPUTER_PLAYER, pos);
                    mSPlayerMediaPlayer.start();
                    // If no winner yet, let the computer make a move
                    int winner = game.checkForWinner();
                    if (winner != 0) {
                        mGameOver = true;
                    }
                    if (winner == 0) {
                        mInfoTextView.setText(R.string.turn_splayer);
                    } else if (winner == 1) {
                        mInfoTextView.setText(R.string.result_tie);
                    } else if (winner == 2) {
                        mInfoTextView.setText(R.string.result_host_wins);
                    } else if (winner == 3) {
                        mInfoTextView.setText(R.string.result_human_wins);
                    }
                    gameReference.child("second_move").setValue(pos);
                    gameReference.child("next").setValue("host");
                    if (winner != 0) {
                        gameReference.child("state").setValue("finished");
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
        mHostMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.human);
        mSPlayerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.computer);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mHostMediaPlayer.release();
        mSPlayerMediaPlayer.release();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
