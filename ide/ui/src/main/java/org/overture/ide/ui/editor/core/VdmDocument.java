/*
 * #%~
 * org.overture.ide.ui
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
package org.overture.ide.ui.editor.core;

import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.IVdmSourceUnit;

/**
 * All the methods in this file has been synchronized due to a bug with Eclipse.<br>
 * IDE edit of keywords causes exception #284
 */
public class VdmDocument extends Document implements IDocument
{
	private IVdmSourceUnit source;

	public IVdmProject getProject()
	{
		if (source != null)
		{
			return source.getProject();
		}
		return null;
	}

	public IVdmSourceUnit getSourceUnit()
	{
		return this.source;
	}

	public void setSourceUnit(IVdmSourceUnit source)
	{
		this.source = source;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected synchronized Map getDocumentManagedPositions()
	{
		return super.getDocumentManagedPositions();
	}

	@Override
	public synchronized void addPosition(String category, Position position)
			throws BadLocationException, BadPositionCategoryException
	{
		super.addPosition(category, position);
	}

	public synchronized void addPositionCategory(String category)
	{
		super.addPositionCategory(category);
	}

	@Override
	public synchronized boolean containsPosition(String category, int offset,
			int length)
	{
		return super.containsPosition(category, offset, length);
	}

	@Override
	public synchronized boolean containsPositionCategory(String category)
	{
		return super.containsPositionCategory(category);
	}

	@Override
	public synchronized int computeIndexInCategory(String category, int offset)
			throws BadLocationException, BadPositionCategoryException
	{
		return super.computeIndexInCategory(category, offset);
	}

	@Override
	public synchronized Position[] getPositions(String category)
			throws BadPositionCategoryException
	{
		return super.getPositions(category);
	}

	@Override
	public synchronized String[] getPositionCategories()
	{
		return super.getPositionCategories();
	}

	@Override
	public synchronized void removePosition(Position position)
	{
		super.removePosition(position);
	}

	@Override
	public synchronized void removePosition(String category, Position position)
			throws BadPositionCategoryException
	{
		super.removePosition(category, position);
	}

	@Override
	public synchronized void removePositionCategory(String category)
			throws BadPositionCategoryException
	{
		super.removePositionCategory(category);
	}

	@Override
	protected synchronized void completeInitialization()
	{
		super.completeInitialization();
	}

	public synchronized void addPosition(Position position)
			throws BadLocationException
	{
		super.addPosition(position);
	}

	public synchronized Position[] getPositions(String category, int offset,
			int length, boolean canStartBefore, boolean canEndAfter)
			throws BadPositionCategoryException
	{
		return super.getPositions(category, offset, length, canStartBefore, canEndAfter);
	}

}
