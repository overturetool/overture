package org.overture.codegen.visitor;

public class CodeGenInfo
{
	private CodeGenerator rootVisitor;

	private ClassVisitorCG classVisitor;
	
	private DeclVisitor declVisitor;
	
	private ExpVisitorCG expVisitor;
	
	private TypeVisitorCG typeVisitor;
	
	private StmVisitorCG stmVisitor;
	
	private StateDesignatorVisitor stateDesignatorVisitor;
	
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
	
}
