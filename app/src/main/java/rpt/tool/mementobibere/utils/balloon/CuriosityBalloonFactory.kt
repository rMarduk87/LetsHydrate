package rpt.tool.mementobibere.utils.balloon

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonHighlightAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.overlay.BalloonOverlayRoundRect
import rpt.tool.mementobibere.R

class CuriosityBalloonFactory : Balloon.Factory() {

    private var waters: Array<String> = arrayOf()
    override fun create(context: Context, lifecycle: LifecycleOwner?): Balloon {
        waters = context.resources.getStringArray(R.array.water)
        val randomIndex: Int = java.util.Random().nextInt(waters.size)
        val randomWaters: String = waters[randomIndex]
        return Balloon.Builder(context)
            .setText(randomWaters)
            .setArrowSize(10)
            .setWidthRatio(1.0f)
            .setHeight(BalloonSizeSpec.WRAP)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            .setArrowPosition(0.5f)
            .setPadding(12)
            .setMarginRight(12)
            .setMarginLeft(12)
            .setTextSize(15f)
            .setCornerRadius(8f)
            .setTextColorResource(R.color.white_87)
            .setBackgroundColorResource(R.color.colorSkyBlue)
            .setBalloonAnimation(BalloonAnimation.ELASTIC)
            .setIsVisibleOverlay(true)
            .setOverlayColorResource(R.color.overlay)
            .setOverlayPaddingResource(R.dimen.editBalloonOverlayPadding)
            .setBalloonHighlightAnimation(BalloonHighlightAnimation.SHAKE)
            .setOverlayShape(
                BalloonOverlayRoundRect(
                    R.dimen.editBalloonOverlayRadius,
                    R.dimen.editBalloonOverlayRadius,
                ),
            )
            .setLifecycleOwner(lifecycle)
            .setDismissWhenClicked(true)
            .setOnBalloonDismissListener {
                Toast.makeText(context.applicationContext, "dismissed", Toast.LENGTH_SHORT).show()
            }.build()
    }
}