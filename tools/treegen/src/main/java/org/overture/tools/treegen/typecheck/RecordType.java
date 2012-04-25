package org.overture.tools.treegen.typecheck;

import java.util.*;

public class RecordType extends Type {
	
	// keep track of the record type name
	String name;
	
	// keep track of the record fields
	Vector<Field> fields = new Vector<Field>();
	
	// constructor
	public RecordType(String prn) { name = prn; }
	
	// overloaded member operations
	public boolean isRecordType() { return true; }
	
	// auxiliary operation to retrieve all fields
	public Vector<Field> getAllFields()
	{
		// place holder for the result
		Vector<Field> res = new Vector<Field>();
		
		// check the super type
		if (super_type != null) {
			if (super_type.isRecordType()) {
				// retrieve the type by casting
				RecordType theRecordType = (RecordType) super_type;
				
				// add the fields to the result set
				res.addAll(theRecordType.getAllFields());
			} else {
				if (!super_type.isUnionType()) {
					// diagnostics
					System.out.println("Incompatible super type found for type '"+name+"'");
				}
			}
		}
		
		// copy all locally defined fields now
		res.addAll(fields);
		
		// return the result set
		return res;
	}

}
