package org.overture.ast.definitions.assistants;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.patterns.assistants.PPatternAssistant;
import org.overture.ast.statements.ASubclassResponsibilityStm;
import org.overture.ast.types.PType;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;


public class AExplicitOperationDefinitionAssistant {

	public static List<? extends PDefinition> getParamDefinitions(
			AExplicitOperationDefinition node) {
		
		Set<PDefinition> defs = new HashSet<PDefinition>();
		Iterator<PType> titer = node.getType().getParameters().iterator();

		for (PPattern p:  node.getParameterPatterns())
		{
   			defs.addAll(PPatternAssistant.getDefinitions(p,titer.next(), NameScope.LOCAL));
		}

		return new Vector<PDefinition>(defs);
	}

	public static PDefinition findName(AExplicitOperationDefinition d,
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
			AExplicitOperationDefinition d) {
		
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

	public static LexNameList getVariableNames(AExplicitOperationDefinition d) {
		
		return new LexNameList(d.getName());
	}

	public static void typeResolve(AExplicitOperationDefinition d,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {

		d.setType(d.getType().apply(rootVisitor, question));

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

		for (PPattern p: d.getParameterPatterns())
		{
			PPatternAssistant.typeResolve(p, rootVisitor, question);
		}
		
	}

}
