/*
 * #%~
 * Integration of the ProB Solver for VDM
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
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
