package org.overture.refactor.tests.base;

import java.util.List;

public class ResultObject {
	public List<String> renamings;
	
	public List<String> getRenamings() {
		return renamings;
	}
	
	public void setRenamings(List<String> renamings) {
			this.renamings = renamings;
		}
	
	public String config;
	
	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}	
	
	public ResultObject(){
		
	}
	
	
}
