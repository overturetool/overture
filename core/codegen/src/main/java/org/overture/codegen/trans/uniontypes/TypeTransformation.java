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
package org.overture.codegen.trans.uniontypes;

import java.util.LinkedList;

import org.overture.codegen.cgast.PCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.AQuoteTypeCG;
import org.overture.codegen.cgast.types.AUnionTypeCG;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.trans.assistants.BaseTransformationAssistant;

public class TypeTransformation extends DepthFirstAnalysisAdaptor
{
	private BaseTransformationAssistant baseAssistant;

	public TypeTransformation(BaseTransformationAssistant baseAssistant)
	{
		this.baseAssistant = baseAssistant;
	}

	public AIntNumericBasicTypeCG consIntType(PCG node)
	{
		SourceNode sourceNode = node.getSourceNode();

		AIntNumericBasicTypeCG intType = new AIntNumericBasicTypeCG();
		intType.setSourceNode(sourceNode);

		return intType;
	}

	@Override
	public void caseAQuoteTypeCG(AQuoteTypeCG node) throws AnalysisException
	{
		baseAssistant.replaceNodeWith(node, consIntType(node));
	}

	@Override
	public void caseAUnionTypeCG(AUnionTypeCG node) throws AnalysisException
	{
		LinkedList<STypeCG> types = node.getTypes();

		for (STypeCG type : types)
		{
			type.apply(this);
		}

		boolean unionOfInts = true;

		for (STypeCG type : types)
		{
			if (!(type instanceof AIntNumericBasicTypeCG))
			{
				unionOfInts = false;
				break;
			}
		}

		if (unionOfInts)
		{
			baseAssistant.replaceNodeWith(node, consIntType(node));
		}
	}
}
