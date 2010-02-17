package org.overture.ide.vdmpp.ui.editor.syntax;

import org.overture.ide.ui.editor.syntax.VdmCodeScanner;
import org.overture.ide.ui.editor.syntax.VdmColorProvider;

public class VdmPpCodeScanner extends VdmCodeScanner {

	public VdmPpCodeScanner(VdmColorProvider provider) {
		super(provider);
		
	}

	@Override
	protected String[] getKeywords() {
		return new VdmPpKeywords().getAllKeywords();
	}

}
