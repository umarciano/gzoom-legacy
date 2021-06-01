package com.mapsengineering.base.test;


import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.entity.GenericEntityException;

import com.mapsengineering.base.util.FolderLayuotTypeExtractor;

public class TestFolderLayuotTypeExtractor extends BaseTestCase {
	
    public enum E {
    	layoutType, folderIndex, folderContentIds
    }
    
    /**
     * test FolderLayuotTypeExtractor
     * @throws GenericEntityException
     */
    public void testFolderLayuotTypeExtractor() {
    	prepareContext();
    	Map<String, Object> parameters = FastMap.newInstance();
    	FolderLayuotTypeExtractor folderLayuotTypeExtractor = new FolderLayuotTypeExtractor(context, parameters);
    	String layoutType = folderLayuotTypeExtractor.getLayoutTypeFromContext();
    	assertEquals("folder1", layoutType);
    }
    
    /**
     * prepare context
     */
    private void prepareContext() {
    	context.put(E.layoutType.name(), "layout");
    	context.put(E.folderIndex.name(), Integer.valueOf(0));
    	context.put(E.folderContentIds.name(), getFolderContentIds());
    }
    
    /**
     * prepare folder list
     * @return
     */
    private List<String> getFolderContentIds() {
    	List<String> folderContentIds = FastList.newInstance();
    	folderContentIds.add("folder1");
    	folderContentIds.add("folder2");
    	return folderContentIds;
    }

}
