package org.overture.ide.vdmrt.ui.preferences;

import org.eclipse.dltk.ui.templates.ScriptTemplateAccess;
import org.eclipse.dltk.ui.templates.ScriptTemplatePreferencePage;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.jface.text.IDocument;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.vdmrt.ui.UIPlugin;
import org.overture.ide.vdmrt.ui.completions.VdmRtTemplateAccess;
import org.overture.ide.vdmrt.ui.internal.editor.IVdmRtPartitions;
import org.overture.ide.vdmrt.ui.internal.editor.SimpleVdmRtSourceViewerConfiguration;
import org.overture.ide.vdmrt.ui.internal.editor.VdmRtTextTools;

public class VdmRtCodeTemplatesPreferencePage extends ScriptTemplatePreferencePage {

	@Override
	protected ScriptSourceViewerConfiguration createSourceViewerConfiguration() {
		return new SimpleVdmRtSourceViewerConfiguration(
				getTextTools().getColorManager(), 
				getPreferenceStore(), 
				null,
				IVdmRtPartitions.vdmrt_PARTITIONING, 
				false);
	}

	@Override
	protected ScriptTemplateAccess getTemplateAccess() {
		return VdmRtTemplateAccess.getInstance();
	}

	@Override
	protected void setPreferenceStore() {
		setPreferenceStore(VdmUIPlugin.getDefault().getPreferenceStore());
		
	}

	private VdmRtTextTools getTextTools() {
		return UIPlugin.getDefault().getTextTools();
	}
	
	@Override
	protected void setDocumentParticioner(IDocument document) {
		getTextTools().setupDocumentPartitioner(document, IVdmRtPartitions.vdmrt_PARTITIONING);
	}


}
