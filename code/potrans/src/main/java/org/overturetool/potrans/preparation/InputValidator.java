/**
 * 
 */
package org.overturetool.potrans.preparation;

import java.io.File;

/**
 * @author miguel_ferreira
 * 
 */
public class InputValidator {
	
	public static void validateAtLeastOneString(String[] strings,
			String message) throws InputException {
		if (strings == null || strings.length == 0 || strings[0] == null
				|| strings[0].length() == 0) {
			throw new InputException(message);
		}
	}

	public static void validateStringNotEmptyNorNull(String string,
			String message) throws InputException {
		if(string == null || string.length() == 0) {
			throw new InputException(message);
		}
	}
	
	/**
	 * @param fileName
	 * @throws InputException
	 */
	public static void validateIsFileAndExists(String fileName, String message)
			throws InputException {
		File file = new File(fileName);
		if(!file.exists() || !file.isFile()) { 
			throw new InputException(message);
		}
	}

}
