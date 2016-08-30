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

import org.overture.codegen.ir.SBindIR;
import org.overture.codegen.ir.SDeclIR;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SExportIR;
import org.overture.codegen.ir.SExportsIR;
import org.overture.codegen.ir.SImportIR;
import org.overture.codegen.ir.SImportsIR;
import org.overture.codegen.ir.SModifierIR;
import org.overture.codegen.ir.SMultipleBindIR;
import org.overture.codegen.ir.SObjectDesignatorIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.SStateDesignatorIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STermIR;
import org.overture.codegen.ir.STraceCoreDeclIR;
import org.overture.codegen.ir.STraceDeclIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.declarations.AModuleDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.traces.TermVisitorIR;
import org.overture.codegen.traces.TraceCoreDeclVisitorIR;
import org.overture.codegen.traces.TraceDeclVisitorIR;

public class VisitorManager
{
	private IRVisitor<SClassDeclIR> classVisitor;
	private IRVisitor<AModuleDeclIR> moduleVisitor;
	private IRVisitor<SImportsIR> importsVisitor;
	private IRVisitor<SImportIR> importVisitor;
	private IRVisitor<SExportsIR> exportsVisitor;
	private IRVisitor<SExportIR> exportVisitor;
	private IRVisitor<SDeclIR> declVisitor;
	private IRVisitor<SExpIR> expVisitor;
	private IRVisitor<STypeIR> typeVisitor;
	private IRVisitor<SStmIR> stmVisitor;
	private IRVisitor<SStateDesignatorIR> stateDesignatorVisitor;
	private IRVisitor<SObjectDesignatorIR> objectDesignatorVisitor;
	private IRVisitor<SMultipleBindIR> multipleBindVisitor;
	private IRVisitor<SBindIR> bindVisitor;
	private IRVisitor<SPatternIR> patternVisitor;
	private IRVisitor<SModifierIR> modifierVisitor;
	private IRVisitor<STermIR> termVisitor;
	private IRVisitor<STraceDeclIR> traceDeclVisitor;
	private IRVisitor<STraceCoreDeclIR> traceCoreDeclVisitor;

	public VisitorManager()
	{
		this.classVisitor = new IRVisitor<SClassDeclIR>(new ClassVisitorIR());
		this.moduleVisitor = new IRVisitor<AModuleDeclIR>(new ModuleVisitorIR());
		this.importsVisitor = new IRVisitor<SImportsIR>(new ImportsVisitorIR());
		this.importVisitor = new IRVisitor<SImportIR>(new ImportVisitorIR());
		this.exportsVisitor = new IRVisitor<SExportsIR>(new ExportsVisitorIR());
		this.exportVisitor = new IRVisitor<SExportIR>(new ExportVisitorIR());
		this.declVisitor = new IRVisitor<SDeclIR>(new DeclVisitorIR());
		this.expVisitor = new IRVisitor<SExpIR>(new ExpVisitorIR());
		this.typeVisitor = new IRVisitorRecursiveTypeHandler(new TypeVisitorIR());
		this.stmVisitor = new IRVisitor<SStmIR>(new StmVisitorIR());
		this.stateDesignatorVisitor = new IRVisitor<SStateDesignatorIR>(new StateDesignatorVisitorIR());
		this.objectDesignatorVisitor = new IRVisitor<SObjectDesignatorIR>(new ObjectDesignatorVisitorIR());
		this.multipleBindVisitor = new IRVisitor<SMultipleBindIR>(new MultipleBindVisitorIR());
		this.bindVisitor = new IRVisitor<SBindIR>(new BindVisitorIR());
		this.patternVisitor = new IRVisitor<SPatternIR>(new PatternVisitorIR());
		this.modifierVisitor = new IRVisitor<SModifierIR>(new ModifierVisitorIR());
		this.termVisitor = new IRVisitor<STermIR>(new TermVisitorIR());
		this.traceDeclVisitor = new IRVisitor<STraceDeclIR>(new TraceDeclVisitorIR());
		this.traceCoreDeclVisitor = new IRVisitor<STraceCoreDeclIR>(new TraceCoreDeclVisitorIR());
	}

	public IRVisitor<SClassDeclIR> getClassVisitor()
	{
		return classVisitor;
	}

	public IRVisitor<AModuleDeclIR> getModuleVisitor()
	{
		return moduleVisitor;
	}

	public IRVisitor<SImportsIR> getImportsVisitor()
	{
		return importsVisitor;
	}

	public IRVisitor<SImportIR> getImportVisitor()
	{
		return importVisitor;
	}

	public IRVisitor<SExportsIR> getExportsVisitor()
	{
		return exportsVisitor;
	}

	public IRVisitor<SExportIR> getExportVisitor()
	{
		return exportVisitor;
	}

	public IRVisitor<SDeclIR> getDeclVisitor()
	{
		return declVisitor;
	}

	public IRVisitor<SExpIR> getExpVisitor()
	{
		return expVisitor;
	}

	public IRVisitor<STypeIR> getTypeVisitor()
	{
		return typeVisitor;
	}

	public IRVisitor<SStmIR> getStmVisitor()
	{
		return stmVisitor;
	}

	public IRVisitor<SStateDesignatorIR> getStateDesignatorVisitor()
	{
		return stateDesignatorVisitor;
	}

	public IRVisitor<SObjectDesignatorIR> getObjectDesignatorVisitor()
	{
		return objectDesignatorVisitor;
	}

	public IRVisitor<SMultipleBindIR> getMultipleBindVisitor()
	{
		return multipleBindVisitor;
	}

	public IRVisitor<SBindIR> getBindVisitor()
	{
		return bindVisitor;
	}

	public IRVisitor<SPatternIR> getPatternVisitor()
	{
		return patternVisitor;
	}

	public IRVisitor<SModifierIR> getModifierVisitor()
	{
		return modifierVisitor;
	}

	public IRVisitor<STermIR> getTermVisitor()
	{
		return termVisitor;
	}

	public IRVisitor<STraceDeclIR> getTraceDeclVisitor()
	{
		return traceDeclVisitor;
	}

	public IRVisitor<STraceCoreDeclIR> getTraceCoreDeclVisitor()
	{
		return traceCoreDeclVisitor;
	}
}
