package org.overture.interpreter.utilities.definition;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.traces.AInstanceTraceDefinition;
import org.overture.ast.definitions.traces.ALetBeStBindingTraceDefinition;
import org.overture.ast.definitions.traces.ALetDefBindingTraceDefinition;
import org.overture.ast.definitions.traces.ARepeatTraceDefinition;
import org.overture.ast.definitions.traces.PTraceDefinition;
import org.overture.ast.node.INode;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.definition.ALetBeStBindingTraceDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.ALetDefBindingTraceDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.ARepeatTraceDefinitionAssistantInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.traces.TraceNode;

/***************************************
 * 
 * This method expands the trace of a definition term in the tree. 
 * 
 * @author gkanos
 *
 ****************************************/
public class TermTraceExpander extends QuestionAnswerAdaptor<Context, TraceNode>
{
	protected IInterpreterAssistantFactory af;
	
	public TermTraceExpander(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public TraceNode caseAInstanceTraceDefinition(
			AInstanceTraceDefinition term, Context ctxt)
			throws AnalysisException
	{
		assert false : "this one is not in Nicks tree";
		return null;
	}
	
	@Override
	public TraceNode caseALetBeStBindingTraceDefinition(
			ALetBeStBindingTraceDefinition term, Context ctxt)
			throws AnalysisException
	{
		return ALetBeStBindingTraceDefinitionAssistantInterpreter.expand(term, ctxt);
	}
	
	@Override
	public TraceNode caseALetDefBindingTraceDefinition(
			ALetDefBindingTraceDefinition term, Context ctxt)
			throws AnalysisException
	{
		return ALetDefBindingTraceDefinitionAssistantInterpreter.expand(term, ctxt);
	}
	
	@Override
	public TraceNode caseARepeatTraceDefinition(ARepeatTraceDefinition term,
			Context ctxt) throws AnalysisException
	{
		return ARepeatTraceDefinitionAssistantInterpreter.expand(term, ctxt);
	}
	
	@Override
	public TraceNode defaultPTraceDefinition(PTraceDefinition term,
			Context ctxt) throws AnalysisException
	{
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
