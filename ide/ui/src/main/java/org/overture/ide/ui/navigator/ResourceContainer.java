package org.overture.ide.ui.navigator;

import org.eclipse.core.resources.IFolder;

public class ResourceContainer {

	private IFolder fFolder;
	
	public ResourceContainer(IFolder folder) {
		fFolder = folder;
	}

	

	public IFolder getFolder() {
		return fFolder;
	}
}
