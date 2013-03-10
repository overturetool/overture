

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.overture.ast.types.ABracketType;
import org.overture.ast.types.PType;
import org.overture.vdmj.Release;
import org.overture.vdmj.Settings;
import org.overture.vdmj.lex.Dialect;
import org.overture.vdmj.lex.LexException;
import org.overture.vdmj.lex.LexTokenReader;
import org.overture.vdmj.syntax.BindReader;
import org.overture.vdmj.syntax.ClassReader;
import org.overture.vdmj.syntax.ExpressionReader;
import org.overture.vdmj.syntax.ModuleReader;
import org.overture.vdmj.syntax.ParserException;
import org.overture.vdmj.syntax.PatternReader;
import org.overture.vdmj.syntax.StatementReader;
import org.overture.vdmj.syntax.SyntaxReader;
import org.overture.vdmj.syntax.TypeReader;

public abstract class BasicTypeCheckTestCase extends TestCase
{
	public enum ParserType
	{
		Expression, Expressions, Module, Class, Pattern, Type, Statement, Bind
	}

	public BasicTypeCheckTestCase(String string)
	{
		super(string);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;
	}

	public <T> T parse(ParserType type, File file) throws ParserException,
			LexException
	{
		return internal(type, new LexTokenReader(file, Settings.dialect));
	}

	public <T> T parse(ParserType type, String content) throws ParserException,
			LexException
	{
		return internal(type, new LexTokenReader(content, Settings.dialect));
	}

	private <T> T internal(ParserType type, LexTokenReader ltr)
			throws ParserException, LexException
	{
		SyntaxReader reader = null;
		T result = null;
		String errorMessages = "";
		try
		{
			reader = getReader(type, ltr);
			result = read(type, reader);

			if (reader != null && reader.getErrorCount() > 0)
			{
				// perrs += reader.getErrorCount();
				StringWriter s = new StringWriter();
				reader.printErrors(new PrintWriter(s));// new PrintWriter(System.out));
				errorMessages = "\n" + s.toString() + "\n";
				System.out.println(s.toString());
			}
			assertEquals(errorMessages, 0, reader.getErrorCount());

			if (reader != null && reader.getWarningCount() > 0)
			{
				// pwarn += reader.getWarningCount();
				// reader.printWarnings(new PrintWriter(System.out));
			}

			return result;
		} finally
		{
			// if (!hasRunBefore())
			// {
			// setHasRunBefore(true);
			// System.out.println("============================================================================================================");
			//
			// System.out.println("|");
			// System.out.println("|\t\t" + getReaderTypeName() + "s");
			// // System.out.println("|");
			// System.out.println("|___________________________________________________________________________________________________________");
			//
			// }
			// System.out.println(pad("Parsed " + getReaderTypeName(), 20) + " - "
			// + pad(getReturnName(result), 35) + ": "
			// + pad(result + "", 35).replace('\n', ' ') + " from \""
			// + (content + "").replace('\n', ' ') + "\"");
			// System.out.flush();
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T read(ParserType type, SyntaxReader reader)
			throws ParserException, LexException
	{
		switch (type)
		{
			case Class:
				return (T) ((ClassReader) reader).readClasses();
			case Expression:
				return (T) ((ExpressionReader) reader).readExpression();
			case Expressions:
				return (T) ((ExpressionReader) reader).readExpressionList();
			case Module:
				return (T) ((ModuleReader) reader).readModules();
			case Pattern:
				return (T) ((PatternReader) reader).readPatternList();
			case Statement:
				return (T) ((StatementReader) reader).readStatement();
			case Type:
				return (T) ((TypeReader) reader).readType();
			case Bind:
				return (T) ((BindReader) reader).readBindList();

		}
		return null;
	}

	private SyntaxReader getReader(ParserType type, LexTokenReader ltr)
	{
		switch (type)
		{
			case Class:
				return new ClassReader(ltr);
			case Expression:
				return new ExpressionReader(ltr);
			case Module:
				return new ModuleReader(ltr);
			case Pattern:
				return new PatternReader(ltr);
			case Statement:
				return new StatementReader(ltr);
			case Type:
				return new TypeReader(ltr);
			case Bind:
				return new BindReader(ltr);

		}
		return null;
	}

	protected PType getResultType(String expectedTypeString)
			throws ParserException, LexException
	{
		if (expectedTypeString != null && !expectedTypeString.trim().isEmpty())
		{
			PType expectedType = parse(ParserType.Type, expectedTypeString);
			expectedType = removeBrackets(expectedType);
			return expectedType;
		}
		return null;
	}

	private PType removeBrackets(PType type)
	{
		if (type instanceof ABracketType)
		{
			return removeBrackets(((ABracketType) type).getType());
		}
		return type;
	}
}
