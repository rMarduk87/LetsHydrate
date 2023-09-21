package rpt.tool.mementobibere.ui.libraries.menu.behavior

import android.animation.Animator
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.marginBottom
import rpt.tool.mementobibere.ui.libraries.menu.utils.AnimationHelper
import rpt.tool.mementobibere.ui.libraries.menu.utils.clamp
import kotlin.math.abs

class ExpandableBottomBarScrollableBehavior<V: View>:
    ExpandableBottomBarBehavior<V> {

    private val handler = Handler(Looper.getMainLooper())
    private var lastKnownRunnable: Runnable? = null

    private var animator: Animator? = null
    private var lastKnownDirection: Int? = null

    constructor(): super()

    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet)

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout, child: V, directTargetChild: View, target: View, axes: Int, type: Int
    ): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int, consumed: IntArray) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed)

        removeActiveRunnable()
        cancelAnimation()

        lastKnownDirection = dyConsumed
        child.translationY = getScrollRange(child, dyConsumed)
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, target: View, type: Int) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type)

        removeActiveRunnable()

        val delayedAnimationRunnable = Runnable {
            animateWithDirection(child)
        }
        handler.postDelayed(delayedAnimationRunnable, 500L)
        lastKnownRunnable = delayedAnimationRunnable
    }

    private fun getScrollRange(child: V, dy: Int): Float {
        return clamp(child.translationY + dy, 0f, getMaxScrollDistance(child))
    }

    private fun getMaxScrollDistance(child: V): Float {
        val childHeight = if (ViewCompat.isLaidOut(child)) child.height else child.measuredHeight
        return childHeight.toFloat() + child.marginBottom
    }

    private fun animateWithDirection(child: V) {
        val dy = lastKnownDirection ?: return
        val halfOfScrollDistance = getMaxScrollDistance(child) / 2F

        val overHalfUp = dy < 0 /* up */ && abs(child.translationY) < halfOfScrollDistance
        val lessThanHalfDown = dy > 0 /* down */ && abs(child.translationY) < halfOfScrollDistance

        if (overHalfUp || lessThanHalfDown) {
            animateTo(child, 0F)
        } else {
            animateTo(child, getMaxScrollDistance(child))
        }
    }

    private fun animateTo(child: V, translation: Float) {
        if (child.translationY == 0F || child.translationY == getMaxScrollDistance(child)) {
            return
        }

        cancelAnimation()
        animator = AnimationHelper.translateViewTo(child, translation)
        animator?.start()
    }

    private fun cancelAnimation() {
        if (animator != null && animator?.isRunning == true) {
            animator?.cancel()
            animator = null
        }
    }

    private fun removeActiveRunnable() {
        val runnableToDelete = lastKnownRunnable
        if (runnableToDelete != null) {
            handler.removeCallbacks(runnableToDelete)
            lastKnownRunnable = null
        }
    }
}
