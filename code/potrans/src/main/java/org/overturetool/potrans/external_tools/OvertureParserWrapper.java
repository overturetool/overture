/**
 * 
 */
package org.overturetool.potrans.external_tools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.overturetool.ast.imp.OmlDocument;
import org.overturetool.ast.itf.IOmlDocument;
import org.overturetool.ast.itf.IOmlExpression;
import org.overturetool.parser.imp.OvertureParser;
/**
 * @author miguel_ferreira
 *
 */
public class OvertureParserWrapper {
	
	public static IOmlDocument getOmlDocument(String vdmFileName)
			throws FileNotFoundException, CGException {
		InputStreamReader fileStream = new InputStreamReader(
				new FileInputStream(vdmFileName));
		return getOmlDocumentFromReader(vdmFileName, fileStream);
	}
	
	public static IOmlExpression getOmlExpression(String vdmExpression)
			throws CGException {
		StringReader sr = new StringReader(vdmExpression);
		OmlDocument omlDocument = getOmlDocumentFromReader(null, sr);
		return omlDocument.getExpression();
	}

	/**
	 * @param vdmFileName
	 * @param inputReader
	 * @return
	 * @throws CGException
	 */
	private static OmlDocument getOmlDocumentFromReader(String vdmFileName,
			Reader inputReader) throws CGException {
		OvertureParser parser = new OvertureParser(inputReader);
		parser.parseDocument();
		parser.astDocument.setFilename(vdmFileName);

		return parser.astDocument;
	}
	
}
