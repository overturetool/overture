package org.overture.core.npp;

/**
 * The Interface InsTable lists all available VDM symbols and is a contract
 * for any symbol table to be used with the new pretty printer.
 *  
 * @author ldc
 */
public interface InsTable {

	
	
	
	/**
	 * Lookup the name or symbol string corresponding to the key.
	 * 
	 * @param key
	 *            the key identifying the language construct
	 * @return the attribute string to be printed for the language construct. A
	 *         configurable default value is returned if the attribute is not
	 *         found.
	 */
	String getAttribute(String key);

	/**
	 * Insert a new name or symbol for a language construct. If the table
	 * already contains an attribute for the key, the old attribute is replaced.
	 * 
	 * @param key
	 *            the key identifying the language construct
	 * @param attribute
	 *            the attribute to associate with the key
	 */
	void insertAttribute(String key, String attribute);

	/**
	 * Default value for missing attributes. Returned when an attribute is not
	 * found.
	 * 
	 * @return the Error String representing default value
	 */
	String getError();

	String getPlus();

}
