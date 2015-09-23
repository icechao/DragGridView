package com.myapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;


/**
 * 项目名称:Project
 * 类描述：可拖动的GridView
 * 创建人：超
 * 创建时间: 2015/9/21 11:11
 * 修改人：
 * 修改时间：
 */
public class DragGridView extends GridView {

    /**
     * 被点击的item
     */
    private int dragItemIndex;
    /**
     * 被拖动时显示的item的图片ImageView
     */
    private ImageView mCacheImageView;
    /**
     * 是否开启drag
     */
    private boolean isStartDrag;
    /**
     * 状态栏高度
     */
    private int mStatusHeight;
    /**
     *
     */
    private WindowManager windowManager;
    /**
     * 拖动View的layoutParams
     */
    private WindowManager.LayoutParams layoutParams;
    /**
     * 点击位置和Item的左边的位置距离
     */
    private int view2TouchX;
    /**
     * 点击位置和Item的上边的位置距离
     */
    private int view2TouchY;
    /**
     * 当前拖动的Item
     */
    private View dragItemView;
    /**
     * 位置发生变化的监听
     */
    private OnExchangeListener onExchangeListener;

    private Runnable runnable;
    private int viewX;
    private int viewY;
    private int viewHeight;
    private int viewWidth;
    private long delayTime = 800;
    /**
     * DragGridView的左边距
     */
    private int view2FatherLeft;
    /**
     * DragGridView的上边距
     */
    private int view2FatherTop;

    public DragGridView(Context context) {
        super(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mStatusHeight = getStatusHeight(context);
    }

    public DragGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mStatusHeight = getStatusHeight(context);
    }

    public DragGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mStatusHeight = getStatusHeight(context);
    }

    /**
     * 设置长按延迟时间毫秒值 默认值800
     *
     * @param delayTime
     */
    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    /**
     * 获取状态栏的高度
     *
     * @param context
     * @return
     */
    private int getStatusHeight(Context context) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }


    @Override
    public boolean dispatchTouchEvent(final MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                checkPointDown(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                checkPointOut(ev);
                break;
            case MotionEvent.ACTION_UP:
                removeCallbacks(runnable);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 手指按下检测相关操作
     *
     * @param ev
     */
    private void checkPointDown(final MotionEvent ev) {
        dragItemIndex = pointToPosition((int) ev.getX(), (int) ev.getY());
        if (dragItemIndex != -1 && dragItemIndex != AdapterView.INVALID_POSITION) {
            initVariable(ev);
            postDelayed(runnable = new Runnable() {
                @Override
                public void run() {
                    startDragView(ev);
                }
            }, delayTime);
        }
    }

    /**
     * 检测点击位置是否超出Item范围,如果超出不进行拖放
     *
     * @param ev
     */
    private void checkPointOut(MotionEvent ev) {
        if (!isStartDrag && (ev.getX() > viewX + viewWidth || ev.getX() < viewX || ev.getY() < viewY || ev.getY() > viewY + viewHeight)) {
            removeCallbacks(runnable);
            isStartDrag = false;
        }
    }

    private void initVariable(MotionEvent ev) {
        dragItemView = getChildAt(dragItemIndex - getFirstVisiblePosition());
        viewX = (int) dragItemView.getX();
        viewY = (int) dragItemView.getY();
        viewHeight = dragItemView.getMeasuredHeight();
        viewWidth = dragItemView.getMeasuredWidth();
        view2TouchX = (int) (ev.getRawX() - dragItemView.getLeft());
        view2TouchY = (int) (ev.getRawY() - dragItemView.getTop());

        view2FatherTop = getTop();
        view2FatherLeft = getLeft();
    }

    /**
     * 开启拖动模式
     *
     * @param ev
     */
    private void startDragView(MotionEvent ev) {

        if (dragItemIndex != -1 && dragItemIndex != AdapterView.INVALID_POSITION) {
            dragItemView = getChildAt(dragItemIndex);
            setDragView((int) ev.getRawX(), (int) ev.getRawY());
            isStartDrag = true;
        }


    }

    /**
     * 设置可拖动的ImageView
     *
     * @param getRawX
     * @param getRawY
     */
    private void setDragView(int getRawX, int getRawY) {

        dragItemView.setVisibility(View.INVISIBLE);
        initLayoutParams(getRawX, getRawY);
        mCacheImageView = new ImageView(getContext());
        mCacheImageView.setImageBitmap(getDragViewBitmap(dragItemView));
        windowManager.addView(mCacheImageView, layoutParams);
    }

    /**
     * 初始化拖动View的layoutParams
     *
     * @param getRawX
     * @param getRawY
     */
    private void initLayoutParams(int getRawX, int getRawY) {
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.format = PixelFormat.TRANSLUCENT; //图片之外的其他地方透明
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        layoutParams.x = getRawX - view2TouchX - view2FatherLeft;
        layoutParams.y = getRawY - view2TouchY + mStatusHeight + view2FatherTop;
        layoutParams.alpha = 0.55f;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {


        if (isStartDrag) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    break;
                case MotionEvent.ACTION_MOVE:
                    int moveY = (int) ev.getRawY();
                    int moveX = (int) ev.getRawX();
                    updataImageView(moveY, moveX);
                    exchangItem(ev);
                    break;
                case MotionEvent.ACTION_UP:
                    overDrag();
            }

            return true;
        }


        return super.onTouchEvent(ev);
    }

    /**
     * 结束拖动,显示当前摆放位置View并去除的拖动View
     */
    private void overDrag() {
        if (dragItemView != null) {
            dragItemView.setVisibility(View.VISIBLE);
        }
        if (mCacheImageView != null) {
            windowManager.removeView(mCacheImageView);
        }
        windowManager.removeView(mCacheImageView);
        isStartDrag = false;
    }

    /**
     * 检测是否需要交换两个Item位置,交换Item执行回调方法
     *
     * @param ev
     */
    private void exchangItem(MotionEvent ev) {
        int newDragItemIndex = pointToPosition((int) ev.getX(), (int) ev.getY());
        if (newDragItemIndex != -1 && newDragItemIndex != AdapterView.INVALID_POSITION) {

            if (onExchangeListener != null && newDragItemIndex != dragItemIndex) {
                onExchangeListener.onExchange(dragItemIndex, newDragItemIndex);
            }
            getChildAt(newDragItemIndex - getFirstVisiblePosition()).setVisibility(View.INVISIBLE);
            dragItemView.setVisibility(View.VISIBLE);
            dragItemView = getChildAt(newDragItemIndex - getFirstVisiblePosition());
            dragItemIndex = newDragItemIndex;
        }
    }

    /**
     * 刷新拖动View的位置
     *
     * @param moveY
     * @param moveX
     */
    private void updataImageView(int moveY, int moveX) {
        layoutParams.x = moveX - view2TouchX;
        layoutParams.y = moveY - view2TouchY + mStatusHeight;
        windowManager.updateViewLayout(mCacheImageView, layoutParams);
    }

    /**
     * 获取拖动View的图片
     *
     * @param dragItemView
     */
    private Bitmap getDragViewBitmap(View dragItemView) {
        dragItemView.setDrawingCacheEnabled(true);
        Bitmap dragViewBitmap = Bitmap.createBitmap(dragItemView.getDrawingCache());
        dragItemView.destroyDrawingCache();
        return dragViewBitmap;
    }


    /**
     * 设置当位置发生交换时的监听,一般需要在适配器里修改GridView数据的顺序需要刷新UI
     *
     * @param onExchangeListener
     */
    public void setOnExchangeListener(OnExchangeListener onExchangeListener) {
        this.onExchangeListener = onExchangeListener;
    }

    /**
     * 当拖动View到相邻其他View时View发生变化,回调此方法
     */
    public interface OnExchangeListener {
        void onExchange(int oldIndex, int newIndex);
    }
}
