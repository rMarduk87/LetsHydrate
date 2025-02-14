package rpt.tool.mementobibere.utils.view.custom

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.webkit.WebView
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.utils.log.d

@SuppressLint("AppCompatCustomView")
class HTMLTextView : WebView {
    var fontName: String? = ""
    var text_size: Int = 12
    var text_align: String = "justify"
    var lastLineTextAlign: String = "left"
    var isRTL: Boolean = false
    var text_bold: Boolean = false
    var language_code: String? = ""
    var text: String? = ""
    var line_height: Float = 1.5f
    var total_height: Float = 0.0f
    var view_line: Int = 0
    var text_color: Int = Color.BLACK
    var textColorHash: String = "#000000"


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context) : super(context) {
        init(null)
    }

    private fun init(attrs: AttributeSet?) {
        if (attrs != null) {
            val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.HTMLTextView)
            fontName = a.getString(R.styleable.HTMLTextView_htv_fontname)
            text = a.getString(R.styleable.HTMLTextView_htv_text)
            language_code = a.getString(R.styleable.HTMLTextView_htv_language_code)
            text_align = get_align_type(a.getInteger(R.styleable.HTMLTextView_htv_text_align, 0))
            lastLineTextAlign =
                get_align_type(a.getInteger(R.styleable.HTMLTextView_htv_text_align_last, 0))
            text_size = a.getInteger(R.styleable.HTMLTextView_htv_text_size, text_size)
            isRTL = a.getBoolean(R.styleable.HTMLTextView_htv_rtl, isRTL)
            text_bold = a.getBoolean(R.styleable.HTMLTextView_htv_text_bold, text_bold)
            view_line = a.getInteger(R.styleable.HTMLTextView_htv_view_line, view_line)
            text_color = a.getColor(R.styleable.HTMLTextView_htv_text_color, text_color)
            textColorHash = "#" + String.format("%X", text_color).substring(2)

            //System.out.print("text_color_hash :"+text_color_hash);
            a.recycle()
        }

        load_data()
    }

    private fun get_align_type(index: Int): String {
        when (index) {
            1 -> return "left"
            2 -> return "right"
            3 -> return "center"
            else -> return "justify"
        }
    }

    private fun get_total_height() {
        total_height = view_line * line_height
    }

    private fun load_data() {
        try {
            get_total_height()

            var str_direction = ""
            var str_lang_code = ""
            var str_style = ""

            if (view_line > 0) str_style += "<style>p{line-height:" + line_height + "em;height:" + total_height + "em;overflow:hidden;}</style>"

            if (isRTL) str_direction = "dir=\"rtl\""

            if (!TextUtils.isEmpty(language_code)) str_lang_code =
                "lang=$language_code"

            if (!TextUtils.isEmpty(fontName)) str_style += "<style>@font-face {font-family: 'verdana';src: url('file:///android_asset/$fontName');}body {font-family: 'verdana';}</style>"

            //white-space: nowrap;
            val str_start = """<html $str_direction $str_lang_code>
 <head>$str_style</head>
 <body style="text-align:$text_align;text-align-last:${lastLineTextAlign};font-size:${text_size}px;color:${textColorHash};">"""
            val str_stop = "</body></html>"

            text = if (text_bold) "<p><b>$text</b></p>"
            else "<p>$text</p>"

            setBackgroundColor(Color.TRANSPARENT)
            if (!TextUtils.isEmpty(text)) {
                println("\n\n DATA START : $str_start")
                println("\n\n DATA BODY : $text")
                println("\n\n DATA STOP : $str_stop")

                loadData(str_start + text + str_stop, "text/html; charset=utf-8", "utf-8")
            }

            println("DATA : $str_start$text$str_stop")
            d("DATA : ", str_start + text + str_stop)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var font: String?
        get() = fontName
        set(fontName) {
            this.fontName = fontName
            load_data()
        }


    var languageCode: String?
        get() = language_code
        set(language_code) {
            this.language_code = language_code
            load_data()
        }

    fun setTextAlign(text_align: Int) {
        this.text_align = get_align_type(text_align)
        load_data()
    }

    var textAlign: String
        get() = text_align
        set(text_align) {
            this.text_align = text_align
            load_data()
        }

    fun setLastLineTextAlign(text_align_last: Int) {
        this.lastLineTextAlign = get_align_type(text_align_last)
        load_data()
    }

    var textSize: Int
        get() = text_size
        set(text_size) {
            this.text_size = text_size
            load_data()
        }

    var maxLine: Int
        get() = view_line
        set(view_line) {
            this.view_line = view_line
            load_data()
        }

    fun setTextColor(text_color_hash: String) {
        this.textColorHash = text_color_hash
        load_data()
    }

    var textColor: Int
        get() = text_color
        set(text_color) {
            this.text_color = text_color
            textColorHash = "#" + String.format("%X", text_color).substring(2)
            load_data()
        }

    fun SetRTL(r2l: Boolean) {
        this.isRTL = r2l
        load_data()
    }

    var textBold: Boolean
        get() = text_bold
        set(text_bold) {
            this.text_bold = text_bold
            load_data()
        }
}
