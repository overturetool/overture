package org.overture.tools.treegen.typecheck;

public class Type {

	// member variable to build the type hierarchy
	public Type super_type = null;
	
	// member variable to keep track whether or not type is optional
	protected boolean opt = false;

	// member operations to query the type
	public boolean isOptionalType()	{ return opt; }	
	public boolean isQuotedType()	{ return false; }
	public boolean isTypeName()		{ return false; }
	public boolean isSeqType()		{ return false; }
	public boolean isSetType()		{ return false; }
	public boolean isMapType()		{ return false; }
	public boolean isUnionType()	{ return false; }
	public boolean isCharType()		{ return false; }
	public boolean isStringType()	{ return false; }
	public boolean isBooleanType()	{ return false; }
	public boolean isNatType()		{ return false; }
	public boolean isRealType()		{ return false; }
	public boolean isRecordType()	{ return false; }
	public boolean isJavaType()		{ return false; }
	public boolean isCollection()	{ return false; }
	
}
