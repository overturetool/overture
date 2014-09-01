package org.overture.interpreter.tests;

import org.junit.Test;
import org.overture.ast.factory.AstFactory;
import org.overture.interpreter.traces.RepeatTraceNode;
import org.overture.interpreter.traces.StatementTraceNode;
import org.overture.interpreter.traces.TraceNode;

public class RepeatTraceNodeTest
{
	private TraceNode repeat = new StatementTraceNode(AstFactory.newASkipStm(null));

	@Test
	public void testRepeatTraceNode1t3()
	{
		RepeatTraceNode n = new RepeatTraceNode(repeat, 1, 3);
		System.out.println(n.getTests().size());
		System.out.println(n.size());
//		System.out.println(n.size2());
		System.out.println(n.getTests());
		System.out.println("Test 1: " +n.getTests().get(1));
		System.out.println("Test 1: " +n.get(1));
	}
	
	@Test
	public void testRepeatTraceNode3()
	{
		RepeatTraceNode n = new RepeatTraceNode(repeat, 3, 3);
		System.out.println(n.getTests().size());
		System.out.println(n.size());
//		System.out.println(n.size2());
		System.out.println(n.getTests());
		System.out.println("Test 0: " +n.getTests().get(0));
		System.out.println("Test 0: " +n.get(0));
	}
}
