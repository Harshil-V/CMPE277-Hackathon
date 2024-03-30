package com.example.cmpe277_hackathon;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

public class Researcher extends AppCompatActivity {

    Spinner spinner;
    String[] countries = {"Select","USA", "India", "China"};
    FrameLayout frameLayout;
    Button show;
    private String selectedCountry = "Select";
    private ArrayList<String> selectedCheckboxes = new ArrayList<>();

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
                selectedCountry = countries[position];
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
        ImageButton openai = findViewById(R.id.openaiButton);



        macrobtn.setOnClickListener(view -> {

            frameLayout.removeAllViews();
            selectedCheckboxes.clear();
            View checkboxView = LayoutInflater.from(this).inflate(R.layout.checkbox_layout,null);
            show = checkboxView.findViewById(R.id.show);
            show.setOnClickListener(view2 -> {
                if (selectedCountry.equals("Select")) {
                    Toast.makeText(Researcher.this, "Select Country", Toast.LENGTH_SHORT).show();
                } else {
                    frameLayout.removeAllViews();
                    replaceFragment(new GraphFragment(selectedCountry, selectedCheckboxes));
                }

            });
            LinearLayout checkboxContainer = checkboxView.findViewById(R.id.checkboxContainer);
            addMacroCheckBoxes(checkboxContainer);
            frameLayout.addView(checkboxView);

        });

        agrbtn.setOnClickListener(view -> {
            frameLayout.removeAllViews();
            selectedCheckboxes.clear();
            View checkboxView = LayoutInflater.from(this).inflate(R.layout.checkbox_layout,null);
            show = checkboxView.findViewById(R.id.show);
            show.setOnClickListener(view2 -> {
                if (selectedCountry.equals("Select")) {
                    Toast.makeText(Researcher.this, "Select Country", Toast.LENGTH_SHORT).show();
                } else {
                    frameLayout.removeAllViews();
                    replaceFragment(new GraphFragment(selectedCountry, selectedCheckboxes));
                }
            });
            LinearLayout checkboxContainer = checkboxView.findViewById(R.id.checkboxContainer);
            addAgricultureCheckBoxes(checkboxContainer);
            frameLayout.addView(checkboxView);
        });

        tradebtn.setOnClickListener(view -> {
            frameLayout.removeAllViews();
            selectedCheckboxes.clear();
            View checkboxView = LayoutInflater.from(this).inflate(R.layout.checkbox_layout,null);
            show = checkboxView.findViewById(R.id.show);
            show.setOnClickListener(view2 -> {
                if (selectedCountry.equals("Select")) {
                    Toast.makeText(Researcher.this, "Select Country", Toast.LENGTH_SHORT).show();
                } else {
                    frameLayout.removeAllViews();
                    replaceFragment(new GraphFragment(selectedCountry, selectedCheckboxes));
                }
            });
            LinearLayout checkboxContainer = checkboxView.findViewById(R.id.checkboxContainer);
            addTradeCheckBoxes(checkboxContainer);
            frameLayout.addView(checkboxView);
        });

        openai.setOnClickListener(view -> {
            frameLayout.removeAllViews();
            replaceFragment(new ChatGPTFragment());
//            Intent intent = new Intent(this, ChatGPT.class);
//            startActivity(intent);

        });
    }

    private void addMacroCheckBoxes(LinearLayout container) {
        String[] checkboxLabels = {"GDP(USD)", "FDI Inflows(USD)", "FDI Outflows(USD)", "Import/Export Flow"};
        for (String label : checkboxLabels) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(label);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedCheckboxes.add(label);
                } else {
                    selectedCheckboxes.remove(label);
                }
            });
            container.addView(checkBox);
        }
    }
    private void addAgricultureCheckBoxes(LinearLayout container) {
        String[] checkboxLabels = {"Agricultural Contribution (% GDP)",
                "Manufacturing(%GDP)",
                "Agriculture, forestry, and fishing, value added (annual % growth)",
                "Fertilizer consumption (kilograms per hectare of arable land)",
                "Fertilizer consumption (% of fertilizer production)"};
        for (String label : checkboxLabels) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(label);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedCheckboxes.add(label);
                } else {
                    selectedCheckboxes.remove(label);
                }
            });
            container.addView(checkBox);
        }
    }
    private void addTradeCheckBoxes(LinearLayout container) {
        String[] checkboxLabels = {"Total reserves in months of imports",
                "Total reserves (includes gold, current US$)",
                "Total reserves (% of total external debt)",
                "GNI (current US$)"};
        for (String label : checkboxLabels) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(label);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedCheckboxes.add(label);
                } else {
                    selectedCheckboxes.remove(label);
                }
            });
            container.addView(checkBox);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.researcherFrameLayout, fragment);
        transaction.addToBackStack(null); // Optional: Add to back stack for back navigation
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectedCheckboxes.clear();
    }
}