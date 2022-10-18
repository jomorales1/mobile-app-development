package com.example.webservice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Details extends AppCompatActivity {
    TextView nameText;
    TextView osText;
    TextView ramText;
    TextView storageText;
    TextView typeText;
    TextView speedText;
    TextView brandText;
    TextView availabilityText;
    TextView valueText;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        Bundle extras = getIntent().getExtras();
        nameText = (TextView) findViewById(R.id.name);
        osText = (TextView) findViewById(R.id.os);
        ramText = (TextView) findViewById(R.id.ram);
        storageText = (TextView) findViewById(R.id.storage);
        typeText = (TextView) findViewById(R.id.type);
        speedText = (TextView) findViewById(R.id.speed);
        brandText = (TextView) findViewById(R.id.brand);
        availabilityText = (TextView) findViewById(R.id.availability);
        valueText = (TextView) findViewById(R.id.value);
        button = (Button) findViewById(R.id.back_button);
        if (extras != null) {
            String name = extras.getString("name");
            nameText.setText(name);
            String os = extras.getString("os");
            osText.setText(os);
            String ram = extras.getString("ram");
            ramText.setText(ram);
            String storage = extras.getString("storage");
            storageText.setText(storage);
            String type = extras.getString("type");
            typeText.setText(type);
            String speed = extras.getString("speed");
            speedText.setText(speed);
            String brand = extras.getString("brand");
            brandText.setText(brand);
            String availability = extras.getString("availability");
            availabilityText.setText(availability);
            String value = extras.getString("value");
            valueText.setText(value);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }
}
