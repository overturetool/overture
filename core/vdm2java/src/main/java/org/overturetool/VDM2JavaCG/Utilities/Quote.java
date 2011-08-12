package org.overturetool.VDM2JavaCG.Utilities;


public class Quote {

	private String Literal;
	
	public Quote(String str) {
		Literal = str;
	}
	
	public Object clone () {
	      return new Quote(this.Literal);
	    }
	
	public String toString () {
	      return "<" + this.Literal + ">";
	    }


	    public boolean equals (Object obj) {
	      if (!(obj instanceof Quote)) 
	        return false;
	      else 
	        return this.Literal.equals(((Quote)obj).Literal);
	    }


	    public int hashCode () {
	      return Literal == null ? 0 : Literal.hashCode();
	    }
}