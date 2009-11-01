package org.overturetool.proofsupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.overturetool.ast.itf.IOmlExpression;
import org.overturetool.proofsupport.external_tools.Utilities;
import org.overturetool.proofsupport.external_tools.hol.HolInterpreter;
import org.overturetool.proofsupport.external_tools.hol.HolInterpreterException;
import org.overturetool.proofsupport.external_tools.hol.HolParameters;
import org.overturetool.proofsupport.external_tools.omlparser.ParserException;
import org.overturetool.proofsupport.external_tools.pog.PoGenerator;
import org.overturetool.proofsupport.external_tools.pog.PoGeneratorException;
import org.overturetool.proofsupport.external_tools.pog.PoProcessor;
import org.overturetool.proofsupport.external_tools.pog.PoProcessorException;
import org.overturetool.proofsupport.external_tools.vdmholtranslator.TranslatorException;
import org.overturetool.proofsupport.external_tools.vdmholtranslator.VdmToHolTranslator;
import org.overturetool.proofsupport.external_tools.vdmholtranslator.VdmToHolTranslatorFactory;

public abstract class AutomaticProofSystem {

	protected static final String PO_GENERATOR_COMPONENT = "[PO-GEN] ";
	protected static final String APS_COMPONENT = "[APS] ";
	protected static final String HOL_COMPONENT = "[HOL] ";
	protected static final String PO_PROCESSOR_COMPONENT = "[PO-PROC] ";
	protected static final String PARSER_COMPONENT = "[PARSER] ";
	protected static final String TRANSLATOR_COMPONENT = "[TRANSLATOR] ";
	protected final TranslationPreProcessor prep;
	protected final VdmToHolTranslator translator;
	protected final HolParameters holParam;
	protected final String vdmTacticsFile;

	protected AutomaticProofSystem(String mosmlDir, String holDir,
			PoGenerator pogGen, PoProcessor pogProc)
			throws AutomaicProofSystemException {
		this.prep = new TranslationPreProcessor(pogGen, pogProc);
		this.holParam = new HolParameters(mosmlDir, holDir);
		try {
			this.translator = VdmToHolTranslatorFactory
					.newVdmToHolTranslatorInstance();
		} catch (TranslatorException e) {
			throw wrapException(TRANSLATOR_COMPONENT, e);
		}
		try {
			this.vdmTacticsFile = new ApplicationSettings()
					.get(ApplicationSettings.VDM_HOL_TACTICS);
		} catch (Exception e) {
			throw wrapException(APS_COMPONENT, e);
		}
	}

	protected void loadTactics(HolInterpreter hol) throws IOException,
			HolInterpreterException {
		String tacticsCode = Utilities.readHolCodeFile(ApplicationSettings
				.getHolTacticsFile(vdmTacticsFile));
		hol.interpretModel(tacticsCode);
	}

	protected void finishUp(HolInterpreter hol)
			throws AutomaicProofSystemException {
		try {
			if(hol != null)
				hol.quit();
		} catch (HolInterpreterException e) {
			throw wrapException(HOL_COMPONENT, "Error terminting interpreter.",
					e);
		}
	}

	protected PreparationData doPreparation(String vdmModelFile,
			List<String> vdmContextFiles) throws AutomaicProofSystemException {
		PreparationData prepData = null;
		try {
			prepData = prep.prepareVdmFiles(vdmModelFile, vdmContextFiles);
		} catch (PoGeneratorException e) {
			throw wrapException(PO_GENERATOR_COMPONENT, e);
		} catch (PoProcessorException e) {
			wrapException(PO_PROCESSOR_COMPONENT, e);
		} catch (ParserException e) {
			wrapException(PARSER_COMPONENT, e);
		}
		return prepData;
	}

	protected PreparationData doPreparation(String vdmModelFile,
			List<String> vdmContextFiles, String pogFile)
			throws AutomaicProofSystemException {
		PreparationData prepData = null;
		try {
			prepData = prep.prepareVdmFiles(vdmModelFile, vdmContextFiles,
					pogFile);
		} catch (PoGeneratorException e) {
			throw wrapException(PO_GENERATOR_COMPONENT, e);
		} catch (PoProcessorException e) {
			wrapException(PO_PROCESSOR_COMPONENT, e);
		} catch (ParserException e) {
			wrapException(PARSER_COMPONENT, e);
		}
		return prepData;
	}

	protected String doModelTranslation(PreparationData prepData)
			throws AutomaicProofSystemException {
		try {
			return translator.translateModel(prepData);
		} catch (TranslatorException e) {
			throw wrapException(TRANSLATOR_COMPONENT, e);
		}
	}

	protected ArrayList<String> doPosTranslation(PreparationData prepData)
			throws AutomaicProofSystemException {
		try {
			ArrayList<String> pos = new ArrayList<String>(prepData.posSize());
			for (IOmlExpression po : prepData.getOmlPos()) {
				pos.add(translator.translateExpression(po));
			}
			return pos;
		} catch (TranslatorException e) {
			throw wrapException(TRANSLATOR_COMPONENT, e);
		}
	}

	protected Proof doModelAndPosTranslation(PreparationData prepData)
			throws AutomaicProofSystemException {
		String model = doModelTranslation(prepData);
		ArrayList<String> pos = doPosTranslation(prepData);
		if(model != null && pos.size() > 0) {
			return new Proof(model, pos, prepData.vdmPos);
		}
		else 
			return null;
	}

	protected AutomaicProofSystemException wrapException(String componentLabel,
			Exception e) {
		return wrapException(componentLabel, null, e);
	}

	protected AutomaicProofSystemException wrapException(String componentLabel,
			String message, Exception e) {
		return new AutomaicProofSystemException(componentLabel
				+ (message != null ? message : "") + e.getMessage(), e);
	}

	protected AutomaicProofSystemException buildException(
			String componentLabel, String message) {
		return new AutomaicProofSystemException(componentLabel
				+ (message != null ? message : ""), null);
	}
}
