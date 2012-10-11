package com.lausdahl.ast.creator.definitions;

import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.java.definitions.JavaName;

public class ExternalJavaClassDefinition extends BaseClassDefinition {
	public final boolean extendsNode;

	public ExternalJavaClassDefinition(String rawName,
			IClassDefinition superClass, ClassType type, String name,
			boolean extendsNode, Environment env) {
		super(createName(rawName, superClass, type, name, extendsNode, env),
				env.getAstPackage());
		this.extendsNode = extendsNode;

	}

	private static JavaName createName(String rawName,
			IClassDefinition superClass, ClassType type, String name,
			boolean extendsNode, Environment env) {
		if (name.contains(".")) {
			return new JavaName(name.substring(0, name.lastIndexOf(".")),
					name.substring(name.lastIndexOf(".") + 1));
		} else {
			return new JavaName("", name);
		}
	}

	@Override
	public JavaName getName() {
		return name;
	}

	@Override
	public String getJavaSourceCode(StringBuilder sb, Environment env) {
		return "";
	}

	@Override
	public String getVdmSourceCode(StringBuilder sb) {
		return "";
	}

	@Override
	public String toString() {
		return getName().toString();
	}

	@Override
	public boolean hasSuper() {
		return false;
	}
}
