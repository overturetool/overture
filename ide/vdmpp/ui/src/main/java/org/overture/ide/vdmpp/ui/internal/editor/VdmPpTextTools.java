package org.overture.ide.vdmpp.ui.internal.editor;

import org.eclipse.dltk.ui.editor.highlighting.SemanticHighlighting;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.ui.texteditor.ITextEditor;
import org.overture.ide.vdmpp.ui.internal.partitioning.VdmPpPartitionScanner;

public class VdmPpTextTools extends ScriptTextTools
{
	private IPartitionTokenScanner fPartitionScanner;
	private final static String[] LEGAL_CONTENT_TYPES = new String[] {
			IVdmPpPartitions.VDMPP_STRING, IVdmPpPartitions.VDMPP_COMMENT };

	public VdmPpTextTools(boolean autoDisposeOnDisplayDispose)
	{
		super(IVdmPpPartitions.VDMPP_PARTITIONING, LEGAL_CONTENT_TYPES,
				autoDisposeOnDisplayDispose);
		fPartitionScanner = new VdmPpPartitionScanner();
	}

	@Override
	public ScriptSourceViewerConfiguration createSourceViewerConfiguraton(
			IPreferenceStore preferenceStore, ITextEditor editor,
			String partitioning)
	{
		return new VdmPpSourceViewerConfiguration(getColorManager(),
				preferenceStore, editor, partitioning);
	}

	@Override
	public SemanticHighlighting[] getSemanticHighlightings()
	{
		return new SemanticHighlighting[0];
	}

	@Override
	public IPartitionTokenScanner getPartitionScanner()
	{
		return fPartitionScanner;
	}

}
