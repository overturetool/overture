package org.overture.ide.ui.editor.syntax;

public interface IVdmKeywords {
	  
	public String[] getReservedwords() ;	  
	public String[] getMultipleKeywords();
	public String[] getBinaryoperators();
	public String[] getBinarySymbolOperators();
	public String[] getBasictypes();
	public String[] getTextvalues();
	public String[] getUnaryoperators();
	//public String[] getAllKeywords();
	public String[] getAllSingleWordKeywords();
	public boolean supportsQuoteTypes();
	public boolean supportsTypleSelect();
	public String[] getUnderscorePrefixKeywords();
	public String[] getUnderscorePrefixReservedWords();

}