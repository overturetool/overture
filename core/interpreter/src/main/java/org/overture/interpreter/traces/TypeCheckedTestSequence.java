package org.overture.interpreter.traces;

import java.util.HashMap;
import java.util.Map;

import org.overture.parser.messages.VDMErrorsException;

public class TypeCheckedTestSequence extends TestSequence
{
	Map<CallSequence, VDMErrorsException> tcFailedTests = new HashMap<>();
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TypeCheckedTestSequence(TestSequence tests,
			Map<CallSequence, VDMErrorsException> tcFailedTests)
	{
		this.tcFailedTests = tcFailedTests;
		for (CallSequence callSequence : tests)
		{
			if (!tcFailedTests.containsKey(callSequence))
			{
				this.add(callSequence);
			}
		}
	}

	public TestSequence getTests()
	{
		TestSequence tests = new TestSequence();
		tests.addAll(this);
		tests.addAll(tcFailedTests.keySet());
		return tests;
	}

	public boolean isTypeCorrect(CallSequence callSequence)
	{
		return !tcFailedTests.containsKey(callSequence);
	}

	public VDMErrorsException getTypeCheckError(CallSequence callSequence)
	{
		return tcFailedTests.get(callSequence);
	}
}
