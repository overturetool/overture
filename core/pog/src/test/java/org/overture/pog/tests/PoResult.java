package org.overture.pog.tests;

import java.io.Serializable;

public class PoResult implements Serializable {

	private static final long serialVersionUID = 1L;
	String poKind;
	String poExp;

	public PoResult() {
		this("", "");
	}

	public PoResult(String poKind, String poExp) {
		this.poKind = poKind;
		this.poExp = poExp;
	}

	public String getPoKind() {
		return poKind;
	}

	public void setPoKind(String poKind) {
		this.poKind = poKind;
	}

	public String getPoExp() {
		return poExp;
	}

	public void setPoExp(String poExp) {
		this.poExp = poExp;
	}

	@Override
	public String toString()
	{
		return poKind + " obligation: " + poExp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((poExp == null) ? 0 : poExp.hashCode());
		result = prime * result + ((poKind == null) ? 0 : poKind.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PoResult other = (PoResult) obj;
		if (poExp == null) {
			if (other.poExp != null)
				return false;
		} else if (!poExp.equals(other.poExp))
			return false;
		if (poKind == null) {
			if (other.poKind != null)
				return false;
		} else if (!poKind.equals(other.poKind))
			return false;
		return true;
	}
	
	

}
