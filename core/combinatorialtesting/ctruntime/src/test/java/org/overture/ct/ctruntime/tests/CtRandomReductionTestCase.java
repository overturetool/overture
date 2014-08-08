package org.overture.ct.ctruntime.tests;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ct.ctruntime.tests.util.CtTestHelper;
import org.overture.ct.ctruntime.tests.util.TraceReductionInfo;
import org.overture.interpreter.traces.TraceReductionType;
import org.overture.interpreter.traces.util.Pair;
import org.overture.test.framework.Properties;

@RunWith(value = Parameterized.class)
public class CtRandomReductionTestCase extends CtTestCaseBase
{
	protected static final int SEED = 999;
	final TraceReductionInfo reductionInfo;

	@Parameters(name = "{0}")
	public static Collection<Object[]> getData()
	{
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
		CtTestHelper testHelper = new CtTestHelper();

		File root = new File("src/test/resources/random_reduction_sl_specs".replace('/', File.separatorChar));

		for (Pair<String, TraceReductionInfo> entry : testReductionInfo2)
		{
			String traceName = entry.first;
			File traceFolder = new File((TRACE_OUTPUT_FOLDER
					+ "random_reduction_sl_specs/" + traceName).replace('/', File.separatorChar));
			File specFile = new File(root, traceName + ".vdmsl");
			tests.add(new Object[] {
					traceName + " " + entry.second,
					specFile,
					traceFolder,
					testHelper.buildArgs("T1", PORT, traceFolder, specFile, entry.second),
					entry.second });
		}
		Properties.recordTestResults = false;

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
		
		return super.getResultFile(filename.substring(0,index)+"-"+reductionInfo.toString().replace(',' ,'_').replace('.', '_')+filename.substring(index));
	}
	
	@Override
	protected File createResultFile(String filename)
	{
		int index = filename.lastIndexOf('.');
		return super.createResultFile(filename.substring(0,index)+"-"+reductionInfo.toString().replace(',' ,'_').replace('.', '_')+filename.substring(index));
	}

}
