package com.zyyoona7.demo

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.widget.SeekBar
import android.widget.Toast
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.zyyoona7.demo.activity.BaseActivity
import com.zyyoona7.demo.databinding.ActivityMain5Binding
import com.zyyoona7.demo.utils.typefaceLight
import com.zyyoona7.demo.utils.typefaceMedium
import com.zyyoona7.demo.utils.typefaceRegular
import com.zyyoona7.demo.utils.vibrateShot
import com.zyyoona7.wheel.WheelView
import com.zyyoona7.wheel.formatter.IntTextFormatter
import com.zyyoona7.wheel.listener.OnItemPositionChangedListener
import kotlinx.android.synthetic.main.activity_main5.*
import java.util.*
import kotlin.random.Random


class MainActivity : BaseActivity<ActivityMain5Binding>(), SeekBar.OnSeekBarChangeListener {

    override fun initLayoutId(): Int {
        return R.layout.activity_main5
    }

    override fun initVariables(savedInstanceState: Bundle?) {
        initWheelData()
        initDefaultWheelAttrs()
        initOperationsValue()
    }

    override fun initListeners(savedInstanceState: Bundle?) {

        binding.btnMore.setOnClickListener {
            DetailsActivity.start(this)
        }

        binding.inNormal.sbVisibleItems.setOnSeekBarChangeListener(this)
        binding.inNormal.sbLineSpacing.setOnSeekBarChangeListener(this)
        binding.inSe.sbSoundVolume.setOnSeekBarChangeListener(this)
        binding.inText.sbTextSize.setOnSeekBarChangeListener(this)
        binding.inText.sbTextBoundaryMargin.setOnSeekBarChangeListener(this)
        binding.inCurved.sbCurvedArcFactor.setOnSeekBarChangeListener(this)
        binding.inCurved.sbCurvedArcFactor.setOnSeekBarChangeListener(this)
        binding.inDivider.sbDividerHeight.setOnSeekBarChangeListener(this)
        binding.inDivider.sbDividerPadding.setOnSeekBarChangeListener(this)
        binding.inDivider.sbDividerOffset.setOnSeekBarChangeListener(this)
        binding.inEText.sbLeftTextSize.setOnSeekBarChangeListener(this)
        binding.inEText.sbRightTextSize.setOnSeekBarChangeListener(this)
        binding.inEText.sbLeftTextMarginRight.setOnSeekBarChangeListener(this)
        binding.inEText.sbRightTextMarginLeft.setOnSeekBarChangeListener(this)

        binding.inNormal.scCyclic.setOnCheckedChangeListener { _, isChecked ->
            binding.wheelview.isCyclic = isChecked
        }

        binding.inSe.scSound.setOnCheckedChangeListener { _, isChecked ->
            binding.wheelview.isSoundEffect = isChecked
        }

        binding.inCurtain.scHasCurtain.setOnCheckedChangeListener { _, isChecked ->
            binding.wheelview.isShowCurtain = isChecked
        }

        binding.inCurved.scCurved.setOnCheckedChangeListener { _, isChecked ->
            binding.wheelview.isCurved = isChecked
        }

        binding.inDivider.scShowDivider.setOnCheckedChangeListener { _, isChecked ->
            binding.wheelview.isShowDivider = isChecked
        }

        binding.inScroll.scCanOverRangeScroll.setOnCheckedChangeListener { _, isChecked ->
            binding.wheelview.canOverRangeScroll = isChecked
        }

        binding.inText.rgAlign.setOnCheckedChangeListener { _, checkedId ->
            binding.wheelview.textAlign = when (checkedId) {
                R.id.rb_align_left -> Paint.Align.LEFT
                R.id.rb_align_right -> Paint.Align.RIGHT
                else -> Paint.Align.CENTER
            }
        }

        binding.inCurved.rgDirection.setOnCheckedChangeListener { _, checkedId ->
            binding.wheelview.curvedArcDirection = when (checkedId) {
                R.id.rb_direction_left -> WheelView.CurvedArcDirection.LEFT
                R.id.rb_direction_right -> WheelView.CurvedArcDirection.RIGHT
                else -> WheelView.CurvedArcDirection.CENTER
            }
        }

        binding.inDivider.rgDividerType.setOnCheckedChangeListener { _, checkedId ->
            binding.wheelview.dividerType = when (checkedId) {
                R.id.rb_divider_fill -> WheelView.DividerType.FILL
                R.id.rb_divider_wrap_all -> WheelView.DividerType.WRAP_ALL
                else -> WheelView.DividerType.WRAP
            }
        }

        binding.inDivider.rgDividerCap.setOnCheckedChangeListener { _, checkedId ->
            binding.wheelview.dividerCap = when (checkedId) {
                R.id.rb_divider_cap_square -> Paint.Cap.SQUARE
                R.id.rb_divider_cap_butt -> Paint.Cap.BUTT
                else -> Paint.Cap.ROUND
            }
        }


        binding.inScroll.btnSelected.setOnClickListener {
            if (binding.inScroll.cbSelectedRandom.isChecked) {
                binding.wheelview.setSelectedPosition(
                        Random.nextInt(0, binding.wheelview.getAdapter()?.getItemCount() ?: 0),
                        binding.inScroll.cbSelectedSmooth.isChecked,
                        binding.inScroll.sbSelectedDuration.progress)
            } else {
                binding.wheelview.setSelectedPosition(binding.inScroll.brSelectedPosition.currentValue.toInt(),
                        binding.inScroll.cbSelectedSmooth.isChecked,
                        binding.inScroll.sbSelectedDuration.progress)
            }
        }

        binding.inCurtain.btnCurtainColor.setOnClickListener {
            showColorPicker(binding.wheelview.curtainColor) {
                binding.wheelview.curtainColor = it
                binding.inCurtain.btnCurtainColor.setBackgroundColor(it)
            }
        }

        binding.inText.btnSelectedColor.setOnClickListener {
            showColorPicker(binding.wheelview.selectedTextColor) {
                binding.wheelview.selectedTextColor = it
                binding.inText.btnSelectedColor.setBackgroundColor(it)
            }
        }

        binding.inText.btnNormalColor.setOnClickListener {
            showColorPicker(binding.wheelview.normalTextColor) {
                binding.wheelview.normalTextColor = it
                binding.inText.btnNormalColor.setBackgroundColor(it)
            }
        }

        binding.inDivider.btnDividerColor.setOnClickListener {
            showColorPicker(binding.wheelview.dividerColor) {
                binding.wheelview.dividerColor = it
                binding.inDivider.btnDividerColor.setBackgroundColor(it)
            }
        }

        binding.inText.btnFontMedium.setOnClickListener {
            binding.wheelview.setTypeface(typefaceMedium(), binding.inText.cbBoldSelected.isChecked)
        }

        binding.inText.btnFontRegular.setOnClickListener {
            binding.wheelview.setTypeface(typefaceRegular(), binding.inText.cbBoldSelected.isChecked)
        }

        binding.inText.btnFontLight.setOnClickListener {
            binding.wheelview.setTypeface(typefaceLight(), binding.inText.cbBoldSelected.isChecked)
        }

        binding.inScroll.rsbSelectedRange.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
            }

            override fun onRangeChanged(view: RangeSeekBar?, leftValue: Float,
                                        rightValue: Float, isFromUser: Boolean) {
                binding.wheelview.setSelectedRange(leftValue.toInt(), rightValue.toInt())
            }

            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
            }

        })

        binding.wheelview.setOnItemPositionChangedListener(object : OnItemPositionChangedListener {
            override fun onItemChanged(wheelView: WheelView, oldPosition: Int, newPosition: Int) {
                if (binding.inSe.scVibrate.isChecked) {
                    vibrateShot(10)
                }
            }
        })

        binding.inEText.btnExtraLeft.setOnClickListener {
            binding.wheelview.leftText = binding.inEText.etExtraLeft.text
        }

        binding.inEText.btnExtraRight.setOnClickListener {
            binding.wheelview.rightText = binding.inEText.etExtraRight.text
        }

        binding.inEText.btnExtraLeftColor.setOnClickListener {
            showColorPicker(binding.wheelview.leftTextColor) {
                binding.wheelview.leftTextColor = it
                binding.inEText.btnExtraLeftColor.setBackgroundColor(it)
            }
        }

        binding.inEText.btnExtraRightColor.setOnClickListener {
            showColorPicker(binding.wheelview.rightTextColor) {
                binding.wheelview.rightTextColor = it
                binding.inEText.btnExtraRightColor.setBackgroundColor(it)
            }
        }

        binding.inEText.rgLeftGravity.setOnCheckedChangeListener { _, checkedId ->
            binding.wheelview.leftTextGravity = when (checkedId) {
                R.id.rb_left_gravity_top -> Gravity.TOP
                R.id.rb_left_gravity_bottom -> Gravity.BOTTOM
                else -> Gravity.CENTER
            }
        }

        binding.inEText.rgRightGravity.setOnCheckedChangeListener { _, checkedId ->
            binding.wheelview.rightTextGravity = when (checkedId) {
                R.id.rb_right_gravity_top -> Gravity.TOP
                R.id.rb_right_gravity_bottom -> Gravity.BOTTOM
                else -> Gravity.CENTER
            }
        }

        binding.inNormal.scDebug.setOnCheckedChangeListener { _, isChecked ->
            wheelview.drawDebugRectEnabled = isChecked
        }

        binding.inNormal.rgGravity.setOnCheckedChangeListener { _, checkedId ->
            binding.wheelview.gravity = when (checkedId) {
                R.id.rb_gravity_center_horizontal -> Gravity.CENTER_HORIZONTAL
                else -> Gravity.CENTER
            }
        }
    }

    private fun initWheelData() {
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH) - 1
        calendar.set(Calendar.DATE, 1)
        calendar.roll(Calendar.DATE, -1)
        val monthOfDays = calendar.get(Calendar.DATE)
        val daysList = arrayListOf<Int>()
        for (i in 1..monthOfDays) {
            daysList.add(i)
        }
        binding.wheelview.setData(daysList)
        binding.wheelview.setTextFormatter(IntTextFormatter("%d日"))
        binding.wheelview.setSelectedPosition(currentDay)

        Handler().postDelayed({
            Toast.makeText(this, "选中${binding.wheelview.getSelectedItem<Int>() ?: -1}",
                    Toast.LENGTH_LONG).show()
        }, 1000)

    }

    private fun initDefaultWheelAttrs() {
        binding.wheelview.setTextSize(18f)
        binding.wheelview.setLineSpacing(10f)
        binding.wheelview.setSoundResource(R.raw.button_choose)
        binding.wheelview.getAdapter()?.getSelectedItem<Int>()
        binding.wheelview.setTextPadding(20f)
        binding.wheelview.curtainColor = Color.WHITE
    }

    private fun initOperationsValue() {
        val wheelView = binding.wheelview
        binding.inNormal.sbVisibleItems.max = 9
        binding.inNormal.sbVisibleItems.progress = wheelView.visibleItems

        binding.inNormal.scCyclic.isChecked = wheelView.isCyclic

        binding.inNormal.sbLineSpacing.max = 50
        binding.inNormal.sbLineSpacing.progress = wheelView.lineSpacing.toInt()

        binding.inScroll.sbSelectedDuration.max = 3000
        binding.inScroll.sbSelectedDuration.progress = WheelView.DEFAULT_SCROLL_DURATION

        binding.inSe.scSound.isChecked = wheelView.isSoundEffect
        binding.inSe.sbSoundVolume.max = 100
        binding.inSe.sbSoundVolume.progress = (wheelView.getSoundVolume() * 100).toInt()

        binding.inCurtain.scHasCurtain.isChecked = wheelView.isShowCurtain
        binding.inCurtain.btnCurtainColor.setBackgroundColor(wheelView.curtainColor)

        binding.inText.sbTextSize.max = 70
        binding.inText.sbTextSize.progress = wheelView.textSize

        when (wheelView.textAlign) {
            Paint.Align.LEFT -> binding.inText.rbAlignLeft.isChecked = true
            Paint.Align.RIGHT -> binding.inText.rbAlignRight.isChecked = true
            else -> binding.inText.rbAlignCenter.isChecked = true
        }

        binding.inText.btnSelectedColor.setBackgroundColor(wheelView.selectedTextColor)
        binding.inText.btnNormalColor.setBackgroundColor(wheelView.normalTextColor)

        binding.inText.sbTextBoundaryMargin.max = 150
        binding.inText.sbTextBoundaryMargin.progress = wheelView.textPaddingLeft

        binding.inText.cbBoldSelected.isChecked = wheelView.isBoldForSelectedItem()

        binding.inCurved.scCurved.isChecked = wheelView.isCurved
        when (wheelView.curvedArcDirection) {
            WheelView.CurvedArcDirection.LEFT -> binding.inCurved.rbDirectionLeft.isChecked = true
            WheelView.CurvedArcDirection.RIGHT -> binding.inCurved.rbDirectionRight.isChecked = true
            else -> binding.inCurved.rbDirectionCenter.isChecked = true
        }
        binding.inCurved.sbCurvedArcFactor.max = 100
        binding.inCurved.sbCurvedArcFactor.progress = (wheelView.curvedArcDirectionFactor * 100).toInt()
        binding.inCurved.sbCurvedArcFactor.max = 100
        binding.inCurved.sbCurvedArcFactor.progress = (wheelView.refractRatio * 100).toInt()

        binding.inDivider.scShowDivider.isChecked = wheelView.isShowDivider
        binding.inDivider.sbDividerHeight.max = 30
        binding.inDivider.sbDividerHeight.progress = wheelView.dividerHeight.toInt()
        binding.inDivider.btnDividerColor.setBackgroundColor(wheelView.dividerColor)
        when (wheelView.dividerType) {
            WheelView.DividerType.FILL -> binding.inDivider.rbDividerFill.isChecked = true
            WheelView.DividerType.WRAP_ALL -> binding.inDivider.rbDividerWrapAll.isChecked = true
            else -> binding.inDivider.rbDividerWrap.isChecked = true
        }
        binding.inDivider.sbDividerPadding.max = 60
        binding.inDivider.sbDividerPadding.progress = wheelView.dividerPadding.toInt()
        binding.inDivider.sbDividerOffset.max = 30
        binding.inDivider.sbDividerOffset.progress = wheelView.dividerOffsetY.toInt()
        when (binding.wheelview.dividerCap) {
            Paint.Cap.SQUARE -> binding.inDivider.rbDividerCapSquare.isChecked = true
            Paint.Cap.BUTT -> binding.inDivider.rbDividerCapButt.isChecked = true
            else -> binding.inDivider.rbDividerCapRound.isChecked = true
        }

        binding.inText.btnFontMedium.typeface = typefaceMedium()
        binding.inText.btnFontRegular.typeface = typefaceRegular()
        binding.inText.btnFontLight.typeface = typefaceLight()

        val maxValue = wheelView.getAdapter()?.getItemCount()?.minus(1) ?: 0
        binding.inScroll.rsbSelectedRange.setRange(0f,
                maxValue.toFloat())
        binding.inScroll.rsbSelectedRange.setProgress(0f, binding.inScroll.rsbSelectedRange.maxProgress)

        binding.inScroll.scCanOverRangeScroll.isChecked = wheelView.canOverRangeScroll

        binding.inScroll.brSelectedPosition.setValue(0f, maxValue.toFloat(), 0f, 1f, 5)

        binding.inEText.sbLeftTextMarginRight.max = 150
        binding.inEText.sbLeftTextMarginRight.progress = wheelView.leftTextMarginRight

        binding.inEText.sbRightTextMarginLeft.max = 150
        binding.inEText.sbRightTextMarginLeft.progress = wheelView.rightTextMarginLeft

        when (wheelView.leftTextGravity) {
            Gravity.TOP -> binding.inEText.rbLeftGravityTop.isChecked = true
            Gravity.BOTTOM -> binding.inEText.rbLeftGravityBottom.isChecked = true
            else -> binding.inEText.rbLeftGravityCenter.isChecked = true
        }

        when (wheelView.rightTextGravity) {
            Gravity.TOP -> binding.inEText.rbRightGravityTop.isChecked = true
            Gravity.BOTTOM -> binding.inEText.rbRightGravityBottom.isChecked = true
            else -> binding.inEText.rbRightGravityCenter.isChecked = true
        }

        binding.inEText.sbLeftTextSize.max = 70
        binding.inEText.sbLeftTextSize.progress = wheelView.leftTextSize

        binding.inEText.sbRightTextSize.max = 70
        binding.inEText.sbRightTextSize.progress = wheelView.rightTextSize

        binding.inNormal.scDebug.isChecked = wheelView.drawDebugRectEnabled

        when (wheelView.gravity) {
            Gravity.CENTER_HORIZONTAL -> binding.inNormal.rbGravityCenterHorizontal.isChecked = true
            else -> binding.inNormal.rbGravityCenter.isChecked = true
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (seekBar == null) {
            return
        }
        when (seekBar.id) {
            R.id.sb_visible_items -> binding.wheelview.visibleItems = progress
            R.id.sb_line_spacing -> binding.wheelview.lineSpacing = progress
            R.id.sb_sound_volume -> binding.wheelview.setSoundVolume(progress / 100f)
            R.id.sb_text_size -> binding.wheelview.textSize = progress
            R.id.sb_text_boundary_margin -> {
                binding.wheelview.textPaddingLeft = progress
                binding.wheelview.textPaddingRight = progress
            }
            R.id.sb_curved_arc_factor -> binding.wheelview.curvedArcDirectionFactor = progress / 100f
            R.id.sb_refract -> binding.wheelview.refractRatio = progress / 100f
            R.id.sb_divider_height -> binding.wheelview.dividerHeight = progress
            R.id.sb_divider_padding -> binding.wheelview.dividerPadding = progress
            R.id.sb_divider_offset -> binding.wheelview.dividerOffsetY = progress
            R.id.sb_left_text_size -> binding.wheelview.leftTextSize = progress
            R.id.sb_right_text_size -> binding.wheelview.rightTextSize = progress
            R.id.sb_left_text_margin_right -> binding.wheelview.leftTextMarginRight = progress
            R.id.sb_right_text_margin_left -> binding.wheelview.rightTextMarginLeft = progress
        }
    }

    private fun showColorPicker(initialColor: Int, block: (Int) -> Unit) {
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("选择颜色")
                .initialColor(initialColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setPositiveButton("设置") { _, selectedColor, _ ->
                    block.invoke(selectedColor)
                }
                .setNegativeButton("取消", null)
                .build()
                .show()
    }

}
