package rpt.tool.letshydrate.utils.view.custom

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Typeface
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.method.MovementMethod
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TextView
import rpt.tool.letshydrate.R

@SuppressLint("AppCompatCustomView")
class ReadMoreView : LinearLayout {
    var mContext: Context

    var txt_desc: TextView? = null
    var txt_read_more: TextView? = null

    var text: String? =
        "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.<br><br>"

    var read_more_text: String? = "Read More"
    var read_less_text: String? = "Read Less"

    var fontName: String? = ""
    var text_size: Int = 12
    var text_color: Int = Color.BLACK
    var readmore_text_color: Int = Color.BLUE
    var max_line: Int = 1

    var text_bold: Boolean = false

    constructor(context: Context) : super(context) {
        mContext = context
        setup(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        mContext = context
        setup(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        mContext = context
        setup(attrs)
    }

    private fun setup(attrs: AttributeSet?) {
        if (attrs != null) {
            val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.ReadMoreView)

            fontName = a.getString(R.styleable.ReadMoreView_rm_fontname)
            text = a.getString(R.styleable.ReadMoreView_rm_text)

            if (TextUtils.isEmpty(text)) text = "Text"

            read_more_text = a.getString(R.styleable.ReadMoreView_rm_read_more_text)

            if (TextUtils.isEmpty(read_more_text)) read_more_text = "Read More"

            read_less_text = a.getString(R.styleable.ReadMoreView_rm_read_less_text)

            if (TextUtils.isEmpty(read_less_text)) read_more_text = "Read Less"

            text_size = a.getInteger(R.styleable.ReadMoreView_rm_text_size, text_size)
            text_color = a.getColor(R.styleable.ReadMoreView_rm_text_color, text_color)
            readmore_text_color =
                a.getInteger(R.styleable.ReadMoreView_rm_read_more_text_color, readmore_text_color)

            text_bold = a.getBoolean(R.styleable.ReadMoreView_rm_text_bold, text_bold)
            max_line = a.getInteger(R.styleable.ReadMoreView_rm_max_line, max_line)

            a.recycle()
        }

        this._create()
    }

    fun _create() {
        orientation = HORIZONTAL
        setPadding(20, 0, 20, 0)

        txt_desc = TextView(mContext)
        txt_desc!!.setTextColor(textColor)
        txt_desc!!.textSize = textSize.toFloat()
        setTypeFace(txt_desc!!)
        val des: String = Html.fromHtml(text!!.trim { it <= ' ' }).toString()
        txt_desc!!.text = Html.fromHtml(des)

        txt_desc!!.layoutParams = TableLayout.LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            1f
        )


        txt_read_more = TextView(mContext)
        txt_read_more!!.setTextColor(readMoreTextColor)
        txt_read_more!!.textSize = textSize.toFloat()
        setTypeFace(txt_read_more!!)
        txt_read_more!!.text = readMoreText


        if (text_bold) {
            txt_desc!!.setTypeface(txt_desc!!.typeface, Typeface.BOLD)
            txt_read_more!!.setTypeface(txt_read_more!!.typeface, Typeface.BOLD)
        }

        txt_desc!!.post {
            val lineCount: Int = txt_desc!!.lineCount

            if (lineCount <= maxLine) txt_read_more!!.visibility = View.GONE
            else {
                txt_read_more!!.visibility = View.VISIBLE

                txt_desc!!.maxLines = maxLine
                if (max_line == 1) txt_desc!!.isSingleLine = true
                txt_desc!!.ellipsize = TextUtils.TruncateAt.END
            }

            //============

            /*Layout layout = txt_desc.getLayout();
                    String text = txt_desc.getText().toString();
                    int start=0;
                    int end;
                    String line[]=new String[txt_desc.getLineCount()];

                    for (int i=0; i<txt_desc.getLineCount(); i++)
                    {
                        end = layout.getLineEnd(i);
                        line[i] = text.substring(start,end);
                        System.out.println("List Line "+i+" : "+text.substring(start,end));
                        Log.d("List Line "+i,""+text.substring(start,end));
                        Toast.makeText(mContext,"List Line "+i+" : "+text.substring(start,end),1).show();
                        start = end;
                    }*/
        }

        clickify_read_more_click(txt_desc!!, txt_read_more!!, text)

        addView(txt_desc)
        addView(txt_read_more)
    }

    var readMoreText: String?
        get() = read_more_text
        set(read_more_text) {
            this.read_more_text = read_more_text
            _create()
        }

    var readLessText: String?
        get() = read_less_text
        set(read_less_text) {
            this.read_less_text = read_less_text
            _create()
        }

    var font: String?
        get() = fontName
        set(fontName) {
            this.fontName = fontName
            _create()
        }

    var textSize: Int
        get() = text_size
        set(text_size) {
            this.text_size = text_size
            _create()
        }

    var maxLine: Int
        get() = max_line
        set(max_line) {
            this.max_line = max_line
            _create()
        }

    var textColor: Int
        get() = text_color
        set(text_color) {
            this.text_color = text_color
            _create()
        }

    var readMoreTextColor: Int
        get() = readmore_text_color
        set(readmore_text_color) {
            this.readmore_text_color = readmore_text_color
            _create()
        }

    var textBold: Boolean
        get() = text_bold
        set(text_bold) {
            this.text_bold = text_bold
            _create()
        }


    private fun setTypeFace(txt: TextView) {
        try {
            if (fontName != null) {
                val myTypeface = Typeface.createFromAsset(context.assets, fontName)
                txt.setTypeface(myTypeface)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun clickify_read_more_click(txt: TextView, read_more: View, str: String?) {
        read_more.setOnClickListener {
            val des: String =
                Html.fromHtml(text!!.trim { it <= ' ' } + " " + readLessText)
                    .toString()
            txt.text = Html.fromHtml(des)

            txt.maxLines = Int.MAX_VALUE //your TextView
            txt.isSingleLine = false
            txt.ellipsize = null
            read_more.visibility = View.GONE

            clickify_read_less_click(txt, read_more, str)
        }
    }

    private fun clickify_read_less_click(txt: TextView, read_more: View, str: String?) {
        clickify(txt, "" + read_less_text, object : ClickSpan.OnClickListener {
            override fun onClick() {
                try {
                    txt.maxLines = maxLine //your TextView
                    if (max_line == 1) txt.isSingleLine = true

                    val des: String = Html.fromHtml(text!!.trim { it <= ' ' }).toString()
                    txt.text = Html.fromHtml(des)

                    read_more.visibility = View.VISIBLE
                    txt.ellipsize = TextUtils.TruncateAt.END
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun clickify(
        view: TextView,
        clickableText: String,
        listener: ClickSpan.OnClickListener
    ) {
        val text: CharSequence = view.text
        val string = text.toString()
        val span = ClickSpan(listener)
        span.setTextColor(readmore_text_color)

        val start = string.indexOf(clickableText)
        val end = start + clickableText.length
        if (start == -1) return

        if (text is Spannable) {
            (text as Spannable).setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        } else {
            val s: SpannableString = SpannableString.valueOf(text)
            s.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            view.text = s
        }

        val m: MovementMethod = view.movementMethod
        if (m !is LinkMovementMethod) {
            view.movementMethod = LinkMovementMethod.getInstance()
        }
    }
}