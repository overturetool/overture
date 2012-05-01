package com.lausdahl.ast.creator.methods;

import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.env.Environment;

public class GetChildrenMethod extends Method {

	public GetChildrenMethod(IClassDefinition c, Environment env) {
		super(c, env);
	}
	
	@Override
	public Set<String> getRequiredImports() {
		Set<String> imports =  super.getRequiredImports();
		imports.add("java.util.Map");
		imports.add("java.util.Hashtable");
		imports.add(env.iNode.getName().getCanonicalName());
		return imports;
	}

	@Override
	protected void prepare() {
		this.name = "getChildren";

		this.returnType = "Map<String,Object>";
		this.requiredImports.add("java.util.Map");

		this.arguments.add(new Argument("Boolean", "includeInheritedFields"));
		this.annotation = "@Override";

		StringBuilder sbDoc = new StringBuilder();
		sbDoc.append("\t/**\n");
		sbDoc.append("\t * Creates a map of all field names and their value\n");
		sbDoc.append("\t * @param includeInheritedFields if true all inherited fields are included\n");
		sbDoc.append("\t * @return a a map of names to values of all fields\n");
		sbDoc.append("\t */");

		StringBuilder sb = new StringBuilder();

		List<Field> fields = new Vector<Field>();

		fields.addAll(classDefinition.getFields());


		sb.append("\t\t"+this.returnType+" fields = new Hashtable<String,Object>();\n");
		sb.append("\t\tif(includeInheritedFields)\n");
		sb.append("\t\t{\n");
		sb.append("\t\t\tfields.putAll(super."+this.name+"(includeInheritedFields));\n");
		sb.append("\t\t}\n");
		
		for (Field field : fields) {
			sb.append("\t\tfields.put(\""+field.getName()+"\",this."+field.getName()+");\n");
		}
		
		sb.append("\t\treturn fields;");
		
		this.javaDoc = sbDoc.toString();
		this.body = sb.toString();
	}
}
