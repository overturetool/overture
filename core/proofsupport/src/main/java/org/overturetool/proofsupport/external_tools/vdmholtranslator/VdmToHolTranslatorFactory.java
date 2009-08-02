package org.overturetool.proofsupport.external_tools.vdmholtranslator;

public abstract class VdmToHolTranslatorFactory {

	public static VdmToHolTranslator newVdmToHolTranslatorInstance() throws TranslatorException {
		return new TranslatorWrapper();
	}
}
