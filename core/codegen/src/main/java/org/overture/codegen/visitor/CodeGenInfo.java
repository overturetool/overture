package org.overture.codegen.visitor;

public class CodeGenInfo
{
	private CodeGenVisitor rootVisitor;

	private FieldVisitor fieldVisitor;
	
	private ExpVisitorCG expVisitor;
	
	private TypeVisitorCG typeVisitor;
	
	public CodeGenInfo(CodeGenVisitor rootVisitor)
	{
		super();
		this.rootVisitor = rootVisitor;
		this.fieldVisitor = new FieldVisitor();
		this.expVisitor = new ExpVisitorCG();
		this.typeVisitor = new TypeVisitorCG();
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
	
	public TypeVisitorCG getTypeVisitor()
	{
		return typeVisitor;
	}
	
}
