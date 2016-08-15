package com.androidclass.fileoperationsapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;

/**
 * Created by jsingh on 8/14/16.
 */
public class DetailStock extends Activity implements OnClickListener {

    int id = -1;
    final static String TAG = DetailStock.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detail);

        TextView sym = (TextView) findViewById(R.id.sym);
        TextView name = (TextView) findViewById(R.id.name);
        TextView price = (TextView) findViewById(R.id.price);

        Intent intent = getIntent();

        String symbolVal = intent.getStringExtra(Constants.KEY_SYMBOL);
        String nameVal = intent.getStringExtra(Constants.KEY_NAME);
        String priceVal = intent.getStringExtra(Constants.KEY_CURPRICE);
        id = intent.getIntExtra(Constants.KEY_ID, -1);

        sym.setText(symbolVal);
        name.setText(nameVal);
        price.setText(priceVal);

        Button btnDelete = (Button) findViewById(R.id.btnDelete);

        btnDelete.setOnClickListener(this);

        Button btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnDelete) {
            Log.d(TAG, "delete pressed");
            Intent i = new Intent();
            i.putExtra(Constants.KEY_ID, id);
            setResult(RESULT_OK,i);
            finish();
        }
        else if (v.getId() == R.id.btnBack) {
            setResult(RESULT_CANCELED);
            finish();
        }

    }

}
