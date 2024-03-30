package com.example.cmpe277_hackathon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GraphFragment extends Fragment {

    EditText startYear;
    EditText endYear;
    Cartesian cartesian;
    AnyChartView anyChartView;
    List<DataEntry> data;
    Button addAnnotation;
    boolean isFirstRow = true;

    private DatabaseHelper dbHelper;
    private String FILE_NAME = "GDP(USD).csv";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.graph_layout, container, false);

        dbHelper = new DatabaseHelper(getActivity());


        startYear = rootView.findViewById(R.id.startYear);
        endYear = rootView.findViewById(R.id.endYear);
        anyChartView = rootView.findViewById(R.id.any_chart_view);
        addAnnotation = rootView.findViewById(R.id.addAnnt);


//        Cartesian cartesian = AnyChart.line();
//        List<DataEntry> data = parseCSVFile();

        startYear.setText("1960");
        endYear.setText("2020");

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
                if(startYear.getText().toString().length() == 4) {
                    loadDataAndDrawChart();
                }
            }
        });

        addAnnotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddItemDialog(getActivity());
//                dbHelper.insertRecord(FILE_NAME, "YourAnnotationTextHere");
            }
        });

        addAnnotation.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                showAnnotationsForGraph(getActivity(), FILE_NAME);
                return true;
            }
        });



//        int start = Integer.parseInt(startYear.getText().toString());
//        int end = Integer.parseInt(endYear.getText().toString());
//        List<DataEntry> filteredData = filterDataByYear(data, start, end);
//
//        // Add data to the chart
//        cartesian.data(filteredData);
//
//        // Customize chart settings if needed
//        cartesian.title("Line Chart from CSV Data");
//
//        // Set the chart to the AnyChartView
//        AnyChartView anyChartView = rootView.findViewById(R.id.any_chart_view);
//        anyChartView.setChart(cartesian);


//        Pie pie = AnyChart.pie();
//
//        List<DataEntry> data = new ArrayList<>();
//        data.add(new ValueDataEntry("John", 10000));
//        data.add(new ValueDataEntry("Jake", 12000));
//        data.add(new ValueDataEntry("Peter", 18000));
//
//        pie.data(data);
//
//        AnyChartView anyChartView = (AnyChartView) rootView.findViewById(R.id.any_chart_view);
//        anyChartView.setChart(pie);


        return rootView;
    }

    private void showAddItemDialog(Context context) {

        final EditText annotationTextInput = new EditText(context);
        annotationTextInput.setHint("Annotation Text");

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(annotationTextInput);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Add New Annotation")
                .setView(layout)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String annotationText = annotationTextInput.getText().toString();
                        dbHelper.insertRecord(FILE_NAME, annotationText);
                        Toast.makeText(context, "Annotation Saved", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void showGraphNameInputDialog(Context context) {


        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("View Annotations")
                .setMessage("Enter the graph name to view its annotations:")

                .setPositiveButton("View", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        List<String> annotations = dbHelper.getAnnotationsByGraphName(FILE_NAME);
                        showAnnotationsDialog(context, annotations);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void showAnnotationsDialog(Context context, List<String> annotations) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Annotations");

        // Convert the list of annotations to a CharSequence array
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

        // Load data based on startYear and endYear
        data = parseCSVFile(start, end);

        // Draw chart
        drawChart();
    }

    private List<DataEntry> parseCSVFile(int start, int end) {
        List<DataEntry> data = new ArrayList<>();

        try {
            isFirstRow = true;
            InputStream inputStream = getActivity().getAssets().open("GDP(USD).csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                if (isFirstRow) {
                    isFirstRow = false;
                    continue; // Skip the first row
                }
                // Split each line of CSV into tokens
                String[] tokens = line.split(",");
                // Assuming the first token is the X-axis value and the second token is the Y-axis value
                int xValue = Integer.parseInt(tokens[0]);
                double yValue = Double.parseDouble(tokens[1]);
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
    private void drawChart() {
        cartesian = AnyChart.line();
        cartesian.yAxis(0).labels().format("{%Value}{scale:(1000)(1)|(k)}");
        cartesian.data(data);
        cartesian.title("Line Chart from CSV Data");

        anyChartView.setChart(cartesian);
    }
//    private List<DataEntry> filterDataByYear(List<DataEntry> data, int startYear, int endYear) {
//        List<DataEntry> filteredData = new ArrayList<>();
//        for (DataEntry entry : data) {
//            // Assuming xValue is the year part of the date (e.g., "2020-Q1" where "2020" is the year)
//            String xValue = entry.getValue("x").toString(); // Extracting year part
//            int year = Integer.parseInt(xValue);
//            if (year >= startYear && year <= endYear) {
//                filteredData.add(entry);
//            }
//        }
//        return filteredData;
//    }
}
