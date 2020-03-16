/*
 * #%~
 * The Overture Abstract Syntax Tree
 * %%
 * Copyright (C) 2008 - 2019 Overture
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

package org.overture.typechecker.utilities.type;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AInMapMapType;
import org.overture.ast.types.AMapMapType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASeq1SeqType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.ASet1SetType;
import org.overture.ast.types.ASetSetType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;

/**
 * Search for all of the ParameterType definitions in a type.
 */
public class ParameterFinder extends AnswerAdaptor<List<String>>
{
	@Override
	public List<String> caseAParameterType(AParameterType node) throws AnalysisException
	{
		List <String> all = new Vector<String>();
		all.add("@" + node.getName());
		return all;
	}

	@Override
	public List<String> caseAFunctionType(AFunctionType node) throws AnalysisException
	{
		List<String> all = new Vector<String>();

		for (PType param: node.getParameters())
		{
			all.addAll(param.apply(this));
		}

		all.addAll(node.getResult().apply(this));
		return all;
	}

	@Override
	public List<String> caseAInMapMapType(AInMapMapType node) throws AnalysisException
	{
		List<String> all = new Vector<String>();
		all.addAll(node.getFrom().apply(this));
		all.addAll(node.getTo().apply(this));
		return all;
	}

	@Override
	public List<String> caseAMapMapType(AMapMapType node) throws AnalysisException
	{
		List<String> all = new Vector<String>();
		all.addAll(node.getFrom().apply(this));
		all.addAll(node.getTo().apply(this));
		return all;
	}

	@Override
	public List<String> caseANamedInvariantType(ANamedInvariantType node) throws AnalysisException
	{
		return node.getType().apply(this);
	}

	@Override
	public List<String> caseAOperationType(AOperationType node) throws AnalysisException
	{
		List<String> all = new Vector<String>();

		for (PType param: node.getParameters())
		{
			all.addAll(param.apply(this));
		}

		all.addAll(node.getResult().apply(this));
		return all;
	}

	@Override
	public List<String> caseAOptionalType(AOptionalType node) throws AnalysisException
	{
		return node.getType().apply(this);
	}

	@Override
	public List<String> caseAProductType(AProductType node) throws AnalysisException
	{
		List<String> all = new Vector<String>();

		for (PType param: node.getTypes())
		{
			all.addAll(param.apply(this));
		}

		return all;
	}

	@Override
	public List<String> caseARecordInvariantType(ARecordInvariantType node) throws AnalysisException
	{
		List<String> all = new Vector<String>();

		for (AFieldField field: node.getFields())
		{
			all.addAll(field.getType().apply(this));
		}

		return all;
	}

	@Override
	public List<String> caseASeq1SeqType(ASeq1SeqType node) throws AnalysisException
	{
		List<String> all = new Vector<String>();
		all.addAll(node.getSeqof().apply(this));
		return all;
	}

	@Override
	public List<String> caseASeqSeqType(ASeqSeqType node) throws AnalysisException
	{
		List<String> all = new Vector<String>();
		all.addAll(node.getSeqof().apply(this));
		return all;
	}

	@Override
	public List<String> caseASet1SetType(ASet1SetType node) throws AnalysisException
	{
		List<String> all = new Vector<String>();
		all.addAll(node.getSetof().apply(this));
		return all;
	}

	@Override
	public List<String> caseASetSetType(ASetSetType node) throws AnalysisException
	{
		List<String> all = new Vector<String>();
		all.addAll(node.getSetof().apply(this));
		return all;
	}

	@Override
	public List<String> caseAUnionType(AUnionType node) throws AnalysisException
	{
		List<String> all = new Vector<String>();

		for (PType param: node.getTypes())
		{
			all.addAll(param.apply(this));
		}

		return all;
	}

	@Override
	public List<String> createNewReturnValue(INode node) throws AnalysisException
	{
		return new Vector<String>();
	}

	@Override
	public List<String> createNewReturnValue(Object node) throws AnalysisException
	{
		return new Vector<String>();
	}
}
