package com.mapsengineering.workeffortext.widgets;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Point;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.dial.DialBackground;
import org.jfree.chart.plot.dial.DialCap;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialPointer.Pin;
import org.jfree.chart.plot.dial.StandardDialFrame;
import org.jfree.chart.plot.dial.StandardDialRange;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.StandardGradientPaintTransformer;

final class MeterBuilder {

    /**
     * Costruisce widget di visualizzazione valori target (tachimetro).
     *  
     * @return Widget PNG image encoded as byte array
     */
    static JFreeChart buildMeter(double currentValue, ArrayList<Range> ranges, boolean pointScaleToTarget, double targetValue) throws IOException {

        //Valore mostrato
        DefaultValueDataset ds = new DefaultValueDataset(currentValue);

        //Plot di base
        DialPlot dialPlot = new DialPlot();
        dialPlot.setView(0.0d, 0.0d, 1.0d, 1.0d);
        dialPlot.setDataset(0, ds);

        StandardDialFrame sdf = new StandardDialFrame();
        sdf.setForegroundPaint(Color.darkGray);
        sdf.setStroke(new BasicStroke(0.7f));
        dialPlot.setDialFrame(sdf);
        //Sfondo
        GradientPaint gp = new GradientPaint(new Point(), new Color(255, 255, 255), new Point(), new Color(170, 170, 220));
        DialBackground db = new DialBackground(gp);
        db.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.VERTICAL));
        dialPlot.setBackground(db);
        //Scala valori
        StandardDialScale sds;
        if (!pointScaleToTarget) {
            sds = new StandardDialScale(0D, 100D, -120d, -300d, 20d, 1);
        } else {
            sds = new StandardDialScale(0D, 100D, -120d, -300d, targetValue, 1);
        }
        sds.setTickRadius(0.80);
        sds.setMajorTickStroke(new BasicStroke(1f));
        sds.setMinorTickStroke(new BasicStroke(1f));
        sds.setTickLabelOffset(0.35);
        sds.setTickLabelFormatter(new DecimalFormat("#0"));
        sds.setTickLabelFont(new Font("Dialog", 0, 6));
        dialPlot.addScale(0, sds);

        dialPlot.mapDatasetToScale(0, 0);

        //Creazione ranges valori
        for (Range rg : ranges) {
            StandardDialRange sdr = new StandardDialRange(rg.lowerBound, rg.upperBound, Colors.decodeColor(rg.rangeColor));
            sdr.setScaleIndex(0);
            sdr.setInnerRadius(0.86d);
            sdr.setOuterRadius(0.90d);
            dialPlot.addLayer(sdr);
        }

        //Cap bianco indicatore
        DialCap dialcap = new DialCap();
        dialcap.setRadius(0.10d);
        dialPlot.setCap(dialcap);

        //Indicatore
        Pin pin = new Pin(0);
        pin.setRadius(0.9d);
        pin.setStroke(new BasicStroke(1f));
        pin.setPaint(Color.BLACK);
        dialPlot.addPointer(pin);

        JFreeChart jfc = new JFreeChart(dialPlot);
        jfc.setBackgroundPaint(new Color(0, 0, 0, 0));

        return jfc;
    }
}
