/**
 * Technique TI Ltda - Project: PlanetaContabilWeb - www.planetacontabil.com.br
 * @author yeyong - http://jbpm.group.javaeye.com/group/blog/470760?page=2
 * @author Ricardo A. Harari - ricardo.harari@gmail.com
 * @date 25/01/2010 12:18:45
 * br.com.technique.process.render.graph
 *
 * TODO
 */

package com.mapsengineering.gzoomjbpm.graph;

import java.awt.Point;  
import java.awt.Rectangle;  
  
public class GeometryUtils {  
  /** 
   * ????(x1,y1)-(x2,y2)??? 
   *  
   * @param x1 
   * @param y1 
   * @param x2 
   * @param y2 
   * @return 
   */  
  public static double getSlope(int x1, int y1, int x2, int y2) {  
    return ((double) y2 - y1) / (x2 - x1);  
  }  
  
  /** 
   * ????(x1,y1)-(x2,y2)?y??? 
   *  
   * @param x1 
   * @param y1 
   * @param x2 
   * @param y2 
   * @return 
   */  
  public static double getYIntercep(int x1, int y1, int x2, int y2) {  
    return y1 - x1 * getSlope(x1, y1, x2, y2);  
  }  
  /** 
   * ??????? 
   *  
   * @param rect 
   * @return 
   */  
  public static Point getRectangleCenter(Rectangle rect) {  
    return new Point((int) rect.getCenterX(), (int) rect.getCenterY());  
  }  
  
  /** 
   * ??????p0?p1????????? 
   *  
   * @param rectangle 
   * @param p1 
   * @return 
   */  
  public static Point getRectangleLineCrossPoint(Rectangle rectangle, Point p1, int grow) {  
    Rectangle rect = rectangle.getBounds();  
    rect.grow(grow, grow);  
    Point p0 = GeometryUtils.getRectangleCenter(rect);  
  
    if (p1.x == p0.x) {  
      if (p1.y < p0.y) {  
        return new Point(p0.x, rect.y);  
      }  
      return new Point(p0.x, rect.y + rect.height);  
    }  
  
    if (p1.y == p0.y) {  
      if (p1.x < p0.x) {  
        return new Point(rect.x, p0.y);  
      }  
      return new Point(rect.x + rect.width, p0.y);  
    }  
  
    double slope = GeometryUtils.getSlope(p0.x, p0.y, rect.x, rect.y);  
    double slopeLine = GeometryUtils.getSlope(p0.x, p0.y, p1.x, p1.y);  
    double yIntercep = GeometryUtils.getYIntercep(p0.x, p0.y, p1.x, p1.y);  
  
    if (Math.abs(slopeLine) > slope - 1e-2) {  
      if (p1.y < rect.y) {  
        return new Point((int) ((rect.y - yIntercep) / slopeLine), rect.y);  
      } 
      return new Point((int) ((rect.y + rect.height - yIntercep) / slopeLine), rect.y + rect.height);  

    }  
    if (p1.x < rect.x) {  
      return new Point(rect.x, (int) (slopeLine * rect.x + yIntercep));  
    }   
    return new Point(rect.x + rect.width, (int) (slopeLine * (rect.x + rect.width) + yIntercep));  
  }  
}  
