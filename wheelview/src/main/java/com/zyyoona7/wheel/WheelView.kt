package com.zyyoona7.wheel

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.media.AudioManager
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.util.TypedValue
import android.view.*
import android.widget.Scroller
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.IntRange
import androidx.annotation.RawRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.zyyoona7.wheel.adapter.ArrayWheelAdapter
import com.zyyoona7.wheel.adapter.ItemIndexer
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
open class WheelView @JvmOverloads constructor(context: Context,
                                               attrs: AttributeSet? = null,
                                               defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr), Runnable, ArrayWheelAdapter.OnFinishScrollCallback {

    /**
     * 画笔
     */
    private val normalPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mainTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val leftTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val rightTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    private val mainTextRect = Rect()
    private val leftTextRect = Rect()
    private val rightTextRect = Rect()
    /**
     * 文字的最大宽度
     */
    private var mainTextMaxWidth: Int = 0
    /**
     * 测量文字完成后的原始宽度
     */
    private var originTextMaxWidth: Int = 0
    private var leftTextWidth: Int = 0
    private var rightTextWidth: Int = 0
    /**
     * 每个item的高度
     */
    private var itemHeight: Int = 0
    /**
     * 文字高度
     */
    private var mainTextHeight: Int = 0
    private var leftTextHeight: Int = 0
    private var rightTextHeight: Int = 0
    /**
     * 弯曲效果时，为了适配偏移多增加的宽度
     */
    private var curvedArcWidth: Int = 0
    /**
     * 文字起始X
     */
    private var textDrawStartX: Int = 0
    /**
     * Y轴中心点
     */
    private var centerY: Int = 0
    /**
     * 选中边界的上下限制
     */
    private var selectedItemTopLimit: Int = 0
    private var selectedItemBottomLimit: Int = 0
    /**
     * 裁剪的边界
     */
    private var clipLeft: Int = 0
    private var clipTop: Int = 0
    private var clipRight: Int = 0
    private var clipBottom: Int = 0
    /**
     * 3D效果实现
     */
    private val cameraForCurved = Camera()
    private val matrixForCurved = Matrix()

    private val scroller: Scroller = Scroller(context)
    private var velocityTracker: VelocityTracker? = null
    private var maxFlingVelocity: Int = 0
    private var minFlingVelocity: Int = 0

    /**
     * 最小滚动距离，上边界
     */
    private var minScrollY: Int = 0
    /**
     * 最大滚动距离，下边界
     */
    private var maxScrollY: Int = 0

    /**
     * Y轴滚动偏移
     */
    private var scrollOffsetY: Int = 0
    /**
     * Y轴已滚动偏移，控制重绘次数
     */
    private var scrolledY = 0
    /**
     * 手指最后触摸的位置
     */
    private var lastTouchY: Float = 0f
    /**
     * 手指按下时间，根据按下抬起时间差处理点击滚动
     */
    private var downStartTime: Long = 0L
    /**
     * 是否强制停止滚动
     */
    private var isForceFinishScroll = false
    /**
     * 是否是快速滚动，快速滚动结束后跳转位置
     */
    private var isFlingScroll: Boolean = false
    private val soundHelper: SoundHelper by lazy { SoundHelper.obtain() }
    private var wheelAdapter: ArrayWheelAdapter<*>? = null

    /**
     * 当前滚动经过的下标
     */
    private var currentScrollPosition: Int = 0
    /**
     * 记录已经执行过 itemPositionChange 的滚动的下标，防止多次执行
     * position不改变得情况下只执行一次 itemPositionChanged()
     */
    private var itemChangedPosition = -1
    /**
     * 当前滚动状态
     */
    private var currentScrollState: Int = SCROLL_STATE_IDLE

    /**
     * 当前选中的下标
     */
    private var selectedPosition: Int = 0
    /*
      ---------- 文字相关 ----------
     */
    /**
     * 最大文本宽度测量模式，适当的模式可以减少文字测量时间
     */
    var maxTextWidthMeasureType: MeasureType = MeasureType.DEFAULT
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyChanged()
        }

    var gravity: Int = Gravity.CENTER
        set(value) {
            if (value == field) {
                return
            }
            field = value
            calculateTextRect()
            invalidate()
        }

    /**
     * 字体大小
     */
    var textSize: Int = DEFAULT_TEXT_SIZE
        set(value) {
            if (value == field || value <= 0) {
                return
            }
            field = value
            notifyDataSetChanged()
        }

    /**
     * 是否自动调整字体大小以显示完全
     */
    var isAutoFitTextSize: Boolean = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    /**
     * 缩放后最小字体大小
     */
    var minTextSize: Int = DEFAULT_MIN_TEXT_SIZE
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyChanged()
        }

    /**
     * 文字对齐方式
     */
    var textAlign: Paint.Align = Paint.Align.CENTER
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyTextAlignChanged()
        }

    /**
     * 未选中item文字颜色
     */
    @ColorInt
    var normalTextColor: Int = DEFAULT_NORMAL_TEXT_COLOR
        set(value) {
            if (value == field) {
                return
            }
            field = value
            invalidate()
        }

    /**
     * 选中item文字颜色
     */
    @ColorInt
    var selectedTextColor: Int = DEFAULT_SELECTED_TEXT_COLOR
        set(value) {
            if (value == field) {
                return
            }
            field = value
            invalidate()
        }

    /**
     * 文本左边距离paddingLeft的空隙，
     * 类似于 paddingLeft 但是 paddingLeft 会影响 divider 和 curtain 绘制的起始位置，
     * 这个属性只会影响 文本绘制时的起始位置
     */
    var textPaddingLeft: Int = DEFAULT_TEXT_PADDING
        set(value) {
            if (value == field) {
                return
            }
            field = value
            requestLayout()
        }

    /**
     * 文本右边距离 paddingRight 的空隙，
     * 类似于 paddingRight 但是 paddingRight 会影响 divider 和 curtain 绘制的起始位置，
     * 这个属性只会影响 文本绘制时的起始位置
     */
    var textPaddingRight: Int = DEFAULT_TEXT_PADDING
        set(value) {
            if (value == field) {
                return
            }
            field = value
            requestLayout()
        }

    /**
     * 是否设置选中字体加粗
     */
    private var isBoldForSelectedItem = false
    /**
     * 如果 isBoldForSelectedItem==true 则这个字体为未选中条目的字体
     */
    private var normalTypeface: Typeface? = null
    /**
     * 如果 isBoldForSelectedItem==true 则这个字体为选中条目的字体
     */
    private var boldTypeface: Typeface? = null

    /**
     * 是否绘制debug文字区域边界
     */
    var drawDebugRectEnabled: Boolean = false
        set(value) {
            if (value == field) {
                return
            }
            field = value
            invalidate()
        }

    /*
      ---------- 文字相关 ----------
     */

    /**
     * 可见的item条数
     */
    var visibleItems: Int = DEFAULT_VISIBLE_ITEM
        set(value) {
            val newValue = adjustVisibleItems(value)
            if (newValue == field) {
                return
            }
            field = newValue
            notifyChanged()
        }

    /**
     * 每个item之间的空间，行间距
     */
    var lineSpacing: Int = DEFAULT_LINE_SPACING
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyChanged()
        }

    /**
     * 是否循环滚动
     */
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

    /**
     * 是否显示分割线
     */
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

    /**
     * 分割线的颜色
     */
    var dividerColor: Int = DEFAULT_SELECTED_TEXT_COLOR
        set(@ColorInt value) {
            if (value == field) {
                return
            }
            field = value
            if (isShowDivider) {
                invalidate()
            }
        }

    /**
     * 分割线高度
     */
    var dividerHeight: Int = DEFAULT_DIVIDER_HEIGHT
        set(value) {
            if (value == field) {
                return
            }
            field = value
            if (isShowDivider) {
                invalidate()
            }
        }

    /**
     * 分割线填充类型
     */
    var dividerType: DividerType = DividerType.FILL
        set(value) {
            if (value == field) {
                return
            }
            field = value
            if (isShowDivider) {
                invalidate()
            }
        }

    /**
     * 分割线类型为DIVIDER_TYPE_WRAP时 分割线左右两端距离文字的间距
     */
    var dividerPadding: Int = 0
        set(value) {
            if (value == field) {
                return
            }
            field = value
            if (isShowDivider) {
                invalidate()
            }
        }

    /**
     * 分割线两端形状，默认圆头
     */
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

    /**
     * 分割线和选中区域垂直方向的偏移，实现扩大选中区域
     */
    var dividerOffsetY: Int = 0
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

    /**
     * 是否绘制选中区域
     */
    var isShowCurtain: Boolean = false
        set(value) {
            if (value == field) {
                return
            }
            field = value
            invalidate()
        }

    /**
     * 选中区域颜色
     */
    @ColorInt
    var curtainColor: Int = Color.TRANSPARENT
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

    /**
     * 是否是弯曲（3D）效果
     */
    var isCurved: Boolean = true
        set(value) {
            if (value == field) {
                return
            }
            field = value
            calculateItemHeight()
            requestLayout()
        }

    /**
     * 弯曲（3D）效果左右圆弧偏移效果方向 center 不偏移
     */
    var curvedArcDirection: CurvedArcDirection = CurvedArcDirection.CENTER
        set(value) {
            if (value == field) {
                return
            }
            field = value
            if (isCurved) {
                requestLayout()
                invalidate()
            }
        }

    /**
     * 弯曲（3D）效果左右圆弧偏移效果系数 0-1之间 越大越明显
     */
    var curvedArcDirectionFactor: Float = DEFAULT_CURVED_FACTOR
        set(value) {
            if (value == field) {
                return
            }
            field = min(1f, max(0f, value))
            if (isCurved) {
                requestLayout()
                invalidate()
            }
        }

    /**
     * 选中后折射的偏移 与字体大小的比值，1为不偏移 越小偏移越明显
     * (普通效果和3d效果都适用)
     */
    var refractRatio: Float = DEFAULT_REFRACT_RATIO
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

    /**
     * 是否开启音频效果
     */
    var isSoundEffect = false
        set(value) {
            field = value
            if (soundHelper.soundPlayVolume == 0f) {
                initDefaultVolume()
            }
        }

    /**
     * 数据变化时，是否重置选中下标到第一个位置
     */
    var isResetSelectedPosition = false

    /*
      ---------- 选中范围限制 ----------
     */

    private var maxSelectedPosition: Int = -1
    private var minSelectedPosition: Int = -1

    /**
     * 有滚动限制并且不是循环滚动时 超出限制范围是否可以继续滚动
     */
    var canOverRangeScroll: Boolean = true
        set(value) {
            field = value
            calculateLimitY()
        }

    /*
      ---------- 选中范围限制 ----------
     */

    /*
      ---------- 左右额外文字相关 ----------
     */

    var leftText: CharSequence = ""
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyChanged()
        }

    var rightText: CharSequence = ""
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyChanged()
        }

    var leftTextSize: Int = DEFAULT_TEXT_SIZE
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyChanged()
        }

    var rightTextSize: Int = DEFAULT_TEXT_SIZE
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyChanged()
        }

    var leftTextMarginRight: Int = 0
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyChanged()
        }

    var rightTextMarginLeft: Int = 0
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyChanged()
        }

    var leftTextColor: Int = Color.BLACK
        set(value) {
            if (value == field) {
                return
            }
            field = value
            invalidate()
        }

    var rightTextColor: Int = Color.BLACK
        set(value) {
            if (value == field) {
                return
            }
            field = value
            invalidate()
        }

    var leftTextGravity: Int = Gravity.CENTER
        set(value) {
            if (value == field) {
                return
            }
            field = value
            calculateLeftTextRect()
            invalidate()
        }

    var rightTextGravity: Int = Gravity.CENTER
        set(value) {
            if (value == field) {
                return
            }
            field = value
            calculateRightTextRect()
            invalidate()
        }

    /*
      ---------- 左右额外文字相关 ----------
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

    /**
     * adapter 中的 textFormatter
     */
    private var textFormatter: TextFormatter? = null
    /**
     * adapter 中的 formatter block
     */
    private var formatterBlock: ((Any?) -> String)? = null
    /**
     * adapter 中的 itemIndexer
     */
    private var itemIndexer: ItemIndexer? = null
    /**
     * adapter 中的 itemIndexerBlock
     */
    private var itemIndexerBlock: ((ArrayWheelAdapter<*>, Any?) -> Int)? = null


    /**
     * 用来保存对应下标下 重新测量过的 textSize （需设置 isAutoFitTextSize=true）
     */
    private val resizeArray: SparseArray<Float> by lazy { SparseArray<Float>() }
    /**
     * 标记 数据是否有变化，如果变化了则重新测量文字宽度
     */
    private var isDataSetChanged: Boolean = false

    companion object {
        private const val TAG = "WheelView"
        val DEFAULT_LINE_SPACING = dp2px(2f)
        val DEFAULT_TEXT_SIZE = sp2px(15f)
        val DEFAULT_MIN_TEXT_SIZE = sp2px(6f)
        val DEFAULT_TEXT_PADDING = dp2px(2f)
        val DEFAULT_DIVIDER_HEIGHT = dp2px(1f)
        const val DEFAULT_NORMAL_TEXT_COLOR = Color.DKGRAY
        const val DEFAULT_SELECTED_TEXT_COLOR = Color.BLACK
        const val DEFAULT_VISIBLE_ITEM = 5
        const val DEFAULT_SCROLL_DURATION = 250
        const val DEFAULT_CLICK_CONFIRM: Long = 120
        /**
         * 默认折射比值，通过字体大小来实现折射视觉差
         */
        const val DEFAULT_REFRACT_RATIO = 1f

        /**
         * 文字对齐方式
         */
        const val TEXT_ALIGN_LEFT = 0
        const val TEXT_ALIGN_CENTER = 1
        const val TEXT_ALIGN_RIGHT = 2

        /**
         * 滚动状态
         */
        const val SCROLL_STATE_IDLE = 0
        const val SCROLL_STATE_DRAGGING = 1
        const val SCROLL_STATE_SCROLLING = 2

        /**
         * 弯曲效果对齐方式
         */
        const val CURVED_ARC_DIRECTION_LEFT = 0
        const val CURVED_ARC_DIRECTION_CENTER = 1
        const val CURVED_ARC_DIRECTION_RIGHT = 2

        const val DEFAULT_CURVED_FACTOR = 0.75f

        /**
         * 分割线填充类型
         */
        const val DIVIDER_FILL = 0
        const val DIVIDER_WRAP = 1
        /**
         * 自适应延伸到左右额外文字处
         */
        const val DIVIDER_WRAP_ALL = 2

        /*
          ---------- 三种测量文字最大宽度的模式 ----------
         */

        /**
         * 按照相同宽度测量，即只测量一个 item 的宽度来作为数据的最大宽度
         */
        const val MEASURED_BY_SAME_WIDTH = 1
        /**
         * 按照文本最大长度测量，即最长的文本 item 的宽度就是最大宽度
         */
        const val MEASURED_BY_MAX_LENGTH = 2
        /**
         * 文本挨个测量找到最大宽度
         */
        const val MEASURED_BY_DEFAULT = 3

        /**
         * dp转换px
         *
         * @param dp dp值
         * @return 转换后的px值
         */
        @JvmStatic
        protected fun dp2px(dp: Float): Int {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().displayMetrics).toInt()
        }

        /**
         * sp转换px
         *
         * @param sp sp值
         * @return 转换后的px值
         */
        @JvmStatic
        protected fun sp2px(sp: Float): Int {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, Resources.getSystem().displayMetrics).toInt()
        }

        private fun logAdapterNull() {
            Log.e(TAG, "the WheelView adapter is null.")
        }

        @JvmStatic
        fun covertExtraGravity(gravity: Int): Int {
            return when (gravity) {
                1 -> Gravity.TOP
                2 -> Gravity.BOTTOM
                else -> Gravity.CENTER
            }
        }

        @JvmStatic
        fun convertTextAlign(align: Int): Paint.Align {
            return when (align) {
                TEXT_ALIGN_LEFT -> Paint.Align.LEFT
                TEXT_ALIGN_RIGHT -> Paint.Align.RIGHT
                else -> Paint.Align.CENTER
            }
        }

        @JvmStatic
        fun convertCurvedArcDirection(direction: Int): CurvedArcDirection {
            return when (direction) {
                CURVED_ARC_DIRECTION_LEFT -> CurvedArcDirection.LEFT
                CURVED_ARC_DIRECTION_RIGHT -> CurvedArcDirection.RIGHT
                else -> CurvedArcDirection.CENTER
            }
        }

        @JvmStatic
        fun convertDividerType(dividerType: Int): DividerType {
            return when (dividerType) {
                DIVIDER_WRAP -> DividerType.WRAP
                DIVIDER_WRAP_ALL -> DividerType.WRAP_ALL
                else -> DividerType.FILL
            }
        }
    }

    init {
        initValue(context)
        leftTextPaint.textAlign = Paint.Align.CENTER
        rightTextPaint.textAlign = Paint.Align.CENTER
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
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.WheelView)
        textSize = typedArray.getDimensionPixelSize(R.styleable.WheelView_wv_textSize, DEFAULT_TEXT_SIZE)
        isAutoFitTextSize = typedArray.getBoolean(R.styleable.WheelView_wv_autoFitTextSize, false)
        minTextSize = typedArray.getDimensionPixelSize(R.styleable.WheelView_wv_minTextSize, DEFAULT_MIN_TEXT_SIZE)
        textAlign = convertTextAlign(typedArray.getInt(R.styleable.WheelView_wv_textAlign, TEXT_ALIGN_CENTER))
        val textPadding = typedArray.getDimensionPixelSize(R.styleable.WheelView_wv_textPadding,
                DEFAULT_TEXT_PADDING)
        val textPaddingLeft = typedArray.getDimensionPixelSize(R.styleable.WheelView_wv_textPaddingLeft,
                DEFAULT_TEXT_PADDING)
        val textPaddingRight = typedArray.getDimensionPixelSize(R.styleable.WheelView_wv_textPaddingRight,
                DEFAULT_TEXT_PADDING)
        if (textPadding > 0) {
            this.textPaddingLeft = textPadding
            this.textPaddingRight = textPadding
        } else {
            this.textPaddingLeft = textPaddingLeft
            this.textPaddingRight = textPaddingRight
        }

        leftText = typedArray.getString(R.styleable.WheelView_wv_leftText) ?: ""
        rightText = typedArray.getString(R.styleable.WheelView_wv_rightText) ?: ""
        leftTextSize = typedArray.getDimensionPixelSize(R.styleable.WheelView_wv_leftTextSize, DEFAULT_TEXT_SIZE)
        rightTextSize = typedArray.getDimensionPixelSize(R.styleable.WheelView_wv_rightTextSize, DEFAULT_TEXT_SIZE)
        leftTextMarginRight = typedArray.getDimensionPixelSize(R.styleable.WheelView_wv_leftTextMarginRight, DEFAULT_TEXT_PADDING)
        rightTextMarginLeft = typedArray.getDimensionPixelSize(R.styleable.WheelView_wv_rightTextMarginLeft, DEFAULT_TEXT_PADDING)
        leftTextColor = typedArray.getColor(R.styleable.WheelView_wv_leftTextColor, DEFAULT_SELECTED_TEXT_COLOR)
        rightTextColor = typedArray.getColor(R.styleable.WheelView_wv_rightTextColor, DEFAULT_SELECTED_TEXT_COLOR)
        val leftTextGravity = typedArray.getInt(R.styleable.WheelView_wv_leftTextGravity, 0)
        val rightTextGravity = typedArray.getInt(R.styleable.WheelView_wv_rightTextGravity, 0)
        this.leftTextGravity = covertExtraGravity(leftTextGravity)
        this.rightTextGravity = covertExtraGravity(rightTextGravity)
        gravity = typedArray.getInt(R.styleable.WheelView_android_gravity, Gravity.CENTER)

        normalTextColor = typedArray.getColor(R.styleable.WheelView_wv_normalTextColor, DEFAULT_NORMAL_TEXT_COLOR)
        selectedTextColor = typedArray.getColor(R.styleable.WheelView_wv_selectedTextColor, DEFAULT_SELECTED_TEXT_COLOR)
        lineSpacing = typedArray.getDimensionPixelSize(R.styleable.WheelView_wv_lineSpacing, DEFAULT_LINE_SPACING)

        visibleItems = typedArray.getInt(R.styleable.WheelView_wv_visibleItems, DEFAULT_VISIBLE_ITEM)
        //跳转可见item为奇数
        visibleItems = adjustVisibleItems(visibleItems)
        val selectedPosition = typedArray.getInt(R.styleable.WheelView_wv_selectedPosition, 0)
        val maxSelectedPosition = typedArray.getInt(R.styleable.WheelView_wv_maxSelectedPosition, -1)
        val minSelectedPosition = typedArray.getInt(R.styleable.WheelView_wv_minSelectedPosition, -1)
        initSelectedPositionAndRange(selectedPosition, minSelectedPosition, maxSelectedPosition)

        isCyclic = typedArray.getBoolean(R.styleable.WheelView_wv_cyclic, false)

        isShowDivider = typedArray.getBoolean(R.styleable.WheelView_wv_showDivider, false)
        dividerType = convertDividerType(typedArray.getInt(R.styleable.WheelView_wv_dividerType, DIVIDER_FILL))
        dividerHeight = typedArray.getDimensionPixelSize(R.styleable.WheelView_wv_dividerHeight, DEFAULT_DIVIDER_HEIGHT)
        dividerColor = typedArray.getColor(R.styleable.WheelView_wv_dividerColor, DEFAULT_SELECTED_TEXT_COLOR)
        dividerPadding = typedArray.getDimensionPixelSize(R.styleable.WheelView_wv_dividerPadding, DEFAULT_TEXT_PADDING)

        dividerOffsetY = typedArray.getDimensionPixelSize(R.styleable.WheelView_wv_dividerOffsetY, 0)

        isShowCurtain = typedArray.getBoolean(R.styleable.WheelView_wv_isShowCurtain, false)
        curtainColor = typedArray.getColor(R.styleable.WheelView_wv_curtainColor, Color.TRANSPARENT)

        isCurved = typedArray.getBoolean(R.styleable.WheelView_wv_curved, true)
        curvedArcDirection = convertCurvedArcDirection(
                typedArray.getInt(R.styleable.WheelView_wv_curvedArcDirection, CURVED_ARC_DIRECTION_CENTER))
        curvedArcDirectionFactor = typedArray.getFloat(R.styleable.WheelView_wv_curvedArcDirectionFactor, DEFAULT_CURVED_FACTOR)
        //折射偏移默认值
        refractRatio = typedArray.getFloat(R.styleable.WheelView_wv_refractRatio, DEFAULT_REFRACT_RATIO)
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
        //测量文字宽高，计算出item高度
        calculateTextSizeAndItemHeight(isDataSetChanged)
        //Line Space算在了mItemHeight中
        val height: Int = if (isCurved) {
            (itemHeight * visibleItems * 2 / Math.PI + paddingTop + paddingBottom).toInt()
        } else {
            itemHeight * visibleItems + paddingTop + paddingBottom
        }
        val leftTextExtraWidth = if (leftText.isEmpty()) 0 else leftTextWidth + leftTextMarginRight
        val rightTextExtraWidth = if (rightText.isEmpty()) 0 else rightTextWidth + rightTextMarginLeft
        //默认主体文字是居中的，所以如果左右有额外文字的话需要保证主体文字居中，宽度计算需要根据左右文字较大的一个*2
        val leftAndRightExtraWidth = if (isDefaultGravity()) {
            max(leftTextExtraWidth, rightTextExtraWidth) * 2
        } else {
            leftTextExtraWidth + rightTextExtraWidth
        }
        var width: Int = mainTextMaxWidth + leftAndRightExtraWidth +
                textPaddingLeft + textPaddingRight + paddingLeft + paddingRight
        //根据偏移计算偏移造成的额外宽度
        if (isCurved && (curvedArcDirection == CurvedArcDirection.LEFT
                        || curvedArcDirection == CurvedArcDirection.RIGHT)) {
            val towardRange = (sin(Math.PI / 48) * height * curvedArcDirectionFactor).toInt()
            //如果偏移时候宽度足够则不需要增加宽度，如果宽度不足则增加宽度以适配偏移
            if (width <= mainTextMaxWidth + towardRange) {
                width += towardRange
                curvedArcWidth = towardRange
            } else {
                curvedArcWidth = 0
            }
        }

        val realWidth = resolveSize(width, widthMeasureSpec)
        if (width > realWidth) {
            //测量的宽度比实际宽度要大，重新设置 mainTextMaxWidth
            mainTextMaxWidth = realWidth - textPaddingLeft - textPaddingRight -
                    leftAndRightExtraWidth - paddingLeft - paddingRight - curvedArcWidth
            //最大宽度变了，则标记一下
            isDataSetChanged = true
        }
        val realHeight = resolveSize(height, heightMeasureSpec)
        setMeasuredDimension(realWidth, realHeight)

        centerY = measuredHeight / 2
        clipLeft = paddingLeft
        clipTop = paddingTop
        clipRight = measuredWidth - paddingRight
        clipBottom = measuredHeight - paddingBottom

        //根据最大文字宽度，如果设置了自适应字体大小 则重新测量一次每个item对应的文字大小
        if (isAutoFitTextSize && isDataSetChanged) {
            remeasureTextSizeForAutoFit()
        }

        calculateTopAndBottomLimit()
        calculateTextRect()
        calculateLimitY()
        calculateScrollOffsetY()
        //修正滚动边界
        correctScrollBoundary()
        isDataSetChanged = false
    }

    /**
     * 如果绘制的文本超过最大文本宽度则按照最大宽度缩放字体大小
     */
    private fun remeasureTextSizeForAutoFit() {
        wheelAdapter?.let {
            resizeArray.clear()
            for (i in 0 until it.getItemCount()) {
                val measureText = it.getItemText(it.getItemData(i))
                val textWidth = mainTextPaint.measureText(measureText).toInt()
                if (textWidth > mainTextMaxWidth) {
                    val newTextSize = resizeTextSize(measureText, textWidth)
                    resizeArray.put(i, newTextSize)
                }
            }
            //测量结束 恢复字体大小
            mainTextPaint.textSize = textSize.toFloat()
        }
    }

    /**
     * 根据字体最大宽度计算自适应字体大小
     */
    private fun resizeTextSize(measureText: String, textWidth: Int): Float {
        //通过计算得出大概的期望文字大小
        val hopeTextSize: Float = mainTextMaxWidth * 1f / textWidth * textSize
        if (hopeTextSize < minTextSize) {
            return minTextSize.toFloat()
        }
        var newTextWidth: Float
        var finalTextSize = hopeTextSize
        var isFirstDo = true
        do {
            mainTextPaint.textSize = finalTextSize
            newTextWidth = mainTextPaint.measureText(measureText)
            if (!isFirstDo) {
                finalTextSize -= 1
                if (finalTextSize < minTextSize) {
                    finalTextSize = minTextSize.toFloat()
                    break
                }
            }
            isFirstDo = false
        } while (newTextWidth > mainTextMaxWidth)
        //恢复textSize
        mainTextPaint.textSize = textSize.toFloat()
        return finalTextSize
    }

    private fun calculateTopAndBottomLimit() {
        selectedItemTopLimit = centerY - itemHeight / 2 - dividerOffsetY
        selectedItemBottomLimit = centerY + itemHeight / 2 + dividerOffsetY
    }

    /**
     * 测量文字最大所占空间和[itemHeight]
     */
    private fun calculateTextSizeAndItemHeight(isDataSetChanged: Boolean) {
        calculateLeftTextWidth()
        calculateRightTextWidth()
        //数据变化时或者mainTextMaxWidth已经变化 才重新测量文字宽高，减少计算量
        if (isDataSetChanged
                || mainTextMaxWidth <= 0
                || originTextMaxWidth != mainTextMaxWidth) {
            calculateMaxTextWidth()
        }
        calculateItemHeight()
    }

    private fun calculateLeftTextWidth() {
        if (leftText.isEmpty()) {
            leftTextWidth = 0
            leftTextHeight = 0
            return
        }
        leftTextPaint.textSize = leftTextSize.toFloat()
        leftTextWidth = leftTextPaint.measureText(leftText.toString()).toInt()
        leftTextHeight = (leftTextPaint.fontMetrics.bottom - leftTextPaint.fontMetrics.top).toInt()
    }

    private fun calculateRightTextWidth() {
        if (rightText.isEmpty()) {
            rightTextWidth = 0
            rightTextHeight = 0
            return
        }
        rightTextPaint.textSize = rightTextSize.toFloat()
        rightTextWidth = rightTextPaint.measureText(rightText.toString()).toInt()
        rightTextHeight = (rightTextPaint.fontMetrics.bottom - rightTextPaint.fontMetrics.top).toInt()
    }

    /**
     * 测量文字最大所占空间
     */
    private fun calculateMaxTextWidth() {
        wheelAdapter?.let {
            if (it.getItemCount() == 0) {
                return
            }
            //重新测量时 清除之前计算的值
            mainTextMaxWidth = 0
            mainTextPaint.textSize = textSize.toFloat()
            if (maxTextWidthMeasureType == MeasureType.SAME_WIDTH) {
                mainTextMaxWidth = mainTextPaint.measureText(it.getItemText(it.getItemData(0))).toInt()
            } else {
                var maxLength = -1
                for (i in 0 until it.getItemCount()) {
                    val text = it.getItemText(it.getItemData(i))
                    //按照文字长度测量宽度，可以减少测量耗时
                    if (maxTextWidthMeasureType == MeasureType.MAX_LENGTH
                            && text.length <= maxLength) {
                        continue
                    }
                    maxLength = text.length
                    val textWidth = mainTextPaint.measureText(text).toInt()
                    mainTextMaxWidth = max(textWidth, mainTextMaxWidth)
                }
            }
            //保存测量完的文字最大宽度
            originTextMaxWidth = mainTextMaxWidth
            mainTextHeight = (mainTextPaint.fontMetrics.bottom - mainTextPaint.fontMetrics.top).toInt()
        } ?: logAdapterNull()
    }

    private fun calculateTextRect() {
        val centerY = measuredHeight / 2
        val leftTextExtraWidth = if (leftText.isEmpty()) 0 else leftTextWidth + leftTextMarginRight
        val rightTextExtraWidth = if (rightText.isEmpty()) 0 else rightTextWidth + rightTextMarginLeft

        val mainLeft = when (gravity) {
            Gravity.START -> paddingLeft + textPaddingLeft +
                    leftTextExtraWidth + curvedArcWidth / 2
            Gravity.END -> measuredWidth - paddingRight -
                    rightTextExtraWidth - curvedArcWidth / 2 - mainTextMaxWidth
            Gravity.CENTER_HORIZONTAL -> (measuredWidth - leftTextExtraWidth -
                    mainTextMaxWidth - rightTextExtraWidth) / 2 + leftTextExtraWidth
            else -> measuredWidth / 2 - mainTextMaxWidth / 2
        }
        val mainTop = centerY - mainTextHeight / 2
        mainTextRect.set(mainLeft, mainTop,
                mainLeft + mainTextMaxWidth, mainTop + mainTextHeight)

        calculateLeftTextRect()
        calculateRightTextRect()

        calculateDrawStart()
    }

    private fun calculateLeftTextRect() {
        if (leftText.isEmpty()) {
            return
        }
        val leftTextLeft = mainTextRect.left - leftTextMarginRight - leftTextWidth
        val leftTextTop = when (leftTextGravity) {
            Gravity.TOP -> mainTextRect.top
            Gravity.BOTTOM -> mainTextRect.bottom - leftTextHeight
            else -> mainTextRect.centerY() - leftTextHeight / 2
        }
        leftTextRect.set(leftTextLeft, leftTextTop,
                leftTextLeft + leftTextWidth, leftTextTop + leftTextHeight)
    }

    private fun calculateRightTextRect() {
        if (rightText.isEmpty()) {
            return
        }
        val rightTextLeft = mainTextRect.left + mainTextMaxWidth + rightTextMarginLeft
        val rightTextTop = when (rightTextGravity) {
            Gravity.TOP -> mainTextRect.top
            Gravity.BOTTOM -> mainTextRect.bottom - rightTextHeight
            else -> mainTextRect.centerY() - rightTextHeight / 2
        }
        rightTextRect.set(rightTextLeft, rightTextTop,
                rightTextLeft + rightTextWidth, rightTextTop + rightTextHeight)
    }

    private fun isDefaultGravity(): Boolean {
        return gravity != Gravity.START && gravity != Gravity.END
                && gravity != Gravity.CENTER_HORIZONTAL
    }

    /**
     * 根据字体大小和行间距计算 [itemHeight]
     */
    private fun calculateItemHeight() {
        //itemHeight实际等于字体高度+一个行间距
        itemHeight = (mainTextPaint.fontMetrics.bottom
                - mainTextPaint.fontMetrics.top + lineSpacing).toInt()
    }

    /**
     * 起算起始位置
     */
    private fun calculateDrawStart() {
        textDrawStartX = when (textAlign) {
            Paint.Align.LEFT -> mainTextRect.left
            Paint.Align.RIGHT -> mainTextRect.right
            else -> mainTextRect.centerX()
        }
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
        mainTextPaint.textAlign = textAlign
    }

    /*
      ---------- 绘制部分 ---------
     */

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            //绘制选中区域
            drawCurtainRect(it)
            //绘制分割线
            drawDivider(it)
            //绘制额外的左右文字
            drawExtraText(it)

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
                    drawCurvedItem(it, i, scrolledOffset, scrolledItem)
                } else {
                    drawNormalItem(it, i, scrolledOffset, scrolledItem)
                }
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
        normalPaint.color = curtainColor
        canvas.drawRect(clipLeft.toFloat(), selectedItemTopLimit.toFloat(),
                clipRight.toFloat(), selectedItemBottomLimit.toFloat(), normalPaint)
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
        normalPaint.color = dividerColor
        val originStrokeWidth = normalPaint.strokeWidth
        normalPaint.strokeJoin = Paint.Join.ROUND
        normalPaint.strokeCap = dividerCap
        normalPaint.strokeWidth = dividerHeight.toFloat()
        val startX: Float
        val stopX: Float
        when (dividerType) {
            DividerType.WRAP -> {
                val wStartX = mainTextRect.left - dividerPadding
                val wStopX = mainTextRect.right + dividerPadding
                //边界处理 超过边界直接按照DIVIDER_TYPE_FILL类型处理
                startX = if (wStartX < clipLeft) clipLeft.toFloat() else wStartX.toFloat()
                stopX = if (wStopX > clipRight) clipRight.toFloat() else wStopX.toFloat()
            }
            DividerType.WRAP_ALL -> {
                val aStartX = mainTextRect.left - leftTextWidth - leftTextMarginRight - dividerPadding
                val aStopX = mainTextRect.right + rightTextWidth + rightTextMarginLeft + dividerPadding
                //边界处理 超过边界直接按照DIVIDER_TYPE_FILL类型处理
                startX = if (aStartX < clipLeft) clipLeft.toFloat() else aStartX.toFloat()
                stopX = if (aStopX > clipRight) clipRight.toFloat() else aStopX.toFloat()
            }
            else -> {
                startX = clipLeft.toFloat()
                stopX = clipRight.toFloat()
            }
        }
        canvas.drawLine(startX, selectedItemTopLimit.toFloat(),
                stopX, selectedItemTopLimit.toFloat(), normalPaint)
        canvas.drawLine(startX, selectedItemBottomLimit.toFloat(),
                stopX, selectedItemBottomLimit.toFloat(), normalPaint)
        normalPaint.strokeWidth = originStrokeWidth
    }

    /**
     * 绘制左右额外文字
     */
    private fun drawExtraText(canvas: Canvas) {
        //debug 绘制边界
        if (drawDebugRectEnabled) {
            val color = leftTextPaint.color
            leftTextPaint.color = Color.BLUE
            canvas.drawRect(mainTextRect, leftTextPaint)
            leftTextPaint.color = Color.RED
            canvas.drawRect(leftTextRect, leftTextPaint)
            leftTextPaint.color = Color.GREEN
            canvas.drawRect(rightTextRect, leftTextPaint)
            leftTextPaint.color = color
        }
        drawExtraLeftText(canvas)
        drawExtraRightText(canvas)
    }

    private fun drawExtraLeftText(canvas: Canvas) {
        if (leftText.isEmpty()) {
            return
        }
        leftTextPaint.textSize = leftTextSize.toFloat()
        leftTextPaint.color = leftTextColor
        val centerToBaselineY = centerToBaselineY(leftTextPaint)
        canvas.drawText(leftText, 0, leftText.length, leftTextRect.centerX().toFloat(),
                (leftTextRect.centerY() - centerToBaselineY).toFloat(), leftTextPaint)
    }

    private fun drawExtraRightText(canvas: Canvas) {
        if (rightText.isEmpty()) {
            return
        }
        rightTextPaint.textSize = rightTextSize.toFloat()
        rightTextPaint.color = rightTextColor
        val centerToBaselineY = centerToBaselineY(rightTextPaint)
        canvas.drawText(rightText, 0, rightText.length, rightTextRect.centerX().toFloat(),
                (rightTextRect.centerY() - centerToBaselineY).toFloat(), rightTextPaint)
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
        val startX = this.textDrawStartX
        //重新测量字体宽度和基线偏移
        val centerToBaselineY = getCenterToBaselineY(index)

        if (abs(item2CenterOffsetY) <= 0) {
            //绘制选中的条目
            mainTextPaint.color = selectedTextColor
            clipAndDrawNormalText(canvas, text, selectedItemTopLimit,
                    selectedItemBottomLimit, item2CenterOffsetY, centerToBaselineY)
        } else if (item2CenterOffsetY in 1 until itemHeight) {
            //绘制与下边界交汇的条目
            mainTextPaint.color = selectedTextColor
            clipAndDrawNormalText(canvas, text, selectedItemTopLimit,
                    selectedItemBottomLimit, item2CenterOffsetY, centerToBaselineY)

            mainTextPaint.color = normalTextColor
            //缩小字体，实现折射效果
            val textSize = mainTextPaint.textSize
            mainTextPaint.textSize = textSize * refractRatio
            //mIsBoldForSelectedItem==true 改变字体
            changeTypefaceIfBoldForSelectedItem()
            clipAndDrawNormalText(canvas, text, selectedItemBottomLimit, clipBottom,
                    item2CenterOffsetY, centerToBaselineY)
            mainTextPaint.textSize = textSize
            //isBoldForSelectedItem==true 恢复字体
            resetTypefaceIfBoldForSelectedItem()
        } else if (item2CenterOffsetY < 0 && item2CenterOffsetY > -itemHeight) {
            //绘制与上边界交汇的条目
            mainTextPaint.color = selectedTextColor
            clipAndDrawNormalText(canvas, text, selectedItemTopLimit,
                    selectedItemBottomLimit, item2CenterOffsetY, centerToBaselineY)

            mainTextPaint.color = normalTextColor
            //缩小字体，实现折射效果
            val textSize = mainTextPaint.textSize
            mainTextPaint.textSize = textSize * refractRatio
            //isBoldForSelectedItem==true 改变字体
            changeTypefaceIfBoldForSelectedItem()
            clipAndDrawNormalText(canvas, text, clipTop, selectedItemTopLimit,
                    item2CenterOffsetY, centerToBaselineY)
            mainTextPaint.textSize = textSize
            //isBoldForSelectedItem==true 恢复字体
            resetTypefaceIfBoldForSelectedItem()
        } else {
            //绘制其他条目
            mainTextPaint.color = normalTextColor
            //缩小字体，实现折射效果
            val textSize = mainTextPaint.textSize
            mainTextPaint.textSize = textSize * refractRatio
            //isBoldForSelectedItem==true 改变字体
            changeTypefaceIfBoldForSelectedItem()
            clipAndDrawNormalText(canvas, text, clipTop, clipBottom,
                    item2CenterOffsetY, centerToBaselineY)
            mainTextPaint.textSize = textSize
            //mIsBoldForSelectedItem==true 恢复字体
            resetTypefaceIfBoldForSelectedItem()
        }

        if (isAutoFitTextSize) {
            //恢复重新测量之前的样式
            mainTextPaint.textSize = textSize.toFloat()
            this.textDrawStartX = startX
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
        canvas.drawText(text, 0, text.length, textDrawStartX.toFloat(),
                (centerY + item2CenterOffsetY - centerToBaselineY).toFloat(), mainTextPaint)
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
        val startX = this.textDrawStartX
        //重新测量字体宽度和基线偏移
        val centerToBaselineY = getCenterToBaselineY(index)
        if (abs(item2CenterOffsetY) <= 0) {
            //绘制选中的条目
            mainTextPaint.color = selectedTextColor
            mainTextPaint.alpha = 255
            clipAndDrawCurvedText(canvas, text, selectedItemTopLimit, selectedItemBottomLimit,
                    rotateX, translateY, translateZ, centerToBaselineY)
        } else if (item2CenterOffsetY in 1 until itemHeight) {
            //绘制与下边界交汇的条目
            mainTextPaint.color = selectedTextColor
            mainTextPaint.alpha = 255
            clipAndDrawCurvedText(canvas, text, selectedItemTopLimit, selectedItemBottomLimit,
                    rotateX, translateY, translateZ, centerToBaselineY)

            mainTextPaint.color = normalTextColor
            mainTextPaint.alpha = alpha
            //缩小字体，实现折射效果
            val textSize = mainTextPaint.textSize
            mainTextPaint.textSize = textSize * refractRatio
            //isBoldForSelectedItem==true 改变字体
            changeTypefaceIfBoldForSelectedItem()
            //字体变化，重新计算距离基线偏移
            val reCenterToBaselineY = centerToBaselineY(mainTextPaint)
            clipAndDrawCurvedText(canvas, text, selectedItemBottomLimit, clipBottom,
                    rotateX, translateY, translateZ, reCenterToBaselineY)
            mainTextPaint.textSize = textSize
            //isBoldForSelectedItem==true 恢复字体
            resetTypefaceIfBoldForSelectedItem()
        } else if (item2CenterOffsetY < 0 && item2CenterOffsetY > -itemHeight) {
            //绘制与上边界交汇的条目
            mainTextPaint.color = selectedTextColor
            mainTextPaint.alpha = 255
            clipAndDrawCurvedText(canvas, text, selectedItemTopLimit, selectedItemBottomLimit,
                    rotateX, translateY, translateZ, centerToBaselineY)

            mainTextPaint.color = normalTextColor
            mainTextPaint.alpha = alpha

            //缩小字体，实现折射效果
            val textSize = mainTextPaint.textSize
            mainTextPaint.textSize = textSize * refractRatio
            //isBoldForSelectedItem==true 改变字体
            changeTypefaceIfBoldForSelectedItem()
            //字体变化，重新计算距离基线偏移
            val reCenterToBaselineY = centerToBaselineY(mainTextPaint)
            clipAndDrawCurvedText(canvas, text, clipTop, selectedItemTopLimit,
                    rotateX, translateY, translateZ, reCenterToBaselineY)
            mainTextPaint.textSize = textSize
            //isBoldForSelectedItem==true 恢复字体
            resetTypefaceIfBoldForSelectedItem()
        } else {
            //绘制其他条目
            mainTextPaint.color = normalTextColor
            mainTextPaint.alpha = alpha

            //缩小字体，实现折射效果
            val textSize = mainTextPaint.textSize
            mainTextPaint.textSize = textSize * refractRatio
            //isBoldForSelectedItem==true 改变字体
            changeTypefaceIfBoldForSelectedItem()
            //字体变化，重新计算距离基线偏移
            val reCenterToBaselineY = centerToBaselineY(mainTextPaint)
            clipAndDrawCurvedText(canvas, text, clipTop, clipBottom,
                    rotateX, translateY, translateZ, reCenterToBaselineY)
            mainTextPaint.textSize = textSize
            //isBoldForSelectedItem==true 恢复字体
            resetTypefaceIfBoldForSelectedItem()
        }

        if (isAutoFitTextSize) {
            //恢复重新测量之前的样式
            mainTextPaint.textSize = textSize.toFloat()
            this.textDrawStartX = startX
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
        //根据弯曲（3d）对齐方式设置系数
        val textCenterX = mainTextRect.centerX()
        val centerX: Float = when (curvedArcDirection) {
            CurvedArcDirection.LEFT -> textCenterX * (1 + curvedArcDirectionFactor)
            CurvedArcDirection.RIGHT -> textCenterX * (1 - curvedArcDirectionFactor)
            else -> textCenterX.toFloat()
        }

        val centerY = centerY + offsetY
        matrixForCurved.preTranslate(-centerX, -centerY)
        matrixForCurved.postTranslate(centerX, centerY)

        canvas.concat(matrixForCurved)
        canvas.drawText(text, 0, text.length, textDrawStartX.toFloat(),
                centerY - centerToBaselineY, mainTextPaint)

    }

    /**
     * 获取 centerToBaselineY 如果是自适应字体大小则设置自适应的textSize
     */
    private fun getCenterToBaselineY(index: Int): Int {
        return if (isAutoFitTextSize) {
            resizeArray.get(index)?.let {
                mainTextPaint.textSize = it
                //高度起点也变了
                centerToBaselineY(mainTextPaint)
            } ?: centerToBaselineY(mainTextPaint)
        } else {
            centerToBaselineY(mainTextPaint)
        }
    }

    private fun centerToBaselineY(paint: Paint): Int {
        val fontMetrics = paint.fontMetrics
        return (fontMetrics.ascent + (fontMetrics.descent - fontMetrics.ascent) / 2).toInt()
    }

    private fun changeTypefaceIfBoldForSelectedItem() {
        if (isBoldForSelectedItem) {
            mainTextPaint.typeface = normalTypeface
        }
    }

    private fun resetTypefaceIfBoldForSelectedItem() {
        if (isBoldForSelectedItem) {
            mainTextPaint.typeface = boldTypeface
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
                    scroller.fling(0, scrollOffsetY, 0, -velocityY, 0, 0,
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
        correctScrollBoundary()
    }

    /**
     * 修正滚动边界
     */
    private fun correctScrollBoundary() {
        //循环滚动的话 最大值和最小值都是 int 类型的边界，
        //小于 min value 则会变成正数
        //大于 max value 则会变成负数
        if (!isCyclic) {
            //修正边界
            if (scrollOffsetY < minScrollY) {
                scrollOffsetY = minScrollY
            } else if (scrollOffsetY > maxScrollY) {
                scrollOffsetY = maxScrollY
            }
        }
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

    /**
     * 设置自己实现的 [ItemIndexer] 索引器，以便 adapter.indexOf() 时调用
     */
    fun setItemIndexer(itemIndexer: ItemIndexer) {
        this.itemIndexer = itemIndexer
        wheelAdapter?.let {
            it.itemIndexer = this.itemIndexer
        }
    }

    /**
     * 设置自己实现的 [ItemIndexer] 索引器，以便 adapter.indexOf() 时调用
     */
    fun setItemIndexer(indexerBlock: (ArrayWheelAdapter<*>, Any?) -> Int) {
        this.itemIndexerBlock = indexerBlock
        wheelAdapter?.let {
            it.itemIndexerBlock = this.itemIndexerBlock
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

    /**
     * 刷新变化 此方法不会重新测量文字
     */
    private fun notifyChanged() {
        wheelAdapter?.let {
            //强制滚动完成
            forceFinishScroll()
            //to 自己：requestLayout 如果当前 view 的大小不变则不会触发 onDraw() 方法
            requestLayout()
            //to 自己：不用担心 onDraw() 方法执行两次，因为只有在下一帧绘制的时候才会执行 onDraw()
            invalidate()
        }
    }

    /**
     * 数据有变化，此方法会重新测量文字
     */
    private fun notifyDataSetChanged() {
        wheelAdapter?.let {
            isDataSetChanged = true
            //强制滚动完成
            forceFinishScroll()
            //to 自己：requestLayout 如果当前 view 的大小不变则不会触发 onDraw() 方法
            requestLayout()
            //to 自己：不用担心 onDraw() 方法执行两次，因为只有在下一帧绘制的时候才会执行 onDraw()
            invalidate()
        }
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
    fun setTextSize(textSizeSp: Float) {
        this.textSize = sp2px(textSizeSp)
    }

    /**
     * 打开自适应字体大小后 设置最小缩放字体大小
     */
    fun setMinTextSize(minTextSizeSp: Float) {
        this.minTextSize = sp2px(minTextSizeSp)
    }

    /**
     * 设置字体
     */
    @JvmOverloads
    fun setTypeface(typeface: Typeface, isBoldForSelectedItem: Boolean = false) {
        if (typeface == mainTextPaint.typeface && isBoldForSelectedItem == this.isBoldForSelectedItem) {
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
            mainTextPaint.typeface = boldTypeface
        } else {
            mainTextPaint.typeface = typeface
        }
        notifyChanged()
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
     * 设置文本距离左右padding的距离
     */
    fun setTextPadding(textPaddingDp: Float) {
        val padding = dp2px(textPaddingDp)
        this.textPaddingLeft = padding
        this.textPaddingRight = padding
    }

    /**
     * 设置文本左边距距离 paddingLeft 的距离
     */
    fun setTextPaddingLeft(textPaddingLeftDp: Float) {
        this.textPaddingLeft = dp2px(textPaddingLeftDp)
    }

    /**
     * 设置文本右边距距离 paddingRight 的距离
     */
    fun setTextPaddingRight(textPaddingRightDp: Float) {
        this.textPaddingRight = dp2px(textPaddingRightDp)
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
    fun setLineSpacing(lineSpacingDp: Float) {
        this.lineSpacing = dp2px(lineSpacingDp)
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
    fun setDividerHeight(dividerHeightDp: Float) {
        this.dividerHeight = dp2px(dividerHeightDp)
    }

    /**
     * 设置分割线类型为 [DIVIDER_WRAP] 时，分割线与文字的左右内边距
     */
    fun setDividerPadding(dividerPaddingDp: Float) {
        this.dividerPadding = dp2px(dividerPaddingDp)
    }

    /**
     * 分割线和选中区域垂直方向的偏移，实现扩大选中区域
     */
    fun setDividerOffsetY(offsetYDp: Float) {
        dividerOffsetY = dp2px(offsetYDp)
    }

    /**
     * 选中区域颜色
     */
    fun setCurtainColorRes(@ColorRes curtainColorRes: Int) {
        curtainColor = ContextCompat.getColor(context, curtainColorRes)
    }

    /*
      ---------- 额外文字相关属性 ----------
     */

    /**
     * 设置左侧文字大小 dp
     */
    fun setLeftTextSize(textSizeSp: Float) {
        leftTextSize = sp2px(textSizeSp)
    }

    /**
     * 设置右侧文字大小 dp
     */
    fun setRightTextSize(textSizeSp: Float) {
        rightTextSize = sp2px(textSizeSp)
    }

    /**
     * 设置左侧文字字体
     */
    fun setLeftTypeface(typeface: Typeface) {
        if (typeface == leftTextPaint.typeface) {
            return
        }
        leftTextPaint.typeface = typeface
        notifyChanged()
    }

    /**
     * 设置右侧文字字体
     */
    fun setRightTypeface(typeface: Typeface) {
        if (typeface == rightTextPaint.typeface) {
            return
        }
        rightTextPaint.typeface = typeface
        notifyChanged()
    }

    /**
     * 设置左侧文本字体颜色
     */
    fun setLeftTextColorRes(@ColorRes leftTextColorRes: Int) {
        leftTextColor = ContextCompat.getColor(context, leftTextColorRes)
    }

    /**
     * 设置右侧文本字体颜色
     */
    fun setRightTextColorRes(@ColorRes rightTextColorRes: Int) {
        rightTextColor = ContextCompat.getColor(context, rightTextColorRes)
    }

    /**
     * 设置左侧文字距离主体文字的距离
     */
    fun setLeftTextMarginRight(marginRightDp: Float) {
        this.leftTextMarginRight = dp2px(marginRightDp)
    }

    /**
     * 设置右侧文字距离主体文字距离
     */
    fun setRightTextMarginLeft(marginLeftDp: Float) {
        this.rightTextMarginLeft = dp2px(marginLeftDp)
    }

    /*
      ---------- 额外文字相关属性 ----------
     */

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
            if (itemHeight == 0) {
                //此时还没有测量结束，直接赋值selctedPosition
                selectedPosition = realPosition
                currentScrollPosition = realPosition
                wheelAdapter?.let {
                    it.selectedItemPosition = selectedPosition
                    //选中条目回调
                    onItemSelected(it, selectedPosition)
                    itemSelectedListener?.onItemSelected(this, it, selectedPosition)
                }
            }
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
        return maxSelectedPosition >= 0 && maxSelectedPosition < adapter.getItemCount()
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
        if (min < 0 && max < 0) {
            minSelectedPosition = -1
            maxSelectedPosition = -1
            calculateLimitY()
            return
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

    /**
     * 获取 item height
     */
    protected fun getItemHeight(): Int {
        return itemHeight
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

    protected open fun onWheelScrollStateChanged(state: Int) {

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
      ---------- 一些枚举 ----------
     */

    /**
     * 自定义左右圆弧效果方向枚举
     *
     *
     * @see [CURVED_ARC_DIRECTION_LEFT]
     * @see [CURVED_ARC_DIRECTION_CENTER]
     * @see [CURVED_ARC_DIRECTION_RIGHT]
     */
    enum class CurvedArcDirection { LEFT, CENTER, RIGHT }

    /**
     * 自定义分割线类型枚举
     *
     *
     * @see [DIVIDER_FILL]
     * @see [DIVIDER_WRAP]
     * @see [DIVIDER_WRAP_ALL]
     */
    enum class DividerType { FILL, WRAP, WRAP_ALL }

    /**
     * 自定义最大文字宽度测量类型枚举
     *
     * @see [MEASURED_BY_SAME_WIDTH]
     * @see [MEASURED_BY_MAX_LENGTH]
     * @see [MEASURED_BY_DEFAULT]
     */
    enum class MeasureType { SAME_WIDTH, MAX_LENGTH, DEFAULT }

    /*
      ---------- 一些枚举 ----------
     */
}
