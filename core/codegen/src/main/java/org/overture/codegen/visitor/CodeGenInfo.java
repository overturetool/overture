package org.overture.codegen.visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;

public class CodeGenInfo
{
	
	//Visitors:
	private CodeGenerator rootVisitor;
	private ClassVisitorCG classVisitor;
	private DeclVisitor declVisitor;
	private ExpVisitorCG expVisitor;
	private TypeVisitorCG typeVisitor;
	private StmVisitorCG stmVisitor;
	private StateDesignatorVisitor stateDesignatorVisitor;

	//Quotes:
	private HashSet<String> quoteVaues;
	
	public CodeGenInfo(CodeGenerator rootVisitor)
	{
		super();
		this.rootVisitor = rootVisitor;
		this.classVisitor = new ClassVisitorCG();
		this.declVisitor = new DeclVisitor();
		this.expVisitor = new ExpVisitorCG();
		this.typeVisitor = new TypeVisitorCG();
		this.stmVisitor = new StmVisitorCG();
		this.stateDesignatorVisitor = new StateDesignatorVisitor();
		
		this.quoteVaues = new HashSet<String>();
	}

	public CodeGenerator getRootVisitor()
	{
		return rootVisitor;
	}
	
	public ClassVisitorCG getClassVisitor()
	{
		return classVisitor;
	}
	
	public DeclVisitor getDeclVisitor()
	{
		return declVisitor;
	}
	
	public ExpVisitorCG getExpVisitor()
	{
		return expVisitor;
	}
	
	public TypeVisitorCG getTypeVisitor()
	{
		return typeVisitor;
	}
	
	public StmVisitorCG getStatementVisitor()
	{
		return stmVisitor;
	}
	
	public StateDesignatorVisitor getStateDesignatorVisitor()
	{
		return stateDesignatorVisitor;
	}
	
	public void registerQuoteValue(String value) throws AnalysisException
	{
		if(value == null || value.isEmpty())
			throw new AnalysisException("Tried to register invalid qoute value");
		
		quoteVaues.add(value);
	}
	
	public List<String> getQuoteValues()
	{
		List<String> quoteValuesSorted = new ArrayList<String>(quoteVaues);
		Collections.sort(quoteValuesSorted);
		
		return quoteValuesSorted;
	}
	
}
