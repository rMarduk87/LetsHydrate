package rpt.tool.letshydrate.utils.view.custom

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import rpt.tool.letshydrate.R


@SuppressLint("AppCompatCustomView")
class ProgressMeter : View {
    var mContext: Context? = null
    var typedArray: TypedArray? = null
    var attrs: AttributeSet? = null

    private var viewWidth = 0
    private var viewHeight = 0

    var shadow: Boolean = false
    var shadow_thickness: Int = 8

    var text: String? = "5/7"
    var text_size: Int = 50
    var text_color: Int = Color.WHITE

    var progress: Int = 50
    var progress_color: Int = Color.YELLOW
    var progress_width: Int = 20

    var background: Int = Color.BLUE

    var indicator_shadow: Boolean = false
    var indicator_color: Int = Color.RED
    var indicator: Boolean = false
    var indicator_width: Int = 4

    var animation: Boolean = false
    var animation_duration: Int = 5

    private var paintInnerBackgroud: Paint? = null
    private var paintProgressBackground: Paint? = null
    private var paintIndicatorBackground: Paint? = null
    var textPaint: TextPaint? = null

    var margin_left: Int = 40

    var currentAngle: Float = 10f


    constructor(context: Context?) : super(context) {
        setup()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        mContext = context
        this.attrs = attrs

        init()
        setup()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        mContext = context
        this.attrs = attrs

        init()
        setup()
    }

    fun init() {
        typedArray = mContext!!.obtainStyledAttributes(attrs, R.styleable.ProgressMeter)


        background = typedArray!!.getColor(R.styleable.ProgressMeter_cpv_background, background)

        text = typedArray!!.getString(R.styleable.ProgressMeter_cpv_text)
        text_size = typedArray!!.getInteger(R.styleable.ProgressMeter_cpv_text_size, text_size)
        text_color = typedArray!!.getColor(R.styleable.ProgressMeter_cpv_text_color, text_color)

        shadow = typedArray!!.getBoolean(R.styleable.ProgressMeter_cpv_shadow, shadow)
        shadow_thickness =
            typedArray!!.getInteger(R.styleable.ProgressMeter_cpv_shadow_thickness, shadow_thickness)

        progress = typedArray!!.getInteger(R.styleable.ProgressMeter_cpv_progress, progress)
        progress_color =
            typedArray!!.getColor(R.styleable.ProgressMeter_cpv_progress_color, progress_color)
        progress_width =
            typedArray!!.getInteger(R.styleable.ProgressMeter_cpv_progress_width, progress_width)

        indicator = typedArray!!.getBoolean(R.styleable.ProgressMeter_cpv_indicator, indicator)
        indicator_shadow =
            typedArray!!.getBoolean(R.styleable.ProgressMeter_cpv_indicator_shadow, indicator_shadow)
        indicator_color =
            typedArray!!.getColor(R.styleable.ProgressMeter_cpv_indicator_color, indicator_color)
        indicator_width =
            typedArray!!.getInteger(R.styleable.ProgressMeter_cpv_indicator_width, indicator_width)

        animation = typedArray!!.getBoolean(R.styleable.ProgressMeter_cpv_animation, animation)
        animation_duration = typedArray!!.getInteger(
            R.styleable.ProgressMeter_cpv_animation_duration,
            animation_duration
        )

        if (progress > 100) progress = 100
    }

    private fun setup() {
        textPaint = TextPaint()
        //textPaint.setColor(Color.rgb(255,255,255));
        setTextColor(text_color)
        textPaint!!.setTextSize(text_size.toFloat())
        textPaint!!.setAntiAlias(true)


        paintInnerBackgroud = Paint()
        setInnerBackgroundColor(background)
        paintInnerBackgroud!!.isAntiAlias = true
        //paintInnerBackgroud.setStyle(Paint.Style.STROKE);
        //paintInnerBackgroud.setStrokeWidth(5);
        this.setLayerType(LAYER_TYPE_SOFTWARE, paintInnerBackgroud)


        paintProgressBackground = Paint()
        setProgressBackgroundColor(progress_color)
        paintProgressBackground!!.isAntiAlias = true
        //paintProgressBackground.setStrokeCap(Paint.Cap.ROUND);
        //paintProgressBackground.setStyle(Paint.Style.STROKE);
        //paintProgressBackground.setStrokeWidth(5);
        this.setLayerType(LAYER_TYPE_SOFTWARE, paintProgressBackground)


        paintIndicatorBackground = Paint()
        setIndicatorBackground(indicator_color)
        paintIndicatorBackground!!.isAntiAlias = true
        this.setLayerType(LAYER_TYPE_SOFTWARE, paintIndicatorBackground)
    }

    @SuppressLint("DrawAllocation")
    public override fun onDraw(canvas: Canvas) {
        //canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        val circleCenter = viewWidth / 2
        val hh = viewHeight / 2

        //final RectF rec = new RectF();
        //rec.set(0, 0, viewWidth, viewHeight);
        //canvas.drawRect(rec,paintBorder2);
        margin_left = (viewWidth * 40) / 300

        if (shadow) paintProgressBackground!!.setShadowLayer(
            shadow_thickness.toFloat(),
            0f,
            0f,
            Color.BLACK
        )

        val tmp_progress_width = margin_left - progress_width
        val progress_angle = (progress * 270) / 100

        val oval: RectF = RectF()
        oval.set(
            tmp_progress_width.toFloat(),
            tmp_progress_width.toFloat(),
            (viewWidth - tmp_progress_width).toFloat(),
            (viewHeight - tmp_progress_width).toFloat()
        )
        if (animation) canvas.drawArc(oval, 135f, currentAngle, true, paintProgressBackground!!)
        else canvas.drawArc(oval, 135f, progress_angle.toFloat(), true, paintProgressBackground!!)



        if (indicator_shadow) paintIndicatorBackground!!.setShadowLayer(2f, 0f, 0f, Color.BLACK)

        if (indicator) {
            var total_progress = 135 + progress_angle

            if (total_progress > 360) total_progress = total_progress - 360

            val oval3: RectF = RectF()
            oval3.set(
                (tmp_progress_width - 2).toFloat(),
                (tmp_progress_width - 2).toFloat(),
                (viewWidth - tmp_progress_width + 2).toFloat(),
                (viewHeight - tmp_progress_width + 2).toFloat()
            )
            canvas.drawArc(
                oval3, total_progress.toFloat(), indicator_width.toFloat(), true,
                paintIndicatorBackground!!
            )
        }


        val oval2: RectF = RectF()
        oval2.set(
            margin_left.toFloat(),
            margin_left.toFloat(),
            (viewWidth - margin_left).toFloat(),
            (viewHeight - margin_left).toFloat()
        )
        canvas.drawArc(oval2, 0f, 360f, true, paintInnerBackgroud!!)


        if (!TextUtils.isEmpty(textString)) {
            val textHeight: Float = textPaint!!.descent() + textPaint!!.ascent()
            canvas.drawText(
                text!!,
                (viewWidth - textPaint!!.measureText(text)) / 2.0f,
                (viewHeight - textHeight) / 2.0f,
                textPaint!!
            )
        }

        if (currentAngle <= progress_angle && animation) {
            currentAngle++
            try {
                Thread.sleep(animation_duration.toLong())
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            invalidate()
        }
    }

    fun setInnerBackgroundColor(borderColor: Int) {
        if (paintInnerBackgroud != null) paintInnerBackgroud!!.color = borderColor

        this.invalidate()
    }

    fun setProgressBackgroundColor(borderColor: Int) {
        if (paintProgressBackground != null) paintProgressBackground!!.color = borderColor

        this.invalidate()
    }

    fun setIndicatorBackground(borderColor: Int) {
        if (paintIndicatorBackground != null) paintIndicatorBackground!!.color = borderColor

        this.invalidate()
    }

    fun setTextColor(text_color: Int) {
        this.text_color = text_color

        if (textPaint != null) textPaint!!.setColor(text_color)

        this.invalidate()
    }

    var textString: String?
        get() = text
        set(text) {
            this.text = text
            this.invalidate()
        }

    fun setTextSize(text: Int) {
        this.text_size = text_size
        this.invalidate()
    }

    fun showShadow(shadow: Boolean) {
        this.shadow = shadow
        this.invalidate()
    }

    fun setShadowThickness(shadow_thickness: Int) {
        this.shadow_thickness = shadow_thickness
        this.invalidate()
    }


    fun setProgressThickness(progress_width: Int) {
        this.progress_width = progress_width
        this.invalidate()
    }

    fun showIndicator(indicator: Boolean) {
        this.indicator = indicator
        this.invalidate()
    }

    fun showIndicatorShadow(indicator_shadow: Boolean) {
        this.indicator_shadow = indicator_shadow
        this.invalidate()
    }

    fun setIndicatorThickness(indicator_width: Int) {
        this.indicator_width = indicator_width
        this.invalidate()
    }

    fun animation(animation: Boolean) {
        this.animation = animation
        currentAngle = 0f
        this.invalidate()
    }

    fun is_animation(): Boolean {
        return animation
    }

    fun setAnimationDuration(animation_duration: Int) {
        this.animation_duration = animation_duration
        this.invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        println("onMeasure : $widthMeasureSpec : $heightMeasureSpec")
        val borderWidth = 3

        val width = measureWidth(widthMeasureSpec)
        val height = measureHeight(heightMeasureSpec)

        viewWidth = width - (borderWidth * 2)
        viewHeight = height - (borderWidth * 2)

        setMeasuredDimension(width, height)
    }

    private fun measureWidth(measureSpec: Int): Int {
        var result = 0
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)

        result = if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            specSize
        } else {
            // Measure the text
            viewWidth
        }

        return result
    }

    private fun measureHeight(measureSpecHeight: Int): Int {
        var result = 0
        val specMode = MeasureSpec.getMode(measureSpecHeight)
        val specSize = MeasureSpec.getSize(measureSpecHeight)

        result = if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            specSize
        } else {
            // Measure the text (beware: ascent is a negative number)
            viewHeight
        }
        return (result + 2)
    }
}
