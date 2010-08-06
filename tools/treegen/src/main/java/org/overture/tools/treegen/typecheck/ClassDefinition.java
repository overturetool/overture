package org.overture.tools.treegen.typecheck;

import java.util.*;
import org.overture.tools.treegen.TreeGenOptions;

public class ClassDefinition {
	
	// keep track of the command-line options used to parse this file
	public TreeGenOptions opts;
	
	// keep track of the class name
	public String class_name;
	
	// keep track of the super class
	public ClassDefinition super_class;
	
	// keep track of the sub classes
	public HashSet<ClassDefinition> sub_classes = new HashSet<ClassDefinition>();
	
	// the values defined in the class
	public HashMap<String,String> values = new HashMap<String,String>();
	
	// the named member variables defined in the class
	public HashMap<String,MemberVariable> variables = new HashMap<String,MemberVariable>();
	
	// the named types defined in the class
	public HashMap<String,Type> types = new HashMap<String,Type>();
	
	// the subtypes defined within a class
	public HashMap<String,String> subtypes = new HashMap<String,String>();
	
	// the top-level entries to the tree
	public HashSet<String> tplvl = new HashSet<String>();

	// retrieve the type by name
	public Type getTypeByName (String ptn)
	{
		// default: check the local look-up table first
		if (types.containsKey(ptn)) return types.get(ptn);
		
		// alternate: check the look-up table of the super class
		if (super_class != null) {
			// delegate to super class
			return super_class.getTypeByName(ptn);
		} else {
			// flag error: type cannot be found
			return null;
		}
	}
	
	// retrieve the root type by name
	public Type getRootTypeByName (String ptn)
	{
		// declare and find the type 
		Type theType = getTypeByName(ptn);
	
		// check for the type existance
		if (theType != null) {
			// recurse to root if the type is defined
			while (theType.super_type != null) theType = theType.super_type;
		}
		
		// return the root type (if it exists)
		return theType;
	}

	// retrieve all variables recursively
	public HashMap<String,MemberVariable> getAllVariables()
	{
		// place holder for the result
		HashMap<String,MemberVariable> res = new HashMap<String,MemberVariable>();
		
		// retrieve the variables of all super classes by recursion
		if (super_class != null) {
			// add these to the result mapping
			res.putAll(super_class.getAllVariables());
		}
		
		// finally add all the member variables at this level
		res.putAll(variables);
		
		// return the result
		return res;
	}
	
	// retrieve all types recursively
	public HashSet<String> getAllTypes()
	{
		// place holder for the result
		HashSet<String> res = new HashSet<String>();
		
		// retrieve the defined types of all super classes by recursion
		if (super_class != null) {
			// add these to the result set
			res.addAll(super_class.getAllTypes());
		}
		
		// finally add all the defined types at this level
		res.addAll(types.keySet());
		
		// return the result
		return res;
	}
	
	// retrieve subtype name recursively
	public String getSubtype(String tp)
	{
		// check whether the key exists locally
		if (subtypes.containsKey(tp)) return subtypes.get(tp);
		
		// check the super type
		if (super_class != null) {
			// check the super classes recursively
			return super_class.getSubtype(tp); 
		} else {
			// default return value
			return "Node";
		}
	}
	
	// retrieve the package name
	public String getPackage()
	{
		// check if the package name is defined
		if (values.containsKey("package")) return values.get("package");
		
		// default: return the lower-cased class name
		return class_name.toLowerCase();
	}
	
	// retrieve the top-level directory
	public String getDirectory()
	{
		// check if the directory name is defined
		if (values.containsKey("directory")) return values.get("directory");
		
		// default: return the current directory indicator
		return ".";
	}
	
	// check whether or not the class uses collection types
	public boolean hasCollection (String tpnm) {
		// check the base node variables
		for (String mvnm: variables.keySet()) {
			MemberVariable mv = variables.get(mvnm);
				if (mv.type.isCollection()) return true;
		}
		
		// retrieve the type
		Type tp = this.getTypeByName(tpnm);
		if (tp != null) {
			if (tp.isRecordType()) {
				// cast to a record type
				RecordType rtp = (RecordType) tp;
				
				// check each field
				for (Field fld : rtp.getAllFields()) {
					if (fld.field_type.isCollection()) return true;
				}
			}
		}
		
		// default
		return false;
	}
	
	// retrieve the top-level tree entry points
	public Set<String> getToplevel()
	{
		// make a copy of the top-level entries
		HashSet<String> retval = new HashSet<String>(tplvl);
		
		// merge all top-level entities from the derived types
		if (super_class != null) retval.addAll(super_class.getToplevel());
		
		// return the result set
		return retval;
	}

}
