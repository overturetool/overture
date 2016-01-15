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

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.ABusClassDefinition;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.ACpuClassDefinition;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.codegen.cgast.declarations.ABusClassDeclCG;
import org.overture.codegen.cgast.declarations.ACpuClassDeclCG;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.ASystemClassDeclCG;
import org.overture.codegen.cgast.declarations.SClassDeclCG;
import org.overture.codegen.ir.IRInfo;

public class ClassVisitorCG extends AbstractVisitorCG<IRInfo, SClassDeclCG>
{
	public ClassVisitorCG()
	{
	}
	
	@Override
	public ADefaultClassDeclCG caseAClassClassDefinition(AClassClassDefinition node,
			IRInfo question) throws AnalysisException
	{
		return question.getDeclAssistant().buildClass(node, question, new ADefaultClassDeclCG());
	}
	
	@Override
	public SClassDeclCG caseACpuClassDefinition(ACpuClassDefinition node, IRInfo question)
			throws AnalysisException
	{
		return question.getDeclAssistant().buildClass(node, question, new ACpuClassDeclCG());
	}
	
	@Override
	public SClassDeclCG caseASystemClassDefinition(ASystemClassDefinition node, IRInfo question)
			throws AnalysisException
	{
		return question.getDeclAssistant().buildClass(node, question, new ASystemClassDeclCG());
	}
	
	@Override
	public SClassDeclCG caseABusClassDefinition(ABusClassDefinition node, IRInfo question)
			throws AnalysisException
	{
		return question.getDeclAssistant().buildClass(node, question, new ABusClassDeclCG());
	}
}
