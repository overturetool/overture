package org.overture.typechecker.tests;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.overture.ast.modules.AModuleModules;
import org.overture.typecheck.ModuleTypeChecker;
import org.overture.typecheck.TypeChecker;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.syntax.ModuleReader;
import org.overturetool.vdmj.syntax.ParserException;

import junit.framework.TestCase;

public class ModuleSlTypeCheckTest extends TestCase
{
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;
	}
	public void test01() throws ParserException, LexException
	{
		String root = "src\\test\\resources\\test1";
		List<AModuleModules> modules = parse(new File(root));
		
		ModuleTypeChecker mtc = new ModuleTypeChecker(modules);
		mtc.typeCheck();
		
		String errorMessages = null;
		if (mtc != null && TypeChecker.getErrorCount() > 0)
		{
			// perrs += reader.getErrorCount();
			StringWriter s = new StringWriter();
			TypeChecker.printErrors(new PrintWriter(s));//new PrintWriter(System.out));
			errorMessages ="\n"+s.toString()+"\n";
			System.out.println(s.toString());
		}
		
		assertEquals(errorMessages,0,TypeChecker.getErrorCount());
		
		if (mtc != null && TypeChecker.getWarningCount() > 0)
		{
			// perrs += reader.getErrorCount();
			StringWriter s = new StringWriter();
			TypeChecker.printWarnings(new PrintWriter(s));//new PrintWriter(System.out));
			String warningMessages ="\n"+s.toString()+"\n";
			System.out.println(s.toString());
		}
	}

	private List<AModuleModules> parse(File file) throws ParserException,
			LexException
	{
		// if (file != null)
		// {
		return internal(new LexTokenReader(file, Settings.dialect));
		// } else if (content != null)
		// {
		// internal(new LexTokenReader(content, Settings.dialect));
		// }
	}

	protected List<AModuleModules> internal(LexTokenReader ltr)
			throws ParserException, LexException
	{
		ModuleReader reader = null;
		List<AModuleModules> result = null;
		String errorMessages = "";
		try
		{
			reader = getReader(ltr);
			result = read(reader);

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

	private List<AModuleModules> read(ModuleReader reader)
	{
		return reader.readModules();
	}

	private ModuleReader getReader(LexTokenReader ltr)
	{
		return new ModuleReader(ltr);
	}
}
