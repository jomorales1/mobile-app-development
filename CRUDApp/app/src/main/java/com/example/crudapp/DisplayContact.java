package com.example.crudapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DisplayContact extends AppCompatActivity {
    int from_Where_I_Am_Coming = 0;
    private DBHelper mydb ;

    TextView name;
    TextView url;
    TextView phone;
    TextView email;
    TextView products;
    TextView category;
    Spinner spinner;
    int id_To_Update = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_contact);
        name = (TextView) findViewById(R.id.editTextName);
        url = (TextView) findViewById(R.id.editTextUrl);
        phone = (TextView) findViewById(R.id.editTextPhone);
        email = (TextView) findViewById(R.id.editTextEmail);
        products = (TextView) findViewById(R.id.editTextProducts);
//        category = (TextView) findViewById(R.id.editTextCategory2);
        spinner = (Spinner) findViewById(R.id.editTextCategory);

        mydb = new DBHelper(this);

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            int Value = extras.getInt("id");

            if(Value>0){
                //means this is the view part not the add contact part.
                Cursor rs = mydb.getData(Value);
                id_To_Update = Value;
                rs.moveToFirst();

                @SuppressLint("Range") String nam = rs.getString(rs.getColumnIndex(DBHelper.COMPANIES_COLUMN_NAME));
                @SuppressLint("Range") String ur = rs.getString(rs.getColumnIndex(DBHelper.COMPANIES_COLUMN_URL));
                @SuppressLint("Range") String phon = rs.getString(rs.getColumnIndex(DBHelper.COMPANIES_COLUMN_PHONE));
                @SuppressLint("Range") String emai = rs.getString(rs.getColumnIndex(DBHelper.COMPANIES_COLUMN_EMAIL));
                @SuppressLint("Range") String prods = rs.getString(rs.getColumnIndex(DBHelper.COMPANIES_COLUMN_PRODUCTS));
                @SuppressLint("Range") String cat = rs.getString(rs.getColumnIndex(DBHelper.COMPANIES_COLUMN_CATEGORY));

                if (!rs.isClosed())  {
                    rs.close();
                }
                Button b = (Button)findViewById(R.id.button1);
                b.setVisibility(View.INVISIBLE);

                name.setText((CharSequence)nam);
                name.setFocusable(false);
                name.setClickable(false);

                url.setText((CharSequence)ur);
                url.setFocusable(false);
                url.setClickable(false);

                phone.setText((CharSequence)phon);
                phone.setFocusable(false);
                phone.setClickable(false);

                email.setText((CharSequence)emai);
                email.setFocusable(false);
                email.setClickable(false);

                products.setText((CharSequence)prods);
                products.setFocusable(false);
                products.setClickable(false);

//                category.setText((CharSequence)cat);
//                category.setFocusable(false);
//                category.setClickable(false);

                if(cat.equals("Consultoria")){
                    spinner.setSelection(1);
                }else if (cat.equals("Desarrollo a la medida")){
                    spinner.setSelection(2);
                }else {
                    spinner.setSelection(3);
                }
                spinner.setEnabled(false);
                spinner.setClickable(false);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Bundle extras = getIntent().getExtras();

        if(extras !=null) {
            int Value = extras.getInt("id");
            if(Value>0){
                getMenuInflater().inflate(R.menu.display_contact, menu);
            } else{
                getMenuInflater().inflate(R.menu.main_menu, menu);
            }
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {
            case R.id.Edit_Contact:
                Button b = (Button)findViewById(R.id.button1);
                b.setVisibility(View.VISIBLE);
                name.setEnabled(true);
                name.setFocusableInTouchMode(true);
                name.setClickable(true);

                url.setEnabled(true);
                url.setFocusableInTouchMode(true);
                url.setClickable(true);

                phone.setEnabled(true);
                phone.setFocusableInTouchMode(true);
                phone.setClickable(true);

                email.setEnabled(true);
                email.setFocusableInTouchMode(true);
                email.setClickable(true);

                products.setEnabled(true);
                products.setFocusableInTouchMode(true);
                products.setClickable(true);

//                category.setEnabled(true);
//                category.setFocusableInTouchMode(true);
//                category.setClickable(true);

                spinner.setEnabled(true);
                spinner.setFocusableInTouchMode(true);
                spinner.setClickable(true);

                return true;
            case R.id.Delete_Contact:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.deleteContact)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mydb.deleteCompany(id_To_Update);
                                Toast.makeText(getApplicationContext(), "Deleted Successfully",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });

                AlertDialog d = builder.create();
                d.setTitle("Are you sure");
                d.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void run(View view) {
        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            int Value = extras.getInt("id");
            if(Value>0){
                if(mydb.updateCompany(id_To_Update,name.getText().toString(),
                        url.getText().toString(), phone.getText().toString(),
                        email.getText().toString(), products.getText().toString(), spinner.getSelectedItem().toString())){
                    Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                } else{
                    Toast.makeText(getApplicationContext(), "not Updated", Toast.LENGTH_SHORT).show();
                }
            } else{
                if(mydb.insertCompany(name.getText().toString(), url.getText().toString(),
                        phone.getText().toString(), email.getText().toString(),
                        products.getText().toString(), spinner.getSelectedItem().toString())){
                    Toast.makeText(getApplicationContext(), "done",
                            Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(getApplicationContext(), "not done",
                            Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        }
    }
}
