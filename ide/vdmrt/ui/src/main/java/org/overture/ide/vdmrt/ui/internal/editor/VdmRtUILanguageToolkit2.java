package org.overture.ide.vdmrt.ui.internal.editor;

import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.overture.ide.vdmrt.ui.VdmRtUILanguageToolkit;

public class VdmRtUILanguageToolkit2 extends VdmRtUILanguageToolkit {

	public ScriptSourceViewerConfiguration createSourceViewerConfiguration() {
		return new SimpleVdmRtSourceViewerConfiguration (
				getTextTools().getColorManager(), 
				getPreferenceStore(),
				null,
				getPartitioningId(),
				false);
	}
}
