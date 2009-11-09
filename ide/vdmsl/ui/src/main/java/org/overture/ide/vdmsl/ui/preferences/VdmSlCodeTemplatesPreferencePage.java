package org.overture.ide.vdmsl.ui.preferences;

import org.eclipse.dltk.ui.templates.ScriptTemplateAccess;
import org.eclipse.dltk.ui.templates.ScriptTemplatePreferencePage;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.jface.text.IDocument;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.vdmsl.ui.UIPlugin;
import org.overture.ide.vdmsl.ui.internal.completion.VdmSlTemplateAccess;
import org.overture.ide.vdmsl.ui.internal.editor.IVdmSlPartitions;
import org.overture.ide.vdmsl.ui.internal.editor.SimpleVdmSlSourceViewerConfiguration;
import org.overture.ide.vdmsl.ui.internal.editor.VdmSlTextTools;

public class VdmSlCodeTemplatesPreferencePage extends ScriptTemplatePreferencePage {

	@Override
	protected ScriptSourceViewerConfiguration createSourceViewerConfiguration() {
		return new SimpleVdmSlSourceViewerConfiguration(
				getTextTools().getColorManager(), 
				getPreferenceStore(), 
				null,
				IVdmSlPartitions.VDMSL_PARTITIONING, 
				false);
	}

	@Override
	protected ScriptTemplateAccess getTemplateAccess() {
		return VdmSlTemplateAccess.getInstance();
	}

	@Override
	protected void setPreferenceStore() {
		setPreferenceStore(VdmUIPlugin.getDefault().getPreferenceStore());
	}
	
	private VdmSlTextTools getTextTools() {
		return UIPlugin.getDefault().getTextTools();
	}
	
	@Override
	protected void setDocumentParticioner(IDocument document) {
		getTextTools().setupDocumentPartitioner(document, IVdmSlPartitions.VDMSL_PARTITIONING);
	}

}
