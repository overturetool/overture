/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.ide.plugins.showtraceNextGen.viewer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.VDMRunTimeException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.overture.ide.core.utility.FileUtility;
import org.overture.ide.ui.internal.util.ConsoleWriter;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenRTLogger;
import org.overturetool.traceviewer.ast.itf.IOmlTraceFile;
import org.overturetool.traceviewer.parser.TraceParser;

public class VdmRtLogEditor extends EditorPart implements IViewCallback
{
	static final boolean $assertionsDisabled = false;// !org/overturetool/tracefile/viewer/TracefileViewer.desiredAssertionStatus();
	private static final ConsoleWriter cw = new ConsoleWriter("RT Log viewer");

	private File selectedFile;
	private Display display;

	private SashForm form;
	private TabFolder folder;
	private ValidationTable theConjectures;
	private GenericTabItem theArch;
	private GenericTabItem theOverview;
	private HashSet<GenericTabItem> theDetails;
	private String fileName;
	private Vector<Long> theTimes;
	private long currentTime;

	private boolean canExportJpg = false;
	private boolean canMoveHorizontal = false;
	private boolean canOpenValidation = false;

	private TracefileVisitor theVisitor;
	private TracefileMarker theMarkers;

	public VdmRtLogEditor()
	{
		theConjectures = null;
		theArch = null;
		theOverview = null;
		theDetails = new HashSet<GenericTabItem>();
		fileName = null;
		theTimes = null;
		currentTime = 0L;
		theVisitor = null;
		theMarkers = null;
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException
	{
		setSite(site);
		setInput(input);
		this.display = site.getShell().getDisplay();

		IPath path = ((IPathEditorInput) input).getPath();

		selectedFile = path.toFile();
		fileName = selectedFile.getAbsolutePath();
	}

	@Override
	public void createPartControl(Composite parent)
	{
		Control[] childern = parent.getChildren();
		for (Control control : childern)
		{
			control.setVisible(false);
		}
		form = new SashForm(parent, 512);
		form.setLayout(new FillLayout());
		folder = new TabFolder(form, 128);
		theConjectures = new ValidationTable(form, this);
		form.setWeights(new int[] { 85, 15 });
		theArch = new GenericTabItem("Architecture overview", folder, null);
		theOverview = new GenericTabItem("Execution overview", folder, null);
		cw.clear();
		try
		{

			IFile file = ((FileEditorInput) getEditorInput()).getFile();

			FileUtility.deleteMarker(file, null, TracefileViewerPlugin.PLUGIN_ID);

			theMarkers = new TracefileMarker(file);

			if (FileUtility.getContent(file).size() == 0)
			{
				// FileUtility.addMarker(file, "File is empty", 0, 0, 0, 0, IMarker.SEVERITY_ERROR,
				// TracefileViewerPlugin.PLUGIN_ID);
				ErrorDialog.openError(getSite().getShell(), "Editor open", "File is empty", Status.CANCEL_STATUS);
				return;
			}
		} catch (CGException cge)
		{
			showMessage(cge);
		}
		// makeActions();
		// contributeToActionBars();
		catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try
		{
			parseFile(selectedFile.getAbsolutePath());
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	// private void makeActions()
	// {
	// fileOpenAction = new Action()
	// {
	// @Override
	// public void run()
	// {
	// openFileAction();
	// }
	// };
	// fileOpenAction.setText("Open trace file");
	// fileOpenAction.setToolTipText("Open trace file");
	// fileOpenAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor("IMG_OBJ_FILE"));
	// }

	void openValidationConjectures()
	{
		FileDialog fDlg = new FileDialog(getSite().getShell());
		String valFileName = fDlg.open();
		theConjectures.parseValidationFile(valFileName);
	}

	// private void openFileAction()
	// {
	// if (fileName != null)
	// deleteTabPages();
	// if (!$assertionsDisabled && theVisitor != null)
	// {
	// throw new AssertionError();
	// } else
	// {
	// FileDialog fDlg = new FileDialog(getSite().getShell());
	// fileName = fDlg.open();
	// parseFile(fileName);
	// return;
	// }
	// }

	void diagramExportAction()
	{
		// if(fileName != null)
		// {
		theArch.exportJPG((new StringBuilder(String.valueOf(fileName))).append(".arch").toString());
		theOverview.exportJPG((new StringBuilder(String.valueOf(fileName))).append(".overview").toString());
		GenericTabItem pgti;
		for (Iterator<GenericTabItem> iter = theDetails.iterator(); iter.hasNext(); pgti.exportJPG((new StringBuilder(String.valueOf(fileName))).append(".").append(pgti.getName()).toString()))
			pgti = iter.next();

		// showMessage("Diagrams generated!");
		// } else
		// {
		// showMessage("Please open a trace file first!");
		// }
	}

	void moveHorizontal()
	{
		SelectTimeDialog theDialog = new SelectTimeDialog(folder.getShell(), theTimes, currentTime);
		if (theDialog.open() == 0 && theDialog.selectedTime != currentTime)
		{
			currentTime = theDialog.selectedTime;
			updateOverviewPage();
		}
	}

	void moveNextHorizontal()
	{
		int index = theTimes.indexOf(currentTime);

		if (index + 1 < theTimes.size())
		{
			currentTime = theTimes.get(index + 1);
			updateOverviewPage();
		}
	}

	void movePreviousHorizontal()
	{
		int index = theTimes.indexOf(currentTime);

		if (index - 1 >= 0)
		{
			currentTime = theTimes.get(index - 1);
			updateOverviewPage();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.ide.plugins.showtraceNextGen.viewer.IViewCallback#panToTime(long, long)
	 */
	public void panToTime(long time, long thrid)
	{
		for (Iterator<Long> iter = theTimes.iterator(); iter.hasNext();)
		{
			long theTime = iter.next().longValue();
			if (theTime < time)
				currentTime = theTime;
		}

		updateOverviewPage();
	}

	public void addLowerError(Long x1, Long x2, String name)
	{
		if (theVisitor != null)
			try
			{
				theVisitor.addFailedLower(x1, x2, name);
			} catch (CGException cge)
			{
				showMessage(cge);
			}
	}

	public void addUpperError(Long x1, Long x2, String name)
	{
		if (theVisitor != null)
			try
			{
				theVisitor.addFailedUpper(x1, x2, name);
			} catch (CGException cge)
			{
				showMessage(cge);
			}
	}

	private void parseFile(final String fname)
	{

		Shell shell = super.getSite().getShell();

		try
		{
			IRunnableWithProgress op = new IRunnableWithProgress()
			{

				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException
				{
					doParse(fname, monitor);
				}

			};
			new ProgressMonitorDialog(shell).run(false, true, op);
		} catch (InvocationTargetException e)
		{
			e.printStackTrace();

		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	private void doParse(final String fname, IProgressMonitor monitor)
	{
		NextGenTraceParser t = new NextGenTraceParser(fname);
		t.start();

		while (!t.isFinished())
		{
			if (monitor.isCanceled())
			{
				try
				{
					t.stop();
				} catch (Exception e)
				{

				}
			}
		}

		if (t.error != null)
		{
			showMessage(t.error.getMessage());
		}		
		else if (t.rtLogger != null)
		{
			//TODO MAA: Add showmessage status
			theVisitor = new TracefileVisitor();
			getSite().getShell().getDisplay().asyncExec(new Runnable()
			{
				public void run()
				{
					createTabPages();
				}

			});
			
		} else
		{
			showMessage("Unable to display log data. RT Logger is unset");
		}
	}

 
	@SuppressWarnings("unchecked")
	private void createTabPages()
	{
		try
		{
			theTimes = theVisitor.getAllTimes();
			theVisitor.drawArchitecture(theArch);
			theVisitor.drawOverview(theOverview, new Long(currentTime));
			canExportJpg = true;
			canMoveHorizontal = true;
			canOpenValidation = true;
			Vector<tdCPU> theCpus = theVisitor.getCpus();
			GenericTabItem theDetail;
			for (Iterator<tdCPU> iter = theCpus.iterator(); iter.hasNext(); theDetails.add(theDetail))
			{
				tdCPU theCpu = iter.next();
				theDetail = new GenericTabItem(theCpu.getName(), folder, theCpu);
				theVisitor.drawCpu(theDetail, new Long(currentTime));
			}
		} catch (VDMRunTimeException e)
		{
			e.printStackTrace();
			showMessage(e);

		} catch (CGException cge)
		{
			cge.printStackTrace();
			showMessage(cge);
		}

	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.ide.plugins.showtraceNextGen.viewer.IViewCallback#updateOverviewPage ()
	 */
	public void updateOverviewPage()
	{
		try
		{
			theOverview.disposeFigures();
			theVisitor.drawOverview(theOverview, new Long(currentTime));
			GenericTabItem theDetail;
			for (Iterator<GenericTabItem> iter = theDetails.iterator(); iter.hasNext(); theVisitor.drawCpu(theDetail, new Long(currentTime)))
			{
				theDetail = iter.next();
				theDetail.disposeFigures();
			}

		} catch (CGException cge)
		{
			showMessage(cge);
		}
	}

	// private void deleteTabPages()
	// {
	// folder.setSelection(0);
	// canExportJpg = false;
	// canMoveHorizontal = false;
	// canOpenValidation = false;
	// GenericTabItem pgti;
	// for (Iterator<GenericTabItem> iter = theDetails.iterator(); iter.hasNext(); pgti.dispose())
	// pgti = iter.next();
	//
	// theDetails = new HashSet<GenericTabItem>();
	// theArch.disposeFigures();
	// theOverview.disposeFigures();
	// fileName = null;
	// theVisitor = null;
	// theTimes = null;
	// currentTime = 0L;
	// try
	// {
	// theMarkers.dispose();
	// IFile file = ((FileEditorInput) getEditorInput()).getFile();
	// theMarkers = new TracefileMarker(file);
	// } catch (CGException cge)
	// {
	// showMessage(cge);
	// }
	// }

	/*
	 * (non-Javadoc)
	 * @see org.overture.ide.plugins.showtraceNextGen.viewer.IViewCallback#showMessage(java .lang.String)
	 */
	public void showMessage(final String message)
	{
		display.asyncExec(new Runnable()
		{

			public void run()
			{

				cw.println(message);
				cw.show();
				// MessageDialog.openInformation(getSite().getShell(),
				// "Tracefile viewer", message);
			}
		});
	}

	private void showMessage(final CGException cge)
	{
		display.asyncExec(new Runnable()
		{

			public void run()
			{
				cw.println(cge.getMessage());
				ConsoleWriter.getExceptionStackTraceAsString(cge);
				cw.show();
			}
		});
	}

	@Override
	public void setFocus()
	{
		folder.setFocus();
	}

	@Override
	public void dispose()
	{
		try
		{
			// deleteTabPages();
			if (theMarkers != null)
			{
				theMarkers.dispose();
			}
		} catch (CGException cge)
		{
			cge.printStackTrace(System.out);
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor)
	{
	}

	@Override
	public void doSaveAs()
	{
	}

	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}

	@Override
	public boolean isDirty()
	{
		return false;
	}

	public boolean canExportJpg()
	{
		return canExportJpg;
	}

	public boolean canMoveHorizontal()
	{
		return canMoveHorizontal;
	}

	public boolean canOpenValidation()
	{
		return canOpenValidation;
	}

}
