package com.way.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.animoto.android.R;
import com.way.view.FlowViewGroup;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class ThirdActivity extends AppCompatActivity implements View.OnClickListener{
    private FlowViewGroup mFlowViewGroup;
    private String[] mTexts = new String[]{"BatteryView.txt", "为自定义View",
            "参考", "定义自定义View属性", "参考fragment_04.xml",
            "属性值", "图片为资源", "背景白圈",
            "一张为一个圆形图片", "用于遮盖XFerMode","形成圆形波浪效果"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        initView();
        initDatas();
    }

    private void initView() {
        mFlowViewGroup = (FlowViewGroup) findViewById(R.id.flowlayout);
        findViewById(R.id.rise).setOnClickListener(this);
        findViewById(R.id.dic).setOnClickListener(this);
    }

    private void initDatas() {
        TextView tv;
        for (int i=0;i<mTexts.length;i++){
            tv= (TextView) LayoutInflater.from(this).inflate(R.layout.item_flow,mFlowViewGroup,false);
            tv.setText(mTexts[i]);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ThirdActivity.this,""+((TextView)v).getText(),Toast.LENGTH_SHORT).show();
                }
            });
            mFlowViewGroup.addView(tv);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rise:
                Arrays.sort(mTexts,new Comparator<String>(){
                    Collator clt=Collator.getInstance();
                    @Override
                    public int compare(String o1, String o2) {
                        return clt.compare(o1, o2);
                    }
                });
                mFlowViewGroup.removeAllViews();
                initDatas();
                break;
            case R.id.dic:
                Arrays.sort(mTexts,new Comparator<String>(){
                    Collator clt=Collator.getInstance();
                    @Override
                    public int compare(String o1, String o2) {
                        return clt.compare(o1, o2);
                    }
                });
                mFlowViewGroup.removeAllViews();
                TextView tv;
                for (int i=mTexts.length-1;i>=0;i--){
                    tv= (TextView) LayoutInflater.from(this).inflate(R.layout.item_flow,mFlowViewGroup,false);
                    tv.setText(mTexts[i]);
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(ThirdActivity.this,""+((TextView)v).getText(),Toast.LENGTH_SHORT).show();
                        }
                    });
                    mFlowViewGroup.addView(tv);
                }
                break;
        }
    }
}
