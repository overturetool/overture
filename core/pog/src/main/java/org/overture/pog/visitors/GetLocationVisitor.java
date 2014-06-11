package org.overture.pog.visitors;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.traces.PTraceCoreDefinition;
import org.overture.ast.definitions.traces.PTraceDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SBinaryExp;
import org.overture.ast.expressions.SBooleanBinaryExp;
import org.overture.ast.expressions.SMapExp;
import org.overture.ast.expressions.SNumericBinaryExp;
import org.overture.ast.expressions.SSeqExp;
import org.overture.ast.expressions.SSetExp;
import org.overture.ast.expressions.SUnaryExp;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.modules.PExport;
import org.overture.ast.modules.PImport;
import org.overture.ast.modules.SValueImport;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.PBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.ALetStm;
import org.overture.ast.statements.PObjectDesignator;
import org.overture.ast.statements.PStateDesignator;
import org.overture.ast.statements.PStm;
import org.overture.ast.statements.SSimpleBlockStm;
import org.overture.ast.types.PType;
import org.overture.ast.types.SBasicType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SNumericBasicType;
import org.overture.ast.types.SSeqType;

class GetLocationVisitor extends AnswerAdaptor<ILexLocation>
{

	@Override
	public ILexLocation defaultPExp(PExp node) throws AnalysisException
	{
		return node.getLocation();
	}

	@Override
	public ILexLocation defaultSUnaryExp(SUnaryExp node)
			throws AnalysisException
	{
		return node.getLocation();
	}

	@Override
	public ILexLocation defaultSBinaryExp(SBinaryExp node)
			throws AnalysisException
	{
		return node.getLocation();
	}

	@Override
	public ILexLocation defaultSMapExp(SMapExp node) throws AnalysisException
	{
		return node.getLocation();
	}

	@Override
	public ILexLocation defaultSSeqExp(SSeqExp node) throws AnalysisException
	{
		return node.getLocation();
	}

	@Override
	public ILexLocation defaultSSetExp(SSetExp node) throws AnalysisException
	{
		return node.getLocation();
	}

	@Override
	public ILexLocation defaultSBooleanBinaryExp(SBooleanBinaryExp node)
			throws AnalysisException
	{
		return node.getLocation();
	}

	@Override
	public ILexLocation defaultSNumericBinaryExp(SNumericBinaryExp node)
			throws AnalysisException
	{
		return node.getLocation();
	}

	@Override
	public ILexLocation defaultPType(PType node) throws AnalysisException
	{
		return node.getLocation();
	}

	@Override
	public ILexLocation defaultSBasicType(SBasicType node)
			throws AnalysisException
	{
		return node.getLocation();
	}

	@Override
	public ILexLocation defaultSInvariantType(SInvariantType node)
			throws AnalysisException
	{
		return node.getLocation();
	}

	@Override
	public ILexLocation defaultSMapType(SMapType node) throws AnalysisException
	{
		return node.getLocation();
	}

	@Override
	public ILexLocation defaultSSeqType(SSeqType node) throws AnalysisException
	{
		return node.getLocation();
	}

	@Override
	public ILexLocation defaultSNumericBasicType(SNumericBasicType node)
			throws AnalysisException
	{
		return node.getLocation();
	}

	@Override
	public ILexLocation defaultPPattern(PPattern node) throws AnalysisException
	{
		return node.getLocation();
	}

	@Override
	public ILexLocation defaultPBind(PBind node) throws AnalysisException
	{
		return node.getLocation();
	}

	@Override
	public ILexLocation defaultPMultipleBind(PMultipleBind node)
			throws AnalysisException
	{
		return node.getLocation();
	}

	@Override
	public ILexLocation defaultPDefinition(PDefinition node)
			throws AnalysisException
	{
		return node.getLocation();
	}

	@Override
	public ILexLocation defaultSClassDefinition(SClassDefinition node)
			throws AnalysisException
	{
		return node.getLocation();
	}

	@Override
	public ILexLocation defaultPTraceDefinition(PTraceDefinition node)
			throws AnalysisException
	{
		return node.getLocation();
	}

	@Override
	public ILexLocation defaultPTraceCoreDefinition(PTraceCoreDefinition node)
			throws AnalysisException
	{
		return node.getLocation();
	}

	@Override
	public ILexLocation defaultPImport(PImport node) throws AnalysisException
	{
		return node.getLocation();
	}

	@Override
	public ILexLocation defaultSValueImport(SValueImport node)
			throws AnalysisException
	{
		return node.getLocation();
	}

	@Override
	public ILexLocation defaultPExport(PExport node) throws AnalysisException
	{
		return node.getLocation();
	}

	@Override
	public ILexLocation defaultPStm(PStm node) throws AnalysisException
	{
		return node.getLocation();
	}

	@Override
	public ILexLocation caseALetStm(ALetStm node) throws AnalysisException
	{
		return node.getLocation();
	}

	@Override
	public ILexLocation defaultSSimpleBlockStm(SSimpleBlockStm node)
			throws AnalysisException
	{
		return node.getLocation();
	}

	@Override
	public ILexLocation defaultPStateDesignator(PStateDesignator node)
			throws AnalysisException
	{
		return node.getLocation();
	}

	@Override
	public ILexLocation defaultPObjectDesignator(PObjectDesignator node)
			throws AnalysisException
	{
		return node.getLocation();
	}

	@Override
	public ILexLocation createNewReturnValue(INode node)
	{
		assert false : "Should not happen";
		return null;
	}

	@Override
	public ILexLocation createNewReturnValue(Object node)
	{
		assert false : "Should not happen";
		return null;
	}

}