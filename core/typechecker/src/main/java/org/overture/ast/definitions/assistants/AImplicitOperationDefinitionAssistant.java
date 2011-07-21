package org.overture.ast.definitions.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.assistants.APatternTypePairAssistant;
import org.overture.ast.statements.ASubclassResponsibilityStm;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.APatternListTypePairAssistant;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class AImplicitOperationDefinitionAssistant {

	public static PDefinition findName(AImplicitOperationDefinition d,
			LexNameToken sought, NameScope scope) {
		if (PDefinitionAssistant.findNameBaseCase(d, sought, scope) != null)
		{
			return d;
		}

		PDefinition predef = d.getPredef();
		if (predef != null && PDefinitionAssistant.findName(predef, sought, scope) != null)
		{
			return predef;
		}

		PDefinition postdef = d.getPostdef();
		if (postdef != null && PDefinitionAssistant.findName(postdef,sought, scope) != null)
		{
			return postdef;
		}

		return null;
	}

	public static List<PDefinition> getDefinitions(
			AImplicitOperationDefinition d) {
		List<PDefinition> defs = new Vector<PDefinition>();
		defs.add(d);

		if (d.getPredef() != null)
		{
			defs.add(d.getPredef());
		}

		if (d.getPostdef() != null)
		{
			defs.add(d.getPostdef());
		}

		return defs;
	}

	public static LexNameList getVariableNames(AImplicitOperationDefinition d) {
		return new LexNameList(d.getName());
	}

	public static void typeResolve(AImplicitOperationDefinition d,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		d.setType(d.getType().apply(rootVisitor, question));

		if (d.getResult() != null)
		{
			APatternTypePairAssistant.typeResolve(d.getResult(), rootVisitor, question);
		}

		if (question.env.isVDMPP())
		{
			d.getName().setTypeQualifier(d.getType().getParameters());

			if (d.getBody() instanceof ASubclassResponsibilityStm)
			{
				d.getClassDefinition().setIsAbstract(true);
			}
		}

		if (d.getPrecondition() != null)
		{
			PDefinitionAssistant.typeResolve(d.getPredef(), rootVisitor, question);
		}

		if (d.getPostcondition() != null)
		{
			PDefinitionAssistant.typeResolve(d.getPostdef(), rootVisitor, question);
		}

		for (APatternListTypePair ptp: d.getParameterPatterns())
		{
			APatternListTypePairAssistant.typeResolve(ptp, rootVisitor, question);
		}
		
	}

}
