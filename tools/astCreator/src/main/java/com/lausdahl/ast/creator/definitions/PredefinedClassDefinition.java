package com.lausdahl.ast.creator.definitions;

import com.lausdahl.ast.creator.java.definitions.JavaFrozenName;
import com.lausdahl.ast.creator.java.definitions.JavaName;

public class PredefinedClassDefinition extends BaseClassDefinition// implements
// IClassDefinition
{
	private String tag = "";

	public PredefinedClassDefinition(String packageName, String name) {
		super(new JavaName(packageName, name), "");

	}

	public PredefinedClassDefinition(String packageName, String name,
			boolean frozenName) {
		super(frozenName ? new JavaFrozenName(packageName, name)
				: new JavaName(packageName, name), "");
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return this.tag;
	}

	@Override
	public String toString() {
		return getName().getCanonicalName();
	}

}
