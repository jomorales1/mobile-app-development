package com.example.graphicsandsound;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListGamesActivity extends AppCompatActivity {
    ListView listView;
    Button bReturn;
    DatabaseReference reference;
    DatabaseReference selectReference;
//    DatabaseReference gameReference;
    ArrayList<String> games = new ArrayList<>();
    String gameSelected = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_games);

        listView = findViewById(R.id.games_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                gameSelected = games.get(i);
//                gameReference = FirebaseDatabase.getInstance().getReference("games/" + gameSelected + "/second_player");
//                gameReference.setValue("joined");
                SharedPreferences preferences = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("currentGame", gameSelected);
                editor.putBoolean("host", false);
                editor.apply();
                selectReference = FirebaseDatabase.getInstance().getReference("games/" + gameSelected + "/state");
                selectReference.setValue("ready");
                startActivity(new Intent(getApplicationContext(), OnlineActivity.class));
                finish();
            }
        });
        bReturn = findViewById(R.id.go_back3);
        reference = FirebaseDatabase.getInstance().getReference("games");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                games.clear();
                Iterable<DataSnapshot> availableGames = snapshot.getChildren();
                for (DataSnapshot data : availableGames) {
                    System.out.println(data.getValue().toString());
                    if (data.child("state").getValue().toString().equals("created")) {
                        games.add(data.getKey());
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ListGamesActivity.this,
                        android.R.layout.simple_list_item_1, games);
                listView.setAdapter(adapter);
                System.out.println(games.toString());
                Log.println(Log.INFO, "firebase", games.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("firebase", "loadPost:onCancelled", error.toException());
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
}
