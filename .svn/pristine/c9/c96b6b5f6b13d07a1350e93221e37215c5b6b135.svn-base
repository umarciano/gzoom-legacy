package com.mapsengineering.base.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class SamlHttpRequest extends HttpServletRequestWrapper {

	private String principal;

	SamlHttpRequest(final HttpServletRequest request, final String samlPrincipal ) {

		super(request);

		this.principal = samlPrincipal;

	}


	@Override
	public String getRemoteUser() {

		if (null == this.principal) {
			return super.getRemoteUser();

		} else {
			return principal;
		}
	}

}
