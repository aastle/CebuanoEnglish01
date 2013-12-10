package com.aastle.cebuanoenglish01;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.view.View.OnClickListener;
import static android.view.View.OnKeyListener;


// TODO add American and Filipino flags icons next to each word returned

public class MainActivity extends Activity {
    private Button submit;
    private TextView translation;
    private EditText input;
    private String results;
    private broadcastReciever receiver;
    private SharedPreferences prefs;
    private ListView listView;
    private View headers;
    SimpleAdapter sAdapter;
    HashMap<String,String> hMap;
    ArrayList<HashMap<String,String>> aList;
    InputMethodManager imm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // register preference change listener
        prefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                String uiLanguage = "";
                if(key.equals("languageValues")){
                    uiLanguage = prefs.getString("language_list","english");
                    setLabels(uiLanguage);
                }
            }
        });

        input = (EditText)findViewById(R.id.input);
        translation = (TextView)findViewById(R.id.translation);
        listView = (ListView)findViewById(R.id.listViewTrans);
        headers = View.inflate(this,R.layout.header_listview,null);
        listView.addHeaderView(headers);

        submit = (Button)findViewById(R.id.submit);
        IntentFilter intentFilter = new IntentFilter(broadcastReciever.ACTION_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new broadcastReciever();
        registerReceiver(receiver,intentFilter);

        final Intent inputIntent = new Intent(this,jsonIntentService.class);

        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                translation.setText("");
                // Hide the keyboard after the user clicks the translation button
                imm.hideSoftInputFromWindow(input.getWindowToken(),0);

                if(isNetworkAvailable()){
                    results = "";
                    CharSequence charSequence = input.getText();
                    if(charSequence.length() != 0){

                        inputIntent.putExtra(jsonIntentService.PARAM_IN_MSG,input.getText().toString());
                        startService(inputIntent);
                    }
                    else {
                        translation.setText("Please enter a word to translate.");
                        Toast.makeText(getBaseContext(),"Please enter a word to translate",Toast.LENGTH_LONG).show();
                    }

                    input.requestFocus();
                }else {
                    Toast warning = Toast.makeText(getBaseContext(),"No network available.",Toast.LENGTH_LONG);
                }
            }

        });

        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if((v instanceof EditText) && hasFocus){
                    input.selectAll();
                }
            }
        });
        input.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER){
                    submit.callOnClick();
                    return true;
                }else{
                    return false;
                }
            }
        });

        //Log.i(MainActivity.class.getName(), "Results of GET = " + results);
        englishcebuano englishCebuano = englishcebuano.getInstance("wife","cebuano");
        englishCebuano.getCebuano();
    }
    @Override
    protected void onPause(){
        super.onPause();
        //unregisterReceiver(receiver);

    }
    @Override
    protected void onResume(){
        super.onResume();
        String Lang = getLanguagePreferences();
        setLabels(Lang);
    }
    private void setLabels(String language){
        if(language.equals("cebuano")){
            submit.setText("hubara");
            input.setHint("I-type ang imong pulong dinhi");
            setTitle("Cebuano-English Diksyonaryo");
        } else if(language.equals("english")){
            submit.setText("translate");
            input.setHint("Type your word here");
            setTitle("A Cebuano to English Dictionary");
        }
    }
    private String getLanguagePreferences(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String UILanguage = preferences.getString("language_list","english");

        return UILanguage;
    }
    public class broadcastReciever extends BroadcastReceiver {
     public static final  String ACTION_RESPONSE =
             "com.aastle.CebuanoEnglish01.intent.action.MESSAGE_PROCESSED";
        String[] transArray;

        String englishTrans;
        String cebuanoTrans;
        JSONArray jsonArray;
        int lengthOfJSONArray = 0;

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String json = intent.getStringExtra(jsonIntentService.PARAM_OUT_MSG);
                if(intent != null && json != null && !json.equals("")){
                    if(!json.equals("false")){
                        jsonArray = new JSONArray(json);
                        translation.setText("");
                        lengthOfJSONArray = jsonArray.length();
                        transArray = new String[jsonArray.length()];

                        aList = new ArrayList<HashMap<String, String>>();

                        // loop through all of the JSONObjects in our array
                        for(int i=0;i<jsonArray.length();i++){
                            //Log.d("alan","jsonArray.length() = "+jsonArray.length());
                            //Log.d("alan","jsonArray Counter = " + i);
                            hMap = new HashMap<String, String>();
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                             englishTrans = jsonObject.has("english") ? jsonObject.getString("english"):"english not found";
                             cebuanoTrans = jsonObject.has("cebuano") ? jsonObject.getString("cebuano"):"cebuano not found";

                            String[] cebuArray = cebuanoTrans.split(";");
                            String[] engArray = englishTrans.split(";");

                            int cebuLength = cebuArray.length;
                            int engLength = engArray.length;

                            if(cebuLength > engLength){
                                //Log.d("alan","cebuLength = "+cebuLength+ ", engLength = " + engLength);
                                for(int c=0;c<cebuArray.length;c++){

                                    //Log.d("alan","c = " + c);
                                    //Log.d("alan", "cebuArray[c] " + cebuArray[c]);

                                    hMap = new HashMap<String, String>();

                                    if(c<engLength){
                                        //Log.d("alan","if(c<engLength) engArray[c] "+engArray[c]);
                                        hMap.put("cebuano", cebuArray[c].toLowerCase().trim());
                                        hMap.put("english", engArray[c].toLowerCase().trim());

                                    }else if(c>=engLength){
                                        //Log.d("alan", "if(c>engLength)");
                                        hMap.put("cebuano", cebuArray[c].toLowerCase().trim());
                                        hMap.put("english","");

                                    }
                                    aList.add(hMap);
                                }
                            }else if(cebuLength < engLength){
                                for(int e=0;e<engArray.length;e++){
                                    hMap = new HashMap<String, String>();
                                    if(e<cebuLength){
                                    hMap.put("cebuano", cebuArray[e].toLowerCase().trim());
                                    }else{
                                        hMap.put("cebuano","");
                                    }
                                    hMap.put("english",engArray[e].toLowerCase().trim());
                                    aList.add(hMap);
                                }
                            }else if(cebuLength == engLength){
                                for(int e=0;e<engArray.length;e++){
                                    hMap = new HashMap<String, String>();
                                    hMap.put("cebuano", cebuArray[e].toLowerCase().trim());
                                    hMap.put("english",engArray[e].toLowerCase().trim());
                                    aList.add(hMap);
                                }
                            }
                        }
                        if(transArray.length != 0){
                            sAdapter = new SimpleAdapter(getBaseContext(),aList,R.layout.textview_listview,new String[]{"english","cebuano"},new int[]{R.id.textViewForList,R.id.textViewForList2});
                            try{
                                listView.setAdapter(sAdapter);

                            }catch (Exception earr){
                                Log.d("alan","ArrayAdapter Error " + earr.fillInStackTrace() + " " + earr.getStackTrace());
                            }
                        }

                    }else{
                        Toast.makeText(getBaseContext(),"translation not found",Toast.LENGTH_LONG).show();
                        translation.setText("Translation Not Found");
                    }
                }else{
                    Toast.makeText(getBaseContext(),"translation not found",Toast.LENGTH_LONG).show();
                    translation.setText("Translation Not Found");
                } //Log.d("alan","exited jsonArray loop!");
            }catch(JSONException jex){
                Log.d("alan","JSONException>>>>>>>> " + jex.fillInStackTrace() + " " + jex.getStackTrace());

            }catch(Exception e){
                Toast.makeText(getBaseContext(), "onReceive Exception: " + e.fillInStackTrace(), Toast.LENGTH_LONG).show();
                Log.d("alan","Exception>>>>>> " + e.fillInStackTrace() + " " + e.getStackTrace());
            }

        }
    }
    private boolean isNetworkAvailable(){

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = connectivityManager.getActiveNetworkInfo();
        return netinfo != null && netinfo.isConnected();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this,SettingsActivity.class));
                return true;
            case R.id.about:
                showAboutDialog(buildAboutInfo());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


        private String buildAboutInfo(){
            String packageName = "";
            String versionName = "";
            int versionCode = 0;
            try{
                packageName = getPackageName();
                versionCode = getPackageManager().getPackageInfo(packageName, 0).versionCode;
                versionName = getPackageManager().getPackageInfo(packageName, 0).versionName;
            }catch (Exception e){
                Toast.makeText(this,"Package Name not Found :(",Toast.LENGTH_LONG).show();
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Cebuano 2 English\n");
            stringBuilder.append("Version: ");
            stringBuilder.append(versionCode);
            stringBuilder.append("\nBuild: ");
            stringBuilder.append(versionName);
            stringBuilder.append("\nCopyright 2013 by Alan W. Astle");
            return stringBuilder.toString();

        }
    private void showAboutDialog(CharSequence about){
        new AlertDialog.Builder(this)
                .setMessage(about)
                .setPositiveButton("OK",null)
                .show();

    }

}
