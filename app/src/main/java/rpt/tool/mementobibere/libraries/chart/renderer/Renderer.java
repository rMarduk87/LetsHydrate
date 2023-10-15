
package rpt.tool.mementobibere.libraries.chart.renderer;

import rpt.tool.mementobibere.libraries.chart.utils.ViewPortHandler;


public abstract class Renderer {

    
    protected ViewPortHandler mViewPortHandler;

    public Renderer(ViewPortHandler viewPortHandler) {
        this.mViewPortHandler = viewPortHandler;
    }
}
