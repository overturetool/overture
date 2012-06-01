package org.overture.parser.tests.framework;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.overturetool.test.framework.ResultTestCase;
import org.overturetool.test.framework.results.IMessage;
import org.overturetool.test.framework.results.Message;
import org.overturetool.test.framework.results.Result;
import org.overturetool.test.util.XmlResultReaderWritter;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.syntax.SyntaxReader;
import org.xml.sax.SAXException;

public abstract class BaseParserTestCase<T extends SyntaxReader> extends
		ResultTestCase
{

public final static boolean DEBUG = true;
	File file;
	String name;
	String content;
	

	public BaseParserTestCase()
	{
		//super("skip");
		super();
	}
	

	public BaseParserTestCase(File file)
	{
		//super("test");
		super(file);
		this.file = file;
		this.content = file.getName();
	}

	public BaseParserTestCase(String name, String content)
	{
		//super("test");
		super();
		this.content = content;
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

	public void test() throws ParserException, LexException
	{
		if (file != null)
		{
			internal(new LexTokenReader(file, Settings.dialect));
		} else if (content != null)
		{
			internal(new LexTokenReader(content, Settings.dialect));
		}
	}

	protected abstract T getReader(LexTokenReader ltr);

	protected abstract Object read(T reader) throws ParserException,
			LexException;

	protected abstract String getReaderTypeName();

	@Override
	protected void setUp() throws Exception
	{
		Settings.dialect = Dialect.VDM_PP;
		Settings.release = Release.VDM_10;
	}

	protected void internal(LexTokenReader ltr) throws ParserException,
			LexException
	{
		T reader = null;
		Object result = null;
		String errorMessages = "";
		try
		{
			reader = getReader(ltr);
			result = read(reader);

			System.out.println();
			
			List<IMessage> warnings = new Vector<IMessage>();
			List<IMessage> errors = new Vector<IMessage>();
			
			collectParserErrorsAndWarnings(reader, errors, warnings);
			Result<Object> resultFinal = new Result<Object>(result, warnings, errors,null);
			
			compareResults(resultFinal, file.getAbsolutePath());
			//compareResults(result, file.getAbsolutePath());
			
				//Result<String> resultFile = xmlreaderWritter.loadFromXml();
				
				
			
			

			
		} finally
		{
			if(!hasRunBefore())
			{
				setHasRunBefore( true);
				if(DEBUG){
				System.out.println("============================================================================================================");
				
				System.out.println("|");
				System.out.println("|\t\t"+getReaderTypeName()+"s");
//				System.out.println("|");
				System.out.println("|___________________________________________________________________________________________________________");
				
			
			System.out.println(pad("Parsed " + getReaderTypeName(),20) +" - "+pad(getReturnName(result),35)+ ": "+
					pad(result+"",35).replace('\n', ' ')+" from \""+ (content+"").replace('\n', ' ') + "\""  );
			System.out.flush();}}
		}
	}


	protected abstract void setHasRunBefore(boolean b);

	protected abstract boolean hasRunBefore();

	@SuppressWarnings("rawtypes")
	private String getReturnName(Object result)
	{
		if(result == null)
		{
			return "null";
		}
		String name = result.getClass().getSimpleName();
		if(result instanceof List)
		{
			try
			{
				name+="<"+((List)result).get(0).getClass().getSimpleName()+">";
			} catch (Exception e)
			{
			}
		}
		return name;
	}

	public static String pad(String text, int length)
	{
		if (text == null)
		{
			text = "null";
		}
		while (text.length() < length)
		{
			text += " ";
		}
		return text;
	}
	
	public void skip(){};
	
	@Override
	protected File createResultFile(String filename) {
		return new File(filename + ".result");
	}

	@Override
	protected File getResultFile(String filename) {
		return new File(filename + ".result");
	}
	
	private static void collectParserErrorsAndWarnings(SyntaxReader reader,
			List<IMessage> errors, List<IMessage> warnings)
	{
		if (reader != null && reader.getErrorCount() > 0)
		{
			for (VDMError msg : reader.getErrors())
			{
				errors.add(new Message(msg.location.file.getName(), msg.number, msg.location.startLine, msg.location.startPos, msg.message));
			}
		}

		if (reader != null && reader.getWarningCount() > 0)
		{
			for (VDMWarning msg : reader.getWarnings())
			{
				warnings.add(new Message(msg.location.file.getName(), msg.number, msg.location.startLine, msg.location.startPos, msg.message));
			}
		}
	}
}
