package org.overture.ast.patterns.assistants;

import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ASeqPattern;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.AUnionPattern;
import org.overture.ast.patterns.PPattern;
import org.overturetool.vdmj.lex.LexNameList;

public class PPatternAssistant {

	public static LexNameList getVariableNames(PPattern pattern) {
		switch (pattern.kindPPattern()) {
		case CONCATENATION:
			return AConcatenationPatternAssistant.getVariableNames((AConcatenationPattern)pattern);
		case IDENTIFIER:
			return AIdentifierPatternAssistant.getVariableNames((AIdentifierPattern)pattern);
		case RECORD:
			return ARecordPatternAssistant.getVariableNames((ARecordPattern)pattern);
		case SEQ:
			return ASeqPatternAssistant.getVariableNames((ASeqPattern)pattern);
		case SET:
			return ASetPatternAssistant.getVariableNames((ASetPattern)pattern);
		case TUPLE:
			return ATuplePatternAssistant.getVariableNames((ATuplePattern)pattern);
		case UNION:
			return AUnionPatternAssistant.getVariableNames((AUnionPattern)pattern);
		default:
			return getVariableNamesBaseCase(pattern);
		}
	}
	
	public static LexNameList getVariableNamesBaseCase(PPattern pattern)
	{
		return new LexNameList();	
	}

	

}
