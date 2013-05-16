package org.overture.codegen.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.ARationalNumericBasicType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.ATokenBasicType;

public class TypeVisitorCG extends QuestionAnswerAdaptor<CodeGenContextMap, String>
{
	private static final long serialVersionUID = 8845855407070139031L;

	private CodeGenVisitor rootVisitor;
	
	public TypeVisitorCG(CodeGenVisitor rootVisitor)
	{
		this.rootVisitor = rootVisitor;
	}

	@Override
	public String caseABooleanBasicType(ABooleanBasicType node,
			CodeGenContextMap question) throws AnalysisException
	{
		return Vdm2JavaBasicTypeMappings.BOOL.toString();
	}
	
	@Override
	public String caseACharBasicType(ACharBasicType node,
			CodeGenContextMap question) throws AnalysisException
	{
		return Vdm2JavaBasicTypeMappings.CHAR.toString();
	}
	
	@Override
	public String caseATokenBasicType(ATokenBasicType node,
			CodeGenContextMap question) throws AnalysisException
	{
		throw new AnalysisException(IMessages.NOT_SUPPORTED_MSG + node.toString());
	}
	
	@Override
	public String caseAIntNumericBasicType(AIntNumericBasicType node,
			CodeGenContextMap question) throws AnalysisException
	{
		return Vdm2JavaBasicTypeMappings.INT.toString();
	}
	
	@Override
	public String caseANatOneNumericBasicType(ANatOneNumericBasicType node,
			CodeGenContextMap question) throws AnalysisException
	{
		return Vdm2JavaBasicTypeMappings.NAT_ONE.toString();
	}
	
	@Override
	public String caseANatNumericBasicType(ANatNumericBasicType node,
			CodeGenContextMap question) throws AnalysisException
	{
		return Vdm2JavaBasicTypeMappings.NAT.toString();
	}
	
	@Override
	public String caseARationalNumericBasicType(ARationalNumericBasicType node,
			CodeGenContextMap question) throws AnalysisException
	{
		throw new AnalysisException(IMessages.NOT_SUPPORTED_MSG + node.toString());
	}
	
	@Override
	public String caseARealNumericBasicType(ARealNumericBasicType node,
			CodeGenContextMap question) throws AnalysisException
	{
		return Vdm2JavaBasicTypeMappings.REAL.toString();
	}
	
}
