package org.overture.tools.astcreator.methods;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.overture.tools.astcreator.definitions.BaseClassDefinition;
import org.overture.tools.astcreator.definitions.IClassDefinition;
import org.overture.tools.astcreator.definitions.IInterfaceDefinition;
import org.overture.tools.astcreator.env.Environment;

public abstract class Method {
	public static class Argument {
		public Argument() {
		}

		public Argument(String type, String name) {
			this.type = type;
			this.name = name;
		}

		public String type;
		public String name;

		@Override
		public String toString() {
			return type + " " + name;
		}

		@Override
		public int hashCode() {
			return name.hashCode() + type.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Argument) {
				Argument obj = (Argument) o;
				return name.equals(obj.name) && type.equals(obj.type);
			}
			return false;
		}

	}

	public static Method newMethod(Class<? extends Method> method,
                                       IClassDefinition c) 
            throws InstantiationException, IllegalAccessException
	{
		Method m = method.newInstance();
		m.setClassDefinition(c);
		return m;
	}

	public boolean isConstructor = false;
	public boolean isAbstract = false;
	protected String javaDoc = "";
	public String name;
	public String returnType = "void";
	public List<Argument> arguments = new Vector<Method.Argument>();
	public String body;
	public String annotation;
	public IClassDefinition classDefinition;
	protected Set<String> requiredImports = new HashSet<String>();
	protected Set<IInterfaceDefinition> throwsDefinitions = new HashSet<IInterfaceDefinition>();
	// private boolean isStructureFinal = false;
	protected boolean skip = false;
	protected boolean optionalVdmArgument = true;

	// protected Environment env;

	public Method(IClassDefinition c) {
		setClassDefinition(c);
	}

	// public Method(IClassDefinition c, boolean finalStructure)
	// {
	// this.isStructureFinal = finalStructure;
	// setClassDefinition(c);
	// }

	public void setClassDefinition(IClassDefinition c) {
		this.classDefinition = c;
	}

	protected void internalPrepare(Environment env) {
		// if (isStructureFinal)
		// {
		// return;
		// }
		requiredImports.clear();
		throwsDefinitions.clear();
		arguments.clear();
		prepare(env);
	}

	private void internalVdmPrepare(Environment env) {
		optionalVdmArgument = true;
		skip = false;
		requiredImports.clear();
		arguments.clear();
		prepareVdm(env);
	}

	protected void prepare(Environment env) {

	}

	protected void prepareVdm(Environment env) {
		prepare(env);
	}

	public Set<String> getRequiredImports(Environment env) {
		internalPrepare(env);
		addImportForType(returnType, env);
		for (Argument arg : arguments) {
			addImportForType(arg.type, env);
		}

		if (!throwsDefinitions.isEmpty()) {
			for (Iterator<IInterfaceDefinition> t = throwsDefinitions
					.iterator(); t.hasNext();) {
				requiredImports.add(t.next().getName().getCanonicalName());
			}
		}

		return requiredImports;
	}

	private void addImportForType(String typeName, Environment env) {
		// TODO type name as a string is not good
		Set<String> names = new HashSet<String>();
		if (typeName.contains("<")) {
			names.add(typeName.substring(0, typeName.indexOf('<')).trim());
			if (!typeName.contains("extends")) {
				String tmp = typeName.substring(typeName.indexOf('<') + 1,
						typeName.indexOf('>')).trim();
				names.addAll(Arrays.asList(tmp.split("\\,")));
			} else {
				String tmp = typeName.substring(typeName.indexOf('<') + 1,
						typeName.indexOf('>')).trim();
				tmp = tmp.replaceAll("\\?", "");
				tmp = tmp.replaceAll("extends", "");
				tmp = tmp.replaceAll(" ", "");
				names.addAll(Arrays.asList(tmp.split("\\,")));
			}
		} else {
			names.add(typeName);
		}

		for (String name : names) {
			IInterfaceDefinition defIntf = env.lookUpInterface(name);
			if (defIntf != null) {
				requiredImports.add(defIntf.getName().getCanonicalName());
			}

			IClassDefinition def = env.lookUpPreferSameContext(name,
					classDefinition != null ? classDefinition.getAstPackage()
							: "");
			if (def != null) {
				requiredImports.add(def.getName().getCanonicalName());
			}
		}

	}

	public Set<String> getRequiredImportsSignature(Environment env) {
		internalPrepare(env);
		addImportForType(returnType, env);
		for (Argument arg : arguments) {
			addImportForType(arg.type, env);
		}

		if (!throwsDefinitions.isEmpty()) {
			for (Iterator<IInterfaceDefinition> t = throwsDefinitions
					.iterator(); t.hasNext();) {
				requiredImports.add(t.next().getName().getCanonicalName());
			}
		}

		return requiredImports;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public String getJavaSourceCode(Environment env) {
		internalPrepare(env);
		if (skip) {
			return "";
		}

		String tmp = javaDoc != null ? javaDoc + "\n" : "";
		if (annotation != null) {
			tmp += "\t" + annotation + "\n";
		}
		tmp += getSignature(env);

		if (isAbstract) {
			tmp += ";\n";
		} else {
			tmp += "\n\t{\n";

			if (body != null) {
				tmp += body;
			}
			tmp += "\n\t}\n\n";
		}
		return tmp;
	}

	public String getVdmSourceCode(Environment env) {
		internalVdmPrepare(env);
		if (skip) {
			return "";
		}

		String tmp = javaDoc != null ? javaDoc + "\n" : "";
		if (annotation != null) {
			tmp += "\t--" + annotation + "\n";
		}
		tmp += getVdmSignature(env) + "\n";

		if (isAbstract) {
			tmp += "is subclass responsibility;";
		} else {
			tmp += "\t(\n";

			if (body != null) {
				tmp += body;
			}
			tmp += "\n\t);\n\n";
		}
		return tmp;
	}

	public String getSignature(Environment env) {
		internalPrepare(env);
		String tmp = "\t"
				+ ("public " + (isAbstract ? "abstract " : "") + (isConstructor ? ""
						: returnType)).trim() + " " + name + "(";
		for (Argument a : arguments) {
			tmp += a.toString() + ", ";
		}
		if (!arguments.isEmpty()) {
			tmp = tmp.substring(0, tmp.length() - 2);
		}
		tmp += ")";

		if (!throwsDefinitions.isEmpty()) {
			tmp += " throws ";
			for (Iterator<IInterfaceDefinition> t = throwsDefinitions
					.iterator(); t.hasNext();) {
				tmp += t.next().getName().getName();
				if (t.hasNext()) {
					tmp += ", ";
				}

			}
		}
		return tmp;
	}

	public String getVdmSignature(Environment env) {
		internalVdmPrepare(env);
		String tmp = "\t" + "public " + name + ": ";
		for (Argument a : arguments) {
			String typeName = a.type;
			if (typeName.contains("List<")) {
				typeName = "seq of "
						+ typeName.substring(typeName.lastIndexOf(' '),
								typeName.lastIndexOf('>')).trim();
			}
			tmp += (optionalVdmArgument ? "[" : "") + typeName
					+ (optionalVdmArgument ? "]" : "") + " * ";
		}
		if (!arguments.isEmpty()) {
			tmp = tmp.substring(0, tmp.length() - 2);
		} else {
			tmp += "() ";
		}

		String returnTypeName = BaseClassDefinition
				.stripGenericArguments(returnType);
		tmp += " ==> "
				+ (returnType.length() == 0 ? name
						: (returnType.equals("void") ? "()" : returnTypeName))
				+ "\n";
		tmp += "\t" + name + "(";
		for (Argument a : arguments) {
			tmp += a.name + ", ";
		}
		if (!arguments.isEmpty()) {
			tmp = tmp.substring(0, tmp.length() - 2);
		}

		tmp += ") == ";
		return tmp;
	}

	public String getJavaDoc(Environment env) {
		internalPrepare(env);
		return javaDoc;
	}

	protected String getSpecializedTypeName(IInterfaceDefinition c,
			Environment env) {
		IInterfaceDefinition intfForClass = env
				.getInterfaceForCommonTreeNode(c);
		if (intfForClass == null) {
			return c.getName().getName();
		} else {
			return intfForClass.getName().getName();
		}
	}

	public boolean isOverride(Method m, Environment env) {
		if (this.name.equals(m.name) && arguments.size() == m.arguments.size()) {
			for (int i = 0; i < arguments.size(); i++) {
				Argument sourceArg1 = arguments.get(i);
				Argument basearg1 = m.arguments.get(i);
				if (!env.isSuperTo(env.lookUpType(basearg1.type),
						env.lookUpType(sourceArg1.type))) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean isSignatureEqual(Method m) {
		if (this.name.equals(m.name) && arguments.size() == m.arguments.size()
				&& returnType.equals(m.returnType)) {
			for (int i = 0; i < arguments.size(); i++) {
				Argument sourceArg1 = arguments.get(i);
				Argument basearg1 = m.arguments.get(i);
				if (!basearg1.type.equals(sourceArg1.type)) {
					return false;
				}

			}
			return true;
		} else {
			return false;
		}
	}
}
