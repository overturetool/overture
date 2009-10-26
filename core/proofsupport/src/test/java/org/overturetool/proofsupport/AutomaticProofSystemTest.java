package org.overturetool.proofsupport;

import java.util.ArrayList;
import java.util.List;

import org.overturetool.proofsupport.external_tools.pog.VdmToolsPoProcessor;
import org.overturetool.proofsupport.external_tools.pog.VdmToolsWrapper;
import org.overturetool.proofsupport.test.AutomaticProofSystemTestCase;

public class AutomaticProofSystemTest extends AutomaticProofSystemTestCase {

	public void testDischargeAllPosSet() throws Exception {
		AutomaticProofSystemBatch aps = new AutomaticProofSystemBatch(mosmlDir,
				holDir, new VdmToolsWrapper(vppdeExecutable),
				new VdmToolsPoProcessor());
		List<String> contextFiles = new ArrayList<String>(0);

		String[] expected = new String[] { "PO-1\nExpression:\n(forall s : Set &\nSet`pre_doNothing(s))\nResult: NOT DISCHARGED" };
		String[] actual = aps.dischargeAllPos(setModel, contextFiles);


		assertEquals(1, actual.length);
		assertEquals(expected[0], actual[0]);		
	}

	public void testDischargeAllPosStack() throws Exception {
		AutomaticProofSystemBatch aps = new AutomaticProofSystemBatch(mosmlDir,
				holDir, new VdmToolsWrapper(vppdeExecutable),
				new VdmToolsPoProcessor());
		List<String> contextFiles = new ArrayList<String>(0);
		String[] expected = new String[] { "PO-1\nExpression:\n(forall s : Stack &\ns <> [])\nResult: NOT DISCHARGED",
				"PO-2\nExpression:\n(forall s : Stack &\ns <> [] =>\n s <> [])\nResult: DISCHARGED" };

		String[] actual = aps.dischargeAllPos(stackModel, contextFiles);
		
		assertEquals(2, actual.length);
		assertEquals(expected[0], actual[0]);
		assertEquals(expected[1], actual[1]);
	}

	public void testDoModelTranslation() throws Exception {
		TranslationPreProcessor prep = new TranslationPreProcessor(
				new VdmToolsWrapper(vppdeExecutable), new VdmToolsPoProcessor());
		String modelFile = setModel;
		List<String> contextFiles = new ArrayList<String>(0);
		PreparationData prepData = prep
				.prepareVdmFiles(modelFile, contextFiles);

		AutomaticProofSystemBatch aps = new AutomaticProofSystemBatch(mosmlDir,
				holDir, new VdmToolsWrapper(vppdeExecutable),
				new VdmToolsPoProcessor());
		String holCode = aps.doModelTranslation(prepData);

		assertNotNull(holCode);
		assertEquals(1210, holCode.length());
	}

	public void testDoModelTranslationWithDependenContext() throws Exception {
		TranslationPreProcessor prep = new TranslationPreProcessor(
				new VdmToolsWrapper(vppdeExecutable), new VdmToolsPoProcessor());
		String modelFile = doSortModel;
		List<String> contextFiles = new ArrayList<String>(1);
		contextFiles.add(sorterModel);
		PreparationData prepData = prep
				.prepareVdmFiles(modelFile, contextFiles);

		AutomaticProofSystemBatch aps = new AutomaticProofSystemBatch(mosmlDir,
				holDir, new VdmToolsWrapper(vppdeExecutable),
				new VdmToolsPoProcessor());
		String holCode = aps.doModelTranslation(prepData);

		assertNotNull(holCode);
		assertEquals(780, holCode.length());
	}
}
