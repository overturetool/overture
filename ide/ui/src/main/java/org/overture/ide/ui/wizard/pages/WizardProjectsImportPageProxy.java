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
package org.overture.ide.ui.wizard.pages;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.util.BundleUtility;
import org.osgi.framework.Bundle;
import org.overture.ide.ui.VdmUIPlugin;

@SuppressWarnings("restriction")
public class WizardProjectsImportPageProxy {
	String inputPath = "";
	private IWizardPage mainPage = null;

	public WizardProjectsImportPageProxy() {
		try {
			@SuppressWarnings("rawtypes")
			Class theClass = Class
					.forName("org.eclipse.ui.internal.wizards.datatransfer.WizardProjectsImportPage");
			mainPage = (IWizardPage) theClass.newInstance();
		} catch (Exception e) {
			VdmUIPlugin
					.log("Failed to create instance: org.eclipse.ui.internal.wizards.datatransfer.WizardProjectsImportPage",
							e);
		}
	}

	public IWizardPage getPage() {
		return this.mainPage;
	}

	public void performCancel() {
		try {
			invokeMainPageMethod("performCancel");
		} catch (NoSuchMethodException e) {
			VdmUIPlugin.log("Failed to invoke performCancel on WizardProjectsImportPage", e);
		} catch (SecurityException e) {
			VdmUIPlugin.log("Failed to invoke performCancel on WizardProjectsImportPage", e);
		} catch (IllegalAccessException e) {
			VdmUIPlugin.log("Failed to invoke performCancel on WizardProjectsImportPage", e);
		} catch (IllegalArgumentException e) {
			VdmUIPlugin.log("Failed to invoke performCancel on WizardProjectsImportPage", e);
		} catch (InvocationTargetException e) {
			VdmUIPlugin.log("Failed to invoke performCancel on WizardProjectsImportPage", e);
		}
	}

	public void performFinish() {
		this.createProjects();
	}

	public void createProjects() {
		try {
			invokeMainPageMethod("createProjects");
		} catch (NoSuchMethodException e) {
			VdmUIPlugin.log("Failed to invoke createProjects on WizardProjectsImportPage", e);
		} catch (SecurityException e) {
			VdmUIPlugin.log("Failed to invoke createProjects on WizardProjectsImportPage", e);
		} catch (IllegalAccessException e) {
			VdmUIPlugin.log("Failed to invoke createProjects on WizardProjectsImportPage", e);
		} catch (IllegalArgumentException e) {
			VdmUIPlugin.log("Failed to invoke createProjects on WizardProjectsImportPage", e);
		} catch (InvocationTargetException e) {
			VdmUIPlugin.log("Failed to invoke createProjects on WizardProjectsImportPage", e);
		}
	}

	/**
	 * This initializes the page with the graphical selection and sets the input
	 * path. The input path must be set prior to the call to this method
	 */
	public void createPageControlsPostconfig() {
		try {
			getMainPageButton("projectFromArchiveRadio").setSelection(true);
			getMainPageButton("projectFromArchiveRadio").setEnabled(false);
			getMainPageButton("projectFromDirectoryRadio").setSelection(false);
			getMainPageButton("projectFromDirectoryRadio").setEnabled(false);

			getMainPageButton("browseDirectoriesButton").setEnabled(false);
			getMainPageButton("browseArchivesButton").setEnabled(false);

			invokeMainPageMethod("archiveRadioSelected");

			Control pathfield = getMainPageField("archivePathField");
			if (pathfield instanceof Text) {
				((Text) pathfield).setText(this.inputPath);
			} else if (pathfield instanceof Combo) {
				((Combo) pathfield).setText(this.inputPath);
			}

			pathfield.setEnabled(false);
		} catch (Exception e) {
			VdmUIPlugin
					.log("Failed to configure throug reflection WizardProjectsImportPage",
							e);
		}
	}

	public void setBundleRelativeInputPath(String bundleId, String relativePath)
			throws IOException {
		URL examplesUrl = getResource(bundleId, relativePath);
		this.inputPath = FileLocator.resolve(examplesUrl).getPath();
		try
		{
			invokeMainPageMethod("updateProjectsList",this.inputPath);
		} catch (Exception e)
		{
			VdmUIPlugin.log("Failed to update project list from path",	e);
		}
	}

	public static URL getResource(String pluginId, String path) {
		// if the bundle is not ready then there is no image
		Bundle bundle = Platform.getBundle(pluginId);
		if (!BundleUtility.isReady(bundle)) {
			return null;
		}

		// look for the image (this will check both the plugin and fragment
		// folders
		URL fullPathString = BundleUtility.find(bundle, path);
		if (fullPathString == null) {
			try {
				fullPathString = new URL(path);
			} catch (MalformedURLException e) {
				return null;
			}
		}

		return fullPathString;

	}

	// utils
	private Button getMainPageButton(String field) throws NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		Field f = mainPage.getClass().getDeclaredField(field);
		f.setAccessible(true);
		return (Button) f.get(mainPage);
	}

	private Control getMainPageField(String field) throws NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		Field f = mainPage.getClass().getDeclaredField(field);
		f.setAccessible(true);
		return (Control) f.get(mainPage);
	}

	private Object invokeMainPageMethod(String method, Object... args)
			throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Method m = null;

		if (args.length == 0) {
			m = mainPage.getClass().getDeclaredMethod(method);
			args = null;
		} else {
			List<Class<?>> parameterTypes = new ArrayList<Class<?>>();
			for (Object object : args) {
				if(object instanceof Boolean)
				{
					parameterTypes.add(boolean.class);
				}else{
				parameterTypes.add(object.getClass());
				}
			}
			try{
				m = mainPage.getClass().getMethod(method,
						parameterTypes.toArray(new Class<?>[] {}));
			}catch(NoSuchMethodException e)
			{;}
			if(m==null)
			{
			m = mainPage.getClass().getDeclaredMethod(method,
					parameterTypes.toArray(new Class<?>[] {}));
			}
		}
		m.setAccessible(true);
		return m.invoke(mainPage, args);
	}

	private Object invokeMainPageMethod(String method)
			throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		return invokeMainPageMethod(method, new Object[] {});
	}
}
