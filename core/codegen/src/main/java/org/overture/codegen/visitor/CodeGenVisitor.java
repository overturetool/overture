package org.overture.codegen.visitor;

import java.util.ArrayList;

import org.overture.ast.analysis.AnalysisAdaptor;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.types.PType;
import org.overture.codegen.cgast.AClassTypeDeclCG;
import org.overture.codegen.logging.ILogger;

public class CodeGenVisitor extends AnalysisAdaptor
{
	private static final long serialVersionUID = -7105226072509250353L;
	
	private ILogger log;
	
	private DefVisitorCG defVisitor;
	private TypeVisitorCG typeVisitor;
	private ExpVisitorCG expVisitor;
		
	private ArrayList<AClassTypeDeclCG> classes;
	
	private CodeGenInfo codeGenInfo;
	
	public CodeGenVisitor(ILogger log)
	{
		this.log = log;
		
		this.defVisitor = new DefVisitorCG();
		this.typeVisitor = new TypeVisitorCG();
		this.expVisitor = new ExpVisitorCG();
		
		this.classes = new ArrayList<>();
		
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
	
	public ArrayList<AClassTypeDeclCG> getClasses()
	{
		return classes;
	}
	
	public void registerClass(AClassTypeDeclCG classCg)
	{
		classes.add(classCg);
	}
		
	@Override
	public void defaultPDefinition(PDefinition node) throws AnalysisException
	{
		node.apply(defVisitor, codeGenInfo);
	}
	
	@Override
	public void defaultPType(PType node)
			throws AnalysisException
	{
		node.apply(typeVisitor, codeGenInfo);
	}	
}
