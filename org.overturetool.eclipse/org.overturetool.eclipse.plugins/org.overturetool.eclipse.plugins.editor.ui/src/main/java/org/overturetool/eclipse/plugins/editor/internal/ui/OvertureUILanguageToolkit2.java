package org.overturetool.eclipse.plugins.editor.internal.ui;

import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.overturetool.eclipse.plugins.editor.internal.ui.text.SimpleOvertureSourceViewerConfiguration;

public class OvertureUILanguageToolkit2 extends OvertureUILanguageToolkit {
	public ScriptSourceViewerConfiguration createSourceViewerConfiguration() {
		return new SimpleOvertureSourceViewerConfiguration(getTextTools().getColorManager(), getPreferenceStore(), null, getPartitioningId(), false);
	}
}
