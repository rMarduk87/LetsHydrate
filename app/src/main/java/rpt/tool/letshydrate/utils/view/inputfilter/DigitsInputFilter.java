package rpt.tool.letshydrate.utils.view.inputfilter;

import android.text.InputFilter;
import android.text.Spanned;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.jetbrains.annotations.Contract;

public class DigitsInputFilter implements InputFilter {

    private final String DOT = ".";
    private int mMaxIntegerDigitsLength;
    private int mMaxDigitsAfterLength;
    private double mMax;


    public DigitsInputFilter(int maxDigitsBeforeDot, int maxDigitsAfterDot, double maxValue) {
        mMaxIntegerDigitsLength = maxDigitsBeforeDot;
        mMaxDigitsAfterLength = maxDigitsAfterDot;
        mMax = maxValue;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String allText = getAllText(source, dest, dstart);
        String onlyDigitsText = getOnlyDigitsPart(allText);

        if (allText.isEmpty()) {
            return null;
        } else {
            double enteredValue;
            try {
                enteredValue = Double.parseDouble(onlyDigitsText);
            } catch (NumberFormatException e) {
                return "";
            }
            return checkMaxValueRule(enteredValue, onlyDigitsText);
        }
    }


    private CharSequence checkMaxValueRule(double enteredValue, String onlyDigitsText) {
        if (enteredValue > mMax) {
            return "";
        } else {
            return handleInputRules(onlyDigitsText);
        }
    }

    private CharSequence handleInputRules(String onlyDigitsText) {
        if (isDecimalDigit(onlyDigitsText)) {
            return checkRuleForDecimalDigits(onlyDigitsText);
        } else {
            return checkRuleForIntegerDigits(onlyDigitsText.length());
        }
    }

    @Contract(pure = true)
    private boolean isDecimalDigit(@NonNull String onlyDigitsText) {
        return onlyDigitsText.contains(DOT);
    }

    @Nullable
    private CharSequence checkRuleForDecimalDigits(@NonNull String onlyDigitsPart) {
        String afterDotPart = onlyDigitsPart.substring(onlyDigitsPart.indexOf(DOT), onlyDigitsPart.length() - 1);
        if (afterDotPart.length() > mMaxDigitsAfterLength) {
            return "";
        }
        return null;
    }

    @Nullable
    @Contract(pure = true)
    private CharSequence checkRuleForIntegerDigits(int allTextLength) {
        if (allTextLength > mMaxIntegerDigitsLength) {
            return "";
        }
        return null;
    }

    @NonNull
    private String getOnlyDigitsPart(@NonNull String text) {
        return text.replaceAll("[^0-9?!\\.]", "");
    }

    private String getAllText(CharSequence source, @NonNull Spanned dest, int dstart) {
        String allText = "";
        if (!dest.toString().isEmpty()) {
            if (source.toString().isEmpty()) {
                allText = deleteCharAtIndex(dest, dstart);
            } else {
                allText = new StringBuilder(dest).insert(dstart, source).toString();
            }
        }
        return allText;
    }

    @NonNull
    private String deleteCharAtIndex(Spanned dest, int dstart) {
        StringBuilder builder = new StringBuilder(dest);
        builder.deleteCharAt(dstart);
        return builder.toString();
    }
}