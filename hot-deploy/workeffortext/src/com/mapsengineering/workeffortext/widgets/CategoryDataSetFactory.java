package com.mapsengineering.workeffortext.widgets;

import java.util.Map;

import org.jfree.data.category.DefaultCategoryDataset;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilValidate;

final class CategoryDataSetFactory {

    static DefaultCategoryDataset createDataSet(Map<String, String> series, Map<String, String> titles, Map<String, Map<String, Object>> values) {
        DefaultCategoryDataset categorydataset = null;

        if (UtilValidate.isNotEmpty(series) && UtilValidate.isNotEmpty(titles) && UtilValidate.isNotEmpty(values)) {
            categorydataset = new DefaultCategoryDataset();

            for (Map.Entry<String, String>  titlesEntry : titles.entrySet()) {
                Map<String, Object> valueForTitle = values.get(titlesEntry.getKey());
                if (UtilValidate.isNotEmpty(valueForTitle)) {                	               	
                	for (Map.Entry<String, String>  seriesEntry : series.entrySet()) {
                        Double value = UtilGenerics.cast(valueForTitle.get(seriesEntry.getKey()));
                        categorydataset.addValue(value, seriesEntry.getValue(), titlesEntry.getValue());
                    }
                }
            }
        }

        return categorydataset;
    }
}
