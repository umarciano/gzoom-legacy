package com.mapsengineering.accountingext.events;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.UtilValidate;


/**
 * GlAccountSessionHistoryWorker
 *
 */
public final class GlAccountSessionHistoryWorker {
	
	public static final String MODULE = GlAccountSessionHistoryWorker.class.getName();

	private static final String SUCCESS = "success";
    
    private GlAccountSessionHistoryWorker() {}

    /**
     * 
     * @param request
     * @param response
     * @return
     */
    public static String addSessionParam(HttpServletRequest request, HttpServletResponse response) {
        
    	String svh = request.getParameter("cleanAccountingSession");

        if (UtilValidate.isNotEmpty(svh) && svh.equals("Y")) {
            /*
             * Elimino dalla sessione i parametri childFolderFile e fromGlAccountTree
             */
        	request.getSession().removeAttribute("_fromGlAccountTree");
        	request.getSession().removeAttribute("_childFolderFile");
        	
        }else if("GlAccountClassView".equals(request.getParameter("entityName")) || "GlAccountView".equals(request.getParameter("entityName"))){
        	
    		String fromGlAccountTree = request.getParameter("fromGlAccountTree");
        	if (UtilValidate.isNotEmpty(fromGlAccountTree)) {
            	/**
            	 * Aggiungo il parametro alla sessione
            	 */
        		request.getSession().setAttribute("_fromGlAccountTree", fromGlAccountTree);
            } else {
            	request.getSession().removeAttribute("_fromGlAccountTree");
            }
        	
        	String childFolderFile = request.getParameter("childFolderFile");
        	if (UtilValidate.isNotEmpty(childFolderFile)) {
            	/**
            	 * Aggiungo il parametro alla sessione
            	 */
        		request.getSession().setAttribute("_childFolderFile", childFolderFile);
            } else {
            	request.getSession().removeAttribute("_childFolderFile");
            }
        	 
        }
        
        return SUCCESS;
    }
}
