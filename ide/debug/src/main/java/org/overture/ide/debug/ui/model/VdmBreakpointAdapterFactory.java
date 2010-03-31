package org.overture.ide.debug.ui.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.texteditor.ITextEditor;

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
					String contentTypeId = file.getContentDescription().getContentType().getId();
					if(SourceViewerEditorManager.getInstance().getContentTypeIds().contains(contentTypeId))
					{
						return new VdmLineBreakpointAdapter();
					}
				} catch (CoreException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				String extension = resource.getFileExtension();
//				if (extension != null && extension.equals("vdmpp"))
//				{
//					return new VdmLineBreakpointAdapter();
//				}
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
