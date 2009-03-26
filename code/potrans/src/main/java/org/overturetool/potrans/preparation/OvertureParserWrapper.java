/**
 * 
 */
package org.overturetool.potrans.preparation;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.Collection;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.overturetool.ast.imp.OmlDocument;
import org.overturetool.ast.imp.OmlExpression;
import org.overturetool.parser.imp.OvertureParser;
/**
 * @author miguel_ferreira
 *
 */
public class OvertureParserWrapper {
	
	public static OmlDocument getOmlDocument(String vdmFileName) 
	       throws FileNotFoundException, CGException {
			InputStreamReader fileStream = new InputStreamReader(new FileInputStream(vdmFileName));
			OvertureParser parser = new OvertureParser(fileStream);
			parser.parseDocument();
			parser.astDocument.setFilename(vdmFileName);
			
			return parser.astDocument;
	}
	
// TODO
//	public static OmlExpression getOmlExpressions(Collection<String> vdmExpressions) {
//		
//	}
}
