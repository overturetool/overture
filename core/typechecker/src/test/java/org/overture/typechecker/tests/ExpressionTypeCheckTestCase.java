package org.overture.typechecker.tests;

import java.util.Vector;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.types.PType;
import org.overture.typecheck.Environment;
import org.overture.typecheck.FlatCheckedEnvironment;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.visitors.TypeCheckVisitor;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.typechecker.NameScope;


public class ExpressionTypeCheckTestCase extends BasicTypeCheckTestCase
{

	public void test1() throws ParserException, LexException
	{
		expressionTc("2+3");
		expressionTc("hd []");
		expressionTc("dom {1|->2}");
	}
	
	private void expressionTc(String expressionString) throws ParserException, LexException
	{
		System.out.flush();
		System.err.flush();
		
		PExp exp = parse(ParserType.Expression, expressionString);
		System.out.println(exp);
		
		Environment env =
			new FlatCheckedEnvironment(new Vector<PDefinition>(), NameScope.NAMESANDSTATE);
		TypeCheckVisitor tc = new TypeCheckVisitor();
		PType type = exp.apply(tc, new TypeCheckInfo(env));
		if(type!=null){
		System.out.println("Type of \""+exp+"\" is: "+type);
		}else
		{
			System.err.println("Type of \""+exp+"\" is: "+type);
		}
		System.out.flush();
		System.err.flush();
	}
}
