package org.overturetool.VDM2JavaCG.Utilities;


public class Token {
	
	private Object value;

    public Token (Object obj) {
      value = obj;
    }


    public Object clone () {
      return new Token(this.value);
    }


    public String toString () {
      return "mk_token(" + value.toString() + ")";
    }


    public boolean equals (Object obj) {
      if (!(obj instanceof Token)) 
        return false;
      else 
        return this.value.equals(((Token)obj).value);
    }


    public int hashCode () {
      return value == null ? 0 : value.hashCode();
    }
}
