package org.overturetool.proofsupport.external_tools.vdmholtranslator;

import org.overturetool.ast.itf.IOmlExpression;
import org.overturetool.proofsupport.PreparationData;

public interface VdmToHolTranslator {

	public String translateModelAndPos(PreparationData prepData) throws TranslatorException;
	
	public String translateModel(PreparationData prepData) throws TranslatorException;
	
	public String translateExpression(IOmlExpression omlExpression) throws TranslatorException;
}
