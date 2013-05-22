/*******************************************************************************
 * Copyright (c) 2009, 2013 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.guibuilder.internal.ir;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * A table to store all annotation of class and/or operations/functions.
 * 
 * @author carlos
 *
 */
public class AnnotationTable {

	// table to store class annotations
	private Hashtable <String, Vector<VdmAnnotation> > classAnnotations = null;
	// table to store op annotations
	private Hashtable <String, Vector<VdmAnnotation> > opAnnotations = null;

	/**
	 * Constructor
	 */
	public AnnotationTable() {
		classAnnotations = new Hashtable<String, Vector<VdmAnnotation> >();
		opAnnotations = new Hashtable<String, Vector<VdmAnnotation> >();
	}

	/**
	 * Gets all annotations of a operation/function identified by its name
	 * @param opName The name of the operation
	 * @return The list of annotations
	 */
	public Vector<VdmAnnotation> getOpAnnotations(String opName) {
		return opAnnotations.get(opName);
	}

	/**
	 * Gets all annotations of a class identified by its name
	 * @param className The name of the class
	 * @return The list of annotations
	 */
	public Vector<VdmAnnotation> getClassAnnotations(String className) {
		return classAnnotations.get(className);
	}
	
	/**
	 * Adds a class annotation to the table
	 * @param className The name of the class that the annotation belongs to.
	 * @param annotationName The name of the annotation.
	 * @param value The value of the annotation
	 */
	public void addClassAnnotation(String className, String annotationName, String value) {
		Vector<VdmAnnotation> list = classAnnotations.get(className);
		if( list == null ) {
			Vector<VdmAnnotation> a = new Vector<VdmAnnotation>();
			a.add(new VdmAnnotation(annotationName, value));
			classAnnotations.put(className, a);
		} else {
			list.add(new VdmAnnotation(annotationName, value));
		}		
	}

	/**
	 * Adds a operation/function to the table
	 * @param className The name of the class that the annotation belongs to.
	 * @param opName The name of the operation/function that the annotation belongs to.
	 * @param annotationName The name of the annotation.
	 * @param value The value of the annotation.
	 */
	public void addOpAnnotation(String className, String opName, String annotationName, String value) {
		Vector<VdmAnnotation> list = opAnnotations.get(className + opName);
		if( list == null ) {
			Vector<VdmAnnotation> a = new Vector<VdmAnnotation>();
			a.add(new VdmAnnotation(annotationName, value));
			opAnnotations.put(className + opName, a);
		} else {
			list.add(new VdmAnnotation(annotationName, value));
		}		
	}
	
	/**
	 * Checks if a class has any annotation.
	 * @param className The name of the class
	 * @return Returns true if the class has annotation.
	 */
	public boolean classHasAnnotations(String className) {
		if (classAnnotations.get(className) == null)
			return false;
		return true;
	}

	/**
	 * Checks if a operation/function has any annotation.
	 * @param className The name of the class the operation/function belongs to.
	 * @param opName The name of the operation/function
	 * @return Returns true if the operation/function has any annotation.
	 */
	public boolean opHasAnnotations(String className, String opName) {
		if (opAnnotations.get(className + opName) == null)
			return false;
		return true;
	}
	
	/**
	 * Checks if a operation/function has a specific annotation (identified by its name)
	 * @param className Name of the class the operation/function belongs to.
	 * @param opName The name of the operation/function
	 * @param annotationName The name of the annotation to check.
	 * @return Returns true if the operation/function has the specific annotation.
	 */
	public boolean opHasAnnotation(String className, String opName, String annotationName) {
		Vector<VdmAnnotation> list = opAnnotations.get(className + opName);
		if ( list == null)
			return false;
		
		for( VdmAnnotation a : list) {
			if (a.getName().equals(annotationName) )
				return true;
		}
		
		return false;
	}

	/**
	 * Checks if a class has a specific annotation (identified by its name)
	 * @param className Name of the class 
	 * @param annotationName Name of the annotation to check
	 * @return Returns true if the clas has the specific annotation.
	 */
	public boolean classHasAnnotation(String className, String annotationName) {
		Vector<VdmAnnotation> list = classAnnotations.get(className);
		if ( list == null)
			return false;
		
		for( VdmAnnotation a : list) {
			if (a.getName().equals(annotationName) )
				return true;
		}
		return false;
	}

	/**
	 * Gets the value of a annotation. Null if there's none.
	 * @param annotationName
	 * @param parentName
	 * @param isClass
	 * @return The value of the annotation.
	 */
	public String getValueOf(String annotationName, String parentName, boolean isClass ) {
		if (isClass) {
			Vector<VdmAnnotation> annotations = classAnnotations.get(parentName);
			for (VdmAnnotation a : annotations) {
				if (a.getName().equals(annotationName) )
					return a.getValue();
			}
		} else {
			Vector<VdmAnnotation> annotations = opAnnotations.get(parentName);
			for (VdmAnnotation a : annotations) {
				if (a.getName().equals(annotationName) )
					return a.getValue();
			}
		}	
		return null;
	}
	
	/**
	 * Returns a "print out" in form of a String of the table.
	 * Used mostly for debugging purposes.
	 * @return String representation of the table.
	 */
	public String printTable() {
		Enumeration<String> cKeys = classAnnotations.keys();
		Enumeration<String> oKeys = opAnnotations.keys();
		String ret = "";
		String key;
		
		while(cKeys.hasMoreElements()) {
			key = cKeys.nextElement();
			ret += key + " has : \n";
			Vector<VdmAnnotation> as = classAnnotations.get(key);
			for (VdmAnnotation a : as) {
				ret += a.getName() + " = " + a.getValue();
			}
			ret += "\n";
		}

		while(oKeys.hasMoreElements()) {
			key = oKeys.nextElement();
			ret += key + " has : \n";
			Vector<VdmAnnotation> as = opAnnotations.get(key);
			for (VdmAnnotation a : as) {
				ret += a.getName() + " = " + a.getValue();
			}
			ret += "\n";
		}
		
		return ret;
	}
}
