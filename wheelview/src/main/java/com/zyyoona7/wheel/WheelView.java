package com.zyyoona7.wheel;

import android.annotation.SuppressLint;
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
import android.media.AudioManager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.FloatRange;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.zyyoona7.wheel.formatter.ItemTextFormatter;
import com.zyyoona7.wheel.sound.SoundHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zyyoona7
 * @since 2018/8/7.
 */
public class WheelView<T> extends View implements Runnable {

    private static final float DEFAULT_LINE_SPACING = dp2px(2f);
    private static final float DEFAULT_TEXT_SIZE = sp2px(15f);
    private static final float DEFAULT_TEXT_BOUNDARY_MARGIN = dp2px(2);
    private static final float DEFAULT_DIVIDER_HEIGHT = dp2px(1);
    private static final int DEFAULT_NORMAL_TEXT_COLOR = Color.DKGRAY;
    private static final int DEFAULT_SELECTED_TEXT_COLOR = Color.BLACK;
    private static final int DEFAULT_VISIBLE_ITEM = 5;
    private static final int DEFAULT_SCROLL_DURATION = 250;
    private static final long DEFAULT_CLICK_CONFIRM = 120;
    //默认折射比值，通过字体大小来实现折射视觉差
    private static final float DEFAULT_REFRACT_RATIO = 1f;

    //文字对齐方式
    public static final int TEXT_ALIGN_LEFT = 0;
    public static final int TEXT_ALIGN_CENTER = 1;
    public static final int TEXT_ALIGN_RIGHT = 2;

    //滚动状态
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_SCROLLING = 2;

    //弯曲效果对齐方式
    public static final int CURVED_ARC_DIRECTION_LEFT = 0;
    public static final int CURVED_ARC_DIRECTION_CENTER = 1;
    public static final int CURVED_ARC_DIRECTION_RIGHT = 2;

    public static final float DEFAULT_CURVED_FACTOR = 0.75f;

    //分割线填充类型
    public static final int DIVIDER_TYPE_FILL = 0;
    public static final int DIVIDER_TYPE_WRAP = 1;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //字体大小
    private float mTextSize;
    //是否自动调整字体大小以显示完全
    private boolean isAutoFitTextSize;
    private Paint.FontMetrics mFontMetrics;
    //每个item的高度
    private int mItemHeight;
    //文字的最大宽度
    private int mMaxTextWidth;
    //文字中心距离baseline的距离
    private int mCenterToBaselineY;
    //可见的item条数
    private int mVisibleItems;
    //每个item之间的空间，行间距
    private float mLineSpacing;
    //是否循环滚动
    private boolean isCyclic;
    //文字对齐方式
    @TextAlign
    private int mTextAlign;
    //文字颜色
    private int mTextColor;
    //选中item文字颜色
    private int mSelectedItemTextColor;

    //是否显示分割线
    private boolean isShowDivider;
    //分割线的颜色
    private int mDividerColor;
    //分割线高度
    private float mDividerSize;
    //分割线填充类型
    @DividerType
    private int mDividerType;
    //分割线类型为DIVIDER_TYPE_WRAP时 分割线左右两端距离文字的间距
    private float mDividerPaddingForWrap;
    //分割线两端形状，默认圆头
    private Paint.Cap mDividerCap = Paint.Cap.ROUND;
    //分割线和选中区域偏移，实现扩大选中区域
    private float mDividerOffset;

    //是否绘制选中区域
    private boolean hasCurtain;
    //选中区域颜色
    private int mCurtainColor;

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

    //3D效果
    private Camera mCamera;
    private Matrix mMatrix;
    //是否是弯曲（3D）效果
    private boolean isCurved;
    //弯曲（3D）效果左右圆弧偏移效果方向 center 不偏移
    @CurvedArcDirection
    private int mCurvedArcDirection;
    //弯曲（3D）效果左右圆弧偏移效果系数 0-1之间 越大越明显
    private float mCurvedArcDirectionFactor;
    //选中后折射的偏移 与字体大小的比值，1为不偏移 越小偏移越明显
    //(普通效果和3d效果都适用)
    private float mRefractRatio;
    //数据变化时，是否重置选中下标到第一个位置
    private boolean isResetSelectedPosition = false;

    private VelocityTracker mVelocityTracker;
    private int mMaxFlingVelocity;
    private int mMinFlingVelocity;
    private Scroller mScroller;

    //最小滚动距离，上边界
    private int mMinScrollY;
    //最大滚动距离，下边界
    private int mMaxScrollY;

    //Y轴滚动偏移
    private int mScrollOffsetY;
    //Y轴已滚动偏移，控制重绘次数
    private int mScrolledY = 0;
    //手指最后触摸的位置
    private float mLastTouchY;
    //手指按下时间，根据按下抬起时间差处理点击滚动
    private long mDownStartTime;
    //是否强制停止滚动
    private boolean isForceFinishScroll = false;
    //是否是快速滚动，快速滚动结束后跳转位置
    private boolean isFlingScroll;
    //当前选中的下标
    private int mSelectedItemPosition;
    //当前滚动经过的下标
    private int mCurrentScrollPosition;

    //字体
    private boolean mIsBoldForSelectedItem = false;
    //如果 mIsBoldForSelectedItem==true 则这个字体为未选中条目的字体
    private Typeface mNormalTypeface = null;
    //如果 mIsBoldForSelectedItem==true 则这个字体为选中条目的字体
    private Typeface mBoldTypeface = null;

    //监听器
    private OnItemSelectedListener<T> mOnItemSelectedListener;
    private OnWheelChangedListener mOnWheelChangedListener;

    //音频
    private SoundHelper mSoundHelper;
    //是否开启音频效果
    private boolean isSoundEffect = false;

    private final DefaultDataDelegate<T> mDataDelegate = new DefaultDataDelegate<>();

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
     * @param context 上下文
     * @param attrs   attrs
     */
    private void initAttrsAndDefault(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WheelView);
        mTextSize = typedArray.getDimension(R.styleable.WheelView_wv_textSize, DEFAULT_TEXT_SIZE);
        isAutoFitTextSize = typedArray.getBoolean(R.styleable.WheelView_wv_autoFitTextSize, false);
        mTextAlign = typedArray.getInt(R.styleable.WheelView_wv_textAlign, TEXT_ALIGN_CENTER);
        mTextBoundaryMargin = typedArray.getDimension(R.styleable.WheelView_wv_textBoundaryMargin,
                DEFAULT_TEXT_BOUNDARY_MARGIN);
        mTextColor = typedArray.getColor(R.styleable.WheelView_wv_normalItemTextColor, DEFAULT_NORMAL_TEXT_COLOR);
        mSelectedItemTextColor = typedArray.getColor(R.styleable.WheelView_wv_selectedItemTextColor, DEFAULT_SELECTED_TEXT_COLOR);
        mLineSpacing = typedArray.getDimension(R.styleable.WheelView_wv_lineSpacing, DEFAULT_LINE_SPACING);

        mVisibleItems = typedArray.getInt(R.styleable.WheelView_wv_visibleItems, DEFAULT_VISIBLE_ITEM);
        //跳转可见item为奇数
        mVisibleItems = adjustVisibleItems(mVisibleItems);
        mSelectedItemPosition = typedArray.getInt(R.styleable.WheelView_wv_selectedItemPosition, 0);
        //初始化滚动下标
        mCurrentScrollPosition = mSelectedItemPosition;
        isCyclic = typedArray.getBoolean(R.styleable.WheelView_wv_cyclic, false);

        isShowDivider = typedArray.getBoolean(R.styleable.WheelView_wv_showDivider, false);
        mDividerType = typedArray.getInt(R.styleable.WheelView_wv_dividerType, DIVIDER_TYPE_FILL);
        mDividerSize = typedArray.getDimension(R.styleable.WheelView_wv_dividerHeight, DEFAULT_DIVIDER_HEIGHT);
        mDividerColor = typedArray.getColor(R.styleable.WheelView_wv_dividerColor, DEFAULT_SELECTED_TEXT_COLOR);
        mDividerPaddingForWrap = typedArray.getDimension(R.styleable.WheelView_wv_dividerPaddingForWrap, DEFAULT_TEXT_BOUNDARY_MARGIN);

        mDividerOffset = typedArray.getDimensionPixelOffset(R.styleable.WheelView_wv_dividerOffsetY, 0);

        hasCurtain = typedArray.getBoolean(R.styleable.WheelView_wv_hasCurtain, false);
        mCurtainColor = typedArray.getColor(R.styleable.WheelView_wv_curtainColor, Color.TRANSPARENT);

        isCurved = typedArray.getBoolean(R.styleable.WheelView_wv_curved, true);
        mCurvedArcDirection = typedArray.getInt(R.styleable.WheelView_wv_curvedArcDirection, CURVED_ARC_DIRECTION_CENTER);
        mCurvedArcDirectionFactor = typedArray.getFloat(R.styleable.WheelView_wv_curvedArcDirectionFactor, DEFAULT_CURVED_FACTOR);
        //折射偏移默认值
        //Deprecated 将在新版中移除
        float curvedRefractRatio = typedArray.getFloat(R.styleable.WheelView_wv_curvedRefractRatio, 0.9f);
        mRefractRatio = typedArray.getFloat(R.styleable.WheelView_wv_refractRatio, DEFAULT_REFRACT_RATIO);
        mRefractRatio = isCurved ? Math.min(curvedRefractRatio, mRefractRatio) : mRefractRatio;
        if (mRefractRatio > 1f) {
            mRefractRatio = 1.0f;
        } else if (mRefractRatio < 0f) {
            mRefractRatio = DEFAULT_REFRACT_RATIO;
        }
        typedArray.recycle();
    }

    /**
     * 初始化并设置默认值
     *
     * @param context 上下文
     */
    private void initValue(Context context) {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mMaxFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        mMinFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        mScroller = new Scroller(context);
        mDrawRect = new Rect();
        mCamera = new Camera();
        mMatrix = new Matrix();
        calculateTextSize();
        updateTextAlign();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mSoundHelper != null) {
            mSoundHelper.release();
        }
    }

    private void initSoundHelper() {
        if (mSoundHelper != null) {
            return;
        }
        mSoundHelper = SoundHelper.obtain();
        initDefaultVolume(getContext());
    }

    /**
     * 初始化默认音量
     *
     * @param context 上下文
     */
    private void initDefaultVolume(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            //获取系统媒体当前音量
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            //获取系统媒体最大音量
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            //设置播放音量
            mSoundHelper.setPlayVolume(currentVolume * 1.0f / maxVolume);
        } else {
            mSoundHelper.setPlayVolume(0.3f);
        }
    }

    /**
     * 测量文字最大所占空间
     */
    private void calculateTextSize() {
        mPaint.setTextSize(mTextSize);
        for (int i = 0; i < mDataDelegate.getItemCount(); i++) {
            int textWidth = (int) mPaint.measureText(mDataDelegate.getItemText(mDataDelegate.getItem(i)));
            mMaxTextWidth = Math.max(textWidth, mMaxTextWidth);
        }

        mFontMetrics = mPaint.getFontMetrics();
        //itemHeight实际等于字体高度+一个行间距
        mItemHeight = (int) (mFontMetrics.bottom - mFontMetrics.top + mLineSpacing);
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
            case TEXT_ALIGN_CENTER:
            default:
                mPaint.setTextAlign(Paint.Align.CENTER);
                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
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
        setMeasuredDimension(resolveSizeAndState(width, widthMeasureSpec, 0),
                resolveSizeAndState(height, heightMeasureSpec, 0));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //设置内容可绘制区域
        mDrawRect.set(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        mCenterX = mDrawRect.centerX();
        mCenterY = mDrawRect.centerY();
        mSelectedItemTopLimit = (int) (mCenterY - mItemHeight / 2 - mDividerOffset);
        mSelectedItemBottomLimit = (int) (mCenterY + mItemHeight / 2 + mDividerOffset);
        mClipLeft = getPaddingLeft();
        mClipTop = getPaddingTop();
        mClipRight = getWidth() - getPaddingRight();
        mClipBottom = getHeight() - getPaddingBottom();

        calculateDrawStart();
        //计算滚动限制
        calculateLimitY();

        //如果初始化时有选中的下标，则计算选中位置的距离
        int itemDistance = calculateItemDistance(mSelectedItemPosition);
        if (itemDistance > 0) {
            doScroll(itemDistance);
        }
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
            case TEXT_ALIGN_CENTER:
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
        mMaxScrollY = isCyclic ? Integer.MAX_VALUE : (mDataDelegate.getItemCount() - 1) * mItemHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制选中区域
        drawCurtainRect(canvas);
        //绘制分割线
        drawDivider(canvas);

        //滚动了多少个item，滚动的Y值高度除去每行Item的高度
        int scrolledItem = mScrollOffsetY / dividedItemHeight();
        //没有滚动完一个item时的偏移值，平滑滑动
        int scrolledOffset = mScrollOffsetY % dividedItemHeight();
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

    }

    /**
     * 绘制选中区域
     *
     * @param canvas 画布
     */
    private void drawCurtainRect(Canvas canvas) {
        if (hasCurtain) {
            mPaint.setColor(mCurtainColor);
            canvas.drawRect(mClipLeft, mSelectedItemTopLimit, mClipRight, mSelectedItemBottomLimit, mPaint);
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
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeWidth(mDividerSize);
            if (mDividerType == DIVIDER_TYPE_FILL) {
                canvas.drawLine(mClipLeft, mSelectedItemTopLimit, mClipRight, mSelectedItemTopLimit, mPaint);
                canvas.drawLine(mClipLeft, mSelectedItemBottomLimit, mClipRight, mSelectedItemBottomLimit, mPaint);
            } else {
                //边界处理 超过边界直接按照DIVIDER_TYPE_FILL类型处理
                int startX = (int) (mCenterX - mMaxTextWidth / 2 - mDividerPaddingForWrap);
                int stopX = (int) (mCenterX + mMaxTextWidth / 2 + mDividerPaddingForWrap);

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
        String text = mDataDelegate.getItemTextByIndex(index);
        if (text == null) {
            return;
        }

        int scrolledItem = mScrollOffsetY / dividedItemHeight();
        //index 的 item 距离中间项的偏移
        int item2CenterOffsetY = (index - scrolledItem) * mItemHeight - scrolledOffset;
        //记录初始测量的字体起始X
        int startX = mStartX;
        //重新测量字体宽度和基线偏移
        int centerToBaselineY = isAutoFitTextSize ? remeasureTextSize(text) : mCenterToBaselineY;

        if (Math.abs(item2CenterOffsetY) <= 0) {
            //绘制选中的条目
            mPaint.setColor(mSelectedItemTextColor);
            clipAndDraw2DText(canvas, text, mSelectedItemTopLimit, mSelectedItemBottomLimit, item2CenterOffsetY, centerToBaselineY);
        } else if (item2CenterOffsetY > 0 && item2CenterOffsetY < mItemHeight) {
            //绘制与下边界交汇的条目
            mPaint.setColor(mSelectedItemTextColor);
            clipAndDraw2DText(canvas, text, mSelectedItemTopLimit, mSelectedItemBottomLimit, item2CenterOffsetY, centerToBaselineY);

            mPaint.setColor(mTextColor);
            //缩小字体，实现折射效果
            float textSize = mPaint.getTextSize();
            mPaint.setTextSize(textSize * mRefractRatio);
            //mIsBoldForSelectedItem==true 改变字体
            changeTypefaceIfBoldForSelectedItem();
            clipAndDraw2DText(canvas, text, mSelectedItemBottomLimit, mClipBottom, item2CenterOffsetY, centerToBaselineY);
            mPaint.setTextSize(textSize);
            //mIsBoldForSelectedItem==true 恢复字体
            resetTypefaceIfBoldForSelectedItem();
        } else if (item2CenterOffsetY < 0 && item2CenterOffsetY > -mItemHeight) {
            //绘制与上边界交汇的条目
            mPaint.setColor(mSelectedItemTextColor);
            clipAndDraw2DText(canvas, text, mSelectedItemTopLimit, mSelectedItemBottomLimit, item2CenterOffsetY, centerToBaselineY);

            mPaint.setColor(mTextColor);
            //缩小字体，实现折射效果
            float textSize = mPaint.getTextSize();
            mPaint.setTextSize(textSize * mRefractRatio);
            //mIsBoldForSelectedItem==true 改变字体
            changeTypefaceIfBoldForSelectedItem();
            clipAndDraw2DText(canvas, text, mClipTop, mSelectedItemTopLimit, item2CenterOffsetY, centerToBaselineY);
            mPaint.setTextSize(textSize);
            //mIsBoldForSelectedItem==true 恢复字体
            resetTypefaceIfBoldForSelectedItem();
        } else {
            //绘制其他条目
            mPaint.setColor(mTextColor);
            //缩小字体，实现折射效果
            float textSize = mPaint.getTextSize();
            mPaint.setTextSize(textSize * mRefractRatio);
            //mIsBoldForSelectedItem==true 改变字体
            changeTypefaceIfBoldForSelectedItem();
            clipAndDraw2DText(canvas, text, mClipTop, mClipBottom, item2CenterOffsetY, centerToBaselineY);
            mPaint.setTextSize(textSize);
            //mIsBoldForSelectedItem==true 恢复字体
            resetTypefaceIfBoldForSelectedItem();
        }

        if (isAutoFitTextSize) {
            //恢复重新测量之前的样式
            mPaint.setTextSize(mTextSize);
            mStartX = startX;
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
     * @param centerToBaselineY  文字中心距离baseline的距离
     */
    private void clipAndDraw2DText(Canvas canvas, String text, int clipTop, int clipBottom,
                                   int item2CenterOffsetY, int centerToBaselineY) {
        canvas.save();
        canvas.clipRect(mClipLeft, clipTop, mClipRight, clipBottom);
        canvas.drawText(text, 0, text.length(), mStartX, mCenterY + item2CenterOffsetY - centerToBaselineY, mPaint);
        canvas.restore();
    }

    /**
     * 重新测量字体大小
     *
     * @param contentText 被测量文字内容
     * @return 文字中心距离baseline的距离
     */
    private int remeasureTextSize(String contentText) {
        float textWidth = mPaint.measureText(contentText);
        float drawWidth = getWidth();
        float textMargin = mTextBoundaryMargin * 2;
        //稍微增加了一点文字边距 最大为宽度的1/10
        if (textMargin > (drawWidth / 10f)) {
            drawWidth = drawWidth * 9f / 10f;
            textMargin = drawWidth / 10f;
        } else {
            drawWidth = drawWidth - textMargin;
        }
        if (drawWidth <= 0) {
            return mCenterToBaselineY;
        }
        float textSize = mTextSize;
        while (textWidth > drawWidth) {
            textSize--;
            if (textSize <= 0) {
                break;
            }
            mPaint.setTextSize(textSize);
            textWidth = mPaint.measureText(contentText);
        }
        //重新计算文字起始X
        recalculateStartX(textMargin / 2.0f);
        //高度起点也变了
        return recalculateCenterToBaselineY();
    }

    /**
     * 重新计算字体起始X
     *
     * @param textMargin 文字外边距
     */
    private void recalculateStartX(float textMargin) {
        switch (mTextAlign) {
            case TEXT_ALIGN_LEFT:
                mStartX = (int) textMargin;
                break;
            case TEXT_ALIGN_RIGHT:
                mStartX = (int) (getWidth() - textMargin);
                break;
            case TEXT_ALIGN_CENTER:
            default:
                mStartX = getWidth() / 2;
                break;
        }
    }

    /**
     * 字体大小变化后重新计算距离基线的距离
     *
     * @return 文字中心距离baseline的距离
     */
    private int recalculateCenterToBaselineY() {
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        //高度起点也变了
        return (int) (fontMetrics.ascent + (fontMetrics.descent - fontMetrics.ascent) / 2);
    }

    /**
     * 绘制弯曲（3D）效果的item
     *
     * @param canvas         画布
     * @param index          下标
     * @param scrolledOffset 滚动偏移
     */
    private void draw3DItem(Canvas canvas, int index, int scrolledOffset) {
        String text = mDataDelegate.getItemTextByIndex(index);
        if (text == null) {
            return;
        }
        // 滚轮的半径
        final int radius = (getHeight() - getPaddingTop() - getPaddingBottom()) / 2;
        int scrolledItem = mScrollOffsetY / dividedItemHeight();
        //index 的 item 距离中间项的偏移
        int item2CenterOffsetY = (index - scrolledItem) * mItemHeight - scrolledOffset;

        // 当滑动的角度和y轴垂直时（此时文字已经显示为一条线），不绘制文字
        if (Math.abs(item2CenterOffsetY) > radius * Math.PI / 2) return;

        final double angle = (double) item2CenterOffsetY / radius;
        // 绕x轴滚动的角度
        float rotateX = (float) Math.toDegrees(-angle);
        // 滚动的距离映射到y轴的长度
        float translateY = (float) (Math.sin(angle) * radius);
        // 滚动的距离映射到z轴的长度
        float translateZ = (float) ((1 - Math.cos(angle)) * radius);
        // 透明度
        int alpha = (int) (Math.cos(angle) * 255);

        //记录初始测量的字体起始X
        int startX = mStartX;
        //重新测量字体宽度和基线偏移
        int centerToBaselineY = isAutoFitTextSize ? remeasureTextSize(text) : mCenterToBaselineY;
        if (Math.abs(item2CenterOffsetY) <= 0) {
            //绘制选中的条目
            mPaint.setColor(mSelectedItemTextColor);
            mPaint.setAlpha(255);
            clipAndDraw3DText(canvas, text, mSelectedItemTopLimit, mSelectedItemBottomLimit,
                    rotateX, translateY, translateZ, centerToBaselineY);
        } else if (item2CenterOffsetY > 0 && item2CenterOffsetY < mItemHeight) {
            //绘制与下边界交汇的条目
            mPaint.setColor(mSelectedItemTextColor);
            mPaint.setAlpha(255);
            clipAndDraw3DText(canvas, text, mSelectedItemTopLimit, mSelectedItemBottomLimit,
                    rotateX, translateY, translateZ, centerToBaselineY);

            mPaint.setColor(mTextColor);
            mPaint.setAlpha(alpha);
            //缩小字体，实现折射效果
            float textSize = mPaint.getTextSize();
            mPaint.setTextSize(textSize * mRefractRatio);
            //mIsBoldForSelectedItem==true 改变字体
            changeTypefaceIfBoldForSelectedItem();
            //字体变化，重新计算距离基线偏移
            int reCenterToBaselineY = recalculateCenterToBaselineY();
            clipAndDraw3DText(canvas, text, mSelectedItemBottomLimit, mClipBottom,
                    rotateX, translateY, translateZ, reCenterToBaselineY);
            mPaint.setTextSize(textSize);
            //mIsBoldForSelectedItem==true 恢复字体
            resetTypefaceIfBoldForSelectedItem();
        } else if (item2CenterOffsetY < 0 && item2CenterOffsetY > -mItemHeight) {
            //绘制与上边界交汇的条目
            mPaint.setColor(mSelectedItemTextColor);
            mPaint.setAlpha(255);
            clipAndDraw3DText(canvas, text, mSelectedItemTopLimit, mSelectedItemBottomLimit,
                    rotateX, translateY, translateZ, centerToBaselineY);

            mPaint.setColor(mTextColor);
            mPaint.setAlpha(alpha);

            //缩小字体，实现折射效果
            float textSize = mPaint.getTextSize();
            mPaint.setTextSize(textSize * mRefractRatio);
            //mIsBoldForSelectedItem==true 改变字体
            changeTypefaceIfBoldForSelectedItem();
            //字体变化，重新计算距离基线偏移
            int reCenterToBaselineY = recalculateCenterToBaselineY();
            clipAndDraw3DText(canvas, text, mClipTop, mSelectedItemTopLimit,
                    rotateX, translateY, translateZ, reCenterToBaselineY);
            mPaint.setTextSize(textSize);
            //mIsBoldForSelectedItem==true 恢复字体
            resetTypefaceIfBoldForSelectedItem();
        } else {
            //绘制其他条目
            mPaint.setColor(mTextColor);
            mPaint.setAlpha(alpha);

            //缩小字体，实现折射效果
            float textSize = mPaint.getTextSize();
            mPaint.setTextSize(textSize * mRefractRatio);
            //mIsBoldForSelectedItem==true 改变字体
            changeTypefaceIfBoldForSelectedItem();
            //字体变化，重新计算距离基线偏移
            int reCenterToBaselineY = recalculateCenterToBaselineY();
            clipAndDraw3DText(canvas, text, mClipTop, mClipBottom,
                    rotateX, translateY, translateZ, reCenterToBaselineY);
            mPaint.setTextSize(textSize);
            //mIsBoldForSelectedItem==true 恢复字体
            resetTypefaceIfBoldForSelectedItem();
        }

        if (isAutoFitTextSize) {
            //恢复重新测量之前的样式
            mPaint.setTextSize(mTextSize);
            mStartX = startX;
        }
    }

    /**
     * 裁剪并绘制弯曲（3D）效果
     *
     * @param canvas            画布
     * @param text              绘制的文字
     * @param clipTop           裁剪的上边界
     * @param clipBottom        裁剪的下边界
     * @param rotateX           绕X轴旋转角度
     * @param offsetY           Y轴偏移
     * @param offsetZ           Z轴偏移
     * @param centerToBaselineY 文字中心距离baseline的距离
     */
    private void clipAndDraw3DText(Canvas canvas, String text, int clipTop, int clipBottom,
                                   float rotateX, float offsetY, float offsetZ, int centerToBaselineY) {

        canvas.save();
        canvas.clipRect(mClipLeft, clipTop, mClipRight, clipBottom);
        draw3DText(canvas, text, rotateX, offsetY, offsetZ, centerToBaselineY);
        canvas.restore();
    }

    /**
     * 绘制弯曲（3D）的文字
     *
     * @param canvas            画布
     * @param text              绘制的文字
     * @param rotateX           绕X轴旋转角度
     * @param offsetY           Y轴偏移
     * @param offsetZ           Z轴偏移
     * @param centerToBaselineY 文字中心距离baseline的距离
     */
    private void draw3DText(Canvas canvas, String text, float rotateX, float offsetY,
                            float offsetZ, int centerToBaselineY) {
        mCamera.save();
        mCamera.translate(0, 0, offsetZ);
        mCamera.rotateX(rotateX);
        mCamera.getMatrix(mMatrix);
        mCamera.restore();

        // 调节中心点
        float centerX = mCenterX;
        //根据弯曲（3d）对齐方式设置系数
        if (mCurvedArcDirection == CURVED_ARC_DIRECTION_LEFT) {
            centerX = mCenterX * (1 + mCurvedArcDirectionFactor);
        } else if (mCurvedArcDirection == CURVED_ARC_DIRECTION_RIGHT) {
            centerX = mCenterX * (1 - mCurvedArcDirectionFactor);
        }

        float centerY = mCenterY + offsetY;
        mMatrix.preTranslate(-centerX, -centerY);
        mMatrix.postTranslate(centerX, centerY);

        canvas.concat(mMatrix);
        canvas.drawText(text, 0, text.length(), mStartX, centerY - centerToBaselineY, mPaint);

    }

    private void changeTypefaceIfBoldForSelectedItem() {
        if (mIsBoldForSelectedItem) {
            mPaint.setTypeface(mNormalTypeface);
        }
    }

    private void resetTypefaceIfBoldForSelectedItem() {
        if (mIsBoldForSelectedItem) {
            mPaint.setTypeface(mBoldTypeface);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //屏蔽如果未设置数据时，触摸导致运算数据不正确的崩溃 issue #20
        if (!isEnabled() || mDataDelegate.getItemCount() == 0) {
            return super.onTouchEvent(event);
        }
        initVelocityTracker();
        mVelocityTracker.addMovement(event);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                //手指按下
                //处理滑动事件嵌套 拦截事件序列
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                //如果未滚动完成，强制滚动完成
                if (!mScroller.isFinished()) {
                    //强制滚动完成
                    mScroller.forceFinished(true);
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
                onWheelScrollStateChanged(SCROLL_STATE_DRAGGING);
                if (Math.abs(deltaY) < 1) {
                    break;
                }
                //deltaY 上滑为正，下滑为负
                doScroll((int) -deltaY);
                mLastTouchY = moveY;
                invalidateIfYChanged();

                break;
            case MotionEvent.ACTION_UP:
                //手指抬起
                isForceFinishScroll = false;
                mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);
                float velocityY = mVelocityTracker.getYVelocity();
                if (Math.abs(velocityY) > mMinFlingVelocity) {
                    //快速滑动
                    mScroller.forceFinished(true);
                    isFlingScroll = true;
                    mScroller.fling(0, mScrollOffsetY, 0, (int) -velocityY, 0, 0,
                            mMinScrollY, mMaxScrollY);
                } else {
                    int clickToCenterDistance = 0;
                    if (System.currentTimeMillis() - mDownStartTime <= DEFAULT_CLICK_CONFIRM) {
                        //处理点击滚动
                        //手指抬起的位置到中心的距离为滚动差值
                        clickToCenterDistance = (int) (event.getY() - mCenterY);
                    }
                    int scrollRange = clickToCenterDistance +
                            calculateDistanceToEndPoint((mScrollOffsetY + clickToCenterDistance) % dividedItemHeight());
                    //大于最小值滚动值
                    boolean isInMinRange = scrollRange < 0 && mScrollOffsetY + scrollRange >= mMinScrollY;
                    //小于最大滚动值
                    boolean isInMaxRange = scrollRange > 0 && mScrollOffsetY + scrollRange <= mMaxScrollY;
                    if (isInMinRange || isInMaxRange) {
                        //在滚动范围之内再修正位置
                        //平稳滑动
                        mScroller.startScroll(0, mScrollOffsetY, 0, scrollRange);
                    }
                }

                invalidateIfYChanged();
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
     * 初始化 VelocityTracker
     */
    private void initVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
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
     * 当Y轴的偏移值改变时再重绘，减少重回次数
     */
    private void invalidateIfYChanged() {
        if (mScrollOffsetY != mScrolledY) {
            mScrolledY = mScrollOffsetY;
            //滚动偏移发生变化
            if (mOnWheelChangedListener != null) {
                mOnWheelChangedListener.onWheelScroll(mScrollOffsetY);
            }
            onWheelScroll(mScrollOffsetY);
            //观察item变化
            observeItemChanged();
            invalidate();
        }
    }

    /**
     * 观察item改变
     */
    private void observeItemChanged() {
        //item改变回调
        int oldPosition = mCurrentScrollPosition;
        int newPosition = getCurrentPosition();
        if (oldPosition != newPosition) {
            //改变了
            if (mOnWheelChangedListener != null) {
                mOnWheelChangedListener.onWheelItemChanged(oldPosition, newPosition);
            }
            onWheelItemChanged(oldPosition, newPosition);
            //播放音频
            playSoundEffect();
            //更新下标
            mCurrentScrollPosition = newPosition;
        }
    }

    /**
     * 播放滚动音效
     */
    public void playSoundEffect() {
        if (mSoundHelper != null && isSoundEffect) {
            mSoundHelper.playSoundEffect();
        }
    }

    /**
     * 强制滚动完成，直接停止
     */
    public void forceFinishScroll() {
        if (!mScroller.isFinished()) {
            mScroller.forceFinished(true);
        }
    }

    /**
     * 强制滚动完成，并且直接滚动到最终位置
     */
    public void abortFinishScroll() {
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
    }

    /**
     * 计算距离终点的偏移，修正选中条目
     *
     * @param remainder 余数
     * @return 偏移量
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
        if (mScroller.isFinished() && !isForceFinishScroll && !isFlingScroll) {
            if (mItemHeight == 0) return;
            //滚动状态停止
            if (mOnWheelChangedListener != null) {
                mOnWheelChangedListener.onWheelScrollStateChanged(SCROLL_STATE_IDLE);
            }
            onWheelScrollStateChanged(SCROLL_STATE_IDLE);
            int currentItemPosition = getCurrentPosition();
            //当前选中的Position没变时不回调 onItemSelected()
            if (currentItemPosition == mSelectedItemPosition) {
                return;
            }
            mSelectedItemPosition = currentItemPosition;
            //停止后重新赋值
            mCurrentScrollPosition = mSelectedItemPosition;

            //停止滚动，选中条目回调
            if (mOnItemSelectedListener != null) {
                mOnItemSelectedListener.onItemSelected(this, mDataDelegate.getItem(mSelectedItemPosition), mSelectedItemPosition);
            }
            onItemSelected(mDataDelegate.getItem(mSelectedItemPosition), mSelectedItemPosition);
            //滚动状态回调
            if (mOnWheelChangedListener != null) {
                mOnWheelChangedListener.onWheelSelected(mSelectedItemPosition);
            }
            onWheelSelected(mSelectedItemPosition);
        }

        if (mScroller.computeScrollOffset()) {
            int oldY = mScrollOffsetY;
            mScrollOffsetY = mScroller.getCurrY();

            if (oldY != mScrollOffsetY) {
                if (mOnWheelChangedListener != null) {
                    mOnWheelChangedListener.onWheelScrollStateChanged(SCROLL_STATE_SCROLLING);
                }
                onWheelScrollStateChanged(SCROLL_STATE_SCROLLING);
            }
            invalidateIfYChanged();
            ViewCompat.postOnAnimation(this, this);
        } else if (isFlingScroll) {
            //滚动完成后，根据是否为快速滚动处理是否需要调整最终位置
            isFlingScroll = false;
            //快速滚动后需要调整滚动完成后的最终位置，重新启动scroll滑动到中心位置
            mScroller.startScroll(0, mScrollOffsetY, 0, calculateDistanceToEndPoint(mScrollOffsetY % dividedItemHeight()));
            invalidateIfYChanged();
            ViewCompat.postOnAnimation(this, this);
        }
    }

    /**
     * 根据偏移计算当前位置下标
     *
     * @return 偏移量对应的当前下标 if dataList is empty return -1
     */
    private int getCurrentPosition() {
        if (mDataDelegate.getItemCount() == 0) {
            return -1;
        }
        int itemPosition;
        if (mScrollOffsetY < 0) {
            itemPosition = (mScrollOffsetY - mItemHeight / 2) / dividedItemHeight();
        } else {
            itemPosition = (mScrollOffsetY + mItemHeight / 2) / dividedItemHeight();
        }
        int currentPosition = itemPosition % mDataDelegate.getItemCount();
        if (currentPosition < 0) {
            currentPosition += mDataDelegate.getItemCount();
        }

        return currentPosition;
    }

    /**
     * mItemHeight 为被除数时避免为0
     *
     * @return 被除数不为0
     */
    private int dividedItemHeight() {
        return mItemHeight > 0 ? mItemHeight : 1;
    }

    /**
     * 获取音效开关状态
     *
     * @return 是否开启滚动音效
     */
    public boolean isSoundEffect() {
        return isSoundEffect;
    }

    /**
     * 设置音效开关
     *
     * @param isSoundEffect 是否开启滚动音效
     */
    public void setSoundEffect(boolean isSoundEffect) {
        this.isSoundEffect = isSoundEffect;
    }

    /**
     * 设置声音效果资源
     *
     * @param rawResId 声音效果资源 越小效果越好 {@link RawRes}
     */
    public void setSoundEffectResource(@RawRes int rawResId) {
        initSoundHelper();
        if (mSoundHelper != null) {
            mSoundHelper.load(getContext(), rawResId);
        }
    }

    /**
     * 获取播放音量
     *
     * @return 播放音量 range 0.0-1.0
     */
    public float getPlayVolume() {
        return mSoundHelper == null ? 0 : mSoundHelper.getPlayVolume();
    }

    /**
     * 设置播放音量
     *
     * @param playVolume 播放音量 range 0.0-1.0
     */
    public void setPlayVolume(@FloatRange(from = 0.0, to = 1.0) float playVolume) {
        initSoundHelper();
        if (mSoundHelper != null) {
            mSoundHelper.setPlayVolume(playVolume);
        }
    }

    /**
     * 获取指定 position 的数据
     *
     * @param position 下标
     * @return position 对应的数据 {@link Nullable}
     */
    @Nullable
    public T getItemData(int position) {
        if (isPositionInRange(position)) {
            return mDataDelegate.getItem(position);
        } else if (mDataDelegate.getItemCount() > 0 && position >= mDataDelegate.getItemCount()) {
            return mDataDelegate.getItem(mDataDelegate.getItemCount() - 1);
        } else if (mDataDelegate.getItemCount() > 0 && position < 0) {
            return mDataDelegate.getItem(0);
        }
        return null;
    }

    /**
     * 获取当前选中的item数据
     *
     * @return 当前选中的item数据
     */
    public T getSelectedItemData() {
        return getItemData(mSelectedItemPosition);
    }

    public List<T> getData() {
        return mDataDelegate.getData();
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
        mDataDelegate.setData(dataList);
        if (!isResetSelectedPosition && mDataDelegate.getItemCount() > 0) {
            //不重置选中下标
            if (mSelectedItemPosition >= mDataDelegate.getItemCount()) {
                mSelectedItemPosition = mDataDelegate.getItemCount() - 1;
                //重置滚动下标
                mCurrentScrollPosition = mSelectedItemPosition;
            }
        } else {
            //重置选中下标和滚动下标
            mCurrentScrollPosition = mSelectedItemPosition = 0;
        }
        //强制滚动完成
        forceFinishScroll();
        calculateTextSize();
        calculateLimitY();
        //重置滚动偏移
        mScrollOffsetY = mSelectedItemPosition * mItemHeight;
        requestLayout();
    }

    /**
     * 当数据变化时，是否重置选中下标到第一个
     *
     * @return 是否重置选中下标到第一个
     */
    public boolean isResetSelectedPosition() {
        return isResetSelectedPosition;
    }

    /**
     * 设置当数据变化时，是否重置选中下标到第一个
     *
     * @param isResetSelectedPosition 当数据变化时,是否重置选中下标到第一个
     */
    public void setResetSelectedPosition(boolean isResetSelectedPosition) {
        this.isResetSelectedPosition = isResetSelectedPosition;
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
     * @param isSp     单位是否是 sp
     */
    public void setTextSize(float textSize, boolean isSp) {
        float tempTextSize = mTextSize;
        mTextSize = isSp ? sp2px(textSize) : textSize;
        if (tempTextSize == mTextSize) {
            return;
        }
        //强制滚动完成
        forceFinishScroll();
        calculateTextSize();
        calculateDrawStart();
        calculateLimitY();
        //字体大小变化，偏移距离也变化了
        mScrollOffsetY = mSelectedItemPosition * mItemHeight;
        requestLayout();
    }

    /**
     * 获取是否自动调整字体大小，以显示完全
     *
     * @return 是否自动调整字体大小
     */
    public boolean isAutoFitTextSize() {
        return isAutoFitTextSize;
    }

    /**
     * 设置是否自动调整字体大小，以显示完全
     *
     * @param isAutoFitTextSize 是否自动调整字体大小
     */
    public void setAutoFitTextSize(boolean isAutoFitTextSize) {
        this.isAutoFitTextSize = isAutoFitTextSize;
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
        setTypeface(typeface, false);
    }

    /**
     * 设置当前字体
     *
     * @param typeface              字体
     * @param isBoldForSelectedItem 是否设置选中条目字体加粗，其他条目不会加粗
     */
    public void setTypeface(Typeface typeface, boolean isBoldForSelectedItem) {
        if (typeface == null || mPaint.getTypeface() == typeface) {
            return;
        }
        //强制滚动完成
        forceFinishScroll();
        mIsBoldForSelectedItem = isBoldForSelectedItem;
        if (mIsBoldForSelectedItem) {
            //如果设置了选中条目字体加粗，其他条目不会加粗，则拆分两份字体
            if (typeface.isBold()) {
                mNormalTypeface = Typeface.create(typeface, Typeface.NORMAL);
                mBoldTypeface = typeface;
            } else {
                mNormalTypeface = typeface;
                mBoldTypeface = Typeface.create(typeface, Typeface.BOLD);
            }
            //测量时 使用加粗字体测量，因为加粗字体比普通字体宽，以大的为准进行测量
            mPaint.setTypeface(mBoldTypeface);
        } else {
            mPaint.setTypeface(typeface);
        }
        calculateTextSize();
        calculateDrawStart();
        //字体大小变化，偏移距离也变化了
        mScrollOffsetY = mSelectedItemPosition * mItemHeight;
        calculateLimitY();
        requestLayout();
    }

    /**
     * 获取文字对齐方式
     *
     * @return 文字对齐
     * {@link #TEXT_ALIGN_LEFT}
     * {@link #TEXT_ALIGN_CENTER}
     * {@link #TEXT_ALIGN_RIGHT}
     */
    public int getTextAlign() {
        return mTextAlign;
    }

    /**
     * 设置文字对齐方式
     *
     * @param textAlign 文字对齐方式
     *                  {@link #TEXT_ALIGN_LEFT}
     *                  {@link #TEXT_ALIGN_CENTER}
     *                  {@link #TEXT_ALIGN_RIGHT}
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
     * @return 未选中条目颜色 ColorInt
     */
    public int getNormalItemTextColor() {
        return mTextColor;
    }

    /**
     * 设置未选中条目颜色
     *
     * @param textColorRes 未选中条目颜色 {@link ColorRes}
     */
    public void setNormalItemTextColorRes(@ColorRes int textColorRes) {
        setNormalItemTextColor(ContextCompat.getColor(getContext(), textColorRes));
    }

    /**
     * 设置未选中条目颜色
     *
     * @param textColor 未选中条目颜色 {@link ColorInt}
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
     * @return 选中条目颜色 ColorInt
     */
    public int getSelectedItemTextColor() {
        return mSelectedItemTextColor;
    }

    /**
     * 设置选中条目颜色
     *
     * @param selectedItemColorRes 选中条目颜色 {@link ColorRes}
     */
    public void setSelectedItemTextColorRes(@ColorRes int selectedItemColorRes) {
        setSelectedItemTextColor(ContextCompat.getColor(getContext(), selectedItemColorRes));
    }

    /**
     * 设置选中条目颜色
     *
     * @param selectedItemTextColor 选中条目颜色 {@link ColorInt}
     */
    public void setSelectedItemTextColor(@ColorInt int selectedItemTextColor) {
        if (mSelectedItemTextColor == selectedItemTextColor) {
            return;
        }
        mSelectedItemTextColor = selectedItemTextColor;
        invalidate();
    }

    /**
     * 获取文字距离边界的外边距
     *
     * @return 外边距值
     */
    public float getTextBoundaryMargin() {
        return mTextBoundaryMargin;
    }

    /**
     * 设置文字距离边界的外边距
     *
     * @param textBoundaryMargin 外边距值
     */
    public void setTextBoundaryMargin(float textBoundaryMargin) {
        setTextBoundaryMargin(textBoundaryMargin, false);
    }

    /**
     * 设置文字距离边界的外边距
     *
     * @param textBoundaryMargin 外边距值
     * @param isDp               单位是否为 dp
     */
    public void setTextBoundaryMargin(float textBoundaryMargin, boolean isDp) {
        float tempTextBoundaryMargin = mTextBoundaryMargin;
        mTextBoundaryMargin = isDp ? dp2px(textBoundaryMargin) : textBoundaryMargin;
        if (tempTextBoundaryMargin == mTextBoundaryMargin) {
            return;
        }
        requestLayout();
    }

    /**
     * 获取item间距
     *
     * @return 行间距值
     */
    public float getLineSpacing() {
        return mLineSpacing;
    }

    /**
     * 设置item间距
     *
     * @param lineSpacing 行间距值
     */
    public void setLineSpacing(float lineSpacing) {
        setLineSpacing(lineSpacing, false);
    }

    /**
     * 设置item间距
     *
     * @param lineSpacing 行间距值
     * @param isDp        lineSpacing 单位是否为 dp
     */
    public void setLineSpacing(float lineSpacing, boolean isDp) {
        float tempLineSpace = mLineSpacing;
        mLineSpacing = isDp ? dp2px(lineSpacing) : lineSpacing;
        if (tempLineSpace == mLineSpacing) {
            return;
        }
        mScrollOffsetY = 0;
        calculateTextSize();
        requestLayout();
    }

    /**
     * 设置格式转换器
     *
     * @param itemTextFormatter wheelView item 显示文字的转换器
     */
    public void setItemTextFormatter(ItemTextFormatter itemTextFormatter) {
        mDataDelegate.setItemTextFormatter(itemTextFormatter);
        calculateTextSize();
        requestLayout();
    }

    /**
     * 获取可见条目数
     *
     * @return 可见条目数
     */
    public int getVisibleItems() {
        return mVisibleItems;
    }

    /**
     * 设置可见的条目数
     *
     * @param visibleItems 可见条目数
     */
    public void setVisibleItems(int visibleItems) {
        if (mVisibleItems == visibleItems) {
            return;
        }
        mVisibleItems = adjustVisibleItems(visibleItems);
        mScrollOffsetY = 0;
        requestLayout();
    }

    /**
     * 跳转可见条目数为奇数
     *
     * @param visibleItems 可见条目数
     * @return 调整后的可见条目数
     */
    private int adjustVisibleItems(int visibleItems) {
        return Math.abs(visibleItems / 2 * 2 + 1); // 当传入的值为偶数时,换算成奇数;
    }

    /**
     * 是否是循环滚动
     *
     * @return 是否是循环滚动
     */
    public boolean isCyclic() {
        return isCyclic;
    }

    /**
     * 设置是否循环滚动
     *
     * @param isCyclic 是否是循环滚动
     */
    public void setCyclic(boolean isCyclic) {
        if (this.isCyclic == isCyclic) {
            return;
        }
        this.isCyclic = isCyclic;
        mDataDelegate.setCyclic(isCyclic);

        forceFinishScroll();
        calculateLimitY();
        //设置当前选中的偏移值
        mScrollOffsetY = mSelectedItemPosition * mItemHeight;
        invalidate();
    }

    /**
     * 获取当前选中下标
     *
     * @return 当前选中的下标
     */
    public int getSelectedItemPosition() {
        return mSelectedItemPosition;
    }

    /**
     * 设置当前选中下标
     *
     * @param position 下标
     */
    public void setSelectedItemPosition(int position) {
        setSelectedItemPosition(position, false);
    }

    /**
     * 设置当前选中下标
     *
     * @param position       下标
     * @param isSmoothScroll 是否平滑滚动
     */
    public void setSelectedItemPosition(int position, boolean isSmoothScroll) {
        setSelectedItemPosition(position, isSmoothScroll, 0);
    }

    /**
     * 设置当前选中下标
     * <p>
     * bug 修复记录：调用这个方法时大多数情况在初始化时，如果没有执行 onSizeChanged() 方法时，调用这个方法会导致失效
     * 因为 onSizeChanged() 方法执行结束才确定边界等信息，
     * 所以在 onSizeChanged() 方法增加了兼容，如果 mSelectedItemPosition >0 的情况重新计算一下滚动值。
     *
     * @param position       下标
     * @param isSmoothScroll 是否平滑滚动
     * @param smoothDuration 平滑滚动时间
     */
    public void setSelectedItemPosition(int position, boolean isSmoothScroll, int smoothDuration) {
        if (!isPositionInRange(position)) {
            return;
        }

        //item之间差值
        int itemDistance = calculateItemDistance(position);
        if (itemDistance == 0) {
            return;
        }
        //如果Scroller滑动未停止，强制结束动画
        abortFinishScroll();

        if (isSmoothScroll) {
            //如果是平滑滚动并且之前的Scroll滚动完成
            mScroller.startScroll(0, mScrollOffsetY, 0, itemDistance,
                    smoothDuration > 0 ? smoothDuration : DEFAULT_SCROLL_DURATION);
            invalidateIfYChanged();
            ViewCompat.postOnAnimation(this, this);
        } else {
            doScroll(itemDistance);
            mSelectedItemPosition = position;
            //选中条目回调
            if (mOnItemSelectedListener != null) {
                mOnItemSelectedListener.onItemSelected(this, mDataDelegate.getItem(mSelectedItemPosition), mSelectedItemPosition);
            }
            onItemSelected(mDataDelegate.getItem(mSelectedItemPosition), mSelectedItemPosition);
            if (mOnWheelChangedListener != null) {
                mOnWheelChangedListener.onWheelSelected(mSelectedItemPosition);
            }
            onWheelSelected(mSelectedItemPosition);
            invalidateIfYChanged();
        }

    }

    private int calculateItemDistance(int position) {
        return position * mItemHeight - mScrollOffsetY;
    }

    /**
     * 判断下标是否在数据列表范围内
     *
     * @param position 下标
     * @return 是否在数据列表范围内
     */
    public boolean isPositionInRange(int position) {
        return position >= 0 && position < mDataDelegate.getItemCount();
    }

    /**
     * 获取是否显示分割线
     *
     * @return 是否显示分割线
     */
    public boolean isShowDivider() {
        return isShowDivider;
    }

    /**
     * 设置是否显示分割线
     *
     * @param isShowDivider 是否显示分割线
     */
    public void setShowDivider(boolean isShowDivider) {
        if (this.isShowDivider == isShowDivider) {
            return;
        }
        this.isShowDivider = isShowDivider;
        invalidate();
    }

    /**
     * 获取分割线颜色
     *
     * @return 分割线颜色 ColorInt
     */
    public int getDividerColor() {
        return mDividerColor;
    }

    /**
     * 设置分割线颜色
     *
     * @param dividerColorRes 分割线颜色 {@link ColorRes}
     */
    public void setDividerColorRes(@ColorRes int dividerColorRes) {
        setDividerColor(ContextCompat.getColor(getContext(), dividerColorRes));
    }

    /**
     * 设置分割线颜色
     *
     * @param dividerColor 分割线颜色 {@link ColorInt}
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
     * @return 分割线高度
     */
    public float getDividerHeight() {
        return mDividerSize;
    }

    /**
     * 设置分割线高度
     *
     * @param dividerHeight 分割线高度
     */
    public void setDividerHeight(float dividerHeight) {
        setDividerHeight(dividerHeight, false);
    }

    /**
     * 设置分割线高度
     *
     * @param dividerHeight 分割线高度
     * @param isDp          单位是否是 dp
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
     * @return 分割线填充类型
     * {@link #DIVIDER_TYPE_FILL}
     * {@link #DIVIDER_TYPE_WRAP}
     */
    public int getDividerType() {
        return mDividerType;
    }

    /**
     * 设置分割线填充类型
     *
     * @param dividerType 分割线填充类型
     *                    {@link #DIVIDER_TYPE_FILL}
     *                    {@link #DIVIDER_TYPE_WRAP}
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
     * @return 分割线内边距
     */
    public float getDividerPaddingForWrap() {
        return mDividerPaddingForWrap;
    }

    /**
     * 设置自适应分割线类型时的分割线内边距
     *
     * @param dividerPaddingForWrap 分割线内边距
     */
    public void setDividerPaddingForWrap(float dividerPaddingForWrap) {
        setDividerPaddingForWrap(dividerPaddingForWrap, false);
    }

    /**
     * 设置自适应分割线类型时的分割线内边距
     *
     * @param dividerPaddingForWrap 分割线内边距
     * @param isDp                  单位是否是 dp
     */
    public void setDividerPaddingForWrap(float dividerPaddingForWrap, boolean isDp) {
        float tempDividerPadding = mDividerPaddingForWrap;
        mDividerPaddingForWrap = isDp ? dp2px(dividerPaddingForWrap) : dividerPaddingForWrap;
        if (tempDividerPadding == mDividerPaddingForWrap) {
            return;
        }
        invalidate();
    }

    /**
     * 获取分割线两端形状
     *
     * @return 分割线两端形状
     * {@link Paint.Cap#BUTT}
     * {@link Paint.Cap#ROUND}
     * {@link Paint.Cap#SQUARE}
     */
    public Paint.Cap getDividerCap() {
        return mDividerCap;
    }

    /**
     * 设置分割线两端形状
     *
     * @param dividerCap 分割线两端形状
     *                   {@link Paint.Cap#BUTT}
     *                   {@link Paint.Cap#ROUND}
     *                   {@link Paint.Cap#SQUARE}
     */
    public void setDividerCap(Paint.Cap dividerCap) {
        if (mDividerCap == dividerCap) {
            return;
        }
        mDividerCap = dividerCap;
        invalidate();
    }

    /**
     * 获取是否绘制选中区域
     *
     * @return 是否绘制选中区域
     */
    public boolean hasCurtain() {
        return hasCurtain;
    }

    /**
     * 设置是否绘制选中区域
     *
     * @param hasCurtain 是否绘制选中区域
     */
    public void setHasCurtain(boolean hasCurtain) {
        this.hasCurtain = hasCurtain;
        invalidate();
    }

    /**
     * 获取选中区域颜色
     *
     * @return 选中区域颜色 ColorInt
     */
    public int getCurtainColor() {
        return mCurtainColor;
    }

    /**
     * 设置选中区域颜色
     *
     * @param selectedRectColorRes 选中区域颜色 {@link ColorRes}
     */
    public void setSelectedRectColorRes(@ColorRes int selectedRectColorRes) {
        setCurtainColor(ContextCompat.getColor(getContext(), selectedRectColorRes));
    }

    /**
     * 设置选中区域颜色
     *
     * @param curtainColor 选中区域颜色 {@link ColorInt}
     */
    public void setCurtainColor(@ColorInt int curtainColor) {
        mCurtainColor = curtainColor;
        invalidate();
    }

    /**
     * 获取是否是弯曲（3D）效果
     *
     * @return 是否是弯曲（3D）效果
     */
    public boolean isCurved() {
        return isCurved;
    }

    /**
     * 设置是否是弯曲（3D）效果
     *
     * @param isCurved 是否是弯曲（3D）效果
     */
    public void setCurved(boolean isCurved) {
        if (this.isCurved == isCurved) {
            return;
        }
        this.isCurved = isCurved;
        calculateTextSize();
        requestLayout();
    }

    /**
     * 获取弯曲（3D）效果左右圆弧效果方向
     *
     * @return 左右圆弧效果方向
     * {@link #CURVED_ARC_DIRECTION_LEFT}
     * {@link #CURVED_ARC_DIRECTION_CENTER}
     * {@link #CURVED_ARC_DIRECTION_RIGHT}
     */
    public int getCurvedArcDirection() {
        return mCurvedArcDirection;
    }

    /**
     * 设置弯曲（3D）效果左右圆弧效果方向
     *
     * @param curvedArcDirection 左右圆弧效果方向
     *                           {@link #CURVED_ARC_DIRECTION_LEFT}
     *                           {@link #CURVED_ARC_DIRECTION_CENTER}
     *                           {@link #CURVED_ARC_DIRECTION_RIGHT}
     */
    public void setCurvedArcDirection(@CurvedArcDirection int curvedArcDirection) {
        if (mCurvedArcDirection == curvedArcDirection) {
            return;
        }
        mCurvedArcDirection = curvedArcDirection;
        invalidate();
    }

    /**
     * 获取弯曲（3D）效果左右圆弧偏移效果方向系数
     *
     * @return 左右圆弧偏移效果方向系数
     */
    public float getCurvedArcDirectionFactor() {
        return mCurvedArcDirectionFactor;
    }

    /**
     * 设置弯曲（3D）效果左右圆弧偏移效果方向系数
     *
     * @param curvedArcDirectionFactor 左右圆弧偏移效果方向系数
     *                                 range 0.0-1.0 越大越明显
     */
    public void setCurvedArcDirectionFactor(@FloatRange(from = 0.0f, to = 1.0f) float curvedArcDirectionFactor) {
        if (mCurvedArcDirectionFactor == curvedArcDirectionFactor) {
            return;
        }
        if (curvedArcDirectionFactor < 0) {
            curvedArcDirectionFactor = 0f;
        } else if (curvedArcDirectionFactor > 1) {
            curvedArcDirectionFactor = 1f;
        }
        mCurvedArcDirectionFactor = curvedArcDirectionFactor;
        invalidate();
    }

    /**
     * 获取折射偏移比例
     *
     * @return 折射偏移比例
     */
    public float getRefractRatio() {
        return mRefractRatio;
    }

    /**
     * 设置选中条目折射偏移比例
     *
     * @param refractRatio 折射偏移比例 range 0.0-1.0
     */
    public void setRefractRatio(@FloatRange(from = 0.0f, to = 1.0f) float refractRatio) {
        float tempRefractRatio = mRefractRatio;
        mRefractRatio = refractRatio;
        if (mRefractRatio > 1f) {
            mRefractRatio = 1.0f;
        } else if (mRefractRatio < 0f) {
            mRefractRatio = DEFAULT_REFRACT_RATIO;
        }
        if (tempRefractRatio == mRefractRatio) {
            return;
        }
        invalidate();
    }

    @Deprecated
    public float getCurvedRefractRatio() {
        return mRefractRatio;
    }

    @Deprecated
    public void setCurvedRefractRatio(@FloatRange(from = 0.0f, to = 1.0f) float refractRatio) {
        setRefractRatio(refractRatio);
    }

    /**
     * 获取选中监听
     *
     * @return 选中监听器
     */
    public OnItemSelectedListener<T> getOnItemSelectedListener() {
        return mOnItemSelectedListener;
    }

    /**
     * 设置选中监听
     *
     * @param onItemSelectedListener 选中监听器
     */
    public void setOnItemSelectedListener(OnItemSelectedListener<T> onItemSelectedListener) {
        mOnItemSelectedListener = onItemSelectedListener;
    }

    /**
     * 获取滚动变化监听
     *
     * @return 滚动变化监听器
     */
    public OnWheelChangedListener getOnWheelChangedListener() {
        return mOnWheelChangedListener;
    }

    /**
     * 设置滚动变化监听
     *
     * @param onWheelChangedListener 滚动变化监听器
     */
    public void setOnWheelChangedListener(OnWheelChangedListener onWheelChangedListener) {
        mOnWheelChangedListener = onWheelChangedListener;
    }

    /*
      --------- 滚动变化方法同监听器方法（适用于子类） ------
     */

    /**
     * WheelView 滚动
     *
     * @param scrollOffsetY 滚动偏移
     */
    protected void onWheelScroll(int scrollOffsetY) {

    }

    /**
     * WheelView 条目变化
     *
     * @param oldPosition 旧的下标
     * @param newPosition 新下标
     */
    protected void onWheelItemChanged(int oldPosition, int newPosition) {

    }

    /**
     * WheelView 选中
     *
     * @param position 选中的下标
     */
    protected void onWheelSelected(int position) {

    }

    /**
     * WheelView 滚动状态
     *
     * @param state 滚动状态
     *              {@link WheelView#SCROLL_STATE_IDLE}
     *              {@link WheelView#SCROLL_STATE_DRAGGING}
     *              {@link WheelView#SCROLL_STATE_SCROLLING}
     */
    protected void onWheelScrollStateChanged(int state) {

    }

    /**
     * 条目选中回调
     *
     * @param data     选中的数据
     * @param position 选中的下标
     */
    protected void onItemSelected(T data, int position) {

    }

    /*
      --------- 滚动变化方法同监听器方法（适用于子类） ------
     */

    /**
     * dp转换px
     *
     * @param dp dp值
     * @return 转换后的px值
     */
    protected static float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    /**
     * sp转换px
     *
     * @param sp sp值
     * @return 转换后的px值
     */
    protected static float sp2px(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, Resources.getSystem().getDisplayMetrics());
    }

    /**
     * 自定义文字对齐方式注解
     * <p>
     * {@link #mTextAlign}
     * {@link #setTextAlign(int)}
     */
    @IntDef({TEXT_ALIGN_LEFT, TEXT_ALIGN_CENTER, TEXT_ALIGN_RIGHT})
    @Retention(value = RetentionPolicy.SOURCE)
    public @interface TextAlign {
    }

    /**
     * 自定义左右圆弧效果方向注解
     * <p>
     * {@link #mCurvedArcDirection}
     * {@link #setCurvedArcDirection(int)}
     */
    @IntDef({CURVED_ARC_DIRECTION_LEFT, CURVED_ARC_DIRECTION_CENTER, CURVED_ARC_DIRECTION_RIGHT})
    @Retention(value = RetentionPolicy.SOURCE)
    public @interface CurvedArcDirection {
    }

    /**
     * 自定义分割线类型注解
     * <p>
     * {@link #mDividerType}
     * {@link #setDividerType(int)}
     */
    @IntDef({DIVIDER_TYPE_FILL, DIVIDER_TYPE_WRAP})
    @Retention(value = RetentionPolicy.SOURCE)
    public @interface DividerType {
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
         * @param scrollOffsetY 滚动偏移
         */
        void onWheelScroll(int scrollOffsetY);

        /**
         * WheelView 条目变化
         *
         * @param oldPosition 旧的下标
         * @param newPosition 新下标
         */
        void onWheelItemChanged(int oldPosition, int newPosition);

        /**
         * WheelView 选中
         *
         * @param position 选中的下标
         */
        void onWheelSelected(int position);

        /**
         * WheelView 滚动状态
         *
         * @param state 滚动状态
         *              {@link WheelView#SCROLL_STATE_IDLE}
         *              {@link WheelView#SCROLL_STATE_DRAGGING}
         *              {@link WheelView#SCROLL_STATE_SCROLLING}
         */
        void onWheelScrollStateChanged(int state);
    }

    private static class DefaultDataDelegate<V> {

        private final List<V> mDataList = new ArrayList<>();
        private ItemTextFormatter mItemTextFormatter;
        private boolean isCyclic;

        int getItemCount() {
            return mDataList.size();
        }

        V getItem(int position) {
            return mDataList.get(position);
        }

        /**
         * 获取展示的文字
         *
         * @param item item
         * @return 展示的文字
         */
        @NonNull
        String getItemText(V item) {
            return mItemTextFormatter == null ? (item == null ? "" : item.toString())
                    : mItemTextFormatter.formatText(item);
        }

        /**
         * 根据下标获取到内容
         *
         * @param index 下标
         * @return 绘制的文字内容
         */
        @Nullable
        String getItemTextByIndex(int index) {
            int dataSize = getItemCount();
            if (dataSize == 0) {
                return null;
            }

            String itemText = null;
            if (isCyclic) {
                int i = index % dataSize;
                if (i < 0) {
                    i += dataSize;
                }
                itemText = getItemText(getItem(i));
            } else {
                if (index >= 0 && index < dataSize) {
                    itemText = getItemText(getItem(index));
                }
            }
            return itemText;
        }

        void setCyclic(boolean cyclic) {
            isCyclic = cyclic;
        }

        void setData(List<V> dataList) {
            mDataList.clear();
            if (dataList == null) {
                return;
            }
            mDataList.addAll(dataList);
        }

        @NonNull
        List<V> getData() {
            return mDataList;
        }

        void setItemTextFormatter(ItemTextFormatter itemTextFormatter) {
            mItemTextFormatter = itemTextFormatter;
        }
    }
}
