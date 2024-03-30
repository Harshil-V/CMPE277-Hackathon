package com.example.cmpe277_hackathon;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
    boolean isFirstRow = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.graph_layout, container, false);
        startYear = rootView.findViewById(R.id.startYear);
        endYear = rootView.findViewById(R.id.endYear);
        anyChartView = rootView.findViewById(R.id.any_chart_view);

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
