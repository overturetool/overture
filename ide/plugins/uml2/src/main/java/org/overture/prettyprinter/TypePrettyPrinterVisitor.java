package org.overture.prettyprinter;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.node.INode;
import org.overture.ast.types.AInMapMapType;
import org.overture.ast.types.AMapMapType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASeq1SeqType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SBasicType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.util.Utils;

public class TypePrettyPrinterVisitor extends
		QuestionAnswerAdaptor<PrettyPrinterEnv, String>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9082823353484822934L;
	
	@Override
	public String defaultSBasicType(SBasicType node, PrettyPrinterEnv question)
			throws AnalysisException
	{
		return node.toString();
	}
	
	@Override
	public String defaultINode(INode node, PrettyPrinterEnv question)
			throws AnalysisException
	{
		return node.toString();
	}
	
	@Override
	public String caseASetType(ASetType node, PrettyPrinterEnv question)
			throws AnalysisException
	{
		return "" + ""+(node.getEmpty() ? "{}" : "set of (" +node.getSetof().apply(this,question) + ")");
	}
	
	@Override
	public String caseASeqSeqType(ASeqSeqType node, PrettyPrinterEnv question)
			throws AnalysisException
	{
		return "" + ""+(node.getEmpty() ? "[]" : "seq of (" + node.getSeqof().apply(this,question)+ ")" );
	}
	
	@Override
	public String caseASeq1SeqType(ASeq1SeqType node, PrettyPrinterEnv question)
			throws AnalysisException
	{
		return "" + "seq1 of ("+node.getSeqof().apply(this,question)+")";
	}
	
	@Override
	public String caseAMapMapType(AMapMapType node, PrettyPrinterEnv question)
			throws AnalysisException
	{
		return "" + "map ("+node.getFrom().apply(this,question)+") to ("+node.getTo().apply(this,question)+")";
	}
	
	@Override
	public String caseAInMapMapType(AInMapMapType node,
			PrettyPrinterEnv question) throws AnalysisException
	{
		return "" + "inmap ("+node.getFrom().apply(this,question)+") to ("+node.getTo().apply(this,question)+")";
	}
	
	@Override
	public String caseAProductType(AProductType node, PrettyPrinterEnv question)
			throws AnalysisException
	{
		List<String> types = new Vector<String>();
		for (PType t : node.getTypes())
		{
			types.add(t.apply(this,question));
		}
		return "" + ""+Utils.listToString("(",types, " * ", ")");
	}
	
	@Override
	public String caseAOptionalType(AOptionalType node,
			PrettyPrinterEnv question) throws AnalysisException
	{
		return "" + "["+node.getType().apply(this,question)+"]";
	}
	
	@Override
	public String caseAUnionType(AUnionType node, PrettyPrinterEnv question)
			throws AnalysisException
	{
		List<String> types = new Vector<String>();
		for (PType t : node.getTypes())
		{
			types.add(t.apply(this,question));
		}
		return "" + ""+(types.size() == 1?types.iterator().next().toString() : Utils.setToString(types, " | "));
	}

	@Override
	public String defaultSInvariantType(SInvariantType node,
			PrettyPrinterEnv question) throws AnalysisException
	{
		LexNameToken name = null;
		switch(node.kindSInvariantType())
		{
			case NAMED:
				name = ((ANamedInvariantType)node).getName();
				break;
			case RECORD:
				name = ((ARecordInvariantType)node).getName();
				break;
			
		}
		if(name !=null)
		{
			if(name.getModule()!=null && !name.getModule().equals(question.getClassName()))
			{
				return name.module+"`"+name.getName();
			}
			return name.getName();
		}
		
		return "unresolved";
	}
	
}
