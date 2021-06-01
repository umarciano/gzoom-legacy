package com.mapsengineering.workeffortext.widgets;

import java.awt.Color;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.ofbiz.base.util.UtilValidate;

final class LineBuilder {

    /**
     * Costruisce widget di visualizzazione a linea.
     *  
     * @return Widget PNG image encoded as byte array
     */
    static JFreeChart buildLine(Map<String, String> series, Map<String, String> titles, Map<String, Map<String, Object>> values, String categoryAxisLabel, String valueAxisLabel, boolean legend, boolean tooltips) throws IOException {
        JFreeChart jfc = null;

        DefaultCategoryDataset categorydataset = CategoryDataSetFactory.createDataSet(series, titles, values);
        if (UtilValidate.isNotEmpty(categorydataset)) {

            jfc = ChartFactory.createLineChart("", categoryAxisLabel, valueAxisLabel, categorydataset, PlotOrientation.VERTICAL, legend, tooltips, false);

            jfc.setBackgroundPaint(new Color(0, 0, 0, 0));

            CategoryPlot categoryPlot = (CategoryPlot)jfc.getPlot();
            NumberAxis numberAxis = (NumberAxis)categoryPlot.getRangeAxis();

            NumberFormat nf = NumberFormat.getNumberInstance(Locale.ITALIAN);
            DecimalFormat df = (DecimalFormat)nf;
            numberAxis.setNumberFormatOverride(df);

            org.jfree.data.Range fittedRange = DatasetUtilities.findRangeBounds(categorydataset);
            double lowerBound = fittedRange.getLowerBound();
            double upperBound = fittedRange.getUpperBound();
            org.jfree.data.Range newRange = new org.jfree.data.Range(lowerBound - (lowerBound * 10 / 100), upperBound + (upperBound * 10 / 100));
            numberAxis.setRange(newRange);
        }

        return jfc;
    }
}
