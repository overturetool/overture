/*
 * #%~
 * RT Trace Viewer Plugin
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
package org.overture.ide.plugins.rttraceviewer.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.overture.ide.core.utility.FileUtility;
import org.overture.ide.plugins.rttraceviewer.IRealTimeTaceViewer;
import org.overture.ide.plugins.rttraceviewer.TracefileViewerPlugin;
import org.overture.ide.plugins.rttraceviewer.data.Conjecture;
import org.overture.ide.plugins.rttraceviewer.data.Conjecture.ConjectureType;
import org.overture.ide.plugins.rttraceviewer.data.ConjectureData;
import org.overture.ide.plugins.rttraceviewer.view.GenericTabItem.AllowedOverrunDirection;
import org.overture.ide.ui.internal.util.ConsoleWriter;

public class VdmRtLogEditor extends EditorPart implements IViewCallback
{
	static final boolean $assertionsDisabled = false;// !org/overturetool/tracefile/viewer/TracefileViewer.desiredAssertionStatus();
	private static final ConsoleWriter cw = new ConsoleWriter("RT Log viewer");

	private File selectedFile;
	private Display display;

	private SashForm form;
	private TabFolder folder;
	// private ValidationTable theConjectures;
	private GenericTabItem theArch;
	private GenericTabItem theOverview;
	private HashMap<Long, GenericTabItem> cpuTabs; // CPU Id, Tab
	private String fileName;
	private List<Long> theTimes;
	private long currentTime;

	private boolean canExportJpg = true;
	private boolean canMoveHorizontal = true;
	private boolean canOpenValidation = true;

	private TraceFileRunner traceRunner;
	private TracefileMarker theMarkers;
	private ConjectureData conjectureData;

	public VdmRtLogEditor()
	{
		conjectureData = new ConjectureData();
		// theConjectures = null;
		theArch = null;
		theOverview = null;
		cpuTabs = new HashMap<Long, GenericTabItem>();
		fileName = null;
		theTimes = null;
		currentTime = 0L;
		traceRunner = null;
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
		// theConjectures = new ValidationTable(form, this);
		// form.setWeights(new int[] { 85, 15 });
		theArch = new GenericTabItem("Architecture overview", folder, AllowedOverrunDirection.Both);
		theOverview = new GenericTabItem("Execution overview", folder, AllowedOverrunDirection.Vertical);
		cw.clear();
		IFile file = null;
		try
		{

			file = ((FileEditorInput) getEditorInput()).getFile();

			FileUtility.deleteMarker(file, null, TracefileViewerPlugin.PLUGIN_ID);

			theMarkers = new TracefileMarker(file);

			if (FileUtility.getContent(file).size() == 0)
			{
				ErrorDialog.openError(getSite().getShell(), "Editor open", "File is empty", Status.CANCEL_STATUS);
				return;
			}

		} catch (Exception e)
		{
			TracefileViewerPlugin.log(e);
		} catch (OutOfMemoryError m)
		{
			showMessage("The trace file can not be visualized because the Java Virtual Machine ran out of heap space. Try to allow Overture more heap space using Virtual Machine custom arguments (e.g. -Xms40m -Xmx512m).");
			return;
		}

		try
		{
			parseFile(selectedFile.getAbsolutePath());
		} catch (Exception e)
		{
			TracefileViewerPlugin.log(e);
		}
		openValidationConjectures(file);
	}

	private void openValidationConjectures(IFile editorInputFile)
	{
		IPath p = new Path(editorInputFile.getName());
		p = p.addFileExtension("vtc");
		IFile vtcFile = editorInputFile.getParent().getFile(p);
		if (vtcFile.exists())
		{
			try
			{
				ValidationConjecturesView v = getValidationConjecturesView();
				if (v != null)
				{
					conjectureData.clear();
					v.initializeLink(new InputStreamReader(vtcFile.getContents()), this);
					updateOverviewPage();
				}
			} catch (PartInitException e)
			{
				TracefileViewerPlugin.log(e);
			} catch (CoreException e)
			{
				TracefileViewerPlugin.log(e);
			}

		}

	}

	private ValidationConjecturesView getValidationConjecturesView()
	{
		IViewPart v;
		try
		{
			if (PlatformUI.getWorkbench().isClosing())
			{
				return null;
			}
			v = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IRealTimeTaceViewer.CONJECTURE_VIEW_ID);
			if (v instanceof ValidationConjecturesView)
			{
				return (ValidationConjecturesView) v;
			}
		} catch (CoreException e)
		{
			TracefileViewerPlugin.log(e);
		}
		return null;
	}

	void openValidationConjectures()
	{
		FileDialog fDlg = new FileDialog(getSite().getShell());
		String valFileName = fDlg.open();

		ValidationConjecturesView v = getValidationConjecturesView();
		if (v != null)
		{
			conjectureData.clear();
			try
			{
				v.initializeLink(new FileReader(valFileName), this);
			} catch (FileNotFoundException e)
			{
				TracefileViewerPlugin.log(e);
			}
			updateOverviewPage();
		}
	}

	void diagramExportAction()
	{
		theArch.exportJPG(fileName + ".arch");
		theOverview.exportJPG(fileName + ".overview");

		for (GenericTabItem tab : cpuTabs.values())
		{
			tab.exportJPG(fileName + "." + tab.getName());
		}
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

	void refresh()
	{
		updateOverviewPage();
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.ide.plugins.rttraceviewer.viewer.IViewCallback#panToTime(long, long)
	 */
	public void panToTime(long time, long thrid)
	{
		for (Iterator<Long> iter = theTimes.iterator(); iter.hasNext();)
		{
			long theTime = iter.next().longValue();
			if (theTime < time)
				currentTime = theTime;
		}

		folder.setSelection(theOverview.getTabItem());
		updateOverviewPage();
	}

	public void addLowerError(Long time, Long threadID, String name)
	{
		conjectureData.addConjecture(new Conjecture(time, threadID, name, ConjectureType.SOURCE));
	}

	public void addUpperError(Long time, Long threadID, String name)
	{
		conjectureData.addConjecture(new Conjecture(time, threadID, name, ConjectureType.DESTINATION));
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
			TracefileViewerPlugin.log(e);

		} catch (InterruptedException e)
		{
			TracefileViewerPlugin.log(e);
		}
	}

	@SuppressWarnings("deprecation")
	private void doParse(final String fname, IProgressMonitor monitor)
	{
		TraceFileParser t = new TraceFileParser(fname);
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
			showMessage("Parser error " + t.error.getMessage());
		} else if (t.data != null)
		{
			traceRunner = new TraceFileRunner(t.data, conjectureData);
			theTimes = t.data.getEventManager().getEventTimes();
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

	private void createTabPages()
	{
		try
		{
			traceRunner.drawArchitecture(theArch);
			traceRunner.drawOverview(theOverview, new Long(currentTime));
			canExportJpg = true;
			canMoveHorizontal = true;
			canOpenValidation = true;
			Vector<Long> theCpus = traceRunner.getCpuIds();

			cpuTabs.clear();

			for (Long cpu : theCpus)
			{
				String cpuName = traceRunner.getCpuName(cpu);
				GenericTabItem theDetail = new GenericTabItem(cpuName, folder, AllowedOverrunDirection.Horizontal);
				traceRunner.drawCpu(theDetail, cpu, new Long(currentTime));
				cpuTabs.put(cpu, theDetail);
			}
		} catch (Exception e)
		{
			TracefileViewerPlugin.log(e);

		}

	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.ide.plugins.rttraceviewer.viewer.IViewCallback#updateOverviewPage ()
	 */
	public void updateOverviewPage()
	{
		try
		{
			theOverview.disposeFigures();
			traceRunner.drawOverview(theOverview, new Long(currentTime));

			for (Long cpu : cpuTabs.keySet())
			{
				GenericTabItem tab = cpuTabs.get(cpu);
				tab.disposeFigures();
				traceRunner.drawCpu(tab, cpu, new Long(currentTime));
			}

		} catch (Exception e)
		{
			TracefileViewerPlugin.log(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.ide.plugins.rttraceviewer.viewer.IViewCallback#showMessage(java .lang.String)
	 */
	public void showMessage(final String message)
	{
		display.asyncExec(new Runnable()
		{

			public void run()
			{

				cw.println(message);
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
			ValidationConjecturesView v = getValidationConjecturesView();
			if (v != null)
			{
				v.unlink(this);
			}

			theOverview.disposeFigures();
			if (theMarkers != null)
			{
				theMarkers.dispose();
			}

		} catch (Exception e)
		{
			TracefileViewerPlugin.log(e);
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
