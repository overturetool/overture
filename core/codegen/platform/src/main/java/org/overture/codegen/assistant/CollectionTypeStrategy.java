package org.overture.codegen.assistant;

import org.overture.codegen.ir.STypeIR;

public interface CollectionTypeStrategy
{
	boolean isCollectionType(STypeIR type);
	
	STypeIR getElementType(STypeIR type);
}
