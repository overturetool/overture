package org.overture.modelcheckers.probsolver;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.modelcheckers.probsolver.AbstractProbSolverUtil.SolverException;
import org.overture.modelcheckers.probsolver.visitors.VdmToBConverter;
import org.overture.test.framework.ConditionalIgnoreMethodRule.ConditionalIgnore;
import org.overture.typechecker.assistant.TypeCheckerAssistantFactory;

public class AirportSLTest extends ProbConverterTestBase
{

	public AirportSLTest()
	{
		super(new File("src/test/resources/modules/complete/AirportNat.vdmsl".replace('/', File.separatorChar)), new TypeCheckerAssistantFactory());
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
		VdmToBConverter.USE_INITIAL_FIXED_STATE = false;
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

	@Override
	protected String getPropertyId()
	{
		return "sl.airport";
	}

	@Override
	protected File getStorageLocation()
	{
		return new File(super.getStorageLocation(), this.getClass().getSimpleName());
	}
}
