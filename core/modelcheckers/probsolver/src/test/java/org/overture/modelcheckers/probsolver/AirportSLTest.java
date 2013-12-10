package org.overture.modelcheckers.probsolver;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class AirportSLTest extends ProbConverterTestBase
{

	public AirportSLTest()
	{
		super(new File("src/test/resources/modules/AirportNat.vdmsl".replace('/', File.separatorChar)));
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
