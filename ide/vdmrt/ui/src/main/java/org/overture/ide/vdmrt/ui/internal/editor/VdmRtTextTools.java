package org.overture.ide.vdmrt.ui.internal.editor;

import org.eclipse.dltk.ui.editor.highlighting.SemanticHighlighting;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.ui.texteditor.ITextEditor;
import org.overture.ide.vdmrt.ui.internal.partitioning.VdmRtPartitionScanner;

public class VdmRtTextTools extends ScriptTextTools
{
	private IPartitionTokenScanner fPartitionScanner;
	private final static String[] LEGAL_CONTENT_TYPES = new String[] {
			IVdmRtPartitions.vdmrt_STRING, IVdmRtPartitions.vdmrt_COMMENT };

	public VdmRtTextTools(boolean autoDisposeOnDisplayDispose)
	{
		super(IVdmRtPartitions.vdmrt_PARTITIONING, LEGAL_CONTENT_TYPES,
				autoDisposeOnDisplayDispose);
		fPartitionScanner = new VdmRtPartitionScanner();
	}

	@Override
	public ScriptSourceViewerConfiguration createSourceViewerConfiguraton(
			IPreferenceStore preferenceStore, ITextEditor editor,
			String partitioning)
	{
		return new VdmRtSourceViewerConfiguration(getColorManager(),
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
