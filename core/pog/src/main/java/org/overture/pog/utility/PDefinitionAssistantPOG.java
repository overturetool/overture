package org.overture.pog.utility;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.definitions.PDefinition;
import org.overture.pog.contexts.PONameContext;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IProofObligationList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;

public class PDefinitionAssistantPOG extends PDefinitionAssistantTC implements IAstAssistant
{

	public PDefinitionAssistantPOG(ITypeCheckerAssistantFactory af)
	{
		super(af);
	}

	public IProofObligationList getProofObligations(
			LinkedList<? extends PDefinition> defs,
			QuestionAnswerAdaptor<IPOContextStack, ? extends IProofObligationList> pogVisitor,
			IPOContextStack ctxt) throws AnalysisException
	{
		IProofObligationList obligations = new ProofObligationList();

		for (PDefinition d : defs)
		{
			ctxt.push(new PONameContext(getVariableNames(d)));
			obligations.addAll(d.apply(pogVisitor, ctxt));
			ctxt.pop();
		}

		return obligations;
	}

}
