package com.zyyoona7.wheel

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.VelocityTracker
import android.view.View
import android.widget.Scroller
import com.zyyoona7.wheel.adapter.BaseWheelAdapter
import com.zyyoona7.wheel.sound.SoundHelper

open class WheelViewKt(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr), Runnable {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    //每个item的高度
    private var itemHeight: Int = 0
    //文字的最大宽度
    private var maxTextWidth: Int = 0
    //文字中心距离baseline的距离
    private var centerToBaselineY: Int = 0
    private var fontMetrics: Paint.FontMetrics? = null
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
    private val crawRect = Rect()
    //3D效果
    private val cameraForCurved = Camera()
    private val matrixForCurved = Matrix()

    private lateinit var scroller: Scroller
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
    private lateinit var wheelAdapter:BaseWheelAdapter<*>

    //属性
    /*
      ---------- 文字相关 ----------
     */
    //字体大小
    var textSize: Float = 0f
    //是否自动调整字体大小以显示完全
    var isAutoFitTextSize: Boolean = false
    //文字对齐方式
    var textAlign: Int = 0
    //未选中item文字颜色
    var normalTextColor: Int = 0
    //选中item文字颜色
    var selectedItemTextColor: Int = 0
    //字体
    var isBoldForSelectedItem = false
    //如果 mIsBoldForSelectedItem==true 则这个字体为未选中条目的字体
    var normalTypeface: Typeface? = null
    //如果 mIsBoldForSelectedItem==true 则这个字体为选中条目的字体
    var boldTypeface: Typeface? = null
    //字体外边距，目的是留有边距
    var textBoundaryMargin: Float = 0f
    /*
      ---------- 文字相关 ----------
     */
    /*
      ---------- 分割线相关 ----------
     */
    //是否显示分割线
    var isShowDivider: Boolean = false
    //分割线的颜色
    var dividerColor: Int = 0
    //分割线高度
    var dividerSize: Float = 0.toFloat()
    //分割线填充类型
    var dividerType: Int = 0
    //分割线类型为DIVIDER_TYPE_WRAP时 分割线左右两端距离文字的间距
    var dividerPaddingForWrap: Float = 0.toFloat()
    //分割线两端形状，默认圆头
    var dividerCap: Paint.Cap = Paint.Cap.ROUND
    //分割线和选中区域偏移，实现扩大选中区域
    var dividerOffset: Float = 0f
    /*
      ---------- 分割线相关 ----------
     */
    /*
      ---------- 选中区域蒙层相关 ----------
     */
    //是否绘制选中区域
    var hasCurtain: Boolean = false
    //选中区域颜色
    var curtainColor: Int = 0
    /*
      ---------- 选中区域蒙层相关 ----------
     */
    /*
      ---------- 3D效果相关 ----------
     */
    //是否是弯曲（3D）效果
    var isCurved: Boolean = false
    //弯曲（3D）效果左右圆弧偏移效果方向 center 不偏移
    var curvedArcDirection: Int = 0
    //弯曲（3D）效果左右圆弧偏移效果系数 0-1之间 越大越明显
    var curvedArcDirectionFactor: Float = 0f
    //选中后折射的偏移 与字体大小的比值，1为不偏移 越小偏移越明显
    //(普通效果和3d效果都适用)
    var refractRatio: Float = 0f
    //数据变化时，是否重置选中下标到第一个位置
    var isResetSelectedPosition = false
    /*
      ---------- 3D效果相关 ----------
     */

    init {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    override fun run() {

    }

//    fun <T> setData(data:List<T>){
//        setAdapter(BaseWheelAdapter<T>())
//        wheelAdapter.setData(data)
//    }
//    fun <T> setAdapter(adapter: BaseWheelAdapter<T>){
//        wheelAdapter=adapter
//    }
}