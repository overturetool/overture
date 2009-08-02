package org.overturetool.proofsupport.external_tools.pog;

import java.io.IOException;

public interface PogGenerator {

	/**
	 * Generates a POG file based on the supplied <code>vdmFiles</code>.
	 * @param vdmFiles
	 * @return the path to the generated file, or null if the input is empty.
	 * @throws IOException
	 */
	public String generatePogFile(String[] vdmFiles) throws PogGeneratorException;
}
