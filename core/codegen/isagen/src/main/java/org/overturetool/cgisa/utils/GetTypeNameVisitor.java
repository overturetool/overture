package org.overturetool.cgisa.utils;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.AnswerAdaptor;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.AMapMapTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.ASeqSeqTypeCG;
import org.overture.codegen.cgast.types.ASetSetTypeCG;
import org.overture.codegen.cgast.types.ATokenBasicTypeCG;
import org.overture.codegen.cgast.types.AUnionTypeCG;

public class GetTypeNameVisitor extends AnswerAdaptor<String>
{

	@Override
	public String createNewReturnValue(INode node) throws AnalysisException
	{
		return "";
	}

	@Override
	public String createNewReturnValue(Object node) throws AnalysisException
	{
		return "";
	}

	@Override
	public String caseARecordTypeCG(ARecordTypeCG node)
			throws AnalysisException
	{
		return "@" + node.getName().getName();
	}

	@Override
	public String caseASeqSeqTypeCG(ASeqSeqTypeCG node)
			throws AnalysisException
	{
		if (node.getNamedInvType() != null)
		{
			return "@" + node.getNamedInvType().getName().getName();
		}

		return "@seq of " + node.getSeqOf().apply(this);

	}

	@Override
	public String caseAMapMapTypeCG(AMapMapTypeCG node)
			throws AnalysisException
	{
		if (node.getNamedInvType() != null)
		{
			return "@" + node.getNamedInvType().getName().getName();
		}
		return "@map " + node.getFrom().apply(this) + " to "
				+ node.getTo().apply(this);
	}

	@Override
	public String caseACharBasicTypeCG(ACharBasicTypeCG node)
			throws AnalysisException
	{
		if (node.getNamedInvType() != null)
		{
			return "@" + node.getNamedInvType().getName().getName();
		}

		return "@char";
	}

	@Override
	public String caseATokenBasicTypeCG(ATokenBasicTypeCG node)
			throws AnalysisException
	{
		if (node.getNamedInvType() != null)
		{
			return "@" + node.getNamedInvType().getName().getName();
		}
		return "@token";
	}

	@Override
	public String caseAUnionTypeCG(AUnionTypeCG node) throws AnalysisException
	{
		return "@" + node.getNamedInvType().getName().getName();
	}

	@Override
	public String caseASetSetTypeCG(ASetSetTypeCG node)
			throws AnalysisException
	{
		if (node.getNamedInvType() != null)
		{
			return "@" + node.getNamedInvType().getName().getName();
		}
		return "@set of " + node.getSetOf().apply(this);
	}
}
