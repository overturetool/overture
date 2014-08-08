package org.overture.ct.ctruntime.tests;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ct.ctruntime.tests.util.CtTestHelper;
import org.overture.ct.ctruntime.tests.util.TraceReductionInfo;
import org.overture.interpreter.traces.TraceReductionType;

@RunWith(value = Parameterized.class)
public class CtRandomReductionTestCase extends CtTestCaseBase
{
	protected static final int SEED = 999;

	@Parameters(name = "{0}")
	public static Collection<Object[]> getData()
	{
		Map<String, TraceReductionInfo> testReductionInfo = new HashMap<String, TraceReductionInfo>();

		// Filter the test "OpRepeated10Times" using 15% random reduction etc.
		testReductionInfo.put("OpRepeatedTenTimes", new TraceReductionInfo(0.15F, TraceReductionType.RANDOM, SEED));
		testReductionInfo.put("ThreeConcurrentOpCalls", new TraceReductionInfo(0.30F, TraceReductionType.RANDOM, SEED));
		testReductionInfo.put("ThreeAlternativeOpCalls", new TraceReductionInfo(0.1F, TraceReductionType.RANDOM, SEED));
		testReductionInfo.put("SingleOpCall", new TraceReductionInfo(1.0F, TraceReductionType.RANDOM, SEED));
		testReductionInfo.put("FourConcurrentOpCalls", new TraceReductionInfo(0.5F, TraceReductionType.RANDOM, SEED));
		testReductionInfo.put("FiveConcurrentOpCalls", new TraceReductionInfo(0.9F, TraceReductionType.RANDOM, SEED));
		testReductionInfo.put("PaperCaseStudy", new TraceReductionInfo(0.01F, TraceReductionType.SHAPES_NOVARS, SEED));

		Collection<Object[]> tests = new Vector<Object[]>();
		CtTestHelper testHelper = new CtTestHelper();

		File root = new File("src/test/resources/random_reduction_sl_specs".replace('/', File.separatorChar));

		for (Entry<String, TraceReductionInfo> entry : testReductionInfo.entrySet())
		{
			String traceName = entry.getKey();
			File traceFolder = new File((TRACE_OUTPUT_FOLDER+"random_reduction_sl_specs/" + traceName).replace('/', File.separatorChar));
			File specFile = new File(root, traceName + ".vdmsl");
			tests.add(new Object[] {
					traceName + " " + entry.getValue(),
					specFile,
					traceFolder,
					testHelper.buildArgs("T1", PORT, traceFolder, specFile, entry.getValue()) });
		}

		return tests;
	}

	public CtRandomReductionTestCase(String name, File file, File traceFolder,
			String[] args)
	{
		super(file, traceFolder, args);
	}

}
