package org.overture.codegen.visitor;

public class CodeGenInfo
{
	private CodeGenVisitor rootVisitor;

	private DeclVisitor declVisitor;
	
	private ExpVisitorCG expVisitor;
	
	private TypeVisitorCG typeVisitor;
	
	private StmVisitorCG stmVisitor;
	
	public CodeGenInfo(CodeGenVisitor rootVisitor)
	{
		super();
		this.rootVisitor = rootVisitor;
		this.declVisitor = new DeclVisitor();
		this.expVisitor = new ExpVisitorCG();
		this.typeVisitor = new TypeVisitorCG();
		this.stmVisitor = new StmVisitorCG();
	}

	public CodeGenVisitor getRootVisitor()
	{
		return rootVisitor;
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
	
}
