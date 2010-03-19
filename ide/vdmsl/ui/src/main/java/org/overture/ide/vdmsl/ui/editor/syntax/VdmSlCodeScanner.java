package org.overture.ide.vdmsl.ui.editor.syntax;

import org.overture.ide.ui.editor.syntax.VdmCodeScanner;
import org.overture.ide.ui.editor.syntax.VdmColorProvider;


public class VdmSlCodeScanner extends VdmCodeScanner {

	public VdmSlCodeScanner(VdmColorProvider provider) {
		super(provider);
		
	}

	@Override
	protected String[] getKeywords() {
		return new VdmSlKeywords().getAllKeywords();
	}

}
