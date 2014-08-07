package org.overture.ct.ctruntime.tests;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.overture.ct.ctruntime.tests.util.TraceReductionInfo;
import org.overture.interpreter.traces.TraceReductionType;

public class CtRandomReductionTestCase extends CtTestCaseBase
{
	protected Map<String, TraceReductionInfo> testReductionInfo;
	protected static final int SEED = 999;
	
	public CtRandomReductionTestCase()
	{
		super();
		init();
	}
	
	public CtRandomReductionTestCase(File file)
	{
		super(file);
		init();
	}
	
	private void init()
	{
		this.testReductionInfo  = new HashMap<String, TraceReductionInfo>();
		
		//Filter the test "OpRepeated10Times" using 15% random reduction etc.
		this.testReductionInfo.put("OpRepeatedTenTimes", new TraceReductionInfo(0.15F, TraceReductionType.RANDOM, SEED));
		this.testReductionInfo.put("ThreeConcurrentOpCalls", new TraceReductionInfo(0.30F, TraceReductionType.RANDOM, SEED));
		this.testReductionInfo.put("ThreeAlternativeOpCalls", new TraceReductionInfo(0.1F, TraceReductionType.RANDOM, SEED));
		this.testReductionInfo.put("SingleOpCall", new TraceReductionInfo(1.0F, TraceReductionType.RANDOM, SEED));
		this.testReductionInfo.put("FourConcurrentOpCalls", new TraceReductionInfo(0.5F, TraceReductionType.RANDOM, SEED));
		this.testReductionInfo.put("FiveConcurrentOpCalls", new TraceReductionInfo(0.9F, TraceReductionType.RANDOM, SEED));
	}

	@Override
	public String[] getArgs(String traceName, File traceFolder,
			File specFileWithExt)
	{
		TraceReductionInfo reductionInfo = testReductionInfo.get(content);
		
		Assert.assertTrue("Could not find reduction info for test: "
				+ content, reductionInfo != null);
		
		return testHelper.buildArgs(traceName, PORT, traceFolder, specFileWithExt, reductionInfo);
	}
}
