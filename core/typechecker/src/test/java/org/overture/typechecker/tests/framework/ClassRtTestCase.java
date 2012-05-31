package org.overture.typechecker.tests.framework;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.overture.typecheck.TypeChecker;
import org.overture.typechecker.tests.OvertureTestHelper;
import org.overture.typechecker.tests.framework.BasicTypeCheckTestCase.ParserType;
import org.overturetool.test.framework.ResultTestCase;
import org.overturetool.test.framework.results.Result;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;
import org.overturetool.vdmj.syntax.ParserException;

public class ClassRtTestCase extends ResultTestCase
{
public final static boolean DEBUG = false;
	public static final String tcHeader = "-- TCErrors:";
	public static final Boolean printOks = false;

	File file;
	String name;
	String content;
	String expectedType;
	ParserType parserType;	
	List<VDMError> errors = new Vector<VDMError>();
	List<VDMWarning> warnings = new Vector<VDMWarning>();

	public ClassRtTestCase()
	{
		super();

	}

	public ClassRtTestCase(File file)
	{
		super(file);
		this.parserType = ParserType.Module;
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
		Settings.dialect = Dialect.VDM_RT;
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

	private void moduleTc(String class_) throws ParserException, LexException,
			IOException
	{
		@SuppressWarnings("rawtypes")
		Result result = new OvertureTestHelper().typeCheckRt(file);
		
		compareResults(result, file.getAbsolutePath());

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
