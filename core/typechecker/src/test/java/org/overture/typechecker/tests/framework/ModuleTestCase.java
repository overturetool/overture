package org.overture.typechecker.tests.framework;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.overture.typecheck.TypeChecker;
import org.overture.typechecker.tests.OvertureTestHelper;
import org.overturetool.test.framework.results.Result;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;
import org.overturetool.vdmj.syntax.ParserException;

public class ModuleTestCase extends TypeCheckTestCase
{

	public static final String tcHeader = "-- TCErrors:";
	public static final Boolean printOks = false;

	File file;
	String name;
	String content;
	String expectedType;
	List<VDMError> errors = new Vector<VDMError>();
	List<VDMWarning> warnings = new Vector<VDMWarning>();

	public ModuleTestCase()
	{
		super();

	}

	public ModuleTestCase(File file)
	{
		super(file);
		this.file = file;
		this.content = file.getName();
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

		Result<Boolean> result = new OvertureTestHelper().typeCheckSl(file);
		
		compareResults(result, file.getAbsolutePath());

	}

}
