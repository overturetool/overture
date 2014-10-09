/*
 * #%~
 * org.overture.ide.ui
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.ui.wizard;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.PlatformUI;
import org.overture.ide.ui.VdmUIPlugin;

public abstract class VdmNewFileWizard extends Wizard implements
		IWorkbenchWizard
{

	private static final String WIZARD_NAME = "VDM New File Wizard";

	private WizardNewFileCreationPage _pageOne;
	private String fPageName;
	private String fPageTitle;
	private String fPageDescription;
	private IStructuredSelection fStructuredSelection;

	public VdmNewFileWizard()
	{
		setWindowTitle(WIZARD_NAME);
		this.fPageName = getPageName();
		this.fPageTitle = getPageTitle();
		this.fPageDescription = getPageDescription();
	}

	@Override
	public void addPages()
	{
		super.addPages();
		_pageOne = new WizardNewFileCreationPage(this.fPageName, this.fStructuredSelection);
		_pageOne.setFileExtension(getFileExtension());
		_pageOne.setTitle(this.fPageTitle);
		_pageOne.setDescription(this.fPageDescription);

		addPage(_pageOne);

	}

	/*
	 * Gets the main page name
	 */
	protected abstract String getPageName();

	/*
	 * Gets the main page title to be displayed
	 */
	protected abstract String getPageTitle();

	/*
	 * Gets the main page description
	 */
	protected abstract String getPageDescription();

	/*
	 * Gets the file extension of the file to create
	 */
	protected abstract String getFileExtension();

	/*
	 * Gets the file template or null if none is provided
	 */
	protected String getFileTemplate(String fileName)
	{
		return null;
	}

	@Override
	public boolean canFinish()
	{
		return super.canFinish() && _pageOne.getErrorMessage() == null;
	}

	@Override
	public boolean performFinish()
	{
		IFile file = _pageOne.createNewFile();
		if (file.exists())
		{
			String fileName = file.getName();
			if (fileName.contains("."))
			{
				fileName = fileName.substring(0, fileName.indexOf("."));
			}

			boolean isClean = false;
			InputStream in =null;
			try
			{
				in= file.getContents();
				if (file.getContents().read() == -1)
				{
					isClean = true;
				}
			} catch (IOException e)
			{
			} catch (CoreException e)
			{
			}finally
			{
				if(in!=null)
				{
					try
					{
						in.close();
					} catch (IOException e)
					{
					}
				}
			}

			if (isClean)
			{
				String fileTemplate = getFileTemplate(fileName);
				if (fileTemplate != null)
				{
					applyTemplate(file, fileTemplate);
				}
			}

		}
		try
		{
			IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), file, true);
			file.touch(null);
			file.refreshLocal(IResource.DEPTH_ONE, null);
		} catch (CoreException e)
		{
			if (VdmUIPlugin.DEBUG)
			{
				e.printStackTrace();
			}
		}
		return true;
	}

	private void applyTemplate(IFile file, String fileTemplate)
	{
		InputStream stream;
		try
		{
			stream = new ByteArrayInputStream(fileTemplate.getBytes());
			file.setContents(stream, IFile.FORCE, null);
		} catch (CoreException e)
		{
			if (VdmUIPlugin.DEBUG)
			{
				e.printStackTrace();
			}

		}

	}

	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		this.fStructuredSelection = selection;
	}

}
