package org.overture.ide.vdmpp.ui.internal.editor;

import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.overture.ide.vdmpp.ui.VdmPpUILanguageToolkit;

public class VdmPpUILanguageToolkit2 extends VdmPpUILanguageToolkit {

	public ScriptSourceViewerConfiguration createSourceViewerConfiguration() {
		return new SimpleVdmPpSourceViewerConfiguration(
				getTextTools().getColorManager(), 
				getPreferenceStore(),
				null,
				getPartitioningId(),
				false);
	}

}
