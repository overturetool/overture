package org.overture.ide.vdmsl.ui.internal.editor;

import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.overture.ide.vdmsl.ui.VdmSlUILanguageToolkit;

public class VdmSlUILanguageToolkit2 extends VdmSlUILanguageToolkit {

	public ScriptSourceViewerConfiguration createSourceViewerConfiguration() {
		return new SimpleVdmSlSourceViewerConfiguration(
				getTextTools().getColorManager(), 
				getPreferenceStore(),
				null,
				getPartitioningId(),
				false);
	}

}
