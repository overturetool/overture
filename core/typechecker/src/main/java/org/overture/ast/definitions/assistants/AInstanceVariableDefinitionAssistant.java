package org.overture.ast.definitions.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.runtime.Environment;
import org.overture.runtime.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class AInstanceVariableDefinitionAssistant {

	public static PDefinition findName(AInstanceVariableDefinition d, LexNameToken sought,
			NameScope scope) {
		
		PDefinition found = PDefinitionAssistant.findNameBaseCase(d, sought, scope);
		if (found != null) return found;
		return scope.matches(NameScope.OLDSTATE) &&
				d.getOldname().equals(sought) ? d : null;
	}

	public static List<PDefinition> getDefinitions(AInstanceVariableDefinition d) {
		List<PDefinition> res = new Vector<PDefinition>();
		res.add(d);
		return res;
	}
	
	public static LexNameList getVariableNames(AInstanceVariableDefinition d) {
		return new LexNameList(d.getName());
	}

	

	public static void typeResolve(AInstanceVariableDefinition d,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		try
		{
			d.setType(PTypeAssistant.typeResolve(d.getType(), null, rootVisitor, question));
		}
		catch (TypeCheckException e)
		{
			PTypeAssistant.unResolve(d.getType());
			throw e;
		}
		
	}

}
