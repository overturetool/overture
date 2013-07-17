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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.util.BundleUtility;
import org.osgi.framework.Bundle;
import org.overture.ide.ui.VdmUIPlugin;

@SuppressWarnings("restriction")
public class WizardProjectsImportPageProxy
{
	String inputPath = "";
	private IWizardPage mainPage = null;

	public WizardProjectsImportPageProxy()
	{
		try
		{
			@SuppressWarnings("rawtypes")
			Class theClass = Class.forName("org.eclipse.ui.internal.wizards.datatransfer.WizardProjectsImportPage");
			mainPage = (IWizardPage) theClass.newInstance();
		} catch (Exception e)
		{
			 VdmUIPlugin.logErrorMessage("Failed to create instance: org.eclipse.ui.internal.wizards.datatransfer.WizardProjectsImportPage");
		}
	}

	public IWizardPage getPage()
	{
		return this.mainPage;
	}

	public void performCancel()
	{
		try
		{
			invokeMainPageMethod("performCancel");
		} catch (NoSuchMethodException | SecurityException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e)
		{
			VdmUIPlugin.logErrorMessage("Failed to invoke performCancel on WizardProjectsImportPage");
		}
	}
	
	public void performFinish()
	{
		this.createProjects();
	}

	public void createProjects()
	{
		try
		{
			invokeMainPageMethod("createProjects");
		} catch (NoSuchMethodException | SecurityException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e)
		{
			VdmUIPlugin.logErrorMessage("Failed to invoke createProjects on WizardProjectsImportPage");
		}
	}

	/**
	 * This initializes the page with the graphical selection and sets the input path. The input path must be set prior
	 * to the call to this method
	 */
	public void createPageControlsPostconfig()
	{
		try
		{
			getMainPageButton("projectFromArchiveRadio").setSelection(true);
			getMainPageButton("projectFromArchiveRadio").setEnabled(false);
			getMainPageButton("projectFromDirectoryRadio").setSelection(false);
			getMainPageButton("projectFromDirectoryRadio").setEnabled(false);

			getMainPageButton("browseDirectoriesButton").setEnabled(false);
			getMainPageButton("browseArchivesButton").setEnabled(false);

			invokeMainPageMethod("archiveRadioSelected");

			getMainPageText("archivePathField").setText(this.inputPath);
			getMainPageText("archivePathField").setEnabled(false);
		} catch (Exception e)
		{
			VdmUIPlugin.logErrorMessage("Failed to configure throug reflection WizardProjectsImportPage");
		}
	}

	public void setBundleRelativeInputPath(String bundleId, String relativePath)
			throws IOException
	{
		URL examplesUrl = getResource(bundleId, relativePath);
		this.inputPath = FileLocator.resolve(examplesUrl).getPath();
	}

	public static URL getResource(String pluginId, String path)
	{
		// if the bundle is not ready then there is no image
		Bundle bundle = Platform.getBundle(pluginId);
		if (!BundleUtility.isReady(bundle))
		{
			return null;
		}

		// look for the image (this will check both the plugin and fragment
		// folders
		URL fullPathString = BundleUtility.find(bundle, path);
		if (fullPathString == null)
		{
			try
			{
				fullPathString = new URL(path);
			} catch (MalformedURLException e)
			{
				return null;
			}
		}

		return fullPathString;

	}

	// utils
	private Button getMainPageButton(String field) throws NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException
	{
		Field f = mainPage.getClass().getDeclaredField(field);
		f.setAccessible(true);
		return (Button) f.get(mainPage);
	}

	private Text getMainPageText(String field) throws NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException
	{
		Field f = mainPage.getClass().getDeclaredField(field);
		f.setAccessible(true);
		return (Text) f.get(mainPage);
	}

	private Object invokeMainPageMethod(String method, Object... args)
			throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException
	{
		Method m = null;

		if (args.length == 0)
		{
			m = mainPage.getClass().getDeclaredMethod(method);
			args = null;
		} else
		{
			List<Class<?>> parameterTypes = new Vector<Class<?>>();
			for (Object object : args)
			{
				parameterTypes.add(object.getClass());
			}
			m = mainPage.getClass().getDeclaredMethod(method, parameterTypes.toArray(new Class<?>[] {}));
		}
		m.setAccessible(true);
		return m.invoke(mainPage, args);
	}

	private Object invokeMainPageMethod(String method)
			throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException
	{
		return invokeMainPageMethod(method, new Object[] {});
	}
}
