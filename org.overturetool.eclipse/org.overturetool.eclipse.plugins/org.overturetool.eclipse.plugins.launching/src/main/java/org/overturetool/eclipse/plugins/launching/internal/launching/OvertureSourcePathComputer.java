package org.overturetool.eclipse.plugins.launching.internal.launching;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate;


public class OvertureSourcePathComputer implements ISourcePathComputerDelegate {
	
	public OvertureSourcePathComputer(){		
	}

	public ISourceContainer[] computeSourceContainers(ILaunchConfiguration configuration, IProgressMonitor monitor) throws CoreException {		
		//configuration -> system libs
		//main script name
		
//		String mainScript = configuration.getAttribute(
//				IDLTKLaunchConfigurationConstants.ATTR_MAIN_SCRIPT_NAME,
//				(String) null);
		
		
		List<Object> containers = new ArrayList<Object>(); 
		
		//IContainer c = ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(new Path(mainScript));

//		if (c != null){
//			IContainer container = c.getParent();
//			
//			containers.add(new DirectorySourceContainer());
//			if (container.getType() == IResource.PROJECT) {
//				containers.add(new ProjectSourceContainer((IProject)container, false));
//			} else if (container.getType() == IResource.FOLDER) {
//				containers.add(new FolderSourceContainer(container, false));
//			}
//		}	
//	
  		return (ISourceContainer[])containers.toArray(new ISourceContainer[containers.size()]);
	}
}
