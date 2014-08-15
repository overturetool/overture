/*
 * #%~
 * org.overture.ide.core
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
package org.overture.ide.core.resources;

//ICompilationUnit
import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.overture.ast.node.INode;
import org.overture.ide.core.IVdmElement;

public interface IVdmSourceUnit extends IVdmElement
{

	public static final int VDM_DEFAULT = 1;
	public static final int VDM_CLASS_SPEC = 1;
	public static final int VDM_MODULE_SPEC = 2;
	public static final int VDM_MODULE_SPEC_FLAT = 3;

	public int getType();

	public void setType(int type);

	public IFile getFile();

	public File getSystemFile();

	public void reconcile(List<INode> parseResult, boolean parseErrors);

	public List<INode> getParseList();

	public boolean exists();

	public void clean();

	public abstract IVdmProject getProject();

	public abstract boolean hasParseTree();

	public abstract boolean hasParseErrors();

	public abstract VdmSourceUnitWorkingCopy getWorkingCopy();
}
