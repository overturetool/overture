package org.overture.refactor.tests.base;

import java.util.List;

public class ResultObject {
	public String language;
	public String config;
	public List<String> renamings;
	public List<String> extractions;
	public List<String> signatureChanges;
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}	
		
	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}	
		
	public List<String> getRenamings() {
		return renamings;
	}
	
	public void setRenamings(List<String> renamings) {
			this.renamings = renamings;
	}

	public List<String> getExtractions() {
		return extractions;
	}
	
	public void setgetExtractions(List<String> extractions) {
			this.extractions = extractions;
	}
		
	public ResultObject(){
		
	}

	public List<String> getSignatureChanges() {
		return signatureChanges;
	}

	public void setSignatureChanges(List<String> signatureChanges) {
		this.signatureChanges = signatureChanges;
	}	
}