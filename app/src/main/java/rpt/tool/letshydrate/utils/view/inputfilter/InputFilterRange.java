package rpt.tool.letshydrate.utils.view.inputfilter;

import static rpt.tool.letshydrate.utils.log.LogUtilsKt.d;
import static rpt.tool.letshydrate.utils.log.LogUtilsKt.e;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

public class InputFilterRange implements InputFilter {

    private double min;
    List<Double> elements;

    public InputFilterRange(double min, List<Double> elements) {
        this.min = min;
        this.elements = elements;
    }

    @Override
    public CharSequence filter(@NonNull CharSequence source, int start, int end,
                               @NonNull Spanned dest, int dstart,
                               int dend) {
        try {
            String str=dest.toString() + source.toString();
            d("CharSequence"," -> "+str.length());

            double input = Double.parseDouble(dest.toString() + source.toString());
            if (isInRange(min, elements,input,str))
                return null;
        } catch (NumberFormatException nfe) {
            e(new Throwable(nfe), Objects.requireNonNull(nfe.getMessage()));
        }
        return "";
    }

    private boolean isInRange(double a, List<Double>  b, double c, @NonNull String cc) {
        if(cc.length()>4)
        {
            return false;
        }

        for(int k=0;k<b.size();k++)
        {
            if(b.get(k)==c)
                return true;
        }
        return false;
    }
}