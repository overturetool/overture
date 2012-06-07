package org.overture.pog.tests.framework;

import java.io.File;

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.BUSClassDefinition;
import org.overturetool.vdmj.definitions.CPUClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.syntax.ParserException;


public class VdmjClassRtPoTestCase extends VdmjClassPpPoTestCase
{
	public VdmjClassRtPoTestCase() {
		super();
	}

	public VdmjClassRtPoTestCase(File file) {
		super(file);
	}
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect =Dialect.VDM_RT;
	}
	
	@Override
	protected void prepareClassesForTc(ClassList classes)
	{
	try
	{
		classes.add(new CPUClassDefinition());
		classes.add(new BUSClassDefinition());
	} catch (ParserException e)
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (LexException e)
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	
}
