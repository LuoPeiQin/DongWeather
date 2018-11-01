package com.dong.dongweather;

import android.widget.ListView;

/**
 * Created by 44607 on 2017/5/3.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;

public class DragListView extends ListView {
    //按下选中的position
    private int mStartPosition;
    //需要达到的position
    private int mEndPosition;
    //手指在条目中的相对Y坐标
    private int dragPoint;
    //ListView在屏幕中的Y坐标
    private int dragOffset;
    //上
    private int upScroll;
    //下
    private int downScroll;
    //窗体
    private WindowManager wm;
    //显示的截图
    private ImageView dragImageView;
    //窗体参数
    private WindowManager.LayoutParams lParams;
    //构造方法
    public DragListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    /**
     * 获取触点所在条目的位置
     * 获取选中条目的图片
     * 事件的拦截机制
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//识别动作
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
//获取触点的坐标
                int x = (int) ev.getX();
                int y = (int) ev.getY();
//这样就可以计算我按到哪个条目上了
                mStartPosition = mEndPosition = pointToPosition(x, y);
//判断触点是否在logo的区域
                ViewGroup itemView = (ViewGroup) getChildAt(mStartPosition - getFirstVisiblePosition());
//记录手指在条目中的相对Y坐标
                dragPoint = y - itemView.getTop();
//ListView在屏幕中的Y坐标
                dragOffset = (int) (ev.getRawY() - y);
//拖动的图标
                View dragger = itemView.findViewById(R.id.guide_shape_im); //有修改
//判断触点是否在logo区域
                if (dragger != null && x < dragger.getRight() + 10) {
//定义ListView的滚动条目
//上
                    upScroll = getHeight() / 3;
//下
                    downScroll = getHeight() * 2 / 3;
//获取选中的图片/截图
                    itemView.setDrawingCacheEnabled(true);
//获取截图
                    Bitmap bitMap = itemView.getDrawingCache();
//图片滚动
                    startDrag(bitMap, y);
                }
                break;
        }
//还会传递事件到子View
        return false;
    }
    /**
     * 图片在Y轴，也就是上下可滚动
     *
     * @param bitMap
     * @param y
     */
    private void startDrag(Bitmap bitMap, int y) {
//窗体仿照
        wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
//设置窗体参数
        lParams = new WindowManager.LayoutParams();
//设置宽高
        lParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//属性
        lParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
//设置半透明
        lParams.alpha = 0.8f;
//设置居中
        lParams.gravity = Gravity.TOP;
//设置xy
        lParams.x = 0;
        lParams.y = y-dragPoint + dragOffset;
//属性
        lParams.format = PixelFormat.TRANSLUCENT;
//设置动画
        lParams.windowAnimations = 0;
//图片
        dragImageView = new ImageView(getContext());
//设置截图
        dragImageView.setImageBitmap(bitMap);
//添加显示窗体
        wm.addView(dragImageView, lParams);
    }
    /**
     * 触摸事件
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//错误的位置
        if (dragImageView != null && mEndPosition != INVALID_POSITION) {
//在滑动事件中控制上下滑动
            switch (ev.getAction()) {
                case MotionEvent.ACTION_MOVE:
//直接获取到Y坐标进行移动
                    int moveY = (int) ev.getY();
                    doDrag(moveY);
                    break;
//停止拖动成像
                case MotionEvent.ACTION_UP:
                    int upY = (int) ev.getY();
                    stopDrag();
                    onDrag(upY);
                    break;
            }
        }
//拦截到事件
        return true;
    }
    /**
     * 最终开始成像
     *
     * @param upY
     */
    private void onDrag(int upY) {
//分割线的处理
//判断移动到分割线 返回-1
        int tempLine = pointToPosition(0, upY);
//我们处理他
        if (tempLine != INVALID_POSITION) {
//只要你不移动到分割线 我才处理
            mEndPosition = tempLine;
        }
/**
 * 你在最上方就直接落在第一个最下方就直接最下面一个
 */
//上边界处理
        if (upY < getChildAt(1).getTop()) {
            mEndPosition = 1;
//下边界处理
        } else if (upY > getChildAt(getChildCount() - 1).getTop()) {
            mEndPosition = getAdapter().getCount() - 1;
        }
//开始更新item顺序
        if (mEndPosition > 0 && mEndPosition < getAdapter().getCount()) {
//            DragAdapter adapter = (DragAdapter) getAdapter();
////删除原来的条目
//            adapter.remove(adapter.getItem(mStartPosition));
////更新
//            adapter.insert(adapter.getItem(mStartPosition), mEndPosition);
        }
    }
    /**
     * 停止的位置
     */
    private void stopDrag() {
//直接移除窗体
        if (dragImageView != null) {
            wm.removeView(dragImageView);
            dragImageView = null;
        }
    }
    /**
     * 控制窗体移动
     *
     * @param moveY
     */
    private void doDrag(int moveY) {
        if (dragImageView != null) {
            lParams.y = moveY - dragPoint + dragOffset;
            wm.updateViewLayout(dragImageView, lParams);
        }
//判断移动到分割线 返回-1
        int tempLine = pointToPosition(0, moveY);
//我们处理他
        if (tempLine != INVALID_POSITION) {
//只要你不移动到分割线 我才处理
            mEndPosition = tempLine;
        }
//拖拽时滚动、滚动速度
        int scrollSpeed = 0;
//上滚
        if (moveY < upScroll) {
            scrollSpeed = 10;
//下滚
        } else if (moveY > downScroll) {
            scrollSpeed = -10;
        }
//开始滚动
        if (scrollSpeed != 0) {
//计算条目的Y坐标
            int dragItemY = getChildAt(mEndPosition - getFirstVisiblePosition()).getTop();
//当前速度
            int dy = dragItemY + scrollSpeed;
//设置
            setSelectionFromTop(mEndPosition, dy);
        }
    }
}