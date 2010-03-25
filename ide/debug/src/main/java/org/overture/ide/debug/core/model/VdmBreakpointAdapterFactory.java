package org.overture.ide.debug.core.model;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.texteditor.ITextEditor;

public class VdmBreakpointAdapterFactory implements IAdapterFactory {

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof ITextEditor) {
	          ITextEditor editorPart = (ITextEditor) adaptableObject;
	          IResource resource = (IResource) editorPart.getEditorInput().getAdapter(IResource.class);
	          if (resource != null) {
	             String extension = resource.getFileExtension();
	             if (extension != null && extension.equals("vdmpp")) {
	             return new VdmLineBreakpointAdapter();
	             }
	          } 
	       }
	       return null;
	}

	public Class[] getAdapterList() {
		// TODO Auto-generated method stub
		return null;
	}

}
