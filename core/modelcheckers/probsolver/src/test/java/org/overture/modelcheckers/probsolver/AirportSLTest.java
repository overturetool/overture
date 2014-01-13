package org.overture.modelcheckers.probsolver;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.modelcheckers.probsolver.ProbSolverUtil.SolverException;

public class AirportSLTest extends ProbConverterTestBase
{

	public AirportSLTest()
	{
		super(new File("src/test/resources/modules/AirportNat.vdmsl".replace('/', File.separatorChar)));
	}

	@Test
	public void testInit() throws IOException, AnalysisException, SolverException
	{
		testMethod("Init");
	}

	@Test
	public void testGivePermission() throws IOException, AnalysisException, SolverException
	{
		testMethod("GivePermission");
	}

	@Test
	public void testRecordLanding() throws IOException, AnalysisException, SolverException
	{
		testMethod("RecordLanding");
	}

	@Test
	public void testRecordTakeOff() throws IOException, AnalysisException, SolverException
	{
		testMethod("RecordTakeOff");
	}

	@Test
	public void testNumberWaiting() throws IOException, AnalysisException, SolverException
	{
		testMethod("NumberWaiting");
	}

}
