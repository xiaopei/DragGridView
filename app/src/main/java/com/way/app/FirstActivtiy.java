package com.way.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.animoto.android.R;

public class FirstActivtiy extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_activtiy);
        findViewById(R.id.drag).setOnClickListener(this);
        findViewById(R.id.flow).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.drag:
                startActivity(new Intent(this,DragViewActivity.class));
                break;
            case R.id.flow:
                startActivity(new Intent(this,ThirdActivity.class));
                break;
        }
    }
}
