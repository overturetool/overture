/*******************************************************************************
 * Copyright (c) 2009, 2013 Overture Team and others.
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
package org.overture.guibuilder.internal;

import java.util.Observable;

/**
 * This class represents a vdm instance. Storing its name and its value.
 * @author carlos
 *
 */
public class VdmInstance extends Observable {
	// the name of the instance
	private String name = null;
	// it's value
	private String value = null;
	// the name of it's type (usually the class name)
	private String type = null;

	/**
	 * Constructor
	 * @param name Name of the instance
	 * @param value Current value of the instance
	 * @param type Type of the instance (the name of the class)
	 */
	public VdmInstance(String name, String value, String type) {
		this.name = name;
		this.value = value;
		this.type = type;
	}
	
	/**
	 * Returns the name of the instantiated class
	 * @return The type name.
	 */
	public String getType() {
		return type;
	}

	/** 
	 * Returns the name of the instance
	 * @return The name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the current value of the instance
	 * @return The value in string form.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Refreshes the value of the instance
	 * @param interpreter The vdm interpreter to use to obtain the value
	 * @return Returns true if the value was successfully updated
	 */
	public boolean refreshValue(VdmjVdmInterpreterWrapper interpreter)  {
		try {
			value = interpreter.getValueOf(name);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		setChanged();
		notifyObservers();

		return true;
	}

}
