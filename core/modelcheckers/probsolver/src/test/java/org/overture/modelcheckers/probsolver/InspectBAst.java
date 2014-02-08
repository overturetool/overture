package org.overture.modelcheckers.probsolver;

import de.be4.classicalb.core.parser.analysis.ASTPrinter;
import de.prob.animator.domainobjects.ClassicalB;

public class InspectBAst
{

	public static void main(String[] args)
	{
		test();
	}

	public static void test()
	{
		// ClassicalB f = new ClassicalB("a <: B");
		// System.out.println(f.getAst());

		// f = new ClassicalB("[1,2,[3,4],5]");
		// System.out.println(f.getAst());

		// f = new ClassicalB("{x,y | x:1..5 & y:1..6}");
		// System.out.println(f.getAst());
		ClassicalB f = new ClassicalB("1|->2");
		System.out.println(f.getAst());
		f.getAst().apply(new ASTPrinter(System.out));

		// f = new ClassicalB("{1|->10, 2|->20,3|->30,4|->40}[{2,3}]");
		// f.getAst().apply(new ASTPrinter(System.out));

		// f = new ClassicalB("[1,2,3,4,5](3)");
		// f.getAst().apply(new ASTPrinter(System.out));

		// f = new ClassicalB("POW({1,2,3})");
		// f.getAst().apply(new ASTPrinter(System.out));

		// f = new ClassicalB("({(1 |-> 3), (2|->4)} ; { (3 |-> 5) ,(4|->1)})");
		// f.getAst().apply(new ASTPrinter(System.out));

		// f = new ClassicalB("{(1 |-> 3), (2|->4)}~");
		// f.getAst().apply(new ASTPrinter(System.out));

		// f = new ClassicalB("iterate({(1 |-> 3), (2|->1), 3 |->2}, 3)");
		// f.getAst().apply(new ASTPrinter(System.out));

		// f = new ClassicalB("1..5");
		// f.getAst().apply(new ASTPrinter(System.out));

		// f = new ClassicalB("max({1,2,3})");
		// f.getAst().apply(new ASTPrinter(System.out));

		// f = new ClassicalB("5 mod 3");
		// f.getAst().apply(new ASTPrinter(System.out));

		// f = new ClassicalB("!(n).(n:{1,2,3} => mx>=n)");
		// f.getAst().apply(new ASTPrinter(System.out));

		// f = new ClassicalB("rev([1,2,3])");
		// f.getAst().apply(new ASTPrinter(System.out));

		// f = new ClassicalB("#x.(x:{1,2,3} & x>2)");
		// f.getAst().apply(new ASTPrinter(System.out));

		// f = new ClassicalB("2*2>0 <=> 2>0");
		// f.getAst().apply(new ASTPrinter(System.out));

		// f = new ClassicalB("{ x | x:{1,2,3,4,5} & x > 3 }");
		// f.getAst().apply(new ASTPrinter(System.out));

		// f = new ClassicalB("{1|->2|->3}");
		// f.getAst().apply(new ASTPrinter(System.out));

		// f = new ClassicalB("-7-(-3)*(-7/-3)");
		// f.getAst().apply(new ASTPrinter(System.out));

		// f = new ClassicalB("TRUE");
		// f.getAst().apply(new ASTPrinter(System.out));

		f = new ClassicalB("{1|->3, 3|->2}");
		f.getAst().apply(new ASTPrinter(System.out));
	}

}
