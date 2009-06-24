package org.overturetool.eclipse.plugins.launching.internal.launching;

public class Dialect {
	private String id;
	private String name;
	
	public Dialect(String dialectName, String dialectId) {
		id= dialectId;
		name = dialectName;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}	
	
}
