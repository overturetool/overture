/*
 * #%~
 * Combinatorial Testing Runtime
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
package org.overture.ct.ctruntime.tests;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ct.ctruntime.utils.CtHelper;
import org.overture.ct.ctruntime.utils.TraceReductionInfo;
import org.overture.interpreter.traces.TraceReductionType;
import org.overture.interpreter.traces.util.Pair;
import org.overture.test.framework.Properties;

@RunWith(value = Parameterized.class)
public class CtRandomReductionTestCase extends CtTestCaseBase
{
	//The name of the test input folder
	private static final String TEST_INPUT_FOLDER = "random_reduction_sl_specs";
	
	private static final String RESOURCES = ("src/test/resources/" + TEST_INPUT_FOLDER).replace('/', File.separatorChar);
	
	//The trace to be tested is assumed to have this trace name
	private static final String TRACE_NAME = "T1";
	
	//Use a fixed seed to make the trace expansion output deterministic
	protected static final int SEED = 999;
	
	final TraceReductionInfo reductionInfo;

	@Parameters(name = "{0}")
	public static Collection<Object[]> getData()
	{
		Properties.recordTestResults = false;
		
		List<Pair<String, TraceReductionInfo>> testReductionInfo2 = new Vector<Pair<String, TraceReductionInfo>>();
		testReductionInfo2.add(new Pair<String, TraceReductionInfo>("OpRepeatedTenTimes", new TraceReductionInfo(0.15F, TraceReductionType.RANDOM, SEED)));
		testReductionInfo2.add(new Pair<String, TraceReductionInfo>("ThreeConcurrentOpCalls", new TraceReductionInfo(0.30F, TraceReductionType.RANDOM, SEED)));
		testReductionInfo2.add(new Pair<String, TraceReductionInfo>("ThreeAlternativeOpCalls", new TraceReductionInfo(0.1F, TraceReductionType.RANDOM, SEED)));
		testReductionInfo2.add(new Pair<String, TraceReductionInfo>("SingleOpCall", new TraceReductionInfo(1.0F, TraceReductionType.RANDOM, SEED)));
		testReductionInfo2.add(new Pair<String, TraceReductionInfo>("FourConcurrentOpCalls", new TraceReductionInfo(0.5F, TraceReductionType.RANDOM, SEED)));
		testReductionInfo2.add(new Pair<String, TraceReductionInfo>("FiveConcurrentOpCalls", new TraceReductionInfo(0.9F, TraceReductionType.RANDOM, SEED)));
		testReductionInfo2.add(new Pair<String, TraceReductionInfo>("PaperCaseStudy", new TraceReductionInfo(0.01F, TraceReductionType.SHAPES_NOVARS, SEED)));
		testReductionInfo2.add(new Pair<String, TraceReductionInfo>("PaperCaseStudy", new TraceReductionInfo(0.01F, TraceReductionType.SHAPES_VARNAMES, SEED)));
		testReductionInfo2.add(new Pair<String, TraceReductionInfo>("PaperCaseStudy", new TraceReductionInfo(0.01F, TraceReductionType.SHAPES_VARVALUES, SEED)));

		Collection<Object[]> tests = new Vector<Object[]>();
		CtHelper testHelper = new CtHelper();

		File root = new File(RESOURCES);

		for (Pair<String, TraceReductionInfo> entry : testReductionInfo2)
		{
			String traceName = entry.first;
			File traceFolder = new File((TRACE_OUTPUT_FOLDER
					+ TEST_INPUT_FOLDER + '/' + traceName).replace('/', File.separatorChar));
			File specFile = new File(root, traceName + ".vdmsl");
			tests.add(new Object[] {
					traceName + " " + entry.second,
					specFile,
					traceFolder,
					testHelper.buildArgs(TRACE_NAME, PORT, traceFolder, specFile, entry.second),
					entry.second });
		}

		return tests;
	}

	public CtRandomReductionTestCase(String name, File file, File traceFolder,
			String[] args, TraceReductionInfo reductionInfo)
	{
		super(file, traceFolder, args);
		this.reductionInfo = reductionInfo;
	}
	
	@Override
	protected File getResultFile(String filename)
	{
		int index = filename.lastIndexOf('.');
		String resultFileName = consResultFileName(filename, index);
		
		return super.getResultFile(resultFileName);
	}

	@Override
	protected File createResultFile(String filename)
	{
		int index = filename.lastIndexOf('.');
		String resultFileName = consResultFileName(filename, index);
		
		return super.createResultFile(resultFileName);
	}
	
	private String consResultFileName(String filename, int index)
	{
		return filename.substring(0, index) + "-"
				+ reductionInfo.toString().replace(',', '_').replace('.', '_')
				+ filename.substring(index);
	}
}
