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

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.ct.ctruntime.utils.CtHelper.CtTestData;
import org.overture.ct.ctruntime.utils.ReductionTestData;
import org.overture.ct.ctruntime.utils.TraceReductionInfo;
import org.overture.ct.ctruntime.utils.TraceResult;
import org.overture.ct.ctruntime.utils.TraceResultReader;
import org.overture.interpreter.traces.TraceReductionType;
import org.overture.test.framework.Properties;

@RunWith(value = Parameterized.class)
public class CtRandomReductionSlTestCase extends CtTestCaseBase
{
	//The name of the test input folder
	private static final String TEST_INPUT_FOLDER = "random_reduction_sl_specs";
	
	private static final String RESOURCES = ("src/test/resources/" + TEST_INPUT_FOLDER).replace('/', File.separatorChar);
	
	//The trace to be tested is assumed to have this trace name
	private static final String TRACE_NAME = "T1";
	
	//Use a fixed seed to make the trace expansion output deterministic
	protected static final int SEED = 999;
	
	final TraceReductionInfo reductionInfo;
    private final int expectedResultSize;

    @Parameters(name = "{0}")
	public static Collection<Object[]> getData()
	{
	    List<ReductionTestData> testReduction = new Vector<>();
		testReduction.add(ReductionTestData.create("OpRepeatedTenTimes", new TraceReductionInfo(0.15F, TraceReductionType.RANDOM, SEED),2));
		testReduction.add(ReductionTestData.create("ThreeAlternativeOpCalls", new TraceReductionInfo(0.1F, TraceReductionType.RANDOM, SEED),1));
        testReduction.add(ReductionTestData.create("ThreeConcurrentOpCalls", new TraceReductionInfo(0.30F, TraceReductionType.RANDOM, SEED),2));
        testReduction.add(ReductionTestData.create("SingleOpCall", new TraceReductionInfo(1.0F, TraceReductionType.RANDOM, SEED),1));
		testReduction.add(ReductionTestData.create("FourConcurrentOpCalls", new TraceReductionInfo(0.5F, TraceReductionType.RANDOM, SEED),12));
		testReduction.add(ReductionTestData.create("FiveConcurrentOpCalls", new TraceReductionInfo(0.9F, TraceReductionType.RANDOM, SEED),108));
		testReduction.add(ReductionTestData.create("PaperCaseStudy", new TraceReductionInfo(0.01F, TraceReductionType.SHAPES_NOVARS, SEED),2));
		testReduction.add(ReductionTestData.create("PaperCaseStudy", new TraceReductionInfo(0.01F, TraceReductionType.SHAPES_VARNAMES, SEED),3));
		testReduction.add(ReductionTestData.create("PaperCaseStudy", new TraceReductionInfo(0.01F, TraceReductionType.SHAPES_VARVALUES, SEED),21));

		Collection<Object[]> tests = new Vector<Object[]>();
		

		File root = new File(RESOURCES);

		for (ReductionTestData entry : testReduction)
		{
			String traceName = entry.name();
			File traceFolder = new File((TRACE_OUTPUT_FOLDER
					+ TEST_INPUT_FOLDER + '/' + traceName).replace('/', File.separatorChar));
			File specFile = new File(root, traceName + ".vdmsl");
			tests.add(new Object[] {
					traceName + " " + entry.reductionInfo(),
					specFile,
					traceFolder,
					new CtTestData( TRACE_NAME,  traceFolder, specFile, entry.reductionInfo()),
					entry.reductionInfo(),
                    entry.expectedResultSize()
                    });
		}

		return tests;
	}
	
	public CtRandomReductionSlTestCase(String name, File file, File traceFolder,
			CtTestData args, TraceReductionInfo reductionInfo, int expectedResultSize)
	{
		super(file, traceFolder, args);
		this.reductionInfo = reductionInfo;
        this.expectedResultSize = expectedResultSize;
	}

	@Test
    @Override
	public void test() throws Exception
	{
		if (file == null)
		{
			return;
		}

		try
		{
			configureResultGeneration();

			File actualResultsFile = computeActualResults(TRACE_NAME);

			if (Properties.recordTestResults)
			{
				try
				{
					File resultFile = createResultFile(file.getAbsolutePath());
					resultFile.getParentFile().mkdirs();

					// Overwrite result file
					FileUtils.copyFile(actualResultsFile, resultFile);

				} catch (Exception e)
				{
					Assert.fail("The produced results could not be stored: "
							+ e.getMessage());
				}
			} else
			{
				TraceResultReader reader = new TraceResultReader();
				List<TraceResult> actualResults = reader.read(actualResultsFile);

                int actualSize =0;

                for (TraceResult td : actualResults){
                    actualSize += td.tests.size();
                }

				Assert.assertEquals(expectedResultSize, actualSize);

			}
		} finally
		{
			unconfigureResultGeneration();
		}

	}

	
	@Override
	public void setUp() throws Exception
	{
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;
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

	@Override
	protected String getPropertyId()
	{
		return "sl.random";
	}
}
