package org.overture.codegen.visitor;

public class CodeGenInfo
{
	private CodeGenVisitor rootVisitor;

	private TypeDeclVisitor typeDeclVisitor;
	
	private DeclVisitor declVisitor;
	
	private ExpVisitorCG expVisitor;
	
	private TypeVisitorCG typeVisitor;
	
	private StmVisitorCG stmVisitor;
	
	private StateDesignatorVisitor stateDesignatorVisitor;
	
	public CodeGenInfo(CodeGenVisitor rootVisitor)
	{
		super();
		this.rootVisitor = rootVisitor;
		this.typeDeclVisitor = new TypeDeclVisitor();
		this.declVisitor = new DeclVisitor();
		this.expVisitor = new ExpVisitorCG();
		this.typeVisitor = new TypeVisitorCG();
		this.stmVisitor = new StmVisitorCG();
		this.stateDesignatorVisitor = new StateDesignatorVisitor();
	}

	public CodeGenVisitor getRootVisitor()
	{
		return rootVisitor;
	}
	
	public TypeDeclVisitor getTypeDeclVisitor()
	{
		return typeDeclVisitor;
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
	
}
