package com.mapsengineering.gzoomjbpm.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.jbpm.api.history.HistoryActivityInstance;
import org.jbpm.api.history.HistoryProcessInstance;
import org.ofbiz.base.util.UtilHttp;

public class JpdlModelDrawer {
	public static final int RECT_OFFSET_X = JpdlModel.RECT_OFFSET_X;
	public static final int RECT_OFFSET_Y = JpdlModel.RECT_OFFSET_Y;
	public static final int RECT_ROUND = 15;

	public static final int DEFAULT_FONT_SIZE = 12;

	public static final Color DEFAULT_STROKE_COLOR = Color.decode("#03689A");
	public static final Stroke DEFAULT_STROKE = new BasicStroke(2);

	public static final Color DEFAULT_LINE_STROKE_COLOR = Color
			.decode("#808080");
	public static final Stroke DEFAULT_LINE_STROKE = new BasicStroke(1);

	public static final Color DEFAULT_FILL_COLOR = Color.decode("#F6F7FF");

	/** R.Harari - nova cores para representar o estado das etapas */
	public static final Color DEFAULT_FILL_COLOR_FINISHED = Color.decode("#D3D3D3");
	public static final Color DEFAULT_FILL_COLOR_CURRENT = Color.decode("#C4FFC1");
	/** */

	private final static Map<String, Object> nodeInfos = JpdlModel
			.getNodeInfos();

	public BufferedImage draw(JpdlModel jpdlModel) throws IOException {
		Rectangle dimension = getCanvasDimension(jpdlModel);
		BufferedImage bi = new BufferedImage(dimension.width, dimension.height,	BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = bi.createGraphics();
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, dimension.width, dimension.height);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Font font = new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE);
		g2.setFont(font);
		Map<String, Node> nodes = jpdlModel.getNodes();
		drawNode(nodes, g2, font, jpdlModel.listActivities, jpdlModel.hpi,
				jpdlModel.fullHistoryMap, jpdlModel.activeActivityNames);
		drawTransition(nodes, g2);
		return bi;
	}

	/**
	 * ?????????
	 * 
	 * @return
	 */
	private Rectangle getCanvasDimension(JpdlModel jpdlModel) {
		Rectangle rectangle = new Rectangle();
		Rectangle rect;
		for (Node node : jpdlModel.getNodes().values()) {
			rect = node.getRectangle();
			if (rect.getMaxX() > rectangle.getMaxX()) {
				rectangle.width = (int) rect.getMaxX();
			}
			if (rect.getMaxY() > rectangle.getMaxY()) {
				rectangle.height = (int) rect.getMaxY();
			}
			for (Transition transition : node.getTransitions()) {
				List<Point> trace = transition.getLineTrace();
				for (Point point : trace) {
					if (rectangle.getMaxX() < point.x) {
						rectangle.width = point.x;
					}
					if (rectangle.getMaxY() < point.y) {
						rectangle.height = point.y;
					}
				}
			}
		}
		rectangle.width += 60;
		rectangle.height += 20;
		return rectangle;
	}

	/**
	 * @param g2
	 * @throws IOException
	 */
	private void drawTransition(Map<String, Node> nodes, Graphics2D g2)
			throws IOException {
		g2.setStroke(DEFAULT_LINE_STROKE);
		g2.setColor(DEFAULT_LINE_STROKE_COLOR);
		for (Node node : nodes.values()) {
			for (Transition transition : node.getTransitions()) {
				String to = transition.getTo();
				Node toNode = nodes.get(to);
				List<Point> trace = new LinkedList<Point>(transition
						.getLineTrace());
				int len = trace.size() + 2;
				trace.add(0, new Point(node.getCenterX(), node.getCenterY()));
				trace.add(new Point(toNode.getCenterX(), toNode.getCenterY()));
				int[] xPoints = new int[len];
				int[] yPoints = new int[len];
				for (int i = 0; i < len; i++) {
					xPoints[i] = trace.get(i).x;
					yPoints[i] = trace.get(i).y;
				}
				final int taskGrow = 4;
				final int smallGrow = -2;
				int grow = 0;
				if (nodeInfos.get(node.getType()) != null) {
					grow = smallGrow;
				} else {
					grow = taskGrow;
				}
				Point p = GeometryUtils.getRectangleLineCrossPoint(node
						.getRectangle(), new Point(xPoints[1], yPoints[1]),
						grow);
				if (p != null) {
					xPoints[0] = p.x;
					yPoints[0] = p.y;
				}
				if (nodeInfos.get(toNode.getType()) != null) {
					grow = smallGrow;
				} else {
					grow = taskGrow;
				}
				p = GeometryUtils.getRectangleLineCrossPoint(toNode
						.getRectangle(), new Point(xPoints[len - 2],
						yPoints[len - 2]), grow);
				if (p != null) {
					xPoints[len - 1] = p.x;
					yPoints[len - 1] = p.y;
				}
				g2.drawPolyline(xPoints, yPoints, len);
				drawArrow(g2, xPoints[len - 2], yPoints[len - 2],
						xPoints[len - 1], yPoints[len - 1]);
				String label = transition.getLabel();
				if (label != null && label.length() > 0) {
					int cx, cy;
					if (len % 2 == 0) {
						cx = (xPoints[len / 2 - 1] + xPoints[len / 2]) / 2;
						cy = (yPoints[len / 2 - 1] + yPoints[len / 2]) / 2;
					} else {
						cx = xPoints[len / 2];
						cy = yPoints[len / 2];
					}
					Point labelPoint = transition.getLabelPosition();
					if (labelPoint != null) {
						cx += labelPoint.x;
						cy += labelPoint.y;
					}
					cy -= RECT_OFFSET_Y + RECT_OFFSET_Y / 2;
					g2.drawString(label, cx, cy);
				}
			}
		}
	}

	private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
		final double len = 8.0;
		double slopy = Math.atan2(y2 - y1, x2 - x1);
		double cosy = Math.cos(slopy);
		double siny = Math.sin(slopy);
		int[] xPoints = { 0, x2, 0 };
		int[] yPoints = { 0, y2, 0 };
		double a = len * siny, b = len * cosy;
		double c = len / 2.0 * siny, d = len / 2.0 * cosy;
		xPoints[0] = x2 - (int) (b + c);
		yPoints[0] = y2 - (int) (a - d);
		xPoints[2] = x2 - (int) (b - c);
		yPoints[2] = y2 - (int) (d + a);

		g2.fillPolygon(xPoints, yPoints, 3);
	}

	/**
	 * @param g2
	 * @throws IOException
	 */
	private void drawNode(Map<String, Node> nodes, Graphics2D g2, Font font,
			Hashtable<String, HistoryActivityInstance> listActivities,
			HistoryProcessInstance hpi,
			Map<String, Map<String, Object>> fullHistoryMap,
			Set<String> activeActivityNames) throws IOException {
		Font originalFont = font;
		for (Node node : nodes.values()) {
			String name = node.getName();
			Date startTime = null;

			if (nodeInfos.get(node.getType()) != null) {
				BufferedImage bi2 = ImageIO.read(getClass()
						.getResourceAsStream(
								"icons/48/" + nodeInfos.get(node.getType())));
				g2.drawImage(bi2, node.getX(), node.getY(), null);
				String type = node.getType();
				if (hpi != null
						&& (type.equals("end") || type.equals("end-cancel") || type
								.equals("end-error"))) {
					if (hpi.getState().equals(
							HistoryProcessInstance.STATE_ENDED)) {
						Date endTime = hpi.getEndTime();
						if (endTime != null) {
							String formattedDate = DateFormat
									.getDateTimeInstance(DateFormat.SHORT,
											DateFormat.SHORT).format(endTime);
							g2.setStroke(DEFAULT_LINE_STROKE);
							g2.setColor(Color.black);
							g2.drawString(formattedDate, node.getX(), node.getCenterY()	- node.getHeight() / 2 - 5);
						}
					}
				}
			} else {
				int x = node.getX();
				int y = node.getY();
				int w = node.getWitdth();
				int h = node.getHeight();

				HistoryActivityInstance hai = null;
				Color fillColor = DEFAULT_FILL_COLOR;

				if (listActivities != null) {
					hai = listActivities.get(name);
					if (hai != null) {
						if (hai.getEndTime() != null) {
							fillColor = DEFAULT_FILL_COLOR_FINISHED;
							startTime = hai.getStartTime();
						} else {
							fillColor = DEFAULT_FILL_COLOR_CURRENT;
						}
					}
					if (activeActivityNames != null
							&& activeActivityNames.contains(name)) {
						fillColor = DEFAULT_FILL_COLOR_CURRENT;
						g2.setFont(g2.getFont().deriveFont(Font.BOLD));
					} else {
						g2.setFont(originalFont);
					}
				}

				g2.setColor(fillColor);
				g2.fillRoundRect(x, y, w, h, RECT_ROUND, RECT_ROUND);
				g2.setColor(DEFAULT_STROKE_COLOR);
				g2.setStroke(DEFAULT_STROKE);
				g2.drawRoundRect(x, y, w, h, RECT_ROUND, RECT_ROUND);

				FontRenderContext frc = g2.getFontRenderContext();
				Rectangle2D r2 = originalFont.getStringBounds(name, frc);
				//int xLabel = (int) (node.getX() + ((node.getWitdth() - r2.getWidth()) / 2));
				//int yLabel = (int) ((node.getY() + ((node.getHeight() - r2.getHeight()) / 2)) - r2.getY());
				g2.setStroke(DEFAULT_LINE_STROKE);
				g2.setColor(Color.black);
				
				//Suddivido il nome del nodo in piu righe
				Vector<String> nameVector = brakIntoLines(g2, g2.getFont(), name, w);
				Iterator<String> it = nameVector.iterator();
				LineMetrics approxMetrics = g2.getFont().getLineMetrics(nameVector.firstElement(), frc); 
				FontMetrics fm = g2.getFontMetrics();
				float yLabel = node.getCenterY() - ((nameVector.size() * approxMetrics.getLeading() + (nameVector.size() % 2) * (approxMetrics.getLeading())) / 2) ;
				while(it.hasNext()) {
					String token = it.next();
			        Rectangle2D labelBounds = g2.getFont().getStringBounds(token, frc);
			        LineMetrics lm = g2.getFont().getLineMetrics(token, frc);
			        double ascent = lm.getAscent();
		
			        float xLabel = node.getCenterX() - (fm.stringWidth(token) / 2);
		        	g2.drawString(token, xLabel, yLabel);
		        	yLabel += ascent + lm.getLeading(); 
		        }
				//g2.drawString(name, xLabel, yLabel);

				if (startTime != null) {
					String formattedDate = DateFormat.getDateTimeInstance(
							DateFormat.SHORT, DateFormat.SHORT).format(
							startTime);
					g2.setFont(originalFont);
					g2.drawString(formattedDate, node.getX(), node.getCenterY()	- node.getHeight() / 2 - 5);
				}

				if (fullHistoryMap.containsKey(name)) {
					Map<String, Object> varMap = fullHistoryMap.get(name);					
					int distance = 0;
					g2.setFont(originalFont);					
					for (Map.Entry<String, Object>  mapEntry : varMap.entrySet()) {
						g2.drawString((String)mapEntry.getValue(), node.getX(), node.getCenterY() + node.getHeight() / 2 + 15); //+ distance);
						distance += 10;
            		}
				}
			}
		}
	}

	private Vector<String> brakIntoLines(Graphics2D g2, Font font, String s, int width) {
		int fromIndex = 0;
		int pos = 0;
		int bestpos;
		String largestString;
		FontMetrics fm = g2.getFontMetrics(font);
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
				// that fits and print that. Note that this will be
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
