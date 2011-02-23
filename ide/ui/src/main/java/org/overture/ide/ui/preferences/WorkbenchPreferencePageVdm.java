package org.overture.ide.ui.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class WorkbenchPreferencePageVdm extends PreferencePage implements
		IWorkbenchPreferencePage
{

	public WorkbenchPreferencePageVdm()
	{

	}

	public WorkbenchPreferencePageVdm(String title)
	{
		super(title);

	}

	public WorkbenchPreferencePageVdm(String title, ImageDescriptor image)
	{
		super(title, image);

	}

	@Override
	protected Control createContents(Composite parent)
	{

		return null;
	}

	public void init(IWorkbench workbench)
	{

	}

}
