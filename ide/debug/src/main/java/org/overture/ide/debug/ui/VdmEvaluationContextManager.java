/*
 * #%~
 * org.overture.ide.debug
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
package org.overture.ide.debug.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.contexts.DebugContextEvent;
import org.eclipse.debug.ui.contexts.IDebugContextListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.model.IVdmStackFrame;

/**
 * Manages the current evaluation context (stack frame) for evaluation actions. In each page, the selection is tracked
 * in each debug view (if any). When a stack frame selection exists, the "debuggerActive" System property is set to
 * true.
 */
public class VdmEvaluationContextManager implements IWindowListener,
		IDebugContextListener
{

	private static VdmEvaluationContextManager fgManager;
	/**
	 * System property indicating a stack frame is selected in the debug view with an <code>IVdmStackFrame</code>
	 * adapter.
	 */
	private static final String DEBUGGER_ACTIVE = VdmDebugPlugin.PLUGIN_ID
			+ ".debuggerActive"; //$NON-NLS-1$

	/**
	 * System property indicating an element is selected in the debug view that is an instanceof
	 * <code>IJavaStackFrame</code> or <code>IJavaThread</code>.
	 */
	private static final String INSTANCE_OF_IJAVA_STACK_FRAME = VdmDebugPlugin.PLUGIN_ID
			+ ".instanceof.IVdmStackFrame"; //$NON-NLS-1$
	/**
	 * System property indicating the frame in the debug view supports 'force return'
	 */
	private static final String SUPPORTS_FORCE_RETURN = VdmDebugPlugin.PLUGIN_ID
			+ ".supportsForceReturn"; //$NON-NLS-1$	
	/**
	 * System property indicating whether the frame in the debug view supports instance and reference retrieval (1.5 VMs
	 * and later).
	 */
	private static final String SUPPORTS_INSTANCE_RETRIEVAL = VdmDebugPlugin.PLUGIN_ID
			+ ".supportsInstanceRetrieval"; //$NON-NLS-1$

	private Map<IWorkbenchPage, IVdmStackFrame> fContextsByPage = null;

	private IWorkbenchWindow fActiveWindow;

	protected VdmEvaluationContextManager()
	{
		DebugUITools.getDebugContextManager().addDebugContextListener(this);

	}

	public static void startup()
	{
		Runnable r = new Runnable()
		{
			public void run()
			{
				if (fgManager == null)
				{
					fgManager = new VdmEvaluationContextManager();
					IWorkbench workbench = PlatformUI.getWorkbench();
					IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
					for (int i = 0; i < windows.length; i++)
					{
						fgManager.windowOpened(windows[i]);
					}
					workbench.addWindowListener(fgManager);
					fgManager.fActiveWindow = workbench.getActiveWorkbenchWindow();
				}
			}
		};
		VdmDebugPlugin.getStandardDisplay().asyncExec(r);
	}

	public void windowActivated(IWorkbenchWindow window)
	{
		fActiveWindow = window;
	}

	public void windowClosed(IWorkbenchWindow window)
	{
	}

	public void windowDeactivated(IWorkbenchWindow window)
	{
	}

	public void windowOpened(IWorkbenchWindow window)
	{
	}

	/**
	 * Sets the evaluation context for the given page, and notes that a valid execution context exists.
	 * 
	 * @param page
	 * @param frame
	 */
	private void setContext(IWorkbenchPage page, IVdmStackFrame frame)
	{
		// pageToContextMap.put(page, frame);
		//		System.setProperty(DEBUGGER_ACTIVE, "true"); //$NON-NLS-1$

		/*
		 * if (frame.canForceReturn()) { System.setProperty(SUPPORTS_FORCE_RETURN, "true"); //$NON-NLS-1$ } else {
		 * System.setProperty(SUPPORTS_FORCE_RETURN, "false"); //$NON-NLS-1$ } if
		 * (((IVdmStackFrame)frame.getDebugTarget()).supportsInstanceRetrieval()){
		 * System.setProperty(SUPPORTS_INSTANCE_RETRIEVAL, "true"); //$NON-NLS-1$ } else {
		 * System.setProperty(SUPPORTS_INSTANCE_RETRIEVAL, "false"); //$NON-NLS-1$ } if (instOf) {
		 * System.setProperty(INSTANCE_OF_IJAVA_STACK_FRAME, "true"); //$NON-NLS-1$ } else {
		 * System.setProperty(INSTANCE_OF_IJAVA_STACK_FRAME, "false"); //$NON-NLS-1$ }
		 */

		if (fContextsByPage == null)
		{
			fContextsByPage = new HashMap<IWorkbenchPage, IVdmStackFrame>();
		}
		fContextsByPage.put(page, frame);
		System.setProperty(DEBUGGER_ACTIVE, "true"); //$NON-NLS-1$
		// if (frame.canForceReturn()) {
		//			System.setProperty(SUPPORTS_FORCE_RETURN, "true"); //$NON-NLS-1$
		// } else {
		//			System.setProperty(SUPPORTS_FORCE_RETURN, "false"); //$NON-NLS-1$
		// }
		// if (((IJavaDebugTarget)frame.getDebugTarget()).supportsInstanceRetrieval()){
		//			System.setProperty(SUPPORTS_INSTANCE_RETRIEVAL, "true"); //$NON-NLS-1$
		// } else {
		//			System.setProperty(SUPPORTS_INSTANCE_RETRIEVAL, "false"); //$NON-NLS-1$
		// }
		// if (instOf) {
		//			System.setProperty(INSTANCE_OF_IJAVA_STACK_FRAME, "true"); //$NON-NLS-1$
		// } else {
		//			System.setProperty(INSTANCE_OF_IJAVA_STACK_FRAME, "false"); //$NON-NLS-1$
		// }
	}

	/**
	 * Removes an evaluation context for the given page, and determines if any valid execution context remain.
	 * 
	 * @param page
	 */
	private void removeContext(IWorkbenchPage page)
	{
		// pageToContextMap.remove(page);
		// if (pageToContextMap.isEmpty())
		// {
		//			System.setProperty(DEBUGGER_ACTIVE, "false"); //$NON-NLS-1$
		// // System.setProperty(INSTANCE_OF_IJAVA_STACK_FRAME, "false");
		//			// //$NON-NLS-1$
		//			// System.setProperty(SUPPORTS_FORCE_RETURN, "false"); //$NON-NLS-1$
		// // System.setProperty(SUPPORTS_INSTANCE_RETRIEVAL, "false");
		//			// //$NON-NLS-1$
		// }
		if (fContextsByPage != null)
		{
			fContextsByPage.remove(page);
			if (fContextsByPage.isEmpty())
			{
				System.setProperty(DEBUGGER_ACTIVE, "false"); //$NON-NLS-1$
				System.setProperty(INSTANCE_OF_IJAVA_STACK_FRAME, "false"); //$NON-NLS-1$
				System.setProperty(SUPPORTS_FORCE_RETURN, "false"); //$NON-NLS-1$
				System.setProperty(SUPPORTS_INSTANCE_RETRIEVAL, "false"); //$NON-NLS-1$
			}
		}
	}

	private static IVdmStackFrame getContext(IWorkbenchPage page)
	{
		// if (manager != null)
		// {
		// if (manager.pageToContextMap != null)
		// {
		// return (IVdmStackFrame) manager.pageToContextMap.get(page);
		// }
		// }
		// return null;
		if (fgManager != null)
		{
			if (fgManager.fContextsByPage != null)
			{
				return (IVdmStackFrame) fgManager.fContextsByPage.get(page);
			}
		}
		return null;
	}

	/**
	 * Returns the evaluation context for the given part, or <code>null</code> if none. The evaluation context
	 * corresponds to the selected stack frame in the following priority order:
	 * <ol>
	 * <li>stack frame in the same page</li>
	 * <li>stack frame in the same window</li>
	 * <li>stack frame in active page of other window</li>
	 * <li>stack frame in page of other windows</li>
	 * </ol>
	 * 
	 * @param part
	 *            the part that the evaluation action was invoked from
	 * @return the stack frame that supplies an evaluation context, or <code>null</code> if none
	 */
	public static IVdmStackFrame getEvaluationContext(IWorkbenchPart part)
	{
		IWorkbenchPage page = part.getSite().getPage();
		IVdmStackFrame frame = getContext(page);
		if (frame == null)
		{
			return getEvaluationContext(page.getWorkbenchWindow());
		}
		return frame;
	}

	/**
	 * Returns the evaluation context for the given window, or <code>null</code> if none. The evaluation context
	 * corresponds to the selected stack frame in the following priority order:
	 * <ol>
	 * <li>stack frame in active page of the window</li>
	 * <li>stack frame in another page of the window</li>
	 * <li>stack frame in active page of another window</li>
	 * <li>stack frame in a page of another window</li>
	 * </ol>
	 * 
	 * @param window
	 *            the window that the evaluation action was invoked from, or <code>null</code> if the current window
	 *            should be consulted
	 * @return the stack frame that supplies an evaluation context, or <code>null</code> if none
	 * @return IJavaStackFrame
	 */
	public static IVdmStackFrame getEvaluationContext(IWorkbenchWindow window)
	{
		List<IWorkbenchWindow> alreadyVisited = new ArrayList<IWorkbenchWindow>();
		if (window == null)
		{
			window = fgManager.fActiveWindow;
		}
		return getEvaluationContext(window, alreadyVisited);
	}

	private static IVdmStackFrame getEvaluationContext(IWorkbenchWindow window,
			List<IWorkbenchWindow> alreadyVisited)
	{
		IWorkbenchPage activePage = window.getActivePage();
		IVdmStackFrame frame = null;
		if (activePage != null)
		{
			frame = getContext(activePage);
		}
		if (frame == null)
		{
			IWorkbenchPage[] pages = window.getPages();
			for (int i = 0; i < pages.length; i++)
			{
				if (activePage != pages[i])
				{
					frame = getContext(pages[i]);
					if (frame != null)
					{
						return frame;
					}
				}
			}

			alreadyVisited.add(window);

			IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
			for (int i = 0; i < windows.length; i++)
			{
				if (!alreadyVisited.contains(windows[i]))
				{
					frame = getEvaluationContext(windows[i], alreadyVisited);
					if (frame != null)
					{
						return frame;
					}
				}
			}
			return null;
		}
		return frame;
	}

	public void debugContextChanged(DebugContextEvent event)
	{
		// if ((event.getFlags() & DebugContextEvent.ACTIVATED) > 0)
		// {
		// IWorkbenchPart part = event.getDebugContextProvider().getPart();
		// if (part != null)
		// {
		// IWorkbenchPage page = part.getSite().getPage();
		// ISelection selection = event.getContext();
		// if (selection instanceof IStructuredSelection)
		// {
		// IStructuredSelection ss = (IStructuredSelection) selection;
		// if (ss.size() == 1)
		// {
		// Object element = ss.getFirstElement();
		// if (element instanceof IAdaptable)
		// {
		// IVdmStackFrame frame = (IVdmStackFrame) ((IAdaptable) element).getAdapter(IVdmStackFrame.class);
		// if (frame != null)
		// {
		// setContext(page, frame);
		// return;
		// }
		// }
		// }
		// }
		//
		// // no context in the given view
		// removeContext(page);
		// }
		// }
		if ((event.getFlags() & DebugContextEvent.ACTIVATED) > 0)
		{
			IWorkbenchPart part = event.getDebugContextProvider().getPart();
			if (part != null)
			{
				IWorkbenchPage page = part.getSite().getPage();
				ISelection selection = event.getContext();
				if (selection instanceof IStructuredSelection)
				{
					IStructuredSelection ss = (IStructuredSelection) selection;
					if (ss.size() == 1)
					{
						Object element = ss.getFirstElement();
						if (element instanceof IAdaptable)
						{
							IVdmStackFrame frame = (IVdmStackFrame) ((IAdaptable) element).getAdapter(IVdmStackFrame.class);
							// boolean instOf = element instanceof IVdmStackFrame || element instanceof IVdmThread;
							if (frame != null)
							{
								// do not consider scrapbook frames
								// if (frame.getLaunch().getAttribute(ScrapbookLauncher.SCRAPBOOK_LAUNCH) == null) {
								// setContext(page, frame, instOf);
								// return;
								// }
								setContext(page, frame);
								return;
							}
						}
					}
				}
				// no context in the given view
				removeContext(page);
			}
		}
	}
}
