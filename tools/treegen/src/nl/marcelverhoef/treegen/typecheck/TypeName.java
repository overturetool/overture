package nl.marcelverhoef.treegen.typecheck;

public class TypeName extends Type {

	// embedded type name
	public String type_name;
	
	// constructor
	public TypeName (String tnstr) { type_name = tnstr; }

	// overloaded member function
	public boolean isTypeName() { return true; }

}
