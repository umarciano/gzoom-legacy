package com.mapsengineering.base.container;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

import javolution.util.FastMap;

import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.location.FlexibleLocation;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.base.util.template.FreeMarkerWorker;
import org.ofbiz.entity.config.DelegatorInfo;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelReader;

import freemarker.template.TemplateException;

/**
 * OFBiz container to generate Java classes from entity models.
 * @author sivi
 *
 */
public class Entity2JavaContainer implements Container {

    public static final String MODULE = Entity2JavaContainer.class.getName();

    private ContainerConfigHelper config;
    private Map<String, Object> context;
    private String delegatorName;
    private ModelReader modelReader;
    private String outputDir;
    private boolean generateRelations;
    private String templatePath;
    private String classNamePattern;
    private String[] locations;
    private Pattern locationFilter;
    private Pattern packageFilter;
    private Pattern entityFilter;

    @Override
    public void init(String[] args, String configFile) throws ContainerException {
        Debug.log("init", MODULE);
        config = new ContainerConfigHelper(Arrays.asList(args), configFile, "generate-container");
        context = FastMap.newInstance();
        delegatorName = config.getProperty("delegatorName", "default");
        outputDir = config.getProperty("outputDir", "src-gen");
        generateRelations = "true".equals(config.getProperty("generateRelations", null));
        templatePath = config.getProperty("templatePath", null);
        classNamePattern = config.getProperty("classNamePattern", null);
    }

    @Override
    public boolean start() throws ContainerException {
        try {
            Debug.log("start", MODULE);
            context.put("generator", this);
            modelReader = ModelReader.getModelReader(delegatorName);
            initRegexFilters();
            initLocations();
            generateFiles();
            System.exit(1);
            return true;
        } catch (Exception e) {
            Debug.log(e, MODULE);
            return false;
        }
    }

    @Override
    public void stop() throws ContainerException {
        Debug.log("stop", MODULE);
    }

    /**
     * Get delegator name
     * @return
     */
    public String getDelegatorName() {
        return delegatorName;
    }

    /**
     * Get model reader
     * @return
     */
    public ModelReader getModelReader() {
        return modelReader;
    }

    /**
     * Get output directory
     * @return
     */
    public String getOutputDir() {
        return outputDir;
    }

    /**
     * Flag to enable entity model relations
     * @return
     */
    public boolean getGenerateRelations() {
        return generateRelations;
    }

    /**
     * Get entity group helper name
     * @param delegatorInfo
     * @param groupName
     * @return
     */
    public String getEntityGroupHelperName(DelegatorInfo delegatorInfo, String groupName) {
        return delegatorInfo.groupMap.get(groupName);
    }

    /**
     * Get the Java package name by the entity model
     * @param modelEntity
     * @return
     */
    public String getPackageName(ModelEntity modelEntity) {
        return modelEntity.getPackageName();
    }

    /**
     * Get the Java class name by a pattern and an entity model
     * @param modelEntity
     * @return
     */
    public String getClassName(ModelEntity modelEntity) {
        if (classNamePattern == null || classNamePattern.isEmpty()) {
            return modelEntity.getEntityName();
        }
        return FlexibleStringExpander.expandString(classNamePattern, context);
    }

    /**
     * Get the full Java class name
     * @param modelEntity
     * @return
     */
    public String getFullClassName(ModelEntity modelEntity) {
        return getPackageName(modelEntity) + '.' + getClassName(modelEntity);
    }

    protected void initRegexFilters() throws Exception {
        String locationFilterParam = config.getProperty("locationFilter", null);
        String packageFilterParam = config.getProperty("packageFilter", null);
        String entityFilterParam = config.getProperty("entityFilter", null);
        locationFilter = UtilValidate.isNotEmpty(locationFilterParam) ? Pattern.compile(locationFilterParam) : null;
        Debug.log("locationFilter=" + locationFilter, MODULE);
        packageFilter = UtilValidate.isNotEmpty(packageFilterParam) ? Pattern.compile(packageFilterParam) : null;
        Debug.log("packageFilter=" + packageFilter, MODULE);
        entityFilter = UtilValidate.isNotEmpty(entityFilterParam) ? Pattern.compile(entityFilterParam) : null;
        Debug.log("entityFilter=" + entityFilter, MODULE);
    }

    protected void initLocations() throws Exception {
        String locationPath = config.getProperty("locationPath", null);
        Debug.log("locationPath=" + locationPath, MODULE);
        if (UtilValidate.isNotEmpty(locationPath)) {
            locations = locationPath.split("\\Q" + File.pathSeparatorChar + "\\E");
            for (int i = 0; i < locations.length; ++i) {
                File file = new File(locations[i]);
                locations[i] = file.getCanonicalPath();
                Debug.log("location[" + i + "]=" + locations[i], MODULE);
            }
        }
    }

    protected boolean acceptModelEntity(ModelEntity modelEntity) throws Exception {
        if (locations != null) {
            URL entityLocationUrl = FlexibleLocation.resolveLocation(modelEntity.getLocation());
            if (entityLocationUrl != null) {
                File entityFile = new File(entityLocationUrl.toURI());
                String entityFilePath = entityFile.getCanonicalPath();
                boolean found = false;
                for (String location : locations) {
                    if (entityFilePath.equals(location)) {
                        Debug.log("location matched: " + location, MODULE);
                        found = true;
                        break;
                    }
                }
                if (!found)
                    return false;
            }
        }
        if (locationFilter != null && !locationFilter.matcher(modelEntity.getLocation()).matches()) {
            return false;
        }
        if (packageFilter != null && !packageFilter.matcher(modelEntity.getPackageName()).matches()) {
            return false;
        }
        if (entityFilter != null && !entityFilter.matcher(modelEntity.getEntityName()).matches()) {
            return false;
        }
        return true;
    }

    protected void generateFiles() throws Exception {
        Map<String, ModelEntity> entityCache = modelReader.getEntityCache();
        for (Map.Entry<String, ModelEntity> entry : entityCache.entrySet()) {
            ModelEntity modelEntity = entry.getValue();
            if (acceptModelEntity(modelEntity)) {
                context.put("modelEntity", modelEntity);
                generateModelEntity(modelEntity);
            }
        }
    }

    protected void generateModelEntity(ModelEntity modelEntity) throws IOException, TemplateException {
        final String packageName = getPackageName(modelEntity);
        final String className = getClassName(modelEntity);
        final String fullClassName = getFullClassName(modelEntity);
        final String fullFileName = outputDir + '/' + fullClassName.replace('.', '/') + ".java";
        Debug.log("fullFileName = " + fullFileName, MODULE);
        context.put("packageName", packageName);
        context.put("className", className);
        context.put("fullClassName", fullClassName);
        context.put("fullFileName", fullFileName);
        File file = new File(fullFileName);
        file.getParentFile().mkdirs();
        FileWriter output = new FileWriter(file);
        try {
            FreeMarkerWorker.renderTemplate(templatePath, context, output);
        } finally {
            output.close();
        }
    }
}
