/*
 * Copyright 2014 Blaž Šolar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rpt.tool.mementobibere.utils.view.custom.horizontalpicker

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.text.BoringLayout
import android.text.Layout
import android.text.TextPaint
import android.text.TextUtils.TruncateAt
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.accessibility.AccessibilityEvent
import android.view.animation.DecelerateInterpolator
import android.widget.EdgeEffect
import android.widget.OverScroller
import androidx.core.text.TextDirectionHeuristicCompat
import androidx.core.text.TextDirectionHeuristicsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.customview.widget.ExploreByTouchHelper
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.utils.view.custom.horizontalpicker.HorizontalPicker
import java.lang.ref.WeakReference
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Created by Blaž Šolar on 24/01/14.
 */
class HorizontalPicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = R.attr.horizontalPickerStyle
) :
    View(context, attrs, defStyle) {
    /**
     * Determines speed during touch scrolling.
     */
    private var mVelocityTracker: VelocityTracker? = null

    /**
     * @see ViewConfiguration.getScaledMinimumFlingVelocity
     */
    private val mMinimumFlingVelocity: Int

    /**
     * @see ViewConfiguration.getScaledMaximumFlingVelocity
     */
    private val maximumFlingVelocity: Int

    private val overscrollDistance: Int

    private val touchSlop: Int

    private var values: Array<CharSequence?>? = null
    private var layouts: Array<BoringLayout?>? = null

    private val textPaint: TextPaint
    private val boringMetrics: BoringLayout.Metrics
    private var ellipsize: TruncateAt? = null

    private var itemWidth = 0
    private var itemClipBounds: RectF? = null
    private var itemClipBoundsOffset: RectF? = null

    private var lastDownEventX = 0f

    private val flingScrollerX: OverScroller
    private val adjustScrollerX: OverScroller

    private var previousScrollerX: Int

    private var scrollingX = false
    private var pressedItem = -1

    private var textColor: ColorStateList? = null

    private var onItemSelected: OnItemSelected? = null
    private var onItemClicked: OnItemClicked? = null

    private var selectedItem = 0

    private var leftEdgeEffect: EdgeEffect? = null
    private var rightEdgeEffect: EdgeEffect? = null

    private var marquee: Marquee? = null
    var marqueeRepeatLimit: Int = 3

    private var dividerSize = 0f

    private var sideItems = 1

    private var textDir: TextDirectionHeuristicCompat? = null

    private val touchHelper: PickerTouchHelper


    var handler: Handler? = null
    var runnable: Runnable? = null

    var current_progress: Int = 90
    var new_progress: Int = 150


    init {
        // create the selector wheel paint
        val paint = TextPaint()
        paint.isAntiAlias = true
        textPaint = paint

        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.HorizontalPicker,
            defStyle, 0
        )

        val values: Array<CharSequence?>
        var ellipsize = 3 // END default value
        var sideItems = this.sideItems

        try {
            textColor = a.getColorStateList(R.styleable.HorizontalPicker_android_textColor)
            if (textColor == null) {
                textColor = ColorStateList.valueOf(-0x1000000)
            }

            values = a.getTextArray(R.styleable.HorizontalPicker_values)
            ellipsize = a.getInt(R.styleable.HorizontalPicker_android_ellipsize, ellipsize)
            marqueeRepeatLimit = a.getInt(
                R.styleable.HorizontalPicker_android_marqueeRepeatLimit,
                marqueeRepeatLimit
            )
            dividerSize = a.getDimension(R.styleable.HorizontalPicker_dividerSize, dividerSize)
            sideItems = a.getInt(R.styleable.HorizontalPicker_sideItems, sideItems)

            val textSize = a.getDimension(R.styleable.HorizontalPicker_android_textSize, -1f)
            if (textSize > -1) {
                setTextSize(textSize)
            }
        } finally {
            a.recycle()
        }

        when (ellipsize) {
            1 -> setEllipsize(TruncateAt.START)
            2 -> setEllipsize(TruncateAt.MIDDLE)
            3 -> setEllipsize(TruncateAt.END)
            4 -> setEllipsize(TruncateAt.MARQUEE)
        }

        val fontMetricsInt = textPaint.fontMetricsInt
        boringMetrics = BoringLayout.Metrics()
        boringMetrics.ascent = fontMetricsInt.ascent
        boringMetrics.bottom = fontMetricsInt.bottom
        boringMetrics.descent = fontMetricsInt.descent
        boringMetrics.leading = fontMetricsInt.leading
        boringMetrics.top = fontMetricsInt.top
        boringMetrics.width = itemWidth

        setWillNotDraw(false)

        flingScrollerX = OverScroller(context)
        adjustScrollerX = OverScroller(context, DecelerateInterpolator(2.5f))

        // initialize constants
        val configuration = ViewConfiguration.get(context)
        touchSlop = configuration.scaledTouchSlop
        mMinimumFlingVelocity = configuration.scaledMinimumFlingVelocity
        maximumFlingVelocity = (configuration.scaledMaximumFlingVelocity
                / SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT)
        overscrollDistance = configuration.scaledOverscrollDistance

        previousScrollerX = Int.MIN_VALUE

        setValues(values)
        setSideItems(sideItems)

        touchHelper = PickerTouchHelper(this)
        ViewCompat.setAccessibilityDelegate(this, touchHelper)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val height: Int
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize
        } else {
            val fontMetrics = textPaint.fontMetrics
            var heightText =
                (abs(fontMetrics.ascent.toDouble()) + abs(fontMetrics.descent.toDouble())).toInt()
            heightText += paddingTop + paddingBottom

            height = if (heightMode == MeasureSpec.AT_MOST) {
                min(heightSize.toDouble(), heightText.toDouble()).toInt()
            } else {
                heightText
            }
        }

        setMeasuredDimension(width, height)
    }

    @SuppressLint("ResourceType", "DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val saveCount = canvas.saveCount
        canvas.save()

        val selectedItem = this.selectedItem

        val itemWithPadding = itemWidth + dividerSize

        // translate horizontal to center
        canvas.translate(itemWithPadding * sideItems, 0f)


        if (values != null) {
            for (i in values!!.indices) {
                // set text color for item

                textPaint.color = Color.parseColor("#523D6B70")

                if (selectedItem == i) {
                    textPaint.color = Color.parseColor("#3D6B70")

                }


                // get text layout
                val layout = layouts!![i]

                val saveCountHeight = canvas.saveCount
                canvas.save()

                var x = 0f

                val lineWidth = layout!!.getLineWidth(0)
                if (lineWidth > itemWidth) {
                    if (isRtl(values!![i])) {
                        x += (lineWidth - itemWidth) / 2
                    } else {
                        x -= (lineWidth - itemWidth) / 2
                    }
                }

                if (marquee != null && i == selectedItem) {
                    x += marquee!!.scroll
                }

                // translate vertically to center
                canvas.translate(-x, ((height - layout.height) / 2).toFloat())

                var clipBounds: RectF?
                if (x == 0f) {
                    clipBounds = itemClipBounds
                } else {
                    clipBounds = itemClipBoundsOffset
                    clipBounds!!.set(itemClipBounds!!)
                    clipBounds.offset(x, 0f)
                }

                val paint = Paint()
                paint.color = Color.TRANSPARENT
                canvas.drawRect(clipBounds!!, paint)

                layout.draw(canvas)

                if (marquee != null && i == selectedItem && marquee!!.shouldDrawGhost()) {
                    canvas.translate(marquee!!.ghostOffset, 0f)
                    layout.draw(canvas)
                }

                // restore vertical translation
                canvas.restoreToCount(saveCountHeight)

                // translate horizontal for 1 item
                canvas.translate(itemWithPadding, 0f)
            }
        }

        // restore horizontal translation
        canvas.restoreToCount(saveCount)

        drawEdgeEffect(canvas, leftEdgeEffect, 270)
        drawEdgeEffect(canvas, rightEdgeEffect, 90)
    }

    override fun onRtlPropertiesChanged(layoutDirection: Int) {
        super.onRtlPropertiesChanged(layoutDirection)

        textDir = textDirectionHeuristic
    }

    /**
     * TODO cache values
     * @param text
     * @return
     */
    private fun isRtl(text: CharSequence): Boolean {
        if (textDir == null) {
            textDir = textDirectionHeuristic
        }

        return textDir!!.isRtl(text, 0, text.length)
    }

    private val textDirectionHeuristic: TextDirectionHeuristicCompat
        get() {
            // Always need to resolve layout direction first

            val defaultIsRtl =
                (layoutDirection == LAYOUT_DIRECTION_RTL)

            return when (textDirection) {
                TEXT_DIRECTION_FIRST_STRONG -> (if (defaultIsRtl)
                    TextDirectionHeuristicsCompat.FIRSTSTRONG_RTL else
                        TextDirectionHeuristicsCompat.FIRSTSTRONG_LTR)
                TEXT_DIRECTION_ANY_RTL -> TextDirectionHeuristicsCompat.ANYRTL_LTR
                TEXT_DIRECTION_LTR -> TextDirectionHeuristicsCompat.LTR
                TEXT_DIRECTION_RTL -> TextDirectionHeuristicsCompat.RTL
                TEXT_DIRECTION_LOCALE -> TextDirectionHeuristicsCompat.LOCALE
                else -> (if (defaultIsRtl) TextDirectionHeuristicsCompat.FIRSTSTRONG_RTL else
                    TextDirectionHeuristicsCompat.FIRSTSTRONG_LTR)
            }
        }

    private fun remakeLayout() {
        if (layouts != null && layouts!!.isNotEmpty() && width > 0) {
            for (i in layouts!!.indices) {
                layouts!![i]!!.replaceOrMake(
                    values!![i], textPaint, itemWidth,
                    Layout.Alignment.ALIGN_CENTER, 1f, 1f, boringMetrics,
                    false, ellipsize,
                    itemWidth
                )
            }
        }
    }

    private fun drawEdgeEffect(canvas: Canvas?, edgeEffect: EdgeEffect?, degrees: Int) {
        if (canvas == null || edgeEffect == null || (degrees != 90 && degrees != 270)) {
            return
        }

        if (!edgeEffect.isFinished) {
            val restoreCount = canvas.saveCount
            val width = width
            val height = height

            canvas.rotate(degrees.toFloat())

            if (degrees == 270) {
                canvas.translate(
                    -height.toFloat(),
                    max(0.0, scrollX.toDouble()).toFloat()
                )
            } else { // 90
                canvas.translate(
                    0f,
                    (-(max(
                        scrollRange.toDouble(),
                        scaleX.toDouble()
                    ) + width)).toFloat()
                )
            }

            edgeEffect.setSize(height, width)
            if (edgeEffect.draw(canvas)) {
                postInvalidateOnAnimation()
            }

            canvas.restoreToCount(restoreCount)
        }
    }

    /**
     * Calculates text color for specified item based on its position and state.
     *
     * @param item Index of item to get text color for
     * @return Item text color
     */
    private fun getTextColor(item: Int): Int {
        val scrollX = scrollX

        // set color of text
        var color = textColor!!.defaultColor
        val itemWithPadding = (itemWidth + dividerSize).toInt()
        if (scrollX > itemWithPadding * item - itemWithPadding / 2 &&
            scrollX < itemWithPadding * (item + 1) - itemWithPadding / 2
        ) {
            val position = scrollX - itemWithPadding / 2
            color = getColor(position, item)
        } else if (item == pressedItem) {
            color = textColor!!.getColorForState(intArrayOf(android.R.attr.state_pressed), color)
        }

        return color
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        calculateItemSize(w, h)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) {
            return false
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(event)

        val action = event.actionMasked
        when (action) {
            MotionEvent.ACTION_MOVE -> {
                val currentMoveX = event.x

                var deltaMoveX = (lastDownEventX - currentMoveX).toInt()

                if (scrollingX ||
                    (abs(deltaMoveX.toDouble()) > touchSlop) && values != null &&
                    values!!.isNotEmpty()
                ) {
                    if (!scrollingX) {
                        deltaMoveX = 0
                        pressedItem = -1
                        scrollingX = true
                        parent.requestDisallowInterceptTouchEvent(true)
                        stopMarqueeIfNeeded()
                    }

                    val range = scrollRange

                    if (overScrollBy(
                            deltaMoveX, 0, scrollX, 0, range, 0,
                            overscrollDistance, 0, true
                        )
                    ) {
                        mVelocityTracker!!.clear()
                    }

                    val pulledToX = (scrollX + deltaMoveX).toFloat()
                    if (pulledToX < 0) {
                        leftEdgeEffect!!.onPull(deltaMoveX.toFloat() / width)
                        if (!rightEdgeEffect!!.isFinished) {
                            rightEdgeEffect!!.onRelease()
                        }
                    } else if (pulledToX > range) {
                        rightEdgeEffect!!.onPull(deltaMoveX.toFloat() / width)
                        if (!leftEdgeEffect!!.isFinished) {
                            leftEdgeEffect!!.onRelease()
                        }
                    }

                    lastDownEventX = currentMoveX
                    invalidate()
                }
            }

            MotionEvent.ACTION_DOWN -> {
                if (!adjustScrollerX.isFinished) {
                    adjustScrollerX.forceFinished(true)
                } else if (!flingScrollerX.isFinished) {
                    flingScrollerX.forceFinished(true)
                } else {
                    scrollingX = false
                }

                lastDownEventX = event.x

                if (!scrollingX) {
                    pressedItem = getPositionFromTouch(event.x)
                }
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                val velocityTracker = mVelocityTracker
                velocityTracker!!.computeCurrentVelocity(1000, maximumFlingVelocity.toFloat())
                val initialVelocityX = velocityTracker.xVelocity.toInt()

                if (scrollingX && abs(initialVelocityX.toDouble()) > mMinimumFlingVelocity) {
                    flingX(initialVelocityX)
                } else if (values != null) {
                    val positionX = event.x
                    if (!scrollingX) {
                        val itemPos = getPositionOnScreen(positionX)
                        val relativePos = itemPos - sideItems

                        if (relativePos == 0) {
                            selectItem()
                        } else {
                            smoothScrollBy(relativePos)
                        }
                    } else if (scrollingX) {
                        finishScrolling()
                    }
                }

                mVelocityTracker!!.recycle()
                mVelocityTracker = null

                if (leftEdgeEffect != null) {
                    leftEdgeEffect!!.onRelease()
                    rightEdgeEffect!!.onRelease()
                }

                pressedItem = -1
                invalidate()

                if (leftEdgeEffect != null) {
                    leftEdgeEffect!!.onRelease()
                    rightEdgeEffect!!.onRelease()
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                pressedItem = -1
                invalidate()

                if (leftEdgeEffect != null) {
                    leftEdgeEffect!!.onRelease()
                    rightEdgeEffect!!.onRelease()
                }
            }
        }

        return true
    }

    private fun selectItem() {
        // post to the UI Thread to avoid potential interference with the OpenGL Thread
        if (onItemClicked != null) {
            post { onItemClicked!!.onItemClicked(getSelectedItem()) }
        }

        adjustToNearestItemX()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (!isEnabled) {
            return super.onKeyDown(keyCode, event)
        }

        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                selectItem()
                return true
            }

            KeyEvent.KEYCODE_DPAD_LEFT -> {
                smoothScrollBy(-1)
                return true
            }

            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                smoothScrollBy(1)
                return true
            }

            else -> return super.onKeyDown(keyCode, event)
        }
    }

    override fun dispatchHoverEvent(event: MotionEvent): Boolean {
        if (touchHelper.dispatchHoverEvent(event)) {
            return true
        }

        return super.dispatchHoverEvent(event)
    }

    override fun computeScroll() {
        computeScrollX()
    }

    override fun getFocusedRect(r: Rect) {
        super.getFocusedRect(r) // TODO this should only be current item
    }

    fun setOnItemSelectedListener(onItemSelected: OnItemSelected?) {
        this.onItemSelected = onItemSelected
    }

    fun setOnItemClickedListener(onItemClicked: OnItemClicked?) {
        this.onItemClicked = onItemClicked
    }

    fun getSelectedItem(): Int {
        val x = scrollX
        return getPositionFromCoordinates(x)
    }

    fun setSelectedItem(index: Int) {
        selectedItem = index
        scrollToItem(index)
    }

    /**
     * @return Number of items on each side of current item.
     */
    fun getSideItems(): Int {
        return sideItems
    }

    fun setSideItems(sideItems: Int) {
        require(this.sideItems >= 0) { "Number of items on each side must be grater or equal to 0." }
        if (this.sideItems != sideItems) {
            this.sideItems = sideItems
            calculateItemSize(width, height + 100)
        }
    }

    /**
     * @return
     */
    fun getValues(): Array<CharSequence>? {
        return values
    }

    /**
     * Sets values to choose from
     * @param values New values to choose from
     */
    fun setValues(values: Array<CharSequence?>) {
        if (!this.values.contentEquals(values)) {
            this.values = values

            if (this.values != null) {
                layouts = arrayOfNulls(this.values!!.size)
                for (i in layouts!!.indices) {
                    layouts!![i] = BoringLayout(
                        this.values!![i], textPaint, itemWidth, Layout.Alignment.ALIGN_CENTER,
                        1f, 1f, boringMetrics, false, ellipsize, itemWidth
                    )
                }
            } else {
                layouts = arrayOfNulls(0)
            }

            // start marque only if has already been measured
            if (width > 0) {
                startMarqueeIfNeeded()
            }

            requestLayout()
            invalidate()
        }
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }

        val ss = state
        super.onRestoreInstanceState(ss.superState)

        setSelectedItem(ss.mSelItem)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()

        val savedState = SavedState(superState)
        savedState.mSelItem = selectedItem

        return savedState
    }

    override fun setOverScrollMode(overScrollMode: Int) {
        if (overScrollMode != OVER_SCROLL_NEVER) {
            val context = context
            leftEdgeEffect = EdgeEffect(context)
            rightEdgeEffect = EdgeEffect(context)
        } else {
            leftEdgeEffect = null
            rightEdgeEffect = null
        }

        super.setOverScrollMode(overScrollMode)
    }

    fun getEllipsize(): TruncateAt? {
        return ellipsize
    }

    fun setEllipsize(ellipsize: TruncateAt) {
        if (this.ellipsize != ellipsize) {
            this.ellipsize = ellipsize

            remakeLayout()
            invalidate()
        }
    }

    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
        super.scrollTo(scrollX, scrollY)

        if (!flingScrollerX.isFinished && clampedX) {
            flingScrollerX.springBack(scrollX, scrollY, 0, scrollRange, 0, 0)
        }
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged() //TODO
    }

    private fun getPositionFromTouch(x: Float): Int {
        return getPositionFromCoordinates((scrollX - (itemWidth + dividerSize) * (sideItems + .5f) + x).toInt())
    }

    private fun computeScrollX() {
        var scroller = flingScrollerX
        if (scroller.isFinished) {
            scroller = adjustScrollerX
            if (scroller.isFinished) {
                return
            }
        }

        if (scroller.computeScrollOffset()) {
            val currentScrollerX = scroller.currX
            if (previousScrollerX == Int.MIN_VALUE) {
                previousScrollerX = scroller.startX
            }

            val range = scrollRange
            if (previousScrollerX >= 0 && currentScrollerX < 0) {
                leftEdgeEffect!!.onAbsorb(scroller.currVelocity.toInt())
            } else if (previousScrollerX <= range && currentScrollerX > range) {
                rightEdgeEffect!!.onAbsorb(scroller.currVelocity.toInt())
            }

            overScrollBy(
                currentScrollerX - previousScrollerX, 0, previousScrollerX, scrollY,
                scrollRange, 0, overscrollDistance, 0, false
            )
            previousScrollerX = currentScrollerX

            if (scroller.isFinished) {
                onScrollerFinishedX(scroller)
            }

            postInvalidate()
            //            postInvalidateOnAnimation(); // TODO
        }
    }

    private fun flingX(velocityX: Int) {
        previousScrollerX = Int.MIN_VALUE
        flingScrollerX.fling(
            scrollX, scrollY, -velocityX, 0, 0,
            (itemWidth + dividerSize).toInt() * (values!!.size - 1), 0, 0, width / 2, 0
        )

        invalidate()
    }

    private fun adjustToNearestItemX() {
        val x = scrollX
        var item = Math.round(x / (itemWidth + dividerSize * 1f))

        if (item < 0) {
            item = 0
        } else if (item > values!!.size) {
            item = values!!.size
        }

        selectedItem = item

        val itemX = (itemWidth + dividerSize.toInt()) * item

        val deltaX = itemX - x

        previousScrollerX = Int.MIN_VALUE
        adjustScrollerX.startScroll(x, 0, deltaX, 0, SELECTOR_ADJUSTMENT_DURATION_MILLIS)
        invalidate()
    }

    private fun calculateItemSize(w: Int, h: Int) {
        val items = sideItems * 2 + 1
        val totalPadding = (dividerSize.toInt() * (items - 1))
        itemWidth = (w - totalPadding) / items

        itemClipBounds = RectF(0f, 0f, itemWidth.toFloat(), h.toFloat())
        itemClipBoundsOffset = RectF(itemClipBounds)

        scrollToItem(selectedItem)

        remakeLayout()
        startMarqueeIfNeeded()
    }

    private fun onScrollerFinishedX(scroller: OverScroller) {
        if (scroller === flingScrollerX) {
            finishScrolling()
        }
    }

    private fun finishScrolling() {
        adjustToNearestItemX()
        scrollingX = false
        startMarqueeIfNeeded()
        // post to the UI Thread to avoid potential interference with the OpenGL Thread
        if (onItemSelected != null) {
            post {
                onItemSelected!!.onItemSelected(
                    getPositionFromCoordinates(
                        scrollX
                    )
                )
            }
        }
    }

    private fun startMarqueeIfNeeded() {
        stopMarqueeIfNeeded()

        val item = getSelectedItem()

        if (layouts != null && layouts!!.size > item) {
            val layout: Layout? = layouts!![item]
            if (ellipsize == TruncateAt.MARQUEE
                && itemWidth < layout!!.getLineWidth(0)
            ) {
                marquee = Marquee(this, layout, isRtl(values!![item]))
                marquee!!.start(marqueeRepeatLimit)
            }
        }
    }

    private fun stopMarqueeIfNeeded() {
        if (marquee != null) {
            marquee!!.stop()
            marquee = null
        }
    }

    private fun getPositionOnScreen(x: Float): Int {
        return (x / (itemWidth + dividerSize)).toInt()
    }

    private fun smoothScrollBy(i: Int) {
        var deltaMoveX = (itemWidth + dividerSize.toInt()) * i
        deltaMoveX = getRelativeInBound(deltaMoveX)

        previousScrollerX = Int.MIN_VALUE
        flingScrollerX.startScroll(scrollX, 0, deltaMoveX, 0)
        stopMarqueeIfNeeded()
        invalidate()
    }

    /**
     * Calculates color for specific position on time picker
     * @param scrollX
     * @return
     */
    private fun getColor(scrollX: Int, position: Int): Int {
        val itemWithPadding = (itemWidth + dividerSize).toInt()
        var proportion =
            abs((((1f * scrollX % itemWithPadding) / 2) / (itemWithPadding / 2f)).toDouble())
                .toFloat()
        proportion = if (proportion > .5) {
            (proportion - .5f)
        } else {
            .5f - proportion
        }
        proportion *= 2f

        val defaultColor: Int
        val selectedColor: Int

        if (pressedItem == position) {
            defaultColor = textColor!!.getColorForState(
                intArrayOf(android.R.attr.state_pressed),
                textColor!!.defaultColor
            )
            selectedColor = textColor!!.getColorForState(
                intArrayOf(android.R.attr.state_pressed, android.R.attr.state_selected),
                defaultColor
            )
        } else {
            defaultColor = textColor!!.defaultColor
            selectedColor =
                textColor!!.getColorForState(intArrayOf(android.R.attr.state_selected), defaultColor)
        }
        return ArgbEvaluator().evaluate(proportion, selectedColor, defaultColor) as Int
    }

    /**
     * Sets text size for items
     * @param size New item text size in px.
     */
    open fun setTextSize(size: Float) {
        if (size != textPaint.textSize) {
            textPaint.textSize = size

            requestLayout()
            invalidate()
        }
    }

    /**
     * Calculates item from x coordinate position.
     * @param x Scroll position to calculate.
     * @return Selected item from scrolling position in {param x}
     */
    private fun getPositionFromCoordinates(x: Int): Int {
        return Math.round(x / (itemWidth + dividerSize))
    }

    /**
     * Scrolls to specified item.
     * @param index Index of an item to scroll to
     */
    private fun scrollToItem(index: Int) {
        scrollTo((itemWidth + dividerSize.toInt()) * index, 0)
        // invalidate() not needed because scrollTo() already invalidates the view
    }

    /**
     * Calculates relative horizontal scroll position to be within our scroll bounds.
     * [com.wefika.horizontalpicker.HorizontalPicker.getInBoundsX]
     * @param x Relative scroll position to calculate
     * @return Current scroll position + {param x} if is within our scroll bounds, otherwise it
     * will return min/max scroll position.
     */
    private fun getRelativeInBound(x: Int): Int {
        val scrollX = scrollX
        return getInBoundsX(scrollX + x) - scrollX
    }

    /**
     * Calculates x scroll position that is still in range of view scroller
     * @param x Scroll position to calculate.
     * @return {param x} if is within bounds of over scroller, otherwise it will return min/max
     * value of scoll position.
     */
    private fun getInBoundsX(x: Int): Int {
        var x = x
        if (x < 0) {
            x = 0
        } else if (x > ((itemWidth + dividerSize.toInt()) * (values!!.size - 1))) {
            x = ((itemWidth + dividerSize.toInt()) * (values!!.size - 1))
        }
        return x
    }

    private val scrollRange: Int
        get() {
            var scrollRange = 0
            if (values != null && values!!.isNotEmpty()) {
                scrollRange = max(
                    0.0,
                    ((itemWidth + dividerSize.toInt()) * (values!!.size - 1)).toDouble()
                ).toInt()
            }
            return scrollRange
        }

    interface OnItemSelected {
        fun onItemSelected(index: Int)
    }

    interface OnItemClicked {
        fun onItemClicked(index: Int)
    }

    private class Marquee(v: HorizontalPicker, l: Layout?, rtl: Boolean) :
        Handler() {
        private val mView: WeakReference<HorizontalPicker>
        private val mLayout: WeakReference<Layout?>

        private var mStatus = MARQUEE_STOPPED
        private var mScrollUnit = 0f
        private var mMaxScroll = 0f
        var maxFadeScroll: Float = 0f
            private set
        private var mGhostStart = 0f
        var ghostOffset: Float = 0f
            private set
        private var mFadeStop = 0f
        private var mRepeatLimit = 0

        var scroll: Float = 0f
            private set

        private val mRtl: Boolean

        init {
            val density = v.context.resources.displayMetrics.density
            val scrollUnit = (MARQUEE_PIXELS_PER_SECOND * density) / MARQUEE_RESOLUTION
            mScrollUnit = if (rtl) {
                -scrollUnit
            } else {
                scrollUnit
            }

            mView = WeakReference(v)
            mLayout = WeakReference(l)
            mRtl = rtl
        }

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MESSAGE_START -> {
                    mStatus = MARQUEE_RUNNING
                    tick()
                }

                MESSAGE_TICK -> tick()
                MESSAGE_RESTART -> if (mStatus == MARQUEE_RUNNING) {
                    if (mRepeatLimit >= 0) {
                        mRepeatLimit--
                    }
                    start(mRepeatLimit)
                }
            }
        }

        fun tick() {
            if (mStatus != MARQUEE_RUNNING) {
                return
            }

            removeMessages(MESSAGE_TICK)

            val view = mView.get()
            val layout = mLayout.get()
            if (view != null && layout != null && (view.isFocused || view.isSelected)) {
                scroll += mScrollUnit
                if (abs(scroll.toDouble()) > mMaxScroll) {
                    scroll = mMaxScroll
                    if (mRtl) {
                        scroll *= -1f
                    }
                    sendEmptyMessageDelayed(MESSAGE_RESTART, MARQUEE_RESTART_DELAY.toLong())
                } else {
                    sendEmptyMessageDelayed(MESSAGE_TICK, MARQUEE_RESOLUTION.toLong())
                }
                view.invalidate()
            }
        }

        fun stop() {
            mStatus = MARQUEE_STOPPED
            removeMessages(MESSAGE_START)
            removeMessages(MESSAGE_RESTART)
            removeMessages(MESSAGE_TICK)
            resetScroll()
        }

        fun resetScroll() {
            scroll = 0.0f
            val view = mView.get()
            view?.invalidate()
        }

        fun start(repeatLimit: Int) {
            if (repeatLimit == 0) {
                stop()
                return
            }
            mRepeatLimit = repeatLimit
            val view = mView.get()
            val layout = mLayout.get()
            if (view != null && layout != null) {
                mStatus = MARQUEE_STARTING
                scroll = 0.0f
                val textWidth = view.itemWidth
                val lineWidth = layout.getLineWidth(0)
                val gap = textWidth / 3.0f
                mGhostStart = lineWidth - textWidth + gap
                mMaxScroll = mGhostStart + textWidth
                ghostOffset = lineWidth + gap
                mFadeStop = lineWidth + textWidth / 6.0f
                maxFadeScroll = mGhostStart + lineWidth + lineWidth

                if (mRtl) {
                    ghostOffset *= -1f
                }

                view.invalidate()
                sendEmptyMessageDelayed(MESSAGE_START, MARQUEE_DELAY.toLong())
            }
        }

        fun shouldDrawLeftFade(): Boolean {
            return scroll <= mFadeStop
        }

        fun shouldDrawGhost(): Boolean {
            return mStatus == MARQUEE_RUNNING && abs(scroll.toDouble()) > mGhostStart
        }

        val isRunning: Boolean
            get() = mStatus == MARQUEE_RUNNING

        val isStopped: Boolean
            get() = mStatus == MARQUEE_STOPPED

        companion object {
            // TODO: Add an option to configure this
            private const val MARQUEE_DELTA_MAX = 0.07f
            private const val MARQUEE_DELAY = 1200
            private const val MARQUEE_RESTART_DELAY = 1200
            private const val MARQUEE_RESOLUTION = 1000 / 30
            private const val MARQUEE_PIXELS_PER_SECOND = 30

            private const val MARQUEE_STOPPED: Byte = 0x0
            private const val MARQUEE_STARTING: Byte = 0x1
            private const val MARQUEE_RUNNING: Byte = 0x2

            private const val MESSAGE_START = 0x1
            private const val MESSAGE_TICK = 0x2
            private const val MESSAGE_RESTART = 0x3
        }
    }

    class SavedState : BaseSavedState {
        var mSelItem: Int = 0

        constructor(superState: Parcelable?) : super(superState)

        private constructor(`in`: Parcel) : super(`in`) {
            mSelItem = `in`.readInt()
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)

            dest.writeInt(mSelItem)
        }

        override fun toString(): String {
            return ("HorizontalPicker.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " selItem=" + mSelItem
                    + "}")
        }


        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }

    private class PickerTouchHelper(private val mPicker: HorizontalPicker) :
        ExploreByTouchHelper(mPicker) {
        override fun getVirtualViewAt(x: Float, y: Float): Int {
            val itemWidth = mPicker.itemWidth + mPicker.dividerSize
            val position = mPicker.scrollX + x - itemWidth * mPicker.sideItems

            val item = position / itemWidth

            if (item < 0 || item > mPicker.values!!.size) {
                return INVALID_ID
            }

            return item.toInt()
        }

        override fun getVisibleVirtualViews(virtualViewIds: MutableList<Int>) {
            val itemWidth = mPicker.itemWidth + mPicker.dividerSize
            val position = mPicker.scrollX - itemWidth * mPicker.sideItems

            var first = (position / itemWidth).toInt()

            var items = mPicker.sideItems * 2 + 1

            if (position % itemWidth != 0f) { // if start next item is starting to appear on screen
                items++
            }

            if (first < 0) {
                items += first
                first = 0
            } else if (first + items > mPicker.values!!.size) {
                items = mPicker.values!!.size - first
            }

            for (i in 0 until items) {
                virtualViewIds.add(first + i)
            }
        }

        override fun onPopulateEventForVirtualView(virtualViewId: Int, event: AccessibilityEvent) {
            event.contentDescription = mPicker.values!![virtualViewId]
        }

        override fun onPopulateNodeForVirtualView(
            virtualViewId: Int,
            node: AccessibilityNodeInfoCompat
        ) {
            val itemWidth = mPicker.itemWidth + mPicker.dividerSize
            val scrollOffset = mPicker.scrollX - itemWidth * mPicker.sideItems

            val left = (virtualViewId * itemWidth - scrollOffset).toInt()
            val right = left + mPicker.itemWidth

            node.contentDescription = mPicker.values!![virtualViewId]
            node.setBoundsInParent(Rect(left, 0, right, mPicker.height))
            node.addAction(AccessibilityNodeInfoCompat.ACTION_CLICK)
        }

        override fun onPerformActionForVirtualView(
            virtualViewId: Int,
            action: Int,
            arguments: Bundle?
        ): Boolean {
            return false
        }
    }

    companion object {
        const val TAG: String = "HorizontalTimePicker"

        /**
         * The coefficient by which to adjust (divide) the max fling velocity.
         */
        private const val SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT = 4

        /**
         * The the duration for adjusting the selector wheel.
         */
        private const val SELECTOR_ADJUSTMENT_DURATION_MILLIS = 800
    }
}
