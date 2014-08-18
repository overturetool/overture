package org.overture.core.npp;

import org.overture.ast.analysis.intf.IQuestionAnswer;


/**
 * The Interface IPrettyPrinter defines a generic pretty printer
 * for ASTs that has an overridable symbol table.
 */
public interface IPrettyPrinter extends IQuestionAnswer<IndentTracker,String> {
	
	/**
	 * Helper method for setting the attribute table.
	 *
	 * @param it the new attribute table
	 */
	 void setInsTable(ISymbolTable it);
	 
	 //ExpressionNpp ExpressionNpp();
	 //DefinitionNpp DefinitionNpp();
	
}
