package com.example.cmpe277_hackathon;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphFragment extends Fragment {

    EditText startYear;
    EditText endYear;
    Cartesian cartesian;
    AnyChartView anyChartView;
    List<DataEntry> data;
    boolean isFirstRow = true;

    private String selectedCountry;
    private ArrayList<String> selectedCheckboxes;
    private Map<String,String> fileNames = new HashMap<>();

    public GraphFragment(String selectedCountry, ArrayList<String> selectedCheckboxes) {
        this.selectedCountry = selectedCountry;
        this.selectedCheckboxes = selectedCheckboxes;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.graph_layout, container, false);
        startYear = rootView.findViewById(R.id.startYear);
        endYear = rootView.findViewById(R.id.endYear);
        anyChartView = rootView.findViewById(R.id.any_chart_view);
        fileNames.put("GDP(USD)","GDP(USD).csv");
        fileNames.put("FDI Inflows(USD)","FDI_Inflows(USD).csv");
        fileNames.put("FDI Outflows(USD)","FDI_Outflows(USD).csv");
        fileNames.put("Agricultural Contribution (% GDP)","Contribution_Agricultural.csv");
        fileNames.put("Manufacturing(%GDP)","Manufacturing(%GDP).csv");
        fileNames.put("Agriculture, forestry, and fishing, value added (annual % growth)","AFFNG.csv");
        fileNames.put("Fertilizer consumption (kilograms per hectare of arable land)","FertilizerConsumption.csv");
        fileNames.put("Fertilizer consumption (% of fertilizer production)", "Fertilizer_consumption_(% of fertilizer production).csv");
        fileNames.put("Total reserves in months of imports","TotalReservesInMonthsPerImport.csv");
        fileNames.put("Total reserves (includes gold, current US$)","TotalReservesInMonthsPerImport.csv");
        fileNames.put("Total reserves (% of total external debt)","TotalReservesInMonthsPerImport.csv");
        fileNames.put("GNI (current US$)","GNI.csv");


//        Cartesian cartesian = AnyChart.line();
//        List<DataEntry> data = parseCSVFile();

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

        return rootView;
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
                    Log.e("Exception","Data Does not exist for year:"+xValue+" for file:"+fileName);
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
        cartesian.yAxis(0).labels().format("{%Value}{scale:(1000)(1)(1000000)(1e-6)|(k)(M)(G)(µ)}");
        cartesian.xAxis(0).labels().enabled(true);
        cartesian.yAxis(0).labels().enabled(true);
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
}
