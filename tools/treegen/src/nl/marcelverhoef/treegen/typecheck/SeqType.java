package nl.marcelverhoef.treegen.typecheck;

public class SeqType extends Type {

	// embedded sequence type
	public Type seq_type;
	
	// constructor
	public SeqType (Type tp) { seq_type = tp; }
	
	// overloaded member functions
	public boolean isSeqType() { return true; }
	public boolean isStringType() { return seq_type.isCharType(); }
	public boolean isCollection() { return !isStringType(); }
	
}
