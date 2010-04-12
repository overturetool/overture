package org.overture.ide.vdmpp.ui.editor.core;

import org.eclipse.jface.text.rules.ITokenScanner;
import org.overture.ide.ui.editor.core.VdmSourceViewerConfiguration;
import org.overture.ide.ui.editor.syntax.VdmColorProvider;
import org.overture.ide.vdmpp.ui.editor.syntax.VdmPpCodeScanner;



public class VdmPpSourceViewerConfiguration extends
		VdmSourceViewerConfiguration {

	@Override
	protected ITokenScanner getVdmCodeScanner() {
		return new VdmPpCodeScanner(new VdmColorProvider());
	}

}
