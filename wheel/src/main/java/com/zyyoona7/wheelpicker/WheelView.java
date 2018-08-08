package com.zyyoona7.wheelpicker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.OverScroller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WheelView<T> extends View {

    private static final String TAG = "WheelView";

    private static final String DEFAULT_TEXT = "DEFAULT_TEXT";
    private static final int DEFAULT_LINE_SPACE = (int) dp2px(2f);
    private static final int DEFAULT_VISIBLE_ITEM = 3;

    Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint.FontMetrics mFontMetrics;
    //每个item的高度
    private int mItemHeight;
    //文字中心距离baseline的距离
    private int mCenterToBaselineY;
    //可见的item条数
    private int mVisibleItems;
    //是否循环滚动
    private boolean mIsCyclic = true;

    private ViewConfiguration mViewConfiguration;
    private VelocityTracker mVelocityTracker;
    private int mMaxFlingVelocity;
    private int mMinFlingVelocity;
    private OverScroller mOverScroller;
    //Y轴滚动偏移
    private int mOffsetScrollY;
    //手指按下的位置
    private float mLastTouchY;

    //最小滚动距离，上边界
    private int mMinScrollY;
    //最大滚动距离，下边界
    private int mMaxScrollY;

    //数据列表
    List<T> mDataList = new ArrayList<>(1);

    public WheelView(Context context) {
        this(context, null);
    }

    public WheelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mPaint.setTextSize(sp2px(15));
        mVisibleItems = DEFAULT_VISIBLE_ITEM;
        mViewConfiguration = ViewConfiguration.get(context);
        mMaxFlingVelocity = mViewConfiguration.getScaledMaximumFlingVelocity();
        mMinFlingVelocity = mViewConfiguration.getScaledMinimumFlingVelocity();
        mOverScroller = new OverScroller(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mFontMetrics == null) {
            mFontMetrics = mPaint.getFontMetrics();
        }
        //itemHeight实际等于字体高度+一个行间距
        mItemHeight = (int) (mFontMetrics.bottom - mFontMetrics.top) + DEFAULT_LINE_SPACE;
        int height = mItemHeight * mVisibleItems + getBottom() + getTop() + (mVisibleItems - 1) * DEFAULT_LINE_SPACE;
        int width = (int) mPaint.measureText(DEFAULT_TEXT) + getPaddingLeft() + getPaddingRight();
        setMeasuredDimension(resolveSize(width, widthMeasureSpec), resolveSize(height, heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //文字中心距离baseline的距离
        mCenterToBaselineY = (int) (mFontMetrics.ascent + (mFontMetrics.descent - mFontMetrics.ascent) / 2);

        //计算滚动限制
        calculateLimitY();
    }

    /**
     * 计算滚动限制
     */
    private void calculateLimitY() {
        mMinScrollY = mIsCyclic ? Integer.MIN_VALUE : 0;
        //下边界 (dataSize - 1 - mInitPosition) * mItemHeight
        mMaxScrollY = mIsCyclic ? Integer.MAX_VALUE : (mDataList.size() - 1) * mItemHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //滚动了多少个item，滚动的Y值高度除去每行Item的高度
        int scrolledItem = mOffsetScrollY / mItemHeight;
        //没有滚动完一个item时的偏移值，平滑滑动
        int scrolledOffset = mOffsetScrollY % mItemHeight;
        //向上取整
        int halfItem = (mVisibleItems + 1) / 2;
        //计算的最小index
        int minIndex;
        //计算的最大index
        int maxIndex;
        if (scrolledOffset < 0) {
            //小于0
            minIndex = scrolledItem - halfItem - 1;
            maxIndex = scrolledItem + halfItem;
        } else if (scrolledOffset > 0) {
            minIndex = scrolledItem - halfItem;
            maxIndex = scrolledItem + halfItem + 1;
        } else {
            minIndex = scrolledItem - halfItem;
            maxIndex = scrolledItem + halfItem;
        }

        for (int i = minIndex; i < maxIndex; i++) {
            drawItem(canvas, i, scrolledOffset);
        }

    }

    private void drawItem(Canvas canvas, int index, int scrolledOffset) {
        String text = getDataByIndex(index);
        if (text == null) {
            return;
        }
        Log.d(TAG, "drawItem: index=" + index + ",text=" + text);
        int centerY = getHeight() / 2;

        //index 的 item 距离中间项的偏移
        int item2CenterOffsetY = (index - mOffsetScrollY / mItemHeight) * mItemHeight - scrolledOffset;

        if (Math.abs(item2CenterOffsetY) <= 0) {
            //绘制选中的条目
            canvas.drawText(text, 0, text.length(), 0, centerY + item2CenterOffsetY - mCenterToBaselineY, mPaint);

        } else if (item2CenterOffsetY > 0 && item2CenterOffsetY < mItemHeight) {
            //绘制与下边界交汇的条目
            canvas.drawText(text, 0, text.length(), 0, centerY + item2CenterOffsetY - mCenterToBaselineY, mPaint);

        } else if (item2CenterOffsetY < 0 && item2CenterOffsetY > -mItemHeight) {
            //绘制与上边界交汇的条目
            canvas.drawText(text, 0, text.length(), 0, centerY + item2CenterOffsetY - mCenterToBaselineY, mPaint);

        } else {
            //绘制其他条目
            canvas.drawText(text, 0, text.length(), 0, centerY + item2CenterOffsetY - mCenterToBaselineY, mPaint);

        }
    }

    /**
     * 根据下标获取到内容
     *
     * @param index
     * @return
     */
    private String getDataByIndex(int index) {
        int dataSize = mDataList.size();
        if (dataSize == 0) {
            return null;
        }

        String itemText = null;
        if (mIsCyclic) {
            int i = index % dataSize;
            if (i < 0) {
                i += dataSize;
            }
            itemText = getDataText(mDataList.get(i));
        } else {
            if (index >= 0 && index < dataSize) {
                itemText = getDataText(mDataList.get(index));
            }
        }
        return itemText;
    }

    /**
     * 获取item text
     *
     * @param item
     * @return
     */
    private String getDataText(T item) {
        if (item == null) {
            return "";
        } else if (item instanceof IWheelable) {
            return ((IWheelable) item).getWheelText();
        } else if (item instanceof Integer) {
            //如果为整形则最少保留两位数.
            return String.format(Locale.getDefault(), "%02d", (Integer) item);
        } else if (item instanceof String) {
            return (String) item;
        }
        return item.toString();
    }

    public List<T> getDataList() {
        return mDataList;
    }

    public void setDataList(List<T> dataList) {
        if (dataList == null) {
            return;
        }
        mDataList = dataList;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                //手指按下
                mLastTouchY = event.getY();
                //处理滑动事件嵌套 拦截事件序列
                if (getParent()!=null){
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                //如果未滚动完成，强制滚动完成
                if (!mOverScroller.isFinished()) {
                    mOverScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //手指移动
                float moveY = event.getY();
                float deltaY = moveY - mLastTouchY;

                if (Math.abs(deltaY) < 1) {
                    break;
                }
                //deltaY 上滑为正，下滑为负
                mOffsetScrollY += -deltaY;
                if (!mIsCyclic) {
                    //修正边界
                    if (mOffsetScrollY < mMinScrollY) {
                        mOffsetScrollY = mMinScrollY;
                    } else if (mOffsetScrollY > mMaxScrollY) {
                        mOffsetScrollY = mMaxScrollY;
                    }
                }
                mLastTouchY = moveY;
                invalidate();

                break;
            case MotionEvent.ACTION_UP:
                //手指抬起
                mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);
                float velocityY = mVelocityTracker.getYVelocity();
                if (Math.abs(velocityY) > mMinFlingVelocity) {
                    //快速滑动
                    mOverScroller.fling(0, mOffsetScrollY, 0, (int) -velocityY, 0, 0, mMinScrollY, mMaxScrollY);
                    //更新结束位置，保证滚动完成后正好选中不发生偏移
                    mOverScroller.notifyVerticalEdgeReached(mOffsetScrollY, mOverScroller.getFinalY() + calculateDistanceToEndPoint(mOverScroller.getFinalY() % mItemHeight), 0);
                } else {
                    //平稳滑动
                    mOverScroller.startScroll(0, mOffsetScrollY, 0, calculateDistanceToEndPoint(mOffsetScrollY % mItemHeight));
                }

                if (!mIsCyclic) {
                    //修正Scroller边界
                    if (mOverScroller.getFinalY() < mMinScrollY) {
                        mOverScroller.notifyVerticalEdgeReached(mOffsetScrollY, mMinScrollY, 0);
                    } else if (mOverScroller.getFinalY() > mMaxScrollY) {
                        mOverScroller.notifyVerticalEdgeReached(mOffsetScrollY, mMaxScrollY, 0);
                    }
                }
                postInvalidateOnAnimation();
                //回收
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                //事件被终止
                //回收
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                break;
        }
        return true;
    }

    /**
     * 计算距离终点的偏移，修正选中条目
     *
     * @param remainder
     * @return
     */
    private int calculateDistanceToEndPoint(int remainder) {
        if (Math.abs(remainder) > mItemHeight / 2)
            if (mOffsetScrollY < 0)
                return -mItemHeight - remainder;
            else
                return mItemHeight - remainder;
        else
            return -remainder;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mOverScroller.computeScrollOffset()) {
            mOffsetScrollY = mOverScroller.getCurrY();
            postInvalidateOnAnimation();
        }
    }

    /**
     * 是否是循环滚动
     *
     * @return
     */
    public boolean isCyclic() {
        return mIsCyclic;
    }

    /**
     * 设置是否循环滚动
     *
     * @param cyclic
     */
    public void setCyclic(boolean cyclic) {
        mIsCyclic = cyclic;
        calculateLimitY();
        invalidate();
    }

    protected static float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    protected static float sp2px(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, Resources.getSystem().getDisplayMetrics());
    }
}
