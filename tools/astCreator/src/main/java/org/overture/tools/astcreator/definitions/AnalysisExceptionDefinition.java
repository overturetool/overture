package org.overture.tools.astcreator.definitions;

import org.overture.tools.astcreator.env.Environment;
import org.overture.tools.astcreator.java.definitions.JavaName;
import org.overture.tools.astcreator.methods.Method.Argument;
import org.overture.tools.astcreator.methods.SuperConstructorMethod;

public class AnalysisExceptionDefinition extends BaseClassDefinition {

	public AnalysisExceptionDefinition(JavaName name, Environment env) {
		super(name, env.getAstPackage());
		superDef = new PredefinedClassDefinition("java.lang", "Exception");
		addMethod(new SuperConstructorMethod(this, new Argument[] {}));
		addMethod(new SuperConstructorMethod(this, new Argument("String",
				"message")));
		addMethod(new SuperConstructorMethod(this, new Argument("String",
				"message"), new Argument("Throwable", "cause")));
		addMethod(new SuperConstructorMethod(this, new Argument("Throwable",
				"cause")));
	}

	public AnalysisExceptionDefinition(String packageName, String name,
			Environment env) {
		this(new JavaName(packageName, name), env);
	}

	public String toString() {
		return "I should die";
	}

}
