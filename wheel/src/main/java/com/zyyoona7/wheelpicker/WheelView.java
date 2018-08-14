package com.zyyoona7.wheelpicker;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.FloatRange;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.OverScroller;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WheelView<T> extends View implements Runnable {

    private static final String TAG = "WheelView";

    private static final float DEFAULT_LINE_SPACE = dp2px(2f);
    private static final float DEFAULT_TEXT_SIZE = sp2px(15f);
    private static final float DEFAULT_TEXT_BOUNDARY_MARGIN = dp2px(2);
    private static final float DEFAULT_DIVIDER_HEIGHT = dp2px(1);
    private static final int DEFAULT_NORMAL_TEXT_COLOR = Color.DKGRAY;
    private static final int DEFAULT_SELECTED_TEXT_COLOR = Color.BLACK;
    private static final int DEFAULT_VISIBLE_ITEM = 5;
    private static final int DEFAULT_SCROLL_DURATION = 250;
    private static final long DEFAULT_CLICK_CONFIRM = 120;
    private static final String DEFAULT_INTEGER_FORMAT = "%02d";

    //文字对齐方式
    public static final int TEXT_ALIGN_LEFT = 0;
    public static final int TEXT_ALIGN_CENTER = 1;
    public static final int TEXT_ALIGN_RIGHT = 2;

    //滚动状态
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_SCROLLING = 2;

    //弯曲效果对齐方式
    public static final int CURVED_ALIGN_LEFT = 0;
    public static final int CURVED_ALIGN_CENTER = 1;
    public static final int CURVED_ALIGN_RIGHT = 2;

    public static final float DEFAULT_CURVED_BIAS = 0.75f;

    //分割线填充类型
    public static final int DIVIDER_TYPE_FILL = 0;
    public static final int DIVIDER_TYPE_WRAP = 1;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //字体大小
    private float mTextSize;
    private Paint.FontMetrics mFontMetrics;
    //每个item的高度
    private int mItemHeight;
    //文字的最大宽度
    private int mMaxTextWidth;
    //文字中心距离baseline的距离
    private int mCenterToBaselineY;
    //可见的item条数
    private int mVisibleItems;
    //每个item之间的空间
    private float mLineSpace;
    //是否循环滚动
    private boolean isCyclic;
    //文字对齐方式
    private int mTextAlign;
    //文字颜色
    private int mTextColor;
    //选中item文字颜色
    private int mSelectedItemColor;

    //是否显示分割线
    private boolean isShowDivider = true;
    //分割线的颜色
    private int mDividerColor;
    //分割线高度
    private float mDividerSize;
    //分割线填充类型
    private int mDividerType;
    //分割线类型为DIVIDER_TYPE_WRAP时 分割线左右两端距离文字的间距
    private float mDividerPaddingForWrap = dp2px(2);
    //分割线两端形状，默认圆头
    private Paint.Join mDividerJoin = Paint.Join.ROUND;

    //文字起始X
    private int mStartX;
    //X轴中心点
    private int mCenterX;
    //Y轴中心点
    private int mCenterY;
    //选中边界的上下限制
    private int mSelectedItemTopLimit;
    private int mSelectedItemBottomLimit;
    //裁剪边界
    private int mClipLeft;
    private int mClipTop;
    private int mClipRight;
    private int mClipBottom;
    //绘制区域
    private Rect mDrawRect;
    //字体外边距，目的是留有边距
    private float mTextBoundaryMargin;
    //数据为Integer类型时，是否需要格式转换
    private boolean isIntegerNeedFormat = false;
    //数据为Integer类型时，转换格式，默认转换为两位数
    private String mIntegerFormat;

    //3D效果
    private Camera mCamera;
    private Matrix mMatrix;
    //是否是弯曲（3D）效果
    private boolean isCurved = true;
    //弯曲（3D）效果对齐方式
    private int mCurvedAlign = CURVED_ALIGN_CENTER;
    //弯曲（3D）效果偏移系数
    private float mCurvedAlignBias = DEFAULT_CURVED_BIAS;
    //弯曲（3D）效果选中后折射的偏移
    private float mCurvedRefractX;

    //数据列表
    List<T> mDataList = new ArrayList<>(1);

    private VelocityTracker mVelocityTracker;
    private int mMaxFlingVelocity;
    private int mMinFlingVelocity;
    private OverScroller mOverScroller;

    //最小滚动距离，上边界
    private int mMinScrollY;
    //最大滚动距离，下边界
    private int mMaxScrollY;

    //Y轴滚动偏移
    private int mScrollOffsetY;
    //手指最后触摸的位置
    private float mLastTouchY;
    //手指按下时间，根据按下抬起时间差处理点击滚动
    private long mDownStartTime;
    //是否强制停止滚动
    private boolean isForceFinishScroll = false;
    //是否是快速滚动，快速滚动结束后跳转位置
    private boolean isFlingScroll;
    //当前选中的下标
    private int mCurrentItemPosition;

    //监听器
    private OnItemSelectedListener<T> mOnItemSelectedListener;
    private OnWheelChangedListener mOnWheelChangedListener;

    public WheelView(Context context) {
        this(context, null);
    }

    public WheelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrsAndDefault(context, attrs);
        initValue(context);
    }

    /**
     * 初始化自定义属性及默认值
     *
     * @param context context
     * @param attrs   attrs
     */
    private void initAttrsAndDefault(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WheelView);
        mTextSize = typedArray.getDimension(R.styleable.WheelView_wv_textSize, DEFAULT_TEXT_SIZE);
        mTextAlign = typedArray.getInt(R.styleable.WheelView_wv_textAlign, TEXT_ALIGN_CENTER);
        mTextBoundaryMargin = typedArray.getDimension(R.styleable.WheelView_wv_textBoundaryMargin,
                DEFAULT_TEXT_BOUNDARY_MARGIN);
        mTextColor = typedArray.getColor(R.styleable.WheelView_wv_normalItemTextColor, DEFAULT_NORMAL_TEXT_COLOR);
        mSelectedItemColor = typedArray.getColor(R.styleable.WheelView_wv_selectedItemTextColor, DEFAULT_SELECTED_TEXT_COLOR);
        mLineSpace = typedArray.getDimension(R.styleable.WheelView_wv_lineSpace, DEFAULT_LINE_SPACE);
        isIntegerNeedFormat = typedArray.getBoolean(R.styleable.WheelView_wv_integerNeedFormat, false);
        mIntegerFormat = typedArray.getString(R.styleable.WheelView_wv_integerFormat);
        if (TextUtils.isEmpty(mIntegerFormat)) {
            mIntegerFormat = DEFAULT_INTEGER_FORMAT;
        }

        mVisibleItems = typedArray.getInt(R.styleable.WheelView_wv_visibleItems, DEFAULT_VISIBLE_ITEM);
        mCurrentItemPosition = typedArray.getInt(R.styleable.WheelView_wv_currentItemPosition, 0);
        isCyclic = typedArray.getBoolean(R.styleable.WheelView_wv_cyclic, true);

        isShowDivider = typedArray.getBoolean(R.styleable.WheelView_wv_showDivider, false);
        mDividerType = typedArray.getInt(R.styleable.WheelView_wv_dividerType, DIVIDER_TYPE_FILL);
        mDividerSize = typedArray.getDimension(R.styleable.WheelView_wv_dividerHeight, DEFAULT_DIVIDER_HEIGHT);
        mDividerColor = typedArray.getColor(R.styleable.WheelView_wv_dividerColor, DEFAULT_SELECTED_TEXT_COLOR);
        mDividerPaddingForWrap = typedArray.getDimension(R.styleable.WheelView_wv_dividerPaddingForWrap, DEFAULT_TEXT_BOUNDARY_MARGIN);

        isCurved = typedArray.getBoolean(R.styleable.WheelView_wv_curved, true);
        mCurvedAlign = typedArray.getInt(R.styleable.WheelView_wv_curvedAlign, CURVED_ALIGN_CENTER);
        mCurvedAlignBias = typedArray.getFloat(R.styleable.WheelView_wv_curvedAlignBias, DEFAULT_CURVED_BIAS);
        //折射偏移默认值
        mCurvedRefractX = typedArray.getDimension(R.styleable.WheelView_wv_curvedRefractX, mTextSize * 0.05f);
        typedArray.recycle();
    }

    /**
     * 初始化并设置默认值
     *
     * @param context context
     */
    private void initValue(Context context) {
        mPaint.setTextSize(mTextSize);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mMaxFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        mMinFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        mOverScroller = new OverScroller(context);
        mDrawRect = new Rect();
        mCamera = new Camera();
        mMatrix = new Matrix();
        calculateTextSize();
        updateTextAlign();
    }

    /**
     * 测量文字最大所占空间
     */
    private void calculateTextSize() {
        for (int i = 0; i < mDataList.size(); i++) {
            int textWidth = (int) mPaint.measureText(getDataText(mDataList.get(i)));
            mMaxTextWidth = Math.max(textWidth, mMaxTextWidth);
        }

        mFontMetrics = mPaint.getFontMetrics();
        //itemHeight实际等于字体高度+一个行间距
        mItemHeight = (int) (mFontMetrics.bottom - mFontMetrics.top + mLineSpace);
    }

    /**
     * 更新textAlign
     */
    private void updateTextAlign() {
        switch (mTextAlign) {
            case TEXT_ALIGN_LEFT:
                mPaint.setTextAlign(Paint.Align.LEFT);
                break;
            case TEXT_ALIGN_RIGHT:
                mPaint.setTextAlign(Paint.Align.RIGHT);
                break;
            default:
                mPaint.setTextAlign(Paint.Align.CENTER);
                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //Line Space算在了mItemHeight中
        int height;
        if (isCurved) {
            height = (int) ((mItemHeight * mVisibleItems * 2 / Math.PI) + getPaddingTop() + getPaddingBottom());
        } else {
            height = mItemHeight * mVisibleItems + getPaddingTop() + getPaddingBottom();
        }
        int width = (int) (mMaxTextWidth + getPaddingLeft() + getPaddingRight() + mTextBoundaryMargin * 2);
        if (isCurved) {
            int towardRange = (int) (Math.sin(Math.PI / 48) * height);
            width += towardRange;
        }
        setMeasuredDimension(resolveSize(width, widthMeasureSpec), resolveSize(height, heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //设置内容可绘制区域
        mDrawRect.set(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        mCenterX = mDrawRect.centerX();
        mCenterY = mDrawRect.centerY();
        mSelectedItemTopLimit = mCenterY - mItemHeight / 2;
        mSelectedItemBottomLimit = mCenterY + mItemHeight / 2;
        mClipLeft = getPaddingLeft();
        mClipTop = getPaddingTop();
        mClipRight = getWidth() - getPaddingRight();
        mClipBottom = getHeight() - getPaddingBottom();

        calculateDrawStart();
        //计算滚动限制
        calculateLimitY();
    }

    /**
     * 起算起始位置
     */
    private void calculateDrawStart() {
        switch (mTextAlign) {
            case TEXT_ALIGN_LEFT:
                mStartX = (int) (getPaddingLeft() + mTextBoundaryMargin);
                break;
            case TEXT_ALIGN_RIGHT:
                mStartX = (int) (getWidth() - getPaddingRight() - mTextBoundaryMargin);
                break;
            default:
                mStartX = getWidth() / 2;
                break;
        }

        //文字中心距离baseline的距离
        mCenterToBaselineY = (int) (mFontMetrics.ascent + (mFontMetrics.descent - mFontMetrics.ascent) / 2);
    }

    /**
     * 计算滚动限制
     */
    private void calculateLimitY() {
        mMinScrollY = isCyclic ? Integer.MIN_VALUE : 0;
        //下边界 (dataSize - 1 - mInitPosition) * mItemHeight
        mMaxScrollY = isCyclic ? Integer.MAX_VALUE : (mDataList.size() - 1) * mItemHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //滚动了多少个item，滚动的Y值高度除去每行Item的高度
        int scrolledItem = mScrollOffsetY / mItemHeight;
        //没有滚动完一个item时的偏移值，平滑滑动
        int scrolledOffset = mScrollOffsetY % mItemHeight;
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

        //绘制item
        for (int i = minIndex; i < maxIndex; i++) {
            if (isCurved) {
                draw3DItem(canvas, i, scrolledOffset);
            } else {
                drawItem(canvas, i, scrolledOffset);
            }
        }

        //绘制分割线
        drawDivider(canvas);

        if (mOnWheelChangedListener != null) {
            mOnWheelChangedListener.onWheelScroll(mScrollOffsetY);
        }
    }

    /**
     * 绘制分割线
     *
     * @param canvas 画布
     */
    private void drawDivider(Canvas canvas) {
        if (isShowDivider) {
            mPaint.setColor(mDividerColor);
            float originStrokeWidth = mPaint.getStrokeWidth();
            mPaint.setStrokeJoin(mDividerJoin);
            mPaint.setStrokeWidth(mDividerSize);
            if (mDividerType == DIVIDER_TYPE_FILL) {
                canvas.drawLine(mClipLeft, mSelectedItemTopLimit, mClipRight, mSelectedItemTopLimit, mPaint);
                canvas.drawLine(mClipLeft, mSelectedItemBottomLimit, mClipRight, mSelectedItemBottomLimit, mPaint);
            } else {
                //边界处理 超过边界直接按照DIVIDER_TYPE_FILL类型处理
                int startX = (int) (mCenterX - mMaxTextWidth / 2 - mDividerPaddingForWrap);
                int stopX = (int) (mCenterX + mMaxTextWidth / 2 + mDividerPaddingForWrap);
                if (isCurved) {
                    // 弯曲效果时加上折射偏移量x
                    startX -= mCurvedRefractX;
                    stopX += mCurvedRefractX;
                }
                int wrapStartX = startX < mClipLeft ? mClipLeft : startX;
                int wrapStopX = stopX > mClipRight ? mClipRight : stopX;
                canvas.drawLine(wrapStartX, mSelectedItemTopLimit, wrapStopX, mSelectedItemTopLimit, mPaint);
                canvas.drawLine(wrapStartX, mSelectedItemBottomLimit, wrapStopX, mSelectedItemBottomLimit, mPaint);
            }
            mPaint.setStrokeWidth(originStrokeWidth);
        }
    }

    /**
     * 绘制2D效果
     *
     * @param canvas         画布
     * @param index          下标
     * @param scrolledOffset 滚动偏移
     */
    private void drawItem(Canvas canvas, int index, int scrolledOffset) {
        String text = getDataByIndex(index);
        if (text == null) {
            return;
        }

        //index 的 item 距离中间项的偏移
        int item2CenterOffsetY = (index - mScrollOffsetY / mItemHeight) * mItemHeight - scrolledOffset;

        if (Math.abs(item2CenterOffsetY) <= 0) {
            //绘制选中的条目
            mPaint.setColor(mSelectedItemColor);
            clipAndDraw2DText(canvas, text, mSelectedItemTopLimit, mSelectedItemBottomLimit, item2CenterOffsetY);
        } else if (item2CenterOffsetY > 0 && item2CenterOffsetY < mItemHeight) {
            //绘制与下边界交汇的条目
            mPaint.setColor(mSelectedItemColor);
            clipAndDraw2DText(canvas, text, mSelectedItemTopLimit, mSelectedItemBottomLimit, item2CenterOffsetY);

            mPaint.setColor(mTextColor);
            clipAndDraw2DText(canvas, text, mSelectedItemBottomLimit, mClipBottom, item2CenterOffsetY);

        } else if (item2CenterOffsetY < 0 && item2CenterOffsetY > -mItemHeight) {
            //绘制与上边界交汇的条目
            mPaint.setColor(mSelectedItemColor);
            clipAndDraw2DText(canvas, text, mSelectedItemTopLimit, mSelectedItemBottomLimit, item2CenterOffsetY);

            mPaint.setColor(mTextColor);
            clipAndDraw2DText(canvas, text, mClipTop, mSelectedItemTopLimit, item2CenterOffsetY);

        } else {
            //绘制其他条目
            mPaint.setColor(mTextColor);
            clipAndDraw2DText(canvas, text, mClipTop, mClipBottom, item2CenterOffsetY);
        }
    }

    /**
     * 裁剪并绘制2d text
     *
     * @param canvas             画布
     * @param text               绘制的文字
     * @param clipTop            裁剪的上边界
     * @param clipBottom         裁剪的下边界
     * @param item2CenterOffsetY 距离中间项的偏移
     */
    private void clipAndDraw2DText(Canvas canvas, String text, int clipTop, int clipBottom, int item2CenterOffsetY) {
        canvas.save();
        canvas.clipRect(mClipLeft, clipTop, mClipRight, clipBottom);
        canvas.drawText(text, 0, text.length(), mStartX, mCenterY + item2CenterOffsetY - mCenterToBaselineY, mPaint);
        canvas.restore();
    }

    /**
     * 绘制弯曲（3D）效果的item
     *
     * @param canvas         画布
     * @param index          下标
     * @param scrolledOffset 滚动偏移
     */
    private void draw3DItem(Canvas canvas, int index, int scrolledOffset) {
        String text = getDataByIndex(index);
        if (text == null) {
            return;
        }
        // 滚轮的半径
        final int radius = (getHeight() - getPaddingTop() - getPaddingBottom()) / 2;
        //index 的 item 距离中间项的偏移
        int item2CenterOffsetY = (index - mScrollOffsetY / mItemHeight) * mItemHeight - scrolledOffset;

        // 当滑动的角度和y轴垂直时（此时文字已经显示为一条线），不绘制文字
        if (Math.abs(item2CenterOffsetY) > radius * Math.PI / 2) return;

        final double angle = (double) item2CenterOffsetY / radius;
        // 绕x轴滚动的角度
        float rotateX = (float) Math.toDegrees(-angle);
        // 滚动的距离映射到y轴的长度
        float translateY = (float) (Math.sin(angle) * radius);
        // 滚动的距离映射到z轴的长度
        float translateZ = (float) ((1 - Math.cos(angle)) * radius);
        // 折射偏移量x
        float refractX = mCurvedRefractX;
        // 透明度
        int alpha = (int) (Math.cos(angle) * 255);

        if (Math.abs(item2CenterOffsetY) <= 0) {
            //绘制选中的条目
            mPaint.setColor(mSelectedItemColor);
            mPaint.setAlpha(255);
            clipAndDraw3DText(canvas, text, mSelectedItemTopLimit, mSelectedItemBottomLimit, refractX, rotateX, translateY, translateZ);
        } else if (item2CenterOffsetY > 0 && item2CenterOffsetY < mItemHeight) {
            //绘制与下边界交汇的条目
            mPaint.setColor(mSelectedItemColor);
            mPaint.setAlpha(255);
            clipAndDraw3DText(canvas, text, mSelectedItemTopLimit, mSelectedItemBottomLimit, refractX, rotateX, translateY, translateZ);

            mPaint.setColor(mTextColor);
            mPaint.setAlpha(alpha);
            clipAndDraw3DText(canvas, text, mSelectedItemBottomLimit, mClipBottom, -1, rotateX, translateY, translateZ);
        } else if (item2CenterOffsetY < 0 && item2CenterOffsetY > -mItemHeight) {
            //绘制与上边界交汇的条目
            mPaint.setColor(mSelectedItemColor);
            mPaint.setAlpha(255);
            clipAndDraw3DText(canvas, text, mSelectedItemTopLimit, mSelectedItemBottomLimit, refractX, rotateX, translateY, translateZ);

            mPaint.setColor(mTextColor);
            mPaint.setAlpha(alpha);
            clipAndDraw3DText(canvas, text, mClipTop, mSelectedItemTopLimit, -1, rotateX, translateY, translateZ);
        } else {
            //绘制其他条目
            mPaint.setColor(mTextColor);
            mPaint.setAlpha(alpha);
            clipAndDraw3DText(canvas, text, mClipTop, mClipBottom, -1, rotateX, translateY, translateZ);
        }
    }

    /**
     * 裁剪并绘制弯曲（3D）效果
     *
     * @param canvas     画布
     * @param text       绘制的文字
     * @param clipTop    裁剪的上边界
     * @param clipBottom 裁剪的下边界
     * @param refractX   选中item折射效果的偏移量
     * @param rotateX    绕X轴旋转角度
     * @param offsetY    Y轴偏移
     * @param offsetZ    Z轴偏移
     */
    private void clipAndDraw3DText(Canvas canvas, String text, int clipTop, int clipBottom, float refractX,
                                   float rotateX, float offsetY, float offsetZ) {

        canvas.save();
        //refractX大于0实现偏移效果
        if (refractX > 0) {
            canvas.translate(refractX, 0);
        }
        canvas.clipRect(mClipLeft, clipTop, mClipRight, clipBottom);
        draw3DText(canvas, text, rotateX, offsetY, offsetZ);
        canvas.restore();
    }

    /**
     * 绘制弯曲（3D）的文字
     *
     * @param canvas  画布
     * @param text    绘制的文字
     * @param rotateX 绕X轴旋转角度
     * @param offsetY Y轴偏移
     * @param offsetZ Z轴偏移
     */
    private void draw3DText(Canvas canvas, String text, float rotateX, float offsetY, float offsetZ) {
        mCamera.save();
        mCamera.translate(0, 0, offsetZ);
        mCamera.rotateX(rotateX);
        mCamera.getMatrix(mMatrix);
        mCamera.restore();

        // 调节中心点
        float centerX = mCenterX;
        //根据弯曲（3d）对齐方式设置系数
        if (mCurvedAlign == CURVED_ALIGN_LEFT) {
            centerX = mCenterX * (1 + mCurvedAlignBias);
        } else if (mCurvedAlign == CURVED_ALIGN_RIGHT) {
            centerX = mCenterX * (1 - mCurvedAlignBias);
        }

        float centerY = mCenterY + offsetY;
        mMatrix.preTranslate(-centerX, -centerY);
        mMatrix.postTranslate(centerX, centerY);

        canvas.concat(mMatrix);
        canvas.drawText(text, 0, text.length(), mStartX, centerY - mCenterToBaselineY, mPaint);

    }

    /**
     * 根据下标获取到内容
     *
     * @param index 下标
     * @return 绘制的文字内容
     */
    private String getDataByIndex(int index) {
        int dataSize = mDataList.size();
        if (dataSize == 0) {
            return null;
        }

        String itemText = null;
        if (isCyclic) {
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
     * @param item item数据
     * @return 文本内容
     */
    private String getDataText(T item) {
        if (item == null) {
            return "";
        } else if (item instanceof IWheelEntity) {
            return ((IWheelEntity) item).getWheelText();
        } else if (item instanceof Integer) {
            //如果为整形则最少保留两位数.
            return isIntegerNeedFormat ? String.format(Locale.getDefault(), mIntegerFormat, (Integer) item)
                    : String.valueOf(item);
        } else if (item instanceof String) {
            return (String) item;
        }
        return item.toString();
    }

    /**
     * 获取数据列表
     *
     * @return 数据列表
     */
    public List<T> getData() {
        return mDataList;
    }

    /**
     * 设置数据
     *
     * @param dataList 数据列表
     */
    public void setData(List<T> dataList) {
        if (dataList == null) {
            return;
        }
        mDataList = dataList;
        if (mCurrentItemPosition > mDataList.size()) {
            mCurrentItemPosition = mDataList.size() - 1;
        }
        if (!mOverScroller.isFinished()) {
            mOverScroller.forceFinished(true);
        }
        mScrollOffsetY = 0;
        calculateTextSize();
        calculateLimitY();
        requestLayout();
        invalidate();
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
                //处理滑动事件嵌套 拦截事件序列
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                //如果未滚动完成，强制滚动完成
                if (!mOverScroller.isFinished()) {
                    //强制滚动完成
                    mOverScroller.forceFinished(true);
                    isForceFinishScroll = true;
                }
                mLastTouchY = event.getY();
                //按下时间
                mDownStartTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                //手指移动
                float moveY = event.getY();
                float deltaY = moveY - mLastTouchY;

                if (mOnWheelChangedListener != null) {
                    mOnWheelChangedListener.onWheelScrollStateChanged(SCROLL_STATE_DRAGGING);
                }
                if (Math.abs(deltaY) < 1) {
                    break;
                }
                //deltaY 上滑为正，下滑为负
                doScroll((int) -deltaY);
                mLastTouchY = moveY;
                invalidate();

                break;
            case MotionEvent.ACTION_UP:
                //手指抬起
                isForceFinishScroll = false;
                mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);
                float velocityY = mVelocityTracker.getYVelocity();
                if (Math.abs(velocityY) > mMinFlingVelocity) {
                    //快速滑动
                    mOverScroller.forceFinished(true);
                    isFlingScroll = true;
                    mOverScroller.fling(0, mScrollOffsetY, 0, (int) -velocityY, 0, 0,
                            mMinScrollY, mMaxScrollY);
                } else {
                    int clickToCenterDistance = 0;
                    if (System.currentTimeMillis() - mDownStartTime <= DEFAULT_CLICK_CONFIRM) {
                        //处理点击滚动
                        //手指抬起的位置到中心的距离为滚动差值
                        clickToCenterDistance = (int) (event.getY() - mCenterY);
                    }

                    //平稳滑动
                    mOverScroller.startScroll(0, mScrollOffsetY, 0, clickToCenterDistance +
                            calculateDistanceToEndPoint((mScrollOffsetY + clickToCenterDistance) % mItemHeight));
                }

                invalidate();
                ViewCompat.postOnAnimation(this, this);

                //回收 VelocityTracker
                recycleVelocityTracker();
                break;
            case MotionEvent.ACTION_CANCEL:
                //事件被终止
                //回收
                recycleVelocityTracker();
                break;
        }
        return true;
    }

    /**
     * 回收 VelocityTracker
     */
    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    /**
     * 计算滚动偏移
     *
     * @param distance 滚动距离
     */
    private void doScroll(int distance) {
        mScrollOffsetY += distance;
        if (!isCyclic) {
            //修正边界
            if (mScrollOffsetY < mMinScrollY) {
                mScrollOffsetY = mMinScrollY;
            } else if (mScrollOffsetY > mMaxScrollY) {
                mScrollOffsetY = mMaxScrollY;
            }
        }
    }

    /**
     * 计算距离终点的偏移，修正选中条目
     *
     * @param remainder 余数
     * @return
     */
    private int calculateDistanceToEndPoint(int remainder) {
        if (Math.abs(remainder) > mItemHeight / 2) {
            if (mScrollOffsetY < 0) {
                return -mItemHeight - remainder;
            } else {
                return mItemHeight - remainder;
            }
        } else {
            return -remainder;
        }
    }

    /**
     * 使用run方法而不是computeScroll是因为，invalidate也会执行computeScroll导致回调执行不准确
     */
    @Override
    public void run() {
        //停止滚动更新当前下标
        if (mOverScroller.isFinished() && !isForceFinishScroll) {
            if (mItemHeight == 0) return;
            mCurrentItemPosition = getCurrentPosition();
            //停止滚动，选中条目回调
            if (mOnItemSelectedListener != null) {
                mOnItemSelectedListener.onItemSelected(this, mDataList.get(mCurrentItemPosition), mCurrentItemPosition);
            }
            //滚动状态回调
            if (mOnWheelChangedListener != null) {
                mOnWheelChangedListener.onWheelSelected(mCurrentItemPosition);
                mOnWheelChangedListener.onWheelScrollStateChanged(SCROLL_STATE_IDLE);
            }
        }

        if (mOverScroller.computeScrollOffset()) {
            if (mOnWheelChangedListener != null) {
                mOnWheelChangedListener.onWheelScrollStateChanged(SCROLL_STATE_SCROLLING);
            }
            mScrollOffsetY = mOverScroller.getCurrY();
            invalidate();
            ViewCompat.postOnAnimation(this, this);
        } else if (isFlingScroll) {
            //滚动完成后，根据是否为快速滚动处理是否需要调整最终位置
            isFlingScroll = false;
            //快速滚动后需要调整滚动完成后的最终位置，重新启动scroll滑动到中心位置
            mOverScroller.startScroll(0, mScrollOffsetY, 0, calculateDistanceToEndPoint(mScrollOffsetY % mItemHeight));
            invalidate();
            ViewCompat.postOnAnimation(this, this);
        }
    }

    /**
     * 根据偏移计算当前位置下标
     *
     * @return
     */
    private int getCurrentPosition() {
        int itemPosition;
        if (mScrollOffsetY < 0) {
            itemPosition = (mScrollOffsetY - mItemHeight / 2) / mItemHeight;
        } else {
            itemPosition = (mScrollOffsetY + mItemHeight / 2) / mItemHeight;
        }
        int currentPosition = itemPosition % mDataList.size();
        if (currentPosition < 0) {
            currentPosition += mDataList.size();
        }

        return currentPosition;
    }

    /**
     * 获取字体大小
     *
     * @return 字体大小
     */
    public float getTextSize() {
        return mTextSize;
    }

    /**
     * 设置字体大小
     *
     * @param textSize 字体大小
     */
    public void setTextSize(float textSize) {
        setTextSize(textSize, false);
    }

    /**
     * 设置字体大小
     *
     * @param textSize 字体大小
     * @param isSp     是否是sp
     */
    public void setTextSize(float textSize, boolean isSp) {
        float tempTextSize = mTextSize;
        mTextSize = isSp ? sp2px(textSize) : textSize;
        if (tempTextSize == mTextSize) {
            return;
        }
        calculateTextSize();
        calculateDrawStart();
        requestLayout();
        invalidate();
    }

    /**
     * 获取当前字体
     *
     * @return 字体
     */
    public Typeface getTypeface() {
        return mPaint.getTypeface();
    }

    /**
     * 设置当前字体
     *
     * @param typeface 字体
     */
    public void setTypeface(Typeface typeface) {
        if (mPaint.getTypeface() == typeface) {
            return;
        }
        mPaint.setTypeface(typeface);
        calculateTextSize();
        calculateDrawStart();
        requestLayout();
        invalidate();
    }

    /**
     * 获取文字对齐方式
     *
     * @return 文字对齐
     */
    public int getTextAlign() {
        return mTextAlign;
    }

    /**
     * 设置文字对齐方式
     *
     * @param textAlign
     */
    public void setTextAlign(@TextAlign int textAlign) {
        if (mTextAlign == textAlign) {
            return;
        }
        mTextAlign = textAlign;
        updateTextAlign();
        calculateDrawStart();
        invalidate();
    }

    /**
     * 获取未选中条目颜色
     *
     * @return
     */
    public int getNormalItemTextColor() {
        return mTextColor;
    }

    /**
     * 设置未选中条目颜色
     *
     * @param textColorRes
     */
    public void setNormalItemTextColorRes(@ColorRes int textColorRes) {
        setNormalItemTextColor(ContextCompat.getColor(getContext(), textColorRes));
    }

    /**
     * 设置未选中条目颜色
     *
     * @param textColor
     */
    public void setNormalItemTextColor(@ColorInt int textColor) {
        if (mTextColor == textColor) {
            return;
        }
        mTextColor = textColor;
        invalidate();
    }

    /**
     * 获取选中条目颜色
     *
     * @return
     */
    public int getSelectedItemColor() {
        return mSelectedItemColor;
    }

    /**
     * 设置选中条目颜色
     *
     * @param selectedItemColorRes
     */
    public void setSelectedItemColorRes(@ColorRes int selectedItemColorRes) {
        setSelectedItemColor(ContextCompat.getColor(getContext(), selectedItemColorRes));
    }

    /**
     * 设置选中条目颜色
     *
     * @param selectedItemColor
     */
    public void setSelectedItemColor(@ColorInt int selectedItemColor) {
        if (mSelectedItemColor == selectedItemColor) {
            return;
        }
        mSelectedItemColor = selectedItemColor;
        invalidate();
    }

    /**
     * 获取文字距离边界的外边距
     *
     * @return
     */
    public float getTextBoundaryMargin() {
        return mTextBoundaryMargin;
    }

    /**
     * 设置文字距离边界的外边距
     *
     * @param textBoundaryMargin
     */
    public void setTextBoundaryMargin(float textBoundaryMargin) {
        setTextBoundaryMargin(textBoundaryMargin, false);
    }

    /**
     * 设置文字距离边界的外边距
     *
     * @param textBoundaryMargin
     * @param isDp
     */
    public void setTextBoundaryMargin(float textBoundaryMargin, boolean isDp) {
        float tempTextBoundaryMargin = mTextBoundaryMargin;
        mTextBoundaryMargin = isDp ? dp2px(textBoundaryMargin) : textBoundaryMargin;
        if (tempTextBoundaryMargin == mTextBoundaryMargin) {
            return;
        }
        requestLayout();
        invalidate();
    }

    /**
     * 获取item间距
     *
     * @return
     */
    public float getLineSpace() {
        return mLineSpace;
    }

    /**
     * 设置item间距
     *
     * @param lineSpace
     */
    public void setLineSpace(float lineSpace) {
        setLineSpace(lineSpace, false);
    }

    /**
     * 设置item间距
     *
     * @param lineSpace
     */
    public void setLineSpace(float lineSpace, boolean isDp) {
        float tempLineSpace = mLineSpace;
        mLineSpace = isDp ? dp2px(lineSpace) : lineSpace;
        if (tempLineSpace == mLineSpace) {
            return;
        }
        mScrollOffsetY = 0;
        calculateTextSize();
        requestLayout();
        invalidate();
    }

    /**
     * 获取数据为Integer类型时是否需要转换
     *
     * @return
     */
    public boolean isIntegerNeedFormat() {
        return isIntegerNeedFormat;
    }

    /**
     * 设置数据为Integer类型时是否需要转换
     *
     * @param integerNeedFormat
     */
    public void setIntegerNeedFormat(boolean integerNeedFormat) {
        if (isIntegerNeedFormat == integerNeedFormat) {
            return;
        }
        isIntegerNeedFormat = integerNeedFormat;
        calculateTextSize();
        requestLayout();
        invalidate();
    }

    /**
     * 获取Integer类型转换格式
     *
     * @return
     */
    public String getIntegerFormat() {
        return mIntegerFormat;
    }

    /**
     * 设置Integer类型转换格式
     *
     * @param integerFormat
     */
    public void setIntegerFormat(String integerFormat) {
        if (TextUtils.isEmpty(integerFormat) || integerFormat.equals(mIntegerFormat)) {
            return;
        }
        mIntegerFormat = integerFormat;
        calculateTextSize();
        requestLayout();
        invalidate();
    }

    /**
     * 获取可见条目数
     *
     * @return
     */
    public int getVisibleItems() {
        return mVisibleItems;
    }

    /**
     * 设置可见的条目数
     *
     * @param visibleItems
     */
    public void setVisibleItems(int visibleItems) {
        if (mVisibleItems == visibleItems) {
            return;
        }
        mVisibleItems = Math.abs(visibleItems / 2 * 2 + 1); // 当传入的值为偶数时,换算成奇数;
        mScrollOffsetY = 0;
        requestLayout();
        invalidate();
    }

    /**
     * 是否是循环滚动
     *
     * @return
     */
    public boolean isCyclic() {
        return isCyclic;
    }

    /**
     * 设置是否循环滚动
     *
     * @param cyclic
     */
    public void setCyclic(boolean cyclic) {
        if (isCyclic == cyclic) {
            return;
        }
        isCyclic = cyclic;
        mScrollOffsetY = 0;
        calculateLimitY();
        invalidate();
    }

    /**
     * 获取当前下标
     *
     * @return
     */
    public int getCurrentItemPosition() {
        return mCurrentItemPosition;
    }

    /**
     * 设置当前下标
     *
     * @param position 下标
     */
    public void setCurrentItemPosition(int position) {
        setCurrentItemPosition(position, true);
    }

    /**
     * 设置当前下标
     *
     * @param position       下标
     * @param isSmoothScroll 是否平滑滚动
     */
    public void setCurrentItemPosition(int position, boolean isSmoothScroll) {
        setCurrentItemPosition(position, isSmoothScroll, 0);
    }

    /**
     * 设置当前下标
     *
     * @param position       下标
     * @param isSmoothScroll 是否平滑滚动
     * @param smoothDuration 平滑滚动时间
     */
    public void setCurrentItemPosition(int position, boolean isSmoothScroll, int smoothDuration) {
        if (!isPositionInRange(position)) {
            return;
        }

        //item之间差值
        int itemDistance = position * mItemHeight - mScrollOffsetY;
        if (itemDistance == 0) {
            return;
        }
        //如果Scroller滑动未停止，强制结束动画
        if (!mOverScroller.isFinished()) {
            mOverScroller.abortAnimation();
        }
        if (isSmoothScroll) {
            //如果是平滑滚动并且之前的Scroll滚动完成
            mOverScroller.startScroll(0, mScrollOffsetY, 0, itemDistance,
                    smoothDuration > 0 ? smoothDuration : DEFAULT_SCROLL_DURATION);
            invalidate();
            ViewCompat.postOnAnimation(this, this);

        } else {
            doScroll(itemDistance);
            invalidate();
        }

    }

    /**
     * 判断下标是否在数据列表范围内
     *
     * @param position
     * @return
     */
    private boolean isPositionInRange(int position) {
        return position >= 0 && position < mDataList.size();
    }

    /**
     * 获取是否显示分割线
     *
     * @return
     */
    public boolean isShowDivider() {
        return isShowDivider;
    }

    /**
     * 设置是否显示分割线
     *
     * @param showDivider
     */
    public void setShowDivider(boolean showDivider) {
        if (isShowDivider == showDivider) {
            return;
        }
        isShowDivider = showDivider;
        invalidate();
    }

    /**
     * 获取分割线颜色
     *
     * @return
     */
    public int getDividerColor() {
        return mDividerColor;
    }

    public void setDividerColorRes(@ColorRes int dividerColorRes) {
        setDividerColor(ContextCompat.getColor(getContext(), dividerColorRes));
    }

    /**
     * 设置分割线颜色
     *
     * @param dividerColor
     */
    public void setDividerColor(@ColorInt int dividerColor) {
        if (mDividerColor == dividerColor) {
            return;
        }
        mDividerColor = dividerColor;
        invalidate();
    }

    /**
     * 获取分割线高度
     *
     * @return
     */
    public float getDividerHeight() {
        return mDividerSize;
    }

    /**
     * 设置分割线高度
     *
     * @param dividerHeight
     */
    public void setDividerHeight(float dividerHeight) {
        setDividerHeight(dividerHeight, false);
    }

    /**
     * 设置分割线高度
     *
     * @param dividerHeight
     * @param isDp
     */
    public void setDividerHeight(float dividerHeight, boolean isDp) {
        float tempDividerHeight = mDividerSize;
        mDividerSize = isDp ? dp2px(dividerHeight) : dividerHeight;
        if (tempDividerHeight == mDividerSize) {
            return;
        }
        invalidate();
    }

    /**
     * 获取分割线填充类型
     *
     * @return
     */
    public int getDividerType() {
        return mDividerType;
    }

    /**
     * 设置分割线填充类型
     *
     * @param dividerType
     */
    public void setDividerType(@DividerType int dividerType) {
        if (mDividerType == dividerType) {
            return;
        }
        mDividerType = dividerType;
        invalidate();
    }

    /**
     * 获取自适应分割线类型时的分割线内边距
     *
     * @return
     */
    public float getDividerPaddingForWrap() {
        return mDividerPaddingForWrap;
    }

    /**
     * 设置自适应分割线类型时的分割线内边距
     *
     * @param dividerPaddingForWrap
     */
    public void setDividerPaddingForWrap(float dividerPaddingForWrap) {
        setDividerPaddingForWrap(dividerPaddingForWrap, false);
    }

    /**
     * 设置自适应分割线类型时的分割线内边距
     *
     * @param wrapDividerPadding
     * @param isDp
     */
    public void setDividerPaddingForWrap(float wrapDividerPadding, boolean isDp) {
        float tempDividerPadding = mDividerPaddingForWrap;
        mDividerPaddingForWrap = isDp ? dp2px(wrapDividerPadding) : wrapDividerPadding;
        if (tempDividerPadding == mDividerPaddingForWrap) {
            return;
        }
        invalidate();
    }

    /**
     * 获取分割线两端形状
     *
     * @return
     */
    public Paint.Join getDividerJoin() {
        return mDividerJoin;
    }

    /**
     * 设置分割线两端形状
     *
     * @param dividerJoin
     */
    public void setDividerJoin(Paint.Join dividerJoin) {
        if (mDividerJoin == dividerJoin) {
            return;
        }
        mDividerJoin = dividerJoin;
        invalidate();
    }

    /**
     * 获取是否是弯曲（3D）效果
     *
     * @return
     */
    public boolean isCurved() {
        return isCurved;
    }

    /**
     * 设置是否是弯曲（3D）效果
     *
     * @param isCurved
     */
    public void setCurved(boolean isCurved) {
        if (this.isCurved == isCurved) {
            return;
        }
        this.isCurved = isCurved;
        calculateTextSize();
        requestLayout();
        invalidate();
    }

    /**
     * 获取弯曲（3D）效果对齐方式
     *
     * @return
     */
    public int getCurvedAlign() {
        return mCurvedAlign;
    }

    /**
     * 设置弯曲（3D）效果对齐方式
     *
     * @param curvedAlign 向指定方向倾斜
     */
    public void setCurvedAlign(@CurvedAlign int curvedAlign) {
        if (mCurvedAlign == curvedAlign) {
            return;
        }
        mCurvedAlign = curvedAlign;
        invalidate();
    }

    /**
     * 获取弯曲（3D）效果对齐系数
     *
     * @return
     */
    public float getCurvedAlignBias() {
        return mCurvedAlignBias;
    }

    /**
     * 设置弯曲（3D）效果对齐系数
     *
     * @param curvedAlignBias 0-1之间
     */
    public void setCurvedAlignBias(@FloatRange(from = 0, to = 1.0) float curvedAlignBias) {
        if (mCurvedAlignBias == curvedAlignBias) {
            return;
        }
        if (curvedAlignBias < 0) {
            curvedAlignBias = 0f;
        } else if (curvedAlignBias > 1) {
            curvedAlignBias = 1f;
        }
        mCurvedAlignBias = curvedAlignBias;
        invalidate();
    }

    /**
     * 获取折射偏移
     *
     * @return
     */
    public float getCurvedRefractX() {
        return mCurvedRefractX;
    }

    /**
     * 设置选中条目折射偏移
     *
     * @param curvedRefractX
     */
    public void setCurvedRefractX(float curvedRefractX) {
        setCurvedRefractX(curvedRefractX, false);
    }

    /**
     * 设置选中条目折射偏移
     *
     * @param curvedRefractX
     * @param isDp
     */
    public void setCurvedRefractX(float curvedRefractX, boolean isDp) {
        float tempRefractX = mCurvedRefractX;
        mCurvedRefractX = isDp ? dp2px(curvedRefractX) : curvedRefractX;
        if (tempRefractX == mCurvedRefractX) {
            return;
        }
        invalidate();
    }

    /**
     * 获取选中监听
     *
     * @return
     */
    public OnItemSelectedListener<T> getOnItemSelectedListener() {
        return mOnItemSelectedListener;
    }

    /**
     * 设置选中监听
     *
     * @param onItemSelectedListener
     */
    public void setOnItemSelectedListener(OnItemSelectedListener<T> onItemSelectedListener) {
        mOnItemSelectedListener = onItemSelectedListener;
    }

    /**
     * 获取滚动变化监听
     *
     * @return
     */
    public OnWheelChangedListener getOnWheelChangedListener() {
        return mOnWheelChangedListener;
    }

    /**
     * 设置滚动变化监听
     *
     * @param onWheelChangedListener
     */
    public void setOnWheelChangedListener(OnWheelChangedListener onWheelChangedListener) {
        mOnWheelChangedListener = onWheelChangedListener;
    }

    protected static float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    protected static float sp2px(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, Resources.getSystem().getDisplayMetrics());
    }

    @IntDef({TEXT_ALIGN_LEFT, TEXT_ALIGN_CENTER, TEXT_ALIGN_RIGHT})
    @Retention(value = RetentionPolicy.SOURCE)
    @interface TextAlign {
    }

    @IntDef({CURVED_ALIGN_LEFT, CURVED_ALIGN_CENTER, CURVED_ALIGN_RIGHT})
    @interface CurvedAlign {
    }

    @IntDef({DIVIDER_TYPE_FILL, DIVIDER_TYPE_WRAP})
    @interface DividerType {
    }

    /**
     * 条目选中监听器
     *
     * @param <T>
     */
    public interface OnItemSelectedListener<T> {

        /**
         * 条目选中回调
         *
         * @param wheelView wheelView
         * @param data      选中的数据
         * @param position  选中的下标
         */
        void onItemSelected(WheelView<T> wheelView, T data, int position);
    }

    /**
     * WheelView滚动状态改变监听器
     */
    public interface OnWheelChangedListener {

        /**
         * WheelView 滚动
         *
         * @param scrollOffsetY
         */
        void onWheelScroll(int scrollOffsetY);

        /**
         * WheelView 选中
         *
         * @param position
         */
        void onWheelSelected(int position);

        /**
         * WheelView 滚动状态
         *
         * @param state
         */
        void onWheelScrollStateChanged(int state);
    }
}
