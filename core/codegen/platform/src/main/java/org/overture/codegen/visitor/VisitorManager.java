/*
 * #%~
 * VDM Code Generator
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
package org.overture.codegen.visitor;

import org.overture.codegen.cgast.SBindCG;
import org.overture.codegen.cgast.SDeclCG;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SExportCG;
import org.overture.codegen.cgast.SExportsCG;
import org.overture.codegen.cgast.SImportCG;
import org.overture.codegen.cgast.SImportsCG;
import org.overture.codegen.cgast.SModifierCG;
import org.overture.codegen.cgast.SMultipleBindCG;
import org.overture.codegen.cgast.SObjectDesignatorCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.SStateDesignatorCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STermCG;
import org.overture.codegen.cgast.STraceCoreDeclCG;
import org.overture.codegen.cgast.STraceDeclCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.AModuleDeclCG;
import org.overture.codegen.cgast.declarations.SClassDeclCG;
import org.overture.codegen.traces.TermVisitorCG;
import org.overture.codegen.traces.TraceCoreDeclVisitorCG;
import org.overture.codegen.traces.TraceDeclVisitorCG;

public class VisitorManager
{
	private CGVisitor<SClassDeclCG> classVisitor;
	private CGVisitor<AModuleDeclCG> moduleVisitor;
	private CGVisitor<SImportsCG> importsVisitor;
	private CGVisitor<SImportCG> importVisitor;
	private CGVisitor<SExportsCG> exportsVisitor;
	private CGVisitor<SExportCG> exportVisitor;
	private CGVisitor<SDeclCG> declVisitor;
	private CGVisitor<SExpCG> expVisitor;
	private CGVisitor<STypeCG> typeVisitor;
	private CGVisitor<SStmCG> stmVisitor;
	private CGVisitor<SStateDesignatorCG> stateDesignatorVisitor;
	private CGVisitor<SObjectDesignatorCG> objectDesignatorVisitor;
	private CGVisitor<SMultipleBindCG> multipleBindVisitor;
	private CGVisitor<SBindCG> bindVisitor;
	private CGVisitor<SPatternCG> patternVisitor;
	private CGVisitor<SModifierCG> modifierVisitor;
	private CGVisitor<STermCG> termVisitor;
	private CGVisitor<STraceDeclCG> traceDeclVisitor;
	private CGVisitor<STraceCoreDeclCG> traceCoreDeclVisitor;

	public VisitorManager()
	{
		this.classVisitor = new CGVisitor<SClassDeclCG>(new ClassVisitorCG());
		this.moduleVisitor = new CGVisitor<AModuleDeclCG>(new ModuleVisitorCG());
		this.importsVisitor = new CGVisitor<SImportsCG>(new ImportsVisitorCG());
		this.importVisitor = new CGVisitor<SImportCG>(new ImportVisitorCG());
		this.exportsVisitor = new CGVisitor<SExportsCG>(new ExportsVisitorCG());
		this.exportVisitor = new CGVisitor<SExportCG>(new ExportVisitorCG());
		this.declVisitor = new CGVisitor<SDeclCG>(new DeclVisitorCG());
		this.expVisitor = new CGVisitor<SExpCG>(new ExpVisitorCG());
		this.typeVisitor = new CGVisitorRecursiveTypeHandler(new TypeVisitorCG());
		this.stmVisitor = new CGVisitor<SStmCG>(new StmVisitorCG());
		this.stateDesignatorVisitor = new CGVisitor<SStateDesignatorCG>(new StateDesignatorVisitorCG());
		this.objectDesignatorVisitor = new CGVisitor<SObjectDesignatorCG>(new ObjectDesignatorVisitorCG());
		this.multipleBindVisitor = new CGVisitor<SMultipleBindCG>(new MultipleBindVisitorCG());
		this.bindVisitor = new CGVisitor<SBindCG>(new BindVisitorCG());
		this.patternVisitor = new CGVisitor<SPatternCG>(new PatternVisitorCG());
		this.modifierVisitor = new CGVisitor<SModifierCG>(new ModifierVisitorCG());
		this.termVisitor = new CGVisitor<STermCG>(new TermVisitorCG());
		this.traceDeclVisitor = new CGVisitor<STraceDeclCG>(new TraceDeclVisitorCG());
		this.traceCoreDeclVisitor = new CGVisitor<STraceCoreDeclCG>(new TraceCoreDeclVisitorCG());
	}

	public CGVisitor<SClassDeclCG> getClassVisitor()
	{
		return classVisitor;
	}
	
	public CGVisitor<AModuleDeclCG> getModuleVisitor()
	{
		return moduleVisitor;
	}
	
	public CGVisitor<SImportsCG> getImportsVisitor()
	{
		return importsVisitor;
	}
	
	public CGVisitor<SImportCG> getImportVisitor()
	{
		return importVisitor;
	}
	
	public CGVisitor<SExportsCG> getExportsVisitor()
	{
		return exportsVisitor;
	}
	
	public CGVisitor<SExportCG> getExportVisitor()
	{
		return exportVisitor;
	}

	public CGVisitor<SDeclCG> getDeclVisitor()
	{
		return declVisitor;
	}

	public CGVisitor<SExpCG> getExpVisitor()
	{
		return expVisitor;
	}

	public CGVisitor<STypeCG> getTypeVisitor()
	{
		return typeVisitor;
	}

	public CGVisitor<SStmCG> getStmVisitor()
	{
		return stmVisitor;
	}

	public CGVisitor<SStateDesignatorCG> getStateDesignatorVisitor()
	{
		return stateDesignatorVisitor;
	}

	public CGVisitor<SObjectDesignatorCG> getObjectDesignatorVisitor()
	{
		return objectDesignatorVisitor;
	}

	public CGVisitor<SMultipleBindCG> getMultipleBindVisitor()
	{
		return multipleBindVisitor;
	}

	public CGVisitor<SBindCG> getBindVisitor()
	{
		return bindVisitor;
	}

	public CGVisitor<SPatternCG> getPatternVisitor()
	{
		return patternVisitor;
	}
	
	public CGVisitor<SModifierCG> getModifierVisitor()
	{
		return modifierVisitor;
	}

	public CGVisitor<STermCG> getTermVisitor()
	{
		return termVisitor;
	}

	public CGVisitor<STraceDeclCG> getTraceDeclVisitor()
	{
		return traceDeclVisitor;
	}

	public CGVisitor<STraceCoreDeclCG> getTraceCoreDeclVisitor()
	{
		return traceCoreDeclVisitor;
	}
}
