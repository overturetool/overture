package org.overture.prettyprintertest.tests.framework;

import java.io.File;

import org.overture.config.Settings;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.syntax.ParserException;
import org.overture.parser.syntax.SyntaxReader;
import org.overturetool.test.framework.ResultTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class BasePrettyPrinterTestCase<T extends SyntaxReader,R> extends ResultTestCase<String>
{

	public BasePrettyPrinterTestCase()
	{
		super();
	}
	

	public BasePrettyPrinterTestCase(File file)
	{
		super(file);
	}

	public BasePrettyPrinterTestCase(File rootSource,String name, String content)
	{
		super(rootSource,name,content); 
	}
	
	public void encondeResult(String result, Document doc, Element resultElement)
	{

	}

	public String decodeResult(Node node)
	{
		return null;
	}

	@Override
	protected boolean assertEqualResults(String expected, String actual)
	{
		return false;
	}

	@Override
	protected File createResultFile(String filename)
	{
		return null;
	}

	@Override
	protected File getResultFile(String filename)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void test() throws Exception
	{
		if (file != null)
		{
			internal(new LexTokenReader(file, Settings.dialect));
		} else if (content != null)
		{
			internal(new LexTokenReader(content, Settings.dialect));
		}

	}

	protected void internal(LexTokenReader ltr) throws ParserException,
			LexException
	{
//		T reader = null;
//		R result = null;
//		
//			reader = getReader(ltr);
//			result = read(reader);
//
//			System.out.println();
//
//			List<IMessage> warnings = new Vector<IMessage>();
//			List<IMessage> errors = new Vector<IMessage>();

			//collectParserErrorsAndWarnings(reader, errors, warnings);
			//Result<R> resultFinal = new Result<R>(result, warnings, errors);

			//compareResults(resultFinal, file.getAbsolutePath());
	}

	protected abstract T getReader(LexTokenReader ltr);
	
	protected abstract R read(T reader) throws ParserException,LexException;
}
