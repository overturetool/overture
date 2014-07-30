import java.io.File;
import java.util.List;

import junit.framework.TestCase;

import org.overture.ast.lex.Dialect;
import org.overture.ast.node.INode;
import org.overture.ast.preview.GraphViz.GraphVizException;
import org.overture.ast.preview.Main;
import org.overture.config.Settings;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.syntax.ClassReader;
import org.overture.parser.syntax.DefinitionReader;
import org.overture.parser.syntax.ExpressionReader;
import org.overture.parser.syntax.ParserException;
import org.overture.parser.syntax.StatementReader;

public class Dot extends TestCase
{
	@Override
	protected void setUp() throws Exception
	{
		Settings.dialect = Dialect.VDM_PP;
		Main.dot = new File("dot.exe");
//		Main.filterClassNames.add(Boolean.class.getSimpleName());
		Main.filterClassNames.add(Integer.class.getSimpleName());
		Main.filterClassNames.add("LexLocation");
		Main.filterClassNames.add("VDMToken");
	}

	public void testCallExp() throws GraphVizException, ParserException,
			LexException
	{
		dot("call", readExp("a.op()"));
	}

	public void testCallStm() throws GraphVizException, ParserException,
			LexException
	{
		dot("call-statement", readStatement("a.op()"));
	}
	
	public void testCallProductStm() throws GraphVizException, ParserException,
	LexException
{
dot("call-product-statement", readStatement("pair.#1.op()"));
}
	
	public void testStaticCallStm() throws GraphVizException, ParserException,
	LexException
{
dot("static-call-statement", readStatement("A`op()"));
}

	public void testDeepMapCallStm() throws GraphVizException, ParserException,
	LexException
{
dot("deep-map-call-statement", readStatement("(dcl m_map : map nat to Meter ;     meters(1).device.op())"));
}
	
	
	public void testNameCallExp() throws GraphVizException, ParserException,
			LexException
	{
		dot("name-call", readExp("B`a.op()"));
	}

	public void testFieldExp() throws GraphVizException, ParserException,
			LexException
	{
		dot("field", readExp("a.b"));
	}

	public void testStaticFieldExp() throws GraphVizException, ParserException,
			LexException
	{
		dot("static-field", readExp("A`b"));
	}

	public void testFieldSelectExp() throws GraphVizException, ParserException,
			LexException
	{
		dot("fieldSelect", readExp("a.b.#1"));
	}

	public void testIdentifierExp() throws GraphVizException, ParserException,
			LexException
	{
		dot("identifier", readExp("a"));
	}

	public void testIdentifierAssignmentExp() throws GraphVizException,
			ParserException, LexException
	{
		dot("identifier-assign", readStatement("a:=5"));
	}

	public void testIdentifier2AssignmentExp() throws GraphVizException,
			ParserException, LexException
	{
		dot("identifier-assign2", readStatement("a.b:=5"));
	}

	public void testIdentifier2AssignmentApplyExp() throws GraphVizException,
			ParserException, LexException
	{
		dot("identifier-assign-apply", readStatement("m(1) :=1;"));
	}

	public void testAssignExp() throws GraphVizException, ParserException,
			LexException
	{
		dot("assign", readStatement("a.b.n := val1"));

	}
	
	public void testAssignCallStm() throws GraphVizException, ParserException,
	LexException
{
dot("assign-object-call", readStatement("a.b := o.op()"));

}
	
	public void testCallOjectStm() throws GraphVizException, ParserException,
	LexException
{
dot("object-call", readStatement("o.op()"));

}

	public void testAssignNew() throws GraphVizException, ParserException,
			LexException
	{
		dot("assign-new", readStatement("a := new Test(d,f)"));

	}

	public void testIntTypeDefinition() throws GraphVizException,
			ParserException, LexException
	{
		dot("class-type-int", readDefinition("class A types t = int end A"));
	}

	public void testValueDefinition() throws GraphVizException,
			ParserException, LexException
	{
		dot("class-value-int", readDefinition("class A values v :int = 5 end A"));
	}

	public void testFunctionDefinition() throws GraphVizException,
			ParserException, LexException
	{
		dot("class-function-record", readDefinition("functions fn :int -> R fn(a)==mk_R(a,a) end A"));
	}

	public void testFunctionPostDefinition() throws GraphVizException,
			ParserException, LexException
	{
		dot("class-function-post", readDefinition("functions fn :int -> R fn(a)==mk_R(a,a) post a~=d end A"));
	}

	public void testClassDefinition() throws GraphVizException,
			ParserException, LexException
	{
		dot("class-type-ref", readClasses("class A types public T =int end A class B functions f : T ->int f(a)==1; end B"));
	}

	public void testInstanceVarClassDefinition() throws GraphVizException,
			ParserException, LexException
	{
		dot("class-instancevariable", readClasses("class A instance variables public a : int := 4; end A"));
	}

	public void testOpCallClassDefinition() throws GraphVizException,
			ParserException, LexException
	{
		dot("class-opcall", readClasses("class A operations op:()==>int op()==return 5; op2:()==>() op2()== a:=op(); instance variables public a : int := 4; end A"));
	}

	
	public void testOpCall2ClassDefinition() throws GraphVizException,
	ParserException, LexException
{
dot("class-opcall2", readClasses("class A operations op:int==>int op(a)==return 5+a; op2:()==>() op2()==let b =8 in a:=op()+b; instance variables public a : int := 4; end A"));
}
	
	private void dot(String name, INode n) throws ParserException,
			LexException, GraphVizException
	{
		Main.makeImage(n, "svg", new File("target/" + name
				+ ".svg".replace('/', File.separatorChar)));
	}

	private void dot(String name, List<? extends INode> n)
			throws ParserException, LexException, GraphVizException
	{
		Main.makeImage(n, "svg", new File("target/" + name
				+ ".svg".replace('/', File.separatorChar)));
	}

	private INode readExp(String text) throws ParserException, LexException
	{
		ExpressionReader r = new ExpressionReader(new LexTokenReader(text, Settings.dialect));
		return r.readExpression();
	}

	private INode readStatement(String text) throws ParserException,
			LexException
	{
		StatementReader r = new StatementReader(new LexTokenReader(text, Settings.dialect));
		return r.readStatement();
	}

	private INode readDefinition(String text) throws ParserException,
			LexException
	{
		DefinitionReader r = new DefinitionReader(new LexTokenReader(text, Settings.dialect));
		return r.readDefinitions().get(0);
	}

	private List<? extends INode> readDefinitions(String text)
			throws ParserException, LexException
	{
		DefinitionReader r = new DefinitionReader(new LexTokenReader(text, Settings.dialect));
		return r.readDefinitions();
	}

	private List<? extends INode> readClasses(String text)
			throws ParserException, LexException
	{
		ClassReader r = new ClassReader(new LexTokenReader(text, Settings.dialect));
		return r.readClasses();
	}
}
