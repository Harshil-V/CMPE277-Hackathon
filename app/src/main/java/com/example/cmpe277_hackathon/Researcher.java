package com.example.cmpe277_hackathon;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Researcher extends AppCompatActivity {

    Spinner spinner;
    String[] countries = {"Select","USA", "IN", "CN"};
    FrameLayout frameLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_researcher);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        frameLayout = findViewById(R.id.researcherFrameLayout);
        frameLayout.addView(getLayoutInflater().inflate(R.layout.welcome_layout, null));

        TextView header = findViewById(R.id.activityHeader);
        header.setText("Macroeconomics Researcher");

        spinner = findViewById(R.id.countrySpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, countries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedCountry = countries[position];
                // Handle selected country
                if (!selectedCountry.equals("Select")) {
                    Toast.makeText(Researcher.this, "Selected: " + selectedCountry, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ImageButton macrobtn = findViewById(R.id.macroButton);
        ImageButton agrbtn = findViewById(R.id.agricultureButton);
        ImageButton tradebtn = findViewById(R.id.tradeButton);

        macrobtn.setOnClickListener(view -> {
            frameLayout.removeAllViews();
            View checkboxView = LayoutInflater.from(this).inflate(R.layout.checkbox_layout,null);
            LinearLayout checkboxContainer = checkboxView.findViewById(R.id.checkboxContainer);
            addMacroCheckBoxes(checkboxContainer);
            frameLayout.addView(checkboxView);

        });

        agrbtn.setOnClickListener(view -> {
            frameLayout.removeAllViews();
            View checkboxView = LayoutInflater.from(this).inflate(R.layout.checkbox_layout,null);
            LinearLayout checkboxContainer = checkboxView.findViewById(R.id.checkboxContainer);
            addAgricultureCheckBoxes(checkboxContainer);
            frameLayout.addView(checkboxView);
        });

        tradebtn.setOnClickListener(view -> {
            frameLayout.removeAllViews();
            View checkboxView = LayoutInflater.from(this).inflate(R.layout.checkbox_layout,null);
            LinearLayout checkboxContainer = checkboxView.findViewById(R.id.checkboxContainer);
            addTradeCheckBoxes(checkboxContainer);
            frameLayout.addView(checkboxView);

        });
    }

    private void addMacroCheckBoxes(LinearLayout container) {
        String[] checkboxLabels = {"GDP(USD)", "FDI Inflows(USD)", "FDI Outflows(USD)", "Import/Export Flow"};
        for (String label : checkboxLabels) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(label);
            container.addView(checkBox);
        }
    }
    private void addAgricultureCheckBoxes(LinearLayout container) {
        String[] checkboxLabels = {"Contribution to GDP", "Credit", "Fertilizers", "Fertilizer Production"};
        for (String label : checkboxLabels) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(label);
            container.addView(checkBox);
        }
    }
    private void addTradeCheckBoxes(LinearLayout container) {
        String[] checkboxLabels = {"Reserves", "GNI", "Total Debt", "GNI(current US$"};
        for (String label : checkboxLabels) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(label);
            container.addView(checkBox);
        }
    }
}