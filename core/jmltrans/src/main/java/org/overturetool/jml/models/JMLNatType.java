package org.overturetool.jml.models;



public class JMLNatType {

	
	public Integer val;
	
	/*@ public invariant
	  @ this.val.intValue() >= 0;
	  @*/
	
	public JMLNatType() {
		
		this.val = null;
	}
	
	
	public void setVal(Integer i) {
		
		this.val = i;
		
	}
	
	public Integer getVal() {
		return this.val;
	}
}
