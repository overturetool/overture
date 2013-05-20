package org.overture.codegen.visitor;

import java.util.ArrayList;

import org.overture.ast.analysis.AnalysisAdaptor;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.codegen.cgast.AClassCG;
import org.overture.codegen.logging.ILogger;

public class CodeGenVisitor extends AnalysisAdaptor
{
	private static final long serialVersionUID = -7105226072509250353L;
	
	private ILogger log;
	
	private DefVisitorCG defVisitor;
	private TypeVisitorCG typeVisitor;
	private ExpVisitorCG expVisitor;
	
	
	private ArrayList<AClassCG> classes;
	//private CodeGenTree tree;
	
	private CodeGenInfo codeGenInfo;
	
	public CodeGenVisitor(ILogger log)
	{
		this.log = log;
		
		defVisitor = new DefVisitorCG(this);
		typeVisitor = new TypeVisitorCG(this);
		expVisitor = new ExpVisitorCG();
		
		classes = new ArrayList<>();
		
		this.codeGenInfo = new CodeGenInfo(this);
	}
	
	public TypeVisitorCG getTypeVisitor()
	{
		return typeVisitor;
	}
	
	public DefVisitorCG getDefVisitor()
	{
		return defVisitor;
	}
	
	public ExpVisitorCG getExpVisitor()
	{
		return expVisitor;
	}
		
	public ILogger getLog()
	{
		return log;
	}
	
	public ArrayList<AClassCG> getClasses()
	{
		return classes;
	}
	
	public void registerClass(AClassCG classCg)
	{
		classes.add(classCg);
	}
		
//	@Override
//	public String defaultINode(INode node, CodeGenContextMap question)
//			throws AnalysisException
//	{
//		throw new AnalysisException(IMessages.NOT_SUPPORTED_MSG + node.toString());
//	}
//	
//	
	@Override
	public void defaultPDefinition(PDefinition node) throws AnalysisException
	{
		node.apply(defVisitor, codeGenInfo);
	}
//	
//	@Override
//	public String defaultPType(PType node, CodeGenContextMap question)
//			throws AnalysisException
//	{
//		return node.apply(typeVisitor, question);
//	}
//	
//	@Override
//	public void defaultPExp(PExp node)
//			throws AnalysisException
//	{
//		node.apply(codeGenInfo.get)
//	}
//	
//	@Override
//	public String caseILexNameToken(ILexNameToken node,
//			CodeGenContextMap question) throws AnalysisException
//	{
//		return node.getName();
//	}
//	
//	@Override
//	public String caseAAccessSpecifierAccessSpecifier(
//			AAccessSpecifierAccessSpecifier node, CodeGenContextMap question)
//			throws AnalysisException
//	{
//		return node.getAccess().toString();
//	}			
}
