package org.overturetool.eclipse.plugins.umltransplugin.actions;

import java.io.File;

import javax.swing.JFileChooser;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.jface.dialogs.MessageDialog;
import org.overturetool.tex.ClassExstractorFromTexFiles;

import javax.swing.filechooser.FileFilter;

/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class TexToVppAction implements IWorkbenchWindowActionDelegate
{
	private IWorkbenchWindow window;

	/**
	 * The constructor.
	 */
	public TexToVppAction()
	{
	}

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action)
	{

		org.eclipse.swt.widgets.Shell s = new org.eclipse.swt.widgets.Shell();

		org.eclipse.swt.widgets.FileDialog fd = new org.eclipse.swt.widgets.FileDialog(s, SWT.MULTI);
		fd.setText("Open");

		String[] filterExt =
		{ "*.tex" };
		fd.setFilterExtensions(filterExt);
		String ret = fd.open();
		String[] fLst = fd.getFileNames();
		if (ret != null)
		{

			try
			{
				String[] files = new String[fLst.length];
				for (int i = 0; i < fLst.length; i++)
				{
					String separator = System.getProperty("file.separator");
					files[i] = fd.getFilterPath() + separator+ fLst[i];
				}

				String[] vppFiles = ClassExstractorFromTexFiles.exstract(files);
				String outFile = "";
				for (int i = 0; i < vppFiles.length; i++)
				{
					outFile += "\n" + vppFiles[i];
				}
				MessageDialog.openInformation(window.getShell(), "Tex 2 vpp", "Processing completed: " + outFile);

			} catch (Exception ex)
			{
				System.err.println(ex.getMessage() + ex.getStackTrace());
				MessageDialog.openInformation(window.getShell(), "Error", "Processing completed with errors");
			}

		}

	}

	/**
	 * Selection in the workbench has been changed. We can change the state of
	 * the 'real' action here if we want, but this can only happen after the
	 * delegate has been created.
	 * 
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
	}

	/**
	 * We can use this method to dispose of any system resources we previously
	 * allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose()
	{
	}

	/**
	 * We will cache window object in order to be able to provide parent shell
	 * for the message dialog.
	 * 
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window)
	{
		this.window = window;
	}
}

