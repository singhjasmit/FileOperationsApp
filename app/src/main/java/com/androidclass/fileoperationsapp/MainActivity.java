package com.androidclass.fileoperationsapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity  implements OnItemClickListener , OnClickListener{


    final static String ASSET_FILE = "stocks.txt";

    final static String INT_FILE = "stocks_out_internal.csv";
    final static String EXT_FILE = "stocks_out_external.csv";


    final static String TAG = MainActivity.class.getSimpleName();
    final static int DETAIL_STOCK_ACT = 10;


    List<HashMap<String, String>> my_stocks = new ArrayList<HashMap<String, String>>();

    SimpleAdapter myAdapter;

    String[] from = {Constants.KEY_SYMBOL, Constants.KEY_CURPRICE, Constants.KEY_NAME};
    int[] to = {R.id.sym, R.id.price, R.id.name};
    ListView lv;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initStocksList();
        Log.d(TAG, "Number of stocks: " + my_stocks.size());

        // We get the ListView component from the layout
        lv = (ListView) findViewById(R.id.my_list);
        myAdapter = new SimpleAdapter(this, my_stocks, R.layout.item_row, from, to);

        lv.setAdapter(myAdapter);
        lv.setOnItemClickListener(this);


        Button btnClear = (Button) findViewById(R.id.btnClearAll);
        Button btnReset = (Button) findViewById(R.id.btnReadInternalFile);
        Button btnExport = (Button) findViewById(R.id.btnWriteFile);

        btnClear.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        btnExport.setOnClickListener(this);

    }




    public void writeFileToInternalStorage() {

        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(INT_FILE, Context.MODE_PRIVATE);
            Log.d(TAG, "writing to internal storage");


            StringBuilder sb = new StringBuilder();

            for (HashMap<String, String> s : my_stocks) {
                sb.append(s.get(Constants.KEY_SYMBOL)).append(":")
                        .append(s.get(Constants.KEY_NAME)).append(":")
                        .append(s.get(Constants.KEY_CURPRICE)).append("\n");
            }
            outputStream.write(sb.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.d(TAG, "Exception e" + e.toString());
            e.printStackTrace();
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    public void writeFileToExternalStorage() {

        if (isExternalStorageWritable()) {

            FileOutputStream outputStream;

            try {
                File file = new File(getExternalFilesDir(null), EXT_FILE);
                outputStream = new FileOutputStream(file);

                Log.d(TAG, "writing to external storage");

                StringBuilder sb = new StringBuilder();

                for (HashMap<String, String> s : my_stocks) {
                    sb.append(s.get(Constants.KEY_SYMBOL)).append(":")
                            .append(s.get(Constants.KEY_NAME)).append(":")
                            .append(s.get(Constants.KEY_CURPRICE)).append("\n");
                }
                outputStream.write(sb.toString().getBytes());
                outputStream.close();
            } catch (Exception e) {
                Log.d(TAG, "Exception e" + e.toString());
                e.printStackTrace();
            }
        }

    }


    public void readFileFromAssetFile() {

        AssetManager assetManager = getAssets();
        BufferedReader reader = null;

        try {

            reader = new BufferedReader(new InputStreamReader(
                    assetManager.open(ASSET_FILE), "UTF-8"));

            String line;
            my_stocks.clear();

            Log.d(TAG, "Reading : " + ASSET_FILE);

            while ((line = reader.readLine()) != null) {

                Log.d(TAG, "Line : " + line);
                String[] separated = line.split(":");
                if (separated.length == 3) {
                    HashMap<String, String> st = new HashMap<String, String>();
                    st.put(Constants.KEY_SYMBOL, separated[0]);
                    st.put(Constants.KEY_NAME, separated[1]);
                    st.put(Constants.KEY_CURPRICE, separated[2]);

                    my_stocks.add(st);
                }
                else {
                    Log.d(TAG, "could not create stock: " + separated.length);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void initStocksList() {
        readFileFromAssetFile();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnReadInternalFile) {
            my_stocks.clear();
            readFileFromAssetFile();
            myAdapter.notifyDataSetChanged();
        }
        else if (v.getId() == R.id.btnClearAll) {

            my_stocks.clear();
            myAdapter.notifyDataSetChanged();

        }
        else if (v.getId() == R.id.btnWriteFile) {
            writeFile();

        }

    }


    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + ") and RESULT_OK = " + RESULT_OK);
        if ((resultCode == RESULT_OK) && (requestCode == DETAIL_STOCK_ACT)) {

            if (data !=null) {
                int id = data.getIntExtra(Constants.KEY_ID, -1);
                Log.d(TAG, "id returned = " + id);

                if (id > -1) {
                    Log.d(TAG, "removing position: " + id);

                    my_stocks.remove(id);
                    myAdapter.notifyDataSetChanged();
                }
            }
        }
    }



    private void writeFile() {
        if (isExternalStorageWritable()) {
            writeFileToExternalStorage();

        }else {
            writeFileToInternalStorage();
        }


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        Log.i(TAG, "You clicked Item: " + id + " at position:" + position);

        // Then you start a new Activity via Intent
        Intent intent = new Intent();

        intent.setClass(this, DetailStock.class);
        HashMap<String, String> st = my_stocks.get(position);
        intent.putExtra(Constants.KEY_SYMBOL, st.get(Constants.KEY_SYMBOL));
        intent.putExtra(Constants.KEY_NAME, st.get(Constants.KEY_NAME));
        intent.putExtra(Constants.KEY_CURPRICE, st.get(Constants.KEY_CURPRICE));
        intent.putExtra(Constants.KEY_ID, position);


        startActivityForResult(intent, DETAIL_STOCK_ACT);

    }


}
