package com.mapsengineering.workeffortext.widgets;

import java.awt.Color;
import java.io.IOException;
import java.util.Map;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilValidate;


final class SpiderBuilder {

    /**
     * Costruisce widget di visualizzazione spider.
     *  
     * @return Widget PNG image encoded as byte array
     */
    static JFreeChart buildSpider(Map<String, String> series, Map<String, String> titles, Map<String, Map<String, Object>> values) throws IOException {
        JFreeChart jfc = null;

        DefaultCategoryDataset categorydataset = CategoryDataSetFactory.createDataSet(series, titles, values);
        if (UtilValidate.isNotEmpty(categorydataset)) {
            for (Map.Entry<String, String> titlesEntry : titles.entrySet()) {
                Map<String, Object> valueForTitle = values.get(titlesEntry.getKey());
                if (UtilValidate.isNotEmpty(valueForTitle)) {                	
                	for (Map.Entry<String, String> seriesEntry : series.entrySet()) {
                        Double value = UtilGenerics.cast(valueForTitle.get(seriesEntry.getKey()));
                        categorydataset.addValue(value, seriesEntry.getValue(), titlesEntry.getValue());
                    }
                }
            }

            SpiderWebPlot spiderwebplot = new CustomSpiderWebPlot(categorydataset);
            spiderwebplot.setStartAngle(54D);
            spiderwebplot.setInteriorGap(0.40000000000000002D);

            jfc = new JFreeChart(spiderwebplot);
            jfc.setBackgroundPaint(new Color(0, 0, 0, 0));
        }

        return jfc;
    }
}
