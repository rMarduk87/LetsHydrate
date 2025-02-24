package rpt.tool.letshydrate.utils.view.custom.justify

import android.text.Layout
import android.text.Spannable
import android.text.Spanned
import android.text.TextPaint
import android.text.style.MetricAffectingSpan
import android.text.style.ScaleXSpan
import android.util.Log
import android.widget.TextView
import rpt.tool.letshydrate.utils.log.e
import java.util.regex.Pattern
import kotlin.math.ceil
import kotlin.math.floor

internal object Justify {
    private val WHITESPACE_PATTERN: Pattern = Pattern.compile("\\s")

    const val DEFAULT_MAX_PROPORTION: Float = 10f

    /**
     * Adds ScaleX spans to expand widespaces and justify the lines.
     *
     * @param justified          the justified TextView.
     * @param textViewSpanEnds   a preallocated array that will hold the span end positions.
     * @param textViewSpanStarts a preallocated array that will hold the span start positions.
     * @param textViewSpans      a preallocated array that will hold the spans.
     */
    fun setupScaleSpans(
        justified: Justified,
        textViewSpanStarts: IntArray,
        textViewSpanEnds: IntArray,
        textViewSpans: Array<ScaleSpan?>
    ) {
        val textView = justified.textView
        val text = textView.text

        val zero = if (textView.isInEditMode) 0.0001f else 0f

        // The text should be a spannable already because we set a movement method.
        if (text !is Spannable) return
        val spannable = text
        val length = spannable.length
        if (length == 0) return

        // Remove any existing ScaleXSpan (from a previous pass).
        val scaleSpans = spannable.getSpans(
            0, spannable.length,
            ScaleSpan::class.java
        )
        if (scaleSpans != null) {
            for (span in scaleSpans) {
                spannable.removeSpan(span)
            }
        }

        // We use the layout to get line widths before justification
        val layout = checkNotNull(textView.layout)
        val count = layout.lineCount
        if (count < 2) return

        // Layout line widths do not include the padding
        val want = textView.measuredWidth -
                textView.compoundPaddingLeft - textView.compoundPaddingRight

        // We won't justify lines if it requires expanding the spaces beyond the maximum proportion.
        val maxProportion = if (textView is Justified) {
            (textView as Justified).maxProportion
        } else {
            DEFAULT_MAX_PROPORTION
        }

        for (line in 0 until count) {
            val lineStart = layout.getLineStart(line)
            val lineEnd = if (line == count - 1) length else layout.getLineEnd(line)

            // Don't justify empty lines
            if (lineEnd == lineStart) continue

            // Don't justify the last line or lines ending with a newline.
            if (lineEnd == length || spannable[lineEnd - 1] == '\n') continue

            // Don't include the trailing whitespace as an expandable whitespace.
            val visibleLineEnd = layout.getLineVisibleEnd(line)

            //      int visibleLineEnd = lineEnd;
//      while (visibleLineEnd > lineStart &&
//             Character.isWhitespace(spannable.charAt(visibleLineEnd-1))) --visibleLineEnd;

            // Don't justify lines that only contain whitespace
            if (visibleLineEnd == lineStart) continue

            // Layout line width
//      final float w = layout.getLineWidth(line);    // Works fine, but only for API > 11
//      final float w = layout.getLineMax(line);      // Doesn't work well
            val w = Layout.getDesiredWidth(spannable, lineStart, lineEnd, layout.paint)

            // Remaining space to fill
            var remaining = floor((want - w).toDouble()) as Int

            if (remaining > 0) {
                // Make sure trailing whitespace doesn't use any space by setting its scaleX to 0
                if (visibleLineEnd < lineEnd) {
                    spannable.setSpan(
                        ScaleXSpan(zero),
                        visibleLineEnd,
                        lineEnd,
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                }

                // Line text
                val sub = spannable.subSequence(lineStart, visibleLineEnd)

                // Accumulated total whitespace width
                var spaceWidth = 0f
                // Number of whitespace sections
                var n = 0

                // Find whitespace sections and store their start and end positions
                val matcher = WHITESPACE_PATTERN.matcher(sub)
                while (matcher.find()) {
                    val matchStart = matcher.start()
                    val matchEnd = matcher.end()
                    // If the line starts with whitespace, it's probably an indentation
                    // and we don't want to expand indentation space to preserve alignment
                    if (matchStart == 0) continue
                    // skip single thin and hair spaces, as well as a single non breaking space
                    if ((matchEnd - matchStart) == 1) {
                        val c = sub[matchStart].code
                        if (c == '\u200a'.code || c == '\u2009'.code || c == '\u00a0'.code) continue
                    }
                    checkNotNull(layout.paint)
                    val matchWidth =
                        layout.paint.measureText(
                            spannable,
                            lineStart + matchStart,
                            lineStart + matchEnd
                        )

                    spaceWidth += matchWidth

                    textViewSpanStarts[n] = matchStart
                    textViewSpanEnds[n] = matchEnd
                    ++n
                }
                if (n > textViewSpans.size) {
                    n = textViewSpans.size
                }

                // Excess space is distributed evenly
                // (with the same proportions for all whitespace sections)
                val proportion = (spaceWidth + remaining) / spaceWidth

                // Don't justify the line if we can't do it without expanding whitespaces too much.
                if (proportion > maxProportion) continue

                // Add ScaleX spans on the whitespace sections we want to expand.
                for (span in 0 until n) {
                    textViewSpans[span] = ScaleSpan(proportion)
                    spannable.setSpan(
                        textViewSpans[span],
                        lineStart + textViewSpanStarts[span],
                        lineStart + textViewSpanEnds[span],
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                }
                // Compute the excess space.
                var excess = ceil(
                    Layout.getDesiredWidth(
                        spannable,
                        lineStart, lineEnd,
                        layout.paint
                    ).toDouble()
                ) as Int - want
                // We might have added too much space because of rounding errors and because adding spans
                // can modify the kerning.
                // If that is the case, then we try to reduce the extra space slightly until there's no
                // excess space left.
                var loop = 0
                while (excess > 0) {
                    if (++loop == 4) {
                        val e = Exception(
                            "Could not compensate for excess space (" + excess + "px)."
                        )
                        e.message?.let { e(Throwable(e), it) }
                    }
                    // Clear the spans from the previous attempt.
                    for (span in 0 until n) {
                        spannable.removeSpan(textViewSpans[span])
                    }
                    // Reduce the remaining space exponentially for each iteration.
                    remaining -= (excess + loop * loop)
                    // Set the spans with the new proportions.
                    val reducedProportions = (spaceWidth + remaining) / spaceWidth
                    for (span in 0 until n) {
                        textViewSpans[span] = ScaleSpan(reducedProportions)
                        spannable.setSpan(
                            textViewSpans[span],
                            lineStart + textViewSpanStarts[span],
                            lineStart + textViewSpanEnds[span],
                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                    }
                    // recompute the excess space.
                    excess = ceil(
                        Layout.getDesiredWidth(
                            spannable,
                            lineStart, lineEnd,
                            layout.paint
                        ).toDouble()
                    ).toInt() - want
                }
            }
        }
    }

    internal interface Justified {
        /**
         * Gets the TextView (usually the class implementing this interface).
         *
         * @return the TextView.
         */
        val textView: TextView

        /**
         * Gets the maximum stretching proportion allowed for whitespaces. Lines that require
         * expanding whitespace beyond this proportion to be justified will not be justified.
         *
         * @return the maximum stretching proportion allowed.
         */
        val maxProportion: Float
    }

    internal class ScaleSpan(private val mProportion: Float) : MetricAffectingSpan() {
        override fun updateDrawState(ds: TextPaint) {
            ds.textScaleX *= mProportion
        }

        override fun updateMeasureState(ds: TextPaint) {
            ds.textScaleX *= mProportion
        }
    }
}