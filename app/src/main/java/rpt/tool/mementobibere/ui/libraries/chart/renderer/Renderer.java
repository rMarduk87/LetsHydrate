
package rpt.tool.mementobibere.ui.libraries.chart.renderer;

import rpt.tool.mementobibere.ui.libraries.chart.utils.ViewPortHandler;
import rpt.tool.mementobibere.ui.libraries.chart.utils.ViewPortHandler;


public abstract class Renderer {

    
    protected ViewPortHandler mViewPortHandler;

    public Renderer(ViewPortHandler viewPortHandler) {
        this.mViewPortHandler = viewPortHandler;
    }
}
