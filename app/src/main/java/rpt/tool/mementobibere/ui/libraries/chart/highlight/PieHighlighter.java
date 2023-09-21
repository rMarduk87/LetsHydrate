package rpt.tool.mementobibere.ui.libraries.chart.highlight;

import rpt.tool.mementobibere.ui.libraries.chart.charts.PieChart;
import rpt.tool.mementobibere.ui.libraries.chart.data.Entry;
import rpt.tool.mementobibere.ui.libraries.chart.interfaces.datasets.IPieDataSet;
import rpt.tool.mementobibere.ui.libraries.chart.charts.PieChart;
import rpt.tool.mementobibere.ui.libraries.chart.data.Entry;
import rpt.tool.mementobibere.ui.libraries.chart.interfaces.datasets.IPieDataSet;


public class PieHighlighter extends PieRadarHighlighter<PieChart> {

    public PieHighlighter(PieChart chart) {
        super(chart);
    }

    @Override
    protected Highlight getClosestHighlight(int index, float x, float y) {

        IPieDataSet set = mChart.getData().getDataSet();

        final Entry entry = set.getEntryForIndex(index);

        return new Highlight(index, entry.getY(), x, y, 0, set.getAxisDependency());
    }
}
