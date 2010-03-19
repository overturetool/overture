package org.overture.ide.vdmsl.ui.editor.core;

import org.overture.ide.ui.editor.core.VdmEditor;
import org.overture.ide.ui.editor.core.VdmSourceViewerConfiguration;



public class VdmSlEditor extends VdmEditor {
	
	public VdmSlEditor() {
		super();
	}

	@Override
	protected VdmSourceViewerConfiguration getVdmSourceViewerConfiguration() {
		// TODO Auto-generated method stub
		return new VdmSlSourceViewerConfiguration();
	}
}
