package rpt.tool.mementobibere.utils.view.inputfilter;

import static rpt.tool.mementobibere.utils.log.LogUtilsKt.e;
import android.text.InputFilter;
import android.text.Spanned;
import androidx.annotation.NonNull;
import org.jetbrains.annotations.Contract;
import java.util.Objects;

public class InputFilterWeightRange implements InputFilter {

    private double min;
    private double max;

    public InputFilterWeightRange(double min,double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public CharSequence filter(@NonNull CharSequence source, int start, int end,
                               @NonNull Spanned dest, int dstart, int dend) {
        try {
            String str=dest.toString() + source.toString();

            double input = Double.parseDouble(dest.toString() + source.toString());
            if (isInRange(min, max,input,str))
                return null;
        } catch (NumberFormatException nfe) {
            e(new Throwable(nfe), Objects.requireNonNull(nfe.getMessage()));
        }
        return "";
    }

    @Contract(pure = true)
    private boolean isInRange(double a, double  b, double c, @NonNull String cc) {
        if(cc.length()>5)
        {
            return false;
        }
        if(c>b)
        {
            return false;
        }
        if(c<a)
        {
            return false;
        }

        return c % 0.5 == 0;
    }
}
