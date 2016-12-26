package com.way.view;

import java.util.ArrayList;
import java.util.Collections;

import com.animoto.android.R;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 另外一种方式实现动画可拖动item的GridView
 * 
 * @author way
 * 
 */
@SuppressLint("WrongCall")
public class DragGridView extends ViewGroup implements View.OnTouchListener,
		View.OnClickListener, View.OnLongClickListener {
	// layout vars
	public static float childRatio = .85f;
	protected int rate=3;//宽高比
	protected int colCount;
	protected int childSize, padding, dpi, scroll = 0;
	protected float lastDelta = 0;
	protected Handler handler = new Handler();
	// dragging vars
	protected int dragged = -1, lastX = -1, lastY = -1, lastTarget = -1;
	protected boolean enabled = true, touching = false;
	// anim vars
	public static int animT = 150;
	protected ArrayList<Integer> newPositions = new ArrayList<Integer>();
	// listeners
	protected OnRearrangeListener onRearrangeListener;
	protected OnClickListener secondaryOnClickListener;
	private OnItemClickListener onItemClickListener;
	private OnItemClickListener onDeleteItemClickListener;
	private Context context;
	private boolean canDelete=false;
	private boolean longClicked=false;
	private int num=0;

	/**
	 * 拖动item的接口
	 */
	public interface OnRearrangeListener {

		public abstract void onRearrange(int oldIndex, int newIndex);
	}

	// CONSTRUCTOR AND HELPERS
	public DragGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setListeners();
		handler.removeCallbacks(updateTask);
		handler.postAtTime(updateTask, SystemClock.uptimeMillis() + 500);
		setChildrenDrawingOrderEnabled(true);

		DisplayMetrics metrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);
		dpi = metrics.densityDpi;
		this.context=context;
	}

	protected void setListeners() {
		setOnTouchListener(this);
		super.setOnClickListener(this);
		setOnLongClickListener(this);
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		secondaryOnClickListener = l;
	}

	protected Runnable updateTask = new Runnable() {
		public void run() {
			if (dragged != -1) {
				if (lastY < padding * (colCount+1) && scroll > 0)
					scroll -= 20;
				else if (lastY > getBottom() - getTop() - (padding * (colCount+1))
						&& scroll < getMaxScroll())
					scroll += 20;
			} else if (lastDelta != 0 && !touching) {
				scroll += lastDelta;
				lastDelta *= .9;
				if (Math.abs(lastDelta) < .25)
					lastDelta = 0;
			}
			clampScroll();
			onLayout(true, getLeft(), getTop(), getRight(), getBottom());

			handler.postDelayed(this, 25);
		}
	};

	@Override
	public void addView(View child) {
		super.addView(child);
		newPositions.add(num++);
	};
	
	public void addView(String itemText) {
//		LayoutInflater layoutInflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View layout=layoutInflater.inflate(R.layout.item1, null);
//		View item=layout.findViewById(R.id.layout);
//		TextView text=(TextView) layout.findViewById(R.id.text);
//		text.setText(itemText);
//		addView(layout);
		TextView view = new TextView(context);
		padding =10;
		view.setBackgroundResource(R.drawable.uneditable);//初始背景
		view.setWidth((childSize+padding)*dpi / 160);
		view.setHeight((childSize/rate+padding*2));
		view.setGravity(Gravity.CENTER);
		view.setPadding(padding,padding,padding,padding);
		view.setText(itemText);
		view.setTextColor(Color.GRAY);
		view.setTextSize(18);
		addView(view);
	}
	
	public void setData(ArrayList<String> data) {
		for(int i=0;i<data.size();i++){
			addView(data.get(i));
		}
	}

	@Override
	public void removeViewAt(int index) {
		super.removeViewAt(index);
		newPositions.remove(index);
	};

	// LAYOUT
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// compute width of view, in dp
		float w = (r - l) / (dpi / 160f);

		// determine number of columns, at least 2
		colCount = 2;//改变列数
//		colCount = 2;
		int sub = 240;
		w -= 280;
		while (w > 0) {
			colCount++;
			w -= sub;
			sub += 40;
		}

		// determine childSize and padding, in px
		childSize = (r - l) / colCount;
		childSize = Math.round(childSize * childRatio);
		padding = ((r - l) - (childSize * colCount)) / (colCount + 1);

		for (int i = 0; i < getChildCount(); i++)
			if (i != dragged) {
				Point xy = getCoorFromIndex(i);
				getChildAt(i).layout(xy.x, xy.y, xy.x + childSize,
						xy.y + childSize/rate);
//				getChildAt(i).layout(xy.x, xy.y, xy.x + childSize,
//						xy.y + childSize);
			}
	}

	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		if (dragged == -1)
			return i;
		else if (i == childCount - 1)
			return dragged;
		else if (i >= dragged)
			return i + 1;
		return i;
	}

	public int getIndexFromCoor(int x, int y) {
		int col = getColOrRowFromCoor(x), row = getColOrRowFromCoorY(y + scroll);
		if (col == -1 || row == -1) // touch is between columns or rows
			return -1;
		int index = row * colCount + col;
		if (index >= getChildCount())
			return -1;
		return index;
	}

	protected int getColOrRowFromCoor(int coor) {
		coor -= padding;
		for (int i = 0; coor > 0; i++) {
			if (coor < childSize)
				return i;
			coor -= (childSize + padding);
		}
		return -1;
	}
	
	protected int getColOrRowFromCoorY(int coor) {
		coor -= padding;
		for (int i = 0; coor > 0; i++) {
			if (coor < childSize/rate)
//				if (coor < childSize)
				return i;
			coor -= (childSize/rate + padding);
//			coor -= (childSize + padding);
		}
		return -1;
	}

	protected int getTargetFromCoor(int x, int y) {
		if (getColOrRowFromCoor(y + scroll) == -1) // touch is between rows
			return -1;
		// if (getIndexFromCoor(x, y) != -1) //touch on top of another visual
		// return -1;

		int leftPos = getIndexFromCoor(x - (childSize / 4), y);
		int rightPos = getIndexFromCoor(x + (childSize / 4), y);
		if (leftPos == -1 && rightPos == -1) // touch is in the middle of
												// nowhere
			return -1;
		if (leftPos == rightPos) // touch is in the middle of a visual
			return -1;

		int target = -1;
		if (rightPos > -1)
			target = rightPos;
		else if (leftPos > -1)
			target = leftPos + 1;
		if (dragged < target)
			return target - 1;

		// Toast.makeText(getContext(), "Target: " + target + ".",
		// Toast.LENGTH_SHORT).show();
		return target;
	}

	protected Point getCoorFromIndex(int index) {
		int col = index % colCount;
		int row = index / colCount;
		return new Point(padding + (childSize + padding) * col, padding
				+ (childSize/rate + padding) * row - scroll);
//		return new Point(padding + (childSize + padding) * col, padding
//				+ (childSize + padding) * row - scroll);
	}

	public int getIndexOf(View child) {
		for (int i = 0; i < getChildCount(); i++)
			if (getChildAt(i) == child)
				return i;
		return -1;
	}

	// EVENT HANDLERS
	public void onClick(View view) {
		if(canDelete){
			int index = getLastIndex();
			if(index!=-1){
				onDeleteItemClickListener.onItemClick(null,
						getChildAt(getLastIndex()), getLastIndex(),
						getLastIndex() / colCount);
//				removeViewAt(index);
			}else{
				canDelete=false;
				for(int i=0;i<newPositions.size();i++){
						View v=getChildAt(i);
						if(null!=v){
//							v.setBackgroundColor(Color.WHITE);
							v.setBackgroundResource(R.drawable.uneditable);//背景还原
						}
				}
			}
			if (enabled) {
				if (secondaryOnClickListener != null)
					secondaryOnClickListener.onClick(view);
			}
		}else{
			if (onItemClickListener != null && getLastIndex() != -1&&enabled)
				onItemClickListener.onItemClick(null,
						getChildAt(getLastIndex()), getLastIndex(),
						getLastIndex() / colCount);
		}
	}

	public boolean onLongClick(View view) {
		
		if (!enabled)
			return false;
		longClicked=true;
		int index = getLastIndex();
		if (index != -1) {
			if(!canDelete){
				canDelete=true;
				for(int i=0;i<newPositions.size();i++){
					View v=getChildAt(i);
					if(null!=v){
						v.setBackgroundResource(R.drawable.editable);//删除背景
					}
				}
			}
			dragged = index;
			animateDragged();
			return true;
		}
		return false;
	}

	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			enabled = true;
			lastX = (int) event.getX();
			lastY = (int) event.getY();
			touching = true;
//			if(canDelete){
//				int index = getLastIndex();
//				if (index != -1) {
//					dragged=index;
//					longClicked=true;
//					animateDragged();
//				}
//			}
			break;
		case MotionEvent.ACTION_MOVE:
			if(canDelete&&longClicked){
				int delta = lastY - (int) event.getY();
				if (dragged != -1) {
					// change draw location of dragged visual
					int x = (int) event.getX(), y = (int) event.getY();
					int l = x - (3 * childSize / 4), t = y - (3 * childSize / 4/rate);
					getChildAt(dragged).layout(l, t, l + (childSize * 3 / 2),
							t + (childSize * 3 / 2 /rate));
	//				getChildAt(dragged).layout(l, t, l + (childSize * 3 / 2),
	//						t + (childSize * 3 / 2));
	
					// check for new target hover
					int target = getTargetFromCoor(x, y);
					if (lastTarget != target) {
						if (target != -1) {
							animateGap(target);
							lastTarget = target;
						}
					}
				} else {
					scroll += delta;
					clampScroll();
					if (Math.abs(delta) > 2)
						enabled = false;
					onLayout(true, getLeft(), getTop(), getRight(), getBottom());
				}
				lastX = (int) event.getX();
				lastY = (int) event.getY();
				lastDelta = delta;
			}
			break;
		case MotionEvent.ACTION_UP:
			longClicked=false;
			if (dragged != -1) {
				View v = getChildAt(dragged);
				TextView textView=(TextView)v;
				textView.setGravity(Gravity.CENTER);
				textView.setTextSize(18);
				if (lastTarget != -1)
					reorderChildren();
				else {
					Point xy = getCoorFromIndex(dragged);
					textView.layout(xy.x, xy.y, xy.x + childSize, xy.y + childSize/rate);
//					v.layout(xy.x, xy.y, xy.x + childSize, xy.y + childSize);
				}
				textView.clearAnimation();
				if (v instanceof ImageView)
					((ImageView) v).setAlpha(255);
				lastTarget = -1;
				dragged = -1;
			}
			touching = false;
			break;
		}
		if (dragged != -1)
			return true;
		return false;
	}

	// EVENT HELPERS
	protected void animateDragged() {
		View v = getChildAt(dragged);
		int x = getCoorFromIndex(dragged).x + childSize / 2, y = getCoorFromIndex(dragged).y
				+ childSize / 2/rate;
//		int x = getCoorFromIndex(dragged).x + childSize / 2, y = getCoorFromIndex(dragged).y
//				+ childSize / 2;
		
		int l = x - (3 * childSize / 4), t = y - (3 * childSize / 4/rate);
//		int l = x - (3 * childSize / 4), t = y - (3 * childSize / 4);
		v.layout(l, t, l + (childSize * 3 / 2), t + (childSize * 3 / 2/rate));
		TextView view=(TextView)v;
		view.setGravity(Gravity.CENTER);
		view.setTextSize(28);
//		v.layout(l, t, l + (childSize * 3 / 2), t + (childSize * 3 / 2));
		AnimationSet animSet = new AnimationSet(true);
		ScaleAnimation scale = new ScaleAnimation(.667f, 1, .667f, 1,
				childSize * 3 / 4, childSize * 3 / 4/rate);
//		ScaleAnimation scale = new ScaleAnimation(.667f, 1, .667f, 1,
//				childSize * 3 / 4, childSize * 3 / 4);
		scale.setDuration(animT);
		AlphaAnimation alpha = new AlphaAnimation(1, .9f);
		alpha.setDuration(animT);

		animSet.addAnimation(scale);
		animSet.addAnimation(alpha);
		animSet.setFillEnabled(true);
		animSet.setFillAfter(true);

		view.clearAnimation();
		view.startAnimation(animSet);
//		v.clearAnimation();
//		v.startAnimation(animSet);
	}

	protected void animateGap(int target) {
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			if (i == dragged)
				continue;
			int newPos = i;
			if (dragged < target && i >= dragged + 1 && i <= target)
				newPos--;
			else if (target < dragged && i >= target && i < dragged)
				newPos++;

			// animate
			int oldPos = i;
			if (newPositions.get(i) != -1)
				oldPos = newPositions.get(i);
			if (oldPos == newPos)
				continue;

			Point oldXY = getCoorFromIndex(oldPos);
			Point newXY = getCoorFromIndex(newPos);
			Point oldOffset = new Point(oldXY.x - v.getLeft(), oldXY.y
					- v.getTop());
			Point newOffset = new Point(newXY.x - v.getLeft(), newXY.y
					- v.getTop());

			TranslateAnimation translate = new TranslateAnimation(
					Animation.ABSOLUTE, oldOffset.x, Animation.ABSOLUTE,
					newOffset.x, Animation.ABSOLUTE, oldOffset.y,
					Animation.ABSOLUTE, newOffset.y);
			translate.setDuration(animT);
			translate.setFillEnabled(true);
			translate.setFillAfter(true);
			v.clearAnimation();
			v.startAnimation(translate);

			newPositions.set(i, newPos);
		}
	}

	protected void reorderChildren() {
		// FIGURE OUT HOW TO REORDER CHILDREN WITHOUT REMOVING THEM ALL AND
		// RECONSTRUCTING THE LIST!!!
		if (onRearrangeListener != null)
			onRearrangeListener.onRearrange(dragged, lastTarget);
		ArrayList<View> children = new ArrayList<View>();
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).clearAnimation();
			children.add(getChildAt(i));
		}
		removeAllViews();
		while (dragged != lastTarget)
			if (lastTarget == children.size()) // dragged and dropped to the
												// right of the last element
			{
				children.add(children.remove(dragged));
				dragged = lastTarget;
			} else if (dragged < lastTarget) // shift to the right
			{
				Collections.swap(children, dragged, dragged + 1);
				dragged++;
			} else if (dragged > lastTarget) // shift to the left
			{
				Collections.swap(children, dragged, dragged - 1);
				dragged--;
			}
		for (int i = 0; i < children.size(); i++) {
			newPositions.set(i, -1);
			TextView view = (TextView)children.get(i);
			view.setTextSize(18);
			addView(view);
		}
		onLayout(true, getLeft(), getTop(), getRight(), getBottom());
	}

	public void scrollToTop() {
		scroll = 0;
	}

	public void scrollToBottom() {
		scroll = Integer.MAX_VALUE;
		clampScroll();
	}

	protected void clampScroll() {
		int stretch = 3, overreach = getHeight() / 2;
		int max = getMaxScroll();
		max = Math.max(max, 0);
		if (scroll < -overreach) {
			scroll = -overreach;
			lastDelta = 0;
		} else if (scroll > max + overreach) {
			scroll = max + overreach;
			lastDelta = 0;
		} else if (scroll < 0) {
			if (scroll >= -stretch)
				scroll = 0;
			else if (!touching)
				scroll -= scroll / stretch;
		} else if (scroll > max) {
			if (scroll <= max + stretch)
				scroll = max;
			else if (!touching)
				scroll += (max - scroll) / stretch;
		}
	}

	protected int getMaxScroll() {
		int rowCount = (int) Math.ceil((double) getChildCount() / colCount), max = rowCount
				* childSize/rate+ (rowCount + 1) * padding - getHeight();
		return max;
	}

	public int getLastIndex() {
		return getIndexFromCoor(lastX, lastY);
	}

	// OTHER METHODS
	public void setOnRearrangeListener(OnRearrangeListener l) {
		this.onRearrangeListener = l;
	}

	public void setOnItemClickListener(OnItemClickListener l) {
		this.onItemClickListener = l;
	}
	
	public void setOnDeleteItemClickListener(OnItemClickListener l) {
		this.onDeleteItemClickListener = l;
	}
	
	public ArrayList<String> getData(){
		ArrayList<String> datas=new ArrayList<String>();
		for(int i=0;i<newPositions.size();i++){
			View v=getChildAt(i);
			if(null!=v&&v instanceof TextView){
				TextView tv=(TextView)v;
				datas.add(tv.getText().toString());
			}
		}
		return datas;
	}
}