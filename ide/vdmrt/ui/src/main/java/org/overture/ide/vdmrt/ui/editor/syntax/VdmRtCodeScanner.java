package org.overture.ide.vdmrt.ui.editor.syntax;

import org.overture.ide.ui.editor.syntax.IVdmKeywords;
import org.overture.ide.ui.editor.syntax.VdmCodeScanner;
import org.overture.ide.ui.editor.syntax.VdmColorProvider;

public class VdmRtCodeScanner extends VdmCodeScanner {

	public VdmRtCodeScanner(VdmColorProvider provider) {
		super(provider);
		
	}

	@Override
	protected IVdmKeywords getKeywords() {
		return new VdmRtKeywords();
	}

}
