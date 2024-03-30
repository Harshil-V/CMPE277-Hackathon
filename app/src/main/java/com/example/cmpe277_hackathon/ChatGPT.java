package com.example.cmpe277_hackathon;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ChatGPT extends AppCompatActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chatgpt);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText promptInput = findViewById(R.id.promptEt);
        Button sendPromptBtn = findViewById(R.id.sendBtn);
        Button cancelBtn = findViewById(R.id.cancelPromptBtn);
        TextView responseTextView = findViewById(R.id.resultTv);

        cancelBtn.setOnClickListener(v -> {
            promptInput.setText("");
            responseTextView.setText("");
        });

        sendPromptBtn.setOnClickListener(v -> {
            String prompt = promptInput.getText().toString();
            if (!prompt.isEmpty()) {
                progressDialog = ProgressDialog.show(this,
                        "Loading",
                        "Please wait...",
                        true);

                new CallAPI(response -> {
                    if(progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    responseTextView.setText(response);
                }).execute(prompt);

            } else {
                Toast.makeText(this, "Enter A Prompt", Toast.LENGTH_SHORT).show();
            }

        });

    }

    private static class CallAPI extends AsyncTask<String, Void, String> {
        public interface ResponseListener {
            void onResponseReceived(String response);
        }

        private final ResponseListener listener;

        public CallAPI(ResponseListener listener) {
            this.listener = listener;
        }
        @Override
        protected String doInBackground(String... params) {
            String urlString = "http://10.0.2.2:5000/ask"; // URL to call
            String data = "{\"prompt\":\"" + params[0] + "\"}"; //data to post
            OutputStream out = null;
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");

                out = new BufferedOutputStream(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
                writer.write(data);
                writer.flush();
                writer.close();
                out.close();

                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // Return the response
                    return response.toString();
                } else {
                    // Handle server error here
                    return "Server returned HTTP " + urlConnection.getResponseCode() + " " + urlConnection.getResponseMessage();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            listener.onResponseReceived(result.trim());
//            if (listener != null) {
//                listener.onResponseReceived(result.trim());
//            }
//            try {
//                JSONObject jsonResponse = new JSONObject(result);
//                String responseText = jsonResponse.optString("response", "No response found");
//                if (listener != null) {
//                    listener.onResponseReceived(responseText.trim());
//                }
//
//            } catch (Exception e) {
//                Log.e("Exception: ", String.valueOf(e));
//                if (listener != null) {
//                    listener.onResponseReceived("Error parsing response: " + e.getMessage());
//                }
//
//            }
//


    }
    }

}