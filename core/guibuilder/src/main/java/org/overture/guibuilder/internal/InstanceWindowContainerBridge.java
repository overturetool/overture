/*
 * #%~
 * Overture GUI Builder
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
package org.overture.guibuilder.internal;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.text.JTextComponent;

import org.overture.guibuilder.internal.ir.IVdmDefinition;
import org.overture.guibuilder.internal.ir.VdmClass;
import org.overture.guibuilder.internal.ir.VdmMethod;
import org.overture.guibuilder.internal.ir.VdmParam;
import org.swixml.SwingEngine;

/**
 * Class to endow a window with functionality. Serves as a backend to a instance window.
 * 
 * @author carlos
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class InstanceWindowContainerBridge implements IContainerBridge,
		Observer
{
	// reference to the main ui inteface object
	private UiInterface parent = null;
	// the swixml engine used to render the gui
	private SwingEngine engine = null;
	// the main container of the ui
	private Container container = null;
	// the id map of the component;
	private Map<String, Object> idMap = null;
	// name of the instance the container's 'fronting' for
	private String nameOfInstance = null;
	// id for this container
	private String id = null;
	// flag for checking if the 'method' widgets are visible
	private boolean methodWidgetsVisible = false;
	// offset for the windows, so they don't overlap on opening
	static final int xOffset = 30, yOffset = 30;
	// number of windows opened so far
	static int openWindowsCount = 0;
	// the instance widget list

	JComboBox instanceComboBox = null;
	// classDef
	private VdmClass classDef = null;

	public InstanceWindowContainerBridge(UiInterface parent)
	{
		this.parent = parent;
		this.engine = new SwingEngine(this);

	}

	@Override
	public void buildComponent(File file) throws Exception
	{
		container = engine.render(file);

		idMap = engine.getIdMap();

		for (String s : idMap.keySet())
		{
			if (s.contains(NamingPolicies.INSTANCE_WINDOW_SUFFIX_ID))
			{
				this.id = s;
				break;
			}
		}

		this.classDef = (VdmClass) parent.getVdmClass(NamingPolicies.extractClassName(id));

		instanceComboBox = (JComboBox) idMap.get(NamingPolicies.INSTANCE_LIST_ID);
		instanceComboBox.addItem("<< new >>");

		// action handling for the instance list
		instanceComboBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (instanceComboBox.getSelectedIndex() == 0)
				{
					// if there's no constructor instance
					if (idMap.get(NamingPolicies.CONSTRUCTORS_CONTAINER_ID) == null)
					{
						String className = NamingPolicies.extractClassName(id);
						String instanceName = NamingPolicies.getInstanceName(className);
						try
						{
							parent.createNewVdmObjectInstance(instanceName, className);
							instanceComboBox.addItem(instanceName);
							nameOfInstance = instanceName;
							instanceComboBox.setSelectedIndex(instanceComboBox.getItemCount() - 1);
						} catch (Exception e1)
						{
							e1.printStackTrace();
						}
					} else
					{
						nameOfInstance = null;
					}
				} else
				{
					nameOfInstance = (String) instanceComboBox.getSelectedItem();
				}
				refreshVisibleContents();
			}
		});

		// adding the action listeners to the vdm method calling buttons (if any)
		for (String id : idMap.keySet())
		{
			if (id.contains(NamingPolicies.METHOD_BUTTON_PREFIX_ID))
			{
				((JButton) idMap.get(id)).addActionListener(this);
			}
		}

		// add this as an observer of the instance list
		InstanceList.getInstance().addObserver(this);

		refreshButtonState();
	}

	@Override
	public void setVisible(boolean b)
	{
		if (nameOfInstance == null
				&& idMap.get(NamingPolicies.CONSTRUCTORS_CONTAINER_ID) == null)
		{
			String className = NamingPolicies.extractClassName(id);
			String instanceName = NamingPolicies.getInstanceName(className);
			try
			{
				parent.createNewVdmObjectInstance(instanceName, className);
				instanceComboBox.addItem(instanceName);
				nameOfInstance = instanceName;
				instanceComboBox.setSelectedIndex(instanceComboBox.getItemCount() - 1);
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		refreshVisibleContents();
		container.setLocation(xOffset * openWindowsCount + 100, yOffset
				* openWindowsCount);
		++openWindowsCount;
		container.setVisible(b);
	}

	/**
	 * Re-evaluates what components should be visible or not.
	 */
	private void refreshVisibleContents()
	{

		// methods
		Container c1 = (Container) idMap.get(NamingPolicies.METHOD_CONTAINER_ID);
		// constructors
		Container c2 = (Container) idMap.get(NamingPolicies.CONSTRUCTORS_CONTAINER_ID);

		// there's a constructor, and we currently have no instance
		if (nameOfInstance == null && c2 != null)
		{
			c1.setVisible(false);
			c2.setVisible(true);
			methodWidgetsVisible = false;
		} else if (c2 != null)
		// no constructor
		{
			methodWidgetsVisible = true;
			c1.setVisible(true);
			c2.setVisible(false);
		} else if (c2 == null)
		{
			methodWidgetsVisible = true;
			c1.setVisible(true);
		}

		// updates the value of labels
		// FIXME: It's better to use a list with the methods...
		if (methodWidgetsVisible)
		{
			for (String id : idMap.keySet())
			{
				if (id.contains(NamingPolicies.RETURN_LABEL_PREFIX_ID))
				{
					String methodName = id.substring(id.lastIndexOf("_") + 1);
					try
					{
						String ret = parent.callVdmMethod(nameOfInstance, methodName, null);
						((JLabel) idMap.get(id)).setText(ret);
					} catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		((javax.swing.JFrame) container).pack();

	}

	/**
	 * Action for the ok button of the window.
	 */
	public void submitOK()
	{
		// FIXME: Only one constructor is taken into account...
		String className = NamingPolicies.extractClassName(id);
		String instanceName = NamingPolicies.getInstanceName(className);

		// extracting the arguments
		Vector<String> constructorArgs = new Vector<String>();
		int i = 0;
		while (true)
		{
			Object o = idMap.get(NamingPolicies.getConstructorWidgetId(className, 0, i));
			// no more arguments
			if (o == null)
			{
				break;
			}
			if (o instanceof JTextComponent)
			{
				constructorArgs.add(((JTextComponent) o).getText());
			} else
			{
				constructorArgs.add((String) ((JComboBox) o).getSelectedItem());
			}

			++i;
		}

		try
		{
			parent.createNewVdmObjectInstance(instanceName, className, constructorArgs);
			this.nameOfInstance = instanceName;
			instanceComboBox.addItem(instanceName);
			instanceComboBox.setSelectedIndex(instanceComboBox.getItemCount() - 1);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		refreshVisibleContents();
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{

		String method = arg0.getActionCommand();
		// TODO: Logger
		System.out.println("(InstanceWindow) " + method);
		// NOTE: as some classes may not have a constructor we will create a 'empty' object
		if (nameOfInstance == null)
		{
			return;
		}
		// fetching arguments if any
		Vector<String> arguments = new Vector<String>();
		int i = 0;
		while (true)
		{
			Object o = idMap.get(NamingPolicies.getInputComponentId(method, i));
			if (o == null)
			{
				break;
			}
			if (o instanceof JTextComponent)
			{
				arguments.add(((JTextComponent) o).getText());
			} else
			{
				arguments.add((String) ((JComboBox) o).getSelectedItem());
			}
			++i;
		}
		// method call
		try
		{
			String ret = parent.callVdmMethod(nameOfInstance, method, arguments);
			System.out.println(ret);
			// if the output is (), there is no "return"
			if (ret.equals("()"))
			{
				ret = "";
			}
			((JLabel) idMap.get(NamingPolicies.getCheckMethodReturnLabelId(method))).setText(ret);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void update(Observable arg0, Object arg1)
	{
		// fetching all arguments who need a class type for input
		for (IVdmDefinition def : classDef.getDefinitions())
		{
			// sanity check: checking if it in fact is a method
			if (!(def instanceof VdmMethod))
			{
				continue;
			}
			VdmMethod method = (VdmMethod) def;

			Vector<VdmParam> paramList = method.getParamList();
			for (int i = 0; i < paramList.size(); ++i)
			{
				VdmParam p = paramList.get(i);
				// update the combo box
				if (p.getType().isClass())
				{
					// get the widget
					Object o = null;
					if (method.isConstructor())
					{
						o = idMap.get(NamingPolicies.getConstructorWidgetId(NamingPolicies.extractClassName(id), 0, i));
					} else
					{
						o = idMap.get(NamingPolicies.getInputComponentId(def.getName(), i));
					}
					if (o == null)
					{
						continue;
					}
					JComboBox combo = (JComboBox) o;
					// update the widget
					combo.removeAllItems();
					for (VdmInstance is : InstanceList.getInstance().getInstancesOfType(p.getType().getName()))
					{
						combo.addItem(is.getName());
					}
				}
			}
		}

		try
		{
			refreshButtonState();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Checks if the method buttons should be enabled or disabled
	 */
	private void refreshButtonState() throws Exception
	{
		if (this.classDef == null)
		{
			throw new Exception("no underlying class definition to read");
		}

		for (IVdmDefinition def : classDef.getDefinitions())
		{
			// sanity check: checking if it in fact is a method
			if (!(def instanceof VdmMethod))
			{
				continue;
			}
			VdmMethod method = (VdmMethod) def;
			// is there a corresponding button ?
			Object btn = idMap.get(NamingPolicies.getMethodButtonId(method.getName()));
			if (btn == null)
			{
				continue;
			}

			// flag for enable property
			Boolean flag = true;
			Vector<VdmParam> paramList = method.getParamList();
			// check if there is a parameter of instance type
			for (int i = 0; i < paramList.size(); ++i)
			{
				VdmParam p = paramList.get(i);
				// should be a combo box
				if (p.getType().isClass())
				{
					Object o = idMap.get(NamingPolicies.getInputComponentId(def.getName(), i));
					// constructors possess different id
					if (o == null)
					{
						continue;
					}
					JComboBox combo = (JComboBox) o;
					// if there is a empty combo box the corresponding button must be set to disabled
					if (combo.getItemCount() == 0)
					{
						flag = false;
					}
				}
			}

			((Component) btn).setEnabled(flag);
		}

	}

	@Override
	public String getId()
	{
		return id;
	}

}
