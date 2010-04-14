package org.overture.ide.ui.navigator;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;

public class SourceContainer  {

	private IFolder fFolder;
	
	
	
	public SourceContainer(IFolder folder) {
		setfFolder(folder);
	}



	public void setfFolder(IFolder fFolder) {
		this.fFolder = fFolder;
	}



	public IFolder getFolder() {
		return fFolder;
	}



	public String getText() {
		IPath path = fFolder.getProjectRelativePath();
		String[] segments = path.segments();
		StringBuffer s = new StringBuffer();
		
		for(int i=0; i < segments.length-1;i++){
			s.append(segments[i]);
			s.append('.');
		}
		s.append(segments[segments.length-1]);
		return s.toString();
		
	}
	
	
}
