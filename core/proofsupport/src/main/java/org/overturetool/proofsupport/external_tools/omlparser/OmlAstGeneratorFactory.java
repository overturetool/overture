package org.overturetool.proofsupport.external_tools.omlparser;

public abstract class OmlAstGeneratorFactory {

	public static OmlAstGenerator newOmlAstGenertorInstance() {
		return new OvertureParserWrapper();
	}
}
