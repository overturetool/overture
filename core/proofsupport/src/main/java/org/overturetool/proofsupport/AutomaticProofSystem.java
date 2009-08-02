package org.overturetool.proofsupport;

import java.io.IOException;
import java.util.List;

import org.overturetool.proofsupport.external_tools.Utilities;
import org.overturetool.proofsupport.external_tools.hol.HolInterpreter;
import org.overturetool.proofsupport.external_tools.hol.HolInterpreterException;
import org.overturetool.proofsupport.external_tools.hol.HolInterpreterFactory;
import org.overturetool.proofsupport.external_tools.hol.HolParameters;
import org.overturetool.proofsupport.external_tools.omlparser.ParserException;
import org.overturetool.proofsupport.external_tools.pog.PogGenerator;
import org.overturetool.proofsupport.external_tools.pog.PogGeneratorException;
import org.overturetool.proofsupport.external_tools.pog.PogProcessor;
import org.overturetool.proofsupport.external_tools.pog.PogProcessorException;
import org.overturetool.proofsupport.external_tools.vdmholtranslator.TranslatorException;
import org.overturetool.proofsupport.external_tools.vdmholtranslator.VdmToHolTranslator;
import org.overturetool.proofsupport.external_tools.vdmholtranslator.VdmToHolTranslatorFactory;

public class AutomaticProofSystem {

	private final TranslationPreProcessor prep;
	private final VdmToHolTranslator translator;
	private final HolParameters holParam;
	private final String vdmTacticsFile;

	public AutomaticProofSystem(String mosmlDir, String holDir, PogGenerator pogGen, PogProcessor pogProc)
			throws AutomaicProofSystemException {
		this.prep = new TranslationPreProcessor(pogGen, pogProc);
		this.holParam = new HolParameters(mosmlDir, holDir);
		try {
			this.translator = VdmToHolTranslatorFactory.newVdmToHolTranslatorInstance();
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
		try {
			return translator.translateOmlToHol(prepData);
		} catch (TranslatorException e) {
			throw new AutomaicProofSystemException("[TRANSLATOR] " + e.getMessage(), e);
		}
	}

	public String[] doBatchProof(String holCode) throws AutomaicProofSystemException {
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
			throw new AutomaicProofSystemException("[APS] Error reading VDM-HOL Tactics from file '" + vdmTacticsFile
					+ "'.", e);
		} finally {
			finishUp(hol);
		}
		return result;
	}

	private void loadTactics(HolInterpreter hol) throws IOException,
			HolInterpreterException {
		String tacticsCode = Utilities.readHolCodeFile(vdmTacticsFile);
		hol.interpretModel(tacticsCode);
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
		} catch (ParserException e) {
			throw new AutomaicProofSystemException("[PARSER] " + e.getMessage(), e);
		}
		return prepData;
	}

}
