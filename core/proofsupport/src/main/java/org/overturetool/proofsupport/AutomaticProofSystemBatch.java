package org.overturetool.proofsupport;

import java.io.IOException;
import java.util.List;

import org.overturetool.proofsupport.external_tools.hol.HolInterpreter;
import org.overturetool.proofsupport.external_tools.hol.HolInterpreterException;
import org.overturetool.proofsupport.external_tools.hol.HolInterpreterFactory;
import org.overturetool.proofsupport.external_tools.pog.PogGenerator;
import org.overturetool.proofsupport.external_tools.pog.PogProcessor;

public class AutomaticProofSystemBatch extends AutomaticProofSystem {

	public AutomaticProofSystemBatch(String mosmlDir, String holDir,
			PogGenerator pogGen, PogProcessor pogProc)
			throws AutomaicProofSystemException {
		super(mosmlDir, holDir, pogGen, pogProc);
	}

	public String[] dischargeAllPos(String vdmModelFile,
			List<String> vdmContextFiles) throws AutomaicProofSystemException {
		String holCode = translateModelAndPos(vdmModelFile, vdmContextFiles);
		return doBatchProof(holCode);
	}

	public String translateModelAndPos(String vdmModelFile,
			List<String> vdmContextFiles) throws AutomaicProofSystemException {
		PreparationData prepData = doPreparation(vdmModelFile, vdmContextFiles);
		return doModelAndPosTranslation(prepData);
	}

	public String[] doBatchProof(String holCode)
			throws AutomaicProofSystemException {
		String[] result = new String[] {};
		HolInterpreter hol = null;
		try {
			hol = HolInterpreterFactory.newHolInterepterInstance(holParam);
			hol.start();

			loadTactics(hol);

			result = hol.interpretModel(holCode);
		} catch (HolInterpreterException e) {
			throw new AutomaicProofSystemException("[HOL] " + e.getMessage(), e);
		} catch (IOException e) {
			throw new AutomaicProofSystemException(
					"[APS] Error reading VDM-HOL Tactics from file '"
							+ vdmTacticsFile + "'.", e);
		} finally {
			finishUp(hol);
		}
		return result;
	}
}