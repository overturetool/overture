package org.overture.codegen.visitor;


public class TypeVisitorCG// extends QuestionAnswerAdaptor<ContextManager, String>
{
	private static final long serialVersionUID = 8845855407070139031L;

	private CodeGenVisitor rootVisitor;
	
	public TypeVisitorCG(CodeGenVisitor rootVisitor)
	{
		this.rootVisitor = rootVisitor;
	}

//	@Override
//	public String caseABooleanBasicType(ABooleanBasicType node,
//			ContextManager question) throws AnalysisException
//	{
//		return Vdm2JavaBasicTypeMappings.BOOL.toString();
//	}
//	
//	@Override
//	public String caseACharBasicType(ACharBasicType node,
//			ContextManager question) throws AnalysisException
//	{
//		return Vdm2JavaBasicTypeMappings.CHAR.toString();
//	}
//	
//	@Override
//	public String caseATokenBasicType(ATokenBasicType node,
//			ContextManager question) throws AnalysisException
//	{
//		throw new AnalysisException(IMessages.NOT_SUPPORTED_MSG + node.toString());
//	}
//	
//	@Override
//	public String caseAIntNumericBasicType(AIntNumericBasicType node,
//			ContextManager question) throws AnalysisException
//	{
//		return Vdm2JavaBasicTypeMappings.INT.toString();
//	}
//	
//	@Override
//	public String caseANatOneNumericBasicType(ANatOneNumericBasicType node,
//			ContextManager question) throws AnalysisException
//	{
//		return Vdm2JavaBasicTypeMappings.NAT_ONE.toString();
//	}
//	
//	@Override
//	public String caseANatNumericBasicType(ANatNumericBasicType node,
//			ContextManager question) throws AnalysisException
//	{
//		return Vdm2JavaBasicTypeMappings.NAT.toString();
//	}
//	
//	@Override
//	public String caseARationalNumericBasicType(ARationalNumericBasicType node,
//			ContextManager question) throws AnalysisException
//	{
//		throw new AnalysisException(IMessages.NOT_SUPPORTED_MSG + node.toString());
//	}
//	
//	@Override
//	public String caseARealNumericBasicType(ARealNumericBasicType node,
//			ContextManager question) throws AnalysisException
//	{
//		return Vdm2JavaBasicTypeMappings.REAL.toString();
//	}
//	
//	@Override
//	public String caseAVoidType(AVoidType node, ContextManager question)
//			throws AnalysisException
//	{
//		return "void";
//	}
	
	
}
