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
import org.overture.ast.patterns.ANilPattern;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;
import org.overture.runtime.Environment;
import org.overture.typecheck.TypeCheckInfo;

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
					defs.add(new ALocalDefinition(idPattern.getLocation(), idPattern.getName(), scope, false, null, null, ptype));
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
			break;
		case SET:
			break;
		case STRING:
			break;
		case TUPLE:
			break;
		case UNION:
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
		default:
			pattern.setResolved(false);
		}
		
	}

	public static void typeResolve(NodeList<PPattern> plist,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {

		for (PPattern pPattern : plist) {
			typeResolve(pPattern, rootVisitor, question);
		}		
	}

}
