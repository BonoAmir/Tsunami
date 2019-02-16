package com.example.android.tusso;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    String LOG_TAG=MainActivity.class.getSimpleName();

    String apiUsgc="https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2014-01-01&endtime=2014-12-01&minmagnitude=7";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        asyncTask task=new asyncTask();
        task.execute();


    }

    private void updateUI(Event event)
    {
        TextView eventn=(TextView)findViewById(R.id.eventInfo);
        eventn.setText(event.getEvent());

        TextView dd=(TextView) findViewById(R.id.dateInfo);

        dd.setText(formatDate(event.getDate()));

        TextView alert=(TextView)findViewById(R.id.alertInfo);

        alert.setText(alertfrimater(event.getAlert()));
    }


    private String formatDate(Long date)
    {

        SimpleDateFormat format=new SimpleDateFormat("EEE, d MMM yyyy 'at' HH:mm:ss z");


        return   format.format(date);

    }

    private String alertfrimater(int alert)
    {
        String result=null;
        switch (alert){
            case 0 :

                return result="No";


            case 1:
                return result="Yes";

                default:
                    return result="Unavaliable";

        }
    }




    private  URL createUrl(String urlString)
    {
        URL url=null;
        try {
             url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG,"Error creating url "+e);

            return null;
        }

        return url ;
    }




private String createHttpconnectio(URL url) throws IOException {

    HttpURLConnection urlConnection=null ;
    String jsonreponse=null;
    InputStream inputStream=null;

    try {
        urlConnection= (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(10000);
        urlConnection.setConnectTimeout(15000);
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();


        if(urlConnection.getResponseCode()==200)
        {
            inputStream=urlConnection.getInputStream();
            jsonreponse=readFromStream(inputStream);
        }
        else
        {
            return null;
        }
    } catch (IOException e) {
        Log.e(LOG_TAG,"error from server responding "+urlConnection.getResponseCode());
    }



    return jsonreponse;


}

private String readFromStream(InputStream inputStream) throws IOException {

    StringBuilder output=new StringBuilder();
    if (inputStream!=null)
    {
        InputStreamReader inputStreamReader=new InputStreamReader(inputStream,Charset.forName("UTF-8"));
        BufferedReader   reader = new BufferedReader(inputStreamReader);
        String line =reader.readLine();

        while (line !=null)
        {
            output.append(line);
            line=reader.readLine();
        }
    }

    return output.toString();
}

    private Event extractFeatureFromJson(String earthquakeJSON) {


        if(TextUtils.isEmpty(earthquakeJSON))
        {return null;}
        try {
            JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);
            JSONArray featureArray = baseJsonResponse.getJSONArray("features");

            // If there are results in the features array
            if (featureArray.length() > 0) {
                // Extract out the first feature (which is an earthquake)
                JSONObject firstFeature = featureArray.getJSONObject(0);
                JSONObject properties = firstFeature.getJSONObject("properties");

                // Extract out the title, time, and tsunami values
                String title = properties.getString("title");
                long time = properties.getLong("time");
                int tsunamiAlert = properties.getInt("tsunami");

                // Create a new {@link Event} object
                return new Event(title, time, tsunamiAlert);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }
        return null;
    }



    class asyncTask extends AsyncTask<URL,Void ,Event >{

        @Override
        protected Event doInBackground(URL... urls) {
            URL ur =createUrl(apiUsgc);
            String jsomreponse=null;

            try {
                 jsomreponse=createHttpconnectio(ur);
            } catch (IOException e) {
                e.printStackTrace();
            }



            Event earthuqyakc=extractFeatureFromJson(jsomreponse);

            return earthuqyakc;

        }

        @Override
        protected void onPostExecute(Event event) {

                updateUI(event);

            }
    }
}
