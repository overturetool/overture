package org.overture.interpreter.assistant.definition;

import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.traces.ATraceDefinitionTerm;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.traces.ReducedTestSequence;
import org.overture.interpreter.traces.SequenceTraceNode;
import org.overture.interpreter.traces.TestSequence;
import org.overture.interpreter.traces.TraceReductionType;

public class ANamedTraceDefinitionAssistantInterpreter implements IAstAssistant
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ANamedTraceDefinitionAssistantInterpreter(
			IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	public TestSequence getTests(ANamedTraceDefinition tracedef, Context ctxt)
			throws Exception
	{
		return getTests(tracedef, ctxt, 1.0F, TraceReductionType.NONE, System.currentTimeMillis());
	}

	public TestSequence getTests(ANamedTraceDefinition tracedef, Context ctxt,
			float subset, TraceReductionType type, long seed) throws Exception
	{
		SequenceTraceNode traces = new SequenceTraceNode();

		for (ATraceDefinitionTerm term : tracedef.getTerms())
		{
			traces.nodes.add(term.apply(af.getTraceExpander(), ctxt));
		}

		TestSequence tests = traces.getTests();

		if (tests.isEmpty())
		{
			throw new Exception("Trace expansion generated no tests");
		}

		if (subset < 1.0)
		{
			tests  = new ReducedTestSequence(tests, subset, type, seed);
		}

		return tests;
	}

}
