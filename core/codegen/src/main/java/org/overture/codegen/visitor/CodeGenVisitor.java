package org.overture.codegen.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.node.INode;
import org.overture.ast.node.IToken;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.PType;
import org.overture.codegen.logging.ILogger;

public class CodeGenVisitor extends
		QuestionAnswerAdaptor<CodeGenContextMap, String>
{
	private static final long serialVersionUID = -7105226072509250353L;
	
	private DefVisitorCG defVisitor;
	private TypeVisitorCG typeVisitor;
	private ExpVisitorCG expVisitor;
	private StmVisitorCG stmVisitor;
	private PatternVisitorCG patternVisitor;
	
	private ILogger log;
	
	public CodeGenVisitor(ILogger log)
	{
		this.log = log;
		
		defVisitor = new DefVisitorCG(this);
		typeVisitor = new TypeVisitorCG(this);
		expVisitor = new ExpVisitorCG(this);
		stmVisitor = new StmVisitorCG(this);
		patternVisitor = new PatternVisitorCG(this);
	}
	
	@Override
	public String defaultINode(INode node, CodeGenContextMap question)
			throws AnalysisException
	{
		throw new AnalysisException(IMessages.NOT_SUPPORTED_MSG + node.toString());
	}
	
	@Override
	public String defaultPDefinition(PDefinition node,
			CodeGenContextMap question) throws AnalysisException
	{
		return node.apply(defVisitor, question);
	}
	
	@Override
	public String defaultPType(PType node, CodeGenContextMap question)
			throws AnalysisException
	{
		return node.apply(typeVisitor, question);
	}
	
	@Override
	public String defaultPExp(PExp node, CodeGenContextMap question)
			throws AnalysisException
	{
		return node.apply(expVisitor, question);
	}
	
	@Override
	public String caseILexNameToken(ILexNameToken node,
			CodeGenContextMap question) throws AnalysisException
	{
		return node.getName();
	}
	
	@Override
	public String caseAAccessSpecifierAccessSpecifier(
			AAccessSpecifierAccessSpecifier node, CodeGenContextMap question)
			throws AnalysisException
	{
		return node.getAccess().toString();
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
}
