package org.overturetool.potrans.proof_system;

import java.util.List;

import org.overturetool.ast.itf.IOmlDocument;
import org.overturetool.ast.itf.IOmlExpression;

public class PreparationData {


	protected IOmlDocument omlModel;
	protected List<IOmlDocument> omlContextDocuments;
	protected List<IOmlExpression> omlPos;

	public PreparationData( IOmlDocument omlModel, List<IOmlDocument> omlContextDocuments, List<IOmlExpression> omlPos) {
		this.omlModel = omlModel;
		this.omlContextDocuments = omlContextDocuments;
		this.omlPos = omlPos;
	}

	public IOmlDocument getOmlModel() {
		return omlModel;
	}

	public List<IOmlDocument> getOmlContextDocuments() {
		return omlContextDocuments;
	}

	public List<IOmlExpression> getOmlPos() {
		return omlPos;
	}
}
