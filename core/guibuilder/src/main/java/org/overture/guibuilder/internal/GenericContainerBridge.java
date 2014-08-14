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

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Map;

import org.swixml.SwingEngine;

/**
 * Generic container brige. Used only for debugging purposes.
 * 
 * @author carlos
 */
public class GenericContainerBridge implements IContainerBridge
{
	// reference to the main ui inteface object
	protected UiInterface parent = null;
	// the swixml engine used to render the gui
	private SwingEngine engine = null;
	// the main container of the ui
	private Container container = null;
	// the id map of the component;
	protected Map<String, Object> idMap = null;

	public GenericContainerBridge(UiInterface parent)
	{
		this.parent = parent;
		this.engine = new SwingEngine();
	}

	@SuppressWarnings("unchecked")
	public void buildComponent(File file) throws Exception
	{
		container = engine.render(file);
		idMap = engine.getIdMap();
		engine.setActionListener(container, this);
	}

	public void setVisible(boolean b)
	{
		container.setVisible(b);
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		System.out.println(arg0.getActionCommand());

	}

	@Override
	public String getId()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
