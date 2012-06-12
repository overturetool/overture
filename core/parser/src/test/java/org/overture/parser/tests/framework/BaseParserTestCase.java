package org.overture.parser.tests.framework;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.messages.VDMError;
import org.overture.parser.messages.VDMWarning;
import org.overture.parser.syntax.ParserException;
import org.overture.parser.syntax.SyntaxReader;
import org.overturetool.test.framework.ResultTestCase;
import org.overturetool.test.framework.results.IMessage;
import org.overturetool.test.framework.results.Message;
import org.overturetool.test.framework.results.Result;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class BaseParserTestCase<T extends SyntaxReader,R> extends
		ResultTestCase<R>
{

public final static boolean DEBUG = true;
	

	public BaseParserTestCase()
	{
		super();
	}
	

	public BaseParserTestCase(File file)
	{
		super(file);
	}

	public BaseParserTestCase(File rootSource,String name, String content)
	{
		super(rootSource,name,content); 
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

	protected abstract R read(T reader) throws ParserException,
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
		R result = null;
		try
		{
			reader = getReader(ltr);
			result = read(reader);

			System.out.println();
			
			List<IMessage> warnings = new Vector<IMessage>();
			List<IMessage> errors = new Vector<IMessage>();
			
			collectParserErrorsAndWarnings(reader, errors, warnings);
			Result<R> resultFinal = new Result<R>(result, warnings, errors);
			
			compareResults(resultFinal, file.getAbsolutePath());
			
		} finally
		{
			if(!hasRunBefore())
			{
				setHasRunBefore( true);
				if(DEBUG){
				System.out.println("============================================================================================================");
				
				System.out.println("|");
				System.out.println("|\t\t"+getReaderTypeName()+"s");
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
		if(mode==ContentModed.String)
		{
			String tmp = getName().substring(name.indexOf('_')+1);
			tmp =File.separatorChar+ ""+tmp.substring(0,tmp.indexOf('_'));
			return new File(filename+"_results"+tmp + ".result");
		}
		return new File(filename + ".result");
	}

	@Override
	protected File getResultFile(String filename) {
		if(mode==ContentModed.String)
		{
			String tmp = getName().substring(name.indexOf('_')+1);
			tmp =File.separatorChar+ ""+tmp.substring(0,tmp.indexOf('_'));
			return new File(filename+"_results"+tmp + ".result");
		}
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


	public void encondeResult(Object result, Document doc, Element resultElement) {
		
	}


	public R decodeResult(Node node) {
		return null;
	}


	@Override
	protected boolean assertEqualResults(R expected, R actual) {
		return true;
	}
}
