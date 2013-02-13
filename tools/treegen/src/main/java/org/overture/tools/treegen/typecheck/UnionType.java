package org.overture.tools.treegen.typecheck;

import java.util.*;

public class UnionType extends Type {

	// member variables
	public Vector<Type> union_type = new Vector<Type>();
	
	// overloaded member function
	public boolean isUnionType() { return true; }
	
	// check for quoted type union
	public boolean isQuotedTypeUnion()
	{
		// check each element
		for (Type tp: union_type) if (!tp.isQuotedType()) return false;
		
		// all elements are quoted types
		return true;
	}

	// retrieve the list of quoted types
	public Vector<String> getQuotedTypes()
	{
		// create an empty vector
		Vector<String> res = new Vector<String>();
		
		// check each element and collect all quoted types
		for (Type tp: union_type)
			if (tp.isQuotedType()) {
				QuotedType qtp = (QuotedType) tp;
				res.add(qtp.quoted_type);
			}
		
		// return the result set
		return res;
	}
	
	// check for type name union
	public boolean isTypeNameUnion()
	{
		// check each element
		for (Type tp: union_type) if (!tp.isTypeName()) return false;
		
		// all elements are type names
		return true;
	}
	
	// retrieve the list of type names
	public Vector<String> getTypeNames()
	{
		// create an empty vector
		Vector<String> res = new Vector<String>();
		
		// check each element and collect all type names
		for (Type tp: union_type) 
			if (tp.isTypeName()) {
				TypeName tpnm = (TypeName) tp;
				res.add(tpnm.type_name);
			}
		
		// return the result set
		return res;
	}
	
}
