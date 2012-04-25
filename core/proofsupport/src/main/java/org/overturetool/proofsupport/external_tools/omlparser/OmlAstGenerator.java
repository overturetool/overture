package org.overturetool.proofsupport.external_tools.omlparser;

import org.overturetool.ast.itf.IOmlDocument;
import org.overturetool.ast.itf.IOmlExpression;

public interface OmlAstGenerator {

	public IOmlDocument getOmlDocument(String vdmFileName) throws ParserException;
	
	public IOmlExpression getOmlExpression(String vdmExpression) throws ParserException;
}
