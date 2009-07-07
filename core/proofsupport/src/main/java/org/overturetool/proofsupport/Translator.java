package org.overturetool.proofsupport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.overturetool.ast.itf.IOmlDocument;
import org.overturetool.ast.itf.IOmlExpression;
import org.overturetool.potrans.DocumentProver;
import org.overturetool.potrans.HolDocument;
import org.overturetool.potrans.MLExpression;
import org.overturetool.potrans.ProofObligation;
import org.overturetool.potrans.VdmHolTranslator;
import org.overturetool.proofsupport.external_tools.Utilities;

public class Translator {
	
	private final VdmHolTranslator vdmHolTrans;

	public Translator() throws TranslatorException {
		try {
			this.vdmHolTrans = new VdmHolTranslator();
		} catch (CGException e) {
			throw new TranslatorException("Error while settingup VDM-HOL translator.", e);
		}
	}
	
	public String translateOmlToHol(PreparationData prepData) throws TranslatorException {
		
			HolDocument holModel = translateDocument(prepData.getOmlModel());
			List<HolDocument> holContext = translateDocuments(prepData.getOmlContextDocuments());
			HashSet<ProofObligation> holPos = translateProofObligations(prepData.getOmlPos());
		return printHolCode(holModel, holContext, holPos).toString();
	}

	private StringBuffer printHolCode(HolDocument holModel, List<HolDocument> holContext,
			HashSet<ProofObligation> holPos) throws TranslatorException {
		StringBuffer holCode = new StringBuffer();
		printContext(holContext, holCode);
		printModel(holModel, holPos, holCode);
		return holCode;
	}

	private void printModel(HolDocument holModel, HashSet<ProofObligation> holPos, StringBuffer holCode)
			throws TranslatorException {
		MLExpression mlExpr = buildMLExpression(holModel, holPos);
		printMLExpression(holCode, mlExpr);
	}

	private void printMLExpression(StringBuffer holCode, MLExpression mlExpr) throws TranslatorException {
		try {
			holCode.append(mlExpr.print());
		} catch (CGException e) {
			throw new TranslatorException("Error while printing model and proof obligations.", e);
		}
	}

	private MLExpression buildMLExpression(HolDocument holModel, HashSet<ProofObligation> holPos)
			throws TranslatorException {
		DocumentProver docProv;
		try {
			docProv = new DocumentProver(holModel, holPos);
		} catch (CGException e) {
			throw new TranslatorException("Error while settingup document prover.", e);
		}
		MLExpression mlExpr;
		try {
			mlExpr = docProv.getProofCounter();
		} catch (CGException e) {
			throw new TranslatorException("Error while translating model and proof obligations.", e);
		}
		return mlExpr;
	}

	private void printContext(List<HolDocument> holContext, StringBuffer holCode) throws TranslatorException {
		for(HolDocument contextDoc : holContext)
			try {
				holCode.append(contextDoc.print()).append(Utilities.LINE_SEPARATOR);
			} catch (CGException e) {
				throw new TranslatorException("Error while printing HOL context theroy.", e);
			}
	}

	private HashSet<ProofObligation> translateProofObligations(List<IOmlExpression> omlPos) throws TranslatorException {
		HashSet<ProofObligation> holPos = new HashSet<ProofObligation>();
		for (IOmlExpression omlPo : omlPos)
			try {
				holPos.add(new ProofObligation(omlPo, new Object()));
			} catch (CGException e) {
				throw new TranslatorException("Error while creating proof obligations for translation.", e);
			} // TODO Classification
		// is not being
		// translated yet,
		// substitute
		// enumeration by
		// empty records:
		// Division ::;
		return holPos;
	}

	private List<HolDocument> translateDocuments(List<IOmlDocument> omlContextDocuments) throws TranslatorException {
		List<HolDocument> holContext = new ArrayList<HolDocument>(omlContextDocuments.size());
		// TODO check if this code passes VdmHolTans dependency checker
		for (IOmlDocument omlContextDocument : omlContextDocuments)
			holContext.add(translateDocument(omlContextDocument));
		return holContext;
	}

	private HolDocument translateDocument(IOmlDocument omlModel) throws TranslatorException {
		try {
			return vdmHolTrans.translateDocument(omlModel);
		} catch (CGException e) {
			throw new TranslatorException("Error while translating IOmlDocument.", e);
		}
	}

}
