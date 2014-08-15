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

import java.io.InputStreamReader;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class ValidationConjecturesView extends ViewPart
{

	private ValidationTable theConjectures;

	public ValidationConjecturesView()
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent)
	{
		theConjectures = new ValidationTable(parent);
	}

	@Override
	public void setFocus()
	{
		// TODO Auto-generated method stub

	}

	public void initializeLink(InputStreamReader inputStreamReader,
			IViewCallback theCallback)
	{
		theConjectures.setCallback(theCallback);
		theConjectures.parseValidationFile(inputStreamReader);
		
	}

	public void unlink(IViewCallback theCallback)
	{
		theConjectures.unlink(theCallback);
	}
	
	

}
