package rpt.tool.letshydrate.utils.view.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class CircularImageViewWhite : AppCompatImageView {
    private var borderWidth = 5
    private var viewWidth = 0
    private var viewHeight = 0
    private var image: Bitmap? = null
    private var paint: Paint? = null
    private var paintBorder: Paint? = null
    private var shader: BitmapShader? = null

    constructor(context: Context) : super(context) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        setup()
    }

    private fun setup() {
        // init paint
        paint = Paint()
        paint!!.isAntiAlias = true

        paintBorder = Paint()

        // setBorderColor(Color.rgb(38,154,250));
        setBorderColor(Color.WHITE)
        paintBorder!!.isAntiAlias = true
        this.setLayerType(LAYER_TYPE_SOFTWARE, paintBorder)
        //paintBorder.setShadowLayer(4.0f, 0.0f, 2.0f, Color.BLACK);	
    }

    fun setBorderWidth(borderWidth: Int) {
        this.borderWidth = borderWidth
        this.invalidate()
    }

    fun setBorderColor(borderColor: Int) {
        if (paintBorder != null) paintBorder!!.color = borderColor

        this.invalidate()
    }

    private fun getBitmapFromDrawable(drawable: Drawable): Bitmap {
        val bmp = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bmp)
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
    public override fun onDraw(canvas: Canvas) {
        //load the bitmap
        loadBitmap()


        // init shader
        if (image != null) {
            shader = BitmapShader(
                Bitmap.createScaledBitmap(image!!, width, height, false),
                Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP
            )
            paint!!.setShader(shader)
            val circleCenter = viewWidth / 2


            // circleCenter is the x or y of the view's center
            // radius is the radius in pixels of the cirle to be drawn
            // paint contains the shader that will texture the shape
            //canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth, circleCenter + borderWidth, paintBorder);
            //canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth, circleCenter, paint);
            canvas.drawCircle(
                (circleCenter + borderWidth).toFloat(),
                (circleCenter + borderWidth).toFloat(),
                circleCenter + borderWidth - 4.0f,
                paintBorder!!
            )
            canvas.drawCircle(
                (circleCenter + borderWidth).toFloat(),
                (circleCenter + borderWidth).toFloat(),
                circleCenter - 4.0f,
                paint!!
            )
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
