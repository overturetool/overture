package org.overture.ast.patterns.assistants;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.AExpressionPattern;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AIntegerPattern;
import org.overture.ast.patterns.AQuotePattern;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ASeqPattern;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.AUnionPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.typechecker.NameScope;

public class PPatternAssistant {

	

	public static LexNameList getVariableNames(PPattern pattern) {
		switch (pattern.kindPPattern()) {
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
