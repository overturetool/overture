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
package org.overture.codegen.ir;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.node.INode;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.codegen.assistant.AssistantManager;
import org.overture.codegen.assistant.BindAssistantIR;
import org.overture.codegen.assistant.DeclAssistantIR;
import org.overture.codegen.assistant.ExpAssistantIR;
import org.overture.codegen.assistant.LocationAssistantIR;
import org.overture.codegen.assistant.NodeAssistantIR;
import org.overture.codegen.assistant.PatternAssistantIR;
import org.overture.codegen.assistant.StmAssistantIR;
import org.overture.codegen.assistant.TypeAssistantIR;
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
import org.overture.codegen.ir.expressions.SVarExpIR;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.visitor.IRVisitor;
import org.overture.codegen.visitor.VisitorManager;
import org.overture.typechecker.assistant.TypeCheckerAssistantFactory;

public class IRInfo
{
	// Visitors
	private VisitorManager visitorManager;

	// Assistants
	private AssistantManager assistantManager;

	// VDM assistant factory
	private TypeCheckerAssistantFactory tcFactory;

	// Quotes:
	private List<String> quoteVaues;

	// Unsupported VDM nodes
	private Set<VdmNodeInfo> unsupportedNodes;
	
	// Transformation warnings
	private Set<IrNodeInfo> transformationWarnings;

	// For generating variable names
	private ITempVarGen tempVarNameGen;

	// For configuring code generation
	private IRSettings settings;

	// Definitions for identifier state designators
	private Map<AIdentifierStateDesignator, PDefinition> idStateDesignatorDefs;
	
	// IR classes
	private List<SClassDeclIR> classes;
	
	// IR modules
	private List<AModuleDeclIR> modules;
	
	// SL state reads
	private List<SVarExpIR> slStateReads;
	
	public IRInfo()
	{
		super();

		this.visitorManager = new VisitorManager();
		this.assistantManager = new AssistantManager();
		this.tcFactory = new TypeCheckerAssistantFactory();
		this.quoteVaues = new LinkedList<String>();
		this.unsupportedNodes = new HashSet<VdmNodeInfo>();
		this.transformationWarnings = new HashSet<IrNodeInfo>();
		this.tempVarNameGen = new TempVarNameGen();

		this.settings = new IRSettings();

		this.idStateDesignatorDefs = new HashMap<AIdentifierStateDesignator, PDefinition>();
		this.classes = new LinkedList<SClassDeclIR>();
		this.modules = new LinkedList<AModuleDeclIR>();
		this.slStateReads = new LinkedList<>();
	}

	public AssistantManager getAssistantManager()
	{
		return assistantManager;
	}

	public IRVisitor<SClassDeclIR> getClassVisitor()
	{
		return visitorManager.getClassVisitor();
	}
	
	public IRVisitor<AModuleDeclIR> getModuleVisitor()
	{
		return visitorManager.getModuleVisitor();
	}
	
	public IRVisitor<SImportsIR> getImportsVisitor()
	{
		return visitorManager.getImportsVisitor();
	}
	
	public IRVisitor<SImportIR> getImportVisitor()
	{
		return visitorManager.getImportVisitor();
	}

	public IRVisitor<SExportsIR> getExportsVisitor()
	{
		return visitorManager.getExportsVisitor();
	}
	
	public IRVisitor<SExportIR> getExportVisitor()
	{
		return visitorManager.getExportVisitor();
	}
	
	public IRVisitor<SDeclIR> getDeclVisitor()
	{
		return visitorManager.getDeclVisitor();
	}

	public IRVisitor<SExpIR> getExpVisitor()
	{
		return visitorManager.getExpVisitor();
	}

	public IRVisitor<STypeIR> getTypeVisitor()
	{
		return visitorManager.getTypeVisitor();
	}

	public IRVisitor<SStmIR> getStmVisitor()
	{
		return visitorManager.getStmVisitor();
	}

	public IRVisitor<SStateDesignatorIR> getStateDesignatorVisitor()
	{
		return visitorManager.getStateDesignatorVisitor();
	}

	public IRVisitor<SObjectDesignatorIR> getObjectDesignatorVisitor()
	{
		return visitorManager.getObjectDesignatorVisitor();
	}

	public IRVisitor<SMultipleBindIR> getMultipleBindVisitor()
	{
		return visitorManager.getMultipleBindVisitor();
	}

	public IRVisitor<SBindIR> getBindVisitor()
	{
		return visitorManager.getBindVisitor();
	}

	public IRVisitor<SPatternIR> getPatternVisitor()
	{
		return visitorManager.getPatternVisitor();
	}

	public IRVisitor<SModifierIR> getModifierVisitor()
	{
		return visitorManager.getModifierVisitor();
	}

	public IRVisitor<STermIR> getTermVisitor()
	{
		return visitorManager.getTermVisitor();
	}

	public IRVisitor<STraceDeclIR> getTraceDeclVisitor()
	{
		return visitorManager.getTraceDeclVisitor();
	}

	public IRVisitor<STraceCoreDeclIR> getTraceCoreDeclVisitor()
	{
		return visitorManager.getTraceCoreDeclVisitor();
	}

	public NodeAssistantIR getNodeAssistant()
	{
		return assistantManager.getNodeAssistant();
	}
	
	public ExpAssistantIR getExpAssistant()
	{
		return assistantManager.getExpAssistant();
	}

	public DeclAssistantIR getDeclAssistant()
	{
		return assistantManager.getDeclAssistant();
	}

	public StmAssistantIR getStmAssistant()
	{
		return assistantManager.getStmAssistant();
	}

	public TypeAssistantIR getTypeAssistant()
	{
		return assistantManager.getTypeAssistant();
	}

	public LocationAssistantIR getLocationAssistant()
	{
		return assistantManager.getLocationAssistant();
	}

	public BindAssistantIR getBindAssistant()
	{
		return assistantManager.getBindAssistant();
	}
	
	public PatternAssistantIR getPatternAssistant()
	{
		return assistantManager.getPatternAssistant();
	}

	public void registerQuoteValue(String value)
	{
		if (value == null || value.isEmpty())
		{
			Logger.getLog().printErrorln("Tried to register invalid qoute value");
		} else
		{
			if (!quoteVaues.contains(value))
			{
				quoteVaues.add(value);
			}
		}
	}

	public TypeCheckerAssistantFactory getTcFactory()
	{
		return tcFactory;
	}

	public List<String> getQuoteValues()
	{
		return quoteVaues;
	}

	public void clearNodes()
	{
		unsupportedNodes.clear();
	}
	
	public void addUnsupportedNode(INode node, String reason)
	{
		for(VdmNodeInfo info : unsupportedNodes)
		{
			if(VdmNodeInfo.matches(info, node, reason))
			{
				return;
			}
		}
		
		VdmNodeInfo info = new VdmNodeInfo(node, reason);
		unsupportedNodes.add(info);
	}

	public Set<VdmNodeInfo> getUnsupportedNodes()
	{
		return unsupportedNodes;
	}
	
	public void clearTransformationWarnings()
	{
		transformationWarnings.clear();
	}
	
	public void addTransformationWarning(org.overture.codegen.ir.INode node, String warning)
	{
		IrNodeInfo info = new IrNodeInfo(node, warning);
		transformationWarnings.add(info);
	}
	
	public Set<IrNodeInfo> getTransformationWarnings()
	{
		return transformationWarnings;
	}

	public ITempVarGen getTempVarNameGen()
	{
		return tempVarNameGen;
	}
	
	public void clear()
	{
		quoteVaues.clear();
		unsupportedNodes.clear();
		transformationWarnings.clear();
		tempVarNameGen.clear();
		idStateDesignatorDefs.clear();
		classes.clear();
		modules.clear();
	}

	public IRSettings getSettings()
	{
		return settings;
	}

	public void setSettings(IRSettings settings)
	{
		this.settings = settings;
	}

	public Map<AIdentifierStateDesignator, PDefinition> getIdStateDesignatorDefs()
	{
		return idStateDesignatorDefs;
	}

	public void setIdStateDesignatorDefs(Map<AIdentifierStateDesignator, PDefinition> idDefs)
	{
		this.idStateDesignatorDefs = idDefs;
	}

	public List<SClassDeclIR> getClasses()
	{
		return classes;
	}

	public void addClass(SClassDeclIR irClass)
	{
		if(this.classes != null)
		{
			this.classes.add(irClass);
		}
	}
	
	public void removeClass(String name)
	{
		SClassDeclIR classToRemove = null;
		
		for (SClassDeclIR clazz : classes)
		{
			if(clazz.getName().equals(name))
			{
				classToRemove = clazz;
				break;
			}
		}
		
		if(classToRemove != null)
		{
			classes.remove(classToRemove);
		}
	}
	
	public void clearClasses()
	{
		if(this.classes != null)
		{
			this.classes.clear();
		}
	}
	
	public List<AModuleDeclIR> getModules()
	{
		return modules;
	}
	
	public void addModule(AModuleDeclIR irModule)
	{
		if(this.modules != null)
		{
			this.modules.add(irModule);
		}
	}
	
	public void removeModule(String name)
	{
		AModuleDeclIR moduleToRemove = null;
		
		for (AModuleDeclIR module : modules)
		{
			if(module.getName().equals(name))
			{
				moduleToRemove = module;
				break;
			}
		}
		
		if(moduleToRemove != null)
		{
			modules.remove(moduleToRemove);
		}
	}
	
	public void clearModules()
	{
		if(this.modules != null)
		{
			this.modules.clear();
		}
	}
	
	public void registerSlStateRead(SVarExpIR var)
	{
		this.slStateReads.add(var);
	}
	
	public boolean isSlStateRead(SVarExpIR var)
	{
		for(SVarExpIR v : slStateReads)
		{
			if(v == var)
			{
				return true;	
			}
		}
		
		return false;
	}
}
