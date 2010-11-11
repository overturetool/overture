package org.overture.ide.vdmpp.ui.editor.core;

import org.overture.ide.ui.editor.core.VdmEditor;
import org.overture.ide.ui.editor.core.VdmSourceViewerConfiguration;

public class VdmPpEditor extends VdmEditor {

	public VdmPpEditor() {
		super();
	}

	@Override
	public VdmSourceViewerConfiguration getVdmSourceViewerConfiguration() {
		return new VdmPpSourceViewerConfiguration();
	}

}
