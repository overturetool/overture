package org.overturetool.proofsupport.external_tools.vdmholtranslator;

import org.overturetool.proofsupport.PreparationData;

public interface VdmToHolTranslator {

	public String translateOmlToHol(PreparationData prepData) throws TranslatorException;
}
