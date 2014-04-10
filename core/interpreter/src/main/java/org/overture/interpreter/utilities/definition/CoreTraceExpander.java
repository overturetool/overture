package org.overture.interpreter.utilities.definition;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.traces.AApplyExpressionTraceCoreDefinition;
import org.overture.ast.definitions.traces.ABracketedExpressionTraceCoreDefinition;
import org.overture.ast.definitions.traces.AConcurrentExpressionTraceCoreDefinition;
import org.overture.ast.definitions.traces.PTraceCoreDefinition;
import org.overture.ast.node.INode;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.definition.AApplyExpressionTraceCoreDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.ABracketedExpressionTraceCoreDefinitionAssitantInterpreter;
import org.overture.interpreter.assistant.definition.AConcurrentExpressionTraceCoreDefinitionAssistantInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.traces.TraceNode;

/***************************************
 * 
 * This method expands the trace of a core element in the tree. 
 * 
 * @author gkanos
 *
 ****************************************/

public class CoreTraceExpander extends QuestionAnswerAdaptor<Context, TraceNode>
{
	protected IInterpreterAssistantFactory af;
	
	public CoreTraceExpander(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public TraceNode caseAApplyExpressionTraceCoreDefinition(
			AApplyExpressionTraceCoreDefinition core, Context ctxt)
			throws AnalysisException
	{
		return AApplyExpressionTraceCoreDefinitionAssistantInterpreter.expand(core, ctxt);
	}
	
	@Override
	public TraceNode caseABracketedExpressionTraceCoreDefinition(
			ABracketedExpressionTraceCoreDefinition core, Context ctxt)
			throws AnalysisException
	{
		return ABracketedExpressionTraceCoreDefinitionAssitantInterpreter.expand(core, ctxt);
	}
	
	@Override
	public TraceNode caseAConcurrentExpressionTraceCoreDefinition(
			AConcurrentExpressionTraceCoreDefinition core, Context ctxt)
			throws AnalysisException
	{
		return AConcurrentExpressionTraceCoreDefinitionAssistantInterpreter.expand(core, ctxt);
	}
	
	@Override
	public TraceNode defaultPTraceCoreDefinition(PTraceCoreDefinition node,
			Context question) throws AnalysisException
	{
		assert false : "Should not happen";
		return null;
	}
	
	@Override
	public TraceNode createNewReturnValue(INode node, Context question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TraceNode createNewReturnValue(Object node, Context question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}
}
