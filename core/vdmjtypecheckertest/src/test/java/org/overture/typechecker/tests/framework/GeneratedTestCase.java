package org.overture.typechecker.tests.framework;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatCheckedEnvironment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeChecker;
import org.overturetool.vdmj.typechecker.TypeComparator;
import org.overturetool.vdmj.types.Type;

public class GeneratedTestCase extends BasicTypeCheckTestCase
{
	File file;
	String name;
	String content;
	String expectedType;
	ParserType parserType;
	private boolean showWarnings;
	private boolean generateResultOutput = true;

	public GeneratedTestCase()
	{
		super("test");
	}

	public GeneratedTestCase(File file)
	{
		super("test");
		this.parserType = ParserType.Module;
		this.file = file;
		this.content = file.getName();
		this.expectedType = null;
	}
		

	public GeneratedTestCase(ParserType type, String name, File file, String content,
			String expectedType)
	{
		super("test");
		this.parserType = type;
		this.file = file;
		this.content = content;
		this.expectedType = expectedType;
		this.name = name;
	}

	@Override
	public String getName()
	{
		if (name != null)
		{
			return name;
		} else if (file != null)
		{
			String name = file.getName();
			if (name.contains("."))
			{
				return name.substring(0, name.indexOf("."));
			}
			return file.getName();
		}
		return "Generic Base Test";
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TypeChecker.clearErrors();
	}

	public void test() throws ParserException, LexException
	{
		if (content != null)
		{
			expressionTc(content, expectedType);
		}
	}

	private void expressionTc(String expressionString, String expectedTypeString)
			throws ParserException, LexException
	{
if(DEBUG){
		System.out.flush();
		System.err.flush();
}
		Expression exp = parse(parserType, expressionString);
		Type expectedType = getResultType(expectedTypeString);

		System.out.println(exp.toString().replace('\n', ' ').replace('\r', ' '));

		Environment env = new FlatCheckedEnvironment(new DefinitionList(), NameScope.NAMESANDSTATE);
//		TypeCheckVisitor tc = new TypeCheckVisitor();
		Type type = exp.typeCheck(env, null, null);//exp.apply(tc, new TypeCheckInfo(env));

		//

		String errorMessages = null;
		if (TypeChecker.getErrorCount() > 0)
		{
			// perrs += reader.getErrorCount();
			StringWriter s = new StringWriter();
			TypeChecker.printErrors(new PrintWriter(s));// new PrintWriter(System.out));
			errorMessages = "\n" + s.toString() + "\n";
if(DEBUG){
			System.out.println(s.toString());
}
		}

		assertEquals(errorMessages, 0, TypeChecker.getErrorCount());

		if (showWarnings && TypeChecker.getWarningCount() > 0)
		{
			// perrs += reader.getErrorCount();
			StringWriter s = new StringWriter();
			TypeChecker.printWarnings(new PrintWriter(s));// new PrintWriter(System.out));
			// String warningMessages = "\n" + s.toString() + "\n";
if(DEBUG){
			System.out.println(s.toString());
}
		}
		//

		if (expectedType != null)
		{
			assertEquals("Type of \n"
					+ exp.toString().replace('\n', ' ').replace('\r', ' ')
					+ " \nis: " + type + " \nexpected: " + expectedType + "\n", true, TypeComparator.compatible(expectedType, type));
		}

		if (type != null)
		{
if(DEBUG){
			System.out.println("Type of \""
					+ exp.toString().replace('\n', ' ').replace('\r', ' ')
					+ "\" is: " + type);}
		} else
		{if(DEBUG){
			System.err.println("Type of \""
					+ exp.toString().replace('\n', ' ').replace('\r', ' ')
					+ "\" is: " + type);}
		}
if(DEBUG){
		System.out.flush();
		System.err.flush();}

		if (generateResultOutput )
		{
			writeTestWithResult(type, expressionString);
		}
	}

	private void writeTestWithResult(Type type, String test)
	{
		File outputFile = new File(file.getAbsolutePath()+ "_generated");
		FileWriter outFile;
		String typeName = "Error";
		if(type != null)
		{
			typeName = type.toString();
		}
		try
		{
			outFile = new FileWriter(outputFile, true);
			PrintWriter out = new PrintWriter(outFile);
			out.println(pad(typeName,20 )+"$" + test);
			out.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}
	
	private static String pad(String text, int length)
	{
		while (text.length() < length)
		{
			text += " ";
		}
		return text;
	}
}
