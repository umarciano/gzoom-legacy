package com.mapsengineering.workeffortext.widgets;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Vector;

import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.util.TableOrder;

@SuppressWarnings("serial")
class CustomSpiderWebPlot extends SpiderWebPlot {

    public CustomSpiderWebPlot(CategoryDataset dataset) {
        super(dataset);
    }

    @Override
    protected void drawLabel(Graphics2D g2, Rectangle2D plotArea, double value, int cat, double startAngle, double extent) {

        FontRenderContext frc = g2.getFontRenderContext();

        String label = null;
        if (getDataExtractOrder() == TableOrder.BY_ROW) {
            // if series are in rows, then the categories are the column keys
            label = getLabelGenerator().generateColumnLabel(getDataset(), cat);
        } else {
            // if series are in columns, then the categories are the row keys
            label = getLabelGenerator().generateRowLabel(getDataset(), cat);
        }

        Composite saveComposite = g2.getComposite();

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g2.setPaint(getLabelPaint());
        g2.setFont(getLabelFont());

        Vector<String> vec = brakIntoLines(g2, label, 90);

        float y = 0;
        boolean firstToken = true;
        Iterator<String> it = vec.iterator();
        while (it.hasNext()) {
            String token = it.next();
            Rectangle2D labelBounds = getLabelFont().getStringBounds(token, frc);
            LineMetrics lm = getLabelFont().getLineMetrics(token, frc);
            double ascent = lm.getAscent();

            Point2D labelLocation = calculateLabelLocation(labelBounds, ascent, plotArea, startAngle);

            if (firstToken) {
                y = (float)labelLocation.getY();
                firstToken = false;
            }

            g2.drawString(token, (float)labelLocation.getX(), y);
            y += ascent + lm.getLeading();
        }

        g2.setComposite(saveComposite);
    }

    private Vector<String> brakIntoLines(Graphics2D g2, String s, int width) {
        int fromIndex = 0;
        int pos = 0;
        int bestpos;
        String largestString;
        FontMetrics fm = g2.getFontMetrics(getLabelFont());
        Vector<String> lines = new Vector<String>();
        String line = "";

        // while we haven't run past the end of the string...

        while (fromIndex != -1) {
            // Automatically skip any spaces at the beginning of the line

            while (fromIndex < s.length() && s.charAt(fromIndex) == ' ') {
                ++fromIndex;
                // If we hit the end of line
                // while skipping spaces, we're done.

                if (fromIndex >= s.length())
                    break;
            }

            // fromIndex represents the beginning of the line

            pos = fromIndex;
            bestpos = -1;
            largestString = null;

            while (pos >= fromIndex) {
                boolean bHardNewline = false;
                int newlinePos = s.indexOf('\n', pos);
                int spacePos = s.indexOf(' ', pos);

                if (newlinePos != -1 && // there is a newline and either
                        ((spacePos == -1) || // 1. there is no space, or
                        (spacePos != -1 && newlinePos < spacePos)))
                // 2. the newline is first
                {
                    pos = newlinePos;
                    bHardNewline = true;
                } else {
                    pos = spacePos;
                    bHardNewline = false;
                }

                // Couldn't find another space?

                if (pos == -1) {
                    line = s.substring(fromIndex);
                } else {
                    line = s.substring(fromIndex, pos);
                }

                // If the string fits, keep track of it.

                if (fm.stringWidth(line) < width) {
                    largestString = line;
                    bestpos = pos;

                    // If we've hit the end of the
                    // string or a newline, use it.

                    if (bHardNewline)
                        bestpos++;
                    if (pos == -1 || bHardNewline)
                        break;
                } else {
                    break;
                }

                ++pos;
            }

            if (largestString == null) {
                // Couldn't wrap at a space, so find the largest line
                // that fits and print that.  Note that this will be
                // slightly off -- the width of a string will not necessarily
                // be the sum of the width of its characters, due to kerning.

                int totalWidth = 0;
                int oneCharWidth = 0;

                pos = fromIndex;

                while (pos < s.length()) {
                    oneCharWidth = fm.charWidth(s.charAt(pos));
                    if ((totalWidth + oneCharWidth) >= width)
                        break;
                    totalWidth += oneCharWidth;
                    ++pos;
                }

                lines.addElement(s.substring(fromIndex, pos));
                fromIndex = pos;
            } else {
                lines.addElement(largestString);
                fromIndex = bestpos;
            }
        }
        return lines;
    }
}
