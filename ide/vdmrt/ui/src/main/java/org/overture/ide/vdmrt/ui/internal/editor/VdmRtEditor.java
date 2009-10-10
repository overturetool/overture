package org.overture.ide.vdmrt.ui.internal.editor;

import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.ui.IEditorInput;
import org.overture.ide.vdmrt.ui.UIPlugin;
import org.overture.ide.vdmsl.core.VdmSlLanguageToolkit;


public class VdmRtEditor  extends ScriptEditor
{

	@Override
	public String getEditorId()
	{
		// TODO Auto-generated method stub
		return VdmRtEditorConstants.EDITOR_ID;
	}

	@Override
	protected void initializeEditor()
	{
		super.initializeEditor();
		setEditorContextMenuId(VdmRtEditorConstants.EDITOR_CONTEXT);

	}

	@Override
	public ScriptTextTools getTextTools()
	{
		return UIPlugin.getDefault().getTextTools();
	}

	@Override
	protected IPreferenceStore getScriptPreferenceStore()
	{
		return UIPlugin.getDefault().getPreferenceStore();
	}

	@Override
	public IDLTKLanguageToolkit getLanguageToolkit()
	{
		return VdmSlLanguageToolkit.getDefault();
	}

	@Override
	protected void connectPartitioningToElement(IEditorInput input,
			IDocument document)
	{
		if (document instanceof IDocumentExtension3)
		{
			IDocumentExtension3 extension = (IDocumentExtension3) document;
			if (extension.getDocumentPartitioner(IVdmRtPartitions.vdmrt_PARTITIONING) == null)
			{
				VdmRtTextTools tools = UIPlugin.getDefault().getTextTools();
				tools.setupDocumentPartitioner(
						document,
						IVdmRtPartitions.vdmrt_PARTITIONING);
			}
		}
	}
	
	

}
