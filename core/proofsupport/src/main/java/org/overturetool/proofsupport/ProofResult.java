package org.overturetool.proofsupport;

import org.overturetool.proofsupport.external_tools.Utilities;

public class ProofResult {

	private String vdmPo = null;
	private String poId = null;
	private boolean isDischarged;
	
	public ProofResult(String poId, String vdmPo, boolean discharged) {
		super();
		this.vdmPo = vdmPo;
		this.poId = poId;
		this.isDischarged = discharged;
	}

	public String getVdmPo() {
		return vdmPo;
	}

	public String getPoId() {
		return poId;
	}

	public boolean isDischarged() {
		return isDischarged;
	}
	
	public String getStatus() {
		return isDischarged ? "DISCHARGED" : "NOT DISCHARGED"; 
	}
	
	public String toString() {
		StringBuffer sb  = new StringBuffer();
		sb.append(poId).append(Utilities.LINE_SEPARATOR);
		sb.append("Expression:").append(Utilities.LINE_SEPARATOR);
		sb.append(vdmPo).append(Utilities.LINE_SEPARATOR);
		sb.append("Status: ").append(getStatus());
		return sb.toString();
	}
}
