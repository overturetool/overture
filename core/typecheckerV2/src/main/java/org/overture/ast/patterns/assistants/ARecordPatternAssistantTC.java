package org.overture.ast.patterns.assistants;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.typecheck.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeCheckerErrors;
import org.overturetool.vdmjV2.typechecker.NameScope;

public class ARecordPatternAssistantTC {

	public static void typeResolve(ARecordPattern pattern,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		if (pattern.getResolved()) return; else { pattern.setResolved(true); }

		try
		{
			PPatternListAssistant.typeResolve(pattern.getPlist(),rootVisitor,question);
			pattern.setType(PTypeAssistant.typeResolve(pattern.getType(),null, rootVisitor,question));
		}
		catch (TypeCheckException e)
		{
			unResolve(pattern);
			throw e;
		}
		
	}

	public static void unResolve(ARecordPattern pattern) {
		PTypeAssistant.unResolve(pattern.getType());
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

	public static List<PDefinition> getDefinitions(ARecordPattern rp,
			PType exptype, NameScope scope) {
		
		List<PDefinition> defs = new Vector<PDefinition>();

		PType type = rp.getType();
		
		if (!PTypeAssistant.isRecord(type))
		{
			TypeCheckerErrors.report(3200, "Mk_ expression is not a record type",rp.getLocation(),rp);
			TypeCheckerErrors.detail("Type", type);
			return defs;
		}

		ARecordInvariantType pattype = PTypeAssistant.getRecord(type);
		PType using = PTypeAssistant.isType(exptype, pattype.getName().getName());

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
	
}
