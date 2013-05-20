package org.overture.codegen.visitor;

public class CodeGenInfo
{
	private CodeGenVisitor rootVisitor;

	private FieldVisitor fieldVisitor;
	
	private ExpVisitorCG expVisitor;
	
	public CodeGenInfo(CodeGenVisitor rootVisitor)
	{
		super();
		this.rootVisitor = rootVisitor;
		this.fieldVisitor = new FieldVisitor();
		this.expVisitor = new ExpVisitorCG();
	}

	public CodeGenVisitor getRootVisitor()
	{
		return rootVisitor;
	}
	
	public FieldVisitor getFieldVisitor()
	{
		return fieldVisitor;
	}
	
	public ExpVisitorCG getExpVisitor()
	{
		return expVisitor;
	}
	
}
