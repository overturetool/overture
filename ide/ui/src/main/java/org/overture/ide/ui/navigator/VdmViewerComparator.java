package org.overture.ide.ui.navigator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.viewers.ViewerSorter;

public class VdmViewerComparator extends ViewerSorter {

	@Override
	public int category(Object element) {
		if (element instanceof IFile) {
			return 2;
		}
		if (element instanceof IFolder) {
			return 1;
		}
		return super.category(element);
	}

}
