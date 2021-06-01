package com.mapsengineering.base.test;

import com.mapsengineering.base.util.ContextPermissionPrefixEnum;

public class TestContextPermissionPrefixEnum extends BaseTestCase {
	
	/**
	 * test ContextPermissionPrefixEnum
	 */
	public void testContextPermissionPrefixEnum() {
		String bscPermissionPrefix = ContextPermissionPrefixEnum.getPermissionPrefix("CTX_BS");
		String orgPermissionPrefix = ContextPermissionPrefixEnum.getPermissionPrefix("CTX_OR");
		String emplPermissionPrefix = ContextPermissionPrefixEnum.getPermissionPrefix("CTX_EP");
		
		assertEquals("BSCPERF", bscPermissionPrefix);
		assertEquals("ORGPERF", orgPermissionPrefix);
		assertEquals("EMPLPERF", emplPermissionPrefix);
	}

}
