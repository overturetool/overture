package org.overture.ide.debug.ui.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.ui.texteditor.ITextEditor;
import org.overture.ide.core.resources.IVdmProject;

public class VdmBreakpointAdapterFactory implements IAdapterFactory
{

	@SuppressWarnings("unchecked")
	public Object getAdapter(Object adaptableObject, Class adapterType)
	{
		if (adaptableObject instanceof ITextEditor)
		{
			ITextEditor editorPart = (ITextEditor) adaptableObject;
			IResource resource = (IResource) editorPart.getEditorInput()
					.getAdapter(IResource.class);
			if (resource != null && resource instanceof IFile)
			{
				IFile file = (IFile) resource;
				try
				{
					if(file==null || !file.exists() || !file.isSynchronized(IResource.DEPTH_ZERO))
					{
						return null;
					}
					IVdmProject project = (IVdmProject) file.getProject().getAdapter(IVdmProject.class);
					if(project != null && project.isModelFile(file))
					{
						return new VdmLineBreakpointAdapter();
					}
//					IContentDescription contentDesc = file.getContentDescription();
//					if(contentDesc == null){
//						return null;
//					}
//					String contentTypeId = contentDesc.getContentType().getId();
//					if(SourceViewerEditorManager.getInstance().getContentTypeIds().contains(contentTypeId))
//					{
//						return new VdmLineBreakpointAdapter();
//					}
				} catch (CoreException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Class[] getAdapterList()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
