package org.overture.codegen.visitor;


public class VisitorManager
{
	private ClassVisitorCG classVisitor;
	private DeclVisitorCG declVisitor;
	private ExpVisitorCG expVisitor;
	private TypeVisitorCG typeVisitor;
	private StmVisitorCG stmVisitor;
	private StateDesignatorVisitorCG stateDesignatorVisitor;
	private ObjectDesignatorVisitorCG objectDesignatorVisitor;
	private MultipleBindVisitorCG multipleBindVisitor;
	private BindVisitorCG bindVisitor;
	private PatternVisitorCG patternVisitor;
	
	public VisitorManager()
	{
		this.classVisitor = new ClassVisitorCG();
		this.declVisitor = new DeclVisitorCG();
		this.expVisitor = new ExpVisitorCG();
		this.typeVisitor = new TypeVisitorCG();
		this.stmVisitor = new StmVisitorCG();
		this.stateDesignatorVisitor = new StateDesignatorVisitorCG();
		this.objectDesignatorVisitor = new ObjectDesignatorVisitorCG();
		this.multipleBindVisitor = new MultipleBindVisitorCG();
		this.bindVisitor = new BindVisitorCG();
		this.patternVisitor = new PatternVisitorCG();
	}
	
	public ClassVisitorCG getClassVisitor()
	{
		return classVisitor;
	}
	
	public DeclVisitorCG getDeclVisitor()
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
	
	public StmVisitorCG getStmVisitor()
	{
		return stmVisitor;
	}
	
	public StateDesignatorVisitorCG getStateDesignatorVisitor()
	{
		return stateDesignatorVisitor;
	}
	
	public ObjectDesignatorVisitorCG getObjectDesignatorVisitor()
	{
		return objectDesignatorVisitor;
	}
	
	public MultipleBindVisitorCG getMultipleBindVisitor()
	{
		return multipleBindVisitor;
	}
	
	public BindVisitorCG getBindVisitor()
	{
		return bindVisitor;
	}
	
	public PatternVisitorCG getPatternVisitor()
	{
		return patternVisitor;
	}
}
