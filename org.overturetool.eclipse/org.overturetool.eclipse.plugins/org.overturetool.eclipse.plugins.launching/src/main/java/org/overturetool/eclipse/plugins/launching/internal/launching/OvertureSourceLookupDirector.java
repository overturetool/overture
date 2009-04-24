package org.overturetool.eclipse.plugins.launching.internal.launching;

import java.io.File;
import java.net.URI;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IPersistableSourceLocator;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.dltk.internal.debug.core.model.ScriptStackFrame;


//public class TclSourceLookupDirector extends AbstractSourceLookupDirector {
//	public void initializeParticipants() {
//		addParticipants(new ISourceLookupParticipant[]{new TclSourceLookupParticipant()});				
//	}
//}

public class OvertureSourceLookupDirector implements IPersistableSourceLocator {
		
	public OvertureSourceLookupDirector() {
	}

	public Object getSourceElement(IStackFrame stackFrame) {
		if (stackFrame instanceof ScriptStackFrame) {
			ScriptStackFrame sf = (ScriptStackFrame) stackFrame;
			URI uri = sf.getSourceURI();

			String pathname = uri.getPath();
			if (Platform.getOS().equals(Platform.OS_WIN32)) {
				pathname = pathname.substring(1);
			}

			File file = new File(pathname);

			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();			
			IContainer container = root
					.getContainerForLocation(new Path(file.getParent()));

			if (container != null) {
				IResource resource = container.findMember(file.getName());

				if (resource instanceof IFile) {
					return (IFile) resource;
				}
			} else {
				return file;
			}
		}

		return null;
	}

	public String getMemento() throws CoreException {
		return null;
	}

	public void initializeDefaults(ILaunchConfiguration configuration)
			throws CoreException {

	}

	public void initializeFromMemento(String memento) throws CoreException {

	}
}
