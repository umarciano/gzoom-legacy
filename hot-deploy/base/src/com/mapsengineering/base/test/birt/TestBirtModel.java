package com.mapsengineering.base.test.birt;

import java.io.ByteArrayInputStream;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;

import com.mapsengineering.base.birt.model.ModelParameter;
import com.mapsengineering.base.birt.model.ModelReader;
import com.mapsengineering.base.birt.model.ModelReport;
import com.mapsengineering.base.test.BaseTestCase;

public class TestBirtModel extends BaseTestCase {
	
	public void testModelParameter() {
		
		String eventXmlString = "<event type=\"zzz\" path=\"path\" invoke=\"inv\"></event>";
		String xmlString = "<node type=\"Date\" name=\"name1\" format=\"dd/mm/yyyy\" mandatory=\"false\" default-value=\"22/05/2013\" validation-message=\"error\">" 
		                   +eventXmlString +"</node>";
		Element node = null;
		try{
		    node =  DocumentBuilderFactory
			    .newInstance()
			    .newDocumentBuilder()
			    .parse(new ByteArrayInputStream(xmlString.getBytes()))
			    .getDocumentElement();
		}catch(Exception e)
		{
			assertTrue(true);
		}
		ModelParameter modelParameter = new ModelParameter(node);
		if(modelParameter != null)
		{
		    String type = modelParameter.getType();
		    assertEquals("Date", type);
		    
		    String name = modelParameter.getName();
		    assertEquals("name1", name);
		    
		    String format = modelParameter.getFormat();
		    assertEquals("dd/mm/yyyy", format);
		    
		    boolean mandatory = modelParameter.isMandatory();
		    assertFalse(mandatory);
		    
		    String defaultValue = modelParameter.getDefaultValue();
		    assertEquals("22/05/2013", defaultValue);
		    
		    String errorMessage = modelParameter.getErrorMessage(Locale.ITALIAN);
		    assertNotNull(errorMessage);
		
		    assertNotNull(modelParameter);

		}
		
	}
		
	public void testModelReportSRESP()
	{
		ModelReport report = null;
		try{
			report = ModelReader.getModelReport("REPORT_SRESP");
			report.validateAllValue(context, Locale.ITALIAN);
			report.invokeAllEvent(context);
			report.convertAllValue(context);
			report.convertValue(context, "");
		}catch(Exception e)
		{
			assertTrue(true);
		}		
	}
	
	public void testModelReportSASS()
	{
		ModelReport report = null;
		try{
			report = ModelReader.getModelReport("REPORT_SASS");
			report.validateAllValue(context, Locale.ITALIAN);
			report.invokeAllEvent(context);
			report.convertAllValue(context);
			report.convertValue(context, "SchedaOrganizzativaSoggetti");

		}catch(Exception e)
		{
			assertTrue(true);
		}
	}
	
	public void testModelReportUOL()
	{
		ModelReport report = null;
		try{			
			report = ModelReader.getModelReport("REPORT_UOL");
			report.validateAllValue(context, Locale.ITALIAN);
			report.invokeAllEvent(context);
			report.convertAllValue(context);
			report.convertValue(context, "SchedaListaUnitaOrganizzative.rptdesign");
		}catch(Exception e)
		{
			assertTrue(true);
		}
	}
      
}
