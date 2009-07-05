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
public abstract class OvertureParserWrapper {

	public static IOmlDocument getOmlDocument(String vdmFileName) throws OvertureParserException {
		try {
			InputStreamReader fileStream = new InputStreamReader(new FileInputStream(vdmFileName));
			return getOmlDocumentFromReader(vdmFileName, fileStream);
		} catch (FileNotFoundException e) {
			throw new OvertureParserException("Can't find VDM file '" + vdmFileName + "'.", e);
		} catch (CGException e) {
			throw new OvertureParserException("Error while parsing file '" + vdmFileName + "'.", e);
		}

	}

	public static IOmlExpression getOmlExpression(String vdmExpression) throws OvertureParserException {
		StringReader sr = new StringReader(vdmExpression);
		
		try {
			OmlDocument omlDocument = getOmlDocumentFromReader(null, sr);
			return omlDocument.getExpression();
		} catch (CGException e) {
			throw new OvertureParserException("Error while parsing expression: " + vdmExpression, e);
		}
	}

	/**
	 * @param vdmFileName
	 * @param inputReader
	 * @return
	 * @throws CGException
	 */
	private static OmlDocument getOmlDocumentFromReader(String vdmFileName, Reader inputReader) throws CGException {
		OvertureParser parser = new OvertureParser(inputReader);
		parser.parseDocument();
		parser.astDocument.setFilename(vdmFileName);

		return parser.astDocument;
	}

}
