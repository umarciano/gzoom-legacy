
package com.mapsengineering.gzoomjbpm.graph;

import java.awt.Point;  
import java.io.InputStream;  
import java.util.HashMap;  
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;  
import java.util.List;  
import java.util.Map;  
import java.util.Set;

import javolution.util.FastMap;

import org.dom4j.Element;  
import org.dom4j.io.SAXReader;
import org.jbpm.api.history.HistoryActivityInstance;
import org.jbpm.api.history.HistoryProcessInstance;

import com.mapsengineering.gzoomjbpm.query.FullHistoryVariableQueryResult;

public class JpdlModel {  

  private Map<String, Node> nodes = new LinkedHashMap<String, Node>();  
  public static final int RECT_OFFSET_X = -7;  
  public static final int RECT_OFFSET_Y = -8;  
  public static final int DEFAULT_PIC_SIZE = 48;

  /** R.Harari - activities list */
  public Hashtable<String, HistoryActivityInstance> listActivities;
  public HistoryProcessInstance hpi;
  public List<FullHistoryVariableQueryResult> fullHistory;
  public Map<String, Map<String, Object>> fullHistoryMap;
  public Set<String> activeActivityNames;


  private final static Map<String, Object> nodeInfos = new HashMap<String, Object>();  
  static {
    nodeInfos.put("start", "start_event_empty.png");  
    nodeInfos.put("end", "end_event_terminate.png");  
    nodeInfos.put("end-cancel", "end_event_cancel.png");  
    nodeInfos.put("end-error", "end_event_error.png");  
    nodeInfos.put("decision", "gateway_exclusive.png");  
    nodeInfos.put("fork", "gateway_parallel.png");  
    nodeInfos.put("join", "gateway_parallel.png");  
    nodeInfos.put("state", null);  
    nodeInfos.put("hql", null);  
    nodeInfos.put("sql", null);  
    nodeInfos.put("java", null);  
    nodeInfos.put("script", null);  
    nodeInfos.put("task", null);  
    nodeInfos.put("sub-process", null);  
    nodeInfos.put("custom", null);  
  }  

  /**
   * 
   * @param is
   * @throws Exception
   */
  public JpdlModel(InputStream is) throws Exception {  
    this(new SAXReader().read(is).getRootElement());  
  }  

  /**
   * 
   * @param is
   * @param listHistoryActivities
   * @param hpi
   * @param fullHistory
   * @param activeActivityNames
   * @throws Exception
   */
  public JpdlModel(InputStream is, List<HistoryActivityInstance> listHistoryActivities, HistoryProcessInstance hpi, List<FullHistoryVariableQueryResult> fullHistory, Set<String> activeActivityNames) throws Exception {
	    this(new SAXReader().read(is).getRootElement());  
	    if (listHistoryActivities != null) {
	    	listActivities = new Hashtable<String, HistoryActivityInstance>();
	    	for (HistoryActivityInstance hai : listHistoryActivities) {
	    		listActivities.put(hai.getActivityName(), hai);
	    	}
	    }
	    this.hpi = hpi;
	    if(fullHistory != null) {
	    	fullHistoryMap = new FastMap<String, Map<String, Object>>();
	    	Iterator<FullHistoryVariableQueryResult> fhIt = fullHistory.iterator();
	    	while(fhIt.hasNext()) {
	    		FullHistoryVariableQueryResult result = fhIt.next();
	    		if(!fullHistoryMap.containsKey(result.getActivityName())) {
	    			fullHistoryMap.put(result.getActivityName(), new FastMap<String, Object>());
	    		}
    			fullHistoryMap.get(result.getActivityName()).put(result.getVariableName(), result.getValue());
	    	}
	    }
	    this.fullHistory = fullHistory;	   
	    this.activeActivityNames = activeActivityNames;
  }  
  
  @SuppressWarnings("unchecked")  
  private JpdlModel(Element rootEl) throws Exception {  
    for (Element el : (List<Element>) rootEl.elements()) {  
      String type = el.getQName().getName();  
      if (!nodeInfos.containsKey(type)) { // ????????  
        continue;  
      }  
      String name = null;  
      if (el.attribute("name") != null) {  
        name = el.attributeValue("name");  
      }  
      String[] location = el.attributeValue("g").split(",");  
      int x = Integer.parseInt(location[0]);  
      int y = Integer.parseInt(location[1]);  
      int w = Integer.parseInt(location[2]);  
      int h = Integer.parseInt(location[3]);  
  
      if (nodeInfos.get(type) != null) {  
        w = DEFAULT_PIC_SIZE;  
        h = DEFAULT_PIC_SIZE;  
      } else {  
        x -= RECT_OFFSET_X;  
        y -= RECT_OFFSET_Y;  
        w += (RECT_OFFSET_X + RECT_OFFSET_X);  
        h += (RECT_OFFSET_Y + RECT_OFFSET_Y);  
      }  
      Node node = new Node(name, type, x, y, w, h);  
      parserTransition(node, el);  
      nodes.put(name, node);  
    }  
  }  
  
  @SuppressWarnings("unchecked")  
  private void parserTransition(Node node, Element nodeEl) {  
    for (Element el : (List<Element>) nodeEl.elements("transition")) {  
      String label = el.attributeValue("name");  
      String to = el.attributeValue("to");  
      Transition transition = new Transition(label, to);  
      String g = el.attributeValue("g");  
      if (g != null && g.length() > 0) {  
        if (g.indexOf(":") < 0) {  
          transition.setLabelPosition(getPoint(g));  
        } else {  
          String[] p = g.split(":");  
          transition.setLabelPosition(getPoint(p[1]));  
          String[] lines = p[0].split(";");  
          for (String line : lines) {  
            transition.addLineTrace(getPoint(line));  
          }  
        }  
      }  
      node.addTransition(transition);  
    }  
  }  
  
  private Point getPoint(String exp) {  
    if (exp == null || exp.length() == 0) {  
      return null;  
    }  
    String[] p = exp.split(",");  
    return new Point(Integer.valueOf(p[0]), Integer.valueOf(p[1]));  
  }  
  
  public Map<String, Node> getNodes() {  
    return nodes;  
  }  
  public static Map<String, Object> getNodeInfos() {  
    return nodeInfos;  
  }  

}