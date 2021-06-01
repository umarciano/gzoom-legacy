package com.mapsengineering.base.jdbc;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.template.FreeMarkerWorker;
import org.ofbiz.entity.Delegator;

import com.mapsengineering.base.util.logging.FormattedStringBuilder;

import freemarker.core.Environment;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;

public class FtlQuery extends JdbcQuery {

    public static final String THIS_NAME = "freeMarkerQuery";

    private static final String MODULE = FtlQuery.class.getName();
    private static final String LIB_LOCATION = "sql/" + FtlQuery.class.getSimpleName() + "-lib.ftl";
    private static final TemplateHashModel JDBC_TYPE = initJdbcType();
    private static final Template LIB_TEMPLATE = initLibTemplate();

    private final String templateLocation;
    private final Map<String, Object> context;

    public FtlQuery(Delegator delegator, String templateLocation, Map<String, Object> context) {
        super(delegator, null);
        this.templateLocation = templateLocation;
        this.context = context;
    }

    @Override
    public String toString() {
        return new FormattedStringBuilder().braceOpen().nl().indent() //
                .nv("templateLocation", templateLocation).comma().space().append(super.toString()).nl() //
                .unindent().braceClose().toString();
    }

    @Override
    protected String buildQueryString() {
        if (templateLocation == null || templateLocation.isEmpty()) {
            return null;
        }
        try {
            clearParams();
            StringWriter writer = new StringWriter();
            try {
                Template template = FreeMarkerWorker.getTemplate(templateLocation);
                renderTemplate(template, context, writer);
            } finally {
                writer.close();
            }
            String sql = writer.toString();
            Debug.log("SQL query: " + sql, MODULE);
            Debug.log("SQL params: " + getParams(), MODULE);
            return sql;
        } catch (Exception e) {
            throw new IllegalStateException("", e);
        }
    }

    /**
     * @see org.ofbiz.base.util.template.FreeMarkerWorker#renderTemplate
     */
    private Environment renderTemplate(Template template, Map<String, Object> context, Writer outWriter) throws TemplateException, IOException {
        // make sure there is no "null" string in there as FreeMarker will try to use it
        context.remove("null");
        // MAPS: add this context
        context.put(THIS_NAME, this);
        context.put("jdbcType", JDBC_TYPE);
        // Since the template cache keeps a single instance of a Template that is shared among users,
        // and since that Template instance is immutable, we need to create an Environment instance and
        // use it to process the template with the user's settings.
        Environment env = template.createProcessingEnvironment(context, outWriter);
        FreeMarkerWorker.applyUserSettings(env, context);
        // MAPS: add default library
        env.include(LIB_TEMPLATE);
        env.process();
        return env;
    }

    private static TemplateHashModel initJdbcType() {
        Configuration config = FreeMarkerWorker.getDefaultOfbizConfig();
        BeansWrapper wrapper = (BeansWrapper)config.getObjectWrapper();
        TemplateHashModel staticModels = wrapper.getStaticModels();
        try {
            return (TemplateHashModel)staticModels.get(java.sql.Types.class.getName());
        } catch (TemplateModelException e) {
            throw new IllegalStateException("Error initializing JDBC type static model", e);
        }
    }

    private static Template initLibTemplate() {
        try {
            return FreeMarkerWorker.getTemplate(LIB_LOCATION);
        } catch (Exception e) {
            throw new IllegalStateException("Error initializing FTL Query Library", e);
        }
    }
}
