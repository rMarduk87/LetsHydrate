package rpt.tool.mementobibere.utils.balloon.walktrought

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonHighlightAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.overlay.BalloonOverlayRoundRect
import rpt.tool.mementobibere.R

class FifthWalkthroughBalloonFactory : Balloon.Factory() {

    override fun create(context: Context, lifecycle: LifecycleOwner?): Balloon {
        return Balloon.Builder(context)
            .setText(context.getText(R.string.walkthrough_text_five))
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
            .setDismissWhenClicked(false)
            .build()
    }
}