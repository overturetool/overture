package org.overture.modelcheckers.probsolver;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.modelcheckers.probsolver.ProbSolverUtil.SolverException;
import org.overture.test.framework.ConditionalIgnoreMethodRule.ConditionalIgnore;

public class AirportSLTest extends ProbConverterTestBase
{

	public AirportSLTest()
	{
		super(new File("src/test/resources/modules/AirportNat.vdmsl".replace('/', File.separatorChar)));
	}

	@Test
	@ConditionalIgnore(condition = ProbNotInstalledCondition.class)
	public void testInit() throws IOException, AnalysisException,
			SolverException
	{
		testMethod("Init");
	}

	@Test
	@ConditionalIgnore(condition = ProbNotInstalledCondition.class)
	public void testGivePermission() throws IOException, AnalysisException,
			SolverException
	{
		testMethod("GivePermission");
	}

	@Test
	@ConditionalIgnore(condition = ProbNotInstalledCondition.class)
	public void testRecordLanding() throws IOException, AnalysisException,
			SolverException
	{
		testMethod("RecordLanding");
	}

	@Test
	@ConditionalIgnore(condition = ProbNotInstalledCondition.class)
	public void testRecordTakeOff() throws IOException, AnalysisException,
			SolverException
	{
		testMethod("RecordTakeOff");
	}

	@Test
	@ConditionalIgnore(condition = ProbNotInstalledCondition.class)
	public void testNumberWaiting() throws IOException, AnalysisException,
			SolverException
	{
		testMethod("NumberWaiting");
	}

}
