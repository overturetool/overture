//package org.overture.typechecker.tests.framework;
//
//import java.io.File;
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.util.List;
//
//import junit.framework.TestCase;
//
//import org.overturetool.vdmj.Release;
//import org.overturetool.vdmj.Settings;
//import org.overturetool.vdmj.lex.Dialect;
//import org.overturetool.vdmj.lex.LexException; 
//import org.overturetool.vdmj.lex.LexTokenReader;
//import org.overturetool.vdmj.syntax.ParserException;
//import org.overturetool.vdmj.syntax.SyntaxReader;
//
//public abstract class BaseParserTestCase<T extends SyntaxReader> extends
//		TestCase
//{
//	File file;
//	String name;
//	String content;
//	
//
//	public BaseParserTestCase()
//	{
//		super("test");
//	}
//
//	public BaseParserTestCase(File file)
//	{
//		super("test");
//		this.file = file;
//		this.content = file.getName();
//	}
//
//	public BaseParserTestCase(String name, String content)
//	{
//		super("test");
//		this.content = content;
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
//	public void test() throws ParserException, LexException
//	{
//		if (file != null)
//		{
//			internal(new LexTokenReader(file, Settings.dialect));
//		} else if (content != null)
//		{
//			internal(new LexTokenReader(content, Settings.dialect));
//		}
//	}
//
//	protected abstract T getReader(LexTokenReader ltr);
//
//	protected abstract Object read(T reader) throws ParserException,
//			LexException;
//
//	protected abstract String getReaderTypeName();
//
//	@Override
//	protected void setUp() throws Exception
//	{
//		Settings.dialect = Dialect.VDM_PP;
//		Settings.release = Release.VDM_10;
//	}
//
//	protected void internal(LexTokenReader ltr) throws ParserException,
//			LexException
//	{
//		T reader = null;
//		Object result = null;
//		String errorMessages = "";
//		try
//		{
//			reader = getReader(ltr);
//			result = read(reader);
//
//			if (reader != null && reader.getErrorCount() > 0)
//			{
//				// perrs += reader.getErrorCount();
//				StringWriter s = new StringWriter();
//				reader.printErrors(new PrintWriter(s));//new PrintWriter(System.out));
//				errorMessages ="\n"+s.toString()+"\n";
//				System.out.println(s.toString());
//			}
//			assertEquals(errorMessages,0,reader.getErrorCount());
//
//			if (reader != null && reader.getWarningCount() > 0)
//			{
//				// pwarn += reader.getWarningCount();
////				reader.printWarnings(new PrintWriter(System.out));
//			}
//		} finally
//		{
//			if(!hasRunBefore())
//			{
//				setHasRunBefore( true);
//				System.out.println("============================================================================================================");
//				
//				System.out.println("|");
//				System.out.println("|\t\t"+getReaderTypeName()+"s");
////				System.out.println("|");
//				System.out.println("|___________________________________________________________________________________________________________");
//				
//			}
//			System.out.println(pad("Parsed " + getReaderTypeName(),20) +" - "+pad(getReturnName(result),35)+ ": "+
//					pad(result+"",35).replace('\n', ' ')+" from \""+ (content+"").replace('\n', ' ') + "\""  );
//			System.out.flush();
//		}
//	}
//
//
//	protected abstract void setHasRunBefore(boolean b);
//
//	protected abstract boolean hasRunBefore();
//
//	@SuppressWarnings("rawtypes")
//	private String getReturnName(Object result)
//	{
//		if(result == null)
//		{
//			return "null";
//		}
//		String name = result.getClass().getSimpleName();
//		if(result instanceof List)
//		{
//			try
//			{
//				name+="<"+((List)result).get(0).getClass().getSimpleName()+">";
//			} catch (Exception e)
//			{
//			}
//		}
//		return name;
//	}
//
//	public static String pad(String text, int length)
//	{
//		if (text == null)
//		{
//			text = "null";
//		}
//		while (text.length() < length)
//		{
//			text += " ";
//		}
//		return text;
//	}
//}
