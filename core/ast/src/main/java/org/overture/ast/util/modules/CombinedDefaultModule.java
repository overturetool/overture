/*
 * #%~
 * The Overture Abstract Syntax Tree
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
package org.overture.ast.util.modules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.lex.LexIdentifierToken;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.util.ClonableFile;

/**
 * This is a module used to handle flat specifications in VDM-SL This module is an encapsulation of a number of flat
 * parsed modules The name of this module is {@code DEFAULT} and it overrides the {@code getDefs} and {@code getFiles}
 * methods of a normal module such that it returns the union of all contained modules.
 * 
 * @author kela
 */
public class CombinedDefaultModule extends AModuleModules
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final Set<AModuleModules> modules = new HashSet<AModuleModules>();

	@SuppressWarnings("deprecation")
	public CombinedDefaultModule(Set<AModuleModules> modules)
	{
		super(null, null, null, new ArrayList<PDefinition>(), new ArrayList<ClonableFile>(), true, false);
		this.modules.addAll(modules);

		if (getDefs().isEmpty())
		{
			setName(new LexIdentifierToken("DEFAULT", false, new LexLocation()));
		} else
		{
			setName(new LexIdentifierToken("DEFAULT", false, getDefs().get(0).getLocation()));
		}
	}

	@Override
	public LinkedList<PDefinition> getDefs()
	{
		LinkedList<PDefinition> definitions = new LinkedList<PDefinition>();

		for (AModuleModules m : modules)
		{
			definitions.addAll(m.getDefs());
		}

		return definitions;
	}

	@Override
	public List<? extends ClonableFile> getFiles()
	{
		LinkedList<ClonableFile> allFiles = new LinkedList<ClonableFile>();

		for (AModuleModules m : modules)
		{
			allFiles.addAll(m.getFiles());
		}

		return allFiles;
	}

	/**
	 * This method returns all the modules encapsulated within this container
	 * 
	 * @return a set of contained modules
	 */
	public Set<AModuleModules> getModules()
	{
		return this.modules;
	}
}
