package org.overturetool.eclipse.plugins.launching.internal.launching;

public interface IOvertureInstallType {
	public Dialect[] getSupportedDialects(); 
	
	public String getDialectNameFromId(String id);
}
