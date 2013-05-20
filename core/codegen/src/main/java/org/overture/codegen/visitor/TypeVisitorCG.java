package org.overture.codegen.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.cgast.types.PTypeCG;

public class TypeVisitorCG extends QuestionAnswerAdaptor<CodeGenInfo, PTypeCG>
{
	private static final long serialVersionUID = 8845855407070139031L;

	public TypeVisitorCG()
	{
	}

	@Override
	public PTypeCG caseAIntNumericBasicType(AIntNumericBasicType node,
			CodeGenInfo question) throws AnalysisException
	{
		return new AIntNumericBasicTypeCG();
	}

	@Override
	public PTypeCG caseANatOneNumericBasicType(ANatOneNumericBasicType node,
			CodeGenInfo question) throws AnalysisException
	{
		return new AIntNumericBasicTypeCG();
	}

	@Override
	public PTypeCG caseANatNumericBasicType(ANatNumericBasicType node,
			CodeGenInfo question) throws AnalysisException
	{
		return new AIntNumericBasicTypeCG();
	}

	@Override
	public PTypeCG caseARealNumericBasicType(ARealNumericBasicType node,
			CodeGenInfo question) throws AnalysisException
	{
		return new ARealNumericBasicTypeCG();
	}

	@Override
	public PTypeCG caseACharBasicType(ACharBasicType node,
			CodeGenInfo question) throws AnalysisException
	{
		return new ACharBasicTypeCG();
	}

	// @Override
	// public String caseABooleanBasicType(ABooleanBasicType node,
	// ContextManager question) throws AnalysisException
	// {
	// return Vdm2JavaBasicTypeMappings.BOOL.toString();
	// }
	//

	//
	// @Override
	// public String caseATokenBasicType(ATokenBasicType node,
	// ContextManager question) throws AnalysisException
	// {
	// throw new AnalysisException(IMessages.NOT_SUPPORTED_MSG + node.toString());
	// }
	//
	//
	// @Override
	// public String caseARationalNumericBasicType(ARationalNumericBasicType node,
	// ContextManager question) throws AnalysisException
	// {
	// throw new AnalysisException(IMessages.NOT_SUPPORTED_MSG + node.toString());
	// }
	//
	// @Override
	// public String caseAVoidType(AVoidType node, ContextManager question)
	// throws AnalysisException
	// {
	// return "void";
	// }

}
