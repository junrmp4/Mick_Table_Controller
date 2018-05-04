package com.benqmedicaltech.bmt_table_controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Table_Controller extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table__controller);
        Button nextPageBtn = (Button)findViewById(R.id.button);
        TextView mtext = (TextView) findViewById(R.id.textView123);
        mtext.setText("幹！");
        nextPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Table_Controller.this , ScanPage.class);
                startActivity(intent);
            }
        });

    }





}
