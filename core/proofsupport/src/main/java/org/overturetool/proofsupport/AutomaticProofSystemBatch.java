package org.overturetool.proofsupport;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.overturetool.proofsupport.external_tools.Utilities;
import org.overturetool.proofsupport.external_tools.hol.HolInterpreter;
import org.overturetool.proofsupport.external_tools.hol.HolInterpreterException;
import org.overturetool.proofsupport.external_tools.hol.HolInterpreterFactory;
import org.overturetool.proofsupport.external_tools.pog.PoGenerator;
import org.overturetool.proofsupport.external_tools.pog.PoProcessor;

public class AutomaticProofSystemBatch extends AutomaticProofSystem {

	protected long TIMEOUT = 5000;

	public AutomaticProofSystemBatch(String mosmlDir, String holDir,
			PoGenerator pogGen, PoProcessor pogProc)
			throws AutomaicProofSystemException {
		super(mosmlDir, holDir, pogGen, pogProc);
	}

	public String[] dischargeAllPos(String vdmModelFile,
			List<String> vdmContextFiles) throws AutomaicProofSystemException {
		Proof proof = translateModelAndPos(vdmModelFile, vdmContextFiles);
		return doBatchProof(proof);
	}

	public String[] dischargeAllPos(String vdmModelFile,
			List<String> vdmContextFiles, String pogFile)
			throws AutomaicProofSystemException {
		Proof proof = translateModelAndPos(vdmModelFile, vdmContextFiles,
				pogFile);
		return doBatchProof(proof);
	}

	public Proof translateModelAndPos(String vdmModelFile,
			List<String> vdmContextFiles) throws AutomaicProofSystemException {
		PreparationData prepData = doPreparation(vdmModelFile, vdmContextFiles);
		return doModelAndPosTranslation(prepData);
	}

	public Proof translateModelAndPos(String vdmModelFile,
			List<String> vdmContextFiles, String pogFile)
			throws AutomaicProofSystemException {
		PreparationData prepData = doPreparation(vdmModelFile, vdmContextFiles,
				pogFile);
		return doModelAndPosTranslation(prepData);
	}

	public String[] doBatchProof(Proof proofData)
			throws AutomaicProofSystemException {
		LinkedList<String> result = new LinkedList<String>();
		HolInterpreter hol = null;
		try {
			hol = HolInterpreterFactory.newHolInterepterInstance(holParam);
			hol.start();

			loadTactics(hol);

			hol.interpretModel(proofData.getTheory());
			// TODO: detect errors in theory

			for (int i = 0; i < proofData.proofCommandsSize(); i++) {
				StringBuffer sb  = new StringBuffer();
				sb.append("PO-").append(i + 1).append(Utilities.LINE_SEPARATOR);
				sb.append("Expression:").append(Utilities.LINE_SEPARATOR);
				sb.append(proofData.getProofExpression(i)).append(Utilities.LINE_SEPARATOR);
				sb.append("Result: ").append(hol.dischargeProof(proofData
						.getProofCommand(i)) ? "DISCHARGED"
								: "NOT DISCHARGED");
				result.add(sb.toString());
			}		
		} catch (HolInterpreterException e) {
			throw new AutomaicProofSystemException("[HOL] " + e.getMessage(), e);
		} catch (IOException e) {
			throw new AutomaicProofSystemException(
					"[APS] Error reading VDM-HOL Tactics from file '"
							+ vdmTacticsFile + "'.", e);
		} finally {
			finishUp(hol);
		}
		return result.toArray(new String[result.size()]);
	}
}