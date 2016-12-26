package com.way.app;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.animoto.android.R;

import java.util.ArrayList;

/**
 * 
 * @author hxp
 * @createdate 2014-4-23 上午9:33:24
 * @Description: 
 */
public class UnselectedTagAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<String> data;

	public UnselectedTagAdapter(Context context, ArrayList<String> data){
		this.context=context;
		this.data=data;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view = new TextView(context);
//		if(SharedPreferenceUtil.getBoolean(context, "isNight")){
//			view.setBackgroundResource(R.drawable.night_uneditable);//初始背景
//		}else{
			view.setBackgroundResource(R.drawable.uneditable);//初始背景
//		}
		view.setGravity(Gravity.CENTER);
		view.setHeight(view.getWidth()/3);
		view.setText(data.get(position));
		view.setTextColor(Color.rgb(141, 141, 141));
		view.setTextSize(14);
		return view;
	}

}
