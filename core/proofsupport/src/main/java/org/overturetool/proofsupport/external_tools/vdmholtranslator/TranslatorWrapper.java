package org.overturetool.proofsupport.external_tools.vdmholtranslator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.overturetool.ast.itf.IOmlDocument;
import org.overturetool.ast.itf.IOmlExpression;
import org.overturetool.potrans.DocumentProver;
import org.overturetool.potrans.HolDocument;
import org.overturetool.potrans.HolExpression;
import org.overturetool.potrans.MLExpression;
import org.overturetool.potrans.ProofObligation;
import org.overturetool.potrans.VdmHolTranslator;
import org.overturetool.proofsupport.PreparationData;
import org.overturetool.proofsupport.external_tools.Utilities;

public class TranslatorWrapper implements VdmToHolTranslator {

	private final VdmHolTranslator vdmHolTrans;

	public TranslatorWrapper() throws TranslatorException {
		try {
			this.vdmHolTrans = new VdmHolTranslator();
		} catch (CGException e) {
			throw new TranslatorException(
					"Error while settingup VDM-HOL translator.", e);
		}
	}

	public String translateModel(PreparationData prepData)
			throws TranslatorException {
		HolDocument holModel = translateDocument(prepData.getOmlModel());
		List<HolDocument> holContext = translateDocuments(prepData
				.getOmlContextDocuments());
		return printHolCode(holModel, holContext).toString();
	}

	public String translateModelAndPos(PreparationData prepData)
			throws TranslatorException {
		HolDocument holModel = translateDocument(prepData.getOmlModel());
		List<HolDocument> holContext = translateDocuments(prepData
				.getOmlContextDocuments());
		HashSet<ProofObligation> holPos = translateProofObligations(prepData
				.getOmlPos());
		return printHolCode(holModel, holContext, holPos).toString();
	}

	public String translateExpression(IOmlExpression omlExpression)
			throws TranslatorException {
		try {
			HolExpression holExpression = vdmHolTrans.translateExpression(omlExpression);
			return holExpression.print();
		} catch (CGException e) {
			throw new TranslatorException(
					"Error while translating OML expression.", e);
		}
	}

	private StringBuffer printHolCode(HolDocument holModel,
			List<HolDocument> holContext) throws TranslatorException {
		StringBuffer holCode = new StringBuffer();
		printContext(holContext, holCode);
		printModel(holModel, holCode);
		return holCode;
	}

	private void printModel(HolDocument holModel, StringBuffer holCode)
			throws TranslatorException {
		try {
			holCode.append(holModel.print()).append(Utilities.LINE_SEPARATOR);
		} catch (CGException e) {
			throw new TranslatorException(
					"Error while printing HOL context theroy.", e);
		}
	}

	private StringBuffer printHolCode(HolDocument holModel,
			List<HolDocument> holContext, HashSet<ProofObligation> holPos)
			throws TranslatorException {
		StringBuffer holCode = new StringBuffer();
		printContext(holContext, holCode);
		printModelAndPos(holModel, holPos, holCode);
		return holCode;
	}

	private void printModelAndPos(HolDocument holModel,
			HashSet<ProofObligation> holPos, StringBuffer holCode)
			throws TranslatorException {
		MLExpression mlExpr = buildMLExpression(holModel, holPos);
		printMLExpression(holCode, mlExpr);
	}

	private void printMLExpression(StringBuffer holCode, MLExpression mlExpr)
			throws TranslatorException {
		try {
			holCode.append(mlExpr.print());
		} catch (CGException e) {
			throw new TranslatorException(
					"Error while printing model and proof obligations.", e);
		}
	}

	private MLExpression buildMLExpression(HolDocument holModel,
			HashSet<ProofObligation> holPos) throws TranslatorException {
		DocumentProver docProv;
		try {
			docProv = new DocumentProver(holModel, holPos);
		} catch (CGException e) {
			throw new TranslatorException(
					"Error while setting up document prover.", e);
		}
		MLExpression mlExpr;
		try {
			mlExpr = docProv.getProofCounter();
		} catch (CGException e) {
			throw new TranslatorException(
					"Error while translating model and proof obligations.", e);
		}
		return mlExpr;
	}

	private void printContext(List<HolDocument> holContext, StringBuffer holCode)
			throws TranslatorException {
		for (HolDocument contextDoc : holContext)
			try {
				holCode.append(contextDoc.print()).append(
						Utilities.LINE_SEPARATOR);
			} catch (CGException e) {
				throw new TranslatorException(
						"Error while printing HOL context theroy.", e);
			}
	}

	private HashSet<ProofObligation> translateProofObligations(
			List<IOmlExpression> omlPos) throws TranslatorException {
		HashSet<ProofObligation> holPos = new HashSet<ProofObligation>();
		for (IOmlExpression omlPo : omlPos)
			try {
				holPos.add(new ProofObligation(omlPo, new Object()));
			} catch (CGException e) {
				throw new TranslatorException(
						"Error while creating proof obligations for translation.",
						e);
			} // TODO Classification
		// is not being
		// translated yet,
		// substitute
		// enumeration by
		// empty records:
		// Division ::;
		return holPos;
	}

	private List<HolDocument> translateDocuments(
			List<IOmlDocument> omlContextDocuments) throws TranslatorException {
		List<HolDocument> holContext = new ArrayList<HolDocument>(
				omlContextDocuments.size());
		for (IOmlDocument omlContextDocument : omlContextDocuments)
			holContext.add(translateDocument(omlContextDocument));
		return holContext;
	}

	private HolDocument translateDocument(IOmlDocument omlModel)
			throws TranslatorException {
		try {
			return vdmHolTrans.translateDocument(omlModel);
		} catch (CGException e) {
			throw new TranslatorException(
					"Error while translating IOmlDocument.", e);
		}
	}

}
