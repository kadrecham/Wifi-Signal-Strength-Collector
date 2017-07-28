package rest.send.org.scan;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 10;
    String location;
    WifiManager wifi;
    TextView myTextView;
    Button stopButton;
    Button scanButton;
    EditText myEditText;
    List<ScanResult> scanner;
    PojoLocationScanResult plsr;
    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    ScheduledExecutorService scheduler;
    PowerManager pm;
    PowerManager.WakeLock wl;
    String macAddress;
    Switch modeSwitch;
    String ipAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toast.makeText(MainActivity.this, "Mac address: " + getMac(), Toast.LENGTH_LONG).show();
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
        wl.acquire();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinner = (Spinner)findViewById(R.id.spinner2);
        adapter = ArrayAdapter.createFromResource(this, R.array.room_names,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        location = getMac();
        spinner.setEnabled(false);
        modeSwitch = (Switch) findViewById(R.id.switch1);
        modeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    spinner.setEnabled(true);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            location = parent.getItemAtPosition(position).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
                else {
                    spinner.setEnabled(false);
                    location = getMac();
                }
            }
        });
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        myTextView = (TextView) findViewById(R.id.textView);
        stopButton = (Button) findViewById(R.id.button);
        scanButton = (Button) findViewById(R.id.button2);

        myEditText = (EditText) findViewById(R.id.editText);
        stopButton.setEnabled(false);
        scanButton.setEnabled(false);
        plsr = new PojoLocationScanResult();
        myTextView.setMovementMethod(new ScrollingMovementMethod());
        if (!wifi.isWifiEnabled()) {
            myTextView.setText("Please turn on the Wifi connection");
        } else {
            scanButton.setEnabled(true);
            stopButton.setEnabled(true);
        }
    }







    public void scan(View view)  {

        ipAddress = myEditText.getText().toString();

        if (ipAddress.matches(""))
            ipAddress = "10.87.88.101";

        Toast.makeText(MainActivity.this, "Sending to " + ipAddress, Toast.LENGTH_SHORT).show();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);

        } else {
            Toast.makeText(MainActivity.this, "from " + location, Toast.LENGTH_SHORT).show();
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate
                    (new Runnable() {
                        public void run() {
                        runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    wifi.startScan();
                            scanner = wifi.getScanResults();
                            int scannerSize = scanner.size();
                            LocationScanResult r[] = new LocationScanResult[scannerSize];
                            for (ScanResult sc : scanner) {
                                scannerSize --;
                                r[scannerSize] = new LocationScanResult();
                                r[scannerSize].setBSSID(sc.BSSID);
                                r[scannerSize].setLevel( String.valueOf(sc.level));
                            }
                            plsr.setLocationScanResult(r);
                            myTextView.setText("");
                            myTextView.setText(plsr.toString());
                            new HttpRequestTask().execute();

                }
            });

                        }
                    }, 0, 5, TimeUnit.SECONDS);


        }
    }

    public void stop(View view) {

        scheduler.shutdown();

    }

    public String getMac () {
        File my_file = new File ("/sys/class/net/wlan0/address");
        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(my_file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                //text.append('\n');
            }
            br.close();
            return text.toString().toUpperCase();
        }
        catch (IOException e) {
            return "No MAC address found!";
        }
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, PojoLocationScanResult> {

        @Override
        protected PojoLocationScanResult doInBackground(Void... params) {

            try {
                final String url = "http://" + ipAddress + ":8080/js?myKey=" + location ;
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                PojoLocationScanResult response = restTemplate.postForObject(url, plsr, PojoLocationScanResult.class);
                return response;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }
    }
    
}
