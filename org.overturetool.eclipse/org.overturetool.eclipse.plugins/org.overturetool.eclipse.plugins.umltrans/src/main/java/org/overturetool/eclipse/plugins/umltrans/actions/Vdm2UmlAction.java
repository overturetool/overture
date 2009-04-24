package org.overturetool.eclipse.plugins.umltrans.actions;

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
import org.overturetool.umltrans.Main.*;



import javax.swing.filechooser.FileFilter;

/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class Vdm2UmlAction implements IWorkbenchWindowActionDelegate
{
	private IWorkbenchWindow window;

	/**
	 * The constructor.
	 */
	public Vdm2UmlAction()
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
		{ "*.vpp", "*.tex" };
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
					files[i]= fd.getFilterPath()+ separator +fLst[i];
				}
				
				
			
				
				org.eclipse.swt.widgets.FileDialog	fdSave = new org.eclipse.swt.widgets.FileDialog(s, SWT.SAVE);
				fdSave.setText("Save model");
				// fd.setFilterPath("C:/");
				String[] filterExt1 =
				{ "*.xml", "*.xmi"};
				fdSave.setFilterExtensions(filterExt1);
				fdSave.setFileName(files[0]+".xml");
				String outFile = fdSave.open();
				if (outFile != null)
				{
					// convert to vpp files the only format supported by the
					// overture parser
					//String[] vppFiles = ClassExstractorFromTexFiles.exstract(files);

					if (!(outFile.endsWith(".xml") || outFile.endsWith(".xml")))
						outFile += ".xml";
					String tmp = new Translator().TransLateTexVdmToUml(files, outFile);

					MessageDialog.openInformation(window.getShell(), "Vdm 2 Uml", "Processing completed: " + outFile);

				}

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

class ExtensionFileFilter extends FileFilter
{
	String description;

	String extensions[];

	public ExtensionFileFilter(String description, String extension)
	{
		this(description, new String[]
		{ extension });
	}

	public ExtensionFileFilter(String description, String extensions[])
	{
		if (description == null)
		{
			this.description = extensions[0];
		} else
		{
			this.description = description;
		}
		this.extensions = (String[]) extensions.clone();
		toLower(this.extensions);
	}

	private void toLower(String array[])
	{
		for (int i = 0, n = array.length; i < n; i++)
		{
			array[i] = array[i].toLowerCase();
		}
	}

	public String getDescription()
	{
		return description;
	}

	public boolean accept(File file)
	{
		if (file.isDirectory())
		{
			return true;
		} else
		{
			String path = file.getAbsolutePath().toLowerCase();
			for (int i = 0, n = extensions.length; i < n; i++)
			{
				String extension = extensions[i];
				if ((path.endsWith(extension) && (path.charAt(path.length() - extension.length() - 1)) == '.'))
				{
					return true;
				}
			}
		}
		return false;
	}
}
