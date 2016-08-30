/*******************************************************************************
 *
 *	Copyright (C) 2008, 2009 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overture.codegen.runtime.traces;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.runtime.Utils;

public abstract class TraceNode
{
	private static final int ENCLOSING_MODULE_ID = -1;
	private static final Object NOT_AVAILABLE = new Object();

	private CallSequence traceVars;

	public TraceNode()
	{
		this.traceVars = new CallSequence();
	}

	public void addVarFirst(TraceVariable var)
	{
		this.traceVars.add(0, var);
	}

	public CallSequence getVars()
	{
		CallSequence c = new CallSequence();
		c.addAll(traceVars);
		return c;
	}

	@Override
	abstract public String toString();

	abstract public TestSequence getTests();

	// SL
	public static void executeTests(TraceNode trace, TestAccumulator acc,
			Store store)
	{
		executeTests(trace, null, acc, store);
	}

	// PP
	public static void executeTests(TraceNode trace, Class<?> instanceType,
			TestAccumulator acc, Store store)
	{
		try
		{
			if (instanceType != null)
			{
				store.register(ENCLOSING_MODULE_ID, instanceType.newInstance());
			}

			int testNo = 1;

			TestSequence tests = trace.getTests();

			tests.size();

			for (CallSequence test : tests)
			{
				/**
				 * We should reset the state before running the test in case the trace variable statements have modified
				 * it, e.g. let a = opWithSideEffects() in ....
				 */
				store.reset();
				List<String> callStms = new LinkedList<String>();
				List<Object> callStmResults = new LinkedList<Object>();

				/*
				 * TODO: Type check missing here If the type check fails we would also have to do filtering
				 */
				if (test.getFilter() > 0)
				{
					acc.registerTest(new TraceTest(testNo, test.toString(), "", Verdict.SKIPPED));
				} else
				{
					int callStmIdx = 0;
					for (; callStmIdx < test.size(); callStmIdx++)
					{
						Statement stm = test.get(callStmIdx);

						if (!(stm instanceof CallStatement))
						{
							continue;
						}

						CallStatement callStm = (CallStatement) stm;
						try
						{
							callStms.add(callStm.toString());

							if (callStm instanceof CallStatementPp)
							{
								((CallStatementPp) callStm).setInstance(store.getValue(ENCLOSING_MODULE_ID));
							}

							// TODO: To be done. Consider where the right place is to check for this.
							if (!callStm.isTypeCorrect()
									|| !callStm.meetsPreCond())
							{
								// Inconclusive
								callStmResults.add(NOT_AVAILABLE);
								callStmResults.add(Verdict.INCONCLUSIVE);
								tests.filter(callStmResults, test, testNo);
								acc.registerTest(new TraceTest(testNo, test.toString(), "", Verdict.INCONCLUSIVE));
								break;
							}

							Object result = callStm.execute();
							callStmResults.add(result);

							if (callStmIdx == test.size() - 1)
							{
								callStmResults.add(Verdict.PASSED);
								acc.registerTest(new TraceTest(testNo, getResultStr(callStms, "; "), getResultStr(callStmResults.subList(0, callStmResults.size()
										- 1), " ; "), Verdict.PASSED));
							}

						} catch (RuntimeException | AssertionError e)
						{
							for (int p = callStmIdx + 1; p < test.size(); p++)
							{
								callStms.add(callStms.toString());
							}

							for (; callStmIdx < test.size(); callStmIdx++)
							{
								if (e.getMessage() != null)
								{
									callStmResults.add(e.getClass().getSimpleName()
											+ ": " + e.getMessage());
								} else
								{
									// Happens for null pointer exceptions
									callStmResults.add(e.getClass().getSimpleName());
								}
							}

							Verdict verdict = Verdict.FAILED;

							callStmResults.add(verdict);

							tests.filter(callStmResults, test, testNo);
							acc.registerTest(new TraceTest(testNo, getResultStr(callStms, "; "), getResultStr(callStmResults.subList(0, callStmResults.size()
									- 1), " ; "), verdict));
						}

					}
				}

				callStms.clear();
				callStmResults.clear();

				testNo++;
			}

		} catch (InstantiationException e)
		{
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	private static String getResultStr(List<? extends Object> results,
			String sep)
	{
		StringBuilder sb = new StringBuilder();

		if (!results.isEmpty())
		{
			sb.append(toStr(results.get(0)));
		}

		for (int i = 1; i < results.size(); i++)
		{
			sb.append(sep + toStr(results.get(i)));
		}

		return sb.toString();
	}

	public static void showResults(List<TraceTest> results)
	{
		for (TraceTest testResult : results)
		{
			System.out.println(testResult.toString());
		}
	}

	private static String toStr(Object obj)
	{
		if (obj instanceof String)
		{
			return obj.toString();
		} else
		{
			return Utils.toString(obj);
		}
	}
}
