package org.overturetool.util.modules;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.modules.AAllImport;
import org.overture.ast.modules.AFromModuleImports;
import org.overture.ast.modules.AModuleImports;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.modules.PImport;
import org.overture.ast.statements.PStm;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.util.Utils;

@SuppressWarnings("serial")
public class ModuleList extends Vector<AModuleModules>
{
	public ModuleList()
	{
		// empty
	}

	public ModuleList(List<AModuleModules> modules)
	{
		addAll(modules);
	}

	@Override
	public String toString()
	{
		return Utils.listToString(this);
	}

	public Set<File> getSourceFiles()
	{
		Set<File> files = new HashSet<File>();

		for (AModuleModules def : this)
		{
			files.addAll(def.getFiles());
		}

		return files;
	}

	public AModuleModules findModule(LexIdentifierToken sought)
	{
		for (AModuleModules m : this)
		{
			if (m.getName().equals(sought))
			{
				return m;
			}
		}

		return null;
	}

	public PStm findStatement(File file, int lineno)
	{
		// TODO
		// for (AModuleModules m: this)
		// {
		//
		// PStm stmt = m.findStatement(file, lineno);
		//
		// if (stmt != null)
		// {
		// return stmt;
		// }
		// }

		return null;
	}

	public PExp findExpression(File file, int lineno)
	{
		// for (AModuleModules m: this)
		// {
		// Expression exp = m.findExpression(file, lineno);
		//
		// if (exp != null)
		// {
		// return exp;
		// }
		// }
		// TODO
		return null;
	}

	// public ProofObligationList getProofObligations()
	// {
	// ProofObligationList obligations = new ProofObligationList();
	//
	// for (AModuleModules m: this)
	// {
	// obligations.addAll(m.getProofObligations());
	// }
	//
	// obligations.trivialCheck();
	// return obligations;
	// }
	//
	// public void setLoaded()
	// {
	// for (AModuleModules m: this)
	// {
	// m.typechecked = true;
	// }
	// }
	//
	// public int notLoaded()
	// {
	// int count = 0;
	//
	// for (AModuleModules m: this)
	// {
	// if (!m.typechecked) count++;
	// }
	//
	// return count;
	// }

	public int combineDefaults()
	{
		int rv = 0;

		if (!isEmpty())
		{

			// AModuleModules def = new AModuleModules(new LexIdentifierToken("DEFAULT", false, new LexLocation()),
			// null, null, new Vector<PDefinition>(), new Vector<ClonableFile>(), true, false);
			//
			CombinedDefaultModule def = new CombinedDefaultModule(getFlatModules());
			if (Settings.release == Release.VDM_10)
			{
				// In VDM-10, we implicitly import all from the other
				// modules included with the flat specifications (if any).

				List<AFromModuleImports> imports = new Vector<AFromModuleImports>();

				for (AModuleModules m : this)
				{
					if (!m.getIsFlat())
					{
						imports.add(importAll(m.getName()));
					}
				}

				if (!imports.isEmpty())
				{
					// def = new AModuleModules(def.getName(),
					// new ModuleImports(def.getName(), imports), null, def.defs);

					// def = new AModuleModules(def.getName(), new AModuleImports(def.getName(), imports), null,
					// def.getDefs(), def.getFiles(), null, null, false, false, false);
					def.setImports(new AModuleImports(def.getName(), imports));
				}
			}

			if (!def.getModules().isEmpty())
			{
				removeAll(getFlatModules());
				add(def);
			}
			// ModuleList named = new ModuleList();
			//
			// for (AModuleModules m : this)
			// {
			// if (m.getIsFlat())
			// {
			// def.getDefs().addAll(m.getDefs());
			// List<ClonableFile> files = new Vector<ClonableFile>(def.getFiles());
			// files.add(new ClonableFile(m.getName().location.file));
			// def.setFiles(files);// TODO files shoudl not return ? extends Files
			// // def.typechecked |= m.typechecked;//TODO
			// } else
			// {
			// named.add(m);
			// }
			// }
			//
			// if (!def.getDefs().isEmpty())
			// {
			// clear();
			// add(def);
			// addAll(named);
			//
			// // TODO
			// // for (PDefinition d: def.getDefs())
			// // {
			// // if (!d.isTypeDefinition())
			// // {
			// // d.markUsed(); // Mark top-level items as used
			// // }
			// // }
			// }
		}

		return rv;
	}

	private Set<AModuleModules> getFlatModules()
	{
		Set<AModuleModules> flats = new HashSet<AModuleModules>();

		for (AModuleModules m : this)
		{
			if (m.getIsFlat() && !(m instanceof CombinedDefaultModule))
			{
				flats.add(m);
			}
		}

		return flats;
	}

	public ANamedTraceDefinition findTraceDefinition(LexNameToken name)
	{
		for (AModuleModules m : this)
		{
			for (PDefinition d : m.getDefs())
			{
				if (name.equals(d.getName()))
				{
					if (d instanceof ANamedTraceDefinition)
					{
						return (ANamedTraceDefinition) d;
					} else
					{
						return null;
					}
				}
			}
		}

		return null;
	}

	// This function is from the module reader
	public static AFromModuleImports importAll(LexIdentifierToken from)
	{
		List<List<PImport>> types = new Vector<List<PImport>>();
		LexNameToken all = new LexNameToken(from.getName(), "all", from.location);
		List<PImport> impAll = new Vector<PImport>();
		impAll.add(new AAllImport(all.location, all, all, null));
		types.add(impAll);
		return new AFromModuleImports(from, types);
	}
}
