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
import org.overture.codegen.ir.declarations.ABusClassDeclIR;
import org.overture.codegen.ir.declarations.ACpuClassDeclIR;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.ASystemClassDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.IRInfo;

public class ClassVisitorIR extends AbstractVisitorIR<IRInfo, SClassDeclIR>
{
	public ClassVisitorIR()
	{
	}
	
	@Override
	public ADefaultClassDeclIR caseAClassClassDefinition(AClassClassDefinition node,
			IRInfo question) throws AnalysisException
	{
		return question.getDeclAssistant().buildClass(node, question, new ADefaultClassDeclIR());
	}
	
	@Override
	public SClassDeclIR caseACpuClassDefinition(ACpuClassDefinition node, IRInfo question)
			throws AnalysisException
	{
		return question.getDeclAssistant().buildClass(node, question, new ACpuClassDeclIR());
	}
	
	@Override
	public SClassDeclIR caseASystemClassDefinition(ASystemClassDefinition node, IRInfo question)
			throws AnalysisException
	{
		return question.getDeclAssistant().buildClass(node, question, new ASystemClassDeclIR());
	}
	
	@Override
	public SClassDeclIR caseABusClassDefinition(ABusClassDefinition node, IRInfo question)
			throws AnalysisException
	{
		return question.getDeclAssistant().buildClass(node, question, new ABusClassDeclIR());
	}
}
