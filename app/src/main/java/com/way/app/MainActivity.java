package com.way.app;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.animoto.android.R;
import com.way.view.DragGridView;
import com.way.view.DragGridView.OnRearrangeListener;

/**
 * MainActivity
 * 
 * @author way
 * 
 */
@SuppressLint("NewApi")
public class MainActivity extends Activity {
	static Random random = new Random();
	static String[] words = "我 是 一 只 大 笨 猪".split(" ");
	ArrayList<String> data;
	DragGridView mDragGridView;
	Button mAddBtn, mViewBtn;
	ArrayList<String> poem = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		data=new ArrayList<String>();
		data.add("轻松一刻");
		data.add("房产");
		data.add("时尚");
		data.add("推荐");
		data.add("娱乐");
		data.add("体育");
		data.add("财经");
		data.add("科技");
		data.add("汽车");
		data.add("图片");
		data.add("北京");
		data.add("真话");
		data.add("教育");
		data.add("电影");
		

		mDragGridView = ((DragGridView) findViewById(R.id.vgv));
		try {
		Method method = AbsListView.class.getMethod("setOverScrollMode",int.class);
		method.setAccessible(true); 
			method.invoke(mDragGridView, new Integer(2));
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mDragGridView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		mDragGridView.setData(data);
		mAddBtn = ((Button) findViewById(R.id.add_item_btn));
		mViewBtn = ((Button) findViewById(R.id.view_poem_item));

		setListeners();
	}

	private void setListeners() {
		mDragGridView.setOnRearrangeListener(new OnRearrangeListener() {
			public void onRearrange(int oldIndex, int newIndex) {
				String word = data.remove(oldIndex);
				if (oldIndex < newIndex)
					data.add(newIndex, word);
				else
					data.add(newIndex, word);
			}
		});
//		mDragGridView.setOnRearrangeListener(new OnRearrangeListener() {
//			public void onRearrange(int oldIndex, int newIndex) {
//				String word = poem.remove(oldIndex);
//				if (oldIndex < newIndex)
//					poem.add(newIndex, word);
//				else
//					poem.add(newIndex, word);
//			}
//		});
		mDragGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent =new Intent(MainActivity.this,SecondActivity.class);
				startActivity(intent);
//				mDragGridView.removeViewAt(arg2);
//				data.remove(arg2);
			}
		});
		mDragGridView.setOnDeleteItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				mDragGridView.removeViewAt(arg2);
				data.remove(arg2);
			}
		});
		mAddBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				String word = words[random.nextInt(words.length)];
//				ImageView view = new ImageView(MainActivity.this);
//				view.setImageBitmap(getThumb(word));
				data.add(word);
				mDragGridView.addView(word);
//				poem.add(word);
			}
		});

		mViewBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				
				ArrayList<String> datas=mDragGridView.getData();
				String finishedPoem = "";
				for (String s : datas)
					finishedPoem += s + " ";
				new AlertDialog.Builder(MainActivity.this).setTitle("这是你选择的")
						.setMessage(finishedPoem).show();
			}
		});
	}

//	private Bitmap getThumb(String s) {
//		Bitmap bmp = Bitmap.createBitmap(50, 50, Bitmap.Config.RGB_565);
//		Canvas canvas = new Canvas(bmp);
//		Paint paint = new Paint();
//
//		paint.setColor(Color.rgb(random.nextInt(128), random.nextInt(128),
//				random.nextInt(128)));
//		paint.setTextSize(24);
//		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
//		canvas.drawRect(new Rect(0, 0, 50, 50), paint);
//		paint.setColor(Color.WHITE);
//		paint.setTextAlign(Paint.Align.CENTER);
//		canvas.drawText(s, 25, 25, paint);
//
//		return bmp;
//	}
}