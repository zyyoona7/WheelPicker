package com.zyyoona7.demo

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.widget.SeekBar
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.zyyoona7.demo.activity.BaseActivity
import com.zyyoona7.demo.databinding.ActivityMain5Binding
import com.zyyoona7.demo.utils.typefaceLight
import com.zyyoona7.demo.utils.typefaceMedium
import com.zyyoona7.demo.utils.typefaceRegular
import com.zyyoona7.wheel.WheelViewKt
import com.zyyoona7.wheel.formatter.IntTextFormatter
import java.util.*
import kotlin.random.Random


class Main5Activity : BaseActivity<ActivityMain5Binding>(), SeekBar.OnSeekBarChangeListener {
    override fun initLayoutId(): Int {
        return R.layout.activity_main5
    }

    override fun initVariables(savedInstanceState: Bundle?) {
        initWheelData()
        initDefaultWheelAttrs()
        initOperationsValue()
    }

    override fun initListeners(savedInstanceState: Bundle?) {

        binding.sbVisibleItems.setOnSeekBarChangeListener(this)
        binding.sbLineSpacing.setOnSeekBarChangeListener(this)
        binding.sbSoundVolume.setOnSeekBarChangeListener(this)
        binding.sbTextSize.setOnSeekBarChangeListener(this)
        binding.sbTextBoundaryMargin.setOnSeekBarChangeListener(this)
        binding.sbCurvedArcFactor.setOnSeekBarChangeListener(this)
        binding.sbRefract.setOnSeekBarChangeListener(this)
        binding.sbDividerHeight.setOnSeekBarChangeListener(this)
        binding.sbDividerPadding.setOnSeekBarChangeListener(this)
        binding.sbDividerOffset.setOnSeekBarChangeListener(this)

        binding.scCyclic.setOnCheckedChangeListener { _, isChecked ->
            binding.wheelview.isCyclic = isChecked
        }

        binding.scSound.setOnCheckedChangeListener { _, isChecked ->
            binding.wheelview.isSoundEffect = isChecked
        }

        binding.scHasCurtain.setOnCheckedChangeListener { _, isChecked ->
            binding.wheelview.hasCurtain = isChecked
        }

        binding.scCurved.setOnCheckedChangeListener { _, isChecked ->
            binding.wheelview.isCurved = isChecked
        }

        binding.scShowDivider.setOnCheckedChangeListener { _, isChecked ->
            binding.wheelview.isShowDivider = isChecked
        }

        binding.rgAlign.setOnCheckedChangeListener { _, checkedId ->
            binding.wheelview.textAlign = when (checkedId) {
                R.id.rb_align_left -> WheelViewKt.TEXT_ALIGN_LEFT
                R.id.rb_align_right -> WheelViewKt.TEXT_ALIGN_RIGHT
                else -> WheelViewKt.TEXT_ALIGN_CENTER
            }
        }

        binding.rgDirection.setOnCheckedChangeListener { _, checkedId ->
            binding.wheelview.curvedArcDirection = when (checkedId) {
                R.id.rb_direction_left -> WheelViewKt.CURVED_ARC_DIRECTION_LEFT
                R.id.rb_direction_right -> WheelViewKt.CURVED_ARC_DIRECTION_RIGHT
                else -> WheelViewKt.CURVED_ARC_DIRECTION_CENTER
            }
        }

        binding.rgDividerType.setOnCheckedChangeListener { _, checkedId ->
            binding.wheelview.dividerType = when (checkedId) {
                R.id.rb_divider_fill -> WheelViewKt.DIVIDER_TYPE_FILL
                else -> WheelViewKt.DIVIDER_TYPE_WRAP
            }
        }

        binding.rgDividerCap.setOnCheckedChangeListener { _, checkedId ->
            binding.wheelview.dividerCap = when (checkedId) {
                R.id.rb_divider_cap_square -> Paint.Cap.SQUARE
                R.id.rb_divider_cap_butt -> Paint.Cap.BUTT
                else -> Paint.Cap.ROUND
            }
        }

        binding.btnSelected.setOnClickListener {
            binding.wheelview.setSelectedPosition(
                    Random.nextInt(0, binding.wheelview.getAdapter()?.getItemCount() ?: 0),
                    binding.cbSelectedSmooth.isChecked,
                    binding.sbSelectedDuration.progress)
        }

        binding.btnCurtainColor.setOnClickListener {
            showColorPicker(binding.wheelview.curtainColor) {
                binding.wheelview.curtainColor = it
                binding.btnCurtainColor.setBackgroundColor(it)
            }
        }

        binding.btnSelectedColor.setOnClickListener {
            showColorPicker(binding.wheelview.selectedItemTextColor) {
                binding.wheelview.selectedItemTextColor = it
                binding.btnSelectedColor.setBackgroundColor(it)
            }
        }

        binding.btnNormalColor.setOnClickListener {
            showColorPicker(binding.wheelview.normalItemTextColor) {
                binding.wheelview.normalItemTextColor = it
                binding.btnNormalColor.setBackgroundColor(it)
            }
        }

        binding.btnDividerColor.setOnClickListener {
            showColorPicker(binding.wheelview.dividerColor) {
                binding.wheelview.dividerColor = it
                binding.btnDividerColor.setBackgroundColor(it)
            }
        }

        binding.btnFontMedium.setOnClickListener {
            binding.wheelview.setTypeface(typefaceMedium(), binding.cbBoldSelected.isChecked)
        }

        binding.btnFontRegular.setOnClickListener {
            binding.wheelview.setTypeface(typefaceRegular(), binding.cbBoldSelected.isChecked)
        }

        binding.btnFontLight.setOnClickListener {
            binding.wheelview.setTypeface(typefaceLight(), binding.cbBoldSelected.isChecked)
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
        binding.wheelview.setData(daysList, IntTextFormatter("%d日"))
        binding.wheelview.setSelectedPosition(currentDay)
    }

    private fun initDefaultWheelAttrs() {
        binding.wheelview.setTextSize(18f, true)
        binding.wheelview.setLineSpacing(10f, true)
        binding.wheelview.setSoundResource(R.raw.button_choose)
        binding.wheelview.getAdapter()?.getSelectedItem<Int>()
        binding.wheelview.curtainColor = Color.WHITE
    }

    private fun initOperationsValue() {
        val wheelView = binding.wheelview
        binding.sbVisibleItems.max = 9
        binding.sbVisibleItems.progress = wheelView.visibleItems

        binding.scCyclic.isChecked = wheelView.isCyclic

        binding.sbLineSpacing.max = 50
        binding.sbLineSpacing.progress = wheelView.lineSpacing.toInt()

        binding.sbSelectedDuration.max = 3000
        binding.sbSelectedDuration.progress = WheelViewKt.DEFAULT_SCROLL_DURATION

        binding.scSound.isChecked = wheelView.isSoundEffect
        binding.sbSoundVolume.max = 100
        binding.sbSoundVolume.progress = (wheelView.getPlayVolume() * 100).toInt()

        binding.scHasCurtain.isChecked = wheelView.hasCurtain
        binding.btnCurtainColor.setBackgroundColor(wheelView.curtainColor)

        binding.sbTextSize.max = 70
        binding.sbTextSize.progress = wheelView.textSize.toInt()

        when (wheelView.textAlign) {
            WheelViewKt.TEXT_ALIGN_LEFT -> binding.rbAlignLeft.isChecked = true
            WheelViewKt.TEXT_ALIGN_RIGHT -> binding.rbAlignRight.isChecked = true
            else -> binding.rbAlignCenter.isChecked = true
        }

        binding.btnSelectedColor.setBackgroundColor(wheelView.selectedItemTextColor)
        binding.btnNormalColor.setBackgroundColor(wheelView.normalItemTextColor)

        binding.sbTextBoundaryMargin.max = 150
        binding.sbTextBoundaryMargin.progress = wheelView.textBoundaryMargin.toInt()

        binding.cbBoldSelected.isChecked = wheelView.isBoldForSelectedItem()

        binding.scCurved.isChecked = wheelView.isCurved
        when (wheelView.curvedArcDirection) {
            WheelViewKt.CURVED_ARC_DIRECTION_LEFT -> binding.rbDirectionLeft.isChecked = true
            WheelViewKt.CURVED_ARC_DIRECTION_RIGHT -> binding.rbDirectionRight.isChecked = true
            else -> binding.rbDirectionCenter.isChecked = true
        }
        binding.sbCurvedArcFactor.max = 100
        binding.sbCurvedArcFactor.progress = (wheelView.curvedArcDirectionFactor * 100).toInt()
        binding.sbRefract.max = 100
        binding.sbRefract.progress = (wheelView.refractRatio * 100).toInt()

        binding.scShowDivider.isChecked = wheelView.isShowDivider
        binding.sbDividerHeight.max = 30
        binding.sbDividerHeight.progress = wheelView.dividerHeight.toInt()
        binding.btnDividerColor.setBackgroundColor(wheelView.dividerColor)
        when (wheelView.dividerType) {
            WheelViewKt.DIVIDER_TYPE_FILL -> binding.rbDividerFill.isChecked = true
            else -> binding.rbDividerWrap.isChecked = true
        }
        binding.sbDividerPadding.max = 60
        binding.sbDividerPadding.progress = wheelView.dividerPaddingForWrap.toInt()
        binding.sbDividerOffset.max = 30
        binding.sbDividerOffset.progress = wheelView.dividerOffsetY.toInt()
        when (binding.wheelview.dividerCap) {
            Paint.Cap.SQUARE -> binding.rbDividerCapSquare.isChecked = true
            Paint.Cap.BUTT -> binding.rbDividerCapButt.isChecked = true
            else -> binding.rbDividerCapRound.isChecked = true
        }

        binding.btnFontMedium.typeface = typefaceMedium()
        binding.btnFontRegular.typeface = typefaceRegular()
        binding.btnFontLight.typeface = typefaceLight()

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
            R.id.sb_line_spacing -> binding.wheelview.lineSpacing = progress.toFloat()
            R.id.sb_sound_volume -> binding.wheelview.setPlayVolume(progress / 100f)
            R.id.sb_text_size -> binding.wheelview.textSize = progress.toFloat()
            R.id.sb_text_boundary_margin -> binding.wheelview.textBoundaryMargin = progress.toFloat()
            R.id.sb_curved_arc_factor -> binding.wheelview.curvedArcDirectionFactor = progress / 100f
            R.id.sb_refract -> binding.wheelview.refractRatio = progress / 100f
            R.id.sb_divider_height -> binding.wheelview.dividerHeight = progress.toFloat()
            R.id.sb_divider_padding -> binding.wheelview.dividerPaddingForWrap = progress.toFloat()
            R.id.sb_divider_offset -> binding.wheelview.dividerOffsetY = progress.toFloat()
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
