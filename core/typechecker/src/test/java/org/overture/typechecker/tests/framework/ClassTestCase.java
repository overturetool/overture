package org.overture.typechecker.tests.framework;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.parser.lex.LexException;
import org.overture.parser.messages.VDMError;
import org.overture.parser.messages.VDMWarning;
import org.overture.parser.syntax.ParserException;
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.tests.OvertureTestHelper;
import org.overturetool.test.framework.results.Result;

public class ClassTestCase extends TypeCheckTestCase
{
public final static boolean DEBUG = false;
	public static final String tcHeader = "-- TCErrors:";
	public static final Boolean printOks = false;

	File file;
	String name;
	String content;
	String expectedType;
	List<VDMError> errors = new Vector<VDMError>();
	List<VDMWarning> warnings = new Vector<VDMWarning>();

	public ClassTestCase()
	{
		super();

	}

	public ClassTestCase(File file)
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
		Settings.dialect = Dialect.VDM_PP;
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
		Result<Boolean> result = new OvertureTestHelper().typeCheckPp(file);
		
		compareResults(result, file.getAbsolutePath());

	}

	
	

	

	
	
}
