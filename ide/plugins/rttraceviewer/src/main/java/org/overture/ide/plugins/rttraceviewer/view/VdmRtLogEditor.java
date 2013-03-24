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
package org.overture.ide.plugins.rttraceviewer.view;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
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
import org.overture.ide.plugins.rttraceviewer.view.GenericTabItem.AllowedOverrunDirection;
import org.overture.ide.plugins.rttraceviewer.data.Conjecture;
import org.overture.ide.plugins.rttraceviewer.data.Conjecture.ConjectureType;
import org.overture.ide.plugins.rttraceviewer.data.ConjectureData;
import org.overture.ide.ui.internal.util.ConsoleWriter;

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
	private HashMap<Long, GenericTabItem> cpuTabs; //CPU Id, Tab
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
		theConjectures = null;
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
		theConjectures = new ValidationTable(form, this);
		form.setWeights(new int[] { 85, 15 });
		theArch = new GenericTabItem("Architecture overview", folder, AllowedOverrunDirection.Both);
		theOverview = new GenericTabItem("Execution overview", folder, AllowedOverrunDirection.Vertical);
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
		} catch (Exception e)
		{
			showMessage(e);
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

	void openValidationConjectures()
	{
		FileDialog fDlg = new FileDialog(getSite().getShell());
		String valFileName = fDlg.open();
		theConjectures.parseValidationFile(valFileName);
		
		updateOverviewPage();
	}

	void diagramExportAction()
	{
		theArch.exportJPG((new StringBuilder(String.valueOf(fileName))).append(".arch").toString());
		theOverview.exportJPG((new StringBuilder(String.valueOf(fileName))).append(".overview").toString());
		
		for(GenericTabItem tab : cpuTabs.values())
		{
			String jpegFile = (new StringBuilder(String.valueOf(fileName))).append(".").append(tab.getName()).toString();
			tab.exportJPG(jpegFile);
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
			e.printStackTrace();

		} catch (InterruptedException e)
		{
			e.printStackTrace();
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
			showMessage(t.error.getMessage());
		}		
		else if (t.data != null)
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
			
			for(Long cpu : theCpus)
			{
				String cpuName = traceRunner.getCpuName(cpu);
				GenericTabItem theDetail = new GenericTabItem(cpuName, folder, AllowedOverrunDirection.Horizontal);
				traceRunner.drawCpu(theDetail, cpu, new Long(currentTime));
				cpuTabs.put(cpu, theDetail);
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			showMessage(e.getMessage());

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
			
			for(Long cpu : cpuTabs.keySet())
			{
				GenericTabItem tab = cpuTabs.get(cpu);
				tab.disposeFigures();
				traceRunner.drawCpu(tab, cpu, new Long(currentTime));
			}

		} catch (Exception cge)
		{
			showMessage(cge.getMessage());
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
				// MessageDialog.openInformation(getSite().getShell(),
				// "Tracefile viewer", message);
			}
		});
	}

	private void showMessage(final Exception cge)
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
			theOverview.disposeFigures();
			if (theMarkers != null)
			{
				theMarkers.dispose();
			}
		} catch (Exception cge)
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
