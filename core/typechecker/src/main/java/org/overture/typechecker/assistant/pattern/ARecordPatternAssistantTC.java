package org.overture.typechecker.assistant.pattern;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckException;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class ARecordPatternAssistantTC {

	public static void typeResolve(ARecordPattern pattern,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException {
		if (pattern.getResolved()) return; else { pattern.setResolved(true); }

		try
		{
			PPatternListAssistantTC.typeResolve(pattern.getPlist(),rootVisitor,question);
			pattern.setType(PTypeAssistantTC.typeResolve(pattern.getType(),null, rootVisitor,question));
		}
		catch (TypeCheckException e)
		{
			unResolve(pattern);
			throw e;
		}
		
	}

	public static void unResolve(ARecordPattern pattern) {
		PTypeAssistantTC.unResolve(pattern.getType());
		pattern.setResolved(false);		
	}

//	public static LexNameList getVariableNames(ARecordPattern pattern) {
//		LexNameList list = new LexNameList();
//
//		for (PPattern p: pattern.getPlist())
//		{
//			list.addAll(PPatternTCAssistant.getVariableNames(p));
//		}
//
//		return list;
//		
//	}

	public static List<PDefinition> getAllDefinitions(ARecordPattern rp,
			PType exptype, NameScope scope) {
		
		List<PDefinition> defs = new Vector<PDefinition>();

		PType type = rp.getType();
		
		if (!PTypeAssistantTC.isRecord(type))
		{
			TypeCheckerErrors.report(3200, "Mk_ expression is not a record type",rp.getLocation(),rp);
			TypeCheckerErrors.detail("Type", type);
			return defs;
		}

		ARecordInvariantType pattype = PTypeAssistantTC.getRecord(type);
		PType using = PTypeAssistantTC.isType(exptype, pattype.getName().getFullName());

		if (using == null || !(using instanceof ARecordInvariantType))
		{
			TypeCheckerErrors.report(3201, "Matching expression is not a compatible record type",rp.getLocation(),rp);
			TypeCheckerErrors.detail2("Pattern type", type, "Expression type", exptype);
			return defs;
		}

		// RecordType usingrec = (RecordType)using;

		if (pattype.getFields().size() != rp.getPlist().size())
		{
			TypeCheckerErrors.report(3202, "Record pattern argument/field count mismatch",rp.getLocation(),rp);
		}
		else
		{
			Iterator<AFieldField> patfi = pattype.getFields().iterator();

    		for (PPattern p: rp.getPlist())
    		{
    			AFieldField pf = patfi.next();
    			// defs.addAll(p.getDefinitions(usingrec.findField(pf.tag).type, scope));
    			defs.addAll(PPatternAssistantTC.getDefinitions(p,pf.getType(), scope));
    		}
		}

		return defs;
	}

	public static PType getPossibleTypes(ARecordPattern pattern) {
		return pattern.getType();
	}

	public static PExp getMatchingExpression(ARecordPattern ptrn) {
		List<PExp> list = new LinkedList<PExp>();

		for (PPattern p : ptrn.getPlist()) {
			list.add(PPatternAssistantTC.getMatchingExpression(p));
		}
		
		LexNameToken tpName = ptrn.getTypename();
		return AstFactory.newAMkTypeExp(tpName.clone(), list);
	}
	
	public static boolean isSimple(ARecordPattern p)
	{
		return PPatternListAssistantTC.isSimple(p.getPlist());
	}
		
}
