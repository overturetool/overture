package org.overture.ide.vdmsl.ui.internal.editor;

import org.eclipse.dltk.ui.editor.highlighting.SemanticHighlighting;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.ui.texteditor.ITextEditor;
import org.overture.ide.vdmsl.ui.internal.partitioning.VdmSlPartitionScanner;

public class VdmSlTextTools extends ScriptTextTools
{
	private IPartitionTokenScanner fPartitionScanner;
	private final static String[] LEGAL_CONTENT_TYPES = new String[] {
			IVdmSlPartitions.VDMSL_STRING, IVdmSlPartitions.VDMSL_COMMENT };

	public VdmSlTextTools(boolean autoDisposeOnDisplayDispose)
	{
		super(IVdmSlPartitions.VDMSL_PARTITIONING, LEGAL_CONTENT_TYPES,
				autoDisposeOnDisplayDispose);
		fPartitionScanner = new VdmSlPartitionScanner();
	}

	@Override
	public ScriptSourceViewerConfiguration createSourceViewerConfiguraton(
			IPreferenceStore preferenceStore, ITextEditor editor,
			String partitioning)
	{
		return new VdmSlSourceViewerConfiguration(getColorManager(),
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
