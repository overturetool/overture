package org.overturetool.proofsupport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.overturetool.ast.itf.IOmlDocument;
import org.overturetool.ast.itf.IOmlExpression;

public class PreparationData {

	protected List<String> vdmPos = null;
	protected IOmlDocument omlModel = null;
	protected List<IOmlDocument> omlContextDocuments = null;
	protected List<IOmlExpression> omlPos = null;

	public PreparationData(List<String> vdmPos, IOmlDocument omlModel, List<IOmlDocument> omlContextDocuments, List<IOmlExpression> omlPos) {
		this.vdmPos = vdmPos;
		this.omlModel = omlModel;
		this.omlContextDocuments = omlContextDocuments;
		this.omlPos = omlPos;
	}
	
	public PreparationData(IOmlDocument omlModel, List<IOmlDocument> omlContext) {
		this.vdmPos = new ArrayList<String>(0);
		this.omlModel = omlModel;
		this.omlContextDocuments = omlContext;
		this.omlPos = new ArrayList<IOmlExpression>(0);
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

	public int posSize() {
		return omlPos.size();
	}
}
