package org.overture.tools.treegen.typecheck;

public class MapType extends Type {

	// embedded domain and range types
	public Type domain;
	public Type range;
	
	// constructor
	public MapType (Type dom, Type rng) { domain = dom; range = rng; }
	
	// overloaded member function
	public boolean isMapType() { return true; }	
	public boolean isCollection() { return true; }

}
