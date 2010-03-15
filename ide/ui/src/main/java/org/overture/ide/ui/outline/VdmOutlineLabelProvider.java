package org.overture.ide.ui.outline;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

public class VdmOutlineLabelProvider  implements ILabelProvider
{

	public Image getImage(Object element)
	{
		return DisplayImageCreator.getImage(element);
		// PlatformUI.getWorkbench().getSharedImages().getImage(org.eclipse.dltk.ui.ScriptElementImageProvider ISharedImages..IMG_OBJ_FOLDER);
		//return null;
	}

	public String getText(Object element)
	{
		return DisplayNameCreator.getDisplayName(element);
	}

	public void addListener(ILabelProviderListener listener)
	{
		// TODO Auto-generated method stub
		
	}

	public void dispose()
	{
		// TODO Auto-generated method stub
		
	}

	public boolean isLabelProperty(Object element, String property)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void removeListener(ILabelProviderListener listener)
	{
		// TODO Auto-generated method stub
		
	}
}
