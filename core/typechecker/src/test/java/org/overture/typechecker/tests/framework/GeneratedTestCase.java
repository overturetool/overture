//package org.overture.typechecker.tests.framework;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.util.Vector;
//
//import org.overture.ast.definitions.PDefinition;
//import org.overture.ast.node.Node;
//import org.overture.ast.types.PType;
//import org.overture.typecheck.Environment;
//import org.overture.typecheck.FlatCheckedEnvironment;
//import org.overture.typecheck.TypeCheckInfo;
//import org.overture.typecheck.TypeChecker;
//import org.overture.typecheck.TypeComparator;
//import org.overture.typecheck.visitors.TypeCheckVisitor;
//import org.overturetool.vdmj.lex.LexException;
//import org.overturetool.vdmj.syntax.ParserException;
//import org.overturetool.vdmj.typechecker.NameScope;
//
//public class GeneratedTestCase extends BasicTypeCheckTestCase
//{
//	File file;
//	String name;
//	String content;
//	String expectedType;
//	ParserType parserType;
//	private boolean showWarnings;
//	private boolean generateResultOutput = false;
//
//	public GeneratedTestCase()
//	{
//		super("test");
//	}
//
//	public GeneratedTestCase(ParserType type, File file)
//	{
//		super("test");
//		this.parserType = type;
//		this.file = file;
//		this.content = file.getName();
//		this.expectedType = null;
//	}
//
//	public GeneratedTestCase(ParserType type, String name, File file, String content,
//			String expectedType)
//	{
//		super("test");
//		this.parserType = type;
//		this.file = file;
//		this.content = content;
//		this.expectedType = expectedType;
//		this.name = name;
//	}
//
//	@Override
//	public String getName()
//	{
//		if (name != null)
//		{
//			return name;
//		} else if (file != null)
//		{
//			String name = file.getName();
//			if (name.contains("."))
//			{
//				return name.substring(0, name.indexOf("."));
//			}
//			return file.getName();
//		}
//		return "Generic Base Test";
//	}
//
//	@Override
//	protected void setUp() throws Exception
//	{
//		super.setUp();
//		TypeChecker.clearErrors();
//	}
//
//	public void test() throws ParserException, LexException
//	{
//		if (content != null)
//		{
//			expressionTc(content, expectedType);
//		}
//	}
//
//	private void expressionTc(String expressionString, String expectedTypeString)
//			throws ParserException, LexException
//	{
//if(DEBUG){
//		System.out.flush();
//		System.err.flush();
//}
//		Node exp = parse(parserType, expressionString);
//		PType expectedType = getResultType(expectedTypeString);
//if(DEBUG){
//		System.out.println(exp.toString().replace('\n', ' ').replace('\r', ' '));
//}
//		Environment env = new FlatCheckedEnvironment(new Vector<PDefinition>(), NameScope.NAMESANDSTATE);
//		TypeCheckVisitor tc = new TypeCheckVisitor();
//		PType type = exp.apply(tc, new TypeCheckInfo(env));
//
//		//
//
//		String errorMessages = null;
//		if (TypeChecker.getErrorCount() > 0)
//		{
//			// perrs += reader.getErrorCount();
//			StringWriter s = new StringWriter();
//			TypeChecker.printErrors(new PrintWriter(s));// new PrintWriter(System.out));
//			errorMessages = "\n" + s.toString() + "\n";
//if(DEBUG){	
//		System.out.println(s.toString());}
//		}
//
//		assertEquals(errorMessages, 0, TypeChecker.getErrorCount());
//
//		if (showWarnings && TypeChecker.getWarningCount() > 0)
//		{
//			// perrs += reader.getErrorCount();
//			StringWriter s = new StringWriter();
//			TypeChecker.printWarnings(new PrintWriter(s));// new PrintWriter(System.out));
//			// String warningMessages = "\n" + s.toString() + "\n";
//if(DEBUG){
//			System.out.println(s.toString());}
//		}
//		//
//
//if(DEBUG){
//		if (expectedType != null)
//		{
//			assertEquals("Type of \n"
//					+ exp.toString().replace('\n', ' ').replace('\r', ' ')
//					+ " \nis: " + type + " \nexpected: " + expectedType + "\n", true, TypeComparator.compatible(expectedType, type));
//		}
//
//		if (type != null)
//		{
//			System.out.println("Type of \""
//					+ exp.toString().replace('\n', ' ').replace('\r', ' ')
//					+ "\" is: " + type);
//		} else
//		{
//			System.err.println("Type of \""
//					+ exp.toString().replace('\n', ' ').replace('\r', ' ')
//					+ "\" is: " + type);
//		}
//		System.out.flush();
//		System.err.flush();
//}
//		if (generateResultOutput )
//		{
//			writeTestWithResult(type, expressionString);
//		}
//	}
//
//	private void writeTestWithResult(PType type, String test)
//	{
//		File outputFile = new File(file.getAbsolutePath()+ "_generated");
//		FileWriter outFile;
//		String typeName = "Error";
//		if(type != null)
//		{
//			typeName = type.toString();
//		}
//		try
//		{
//			outFile = new FileWriter(outputFile, true);
//			PrintWriter out = new PrintWriter(outFile);
//			out.println(pad(typeName,20 )+"$" + test);
//			out.close();
//		} catch (IOException e)
//		{
//			e.printStackTrace();
//		}
//
//	}
//	
//	private static String pad(String text, int length)
//	{
//		while (text.length() < length)
//		{
//			text += " ";
//		}
//		return text;
//	}
//}
