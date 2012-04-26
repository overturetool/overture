package org.overturetool.util.modules;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overturetool.util.ClonableFile;
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.lex.LexLocation;

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

	public CombinedDefaultModule(Set<AModuleModules> modules)
	{
		super(new LexIdentifierToken("DEFAULT", false, new LexLocation()), null, null, new Vector<PDefinition>(), new Vector<ClonableFile>(), true, false);
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
	 * @return a set of contained modules
	 */
	public Set<AModuleModules> getModules()
	{
		return this.modules;
	}
}
