package org.overture.modelcheckers.probsolver;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;

import de.be4.classicalb.core.parser.exceptions.BException;

public class AirportPPTest extends ProbConverterTestBase
{

	public AirportPPTest()
	{
		super(new File("src/test/resources/classes/Airport.vdmpp".replace('/', File.separatorChar)));
	}
	
	@Before
	public void setup() throws BException
	{
		Settings.dialect = Dialect.VDM_PP;
		Settings.release = Release.VDM_10;
	}

	@Test
	public void testInit() throws IOException
	{
		testMethod("Init");
	}

	@Test
	public void testGivePermission() throws IOException
	{
		testMethod("GivePermission");
	}

	@Test
	public void testRecordLanding() throws IOException
	{
		testMethod("RecordLanding");
	}

	@Test
	public void testRecordTakeOff() throws IOException
	{
		testMethod("RecordTakeOff");
	}

	@Test
	public void testNumberWaiting() throws IOException
	{
		testMethod("NumberWaiting");
	}

}
