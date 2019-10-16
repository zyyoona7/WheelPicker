package com.zyyoona7.wheel

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.media.AudioManager
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.Scroller
import androidx.annotation.*
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.zyyoona7.wheel.adapter.ArrayWheelAdapter
import com.zyyoona7.wheel.formatter.TextFormatter
import com.zyyoona7.wheel.listener.OnItemPositionChangedListener
import com.zyyoona7.wheel.listener.OnItemSelectedListener
import com.zyyoona7.wheel.listener.OnScrollChangedListener
import com.zyyoona7.wheel.sound.SoundHelper
import kotlin.math.*

/**
 * 滚轮选择控件
 *
 * @author zyyoona7
 */
open class WheelViewKt @JvmOverloads constructor(context: Context,
                                                 attrs: AttributeSet? = null,
                                                 defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr), Runnable, ArrayWheelAdapter.OnFinishScrollCallback {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    //每个item的高度
    private var itemHeight: Int = 0
    //文字的最大宽度
    private var maxTextWidth: Int = 0
    //文字中心距离baseline的距离
    private var centerToBaselineY: Int = 0
    //文字起始X
    private var startX: Int = 0
    //X轴中心点
    private var centerX: Int = 0
    //Y轴中心点
    private var centerY: Int = 0
    //选中边界的上下限制
    private var selectedItemTopLimit: Int = 0
    private var selectedItemBottomLimit: Int = 0
    //裁剪边界
    private var clipLeft: Int = 0
    private var clipTop: Int = 0
    private var clipRight: Int = 0
    private var clipBottom: Int = 0
    //绘制区域
    private val drawRect = Rect()
    //3D效果
    private val cameraForCurved = Camera()
    private val matrixForCurved = Matrix()

    private val scroller: Scroller = Scroller(context)
    private var velocityTracker: VelocityTracker? = null
    private var maxFlingVelocity: Int = 0
    private var minFlingVelocity: Int = 0

    //最小滚动距离，上边界
    private var minScrollY: Int = 0
    //最大滚动距离，下边界
    private var maxScrollY: Int = 0

    //Y轴滚动偏移
    private var scrollOffsetY: Int = 0
    //Y轴已滚动偏移，控制重绘次数
    private var scrolledY = 0
    //手指最后触摸的位置
    private var lastTouchY: Float = 0f
    //手指按下时间，根据按下抬起时间差处理点击滚动
    private var downStartTime: Long = 0L
    //是否强制停止滚动
    private var isForceFinishScroll = false
    //是否是快速滚动，快速滚动结束后跳转位置
    private var isFlingScroll: Boolean = false
    private val soundHelper: SoundHelper by lazy { SoundHelper.obtain() }
    private var wheelAdapter: ArrayWheelAdapter<*>? = null

    //当前滚动经过的下标
    private var currentScrollPosition: Int = 0
    //记录已经执行过 itemPositionChange 的滚动的下标，防止多次执行
    //position不改变得情况下只执行一次 itemPositionChanged()
    private var itemChangedPosition = -1
    //当前滚动状态
    @ScrollState
    private var currentScrollState: Int = SCROLL_STATE_IDLE

    //属性
    //当前选中的下标
    private var selectedPosition: Int = 0
    /*
      ---------- 文字相关 ----------
     */
    //字体大小
    var textSize: Float = 0f
        set(value) {
            if (value == field || value <= 0f) {
                return
            }
            field = value
            notifyDataSetChanged()
        }
    //是否自动调整字体大小以显示完全
    var isAutoFitTextSize: Boolean = false
        set(value) {
            field = value
            invalidate()
        }
    //文字对齐方式
    @TextAlign
    var textAlign: Int = TEXT_ALIGN_CENTER
        set(@TextAlign value) {
            if (value == field) {
                return
            }
            field = value
            notifyTextAlignChanged()
        }
    //未选中item文字颜色
    var normalTextColor: Int = 0
        set(@ColorInt value) {
            if (value == field) {
                return
            }
            field = value
            invalidate()
        }
    //选中item文字颜色
    var selectedTextColor: Int = 0
        set(@ColorInt value) {
            if (value == field) {
                return
            }
            field = value
            invalidate()
        }
    //字体外边距，目的是留有边距
    var textBoundaryMargin: Float = 0f
        set(value) {
            if (value == field) {
                return
            }
            field = value
            requestLayout()
        }
    //字体
    private var isBoldForSelectedItem = false
    //如果 mIsBoldForSelectedItem==true 则这个字体为未选中条目的字体
    private var normalTypeface: Typeface? = null
    //如果 mIsBoldForSelectedItem==true 则这个字体为选中条目的字体
    private var boldTypeface: Typeface? = null
    /*
      ---------- 文字相关 ----------
     */
    //可见的item条数
    var visibleItems: Int = 0
        set(value) {
            if (value == field) {
                return
            }
            field = adjustVisibleItems(value)
            calculateScrollOffsetY()
            requestLayout()
        }
    //每个item之间的空间，行间距
    var lineSpacing: Float = 0f
        set(value) {
            if (value == field) {
                return
            }
            field = value
            calculateItemHeight()
            calculateScrollOffsetY()
            requestLayout()
        }
    //是否循环滚动
    var isCyclic: Boolean = false
        set(value) {
            if (value == field) {
                return
            }
            field = value
            wheelAdapter?.isCyclic = field
            notifyCyclicChanged()
        }
    /*
      ---------- 分割线相关 ----------
     */
    //是否显示分割线
    var isShowDivider: Boolean = false
        set(value) {
            if (value == field) {
                return
            }
            field = value
            if (dividerOffsetY > 0) {
                calculateTopAndBottomLimit()
            }
            invalidate()
        }
    //分割线的颜色
    var dividerColor: Int = 0
        set(@ColorInt value) {
            if (value == field) {
                return
            }
            field = value
            if (isShowDivider) {
                invalidate()
            }
        }
    //分割线高度
    var dividerHeight: Float = 0f
        set(value) {
            if (value == field) {
                return
            }
            field = value
            if (isShowDivider) {
                invalidate()
            }
        }
    //分割线填充类型
    var dividerType: Int = 0
        set(@DividerType value) {
            if (value == field) {
                return
            }
            field = value
            if (isShowDivider) {
                invalidate()
            }
        }
    //分割线类型为DIVIDER_TYPE_WRAP时 分割线左右两端距离文字的间距
    var dividerPaddingForWrap: Float = 0f
        set(value) {
            if (value == field) {
                return
            }
            field = value
            if (isShowDivider) {
                invalidate()
            }
        }
    //分割线两端形状，默认圆头
    var dividerCap: Paint.Cap = Paint.Cap.ROUND
        set(value) {
            if (value == field) {
                return
            }
            field = value
            if (isShowDivider) {
                invalidate()
            }
        }
    //分割线和选中区域垂直方向的偏移，实现扩大选中区域
    var dividerOffsetY: Float = 0f
        set(value) {
            if (value == field) {
                return
            }
            field = value
            if (isShowDivider) {
                calculateTopAndBottomLimit()
                invalidate()
            }
        }
    /*
      ---------- 分割线相关 ----------
     */
    /*
      ---------- 选中区域蒙层相关 ----------
     */
    //是否绘制选中区域
    var isShowCurtain: Boolean = false
        set(value) {
            if (value == field) {
                return
            }
            field = value
            invalidate()
        }
    //选中区域颜色
    @ColorInt
    var curtainColor: Int = 0
        set(@ColorInt value) {
            if (value == field) {
                return
            }
            field = value
            if (isShowCurtain) {
                invalidate()
            }
        }
    /*
      ---------- 选中区域蒙层相关 ----------
     */
    /*
      ---------- 3D效果相关 ----------
     */
    //是否是弯曲（3D）效果
    var isCurved: Boolean = false
        set(value) {
            if (value == field) {
                return
            }
            field = value
            calculateItemHeight()
            requestLayout()
        }
    //弯曲（3D）效果左右圆弧偏移效果方向 center 不偏移
    @CurvedArcDirection
    var curvedArcDirection: Int = 0
        set(@CurvedArcDirection value) {
            if (value == field) {
                return
            }
            field = value
            if (isCurved) {
                invalidate()
            }
        }
    //弯曲（3D）效果左右圆弧偏移效果系数 0-1之间 越大越明显
    var curvedArcDirectionFactor: Float = 0f
        set(value) {
            if (value == field) {
                return
            }
            field = min(1f, max(0f, value))
            if (isCurved) {
                invalidate()
            }
        }
    //选中后折射的偏移 与字体大小的比值，1为不偏移 越小偏移越明显
    //(普通效果和3d效果都适用)
    var refractRatio: Float = 0f
        set(value) {
            if (value == field) {
                return
            }
            field = min(1f, max(0f, value))
            invalidate()
        }
    /*
      ---------- 3D效果相关 ----------
     */
    //是否开启音频效果
    var isSoundEffect = false
        set(value) {
            field = value
            if (soundHelper.soundPlayVolume == 0f) {
                initDefaultVolume()
            }
        }
    //数据变化时，是否重置选中下标到第一个位置
    var isResetSelectedPosition = false
    /*
      ---------- 选中范围限制 ----------
     */
    private var maxSelectedPosition: Int = -1
    private var minSelectedPosition: Int = -1
    //有滚动限制并且不是循环滚动时 超出限制范围是否可以继续滚动
    var canOverRangeScroll: Boolean = true
        set(value) {
            field = value
            calculateLimitY()
        }
    /*
      ---------- 选中范围限制 ----------
     */
    /*
      ---------- 监听器 -----------
     */
    private var itemSelectedListener: OnItemSelectedListener? = null
    private var scrollChangedListener: OnScrollChangedListener? = null
    private var itemPositionChangedListener: OnItemPositionChangedListener? = null
    /*
      ---------- 监听器 ----------
     */
    //adapter 中的 textFormatter
    private var textFormatter: TextFormatter? = null
    //adapter 中的 formatter block
    private var formatterBlock: ((Any?) -> String)? = null

    companion object {
        private const val TAG = "WheelView"
        private val DEFAULT_LINE_SPACING = dp2px(2f)
        private val DEFAULT_TEXT_SIZE = sp2px(15f)
        private val DEFAULT_TEXT_BOUNDARY_MARGIN = dp2px(2f)
        private val DEFAULT_DIVIDER_HEIGHT = dp2px(1f)
        private const val DEFAULT_NORMAL_TEXT_COLOR = Color.DKGRAY
        private const val DEFAULT_SELECTED_TEXT_COLOR = Color.BLACK
        private const val DEFAULT_VISIBLE_ITEM = 5
        const val DEFAULT_SCROLL_DURATION = 250
        private const val DEFAULT_CLICK_CONFIRM: Long = 120
        //默认折射比值，通过字体大小来实现折射视觉差
        private const val DEFAULT_REFRACT_RATIO = 1f

        //文字对齐方式
        const val TEXT_ALIGN_LEFT = 0
        const val TEXT_ALIGN_CENTER = 1
        const val TEXT_ALIGN_RIGHT = 2

        //滚动状态
        const val SCROLL_STATE_IDLE = 0
        const val SCROLL_STATE_DRAGGING = 1
        const val SCROLL_STATE_SCROLLING = 2

        //弯曲效果对齐方式
        const val CURVED_ARC_DIRECTION_LEFT = 0
        const val CURVED_ARC_DIRECTION_CENTER = 1
        const val CURVED_ARC_DIRECTION_RIGHT = 2

        const val DEFAULT_CURVED_FACTOR = 0.75f

        //分割线填充类型
        const val DIVIDER_TYPE_FILL = 0
        const val DIVIDER_TYPE_WRAP = 1

        /**
         * dp转换px
         *
         * @param dp dp值
         * @return 转换后的px值
         */
        protected fun dp2px(dp: Float): Float {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().displayMetrics)
        }

        /**
         * sp转换px
         *
         * @param sp sp值
         * @return 转换后的px值
         */
        protected fun sp2px(sp: Float): Float {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, Resources.getSystem().displayMetrics)
        }

        private fun logAdapterNull() {
            Log.e(TAG, "the WheelView adapter is null.")
        }
    }

    init {
        initValue(context)
        attrs?.let {
            initAttrsAndDefault(context, it)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        soundHelper.release()
    }

    /**
     * 初始化自定义属性及默认值
     *
     * @param context 上下文
     * @param attrs   attrs
     */
    private fun initAttrsAndDefault(context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.WheelViewKt)
        textSize = typedArray.getDimension(R.styleable.WheelViewKt_wv_textSize, DEFAULT_TEXT_SIZE)
        isAutoFitTextSize = typedArray.getBoolean(R.styleable.WheelViewKt_wv_autoFitTextSize, false)
        textAlign = typedArray.getInt(R.styleable.WheelViewKt_wv_textAlign, TEXT_ALIGN_CENTER)
        textBoundaryMargin = typedArray.getDimension(R.styleable.WheelViewKt_wv_textBoundaryMargin,
                DEFAULT_TEXT_BOUNDARY_MARGIN)
        normalTextColor = typedArray.getColor(R.styleable.WheelViewKt_wv_normalTextColor, DEFAULT_NORMAL_TEXT_COLOR)
        selectedTextColor = typedArray.getColor(R.styleable.WheelViewKt_wv_selectedTextColor, DEFAULT_SELECTED_TEXT_COLOR)
        lineSpacing = typedArray.getDimension(R.styleable.WheelViewKt_wv_lineSpacing, DEFAULT_LINE_SPACING)

        visibleItems = typedArray.getInt(R.styleable.WheelViewKt_wv_visibleItems, DEFAULT_VISIBLE_ITEM)
        //跳转可见item为奇数
        visibleItems = adjustVisibleItems(visibleItems)
        val selectedPosition = typedArray.getInt(R.styleable.WheelViewKt_wv_selectedPosition, 0)
        val maxSelectedPosition = typedArray.getInt(R.styleable.WheelViewKt_wv_maxSelectedPosition, -1)
        val minSelectedPosition = typedArray.getInt(R.styleable.WheelViewKt_wv_minSelectedPosition, -1)
        initSelectedPositionAndRange(selectedPosition, minSelectedPosition, maxSelectedPosition)

        isCyclic = typedArray.getBoolean(R.styleable.WheelViewKt_wv_cyclic, false)

        isShowDivider = typedArray.getBoolean(R.styleable.WheelViewKt_wv_showDivider, false)
        dividerType = typedArray.getInt(R.styleable.WheelViewKt_wv_dividerType, DIVIDER_TYPE_FILL)
        dividerHeight = typedArray.getDimension(R.styleable.WheelViewKt_wv_dividerHeight, DEFAULT_DIVIDER_HEIGHT)
        dividerColor = typedArray.getColor(R.styleable.WheelViewKt_wv_dividerColor, DEFAULT_SELECTED_TEXT_COLOR)
        dividerPaddingForWrap = typedArray.getDimension(R.styleable.WheelViewKt_wv_dividerPaddingForWrap, DEFAULT_TEXT_BOUNDARY_MARGIN)

        dividerOffsetY = typedArray.getDimensionPixelOffset(R.styleable.WheelViewKt_wv_dividerOffsetY, 0).toFloat()

        isShowCurtain = typedArray.getBoolean(R.styleable.WheelViewKt_wv_isShowCurtain, false)
        curtainColor = typedArray.getColor(R.styleable.WheelViewKt_wv_curtainColor, Color.TRANSPARENT)

        isCurved = typedArray.getBoolean(R.styleable.WheelViewKt_wv_curved, true)
        curvedArcDirection = typedArray.getInt(R.styleable.WheelViewKt_wv_curvedArcDirection, CURVED_ARC_DIRECTION_CENTER)
        curvedArcDirectionFactor = typedArray.getFloat(R.styleable.WheelViewKt_wv_curvedArcDirectionFactor, DEFAULT_CURVED_FACTOR)
        //折射偏移默认值
        //Deprecated 将在新版中移除
        val curvedRefractRatio = typedArray.getFloat(R.styleable.WheelViewKt_wv_curvedRefractRatio, 0.9f)
        refractRatio = typedArray.getFloat(R.styleable.WheelViewKt_wv_refractRatio, DEFAULT_REFRACT_RATIO)
        refractRatio = if (isCurved) min(curvedRefractRatio, refractRatio) else refractRatio
        if (refractRatio > 1f) {
            refractRatio = 1.0f
        } else if (refractRatio < 0f) {
            refractRatio = DEFAULT_REFRACT_RATIO
        }
        typedArray.recycle()
    }

    /**
     * 在构造方法中初始化 selectedPosition、minSelectedPosition、maxSelectedPosition
     */
    protected fun initSelectedPositionAndRange(selectedPosition: Int,
                                               minSelectedPosition: Int,
                                               maxSelectedPosition: Int) {
        this.minSelectedPosition = minSelectedPosition
        this.maxSelectedPosition = maxSelectedPosition
        this.selectedPosition = checkPositionInSelectedRange(selectedPosition)
        //初始化滚动下标
        currentScrollPosition = this.selectedPosition
    }

    /**
     * 跳转可见条目数为奇数
     *
     * @param visibleItems 可见条目数
     * @return 调整后的可见条目数
     */
    private fun adjustVisibleItems(visibleItems: Int): Int {
        return abs(visibleItems / 2 * 2 + 1) // 当传入的值为偶数时,换算成奇数;
    }

    /**
     * 初始化并设置默认值
     *
     * @param context 上下文
     */
    private fun initValue(context: Context) {
        val viewConfiguration = ViewConfiguration.get(context)
        maxFlingVelocity = viewConfiguration.scaledMaximumFlingVelocity
        minFlingVelocity = viewConfiguration.scaledMinimumFlingVelocity
        calculateTextSizeAndItemHeight()
        updateTextAlign()
    }

    /**
     * 初始化默认播放声音音量
     */
    private fun initDefaultVolume() {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        audioManager?.let {
            //获取系统媒体当前音量
            val currentVolume = it.getStreamVolume(AudioManager.STREAM_MUSIC)
            //获取系统媒体最大音量
            val maxVolume = it.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            //设置播放音量
            soundHelper.soundPlayVolume = currentVolume * 1.0f / maxVolume
        } ?: kotlin.run {
            soundHelper.soundPlayVolume = 0.3f
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //Line Space算在了mItemHeight中
        val height: Int = if (isCurved) {
            (itemHeight * visibleItems * 2 / Math.PI + paddingTop.toDouble()
                    + paddingBottom.toDouble()).toInt()
        } else {
            itemHeight * visibleItems + paddingTop + paddingBottom
        }
        var width = (maxTextWidth.toFloat() + paddingLeft.toFloat()
                + paddingRight.toFloat() + textBoundaryMargin * 2).toInt()
        if (isCurved) {
            val towardRange = (sin(Math.PI / 48) * height).toInt()
            width += towardRange
        }
        setMeasuredDimension(resolveSizeAndState(width, widthMeasureSpec, 0),
                resolveSizeAndState(height, heightMeasureSpec, 0))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //设置内容可绘制区域
        drawRect.set(paddingLeft, paddingTop, width - paddingRight, height - paddingBottom)
        centerX = drawRect.centerX()
        centerY = drawRect.centerY()
        calculateTopAndBottomLimit()
        clipLeft = paddingLeft
        clipTop = paddingTop
        clipRight = width - paddingRight
        clipBottom = height - paddingBottom

        calculateDrawStart()
        //计算滚动限制
        calculateLimitY()

        //如果初始化时有选中的下标，则计算选中位置的距离
        val itemDistance = calculateItemDistance(selectedPosition)
        if (itemDistance > 0) {
            doScroll(itemDistance)
        }
    }

    private fun calculateTopAndBottomLimit() {
        selectedItemTopLimit = (centerY.toFloat() - (itemHeight / 2).toFloat()
                - dividerOffsetY).toInt()
        selectedItemBottomLimit = (centerY.toFloat() + (itemHeight / 2).toFloat()
                + dividerOffsetY).toInt()
    }

    /**
     * 测量文字最大所占空间和[itemHeight]
     */
    private fun calculateTextSizeAndItemHeight() {
        calculateMaxTextWidth()
        calculateItemHeight()
    }

    /**
     * 测量文字最大所占空间
     */
    private fun calculateMaxTextWidth() {
        wheelAdapter?.let {
            paint.textSize = textSize
            for (i in 0 until it.getItemCount()) {
                val textWidth = paint.measureText(it.getItemText(it.getItemData(i))).toInt()
                maxTextWidth = max(textWidth, maxTextWidth)
            }
        } ?: logAdapterNull()
    }

    /**
     * 根据字体大小和行间距计算 [itemHeight]
     */
    private fun calculateItemHeight() {
        //itemHeight实际等于字体高度+一个行间距
        itemHeight = (paint.fontMetrics.bottom
                - paint.fontMetrics.top + lineSpacing).toInt()
    }

    /**
     * 起算起始位置
     */
    private fun calculateDrawStart() {
        startX = when (textAlign) {
            TEXT_ALIGN_LEFT -> (paddingLeft + textBoundaryMargin).toInt()
            TEXT_ALIGN_RIGHT -> (width.toFloat() - paddingRight.toFloat() - textBoundaryMargin).toInt()
            TEXT_ALIGN_CENTER -> width / 2
            else -> width / 2
        }

        //文字中心距离baseline的距离
        centerToBaselineY = (paint.fontMetrics.ascent
                + (paint.fontMetrics.descent - paint.fontMetrics.ascent) / 2).toInt()
    }

    /**
     * 计算滚动限制
     */
    private fun calculateLimitY() {
        wheelAdapter?.let {
            minScrollY = if (isCyclic) Integer.MIN_VALUE else minScrollPosition(it) * itemHeight
            //下边界 (dataSize - 1) * mItemHeight
            maxScrollY = if (isCyclic) Integer.MAX_VALUE else maxScrollPosition(it) * itemHeight
        } ?: logAdapterNull()
        Log.d(TAG, "calculateLimitY minScrollY=${minScrollY},maxScrollY=${maxScrollY}")
    }

    /**
     * 获取最大滚动下标
     */
    private fun maxScrollPosition(adapter: ArrayWheelAdapter<*>): Int {
        return if (maxSelectedPosition >= 0 && maxSelectedPosition < adapter.getItemCount()
                && !canOverRangeScroll) {
            maxSelectedPosition
        } else {
            adapter.getItemCount() - 1
        }
    }

    private fun minScrollPosition(adapter: ArrayWheelAdapter<*>): Int {
        return if (minSelectedPosition in 0 until maxSelectedPosition
                && maxSelectedPosition < adapter.getItemCount()
                && !canOverRangeScroll) {
            minSelectedPosition
        } else {
            0
        }
    }

    /**
     * 根据当前的选中下标和[itemHeight]重新计算偏移
     */
    private fun calculateScrollOffsetY() {
        scrollOffsetY = selectedPosition * itemHeight
    }

    /**
     * 更新textAlign
     */
    private fun updateTextAlign() {
        when (textAlign) {
            TEXT_ALIGN_LEFT -> paint.textAlign = Paint.Align.LEFT
            TEXT_ALIGN_RIGHT -> paint.textAlign = Paint.Align.RIGHT
            TEXT_ALIGN_CENTER -> paint.textAlign = Paint.Align.CENTER
            else -> paint.textAlign = Paint.Align.CENTER
        }
    }

    /*
      ---------- 绘制部分 ---------
     */

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) {
            return
        }
        //绘制选中区域
        drawCurtainRect(canvas)
        //绘制分割线
        drawDivider(canvas)

        //滚动了多少个item，滚动的Y值高度除去每行Item的高度
        val scrolledItem = scrollOffsetY / dividedItemHeight()
        //没有滚动完一个item时的偏移值，平滑滑动
        val scrolledOffset = scrollOffsetY % dividedItemHeight()
        //向上取整
        val halfItem = (visibleItems + 1) / 2
        //计算的最小index
        val minIndex: Int
        //计算的最大index
        val maxIndex: Int
        when {
            scrolledOffset < 0 -> {
                //小于0
                minIndex = scrolledItem - halfItem - 1
                maxIndex = scrolledItem + halfItem
            }
            scrolledOffset > 0 -> {
                minIndex = scrolledItem - halfItem
                maxIndex = scrolledItem + halfItem + 1
            }
            else -> {
                minIndex = scrolledItem - halfItem
                maxIndex = scrolledItem + halfItem
            }
        }

        //绘制item
        for (i in minIndex until maxIndex) {
            if (isCurved) {
                drawCurvedItem(canvas, i, scrolledOffset, scrolledItem)
            } else {
                drawNormalItem(canvas, i, scrolledOffset, scrolledItem)
            }
        }
    }

    /**
     * 绘制选中区域
     *
     * @param canvas 画布
     */
    private fun drawCurtainRect(canvas: Canvas) {
        if (!isShowCurtain) {
            return
        }
        paint.color = curtainColor
        canvas.drawRect(clipLeft.toFloat(), selectedItemTopLimit.toFloat(),
                clipRight.toFloat(), selectedItemBottomLimit.toFloat(), paint)
    }

    /**
     * 绘制分割线
     *
     * @param canvas 画布
     */
    private fun drawDivider(canvas: Canvas) {
        if (!isShowDivider) {
            return
        }
        paint.color = dividerColor
        val originStrokeWidth = paint.strokeWidth
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = dividerCap
        paint.strokeWidth = dividerHeight
        if (dividerType == DIVIDER_TYPE_FILL) {
            canvas.drawLine(clipLeft.toFloat(), selectedItemTopLimit.toFloat(),
                    clipRight.toFloat(), selectedItemTopLimit.toFloat(), paint)
            canvas.drawLine(clipLeft.toFloat(), selectedItemBottomLimit.toFloat(),
                    clipRight.toFloat(), selectedItemBottomLimit.toFloat(), paint)
        } else {
            //边界处理 超过边界直接按照DIVIDER_TYPE_FILL类型处理
            val startX = (centerX.toFloat() - (maxTextWidth / 2).toFloat()
                    - dividerPaddingForWrap).toInt()
            val stopX = (centerX.toFloat() + (maxTextWidth / 2).toFloat()
                    + dividerPaddingForWrap).toInt()

            val wrapStartX = if (startX < clipLeft) clipLeft else startX
            val wrapStopX = if (stopX > clipRight) clipRight else stopX
            canvas.drawLine(wrapStartX.toFloat(), selectedItemTopLimit.toFloat(),
                    wrapStopX.toFloat(), selectedItemTopLimit.toFloat(), paint)
            canvas.drawLine(wrapStartX.toFloat(), selectedItemBottomLimit.toFloat(),
                    wrapStopX.toFloat(), selectedItemBottomLimit.toFloat(), paint)
        }
        paint.strokeWidth = originStrokeWidth
    }

    /**
     * 绘制2D效果
     *
     * @param canvas         画布
     * @param index          下标
     * @param scrolledOffset 滚动偏移
     * @param scrolledItem   滚动了多少个item，滚动的Y值高度除去每行Item的高度
     */
    private fun drawNormalItem(canvas: Canvas, index: Int,
                               scrolledOffset: Int, scrolledItem: Int) {
        val text = wheelAdapter?.getItemTextByIndex(index) ?: ""
        if (text.trim().isEmpty()) {
            return
        }

        //index 的 item 距离中间项的偏移
        val item2CenterOffsetY = (index - scrolledItem) * itemHeight - scrolledOffset
        //记录初始测量的字体起始X
        val startX = this.startX
        //重新测量字体宽度和基线偏移
        val centerToBaselineY = if (isAutoFitTextSize) remeasureTextSize(text) else centerToBaselineY

        if (Math.abs(item2CenterOffsetY) <= 0) {
            //绘制选中的条目
            paint.color = selectedTextColor
            clipAndDrawNormalText(canvas, text, selectedItemTopLimit,
                    selectedItemBottomLimit, item2CenterOffsetY, centerToBaselineY)
        } else if (item2CenterOffsetY in 1 until itemHeight) {
            //绘制与下边界交汇的条目
            paint.color = selectedTextColor
            clipAndDrawNormalText(canvas, text, selectedItemTopLimit,
                    selectedItemBottomLimit, item2CenterOffsetY, centerToBaselineY)

            paint.color = normalTextColor
            //缩小字体，实现折射效果
            val textSize = paint.textSize
            paint.textSize = textSize * refractRatio
            //mIsBoldForSelectedItem==true 改变字体
            changeTypefaceIfBoldForSelectedItem()
            clipAndDrawNormalText(canvas, text, selectedItemBottomLimit, clipBottom,
                    item2CenterOffsetY, centerToBaselineY)
            paint.textSize = textSize
            //isBoldForSelectedItem==true 恢复字体
            resetTypefaceIfBoldForSelectedItem()
        } else if (item2CenterOffsetY < 0 && item2CenterOffsetY > -itemHeight) {
            //绘制与上边界交汇的条目
            paint.color = selectedTextColor
            clipAndDrawNormalText(canvas, text, selectedItemTopLimit,
                    selectedItemBottomLimit, item2CenterOffsetY, centerToBaselineY)

            paint.color = normalTextColor
            //缩小字体，实现折射效果
            val textSize = paint.textSize
            paint.textSize = textSize * refractRatio
            //isBoldForSelectedItem==true 改变字体
            changeTypefaceIfBoldForSelectedItem()
            clipAndDrawNormalText(canvas, text, clipTop, selectedItemTopLimit,
                    item2CenterOffsetY, centerToBaselineY)
            paint.textSize = textSize
            //isBoldForSelectedItem==true 恢复字体
            resetTypefaceIfBoldForSelectedItem()
        } else {
            //绘制其他条目
            paint.color = normalTextColor
            //缩小字体，实现折射效果
            val textSize = paint.textSize
            paint.textSize = textSize * refractRatio
            //isBoldForSelectedItem==true 改变字体
            changeTypefaceIfBoldForSelectedItem()
            clipAndDrawNormalText(canvas, text, clipTop, clipBottom,
                    item2CenterOffsetY, centerToBaselineY)
            paint.textSize = textSize
            //mIsBoldForSelectedItem==true 恢复字体
            resetTypefaceIfBoldForSelectedItem()
        }

        if (isAutoFitTextSize) {
            //恢复重新测量之前的样式
            paint.textSize = textSize
            this.startX = startX
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
    private fun clipAndDrawNormalText(canvas: Canvas, text: String, clipTop: Int, clipBottom: Int,
                                      item2CenterOffsetY: Int, centerToBaselineY: Int) {
        canvas.save()
        canvas.clipRect(clipLeft, clipTop, clipRight, clipBottom)
        canvas.drawText(text, 0, text.length, startX.toFloat(),
                (centerY + item2CenterOffsetY - centerToBaselineY).toFloat(), paint)
        canvas.restore()
    }

    /**
     * 绘制弯曲（3D）效果的item
     *
     * @param canvas         画布
     * @param index          下标
     * @param scrolledOffset 滚动偏移
     * @param scrolledItem   滚动了多少个item，滚动的Y值高度除去每行Item的高度
     */
    private fun drawCurvedItem(canvas: Canvas, index: Int,
                               scrolledOffset: Int, scrolledItem: Int) {
        val text = wheelAdapter?.getItemTextByIndex(index) ?: ""
        if (text.isEmpty()) {
            return
        }
        // 滚轮的半径
        val radius = (height - paddingTop - paddingBottom) / 2
        //index 的 item 距离中间项的偏移
        val item2CenterOffsetY = (index - scrolledItem) * itemHeight - scrolledOffset

        // 当滑动的角度和y轴垂直时（此时文字已经显示为一条线），不绘制文字
        if (abs(item2CenterOffsetY) > radius * Math.PI / 2) return

        val angle = item2CenterOffsetY.toDouble() / radius
        // 绕x轴滚动的角度
        val rotateX = Math.toDegrees(-angle).toFloat()
        // 滚动的距离映射到y轴的长度
        val translateY = (sin(angle) * radius).toFloat()
        // 滚动的距离映射到z轴的长度
        val translateZ = ((1 - cos(angle)) * radius).toFloat()
        // 透明度
        val alpha = (cos(angle) * 255).toInt()

        //记录初始测量的字体起始X
        val startX = this.startX
        //重新测量字体宽度和基线偏移
        val centerToBaselineY = if (isAutoFitTextSize) remeasureTextSize(text) else centerToBaselineY
        if (abs(item2CenterOffsetY) <= 0) {
            //绘制选中的条目
            paint.color = selectedTextColor
            paint.alpha = 255
            clipAndDrawCurvedText(canvas, text, selectedItemTopLimit, selectedItemBottomLimit,
                    rotateX, translateY, translateZ, centerToBaselineY)
        } else if (item2CenterOffsetY in 1 until itemHeight) {
            //绘制与下边界交汇的条目
            paint.color = selectedTextColor
            paint.alpha = 255
            clipAndDrawCurvedText(canvas, text, selectedItemTopLimit, selectedItemBottomLimit,
                    rotateX, translateY, translateZ, centerToBaselineY)

            paint.color = normalTextColor
            paint.alpha = alpha
            //缩小字体，实现折射效果
            val textSize = paint.textSize
            paint.textSize = textSize * refractRatio
            //isBoldForSelectedItem==true 改变字体
            changeTypefaceIfBoldForSelectedItem()
            //字体变化，重新计算距离基线偏移
            val reCenterToBaselineY = recalculateCenterToBaselineY()
            clipAndDrawCurvedText(canvas, text, selectedItemBottomLimit, clipBottom,
                    rotateX, translateY, translateZ, reCenterToBaselineY)
            paint.textSize = textSize
            //isBoldForSelectedItem==true 恢复字体
            resetTypefaceIfBoldForSelectedItem()
        } else if (item2CenterOffsetY < 0 && item2CenterOffsetY > -itemHeight) {
            //绘制与上边界交汇的条目
            paint.color = selectedTextColor
            paint.alpha = 255
            clipAndDrawCurvedText(canvas, text, selectedItemTopLimit, selectedItemBottomLimit,
                    rotateX, translateY, translateZ, centerToBaselineY)

            paint.color = normalTextColor
            paint.alpha = alpha

            //缩小字体，实现折射效果
            val textSize = paint.textSize
            paint.textSize = textSize * refractRatio
            //isBoldForSelectedItem==true 改变字体
            changeTypefaceIfBoldForSelectedItem()
            //字体变化，重新计算距离基线偏移
            val reCenterToBaselineY = recalculateCenterToBaselineY()
            clipAndDrawCurvedText(canvas, text, clipTop, selectedItemTopLimit,
                    rotateX, translateY, translateZ, reCenterToBaselineY)
            paint.setTextSize(textSize)
            //isBoldForSelectedItem==true 恢复字体
            resetTypefaceIfBoldForSelectedItem()
        } else {
            //绘制其他条目
            paint.color = normalTextColor
            paint.alpha = alpha

            //缩小字体，实现折射效果
            val textSize = paint.textSize
            paint.textSize = textSize * refractRatio
            //isBoldForSelectedItem==true 改变字体
            changeTypefaceIfBoldForSelectedItem()
            //字体变化，重新计算距离基线偏移
            val reCenterToBaselineY = recalculateCenterToBaselineY()
            clipAndDrawCurvedText(canvas, text, clipTop, clipBottom,
                    rotateX, translateY, translateZ, reCenterToBaselineY)
            paint.textSize = textSize
            //isBoldForSelectedItem==true 恢复字体
            resetTypefaceIfBoldForSelectedItem()
        }

        if (isAutoFitTextSize) {
            //恢复重新测量之前的样式
            paint.textSize = textSize
            this.startX = startX
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
    private fun clipAndDrawCurvedText(canvas: Canvas, text: String, clipTop: Int, clipBottom: Int,
                                      rotateX: Float, offsetY: Float, offsetZ: Float, centerToBaselineY: Int) {

        canvas.save()
        canvas.clipRect(clipLeft, clipTop, clipRight, clipBottom)
        drawCurvedText(canvas, text, rotateX, offsetY, offsetZ, centerToBaselineY)
        canvas.restore()
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
    private fun drawCurvedText(canvas: Canvas, text: String, rotateX: Float, offsetY: Float,
                               offsetZ: Float, centerToBaselineY: Int) {
        cameraForCurved.save()
        cameraForCurved.translate(0f, 0f, offsetZ)
        cameraForCurved.rotateX(rotateX)
        cameraForCurved.getMatrix(matrixForCurved)
        cameraForCurved.restore()

        // 调节中心点
        var centerX = centerX.toFloat()
        //根据弯曲（3d）对齐方式设置系数
        if (curvedArcDirection == CURVED_ARC_DIRECTION_LEFT) {
            centerX *= (1 + curvedArcDirectionFactor)
        } else if (curvedArcDirection == CURVED_ARC_DIRECTION_RIGHT) {
            centerX *= (1 - curvedArcDirectionFactor)
        }

        val centerY = centerY + offsetY
        matrixForCurved.preTranslate(-centerX, -centerY)
        matrixForCurved.postTranslate(centerX, centerY)

        canvas.concat(matrixForCurved)
        canvas.drawText(text, 0, text.length, startX.toFloat(),
                centerY - centerToBaselineY, paint)

    }

    /**
     * 重新测量字体大小
     *
     * @param contentText 被测量文字内容
     * @return 文字中心距离baseline的距离
     */
    private fun remeasureTextSize(contentText: String): Int {
        var textWidth = paint.measureText(contentText)
        var drawWidth = width.toFloat()
        var textMargin = textBoundaryMargin * 2
        //稍微增加了一点文字边距 最大为宽度的1/10
        if (textMargin > drawWidth / 10f) {
            drawWidth = drawWidth * 9f / 10f
            textMargin = drawWidth / 10f
        } else {
            drawWidth -= textMargin
        }
        if (drawWidth <= 0) {
            return centerToBaselineY
        }
        var textSize = textSize
        while (textWidth > drawWidth) {
            textSize--
            if (textSize <= 0) {
                break
            }
            paint.textSize = textSize
            textWidth = paint.measureText(contentText)
        }
        //重新计算文字起始X
        recalculateStartX(textMargin / 2.0f)
        //高度起点也变了
        return recalculateCenterToBaselineY()
    }

    /**
     * 重新计算字体起始X
     *
     * @param textMargin 文字外边距
     */
    private fun recalculateStartX(textMargin: Float) {
        startX = when (textAlign) {
            TEXT_ALIGN_LEFT -> textMargin.toInt()
            TEXT_ALIGN_RIGHT -> (width - textMargin).toInt()
            TEXT_ALIGN_CENTER -> width / 2
            else -> width / 2
        }
    }

    /**
     * 字体大小变化后重新计算距离基线的距离
     *
     * @return 文字中心距离baseline的距离
     */
    private fun recalculateCenterToBaselineY(): Int {
        val fontMetrics = paint.fontMetrics
        //高度起点也变了
        return (fontMetrics.ascent + (fontMetrics.descent - fontMetrics.ascent) / 2).toInt()
    }

    private fun changeTypefaceIfBoldForSelectedItem() {
        if (isBoldForSelectedItem) {
            paint.typeface = normalTypeface
        }
    }

    private fun resetTypefaceIfBoldForSelectedItem() {
        if (isBoldForSelectedItem) {
            paint.typeface = boldTypeface
        }
    }

    /*
      --------- 绘制部分 ----------
     */

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        //屏蔽如果未设置数据时，触摸导致运算数据不正确的崩溃 issue #20
        if (!isEnabled || wheelAdapter == null
                || wheelAdapter?.getItemCount() == 0
                || event == null) {
            return super.onTouchEvent(event)
        }
        initVelocityTracker()
        velocityTracker?.addMovement(event)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                //手指按下
                //处理滑动事件嵌套 拦截事件序列
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                //如果未滚动完成，强制滚动完成
                if (!scroller.isFinished) {
                    //强制滚动完成
                    scroller.forceFinished(true)
                    isForceFinishScroll = true
                }
                lastTouchY = event.y
                //按下时间
                downStartTime = System.currentTimeMillis()
            }
            MotionEvent.ACTION_MOVE -> {
                //手指移动
                val moveY = event.y
                val deltaY = moveY - lastTouchY

                //回调
                currentScrollState = SCROLL_STATE_DRAGGING
                onWheelScrollStateChanged(SCROLL_STATE_DRAGGING)
                scrollChangedListener?.onScrollStateChanged(this, SCROLL_STATE_DRAGGING)

                if (abs(deltaY) < 1) {
                    return false
                }
                //deltaY 上滑为正，下滑为负
                doScroll((-deltaY).toInt())
                lastTouchY = moveY
                invalidateIfYChanged()
            }
            MotionEvent.ACTION_UP -> {
                //手指抬起
                isForceFinishScroll = false
                velocityTracker?.computeCurrentVelocity(1000, maxFlingVelocity.toFloat())
                val velocityY: Int = velocityTracker?.yVelocity?.toInt() ?: minFlingVelocity
                if (abs(velocityY) > minFlingVelocity) {
                    //快速滑动
                    scroller.forceFinished(true)
                    isFlingScroll = true
                    scroller.fling(0, scrollOffsetY, 0, (-velocityY).toInt(), 0, 0,
                            minScrollY, maxScrollY)
                } else {
                    var clickToCenterDistance = 0
                    if (System.currentTimeMillis() - downStartTime <= DEFAULT_CLICK_CONFIRM) {
                        //处理点击滚动
                        //手指抬起的位置到中心的距离为滚动差值
                        clickToCenterDistance = (event.y - centerY).toInt()
                    }
                    val scrollRange = clickToCenterDistance +
                            calculateDistanceToEndPoint(
                                    (scrollOffsetY + clickToCenterDistance) % dividedItemHeight())
                    //大于最小值滚动值
                    val isInMinRange = scrollRange < 0 && scrollOffsetY + scrollRange >= minScrollY
                    //小于最大滚动值
                    val isInMaxRange = scrollRange > 0 && scrollOffsetY + scrollRange <= maxScrollY
                    if (isInMinRange || isInMaxRange) {
                        //在滚动范围之内再修正位置
                        //平稳滑动
                        scroller.startScroll(0, scrollOffsetY, 0, scrollRange)
                    }
                }

                invalidateIfYChanged()
                ViewCompat.postOnAnimation(this, this)
                //回收 VelocityTracker
                recycleVelocityTracker()
            }
            MotionEvent.ACTION_CANCEL ->
                //事件被终止
                //回收
                recycleVelocityTracker()
        }
        return true
    }

    /**
     * 初始化 VelocityTracker
     */
    private fun initVelocityTracker() {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain()
        }
    }

    /**
     * 回收 VelocityTracker
     */
    private fun recycleVelocityTracker() {
        velocityTracker?.recycle()
        velocityTracker = null
    }

    /**
     * 使用run方法而不是computeScroll是因为，invalidate也会执行computeScroll导致回调执行不准确
     */
    override fun run() {
        //停止滚动更新当前下标
        if (scroller.isFinished && !isForceFinishScroll && !isFlingScroll) {
            if (itemHeight == 0) return
            //滚动状态停止
            //回调
            currentScrollState = SCROLL_STATE_IDLE
            onWheelScrollStateChanged(SCROLL_STATE_IDLE)
            scrollChangedListener?.onScrollStateChanged(this, SCROLL_STATE_IDLE)

            val currentItemPosition = getCurrentPosition()
            //当前选中的Position没变时不回调 onItemSelected()
            if (currentItemPosition == selectedPosition) {
                return
            }
            selectedPosition = currentItemPosition
            //停止后重新赋值
            currentScrollPosition = selectedPosition

            wheelAdapter?.let {
                it.selectedItemPosition = selectedPosition
                //检查当前选中的范围是否合法
                if (!checkSelectedPositionInRange(it)) {
                    return@let
                }
                //停止滚动，选中条目回调
                onItemSelected(it, selectedPosition)
                itemSelectedListener?.onItemSelected(this, it, selectedPosition)
            }
        }

        if (scroller.computeScrollOffset()) {
            val oldY = scrollOffsetY
            scrollOffsetY = scroller.currY

            if (oldY != scrollOffsetY) {
                currentScrollState = SCROLL_STATE_SCROLLING
                onWheelScrollStateChanged(SCROLL_STATE_SCROLLING)
                scrollChangedListener?.onScrollStateChanged(this, SCROLL_STATE_SCROLLING)
            }
            invalidateIfYChanged()
            ViewCompat.postOnAnimation(this, this)
        } else if (isFlingScroll) {
            //滚动完成后，根据是否为快速滚动处理是否需要调整最终位置
            isFlingScroll = false
            //快速滚动后需要调整滚动完成后的最终位置，重新启动scroll滑动到中心位置
            scroller.startScroll(0, scrollOffsetY,
                    0, calculateDistanceToEndPoint(scrollOffsetY % dividedItemHeight()))
            invalidateIfYChanged()
            ViewCompat.postOnAnimation(this, this)
        }
    }

    /**
     * 计算滚动偏移
     *
     * @param distance 滚动距离
     */
    private fun doScroll(distance: Int) {
        scrollOffsetY += distance
        if (!isCyclic) {
            //修正边界
            if (scrollOffsetY < minScrollY) {
                scrollOffsetY = minScrollY
            } else if (scrollOffsetY > maxScrollY) {
                scrollOffsetY = maxScrollY
            }
        }

        Log.d(TAG, "min scroll=${minScrollY},max scroll=${maxScrollY}")
    }

    /**
     * 当Y轴的偏移值改变时再重绘，减少重回次数
     */
    private fun invalidateIfYChanged() {
        if (scrollOffsetY != scrolledY) {
            scrolledY = scrollOffsetY
            //滚动偏移发生变化
            //回调
            onWheelScrollChanged(scrollOffsetY)
            scrollChangedListener?.onScrollChanged(this, scrollOffsetY)
            //观察item变化
            observeItemChanged()
            invalidate()
        }
    }

    /**
     * 观察item改变
     */
    private fun observeItemChanged() {
        //item改变回调
        val oldPosition = currentScrollPosition
        val newPosition = getCurrentPosition()
        if (oldPosition != newPosition &&
                isScrollOffsetYInRange(newPosition)
                && itemChangedPosition != newPosition) {
            //下标改变了
            //回调
            onItemChanged(oldPosition, newPosition)
            itemPositionChangedListener?.onItemChanged(this, oldPosition, newPosition)

            //播放音频
            playScrollSoundEffect()
            //更新下标
            currentScrollPosition = newPosition
            itemChangedPosition = newPosition
        }
    }

    /**
     * 播放滚动音效
     */
    private fun playScrollSoundEffect() {
        if (isSoundEffect) {
            soundHelper.playSoundEffect()
        }
    }

    /**
     * 检查是否选中的下标在限制的下标范围内
     */
    private fun checkSelectedPositionInRange(adapter: ArrayWheelAdapter<*>): Boolean {
        if (isSelectedRangeInvalid()) {
            return true
        }
        if (isLessThanMinSelected(selectedPosition)) {
            setSelectedPosition(minSelectedPosition)
            return false
        }
        if (isMoreThanMaxSelected(selectedPosition, adapter)) {
            setSelectedPosition(maxSelectedPosition)
            return false
        }
        return true
    }

    /**
     * 计算当前滚动偏移与[position]处偏移的距离
     */
    private fun calculateItemDistance(position: Int): Int {
        return position * itemHeight - scrollOffsetY
    }

    /**
     * 计算距离终点的偏移，修正选中条目
     *
     * @param remainder 余数
     * @return 偏移量
     */
    private fun calculateDistanceToEndPoint(remainder: Int): Int {
        return if (abs(remainder) > itemHeight / 2) {
            if (scrollOffsetY < 0) {
                -itemHeight - remainder
            } else {
                itemHeight - remainder
            }
        } else {
            -remainder
        }
    }

    /**
     * 根据偏移计算当前位置下标
     *
     * @return 偏移量对应的当前下标 if dataList is empty return -1
     */
    private fun getCurrentPosition(): Int {
        wheelAdapter?.let {
            if (it.getItemCount() == 0) {
                return -1
            }
            val itemPosition: Int = if (scrollOffsetY < 0) {
                (scrollOffsetY - itemHeight / 2) / dividedItemHeight()
            } else {
                (scrollOffsetY + itemHeight / 2) / dividedItemHeight()
            }
            var currentPosition = itemPosition % it.getItemCount()
            if (currentPosition < 0) {
                currentPosition += it.getItemCount()
            }
            return currentPosition
        } ?: kotlin.run {
            logAdapterNull()
            return -1
        }
    }

    /**
     * 根据下标计算当前滚动偏移[scrollOffsetY]是否在这个[position]范围内
     */
    private fun isScrollOffsetYInRange(position: Int): Boolean {
        return wheelAdapter?.let {
            val newScrollY = getShouldScrollOffsetY(position)
            //item范围中心的1/3表示在范围内
            val itemRange = itemHeight / 6
            //每圈 对应的 position 应该滚动的偏移 scrollOffsetY
            //因为有循环滚动的情况，所以要取余每圈数据的总长度
            val scrollOffsetYForCircle = scrollOffsetY % (wheelAdapter!!.getItemCount() * itemHeight)
            scrollOffsetYForCircle in newScrollY - itemRange..newScrollY + itemRange
        } ?: false
    }

    /**
     * 根据当前下标反推应该滚动的偏移  (得到的符号同scrollOffsetY)
     */
    private fun getShouldScrollOffsetY(position: Int): Int {
        return wheelAdapter?.let {
            if (it.getItemCount() == 0) {
                return 0
            }
            val itemCount = it.getItemCount()
            val currentPosition = if (scrollOffsetY < 0) {
                position - itemCount
            } else {
                position
            }
            val itemPosition = if (abs(currentPosition) < itemCount) {
                currentPosition % it.getItemCount()
            } else {
                currentPosition
            }
            itemPosition * itemHeight
        } ?: kotlin.run {
            logAdapterNull()
            0
        }
    }

    /**
     * mItemHeight 为被除数时避免为0
     *
     * @return 被除数不为0
     */
    private fun dividedItemHeight(): Int {
        return if (itemHeight > 0) itemHeight else 1
    }

    /**
     * 强制滚动完成，直接停止
     */
    private fun forceFinishScroll() {
        if (!scroller.isFinished) {
            scroller.forceFinished(true)
        }
    }

    /**
     * 强制滚动完成，并且直接滚动到最终位置
     */
    private fun abortFinishScroll() {
        if (!scroller.isFinished) {
            scroller.abortAnimation()
        }
    }

    /**
     * ArrayWheelAdapter 中触发停止滚动回调
     */
    override fun onFinishScroll() {
        forceFinishScroll()
    }

    fun <T> setData(data: List<T>) {
        setAdapter(ArrayWheelAdapter(data))
    }

    fun setAdapter(adapter: ArrayWheelAdapter<*>) {
        wheelAdapter = adapter
        wheelAdapter?.let {
            it.textFormatter = this.textFormatter
            it.formatterBlock = this.formatterBlock
            it.isCyclic = this.isCyclic
            it.selectedItemPosition = selectedPosition
            it.finishScrollCallback = this
            checkResetPosition()
            notifyDataSetChanged()
        }
    }

    /**
     * 设置 [TextFormatter]
     */
    fun setTextFormatter(textFormatter: TextFormatter) {
        this.textFormatter = textFormatter
        wheelAdapter?.let {
            it.textFormatter = this.textFormatter
            checkResetPosition()
            notifyDataSetChanged()
        }
    }

    /**
     * 设置 [TextFormatter]
     */
    fun setTextFormatter(formatterBlock: (Any?) -> String) {
        this.formatterBlock = formatterBlock
        wheelAdapter?.let {
            it.formatterBlock = this.formatterBlock
            checkResetPosition()
            notifyDataSetChanged()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun getAdapter(): ArrayWheelAdapter<*>? {
        return wheelAdapter
    }

    private fun checkResetPosition() {
        wheelAdapter?.let {
            if (!isResetSelectedPosition && it.getItemCount() > 0) {
                //不重置选中下标
                if (selectedPosition >= it.getItemCount()) {
                    selectedPosition = it.getItemCount() - 1
                    //重置滚动下标
                    currentScrollPosition = selectedPosition
                    it.selectedItemPosition = selectedPosition
                }
            } else {
                //重置选中下标和滚动下标
                selectedPosition = 0
                currentScrollPosition = selectedPosition
                it.selectedItemPosition = selectedPosition
            }
        } ?: logAdapterNull()
    }

    fun notifyDataSetChanged() {
        //强制滚动完成
        forceFinishScroll()
        calculateTopAndBottomLimit()
        calculateTextSizeAndItemHeight()
        calculateDrawStart()
        calculateLimitY()
        calculateScrollOffsetY()
        //to 自己：requestLayout 如果当前 view 的大小不变则不会触发 onDraw() 方法
        requestLayout()
        //to 自己：不用担心 onDraw() 方法执行两次，因为只有在下一帧绘制的时候才会执行 onDraw()
        invalidate()
    }

    private fun notifyTextAlignChanged() {
        updateTextAlign()
        calculateDrawStart()
        invalidate()
    }

    private fun notifyCyclicChanged() {
        forceFinishScroll()
        calculateLimitY()
        calculateScrollOffsetY()
        invalidate()
    }

    /*
      ---------- 属性设置 ----------
     */
    /**
     * 设置字体大小
     */
    fun setTextSize(textSize: Float, isSp: Boolean) {
        this.textSize = if (isSp) sp2px(textSize) else textSize
    }

    /**
     * 设置字体
     */
    @JvmOverloads
    fun setTypeface(typeface: Typeface, isBoldForSelectedItem: Boolean = false) {
        if (typeface == paint.typeface && isBoldForSelectedItem == this.isBoldForSelectedItem) {
            return
        }
        this.isBoldForSelectedItem = isBoldForSelectedItem
        if (isBoldForSelectedItem) {
            //如果设置了选中条目字体加粗，其他条目不会加粗，则拆分两份字体
            if (typeface.isBold) {
                normalTypeface = Typeface.create(typeface, Typeface.NORMAL)
                boldTypeface = typeface
            } else {
                normalTypeface = typeface
                boldTypeface = Typeface.create(typeface, Typeface.BOLD)
            }
            //测量时 使用加粗字体测量，因为加粗字体比普通字体宽，以大的为准进行测量
            paint.typeface = boldTypeface
        } else {
            paint.typeface = typeface
        }
        notifyDataSetChanged()
    }

    /**
     * 设置未选中条目文字颜色
     */
    fun setNormalTextColorRes(@ColorRes normalColorRes: Int) {
        normalTextColor = ContextCompat.getColor(context, normalColorRes)
    }

    /**
     * 设置选中条目文字颜色
     */
    fun setSelectedTextColorRes(@ColorRes selectedColorRes: Int) {
        selectedTextColor = ContextCompat.getColor(context, selectedColorRes)
    }

    /**
     * 设置文字边界外边距
     */
    fun setTextBoundaryMargin(textBoundaryMargin: Float, isDp: Boolean) {
        this.textBoundaryMargin = if (isDp) dp2px(textBoundaryMargin) else textBoundaryMargin
    }

    /**
     * 获取是否设置了选中条目字体加粗
     */
    fun isBoldForSelectedItem(): Boolean {
        return isBoldForSelectedItem
    }

    /**
     * 设置行间距
     */
    fun setLineSpacing(lineSpacing: Float, isDp: Boolean) {
        this.lineSpacing = if (isDp) dp2px(lineSpacing) else this.lineSpacing
    }

    /**
     * 设置分割线颜色
     */
    fun setDividerColorRes(@ColorRes dividerColorRes: Int) {
        dividerColor = ContextCompat.getColor(context, dividerColorRes)
    }

    /**
     * 设置分割线高度
     */
    fun setDividerHeight(dividerHeight: Float, isDp: Boolean) {
        this.dividerHeight = if (isDp) dp2px(dividerHeight) else dividerHeight
    }

    /**
     * 设置分割线类型为 [DIVIDER_TYPE_WRAP] 时，分割线与文字的左右内边距
     */
    fun setDividerPaddingForWrap(dividerPadding: Float, isDp: Boolean) {
        dividerPaddingForWrap = if (isDp) dp2px(dividerPadding) else dividerPadding
    }

    /**
     * 分割线和选中区域垂直方向的偏移，实现扩大选中区域
     */
    fun setDividerOffsetY(offsetY: Float, isDp: Boolean) {
        dividerOffsetY = if (isDp) dp2px(offsetY) else offsetY
    }

    /**
     * 选中区域颜色
     */
    fun setCurtainColorRes(@ColorRes curtainColorRes: Int) {
        curtainColor = ContextCompat.getColor(context, curtainColorRes)
    }

    /**
     * 设置选中条目下标
     */
    @JvmOverloads
    fun setSelectedPosition(position: Int, isSmoothScroll: Boolean = false,
                            smoothDuration: Int = DEFAULT_SCROLL_DURATION) {
        //adapter null or position not in range return
        wheelAdapter?.let {
            if (position !in 0 until it.getItemCount()) {
                return
            }
        } ?: return

        val realPosition = checkPositionInSelectedRange(position)

        //item之间差值
        val itemDistance = calculateItemDistance(realPosition)
        if (itemDistance == 0) {
            return
        }
        //如果Scroller滑动未停止，强制结束动画
        abortFinishScroll()

        if (isSmoothScroll) {
            //如果是平滑滚动并且之前的Scroll滚动完成
            scroller.startScroll(0, scrollOffsetY, 0, itemDistance,
                    if (smoothDuration > 0) smoothDuration else DEFAULT_SCROLL_DURATION)
            invalidateIfYChanged()
            ViewCompat.postOnAnimation(this, this)
        } else {
            doScroll(itemDistance)
            selectedPosition = realPosition
            currentScrollPosition = realPosition
            wheelAdapter?.let {
                it.selectedItemPosition = selectedPosition
                //选中条目回调
                onItemSelected(it, selectedPosition)
                itemSelectedListener?.onItemSelected(this, it, selectedPosition)
            }
            invalidateIfYChanged()
        }
    }

    /**
     * 检查position是否在限制范围内
     */
    private fun checkPositionInSelectedRange(position: Int): Int {
        if (isSelectedRangeInvalid()) {
            return position
        }
        if (isLessThanMinSelected(position)) {
            return minSelectedPosition
        }
        return wheelAdapter?.let {
            if (isMoreThanMaxSelected(position, it)) {
                maxSelectedPosition
            } else {
                position
            }
        } ?: position
    }

    /**
     * 选中范围是否无效
     */
    private fun isSelectedRangeInvalid(): Boolean {
        return maxSelectedPosition < 0 && minSelectedPosition < 0
    }

    /**
     * 当前[position]小于最小选中下标
     */
    private fun isLessThanMinSelected(position: Int): Boolean {
        return minSelectedPosition >= 0
                && position < minSelectedPosition
    }

    /**
     * 当前[position]大于最大选中下标
     */
    private fun isMoreThanMaxSelected(position: Int, adapter: ArrayWheelAdapter<*>): Boolean {
        return maxSelectedPosition < adapter.getItemCount()
                && position > maxSelectedPosition
    }

    /**
     * 设置选中范围 限制最小 最大选中下标
     */
    @JvmOverloads
    fun setSelectedRange(@IntRange(from = 0) min: Int = 0, @IntRange(from = 0) max: Int) {
        require(max >= min) {
            "maxSelectedPosition must be greater than minSelectedPosition in WheelView."
        }
        minSelectedPosition = max(0, min)
        maxSelectedPosition = wheelAdapter?.let {
            if (max >= it.getItemCount()) it.getItemCount() - 1 else max
        } ?: max
        if (selectedPosition < minSelectedPosition) {
            setSelectedPosition(min)
        } else if (selectedPosition > maxSelectedPosition) {
            setSelectedPosition(max)
        }
        calculateLimitY()
    }

    /**
     * 获取选中下标
     */
    fun getSelectedPosition(): Int {
        //如果正在滚动，停止滚动
        forceFinishScroll()
        return selectedPosition
    }

    /**
     * 获取选中条目数据
     */
    fun <T> getSelectedItem(): T? {
        return wheelAdapter?.getSelectedItem()
    }

    /**
     * 获取数据总条目数
     */
    fun getItemCount(): Int {
        return wheelAdapter?.getItemCount() ?: 0
    }

    fun getSoundVolume(): Float {
        return soundHelper.soundPlayVolume
    }

    /**
     * 设置滚动音效音量
     */
    fun setSoundVolume(playVolume: Float) {
        soundHelper.soundPlayVolume = min(1f, max(0f, playVolume))
    }

    /**
     * 设置音效资源
     */
    fun setSoundResource(@RawRes soundRes: Int) {
        soundHelper.load(context, soundRes)
    }

    /*
      ---------- 属性设置 ----------
     */

    /*
      ---------- 一些供子类重写的方法 ----------
     */
    protected open fun onWheelScrollChanged(scrollOffsetY: Int) {

    }

    protected open fun onWheelScrollStateChanged(@ScrollState state: Int) {

    }

    protected open fun onItemSelected(adapter: ArrayWheelAdapter<*>, position: Int) {

    }

    protected open fun onItemChanged(oldPosition: Int, newPosition: Int) {

    }

    /*
      ---------- 一些供子类重写的方法 ----------
     */
    /*
      ---------- 设置回调 ----------
     */
    fun setOnItemSelectedListener(itemSelectedListener: OnItemSelectedListener?) {
        this.itemSelectedListener = itemSelectedListener
    }

    fun setOnItemPositionChangedListener(itemPositionChangedListener: OnItemPositionChangedListener?) {
        this.itemPositionChangedListener = itemPositionChangedListener
    }

    fun setOnScrollChangedListener(scrollChangedListener: OnScrollChangedListener?) {
        this.scrollChangedListener = scrollChangedListener
    }
    /*
      ---------- 设置回调 ----------
     */

    /*
      ---------- 一些注解 ----------
     */

    /**
     * 自定义文字对齐方式注解
     *
     *
     * [textAlign]
     */
    @IntDef(TEXT_ALIGN_LEFT, TEXT_ALIGN_CENTER, TEXT_ALIGN_RIGHT)
    @Retention(AnnotationRetention.SOURCE)
    internal annotation class TextAlign

    /**
     * 自定义左右圆弧效果方向注解
     *
     *
     * [curvedArcDirection]
     */
    @IntDef(CURVED_ARC_DIRECTION_LEFT, CURVED_ARC_DIRECTION_CENTER, CURVED_ARC_DIRECTION_RIGHT)
    @Retention(AnnotationRetention.SOURCE)
    internal annotation class CurvedArcDirection

    /**
     * 自定义分割线类型注解
     *
     *
     * [dividerType]
     */
    @IntDef(DIVIDER_TYPE_FILL, DIVIDER_TYPE_WRAP)
    @Retention(AnnotationRetention.SOURCE)
    internal annotation class DividerType

    /**
     * 自定义滚动状态注解
     */
    @IntDef(SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SCROLLING)
    @Retention(AnnotationRetention.SOURCE)
    internal annotation class ScrollState

    /*
      ---------- 一些注解 ----------
     */
}