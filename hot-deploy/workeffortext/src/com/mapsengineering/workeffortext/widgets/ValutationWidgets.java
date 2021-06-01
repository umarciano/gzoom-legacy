package com.mapsengineering.workeffortext.widgets;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilURL;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

/**
 * Generazione widgets per strumenti di valutazione 
 * @author sandro
 * @version 1.0 08/02/2010
 *
 */
public final class ValutationWidgets {

    /**
     * Counter for naming var
     */
    private static int counter = 0;

    private ValutationWidgets() {
    }

    /**
     * Costruisce widget come PNG e lo salva nel path indicato
     * TODO: sostituire request con i semplici parametri stringa
     * @param path (relativo a webapp/< context >)
     * @param currentValue
     * @param ranges
     * @param width
     * @param height
     * @param pointScaleToTarget Adatta la scala valori al valore obiettivo
     * @param targetValue Valore obiettivo
     * @return  src path del png generato
     */
    public static String buildAndSaveMeter(HttpServletRequest request, String relPath, double currentValue, ArrayList<Range> ranges, int width, int height) {
        try {

            String pngName = String.format("%d%d.png", System.currentTimeMillis(), counter++);
            String fullPngName = (relPath.endsWith("/")) ? relPath + pngName : relPath + "/" + pngName;
            fullPngName = (fullPngName.startsWith("/")) ? fullPngName : "/" + fullPngName;

            String contextRoot = (String)request.getAttribute("_CONTEXT_ROOT_");
            String contextPath = request.getContextPath();

            File file = new File(contextRoot + fullPngName);
            file.setReadable(true, false);
            file.setWritable(true, false);

            ChartUtilities.saveChartAsPNG(file, MeterBuilder.buildMeter(currentValue, ranges, false, 0), width, height, new ChartRenderingInfo(), true, 0);

            return contextPath + fullPngName;

        } catch (Exception e) {
            e.printStackTrace();
            Debug.log("Errore creazione JFreeChart: " + e.getMessage());
            return "";
        }
    }

    /**
     * Costruisce widget come PNG, con ranges desunti da uomRangeScoreId, e lo salva nel path indicato
     * @param delegator
     * @param path (relativo a webapp/< context >)
     * @param currentValue
     * @param uomRangeScoreId id per reperire le fasce valori
     * @param width
     * @param height
     * @return src path del png generato
     */
    public static String buildAndSaveMeter(Delegator delegator, HttpServletRequest request, String path, double currentValue, String uomRangeScoreId, int width, int height) {

        try {
            ArrayList<Range> ranges = new ArrayList<Range>();

            List<GenericValue> list = delegator.findByAnd("UomRangeValues", UtilMisc.toMap("uomRangeId", uomRangeScoreId));
            if (UtilValidate.isNotEmpty(list)) {

                for (GenericValue value : list) {
                    try {//TODO: try catch da togliere
                        String color = value.getString("colorEnumId");
                        Double fromValue = value.getDouble("fromValue");
                        Double thruValue = value.getDouble("thruValue");
                        Range rg = new Range();
                        rg.lowerBound = fromValue;
                        rg.upperBound = thruValue;
                        rg.rangeColor = color;
                        ranges.add(rg);
                    } catch (Exception e) {
                    }
                }
            }

            return ValutationWidgets.buildAndSaveMeter(request, path, currentValue, ranges, width, height);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            Debug.log("Errore creazione JFreeChart: " + e.getMessage());
            return "";
        }
    }

    /**
     * Costruisce widget come PNG, senza Ranges, e lo salva nel path indicato
     * @param path (relativo a webapp/< context >)
     * @param currentValue
     * @param ranges
     * @param width
     * @param height
     * @return src path del png generato
     */
    public static String buildAndSaveMeter(HttpServletRequest request, String path, double currentValue, int width, int height) {
        ArrayList<Range> ranges = new ArrayList<Range>();
        return ValutationWidgets.buildAndSaveMeter(request, path, currentValue, ranges, width, height);
    }

    public static String buildAndSaveSpider(HttpServletRequest request, String relPath, Map<String, String> series, Map<String, String> titles, Map<String, Map<String, Object>> values, int width, int height) {
        try {
            JFreeChart chart = SpiderBuilder.buildSpider(series, titles, values);
            return buildAndSave(chart, request, relPath, width, height);
        } catch (Exception e) {
            e.printStackTrace();
            Debug.log("Errore creazione JFreeChart: " + e.getMessage());
            return "";
        }
    }

    public static String buildAndSaveLine(HttpServletRequest request, String relPath, Map<String, String> series, Map<String, String> titles, Map<String, Map<String, Object>> values, int width, int height, String categoryAxisLabel, String valueAxisLabel, boolean legend, boolean tooltips) {
        try {
            JFreeChart chart = LineBuilder.buildLine(series, titles, values, categoryAxisLabel, valueAxisLabel, legend, tooltips);
            return buildAndSave(chart, request, relPath, width, height);
        } catch (Exception e) {
            e.printStackTrace();
            Debug.log("Errore creazione JFreeChart: " + e.getMessage());
            return "";
        }
    }

    private static String buildAndSave(JFreeChart chart, HttpServletRequest request, String relPath, int width, int height) {
        try {

            String pngName = String.format("%d%d.png", System.currentTimeMillis(), counter++);
            String fullPngName = (relPath.endsWith("/")) ? relPath + pngName : relPath + "/" + pngName;
            fullPngName = (fullPngName.startsWith("/")) ? fullPngName : "/" + fullPngName;

            String contextRoot = (String)request.getAttribute("_CONTEXT_ROOT_");
            String contextPath = request.getContextPath();

            File file = new File(contextRoot + fullPngName);
            file.setReadable(true, false);
            file.setWritable(true, false);

            ChartRenderingInfo info = new ChartRenderingInfo();
            ChartUtilities.saveChartAsPNG(file, chart, width, height, info, true, 0);

            return contextPath + fullPngName;

        } catch (Exception e) {
            e.printStackTrace();
            Debug.log("Errore creazione JFreeChart: " + e.getMessage());
            return "";
        }
    }

    /**
     * Costruisce widget come PNG e lo resituisce come array di byte
     * @param path
     * @param currentValue
     * @param ranges
     * @param width
     * @param height
     * @return byte array png generato
     */
    public static byte[] buildAndGetMeter(double currentValue, ArrayList<Range> ranges, int width, int height) {
        try {

            return ChartUtilities.encodeAsPNG(MeterBuilder.buildMeter(currentValue, ranges, false, 0d).createBufferedImage(width, height));

        } catch (Exception e) {
            e.printStackTrace();
            Debug.log("Errore creazione JFreeChart: " + e.getMessage());
            return null;
        }
    }

    /**
     * Esegue la query per resituire il ContentId dell'emoticon
     * @return contentId
     */
    public static String getEmoticonContentId(Delegator delegator, String uomRangeScoreId, double scoreValue) {
        EntityCondition ec = EntityCondition.makeCondition(EntityCondition.makeCondition(UtilMisc.toMap("uomRangeId", uomRangeScoreId)), EntityJoinOperator.AND, EntityCondition.makeCondition(EntityCondition.makeCondition("fromValue", EntityOperator.LESS_THAN_EQUAL_TO, scoreValue), EntityJoinOperator.AND, EntityCondition.makeCondition("thruValue", EntityOperator.GREATER_THAN_EQUAL_TO, scoreValue)));
        return IconContentHelper.getIconContentId(delegator, ec);
    }

    /**
     * Esegue la query per resituire il ContentId dell'icona di Trend
     * @return contentId
     */
    @SuppressWarnings("finally")
    public static String getTrendContentId(Delegator delegator, Map<String, Object> row, GenericValue date) {
        String toReturn = null;
        try {
            String workEffortMeasureId = (String)row.get("workEffortMeasureId");
            Object previousDate = date.get("transactionDate");

            Double curBudScore = (Double)row.get("scoreBud");
            Double curActScore = (Double)row.get("scoreAct");

            List<GenericValue> preActList = delegator.findByAnd("AcctgTransActual", UtilMisc.toMap("voucherRef", workEffortMeasureId, "transactionDate", previousDate));
            List<GenericValue> preBudList = delegator.findByAnd("AcctgTransBudget", UtilMisc.toMap("voucherRef", workEffortMeasureId, "transactionDate", previousDate));
            GenericValue preAct = EntityUtil.getFirst(preActList);
            GenericValue preBud = EntityUtil.getFirst(preBudList);
            if (preAct == null || preBud == null) {
                return null;
            }

            double score = 0;
            if (curBudScore != null) {
                score = (Double)preBud.get("score") - (Double)preAct.get("score") - curBudScore + curActScore;
            } else {
                score = (Double)preAct.get("score") - curActScore;
            }

            EntityCondition ec = EntityCondition.makeCondition(EntityCondition.makeCondition(UtilMisc.toMap("uomRangeId", "TREND")), EntityJoinOperator.AND, EntityCondition.makeCondition(EntityCondition.makeCondition("fromValue", EntityOperator.LESS_THAN_EQUAL_TO, score), EntityJoinOperator.AND, EntityCondition.makeCondition("thruValue", EntityOperator.GREATER_THAN_EQUAL_TO, score)));

            toReturn = IconContentHelper.getIconContentId(delegator, ec);
        } catch (Exception e) {
            e.printStackTrace();
            Debug.log("Errore ricerca Trend icon: " + e.getMessage());
            toReturn = null;
        } finally {
            return toReturn;
        }
    }

    /**
     * Servizio di cancellazione file con estensione specificata
     * <p>Parametri attesi:
     * <p>folderPath - percorso relativo della folder, a partire dalla root di ofbiz, es. hot-deploy/workeffortext...;
     * fileExt - estensione, senza punto, dei files da rimuovere		
     * @param dctx
     * @param context 
     * @return
     */
    public static Map<String, Object> cleanFolderByExt(DispatchContext dctx, Map<String, ? extends Object> context) {
        try {
            List<String> errorMessageList = new ArrayList<String>();
            final String fileExt = (String)context.get("fileExt");

            // Separo i path
            String folderPath = (String)context.get("folderPath");
            String[] paths = folderPath.split(",");

            for (String path : paths) {

                URL url = UtilURL.fromOfbizHomePath(path);
                if (url == null) {
                    errorMessageList.add(String.format("Path %s non risolto.", path));
                    continue;
                }

                File folder = new File(url.getPath());
                //Instance of FileFilter
                FileFilter filter = new FileFilter() {
                    public boolean accept(File pathname) {
                        if (fileExt.equals("*")) {
                            return true;
                        }
                        String name = pathname.getName().toLowerCase();
                        String ext = fileExt.toLowerCase();
                        return (name.endsWith(ext)) ? true : false;
                    }
                };

                for (File file : folder.listFiles(filter)) {
                    if (!file.delete()) {
                        errorMessageList.add(String.format("Job cleanFolderByExt non ha cancellato il file %s", file.getPath()));
                    }
                }
            }

            if (errorMessageList.size() == 0) {
                return ServiceUtil.returnSuccess();
            } else {
                return ServiceUtil.returnError(errorMessageList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceUtil.returnError("Errore job cleanFolderByExt: " + e.getMessage());
        }
    }

    /**
     * Esegue la query per resituire il ContentId dell'icona di hasAlert
     * @return contentId
     */
    public static String getHasAlertContentId(Delegator delegator, String uomRangeId, String hasAlert) {
        EntityCondition ec = EntityCondition.makeCondition(EntityCondition.makeCondition(UtilMisc.toMap("uomRangeId", uomRangeId)), EntityJoinOperator.AND, EntityCondition.makeCondition(UtilMisc.toMap("alert", hasAlert)));
        return IconContentHelper.getIconContentId(delegator, ec);
    }
}
