package nl.marcelverhoef.treegen.typecheck;

public class QuotedType extends Type {

	// embedded quote type name
	public String quoted_type;

	// constructor
	public QuotedType (String qtstr) { quoted_type = qtstr; }
	
	// overloaded member function
	public boolean isQuotedType() { return true; }
	
}
