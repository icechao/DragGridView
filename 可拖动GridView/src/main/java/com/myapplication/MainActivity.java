package com.myapplication;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DragGridView gridView = (DragGridView) findViewById(R.id.gridview);
        MenuAdapter adapter = new MenuAdapter(this);
        gridView.setAdapter(adapter);
        gridView.setOnExchangeListener(adapter);
    }
}
