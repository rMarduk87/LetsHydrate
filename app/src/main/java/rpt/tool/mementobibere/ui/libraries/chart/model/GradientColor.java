package rpt.tool.mementobibere.ui.libraries.chart.model;

import rpt.tool.mementobibere.ui.libraries.chart.utils.Fill;
import rpt.tool.mementobibere.ui.libraries.chart.utils.Fill;


@Deprecated
public class GradientColor extends Fill
{

    @Deprecated
    public int getStartColor()
    {
        return getGradientColors()[0];
    }


    @Deprecated
    public void setStartColor(int startColor)
    {
        if (getGradientColors() == null || getGradientColors().length != 2)
        {
            setGradientColors(new int[]{
                    startColor,
                    getGradientColors() != null && getGradientColors().length > 1
                            ? getGradientColors()[1]
                            : 0
            });
        } else
        {
            getGradientColors()[0] = startColor;
        }
    }


    @Deprecated
    public int getEndColor()
    {
        return getGradientColors()[1];
    }


    @Deprecated
    public void setEndColor(int endColor)
    {
        if (getGradientColors() == null || getGradientColors().length != 2)
        {
            setGradientColors(new int[]{
                    getGradientColors() != null && getGradientColors().length > 0
                            ? getGradientColors()[0]
                            : 0,
                    endColor
            });
        } else
        {
            getGradientColors()[1] = endColor;
        }
    }

}
