package org.overture.modelcheckers.probsolver;

import de.be4.classicalb.core.parser.analysis.ASTPrinter;
import de.prob.animator.domainobjects.ClassicalB;


public class InspectBAst {
	
	public static void main(String[] args){
		test();
	}
	
	
	public static void test() {
		//ClassicalB f = 	new ClassicalB("a <: B");
		//System.out.println(f.getAst());
		
		//f = new ClassicalB("[1,2,[3,4],5]");
		//System.out.println(f.getAst());
		
		//f = new ClassicalB("{x,y | x:1..5 & y:1..6}");
		//System.out.println(f.getAst());
		ClassicalB f = new ClassicalB("3|->2");
		System.out.println(f.getAst());
		f.getAst().apply(new ASTPrinter(System.out));
	}
}
