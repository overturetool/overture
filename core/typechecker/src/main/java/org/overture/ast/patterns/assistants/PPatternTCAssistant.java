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
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.ast.types.assistants.PTypeList;
import org.overture.ast.types.assistants.PTypeSet;
import org.overture.ast.types.assistants.SNumericBasicTypeAssistant;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.typechecker.NameScope;

public class PPatternTCAssistant extends PPatternAssistant {

	public static List<PDefinition> getDefinitions(PPattern rp,
			PType ptype, NameScope scope) {		
		switch (rp.kindPPattern()) {		
		case IDENTIFIER:
			AIdentifierPattern idPattern = (AIdentifierPattern) rp;					
			List<PDefinition> defs = new ArrayList<PDefinition>();
			defs.add(new ALocalDefinition(idPattern.getLocation(), idPattern.getName(), scope, false, null, null, ptype,false));
			return defs;					
		case BOOLEAN:
		case CHARACTER:
		case EXPRESSION:
		case IGNORE:
		case INTEGER:
		case NIL:
		case QUOTE:
		case REAL:
		case STRING:
			return new Vector<PDefinition>();				
		case CONCATENATION:
			return AConcatenationPatternAssistant.getDefinitions((AConcatenationPattern)rp,ptype,scope);
		case RECORD:
			return ARecordPatternTCAssistant.getDefinitions((ARecordPattern)rp,ptype,scope);		
		case SEQ:
			return ASeqPatternTCAssistant.getDefinitions((ASeqPattern)rp,ptype,scope);
		case SET:
			return ASetPatternTCAssistant.getDefinitions((ASeqPattern)rp,ptype,scope);
		case TUPLE:
			return ATuplePatternAssistant.getDefinitions((ATuplePattern)rp,ptype,scope);
		case UNION:
			return AUnionPatternTCAssistant.getDefinitions((AUnionPattern)rp,ptype,scope);
		default:
			assert false : "PPatternAssistant.getDefinitions - should not hit this case";
			return null;
		}
		
	}

	public static void typeResolve(PPattern pattern, QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor, TypeCheckInfo question) {
		switch (pattern.kindPPattern()) {		
		case CONCATENATION:
			if(pattern instanceof AConcatenationPattern)
			{
				AConcatenationPatternAssistant.typeResolve((AConcatenationPattern)pattern,rootVisitor,question);
			}
			break;
		case EXPRESSION:
			if(pattern instanceof AExpressionPattern)
			{
				AExpressionPatternAssistant.typeResolve((AExpressionPattern)pattern,rootVisitor,question);
			}
			break;				
		case RECORD:
			if(pattern instanceof ARecordPattern)
			{
				ARecordPatternTCAssistant.typeResolve((ARecordPattern)pattern,rootVisitor,question);
			}
			break;
		case SEQ:
			if(pattern instanceof ASeqPattern)
			{
				ASeqPatternTCAssistant.typeResolve((ASeqPattern)pattern,rootVisitor,question);
			}
			break;			
		case SET:
			if(pattern instanceof ASetPattern)
			{
				ASetPatternTCAssistant.typeResolve((ASetPattern)pattern,rootVisitor,question);
			}
			break;		
		case TUPLE:
			if(pattern instanceof ATuplePattern)
			{
				ATuplePatternAssistant.typeResolve((ATuplePattern)pattern,rootVisitor,question);
			}
			break;
		case UNION:
			if(pattern instanceof AUnionPattern)
			{
				AUnionPatternTCAssistant.typeResolve((AUnionPattern)pattern,rootVisitor,question);
			}
			break;
		default:
			pattern.setResolved(true);
		}
		
		
	}

	public static void unResolve(PPattern pattern) {
		switch (pattern.kindPPattern()) 
		{
		case CONCATENATION:
			if(pattern instanceof AConcatenationPattern)
			{
				AConcatenationPatternAssistant.unResolve((AConcatenationPattern)pattern);
			}
			break;
		case RECORD:
			if(pattern instanceof ARecordPattern)
			{
				ARecordPatternTCAssistant.unResolve((ARecordPattern)pattern);
			}
			break;
		case SEQ:
			if(pattern instanceof ASeqPattern)
			{
				ASeqPatternTCAssistant.unResolve((ASeqPattern)pattern);
			}
			break;
		case SET:
			if(pattern instanceof ASetPattern)
			{
				ASetPatternTCAssistant.unResolve((ASetPattern)pattern);
			}
			break;
		case TUPLE:
			if(pattern instanceof ATuplePattern)
			{
				ATuplePatternAssistant.unResolve((ATuplePattern)pattern);
			}
			break;
		case UNION:
			if(pattern instanceof AUnionPattern)
			{
				AUnionPatternTCAssistant.unResolve((AUnionPattern)pattern);
			}
			break;		
		default:
			pattern.setResolved(false);
		}
		
	}

	

//	public static LexNameList getVariableNames(PPattern pattern) {
//		switch (pattern.kindPPattern()) {
//		case RECORD:
//			return ARecordPatternAssistant.getVariableNames((ARecordPattern)pattern);
//		case SEQ:
//			return ASeqPatternAssistant.getVariableNames((ASeqPattern)pattern);
//		case SET:
//			return ASetPatternAssistant.getVariableNames((ASetPattern)pattern);
//		case TUPLE:
//			return ATuplePatternAssistant.getVariableNames((ATuplePattern)pattern);
//		case UNION:
//			return AUnionPatternAssistant.getVariableNames((AUnionPattern)pattern);
//		default:
//			return getVariableNamesBaseCase(pattern);
//		}
//	}
//	
//	public static LexNameList getVariableNamesBaseCase(PPattern pattern)
//	{
//		return new LexNameList();	
//	}

	public static PType getPossibleType(PPattern pattern) {
		switch (pattern.kindPPattern()) {
		case BOOLEAN:
			return new ABooleanBasicType(pattern.getLocation(),false);
		case CHARACTER:
			return new ACharBasicType(pattern.getLocation(),false);
		case CONCATENATION:
			return new ASeqSeqType(pattern.getLocation(), false, new AUnknownType(pattern.getLocation(),false),false);
		case EXPRESSION:
			return new AUnknownType(pattern.getLocation(),false);
		case IDENTIFIER:
			return new AUnknownType(pattern.getLocation(),false);
		case IGNORE:
			return new AUnknownType(pattern.getLocation(),false);
		case INTEGER:
			return SNumericBasicTypeAssistant.typeOf(((AIntegerPattern)pattern).getValue().value,pattern.getLocation());
		case NIL:
			return new AOptionalType(pattern.getLocation(), false, new AUnknownType(pattern.getLocation(),false));
		case QUOTE:
			return new AQuoteType(pattern.getLocation(),false, ((AQuotePattern)pattern).getValue());
		case REAL:
			return new ARealNumericBasicType(pattern.getLocation(), false);
		case RECORD:
			return ((ARecordPattern)pattern).getType();
		case SEQ:
			return new ASeqSeqType(pattern.getLocation(), false, new AUnknownType(pattern.getLocation(),false),false);
		case SET:
			return new ASetType(pattern.getLocation(), false, new AUnknownType(pattern.getLocation(),false),false, false);
		case STRING:
			return new ASeqSeqType(pattern.getLocation(), false,new ACharBasicType(pattern.getLocation(),false),false);
		case TUPLE:
			ATuplePattern tupplePattern = (ATuplePattern)pattern;
			PTypeList list = new PTypeList();

			for (PPattern p: tupplePattern.getPlist())
			{
				list.add(getPossibleType(p));
			}

			return  list.getType(tupplePattern.getLocation());
		case UNION:
			AUnionPattern unionPattern = (AUnionPattern)pattern;
			PTypeSet set = new PTypeSet();

			set.add(getPossibleType(unionPattern.getLeft()));
			set.add(getPossibleType(unionPattern.getRight()));

			PType s = set.getType(unionPattern.getLocation());
			
			return PTypeAssistant.isUnknown(s) ?
				new ASetType(unionPattern.getLocation(), false, null, new AUnknownType(unionPattern.getLocation(),false), false, false) : s;		
		}
		return null;
	}

}
