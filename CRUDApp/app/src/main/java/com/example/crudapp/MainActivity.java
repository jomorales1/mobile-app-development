package com.example.crudapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Context;
import android.content.Intent;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "MESSAGE";
    private ListView obj;
    private TextView name;
    private Spinner category;
    DBHelper mydb;
    HashMap<String, Integer> data = new HashMap<>();
    ArrayList<DBHelper.DataObject> array_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = (TextView) findViewById(R.id.filterName);
        category = (Spinner) findViewById(R.id.filterCategory);

        mydb = new DBHelper(this);
        //data.clear();
        array_list = mydb.getAllCompanies();
        ArrayList<String> companiesNames = new ArrayList<>();
        for (DBHelper.DataObject dataObject : array_list) {
            data.put(dataObject.getName(), dataObject.getId());
            companiesNames.add(dataObject.getName());
        }
        ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1, companiesNames);

        obj = (ListView)findViewById(R.id.listView1);
        obj.setAdapter(arrayAdapter);
        obj.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
                // TODO Auto-generated method stub
                int id_To_Search = arg2;

                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", data.get(array_list.get(id_To_Search).getName()));

                Intent intent = new Intent(getApplicationContext(),DisplayContact.class);

                intent.putExtras(dataBundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);

        switch(item.getItemId()) {
            case R.id.item1:Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", 0);

                Intent intent = new Intent(getApplicationContext(),DisplayContact.class);
                intent.putExtras(dataBundle);

                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keycode, event);
    }

    public void runFilter(View view) {
        String categoryT = category.getSelectedItem().toString();
        String nameT = name.getText().toString();

        array_list = mydb.filter(nameT, categoryT);
        ArrayList<String> companiesNames = new ArrayList<>();
        //data.clear();
        for (DBHelper.DataObject dataObject : array_list) {
            data.put(dataObject.getName(), dataObject.getId());
            companiesNames.add(dataObject.getName());
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, companiesNames);
        obj.setAdapter(arrayAdapter);
    }
}