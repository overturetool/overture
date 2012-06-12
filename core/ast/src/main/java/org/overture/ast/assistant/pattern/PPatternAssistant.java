package org.overture.ast.assistant.pattern;

import java.util.HashSet;
import java.util.Set;

import org.overture.ast.lex.LexNameList;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ASeqPattern;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.AUnionPattern;
import org.overture.ast.patterns.PPattern;

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

	/**
	 * This method should only be called by subclasses of PPattern. For other classes
	 * call {@link PPatternAssistant#getVariableNames(PPattern)}.
	 * @param pattern
	 * @return
	 */
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
	
	private static LexNameList getAllVariableNamesBaseCase(PPattern pattern)
	{
		return new LexNameList();	
	}

	

}
