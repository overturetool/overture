package org.overture.tools.treegen.typecheck;

public class Field {
	
	// keep track of the field name
	public String field_name;
	
	// keep track of the field type
	public Type field_type;
	
	// constructor
	public Field (String pfn, Type pft) { field_name = pfn; field_type = pft; }
	
}
