package org.overture.ast.patterns.assistants;

import java.util.ArrayList;
import java.util.List;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.node.NodeList;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.AExpressionPattern;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ASeqPattern;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.AUnionPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.typechecker.NameScope;

public class PPatternAssistant {

	public static List<PDefinition> getDefinitions(PPattern rp,
			PType ptype, NameScope scope) {		
		switch (rp.kindPPattern()) {		
			case IDENTIFIER:
				if(rp instanceof AIdentifierPattern)
				{
					AIdentifierPattern idPattern = (AIdentifierPattern) rp;					
					List<PDefinition> defs = new ArrayList<PDefinition>();
					defs.add(new ALocalDefinition(idPattern.getLocation(), idPattern.getName(), scope, false, null, null, ptype,false));
					return defs;
				}
				break;
			default:
				System.out.println("HelperPattern : getDefinitions not implemented");
				break;
		}

		return null;
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
				ARecordPatternAssistant.typeResolve((ARecordPattern)pattern,rootVisitor,question);
			}
			break;
		case SEQ:
			if(pattern instanceof ASeqPattern)
			{
				ASeqPatternAssistant.typeResolve((ASeqPattern)pattern,rootVisitor,question);
			}
			break;			
		case SET:
			if(pattern instanceof ASetPattern)
			{
				ASetPatternAssistant.typeResolve((ASetPattern)pattern,rootVisitor,question);
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
				AUnionPatternAssistant.typeResolve((AUnionPattern)pattern,rootVisitor,question);
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
				ARecordPatternAssistant.unResolve((ARecordPattern)pattern);
			}
			break;
		case SEQ:
			if(pattern instanceof ASeqPattern)
			{
				ASeqPatternAssistant.unResolve((ASeqPattern)pattern);
			}
			break;
		case SET:
			if(pattern instanceof ASetPattern)
			{
				ASetPatternAssistant.unResolve((ASetPattern)pattern);
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
				AUnionPatternAssistant.unResolve((AUnionPattern)pattern);
			}
			break;		
		default:
			pattern.setResolved(false);
		}
		
	}

	public static void typeResolve(List<PPattern> plist,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {

		for (PPattern pPattern : plist) {
			typeResolve(pPattern, rootVisitor, question);
		}		
	}

	public static void unResolve(List<PPattern> plist) {
		
		for (PPattern pPattern : plist) {
			unResolve(pPattern);
		}	
	}

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
