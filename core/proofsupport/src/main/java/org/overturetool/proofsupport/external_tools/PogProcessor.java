package org.overturetool.proofsupport.external_tools;

import java.util.List;

public interface PogProcessor {

	public List<String[]> extractPosFromFile(String pogFileName) throws PogProcessorException;
	
	public String extractPoExpression(String[] poText);
	
	public List<String> extractPoExpressions(List<String[]> poList);
}
