package rpt.tool.mementobibere.utils.view.custom

import android.annotation.SuppressLint
import android.content.res.TypedArray
import android.graphics.BitmapShader
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.Drawable
import rpt.tool.mementobibere.R

@SuppressLint("AppCompatCustomView")
class ShapeImageView : android.widget.ImageView {
    var mContext: android.content.Context

    private var viewWidth = 0
    private var viewHeight = 0
    private var image: android.graphics.Bitmap? = null

    private var paint: android.graphics.Paint? = null
    private var paintBorder: android.graphics.Paint? = null

    private var shader: BitmapShader? = null

    private var border = false
    private var borderWidth = 8
    private var borderColor = android.graphics.Color.BLUE
    private var shape = 0
    private var shadow = false
    private var shadowColor = android.graphics.Color.GRAY
    private var shadowThickness = 8


    constructor(context: android.content.Context) : super(context) {
        mContext = context
        init(null)
    }

    constructor(
        context: android.content.Context,
        attrs: android.util.AttributeSet?
    ) : super(context, attrs) {
        mContext = context
        init(attrs)
    }

    constructor(
        context: android.content.Context,
        attrs: android.util.AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        mContext = context
        init(attrs)
    }

    @SuppressLint("Recycle")
    fun init(attrs: android.util.AttributeSet?) {
        if (attrs != null) {
            val typedArray: TypedArray =
                mContext.obtainStyledAttributes(attrs, R.styleable.ShapeImageView)

            shadow = typedArray.getBoolean(R.styleable.ShapeImageView_siv_shadow, shadow)
            shadowColor =
                typedArray.getColor(R.styleable.ShapeImageView_siv_shadow_color, shadowColor)
            shadowThickness = typedArray.getInteger(
                R.styleable.ShapeImageView_siv_shadow_thickness,
                shadowThickness
            )

            border = typedArray.getBoolean(R.styleable.ShapeImageView_siv_border, border)
            borderColor =
                typedArray.getColor(R.styleable.ShapeImageView_siv_border_color, borderColor)
            borderWidth =
                typedArray.getInteger(R.styleable.ShapeImageView_siv_border_width, borderWidth)

            shape = typedArray.getInteger(R.styleable.ShapeImageView_siv_shape, shape)
        }

        println("shape : $shape")

        setup()
    }

    private fun setup() {
        paint = android.graphics.Paint()
        paint!!.isAntiAlias = true

        paintBorder = android.graphics.Paint()
        setBorderColor(borderColor)
        paintBorder!!.isAntiAlias = true
        this.setLayerType(LAYER_TYPE_SOFTWARE, paintBorder)
    }

    fun setBorderWidth(borderWidth: Int) {
        this.borderWidth = borderWidth
        this.invalidate()
    }

    fun setBorderColor(borderColor: Int) {
        if (paintBorder != null) paintBorder!!.color = borderColor

        this.invalidate()
    }

    private fun getBitmapFromDrawable(drawable: Drawable): android.graphics.Bitmap {
        val bmp = android.graphics.Bitmap.createBitmap(
            drawable.getIntrinsicWidth(),
            drawable.getIntrinsicHeight(),
            android.graphics.Bitmap.Config.ARGB_8888
        )
        val canvas = android.graphics.Canvas(bmp)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bmp
    }

    private fun loadBitmap() {
        /*BitmapDrawable bitmapDrawable = (BitmapDrawable) this.getDrawable();

		if(bitmapDrawable != null)
			image = bitmapDrawable.getBitmap();*/

        image = getBitmapFromDrawable(this.drawable)
    }

    @SuppressLint("DrawAllocation")
    public override fun onDraw(canvas: android.graphics.Canvas) {
        //load the bitmap
        loadBitmap()

        // init shader
        if (image != null) {
            shader = BitmapShader(
                android.graphics.Bitmap.createScaledBitmap(
                    image!!,
                    width,
                    height,
                    false
                ), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP
            )
            paint!!.setShader(shader)
            val centerX = viewWidth / 2
            val centerY = viewHeight / 2

            if (shadow && border) paintBorder!!.setShadowLayer(
                shadowThickness.toFloat(),
                0f,
                0f,
                shadowColor
            )
            else if (shadow) paint!!.setShadowLayer(shadowThickness.toFloat(), 0f, 0f, shadowColor)


            if (shape == 0)  //======Circle======
            {
                if (border) canvas.drawCircle(
                    (centerX + borderWidth).toFloat(),
                    (centerY + borderWidth).toFloat(),
                    (centerX + borderWidth).toFloat(),
                    paintBorder!!
                )
                canvas.drawCircle(
                    (centerX + borderWidth).toFloat(),
                    (centerY + borderWidth).toFloat(),
                    centerX.toFloat(),
                    paint!!
                )
            } else if (shape == 1)  //======Square======
            {
                val r: RectF = RectF(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
                if (border) paintBorder?.let { canvas.drawRect(r, it) }
                val r2: RectF = RectF(
                    (0 + borderWidth).toFloat(),
                    (0 + borderWidth).toFloat(),
                    (viewWidth - borderWidth).toFloat(),
                    (viewHeight - borderWidth).toFloat()
                )
                canvas.drawRect(r2, paint!!)
            } else if (shape == 2)  //======RoundRect======
            {
                val rr: RectF = RectF(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
                if (border) canvas.drawRoundRect(rr, 20f, 20f, paintBorder!!)
                val rr2: RectF = RectF(
                    (0 + borderWidth).toFloat(),
                    (0 + borderWidth).toFloat(),
                    (viewWidth - borderWidth).toFloat(),
                    (viewHeight - borderWidth).toFloat()
                )
                canvas.drawRoundRect(rr2, 20f, 20f, paint!!)
            } else if (shape == 3)  //======ARC======
            {
                val ar: RectF = RectF(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
                if (border) canvas.drawArc(ar, 180f, 180f, true, paintBorder!!)
                val ar2: RectF = RectF(
                    (0 + borderWidth).toFloat(),
                    (0 + borderWidth).toFloat(),
                    (viewWidth - borderWidth).toFloat(),
                    (viewHeight - borderWidth).toFloat()
                )
                canvas.drawArc(ar2, 180f, 180f, true, paint!!)
            } else if (shape == 4)  //======STAR======
            {
                val min =
                    kotlin.math.min(viewWidth.toDouble(), viewHeight.toDouble()).toFloat()

                val half = min / 2

                val mid = centerX - half

                val spath = android.graphics.Path()
                // top left
                spath.moveTo(mid + half * 0.5f, half * 0.84f)
                // top right
                spath.lineTo(mid + half * 1.5f, half * 0.84f)
                // bottom left
                spath.lineTo(mid + half * 0.68f, half * 1.45f)
                // top tip
                spath.lineTo(mid + half * 1.0f, half * 0.5f)
                // bottom right
                spath.lineTo(mid + half * 1.32f, half * 1.45f)
                if (border) canvas.drawPath(spath, paintBorder!!)

                val spath2 = android.graphics.Path()
                // top left
                spath2.moveTo(mid + half * 0.55f, half * 0.86f)
                // top right
                spath2.lineTo(mid + half * 1.45f, half * 0.86f)
                // bottom left
                spath2.lineTo(mid + half * 0.72f, half * 1.40f)
                // top tip
                spath2.lineTo(mid + half * 1.0f, half * 0.55f)
                // bottom right
                spath2.lineTo(mid + half * 1.28f, half * 1.40f)
                canvas.drawPath(spath2, paint!!)
            } else if (shape == 5)  //======HEART======
            {
                val width = viewWidth.toFloat()
                val height = viewHeight.toFloat()

                val hpath = android.graphics.Path()

                // Starting point
                hpath.moveTo(width / 2, height / 5)

                // Upper left path
                hpath.cubicTo(
                    5 * width / 14, 0f,
                    0f, height / 15,
                    width / 28, 2 * height / 5
                )

                // Lower left path
                hpath.cubicTo(
                    width / 14, 2 * height / 3,
                    3 * width / 7, 5 * height / 6,
                    width / 2, height
                )

                // Lower right path
                hpath.cubicTo(
                    4 * width / 7, 5 * height / 6,
                    13 * width / 14, 2 * height / 3,
                    27 * width / 28, 2 * height / 5
                )

                // Upper right path
                hpath.cubicTo(
                    width, height / 15,
                    9 * width / 14, 0f,
                    width / 2, height / 5
                )

                canvas.drawPath(hpath, paint!!)

                /*Path hpath2 = new Path();

				// Starting point
				hpath2.moveTo(width / 2, height / 5 + borderWidth);

				// Upper left path
				hpath2.cubicTo(5 * width / 14 + borderWidth, 0 + borderWidth,
						0 + borderWidth, height / 15,
						width / 28 + borderWidth, 2 * height / 5 + borderWidth);

				// Lower left path
				hpath2.cubicTo(width / 14 + borderWidth, 2 * height / 3 + borderWidth,
						3 * width / 7 + borderWidth*2, 5 * height / 6,
						width / 2, height - borderWidth);

				// Lower right path
				hpath2.cubicTo(4 * width / 7 - borderWidth, 5 * height / 6,
						13 * width / 14 - borderWidth*2, 2 * height / 3,
						27 * width / 28 - borderWidth, 2 * height / 5 - borderWidth);

				// Upper right path
				hpath2.cubicTo(width - borderWidth, height / 15 -borderWidth,
						9 * width / 14 - borderWidth*2, 0 -borderWidth,
						width / 2 - borderWidth, height / 5 +borderWidth);

				canvas.drawPath(hpath2, paint);*/
            } else if (shape == 6)  //======Hexagon======
            {
                val path = android.graphics.Path()
                path.moveTo(0f, centerY.toFloat())
                path.lineTo(((viewWidth * 25) / 100).toFloat(), viewHeight.toFloat())
                path.lineTo(((viewWidth * 75) / 100).toFloat(), viewHeight.toFloat())
                path.lineTo(viewWidth.toFloat(), centerY.toFloat())
                path.lineTo(((viewWidth * 75) / 100).toFloat(), 0f)
                path.lineTo(((viewWidth * 25) / 100).toFloat(), 0f)
                if (border) canvas.drawPath(path, paintBorder!!)

                val path2 = android.graphics.Path()
                path2.moveTo((0 + borderWidth).toFloat(), centerY.toFloat())
                path2.lineTo(
                    ((viewWidth * 25) / 100 + borderWidth).toFloat(),
                    (viewHeight - borderWidth).toFloat()
                )
                path2.lineTo(
                    ((viewWidth * 75) / 100 - borderWidth).toFloat(),
                    (viewHeight - borderWidth).toFloat()
                )
                path2.lineTo((viewWidth - borderWidth).toFloat(), centerY.toFloat())
                path2.lineTo(
                    ((viewWidth * 75) / 100 - borderWidth).toFloat(),
                    (0 + borderWidth).toFloat()
                )
                path2.lineTo(
                    ((viewWidth * 25) / 100 + borderWidth).toFloat(),
                    (0 + borderWidth).toFloat()
                )
                canvas.drawPath(path2, paint!!)
            } else if (shape == 7)  //======Octagon======
            {
                val opath = android.graphics.Path()
                opath.moveTo(0f, ((viewHeight * 30) / 100).toFloat())
                opath.lineTo(0f, ((viewHeight * 70) / 100).toFloat())
                opath.lineTo(((viewWidth * 30) / 100).toFloat(), viewHeight.toFloat())
                opath.lineTo(((viewWidth * 70) / 100).toFloat(), viewHeight.toFloat())
                opath.lineTo(viewWidth.toFloat(), ((viewHeight * 70) / 100).toFloat())
                opath.lineTo(viewWidth.toFloat(), ((viewHeight * 30) / 100).toFloat())
                opath.lineTo(((viewWidth * 70) / 100).toFloat(), 0f)
                opath.lineTo(((viewWidth * 30) / 100).toFloat(), 0f)
                if (border) canvas.drawPath(opath, paintBorder!!)

                val opath2 = android.graphics.Path()
                opath2.moveTo((0 + borderWidth).toFloat(), ((viewHeight * 30) / 100).toFloat())
                opath2.lineTo((0 + borderWidth).toFloat(), ((viewHeight * 70) / 100).toFloat())
                opath2.lineTo(
                    ((viewWidth * 30) / 100 + borderWidth).toFloat(),
                    (viewHeight - borderWidth).toFloat()
                )
                opath2.lineTo(
                    ((viewWidth * 70) / 100 - borderWidth).toFloat(),
                    (viewHeight - borderWidth).toFloat()
                )
                opath2.lineTo(
                    (viewWidth - borderWidth).toFloat(),
                    ((viewHeight * 70) / 100).toFloat()
                )
                opath2.lineTo(
                    (viewWidth - borderWidth).toFloat(),
                    ((viewHeight * 30) / 100).toFloat()
                )
                opath2.lineTo(
                    ((viewWidth * 70) / 100 - borderWidth).toFloat(),
                    (0 + borderWidth).toFloat()
                )
                opath2.lineTo(
                    ((viewWidth * 30) / 100 + borderWidth).toFloat(),
                    (0 + borderWidth).toFloat()
                )
                canvas.drawPath(opath2, paint!!)
            } else if (shape == 8)  //======Diamond======
            {
                val dpath = android.graphics.Path()
                dpath.moveTo(0f, centerY.toFloat())
                dpath.lineTo(centerX.toFloat(), viewHeight.toFloat())
                dpath.lineTo(viewWidth.toFloat(), centerY.toFloat())
                dpath.lineTo(centerX.toFloat(), 0f)
                if (border) canvas.drawPath(dpath, paintBorder!!)

                val dpath2 = android.graphics.Path()
                dpath2.moveTo((0 + borderWidth).toFloat(), centerY.toFloat())
                dpath2.lineTo(centerX.toFloat(), (viewHeight - borderWidth).toFloat())
                dpath2.lineTo((viewWidth - borderWidth).toFloat(), centerY.toFloat())
                dpath2.lineTo(centerX.toFloat(), (0 + borderWidth).toFloat())
                canvas.drawPath(dpath2, paint!!)
            } else if (shape == 9)  //======Pentagon======
            {
                val ppath = android.graphics.Path()
                ppath.moveTo(0f, ((viewHeight * 30) / 100).toFloat())
                ppath.lineTo(((viewWidth * 15) / 100).toFloat(), viewHeight.toFloat())
                ppath.lineTo(((viewWidth * 85) / 100).toFloat(), viewHeight.toFloat())
                ppath.lineTo(viewWidth.toFloat(), ((viewHeight * 30) / 100).toFloat())
                ppath.lineTo(centerX.toFloat(), 0f)
                if (border) canvas.drawPath(ppath, paintBorder!!)

                val ppath2 = android.graphics.Path()
                ppath2.moveTo(
                    (0 + borderWidth + (borderWidth / 2)).toFloat(),
                    ((viewHeight * 30) / 100 + (borderWidth / 2)).toFloat()
                )
                ppath2.lineTo(
                    ((viewWidth * 15) / 100 + borderWidth).toFloat(),
                    (viewHeight - borderWidth).toFloat()
                )
                ppath2.lineTo(
                    ((viewWidth * 85) / 100 - borderWidth).toFloat(),
                    (viewHeight - borderWidth).toFloat()
                )
                ppath2.lineTo(
                    (viewWidth - borderWidth - (borderWidth / 2)).toFloat(),
                    ((viewHeight * 30) / 100 + (borderWidth / 2)).toFloat()
                )
                ppath2.lineTo(centerX.toFloat(), (0 + borderWidth + (borderWidth / 2)).toFloat())
                canvas.drawPath(ppath2, paint!!)
            } else if (shape == 10)  //======CUSTOM======
            {
                val cr: RectF = RectF(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
                if (border) canvas.drawArc(cr, 135f, 270f, false, paintBorder!!)
                val cr2: RectF = RectF(
                    (0 + borderWidth).toFloat(),
                    (0 + borderWidth).toFloat(),
                    (viewWidth - borderWidth).toFloat(),
                    (viewHeight - borderWidth).toFloat()
                )
                canvas.drawArc(cr2, 135f, 270f, false, paint!!)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = measureWidth(widthMeasureSpec)
        val height = measureHeight(heightMeasureSpec, widthMeasureSpec)

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

    private fun measureHeight(measureSpecHeight: Int, measureSpecWidth: Int): Int {
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