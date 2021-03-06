package com.example.abela.marketspiral;

import android.os.AsyncTask;
import com.example.abela.marketspiral.Utility.Functions;
import com.example.abela.marketspiral.interfaces.RegisterResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by HaZe on 4/23/17.
 */

public class RegisterBackFetch extends AsyncTask<HashMap<String, String>, Void, String> {
    private RegisterResponse RegisterResponce;


    public RegisterBackFetch (RegisterResponse registerResponce){
        this.RegisterResponce = registerResponce;
    }

    @Override
    protected String doInBackground(HashMap<String, String>... hashMaps) {

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String response ="";
        // String Search="https://api.data.gov/nrel/alt-fuel-stations/v1/nearest.json?api_key=5cwCk6nhFAkPu9BU3EyxafUN5jqytIGvGD6R4kcO&location=Denver+CO";
        InputStream inputStream = null;
        //--------------------------------------------------------------------
        //-----------------------------------------------------------------------------------------------
        URL url = null;
        try {
            //Login php script location
            url = new URL("http://10.0.2.2/register.php");

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");      //Data sent via POST method

            connection.setDoOutput(true);
            connection.setConnectTimeout(10000);

            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());

//            HashMap<String,String> user_data = hashMaps[0]; //Collect data from input
//            String data = "username="+user_data.get("username")+"&password="+user_data.get("password"); // Concatenate data into a request

            wr.write(Functions.ConcatenateForServer(hashMaps[0]));
            wr.flush();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            response = sb.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //                reader.close();
            //                inputStream.close();
            connection.disconnect();
        }

        return "" + response;
    }

    @Override
    protected void onPostExecute(String s) {

        if(s.equals("SUCCESS")) {
            RegisterResponce.registerFinished();
        }else{

            //TODO display error message
        }
    }
}
