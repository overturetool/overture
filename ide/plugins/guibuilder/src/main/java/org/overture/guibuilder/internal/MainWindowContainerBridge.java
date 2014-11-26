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
import java.io.File;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import org.overture.guibuilder.internal.ir.IVdmDefinition;
import org.overture.guibuilder.internal.ir.VdmClass;
import org.overture.guibuilder.internal.ir.VdmMethod;
import org.overture.guibuilder.internal.ir.VdmParam;
import org.swixml.SwingEngine;

/**
 * Class to endow a main window with functionality. Serves as a backend to a main window.
 * 
 * @author carlos
 */
public class MainWindowContainerBridge implements IContainerBridge, Observer
{

	// reference to the main ui inteface object
	private UiInterface parent = null;
	// the swixml engine used to render the gui
	private SwingEngine engine = null;
	// the main container of the ui
	private Container container = null;
	// ui element id map
	private Map<String, Object> idMap = null;

	/**
	 * Constructor
	 * 
	 * @param parent
	 */
	@SuppressWarnings("unchecked")
	public MainWindowContainerBridge(UiInterface parent)
	{
		this.parent = parent;
		this.engine = new SwingEngine();
		idMap = engine.getIdMap();
	}

	@Override
	public void buildComponent(File file) throws Exception
	{
		container = engine.render(file);
		engine.setActionListener(container, this);
		InstanceList.getInstance().addObserver(this);
		// we call this to check if the buttons are enabled/disabled
		update(null, null);
	}

	@Override
	public void setVisible(boolean b)
	{
		((javax.swing.JFrame) container).pack();
		container.setVisible(b);
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		try
		{
			parent.setWindowVisible(arg0.getActionCommand(), true);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void update(Observable arg0, Object arg1)
	{
		Vector<IVdmDefinition> classes = parent.getVdmClassList();
		for (IVdmDefinition def : classes)
		{
			VdmClass c = (VdmClass) def;
			// if the class has no constructors we can call the window knowing it has no dependencies
			if (!c.hasConstructors())
			{
				continue;
			}

			Object o = idMap.get(NamingPolicies.getButtonWindowCallerId(def.getName()));
			// sanity check
			if (o == null)
			{
				continue;
			}

			// enable flag
			boolean flag = true;
			// looking for the constructor
			for (IVdmDefinition m : c.getDefinitions())
			{
				if (m instanceof VdmMethod)
				{
					// we found the contructor
					if (((VdmMethod) m).isConstructor())
					{
						// checking its parameters
						for (VdmParam param : ((VdmMethod) m).getParamList())
						{
							// if its a class type
							if (param.getType().isClass())
							{
								// there's no instance of this type currently available
								if (InstanceList.getInstance().getInstancesOfType(param.getType().getName()).size() == 0)
								{
									flag = false;
								}
							}
						}
					}

				}
			}
			// setting enabled
			((Component) o).setEnabled(flag);

		}

	}

	@Override
	public String getId()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
