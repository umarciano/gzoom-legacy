package org.ofbiz.widget.screen;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.webapp.view.AbstractViewHandler;
import org.ofbiz.webapp.view.ViewHandlerException;
import org.ofbiz.widget.html.HtmlScreenRenderer;
import org.ofbiz.widget.poi.PoiHssfContext;
import org.ofbiz.widget.poi.PoiHssfFormRenderer;
import org.ofbiz.widget.poi.PoiHssfScreenRenderer;

/**
 * Generates Excel views
 * @author sivi
 *
 */
public class ScreenPoiHssfViewHandler extends AbstractViewHandler {

    private static final String MODULE = ScreenPoiHssfViewHandler.class.getName();

    private static final String DEFAULT_ERROR_TEMPLATE = "component://common/widget/CommonScreens.xml#EventMessages";
    private static final String XLS_MIME_TYPE = "application/vnd.ms-excel";

    protected ServletContext servletContext = null;
    protected PoiHssfScreenRenderer poiHssfScreenRenderer = new PoiHssfScreenRenderer();

    public void init(ServletContext context) throws ViewHandlerException {
        this.servletContext = context;
    }

    public void render(String name, String page, String info, String contentType, String encoding, HttpServletRequest request, HttpServletResponse response) throws ViewHandlerException {

        PoiHssfContext poiHssfContext = new PoiHssfContext();
        String suffix = "";

        // render HSSF workbook
        Writer writer = new StringWriter();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ScreenRenderer screens = new ScreenRenderer(writer, null, poiHssfScreenRenderer);
            screens.populateContextForRequest(request, response, servletContext);
            // this is the object used to render forms from their definitions
            screens.getContext().put("poiHssfContext", poiHssfContext);
            screens.getContext().put("formStringRenderer", new PoiHssfFormRenderer(request, response));
            screens.getContext().put("simpleEncoder", StringUtil.xmlEncoder);
            screens.render(page);
        } catch (Exception e) {
            renderError("Problems building HSSF workbook", e, request, response);
            return;
        }

        try {
            poiHssfContext.getWorkbook().write(out);
            if (UtilValidate.isEmpty(contentType)) {
                contentType = XLS_MIME_TYPE;
            }
            if (XLS_MIME_TYPE.equals(contentType)) {
                suffix = ".xls";
            }
        } catch (IOException e) {
            renderError("Problems writing HSSF workbook", e, request, response);
            return;
        }

        try {
            // write to the browser
            UtilHttp.streamContentToBrowser(response, out.toByteArray(), contentType, name + suffix);
        } catch (IOException e) {
            renderError("Unable to write to OutputStream", e, request, response);
        }
    }

    protected void renderError(String msg, Exception e, HttpServletRequest request, HttpServletResponse response) throws ViewHandlerException {
        Debug.logError(msg + ": " + e, MODULE);
        try {
            Writer writer = new StringWriter();
            ScreenRenderer screens = new ScreenRenderer(writer, null, new HtmlScreenRenderer());
            screens.populateContextForRequest(request, response, servletContext);
            screens.getContext().put("errorMessage", msg + ": " + e);
            screens.render(DEFAULT_ERROR_TEMPLATE);
            response.setContentType("text/html");
            response.getWriter().write(writer.toString());
            writer.close();
        } catch (Exception x) {
            Debug.logError("Multiple errors rendering POI-HSSF", MODULE);
            throw new ViewHandlerException("Multiple errors rendering POI-HSSF", x);
        }
    }
}
