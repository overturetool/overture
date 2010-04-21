package org.overture.ide.vdmrt.ui.editor.core;

import org.overture.ide.ui.editor.core.VdmEditor;
import org.overture.ide.ui.editor.core.VdmSourceViewerConfiguration;

public class VdmRtEditor extends VdmEditor {

	public VdmRtEditor() {
		super();
	}

	@Override
	public VdmSourceViewerConfiguration getVdmSourceViewerConfiguration() {
		// TODO Auto-generated method stub
		return new VdmRtSourceViewerConfiguration();
	}

}
