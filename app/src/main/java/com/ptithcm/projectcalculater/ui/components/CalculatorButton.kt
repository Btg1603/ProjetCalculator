package com.ptithcm.projectcalculater.ui.components

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import com.google.android.material.button.MaterialButton

/**
 * Nút bấm của máy tính.
 * Dùng HapticFeedback thay vì Vibrator trực tiếp → không cần permission VIBRATE.
 */
class CalculatorButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.materialButtonStyle
) : MaterialButton(context, attrs, defStyleAttr) {

    override fun performClick(): Boolean {
        // Haptic feedback nhẹ, không cần permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
        } else {
            performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
        return super.performClick()
    }
}