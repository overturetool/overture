package org.overturetool.jml.util;

public class MapperException extends Exception {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The message of the exception.
	 */
	public String message;
	
	
	
	public MapperException(String str) {
		
		this.message = str;
		
		System.out.println("An Exception occured.\nOriginal message: " + this.message + "\nAborting...\n");
	}
	
}
