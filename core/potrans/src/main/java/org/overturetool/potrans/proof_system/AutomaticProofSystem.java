package org.overturetool.potrans.proof_system;

import java.io.IOException;
import java.util.List;

import org.overturetool.potrans.external_tools.OvertureParserException;
import org.overturetool.potrans.external_tools.PogGenerator;
import org.overturetool.potrans.external_tools.PogGeneratorException;
import org.overturetool.potrans.external_tools.PogProcessor;
import org.overturetool.potrans.external_tools.PogProcessorException;
import org.overturetool.potrans.external_tools.Utilities;
import org.overturetool.potrans.external_tools.hol.HolInterpreter;
import org.overturetool.potrans.external_tools.hol.HolInterpreterException;
import org.overturetool.potrans.external_tools.hol.HolParameters;

public class AutomaticProofSystem {

	private final TranslationPreProcessor prep;
	private final Translator translator;
	private final HolParameters holParam;
	private final String vdmTacticsFile;

	public AutomaticProofSystem(String mosmlDir, String holDir, PogGenerator pogGen, PogProcessor pogProc)
			throws AutomaicProofSystemException {
		this.prep = new TranslationPreProcessor(pogGen, pogProc);
		this.holParam = new HolParameters(mosmlDir, holDir);
		try {
			this.translator = new Translator();
		} catch (TranslatorException e) {
			throw new AutomaicProofSystemException("[TRANSLATOR] " + e.getMessage(), e);
		}
		try {
			this.vdmTacticsFile = new ApplicationSettings().get(ApplicationSettings.VDM_HOL_TACTICS);
		} catch (Exception e) {
			throw new AutomaicProofSystemException("[APS] " + e.getMessage(), e);
		}
	}

	public String[] dischargeAllPos(String vdmModelFile, List<String> vdmContextFiles)
			throws AutomaicProofSystemException {
		String holCode = translateModelAndPos(vdmModelFile, vdmContextFiles);

		String[] result = doBatchProof(holCode);

		return result;
	}

	public String translateModelAndPos(String vdmModelFile,
			List<String> vdmContextFiles) throws AutomaicProofSystemException {
		PreparationData prepData = doPreparation(vdmModelFile, vdmContextFiles);
		return doTranslation(prepData);
	}

	private String doTranslation(PreparationData prepData) throws AutomaicProofSystemException {
		String holCode = "";
		try {
			holCode = translator.translateOmlToHol(prepData);
		} catch (TranslatorException e) {
			throw new AutomaicProofSystemException("[TRANSLATOR] " + e.getMessage(), e);
		}
		return holCode;
	}

	private String[] doBatchProof(String holCode) throws AutomaicProofSystemException {
		String[] result = new String[] {};
		HolInterpreter hol = null;
		try {
			hol = new HolInterpreter(holParam);
			hol.start();
			
			// load tactics
			String tacticsCode = Utilities.readHolCodeFile(vdmTacticsFile);
			hol.interpretModel(tacticsCode);

			// proof
			result = hol.interpretModel(holCode);
		} catch (HolInterpreterException e) {
			throw new AutomaicProofSystemException("[HOL] " + e.getMessage(), e);
		} catch (IOException e) {
			throw new AutomaicProofSystemException("[APS] Error reading VDM-HOL Tactics from file '" + vdmTacticsFile
					+ "'.", e);
		} finally {
			finishUp(hol);
		}
		return result;
	}

	private void finishUp(HolInterpreter hol) throws AutomaicProofSystemException {
		try {
			hol.quit();
		} catch (HolInterpreterException e) {
			throw new AutomaicProofSystemException("[HOL] Error terminting interpreter.", e);
		}
	}

	private PreparationData doPreparation(String vdmModelFile, List<String> vdmContextFiles)
			throws AutomaicProofSystemException {
		PreparationData prepData;
		try {
			prepData = prep.prepareVdmFiles(vdmModelFile, vdmContextFiles);
		} catch (PogGeneratorException e) {
			throw new AutomaicProofSystemException("[PO-GEN] " + e.getMessage(), e);
		} catch (PogProcessorException e) {
			throw new AutomaicProofSystemException("[PO-PROC] " + e.getMessage(), e);
		} catch (OvertureParserException e) {
			throw new AutomaicProofSystemException("[OVTPARSER] " + e.getMessage(), e);
		}
		return prepData;
	}

}
