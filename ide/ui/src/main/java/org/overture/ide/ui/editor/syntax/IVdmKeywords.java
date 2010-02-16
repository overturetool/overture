package org.overture.ide.ui.editor.syntax;

public interface IVdmKeywords {
	  
	public String[] getReservedwords() ;	  
	public String[] getMultipleKeywords();
	public String[] getBinaryoperators();
	public String[] getBasictypes();
	public String[] getTextvalues();
	public String[] getUnaryoperators();
	public String[] getAllKeywords();

}