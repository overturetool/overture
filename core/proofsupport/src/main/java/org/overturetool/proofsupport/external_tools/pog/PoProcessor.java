package org.overturetool.proofsupport.external_tools.pog;

import java.util.List;

public interface PoProcessor {

	public List<String[]> extractPosFromFile(String pogFileName) throws PoProcessorException;
	
	public String extractPoExpression(String[] poText);
	
	public List<String> extractPoExpressions(List<String[]> poList);
}
