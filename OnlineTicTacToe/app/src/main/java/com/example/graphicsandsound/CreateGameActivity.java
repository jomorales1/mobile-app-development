package com.example.graphicsandsound;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CreateGameActivity extends AppCompatActivity {
    EditText editText;
    Button submitName;
    Button bReturn;
    DatabaseReference reference;
    String gameName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_game);

        editText = findViewById(R.id.game_name);
        submitName = findViewById(R.id.create_game);
        bReturn = findViewById(R.id.go_back2);

//        reference.child("games").child(gameName).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()) {
//                    Log.e("firebase", "Error getting data", task.getException());
//                }
//                else {
//                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
//                }
//            }
//        });

        submitName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameName = editText.getText().toString();
                editText.setText("");
                if (!gameName.isEmpty()) {
                    reference = FirebaseDatabase.getInstance().getReference("available/" + gameName);
                    addEventListener();
                    reference.setValue("").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Write was successful!
                                    // ...
                                    Log.i("firebase" , "done");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Write failed see log for details
                                    Log.i("firebase" , "e : " + e );
                                }
                            });
                    SharedPreferences preferences = getSharedPreferences("PREFS", 0);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("currentGame", gameName);
                    editor.putBoolean("created", true);
                    editor.apply();
                }
            }
        });
        bReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }
    private void addEventListener() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                startActivity(new Intent(getApplicationContext(), OnlineActivity.class));
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("firebase", "loadPost:onCancelled", error.toException());
            }
        });
    }
}
