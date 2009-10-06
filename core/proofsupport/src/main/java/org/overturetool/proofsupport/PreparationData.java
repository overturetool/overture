package org.overturetool.proofsupport;

import java.util.List;

import org.overturetool.ast.itf.IOmlDocument;
import org.overturetool.ast.itf.IOmlExpression;

public class PreparationData {

	protected List<String> vdmPos;
	protected IOmlDocument omlModel;
	protected List<IOmlDocument> omlContextDocuments;
	protected List<IOmlExpression> omlPos;

	public PreparationData(List<String> vdmPos, IOmlDocument omlModel, List<IOmlDocument> omlContextDocuments, List<IOmlExpression> omlPos) {
		this.vdmPos = vdmPos;
		this.omlModel = omlModel;
		this.omlContextDocuments = omlContextDocuments;
		this.omlPos = omlPos;
	}

	public List<String> getVdmPos() {
		return vdmPos;
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
