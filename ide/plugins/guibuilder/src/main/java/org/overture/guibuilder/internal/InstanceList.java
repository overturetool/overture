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

import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

/**
 * This class just wraps around a Vector, to implement the observable and singleton software patterns.
 * 
 * @author carlos
 */
public class InstanceList extends Observable implements Observer
{

	private Vector<VdmInstance> instances = null;
	// the single avaliable instance for this class
	private static InstanceList instance = null;

	private InstanceList()
	{
		super();
		instances = new Vector<VdmInstance>();
	}

	public static InstanceList getInstance()
	{
		if (instance == null)
		{
			instance = new InstanceList();
		}

		return instance;
	}

	public synchronized void setChanged()
	{
		super.setChanged();
	}

	/**
	 * Add a new instance to the list.
	 * 
	 * @param variable
	 *            The vdm instance.
	 */
	public void add(VdmInstance variable)
	{
		instances.add(variable);
		variable.addObserver(this);
		setChanged();
		notifyObservers();
	}

	/**
	 * Remove the instance at position i.
	 * 
	 * @param index
	 *            The position of the instance in the list.
	 */
	public void remove(int index)
	{
		instances.remove(index);
		setChanged();
		notifyObservers();
	}

	/**
	 * @return The current number of instances in the list.
	 */
	public int size()
	{
		return instances.size();
	}

	/**
	 * Returns the vdm instance at a given position.
	 * 
	 * @param index
	 *            The position of the vdm instance in the list.
	 * @return The vdm instance.
	 */
	public VdmInstance get(int index)
	{
		return instances.get(index);
	}

	/**
	 * Returns all instances of a given type.
	 * 
	 * @param type
	 *            The type of instances.
	 * @return The vdm instance.
	 */
	public Vector<VdmInstance> getInstancesOfType(String type)
	{
		Vector<VdmInstance> is = new Vector<VdmInstance>();
		for (VdmInstance i : instances)
		{
			if (i.getType().equals(type))
			{
				is.add(i);
			}
		}
		return is;
	}

	@Override
	public void update(Observable o, Object arg)
	{
		setChanged();
		notifyObservers();
	}
}
