package org.overture.typechecker.tests.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;

import org.overture.ast.modules.AModuleModules;
import org.overture.typecheck.ModuleTypeChecker;
import org.overture.typecheck.TypeChecker;
import org.overture.typechecker.tests.OvertureTestHelper;
import org.overture.typechecker.tests.framework.BasicTypeCheckTestCase.ParserType;
import org.overture.typechecker.tests.framework.TCStruct.Type;
import org.overturetool.test.framework.ResultTestCase;
import org.overturetool.test.framework.results.Result;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;
import org.overturetool.vdmj.syntax.ModuleReader;
import org.overturetool.vdmj.syntax.ParserException;

public class ModuleTestCase extends ResultTestCase
{

	public static final String tcHeader = "-- TCErrors:";
	public static final Boolean printOks = false;

	File file;
	String name;
	String content;
	String expectedType;
	ParserType parserType;
	private TCStructList tcHeaderList = null;
	private boolean isParseOk = true;
	List<VDMError> errors = new Vector<VDMError>();
	List<VDMWarning> warnings = new Vector<VDMWarning>();

	public ModuleTestCase()
	{
		super();

	}

	public ModuleTestCase(File file)
	{
		super(file);
		this.parserType = ParserType.Module;
		this.file = file;
		this.content = file.getName();
		this.tcHeaderList = new TCStructList();
	}

	@Override
	public String getName()
	{
		return this.content;
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;
		TypeChecker.clearErrors();
	}

	public void test() throws ParserException, LexException, IOException
	{
		if (content != null)
		{
			moduleTc(content);
		}
	}

	private void moduleTc(String module) throws ParserException, LexException,
			IOException
	{

		Result result = new OvertureTestHelper().typeCheckSl(file);
		
		compareResults(result, file.getAbsolutePath());
		

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
				reader.printErrors(new PrintWriter(s));// new
														// PrintWriter(System.out));
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

	

	@Override
	protected File createResultFile(String filename) {
		return new File(filename + ".result");
	}

	@Override
	protected File getResultFile(String filename) {
		return new File(filename + ".result");
	}
}
