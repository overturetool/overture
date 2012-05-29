package org.overture.ast.patterns.assistants;

import java.util.HashSet;
import java.util.Set;

import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ASeqPattern;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.AUnionPattern;
import org.overture.ast.patterns.PPattern;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;

public class PPatternAssistant {

	
	public static LexNameList getVariableNames(PPattern pattern) {
		
		return getVariableNamesBaseCase(pattern);
	}
	
	private static LexNameList getVariableNamesBaseCase(PPattern pattern) {
		Set<LexNameToken> set = new HashSet<LexNameToken>();
		set.addAll(getAllVariableNames(pattern));
		LexNameList list = new LexNameList();
		list.addAll(set);
		return list;
	}

	public static LexNameList getAllVariableNames(PPattern pattern) {
		switch (pattern.kindPPattern()) {
		case CONCATENATION:
			return AConcatenationPatternAssistant.getAllVariableNames((AConcatenationPattern)pattern);
		case IDENTIFIER:
			return AIdentifierPatternAssistant.getAllVariableNames((AIdentifierPattern)pattern);
		case RECORD:
			return ARecordPatternAssistant.getAllVariableNames((ARecordPattern)pattern);
		case SEQ:
			return ASeqPatternAssistant.getAllVariableNames((ASeqPattern)pattern);
		case SET:
			return ASetPatternAssistant.getAllVariableNames((ASetPattern)pattern);
		case TUPLE:
			return ATuplePatternAssistant.getAllVariableNames((ATuplePattern)pattern);
		case UNION:
			return AUnionPatternAssistant.getAllVariableNames((AUnionPattern)pattern);
		default:
			return getAllVariableNamesBaseCase(pattern);
		}
	}
	
	public static LexNameList getAllVariableNamesBaseCase(PPattern pattern)
	{
		return new LexNameList();	
	}

	

}
