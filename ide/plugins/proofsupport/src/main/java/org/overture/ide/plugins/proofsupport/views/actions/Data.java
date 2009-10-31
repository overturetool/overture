package org.overture.ide.plugins.proofsupport.views.actions;

import org.overturetool.proofsupport.ProofResult;

public class Data
{
	private ProofResult proofResult = null;
	
	public Data(ProofResult profResult)
	{
		this.proofResult = profResult;
		
	}

	public String getStatus() {
		return proofResult.getStatus();
	}

	public String getPoId() {
		return proofResult.getPoId();
	}

	public String getVdmPo() {
		return proofResult.getVdmPo();
	}

	public boolean isDischarged() {
		return proofResult.isDischarged();
	}

	public String toString() {
		return proofResult.toString();
	}


}