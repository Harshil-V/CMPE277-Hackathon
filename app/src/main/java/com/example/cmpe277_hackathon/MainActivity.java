package com.example.cmpe277_hackathon;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TextView header = findViewById(R.id.activityHeader);
        header.setText("Macroeconomic Researcher Food Security Time Series Dashboard");


        ImageButton researcher = findViewById(R.id.researcherButton);
        ImageButton official = findViewById(R.id.govOffButton);

        researcher.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Researcher.class);
            startActivity(intent);
        });

        official.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, GovernmentOfficial.class);
            startActivity(intent);
        });

    }
}