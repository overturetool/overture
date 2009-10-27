package org.overture.ide.vdmpp.ui.preferences;

import org.eclipse.dltk.ui.templates.ScriptTemplateAccess;
import org.eclipse.dltk.ui.templates.ScriptTemplatePreferencePage;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.jface.text.IDocument;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.vdmpp.ui.UIPlugin;
import org.overture.ide.vdmpp.ui.completions.VdmPpTemplateAccess;
import org.overture.ide.vdmpp.ui.internal.editor.IVdmPpPartitions;
import org.overture.ide.vdmpp.ui.internal.editor.SimpleVdmPpSourceViewerConfiguration;
import org.overture.ide.vdmpp.ui.internal.editor.VdmPpTextTools;

public class VdmPpCodeTemplatesPreferencePage extends ScriptTemplatePreferencePage {

	@Override
	protected ScriptSourceViewerConfiguration createSourceViewerConfiguration() {
		return new SimpleVdmPpSourceViewerConfiguration(
				getTextTools().getColorManager(), 
				getPreferenceStore(), 
				null,
				IVdmPpPartitions.VDMPP_PARTITIONING, 
				false);
	}

	@Override
	protected ScriptTemplateAccess getTemplateAccess() {
		return VdmPpTemplateAccess.getInstance();
	}

	@Override
	protected void setPreferenceStore() {
		setPreferenceStore(VdmUIPlugin.getDefault().getPreferenceStore());
		
	}

	private VdmPpTextTools getTextTools() {
		return UIPlugin.getDefault().getTextTools();
	}
	
	protected void setDocumentParticioner(IDocument document) {
		getTextTools().setupDocumentPartitioner(document, IVdmPpPartitions.VDMPP_PARTITIONING);
	}

	// TODO check if these
}
