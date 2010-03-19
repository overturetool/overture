package org.overture.ide.vdmsl.ui.editor.core;

import org.eclipse.jface.text.rules.ITokenScanner;
import org.overture.ide.ui.editor.core.VdmSourceViewerConfiguration;
import org.overture.ide.ui.editor.syntax.VdmColorProvider;
import org.overture.ide.vdmsl.ui.editor.syntax.VdmSlCodeScanner;



public class VdmSlSourceViewerConfiguration extends
		VdmSourceViewerConfiguration {


	@Override
	protected ITokenScanner getVdmCodeScanner() {
		return new VdmSlCodeScanner(new VdmColorProvider());
	}
}
