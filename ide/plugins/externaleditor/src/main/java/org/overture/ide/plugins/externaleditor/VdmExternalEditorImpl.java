package org.overture.ide.plugins.externaleditor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.ui.editor.core.VdmExternalEditor;
import org.overture.ide.ui.editor.core.VdmSourceViewerConfiguration;
import org.overture.ide.vdmpp.ui.editor.core.VdmPpSourceViewerConfiguration;
import org.overture.ide.vdmrt.ui.editor.core.VdmRtSourceViewerConfiguration;
import org.overture.ide.vdmsl.ui.editor.core.VdmSlSourceViewerConfiguration;

public class VdmExternalEditorImpl extends VdmExternalEditor
{
	@Override
	public VdmSourceViewerConfiguration getVdmSourceViewerConfiguration()
	{
		return new VdmSlSourceViewerConfiguration();
	}
	
	@Override
	protected void doSetInput(IEditorInput input) throws CoreException
	{

		if (input instanceof FileEditorInput)
		{
			IFile file = ((FileEditorInput) input).getFile();
			
			IProject p =file.getProject();
			
			IVdmProject project = (IVdmProject) p.getAdapter(IVdmProject.class);
			
			VdmSourceViewerConfiguration config = null;
			switch (project.getDialect())
			{
				case VDM_PP:
					config = new VdmPpSourceViewerConfiguration();
					break;
				case VDM_RT:
					config = new VdmRtSourceViewerConfiguration();
					break;
				case VDM_SL:
					config = new VdmSlSourceViewerConfiguration();
					break;
				
			}
			super.fVdmSourceViewer = config;
			super.setSourceViewerConfiguration(config);
		}
		
		super.doSetInput(input);
	}
}
