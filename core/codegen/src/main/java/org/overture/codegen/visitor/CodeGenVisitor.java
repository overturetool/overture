package org.overture.codegen.visitor;

import org.overture.ast.analysis.AnalysisAdaptor;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.codegen.logging.ILogger;
import org.overture.codegen.newstuff.CodeGenTree;

public class CodeGenVisitor extends AnalysisAdaptor
{
	private static final long serialVersionUID = -7105226072509250353L;
	
	private DefVisitorCG defVisitor;
	private TypeVisitorCG typeVisitor;
	private ExpVisitorCG expVisitor;
	private StmVisitorCG stmVisitor;
	private PatternVisitorCG patternVisitor;
	
	private CodeGenTree tree;
	
	private ILogger log;
	
	public CodeGenVisitor(ILogger log)
	{
		this.log = log;
		
		defVisitor = new DefVisitorCG(this);
		typeVisitor = new TypeVisitorCG(this);
		expVisitor = new ExpVisitorCG(this);
		stmVisitor = new StmVisitorCG(this);
		patternVisitor = new PatternVisitorCG(this);
		
		this.tree = new CodeGenTree();
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
	
	public StmVisitorCG getStmVisitor()
	{
		return stmVisitor;
	}
	
	public PatternVisitorCG getPatternVisitor()
	{
		return patternVisitor;
	}
	
	
	public ILogger getLog()
	{
		return log;
	}
	
	public CodeGenTree getTree()
	{
		return tree;
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
		node.apply(defVisitor);
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
//	public String defaultPExp(PExp node, CodeGenContextMap question)
//			throws AnalysisException
//	{
//		return node.apply(expVisitor, question);
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
