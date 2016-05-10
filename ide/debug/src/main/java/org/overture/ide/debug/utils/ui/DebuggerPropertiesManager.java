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
package org.overture.ide.debug.utils.ui;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.overture.ide.debug.utils.DebuggerProperties.DebuggerProperty;
import org.overture.ide.debug.utils.DebuggerProperties.DebuggerProperty.ValueType;

public class DebuggerPropertiesManager
{
	Set<DebuggerProperty> props;
	String launchConfigkey;
	Map<Control, DebuggerProperty> map = new Hashtable<Control, DebuggerProperty>();

	public DebuggerPropertiesManager(String launchConfigkey,
			Set<DebuggerProperty> props)
	{
		this.launchConfigkey = launchConfigkey;
		this.props = props;
	}

	public void createControl(Composite parent,
			final SelectionListener fListener)
	{
		Group group = new Group(parent, SWT.NONE);
		group.setText("Debugger Properties");
		RowLayout rowLayout = new RowLayout();
		rowLayout.wrap = false;
		rowLayout.pack = false;
		rowLayout.justify = true;
		rowLayout.fill = true;
		rowLayout.type = SWT.VERTICAL;
		rowLayout.marginLeft = 5;
		rowLayout.marginTop = 5;
		rowLayout.marginRight = 5;
		rowLayout.marginBottom = 5;
		rowLayout.spacing = 0;
		group.setLayout(rowLayout);

		List<DebuggerProperty> sorted = new ArrayList<DebuggerProperty>();
		sorted.addAll(props);
		Collections.sort(sorted);

		for (DebuggerProperty p : sorted)
		{
			switch (p.type)
			{
				case Bool:
				{
					Button checkBox = new Button(group, SWT.CHECK);
					map.put(checkBox, p);
					checkBox.setText(p.name);
					checkBox.setSelection(p.bValue);
					checkBox.addSelectionListener(new SelectionListener()
					{

						public void widgetSelected(SelectionEvent e)
						{
							DebuggerProperty p = map.get(e.widget);
							if (e.widget instanceof Button
									&& p.type == ValueType.Bool)
							{
								p.bValue = ((Button) e.widget).getSelection();
								if (fListener != null)
								{
									fListener.widgetSelected(e);
								}
							}
						}

						public void widgetDefaultSelected(SelectionEvent e)
						{
						}
					});
				}
					break;
				case Int:
				{
					Composite comp = new Composite(group, SWT.NONE);
					RowLayout rowLayout1 = new RowLayout();
					rowLayout1.wrap = true;
					rowLayout1.pack = true;
					rowLayout1.justify = false;
					rowLayout1.type = SWT.HORIZONTAL;
					rowLayout1.marginLeft = 5;
					rowLayout1.marginTop = 5;
					rowLayout1.marginRight = 5;
					rowLayout1.marginBottom = 5;
					rowLayout1.spacing = 0;
					comp.setLayout(rowLayout1);
					Label label = new Label(comp, SWT.None);
					label.setText(p.name);

					Spinner spinner = new Spinner(comp, SWT.BORDER);
					map.put(spinner, p);
					spinner.setMinimum(0);
					spinner.setMaximum(1000000000);
					spinner.setSelection(p.iValue);
					spinner.setIncrement(1);
					spinner.setPageIncrement(1000);
					spinner.addSelectionListener(new SelectionListener()
					{

						public void widgetSelected(SelectionEvent e)
						{
							DebuggerProperty p = map.get(e.widget);
							if (e.widget instanceof Spinner
									&& p.type == ValueType.Int)
							{
								p.iValue = ((Spinner) e.widget).getSelection();
								if (fListener != null)
								{
									fListener.widgetSelected(e);
								}
							}
						}

						public void widgetDefaultSelected(SelectionEvent e)
						{
						}
					});
				}
					break;
			}
		}
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
		try
		{
			initializeFrom(configuration.getAttribute(launchConfigkey, ""));
		} catch (CoreException e)
		{
		}
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		// configuration.setAttribute(launchConfigkey, getConfigString(props));
		try
		{
			List<DebuggerProperty> sorted = new ArrayList<DebuggerProperty>();
			sorted.addAll(props);
			Collections.sort(sorted);

			if (!getConfigString(sorted).equals(configuration.getAttribute(launchConfigkey, "")))
			{
				configuration.setAttribute(launchConfigkey, getConfigString(sorted));
			}

		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String getConfigString(List<DebuggerProperty> ps)
	{
		String tmp = "";
		for (Iterator<DebuggerProperty> itr = ps.iterator(); itr.hasNext();)
		{
			tmp += itr.next();
			if (itr.hasNext())
			{
				tmp += ";";
			}

		}
		return tmp;
	}

	public void setDefaults(Set<DebuggerProperty> defaultProps,
			ILaunchConfigurationWorkingCopy configuration)
	{
		List<DebuggerProperty> dProps = new ArrayList<DebuggerProperty>(defaultProps);
		Collections.sort(dProps);
		initializeFrom(getConfigString(dProps));
		performApply(configuration);
	}

	private void initializeFrom(String configString)
	{
		try
		{
			String[] tmp = configString.split(";");
			for (String string : tmp)
			{
				DebuggerProperty p = DebuggerProperty.load(string);
				for (Entry<Control, DebuggerProperty> entry : map.entrySet())
				{

					DebuggerProperty p2 = entry.getValue();
					if (p2.name.equals(p.name) && p2.type == p.type)
					{
						switch (p2.type)
						{
							case Bool:
							{
								Button checkBox = (Button) entry.getKey();
								checkBox.setSelection(p.bValue);
								p2.bValue = p.bValue;
							}
								break;
							case Int:
							{
								Spinner checkBox = (Spinner) entry.getKey();
								checkBox.setSelection(p.iValue);
								p2.iValue = p.iValue;
							}
								break;
						}
					}
				}
			}

		} catch (Exception e)
		{

		}
	}

}
