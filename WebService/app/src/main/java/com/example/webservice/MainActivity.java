package com.example.webservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String data_url = "https://www.datos.gov.co/resource/46yq-tz63.json";
    ListView listView;
    Button fetchButton;
    ProgressDialog progressDialog;
    HashMap<String, Device> devices;
    ArrayList<String> names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list);
        devices = new HashMap<>();
        names = new ArrayList<>();
        if (savedInstanceState != null) {
            devices = (HashMap<String, Device>) savedInstanceState.getSerializable("devices");
            names = (ArrayList<String>) savedInstanceState.getSerializable("names");
            System.out.println(names);
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, names);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Bundle dataBundle = new Bundle();
                String name = (String) arg0.getItemAtPosition(arg2);
                Device device = devices.get(name);
                assert device != null;
                dataBundle.putString("name", name);
                dataBundle.putString("os", device.getOs());
                dataBundle.putString("ram", device.getRam());
                dataBundle.putString("storage", device.getStorage());
                dataBundle.putString("type", device.getType());
                dataBundle.putString("speed", device.getSpeed());
                dataBundle.putString("brand", device.getBrand());
                dataBundle.putString("availability", device.getAvailability());
                dataBundle.putString("value", device.getValue());
                Intent intent = new Intent(getApplicationContext(), Details.class);
                intent.putExtras(dataBundle);
                startActivity(intent);
            }
        });
        fetchButton = (Button) findViewById(R.id.fetch_button);
        fetchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyAsyncTask asyncTask = new MyAsyncTask();
                asyncTask.execute();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!this.devices.isEmpty()) {
            System.out.println("saving state");
            outState.putSerializable("names", this.names);
            outState.putSerializable("devices", this.devices);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        devices = (HashMap<String, Device>) savedInstanceState.getSerializable("devices");
        names = (ArrayList<String>) savedInstanceState.getSerializable("names");
    }

    class MyAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            devices.clear();
            names.clear();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Obteniendo datos...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder response = new StringBuilder();
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(data_url);
                urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    InputStream responseBody = urlConnection.getInputStream();
                    InputStreamReader responseBodyReader = new InputStreamReader(responseBody, StandardCharsets.UTF_8);
                    int data = responseBodyReader.read();
                    while (data != -1) {
                        response.append((char) data);
                        data = responseBodyReader.read();
                    }
//                    System.out.println(response);
                    return response.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            try {
                names = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Device device = new Device();
                    device.setName(jsonObject.getString("nombre_de_activo"));
                    names.add(device.getName());
                    device.setOs(jsonObject.getString("sistema_operativo"));
                    device.setRam(Integer.toString(jsonObject.getInt("memoria_ram_en_gb")));
                    device.setStorage(jsonObject.getString("disco_duro_en_gb"));
                    device.setType(jsonObject.getString("tipo_de_activo"));
                    device.setSpeed(jsonObject.getString("velocidad"));
                    device.setBrand(jsonObject.getString("marca_y_modelo"));
                    device.setAvailability(Integer.toString(jsonObject.getInt("disponibilidad")));
                    device.setValue(Integer.toString(jsonObject.getInt("valor")));
                    devices.put(jsonObject.getString("nombre_de_activo"), device);
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, names);
                listView.setAdapter(arrayAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    static class Device implements Serializable {
        String name;
        String os;
        String ram;
        String storage;
        String type;
        String speed;
        String brand;
        String availability;
        String value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOs() {
            return os;
        }

        public void setOs(String os) {
            this.os = os;
        }

        public String getRam() {
            return ram;
        }

        public void setRam(String ram) {
            this.ram = ram;
        }

        public String getStorage() {
            return storage;
        }

        public void setStorage(String storage) {
            this.storage = storage;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSpeed() {
            return speed;
        }

        public void setSpeed(String speed) {
            this.speed = speed;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getAvailability() {
            return availability;
        }

        public void setAvailability(String availability) {
            this.availability = availability;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}