/**
 * 
 */
package org.overturetool.potrans.preparation;

import java.io.File;

/**
 * @author miguel_ferreira
 * 
 */
public class Utils {

	public static void validateAtLeastOneFile(String[] fileNames, String message)
			throws IllegalArgumentException {
		if (fileNames == null || fileNames.length == 0 || fileNames[0] == null
				|| fileNames[0].length() == 0) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void validateStringNotEmptyNorNull(String string,
			String message) throws IllegalArgumentException {
		if(string == null || string.length() == 0) {
			throw new IllegalArgumentException(message);
		}
	}
	
	/**
	 * @param fileName
	 * @throws IllegalArgumentException
	 */
	public static void validateIsFileAndExists(String fileName, String message)
			throws IllegalArgumentException {
		File file = new File(fileName);
		if(!file.exists() || !file.isFile()) { 
			throw new IllegalArgumentException(message);
		}
	}

}
