package rpt.tool.mementobibere.ui.libraries.menu.utils

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator

object AnimationHelper {

    fun <T: View> translateViewTo(child: T, translation: Float): Animator {
        val animator = ValueAnimator.ofFloat(child.translationY, translation)
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener {
            val animatedValue = it.animatedValue as Float
            child.translationY = animatedValue
        }

        return animator
    }

}
