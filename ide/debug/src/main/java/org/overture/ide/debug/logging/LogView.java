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
package org.overture.ide.debug.logging;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;
import org.overture.ide.debug.core.dbgp.IDbgpRawPacket;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.overture.ide.debug.core.dbgp.internal.DbgpRawPacket;
import org.overture.ide.debug.core.dbgp.internal.packets.DbgpResponsePacket;
import org.overture.ide.debug.core.dbgp.internal.utils.DbgpXmlPacketParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class LogView extends ViewPart
{
	private TableViewer viewer;
	private IAction copyAction;
	private IAction clearAction;
	private IAction executionFilterAction;
	private IAction scrollLockAction;

	final Display display = Display.getCurrent();

	private static final List<LogItem> content = new Vector<LogItem>();

	@Override
	public void createPartControl(Composite parent)
	{
		viewer = new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.MULTI | SWT.FULL_SELECTION | SWT.VIRTUAL);

		addColumn("Type", 80, true);
		addColumn("Thread", 0, true);
		addColumn("Message", 0, true);
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setSortDirection(SWT.NONE);
		viewer.setSorter(null);

		viewer.getTable().addListener(SWT.Resize, new Listener()
		{

			public void handleEvent(Event event)
			{
				final Table table = (Table) event.widget;
				final int columnCount = table.getColumnCount();
				int w = table.getClientArea().width;
				for (int i = 0; i < columnCount - 1; ++i)
				{
					w -= table.getColumn(i).getWidth();
				}
				if (w > 0)
				{
					table.getColumn(columnCount - 1).setWidth(w);
				}
			}

		});

		viewer.setContentProvider(new LogContentProvider());
		viewer.setLabelProvider(new LogLabelProvider());
		viewer.setInput(content);

		parent.layout();
		createActions();
		createMenu();
		// createToolbar();
		createContextMenu();

	}

	@Override
	public void setFocus()
	{
		Shell shell = getShell();
		if (shell != null)
		{
			final Table table = viewer.getTable();
			if (table.isDisposed())
			{
				return;
			}
			final Display display = table.getDisplay();
			if (display.isDisposed())
			{
				return;
			}
			shell.getDisplay().asyncExec(new Runnable()
			{

				public void run()
				{
					viewer.getControl().setFocus();
				}
			});
		}

	}

	/**
	 * @param caption
	 * @param width
	 */
	private void addColumn(String caption, int width, boolean resizeable)
	{
		final TableColumn column = new TableColumn(viewer.getTable(), SWT.LEFT);
		column.setText(caption);
		column.setWidth(width);
		column.setResizable(resizeable);
		if (width == 0)
		{
			column.pack();
		}

	}

	public void createActions()
	{
		copyAction = new DebugLogCopyAction(viewer);
		clearAction = new Action("Clear")
		{
			public void run()
			{
				synchronized (content)
				{
					content.clear();
				}
				viewer.refresh();
			}
		};
		executionFilterAction = new Action("Execution filter", SWT.TOGGLE)
		{
			public void run()
			{
				synchronized (viewer)
				{
					List<ViewerFilter> filters = new ArrayList<ViewerFilter>();
					filters.addAll(Arrays.asList(viewer.getFilters()));
					if (executionFilterAction.isChecked())
					{
						filters.add(new DebugLogExecutionControlFilter());
					} else
					{
						for (int i = 0; i < filters.size(); i++)
						{
							if (filters.get(i) instanceof DebugLogExecutionControlFilter)
							{
								filters.remove(i);
								break;
							}
						}
					}
					viewer.setFilters(filters.toArray(new ViewerFilter[filters.size()]));
					viewer.refresh();
				}
			}
		};

		scrollLockAction = new Action("Scroll Lock", SWT.TOGGLE)
		{

		};
	}

	private void createMenu()
	{
		IMenuManager manager = getViewSite().getActionBars().getMenuManager();
		manager.add(copyAction);
		manager.add(clearAction);
		manager.add(executionFilterAction);
		manager.add(scrollLockAction);
	}

	// private void createToolbar() {
	// IToolBarManager manager = getViewSite().getActionBars()
	// .getToolBarManager();
	// manager.add(copyAction);
	// manager.add(clearAction);
	// }

	private void createContextMenu()
	{
		// Create menu manager.
		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener()
		{
			public void menuAboutToShow(IMenuManager manager)
			{
				fillContextMenu(manager);
			}
		});

		// Create menu.
		Menu menu = menuManager.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);

		// Register menu for extension.
		getSite().registerContextMenu(menuManager, viewer);
	}

	private void fillContextMenu(IMenuManager manager)
	{
		manager.add(copyAction);
		manager.add(clearAction);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	public synchronized void clear()
	{
		content.clear();
		Shell shell = getShell();
		if (shell != null)
		{
			final Table table = viewer.getTable();
			if (table.isDisposed())
			{
				return;
			}
			final Display display = table.getDisplay();
			if (display.isDisposed())
			{
				return;
			}
			shell.getDisplay().asyncExec(new Runnable()
			{

				public void run()
				{
					viewer.refresh();
				}
			});
		}
	}

	public Shell getShell()
	{
		return getSite().getShell();
	}

	public synchronized void log(LogItem item)
	{
		content.add(item);

		Shell shell = getShell();
		if (shell != null)
		{
			final Table table = viewer.getTable();
			if (table.isDisposed())
			{
				return;
			}
			final Display display = table.getDisplay();
			if (display.isDisposed())
			{
				return;
			}
			shell.getDisplay().asyncExec(new Runnable()
			{

				public void run()
				{
					viewer.refresh(false, false);
					if (table.isDisposed() || table.getDisplay().isDisposed())
					{
						return;
					}
					final int itemCount = table.getItemCount();
					if (itemCount > 0 && !scrollLockAction.isChecked())
					{
						table.showItem(table.getItem(itemCount - 1));
					}

				}
			});
		}
	}

	private static final String INIT_TAG = "init"; //$NON-NLS-1$
	private static final String RESPONSE_TAG = "response"; //$NON-NLS-1$
	private static final String STREAM_TAG = "stream"; //$NON-NLS-1$
	private static final String NOTIFY_TAG = "notify"; //$NON-NLS-1$

	public void dbgpPacketReceived(int sessionId, IDbgpRawPacket content)
	{
		// FIXME: what is this! why is it here and is it used
		try
		{
			Document doc = ((DbgpRawPacket) content).getParsedXml();
			Element element = (Element) doc.getFirstChild();
			String tag = element.getTagName();

			if (tag.equals(INIT_TAG))
			{
				// DbgpResponsePacket responsePacket = new DbgpResponsePacket(element, -1);
			} else if (tag.equals(RESPONSE_TAG))
			{
				DbgpResponsePacket packet = DbgpXmlPacketParser.parseResponsePacket(element);
				System.out.println(packet.toString());

				this.log(new LogItem(new Integer(sessionId).toString(), RESPONSE_TAG, "", false, ""));

			} else if (tag.equals(STREAM_TAG))
			{
				// DbgpStreamPacket streamPacket = DbgpXmlPacketParser.parseStreamPacket(element);
			} else if (tag.equals(NOTIFY_TAG))
			{
				// DbgpNotifyPacket notifyPacket = DbgpXmlPacketParser.parseNotifyPacket(element);
			}

		} catch (DbgpException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void dbgpPacketSent(int sessionId, IDbgpRawPacket content)
	{
		// TODO Auto-generated method stub

	}

}
