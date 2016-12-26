package com.way.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.animoto.android.R;
import com.way.view.DragGridView;

import java.util.ArrayList;

/**
 * 栏目定制
 * @author hxp
 * @createdate 2014-7-17 下午3:58:51
 * @Description: 
 */
public class DragViewActivity extends Activity implements OnClickListener {
	private DragGridView mDragGridView;
	private ArrayList<String> unselectedData;
	private ArrayList<String> selectedData;
	private GridView unselected;
	private TextView columnDiyTv;
	private UnselectedTagAdapter adapter;
	@SuppressLint("HandlerLeak")
	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// mDragGridView加载完后,给unselected设定与mDragGridView同样的padding
			case 0:
				unselected.setHorizontalSpacing(mDragGridView.padding);
				unselected.setVerticalSpacing(mDragGridView.padding);
				unselected.setPadding(mDragGridView.padding,
						mDragGridView.padding, mDragGridView.padding,
						mDragGridView.padding);
				break;

			// 编辑状态
			case 1:
				columnDiyTv.setVisibility(View.GONE);
				unselected.setVisibility(View.GONE);
				mDragGridView.getLayoutParams().height = LayoutParams.MATCH_PARENT;
				mDragGridView.setLayoutParams(mDragGridView.getLayoutParams());
				break;

			// 还原非编辑状态
			case 2:
				columnDiyTv.setVisibility(View.VISIBLE);
				unselected.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};
	private LinearLayout bg;
	private int dpi;
	private int tvTopPading;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_column_diy_layout);
//		titleView.setText("栏目制定");
//		templateButtonRight.setBackgroundResource(0);
//		templateButtonRight.setText("完成");

		columnDiyTv = (TextView) findViewById(R.id.columnDiyTv);
		bg = (LinearLayout) findViewById(R.id.ll_bg);
		initData();
	}

	private void initData() {
		selectedData = new ArrayList<String>();
		selectedData.add("轻松一刻");
		selectedData.add("房产");
		selectedData.add("时尚");
		selectedData.add("推荐");
		selectedData.add("娱乐");
		selectedData.add("体育");
		selectedData.add("财经");

		mDragGridView = (DragGridView) findViewById(R.id.vgv);
		mDragGridView.setData(selectedData);
		mDragGridView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		unselected = (GridView) findViewById(R.id.unselected);
		DisplayMetrics metrics = new DisplayMetrics();
		DragViewActivity.this.getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);
		dpi = metrics.densityDpi;
		if (dpi < 201) {
			tvTopPading = 8;
		} else if (dpi < 319) {
			tvTopPading = 14;
		} else if (dpi < 399) {
			tvTopPading = 20;
		} else if (dpi >= 399) {
			tvTopPading = 30;
		}

		unselectedData = new ArrayList<String>();
		unselectedData.add("科技");
		unselectedData.add("汽车");
		unselectedData.add("图片");
		unselectedData.add("北京");
		unselectedData.add("真话");
		unselectedData.add("教育");
		unselectedData.add("电影");
		adapter = new UnselectedTagAdapter(DragViewActivity.this,
				unselectedData);
		unselected.setAdapter(adapter);
		setListener();
	}
	

	private void setListener() {
		mDragGridView.setOnRearrangeListener(new DragGridView.OnRearrangeListener() {
			public void onRearrange(int oldIndex, int newIndex) {
				String word = selectedData.remove(oldIndex);
				if (oldIndex < newIndex) {
					selectedData.add(newIndex, word);
				} else
					selectedData.add(newIndex, word);
			}
		});
		mDragGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				String word = selectedData.get(arg2);
//				int currentId = FlyApplication.list.indexOf(word);
//				if(currentId!=-1){
//					HomeFragment.current = currentId;
//					finish();
//				}
			}
		});
		mDragGridView.setOnDeleteItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				mDragGridView.removeViewAt(arg2);
				String word = selectedData.remove(arg2);
				unselectedData.add(word);
				adapter.notifyDataSetChanged();
			}
		});
		unselected.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				if(!mDragGridView.canDelete) {
					String word = unselectedData.remove(arg2);
					mDragGridView.addView(word);
					mDragGridView.postInvalidate();
					selectedData.add(word);
					adapter.notifyDataSetChanged();
				}else{
					Toast.makeText(DragViewActivity.this,"退出排序状态才可添加~",Toast.LENGTH_LONG).show();
				}
			}
		});
//		templateButtonRight.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				FlyApplication.list.clear();
//				FlyApplication.list.addAll(selectedData);
//				if (mDragGridView.canDelete) {
//					mDragGridView.canDelete = false;
//					for (int i = 0; i < mDragGridView.newPositions.size(); i++) {
//						View view = mDragGridView.getChildAt(i);
//						if (null != view) {
//							if(SharedPreferenceUtil.getBoolean(ColumnDiy.this, "isNight")){
//								view.setBackgroundResource(R.drawable.night_uneditable);// 背景还原
//
//							}else{
//								view.setBackgroundResource(R.drawable.uneditable);// 背景还原
//							}
//							view.setPadding(0, tvTopPading, 0, 0);
//							((TextView)view).setTextSize(14);
//						}
//					}
//					handler.sendEmptyMessage(2);
//				} else {
//					selectedData.clear();
//					unselectedData=null;
//					finish();
//				}
//
//			}
//		});
//		 findViewById(R.id.title_but_left).setOnClickListener(this);
		 bg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				for (int i = 0; i < mDragGridView.newPositions.size(); i++) {
					View view = mDragGridView.getChildAt(i);
					if (null != view) {
//						if(SharedPreferenceUtil.getBoolean(ColumnDiy.this, "isNight")){
//							view.setBackgroundResource(R.drawable.night_uneditable);// 背景还原
//						}else{
							view.setBackgroundResource(R.drawable.uneditable);// 背景还原
//						}
					}
				}
				handler.sendEmptyMessage(2);

			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void finish() {
//		FlyApplication.colEditType = true;
		super.finish();
	}

	 @Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if(keyCode == KeyEvent.KEYCODE_BACK){
//			 templateButtonRight.performClick();
		 }
		 finish();
		 return true;
	 }

	@Override
	public void onClick(View v) {
		if (mDragGridView.canDelete) {
			mDragGridView.canDelete = false;
			for (int i = 0; i < mDragGridView.newPositions.size(); i++) {
				View view = mDragGridView.getChildAt(i);
				if (null != view) {
					view.setBackgroundResource(R.drawable.uneditable);// 背景还原
					view.setPadding(0, tvTopPading, 0, 0);
					((TextView)view).setTextSize(14);
				}
			}
			handler.sendEmptyMessage(2);
		} else {
			unselectedData = null;
			finish();
		}
	}
}
