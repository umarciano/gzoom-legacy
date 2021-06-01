package com.mapsengineering.base.jawr.web.resource.bundle.generator.component;

import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import net.jawr.web.resource.bundle.generator.AbstractJavascriptGenerator;
import net.jawr.web.resource.bundle.generator.GeneratorContext;
import net.jawr.web.resource.bundle.generator.GeneratorRegistry;
import net.jawr.web.resource.bundle.generator.ResourceGenerator;

import org.ofbiz.base.location.FlexibleLocation;

public class ComponentJSGenerator extends AbstractJavascriptGenerator implements  ResourceGenerator {

    /** The component resource bundle prefix */
    public static final String COMPONENT_RESOURCE_BUNDLE_PREFIX = "component";

    public Reader createResource(GeneratorContext context) {
        try {
            URL url = FlexibleLocation.resolveLocation(COMPONENT_RESOURCE_BUNDLE_PREFIX + GeneratorRegistry.PREFIX_SEPARATOR + context.getPath());

            ReadableByteChannel chan = Channels.newChannel(url.openStream());
            return Channels.newReader(chan,context.getCharset().newDecoder (),-1);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getMappingPrefix() {
        return ComponentJSGenerator.COMPONENT_RESOURCE_BUNDLE_PREFIX;
    }

}
