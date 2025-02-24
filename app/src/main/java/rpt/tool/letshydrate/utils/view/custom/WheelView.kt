package rpt.tool.letshydrate.utils.view.custom

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import rpt.tool.letshydrate.utils.helpers.UtilityFunction
import rpt.tool.letshydrate.utils.log.d

class WheelView : ScrollView {
    var uf: UtilityFunction? = null

    class OnWheelViewListener {
        fun onSelected(selectedIndex: Int, item: String?) {
        }
    }


    private var context: Context? = null

    //    private ScrollView scrollView;
    private var views: LinearLayout? = null

    constructor(context: Context) : super(context) {
        init(context)
        uf = UtilityFunction(context, context as Activity)
        uf!!.permission_StrictMode()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(context)
    }

    //    String[] items;
    var items: MutableList<String>? = null

    var offset: Int = OFF_SET_DEFAULT

    var displayItemCount: Int = 0

    var selectedIndex: Int = 1


    private fun init(context: Context) {
        this.context = context

        //scrollView = ((ScrollView)this.getParent());
        d(TAG, "parent: " + this.parent)
        this.isVerticalScrollBarEnabled = false

        views = LinearLayout(context)
        views!!.orientation = LinearLayout.VERTICAL
        this.addView(views)

        scrollerTask = Runnable {
            val newY: Int = scrollY
            if (initialY - newY == 0) {
                val remainder = initialY % itemHeight
                val divided = initialY / itemHeight
                if (remainder == 0) {
                    selectedIndex = divided + offset
                    onSeletedCallBack()
                } else {
                    if (remainder > itemHeight / 2) {
                        this@WheelView.post(Runnable {
                            this@WheelView.smoothScrollTo(
                                0,
                                initialY - remainder + itemHeight
                            )
                            selectedIndex = divided + offset + 1
                            onSeletedCallBack()
                        })
                    } else {
                        this@WheelView.post(Runnable {
                            this@WheelView.smoothScrollTo(
                                0,
                                initialY - remainder
                            )
                            selectedIndex = divided + offset
                            onSeletedCallBack()
                        })
                    }
                }
            } else {
                initialY = scrollY
                this@WheelView.postDelayed(scrollerTask, newCheck.toLong())
            }
        }
    }

    var initialY: Int = 0

    var scrollerTask: Runnable? = null
    var newCheck: Int = 50

    fun startScrollerTask() {
        initialY = scrollY
        this.postDelayed(scrollerTask, newCheck.toLong())
    }

    private fun initData() {
        displayItemCount = offset * 2 + 1

        for (item in items!!) {
            views!!.addView(createView(item))
        }

        refreshItemView(0)
    }

    var itemHeight: Int = 0

    private fun createView(item: String): TextView {
        val tv: TextView = TextView(context)
        tv.layoutParams = LayoutParams(
            ViewGroup.LayoutParams.FILL_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        tv.isSingleLine = true
        tv.ellipsize = TextUtils.TruncateAt.END

        if (uf!!.isTablet) tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f) //20
        else tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f) //20


        tv.text = item
        tv.gravity = Gravity.CENTER
        val padding = dip2px(15f) //15
        tv.setPadding(padding, padding, padding, padding)
        if (0 == itemHeight) {
            itemHeight = getViewMeasuredHeight(tv)
            d(TAG, "itemHeight: $itemHeight")
            views!!.layoutParams = LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                itemHeight * displayItemCount
            )
            val lp: LinearLayout.LayoutParams = this.layoutParams as LinearLayout.LayoutParams
            this.layoutParams = LinearLayout.LayoutParams(lp.width, itemHeight * displayItemCount)
        }
        return tv
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)

        //        Log.d(TAG, "l: " + l + ", t: " + t + ", oldl: " + oldl + ", oldt: " + oldt);
//        try {
//            Field field = ScrollView.class.getDeclaredField("mScroller");
//            field.setAccessible(true);
//            OverScroller mScroller = (OverScroller) field.get(this);
//
//
//            if(mScroller.isFinished()){
//                Log.d(TAG, "isFinished...");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        refreshItemView(t)

        scrollDirection = if (t > oldt) {
            SCROLL_DIRECTION_DOWN
        } else {
            SCROLL_DIRECTION_UP
        }
    }

    private fun refreshItemView(y: Int) {
        var position = y / itemHeight + offset
        val remainder = y % itemHeight
        val divided = y / itemHeight

        if (remainder == 0) {
            position = divided + offset
        } else {
            if (remainder > itemHeight / 2) {
                position = divided + offset + 1
            }

            //          if(remainder > itemHeight / 2){
////                if(scrollDirection == SCROLL_DIRECTION_DOWN){
//                    position = divided + offset;
//                    Log.d(TAG, ">down...position: " + position);
//                }else if(scrollDirection == SCROLL_DIRECTION_UP){
//                    position = divided + offset + 1;
//                    Log.d(TAG, ">up...position: " + position);
//                }
//            }else{
////                position = y / itemHeight + offset;
//                if(scrollDirection == SCROLL_DIRECTION_DOWN){
//                    position = divided + offset;
//                    Log.d(TAG, "<down...position: " + position);
//                }else if(scrollDirection == SCROLL_DIRECTION_UP){
//                    position = divided + offset + 1;
//                    Log.d(TAG, "<up...position: " + position);
//                }
//            }
//        }

//        if(scrollDirection == SCROLL_DIRECTION_DOWN){
//            position = divided + offset;
//        }else if(scrollDirection == SCROLL_DIRECTION_UP){
//            position = divided + offset + 1;
        }

        val childSize: Int = views!!.childCount
        for (i in 0 until childSize) {
            val itemView: TextView = views!!.getChildAt(i) as TextView
            if (position == i) {
                itemView.setTextColor(Color.parseColor("#4F4F4F"))
                itemView.setTypeface(null, Typeface.BOLD)
                if (uf!!.isTablet) itemView.textSize = 30f
            } else {
                //itemView.setBackgroundColor(context.getResources().getColor(R.color.button_blue));
                itemView.setTextColor(Color.parseColor("#bbbbbb"))
                itemView.setTypeface(null, Typeface.NORMAL)
            }
        }
    }

    var selectedAreaBorder: IntArray? = null

    private fun obtainSelectedAreaBorder(): IntArray {
        if (null == selectedAreaBorder) {
            selectedAreaBorder = IntArray(2)
            selectedAreaBorder!![0] = itemHeight * offset
            selectedAreaBorder!![1] = itemHeight * (offset + 1)
        }
        return selectedAreaBorder as IntArray
    }


    private var scrollDirection = -1
    var paint: Paint? = null
    var viewWidth: Int = 0

    override fun setBackgroundDrawable(background: Drawable) {
        var background: Drawable? = background
        if (viewWidth == 0) {
            viewWidth = (context as Activity).windowManager.defaultDisplay.width
            d(TAG, "viewWidth: $viewWidth")
        }
        if (null == paint) {
            paint = Paint()
            paint!!.color = Color.parseColor("#4F4F4F") //#83cde6
            paint!!.strokeWidth = dip2px(1f).toFloat()
        }
        background = object : Drawable() {
            override fun draw(canvas: Canvas) {
                canvas.drawLine(
                    (viewWidth * 1 / 6).toFloat(),
                    obtainSelectedAreaBorder()[0].toFloat(),
                    (viewWidth * 5 / 6).toFloat(),
                    obtainSelectedAreaBorder()[0].toFloat(),
                    paint!!
                )
                canvas.drawLine(
                    (viewWidth * 1 / 6).toFloat(),
                    obtainSelectedAreaBorder()[1].toFloat(),
                    (viewWidth * 5 / 6).toFloat(),
                    obtainSelectedAreaBorder()[1].toFloat(),
                    paint!!
                )
            }

            override fun setAlpha(alpha: Int) {
            }

            override fun setColorFilter(cf: ColorFilter?) {
            }

            @Deprecated("Deprecated in Java", ReplaceWith("opacity"))
            override fun getOpacity(): Int {
                return PixelFormat.TRANSPARENT
            }
        }
        super.setBackgroundDrawable(background)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        d(TAG, "w: $w, h: $h, oldw: $oldw, oldh: $oldh")
        viewWidth = w
        setBackgroundDrawable(null!!)
    }

    private fun onSeletedCallBack() {
        if (null != onWheelViewListener) {
            onWheelViewListener!!.onSelected(selectedIndex, items!![selectedIndex])
        }
    }

    fun setSeletion(position: Int) {
        val p = position
        selectedIndex = p + offset
        this.post(Runnable {
            this@WheelView.smoothScrollTo(
                0,
                p * itemHeight
            )
        })
    }

    val seletedItem: String
        get() = items!![selectedIndex]

    val seletedIndex: Int
        get() = selectedIndex - offset


    override fun fling(velocityY: Int) {
        super.fling(velocityY / 3)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            startScrollerTask()
        }
        return super.onTouchEvent(ev)
    }

    var onWheelViewListener: OnWheelViewListener? = null

    private fun dip2px(dpValue: Float): Int {
        val scale = context!!.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    private fun getViewMeasuredHeight(view: View): Int {
        val width: Int = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        val expandSpec: Int = MeasureSpec.makeMeasureSpec(Int.MAX_VALUE shr 2, MeasureSpec.AT_MOST)
        view.measure(width, expandSpec)
        return view.measuredHeight
    }

    companion object {
        val TAG: String = WheelView::class.java.simpleName

        const val OFF_SET_DEFAULT: Int = 1
        private const val SCROLL_DIRECTION_UP = 0
        private const val SCROLL_DIRECTION_DOWN = 1
    }
}