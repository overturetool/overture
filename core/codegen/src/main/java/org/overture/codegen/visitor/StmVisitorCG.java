package org.overture.codegen.visitor;


public class StmVisitorCG// extends QuestionAnswerAdaptor<CodeGenContextMap, String>
{
//	private static final long serialVersionUID = 5210069834877599547L;
//
//	private CodeGenVisitor rootVisitor;
//	private StmAssistantCG stmAssistant;
//
//	public StmVisitorCG(CodeGenVisitor rootVisitor)
//	{
//		super();
//		this.rootVisitor = rootVisitor;
//		this.stmAssistant = new StmAssistantCG(rootVisitor);
//	}
//
//	@Override
//	public String caseABlockSimpleBlockStm(ABlockSimpleBlockStm node,
//			CodeGenContextMap question) throws AnalysisException
//	{
//		MethodDeinitionCG methodDef = stmAssistant.getMethodDefinition(node, question);
//
//		LinkedList<PDefinition> assignmentDefs = node.getAssignmentDefs();
//
//		for (PDefinition def : assignmentDefs)
//		{
//			AAssignmentDefinition assignment = (AAssignmentDefinition) def;
//			String type = assignment.getType().apply(rootVisitor.getTypeVisitor(), question);
//			String name = assignment.getName().apply(rootVisitor, question);
//			String exp = assignment.getExpression().apply(rootVisitor.getExpVisitor(), question);
//
//			methodDef.addStatement(new DeclarationStmCG(type, name, exp));
//		}
//
//		LinkedList<PStm> statements = node.getStatements();
//
//		for (PStm stm : statements)
//		{
//			stm.apply(this, question);
//		}
//
//		return null;
//	}
//
//	@Override
//	public String caseAIfStm(AIfStm node, CodeGenContextMap question)
//			throws AnalysisException
//	{
//
//		CodeGenContext context = new CodeGenContext();
//
//		context.put(TemplateParameters.IF_STM_TEST, "hejhej1");
//		context.put(TemplateParameters.IF_STM_THEN_STM, "hejhej2");
//		context.put(TemplateParameters.IF_STM_ELSE_STM, "hejhej3");
//
//		Template t = Vdm2CppUtil.getTemplate("if_statement.vm");
//
//		PrintWriter out = new PrintWriter(System.out);
//
//		System.out.println("***");
//		t.merge(context.getVelocityContext(), out);
//		out.flush();
//		out.println();
//		System.out.println("***");
//
//		return super.caseAIfStm(node, question);
//	}
}
