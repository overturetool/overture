/**
 * 
 */
package org.overturetool.potrans.preparation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.overturetool.ast.imp.OmlDocument;
import org.overturetool.parser.imp.OvertureParser;
/**
 * @author miguel_ferreira
 *
 */
public class OvertureParserWrapper {
	
	public static OmlDocument getOmlDocument(String vdmFileName) 
	       throws FileNotFoundException, CGException {
			InputStreamReader fileStream = new InputStreamReader(new FileInputStream(vdmFileName));
			return getOmlDocument(vdmFileName, fileStream);
	}

	/**
	 * @param vdmFileName
	 * @param inputStream
	 * @return
	 * @throws CGException
	 */
	private static OmlDocument getOmlDocument(String vdmFileName,
			InputStreamReader inputStream) throws CGException {
		OvertureParser parser = new OvertureParser(inputStream);
		parser.parseDocument();
		parser.astDocument.setFilename(vdmFileName);
		
		return parser.astDocument;
	}
	
}
