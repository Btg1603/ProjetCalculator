package com.ptithcm.projectcalculater.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import com.google.android.material.button.MaterialButton

class CalculatorButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.materialButtonStyle
) : MaterialButton(context, attrs, defStyleAttr) {

    override fun performClick(): Boolean {

        animate()
            .scaleX(0.92f)
            .scaleY(0.92f)
            .setDuration(40)
            .withEndAction {

                animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(40)
                    .start()
            }
            .start()

        performHapticFeedback(
            HapticFeedbackConstants.VIRTUAL_KEY,
            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
        )

        return super.performClick()
    }
}
