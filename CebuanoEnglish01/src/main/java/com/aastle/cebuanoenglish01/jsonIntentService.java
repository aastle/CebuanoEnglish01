package com.aastle.cebuanoenglish01;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class jsonIntentService extends IntentService {
    public static final String PARAM_IN_MSG = "inmsg";
    public static final String PARAM_OUT_MSG = "outmsg";
    // Defines a custom Intent action
    public static final String BROADCAST_ACTION =
            "com.aastle.cebuanoenglish01.BROADCAST";

    // Defines the key for the status "extra" in an Intent
    public static final String EXTENDED_DATA_STATUS =
            "com.aastle.cebuanoenglish01.STATUS";

    public jsonIntentService(){
        super("jsonIntentService");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        String status = "fetching";
        Intent localIntent =
                new Intent(jsonIntentService.BROADCAST_ACTION).putExtra(jsonIntentService.EXTENDED_DATA_STATUS, status);
        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.sendBroadcast(localIntent);
        String msg = intent.getStringExtra(PARAM_IN_MSG);
        StringBuilder jsonParsed = new StringBuilder();
        Uri.Builder builder = Uri.parse("http://bisayan.me/mysql/searchall.php").buildUpon();
        builder.appendQueryParameter("myinput",msg);
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(builder.build().toString());
        try{
            HttpResponse httpResponse = httpClient.execute(httpGet);
            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if(statusCode == 200){
                HttpEntity httpEntity = httpResponse.getEntity();
                InputStream inputStream = httpEntity.getContent();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null){
                    jsonParsed.append(line);
                }

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(MainActivity.broadcastReciever.ACTION_RESPONSE);
                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                broadcastIntent.putExtra(PARAM_OUT_MSG,jsonParsed.toString());
                sendBroadcast(broadcastIntent);

            }
            else {
                //Log.e(jsonIntentService.class.toString(), "Failed to recieve json text!");
            }

        }catch (ClientProtocolException ce){
            ce.printStackTrace();

        }catch (IOException e){
            e.printStackTrace();
        }

    }



    
}
