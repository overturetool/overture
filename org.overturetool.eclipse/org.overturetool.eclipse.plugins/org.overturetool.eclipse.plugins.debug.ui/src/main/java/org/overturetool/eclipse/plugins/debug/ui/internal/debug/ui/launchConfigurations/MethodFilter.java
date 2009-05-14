package org.overturetool.eclipse.plugins.debug.ui.internal.debug.ui.launchConfigurations;


public class MethodFilter extends PrivateMethodFilter{
	public MethodFilter() {		
		addFilter(PrivateMethodFilter.FILTER_PRIVATEMETHODS);
	}
}
