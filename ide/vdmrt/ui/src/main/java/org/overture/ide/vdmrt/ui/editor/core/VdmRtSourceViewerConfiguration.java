package org.overture.ide.vdmrt.ui.editor.core;

import org.eclipse.jface.text.rules.ITokenScanner;
import org.overture.ide.ui.editor.core.VdmSourceViewerConfiguration;
import org.overture.ide.ui.editor.syntax.VdmColorProvider;
import org.overture.ide.vdmrt.ui.editor.syntax.VdmRtCodeScanner;


public class VdmRtSourceViewerConfiguration extends
		VdmSourceViewerConfiguration {

	@Override
	protected ITokenScanner getVdmCodeScanner() {
		return new VdmRtCodeScanner(new VdmColorProvider());
	}

}
