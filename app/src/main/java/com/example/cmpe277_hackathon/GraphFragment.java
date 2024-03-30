package com.example.cmpe277_hackathon;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;

import org.jetbrains.annotations.Contract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GraphFragment extends Fragment {

    EditText startYear;
    EditText endYear;
    Cartesian cartesian;
    AnyChartView anyChartView;
    List<DataEntry> data;
    Button addAnnotation;
    boolean isFirstRow = true;

    String user;

    private String selectedCountry;
    private ArrayList<String> selectedCheckboxes;
    private Map<String,String> fileNames = new HashMap<>();

    public GraphFragment(String selectedCountry, ArrayList<String> selectedCheckboxes, String user) {
        this.selectedCountry = selectedCountry;
        this.selectedCheckboxes = selectedCheckboxes;
        this.user = user;
    }

    private DatabaseHelper dbHelper;
    private String FILE_NAME = "";

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.graph_layout, container, false);

        dbHelper = new DatabaseHelper(getActivity());

        startYear = rootView.findViewById(R.id.startYear);
        endYear = rootView.findViewById(R.id.endYear);
        anyChartView = rootView.findViewById(R.id.any_chart_view);
        fileNames.put("GDP(USD)","GDP(USD).csv");
        fileNames.put("FDI Inflows(USD)","FDI_Inflows(USD).csv");
        fileNames.put("FDI Outflows(USD)","FDI_Outflows(USD).csv");
        fileNames.put("Import/Export Flow", "Import_Export.csv");
        fileNames.put("Agricultural Contribution (% GDP)","Contribution_Agriculture.csv");

        fileNames.put("Manufacturing(%GDP)","Manufacturing(%GDP).csv");
        fileNames.put("Agriculture, forestry, and fishing, value added (annual % growth)","AFFNG.csv");

        fileNames.put("Fertilizer consumption (kilograms per hectare of arable land)", "FC(KG).csv");
        fileNames.put("Fertilizer consumption (% of fertilizer production)", "FC(FP).csv");


        fileNames.put("Total reserves in months of imports","TotalReservesInMonthsPerImport.csv");
        fileNames.put("Total reserves (includes gold, current US$)","TotalReservesInMonthsPerImport.csv");
        fileNames.put("Total reserves (% of total external debt)","TotalReservesInMonthsPerImport.csv");

        fileNames.put("GNI (current US$)","GNI.csv");

        addAnnotation = rootView.findViewById(R.id.addAnnt);

        FILE_NAME = sortCharactersInStringList(selectedCheckboxes);

        if (Objects.equals(user, "Offical")) {
            addAnnotation.setEnabled(false);
            addAnnotation.setVisibility(View.INVISIBLE);
        }

        updateAnnotationButtonText();

        startYear.setText("1960");
        endYear.setText("2020");
        cartesian = AnyChart.line();

        loadDataAndDrawChart();

        startYear.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(startYear.getText().toString().length() == 4) {
                    anyChartView.clear();
                    loadDataAndDrawChart();
                }

            }
        });

        // Add listener to endYear EditText
        endYear.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(endYear.getText().toString().length() == 4) {
                    anyChartView.clear();
                    loadDataAndDrawChart();
                }
            }
        });

        addAnnotation.setOnClickListener(v -> {
            showAddItemDialog(getActivity());

        });

        addAnnotation.setOnLongClickListener(v -> {
            showAnnotationsForGraph(getActivity(), FILE_NAME);
            return true;
        });

        return rootView;
    }


    private void updateAnnotationButtonText() {
        // This assumes you have a method `getAnnotationsCount` that returns the count of annotations
        int currentAnnotationCount = dbHelper.getAnnotationsCount(FILE_NAME);
        if (currentAnnotationCount > 0) {
            addAnnotation.setText("Add Annotation (" + currentAnnotationCount + ")");
        }
    }
    private void showAddItemDialog(Context context) {

        final EditText annotationTextInput = new EditText(context);
        annotationTextInput.setHint("Annotation Text");

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(annotationTextInput);

        FILE_NAME = sortCharactersInStringList(selectedCheckboxes);

        @SuppressLint("SetTextI18n") AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Add New Annotation")
                .setView(layout)
                .setPositiveButton("Save", (dialog1, which) -> {

                    String annotationText = annotationTextInput.getText().toString();
                    dbHelper.insertRecord(FILE_NAME, annotationText);
                    Toast.makeText(context, "Annotation Saved", Toast.LENGTH_LONG).show();
                    updateAnnotationButtonText();
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }


    private void showAnnotationsDialog(Context context, @NonNull List<String> annotations) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Annotations");

        CharSequence[] annotationsArray = annotations.toArray(new CharSequence[0]);

        builder.setItems(annotationsArray, null);

        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAnnotationsForGraph(Context context, String fileName) {
        List<String> annotations = dbHelper.getAnnotationsByGraphName(fileName);
        showAnnotationsDialog(context, annotations);
    }

    private void loadDataAndDrawChart() {
        int start = Integer.parseInt(startYear.getText().toString());
        int end = Integer.parseInt(endYear.getText().toString());

        List<List<DataEntry>> allDatasets = new ArrayList<>();
        for (String checkbox : selectedCheckboxes) {
            String fileName = fileNames.get(checkbox);
            if (fileName != null) {
                List<DataEntry> fileData = parseCSVFile(fileName, start, end);
                allDatasets.add(fileData);
            }
        }

        drawChart(allDatasets);
    }

    private List<DataEntry> parseCSVFile(String fileName, int start, int end) {
        List<DataEntry> data = new ArrayList<>();

        try {
            isFirstRow = true;
            int countryColumnIndex = 1;
            InputStream inputStream = getActivity().getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                if (isFirstRow) {
                    countryColumnIndex = getCountryColumnIndex(line);
                    isFirstRow = false;
                    continue; // Skip the first row
                }
                // Split each line of CSV into tokens
                String[] tokens = line.split(",");
                // Assuming the first token is the X-axis value and the second token is the Y-axis value
                int xValue = Integer.parseInt(tokens[0]);
                double yValue;
                try {
                    yValue = Double.parseDouble(tokens[countryColumnIndex]);
                } catch (Exception e) {
                    Log.e("Exception", "Data Does not exist for year:" + xValue + " for file:" + fileName);
                    continue;
                }

                // Create a DataEntry and add it to the list
                if (xValue >= start && xValue <= end ) {
                    data.add(new ValueDataEntry(xValue, yValue));
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }
    private void drawChart(List<List<DataEntry>> allDatasets) {
        cartesian.removeAllSeries();
//        cartesian.yAxis(0).labels().format("{%Value}{scale:(1000)(1)(1000000)(1e-6)|(k)(M)(G)(µ)}");
        cartesian.xAxis(0).labels().enabled(true);
        cartesian.yAxis(0).labels().enabled(true);
        cartesian.xAxis(0).title("Year");
        cartesian.yAxis(0).title("USD");
        for (int i = 0; i < allDatasets.size(); i++) {
            List<DataEntry> dataset = allDatasets.get(i);
            cartesian.line(dataset).name("Dataset " + (i + 1));
        }
        cartesian.legend().enabled(true);
        anyChartView.setChart(cartesian);
    }

    private int getCountryColumnIndex(String line) {
        String[] columns = line.split(",");
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].contains(selectedCountry)) {
                return i;
            }
        }
        return -1; // Return -1 if the selected country is not found
    }

    /**
     * Takes a list of strings, concatenates them into one string, sorts the characters,
     * and returns the sorted string.
     *
     * @param listOfStrings The list of strings to process.
     * @return A sorted string containing all characters from the list.
     */
    @NonNull
    @Contract("_ -> new")
    public static String sortCharactersInStringList(@NonNull List<String> listOfStrings) {
        StringBuilder combinedString = new StringBuilder();
        for (String str : listOfStrings) {
            combinedString.append(str);
        }

        char[] charArray = combinedString.toString().toCharArray();
        Arrays.sort(charArray);

        return new String(charArray);
    }

}
