/*
 * #%~
 * The VDM Type Checker
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
package org.overture.typechecker.utilities;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.node.INode;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AInMapMapType;
import org.overture.ast.types.AMapMapType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASeq1SeqType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.SSetType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;

/**
 * This class implements a way to collect compose types from a node in the AST
 * 
 * @author Nick Battle
 */
public class ComposeTypeCollector extends AnswerAdaptor<PTypeList>
{
	public ComposeTypeCollector()
	{
	}

	@Override
	public PTypeList createNewReturnValue(INode node) throws AnalysisException
	{
		return new PTypeList();
	}

	@Override
	public PTypeList createNewReturnValue(Object node) throws AnalysisException
	{
		return new PTypeList();
	}

	@Override
	public PTypeList caseABracketType(ABracketType node)
			throws AnalysisException
	{
		return node.getType().apply(THIS);
	}

	@Override
	public PTypeList caseAOptionalType(AOptionalType node)
			throws AnalysisException
	{
		return node.getType().apply(THIS);
	}

	@Override
	public PTypeList caseASeqSeqType(ASeqSeqType node) throws AnalysisException
	{
		return node.getSeqof().apply(THIS);
	}

	@Override
	public PTypeList caseASeq1SeqType(ASeq1SeqType node)
			throws AnalysisException
	{
		return node.getSeqof().apply(THIS);
	}

	@Override
	public PTypeList defaultSSetType(SSetType node) throws AnalysisException
	{
		return node.getSetof().apply(THIS);
	}

	@Override
	public PTypeList caseAProductType(AProductType node)
			throws AnalysisException
	{
		PTypeList list = new PTypeList();

		for (PType ptype : node.getTypes())
		{
			list.addAll(ptype.apply(THIS));
		}

		return list;
	}

	@Override
	public PTypeList caseAUnionType(AUnionType node) throws AnalysisException
	{
		PTypeList list = new PTypeList();

		for (PType ptype : node.getTypes())
		{
			list.addAll(ptype.apply(THIS));
		}

		return list;
	}

	@Override
	public PTypeList caseAFunctionType(AFunctionType node)
			throws AnalysisException
	{
		PTypeList list = new PTypeList();

		for (PType ptype : node.getParameters())
		{
			list.addAll(ptype.apply(THIS));
		}

		list.addAll(node.getResult().apply(THIS));
		return list;
	}

	@Override
	public PTypeList caseAInMapMapType(AInMapMapType node)
			throws AnalysisException
	{
		PTypeList list = new PTypeList();
		list.addAll(node.getFrom().apply(THIS));
		list.addAll(node.getTo().apply(THIS));
		return list;
	}

	@Override
	public PTypeList caseAMapMapType(AMapMapType node) throws AnalysisException
	{
		PTypeList list = new PTypeList();
		list.addAll(node.getFrom().apply(THIS));
		list.addAll(node.getTo().apply(THIS));
		return list;
	}

	@Override
	public PTypeList caseAOperationType(AOperationType node)
			throws AnalysisException
	{
		PTypeList list = new PTypeList();

		for (PType ptype : node.getParameters())
		{
			list.addAll(ptype.apply(THIS));
		}

		list.addAll(node.getResult().apply(THIS));
		return list;
	}

	@Override
	public PTypeList caseARecordInvariantType(ARecordInvariantType node)
			throws AnalysisException
	{
		if (node.getComposed())
		{
			PTypeList types = new PTypeList(node);

			for (AFieldField f : node.getFields())
			{
				types.addAll(f.getType().apply(THIS));
			}

			return types;
		} else
		{
			return new PTypeList();
		}
	}
}
