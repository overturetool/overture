package org.overture.codegen.assistant;

public class AssistantManager
{
	private ExpAssistantCG expAssistant;
	private DeclAssistantCG declAssistant;
	private DesignatorAssistantCG designatorAssistant;
	private StmAssistantCG stmAssistant;
	private TypeAssistantCG typeAssistant;
	
	public AssistantManager()
	{
		this.expAssistant = new ExpAssistantCG();
		this.declAssistant = new DeclAssistantCG();
		this.designatorAssistant = new DesignatorAssistantCG();
		this.stmAssistant = new StmAssistantCG();
		this.typeAssistant = new TypeAssistantCG();
	}
	
	
	public ExpAssistantCG getExpAssistant()
	{
		return expAssistant;
	}

	public DeclAssistantCG getDeclAssistant()
	{
		return declAssistant;
	}

	public DesignatorAssistantCG getDesignatorAssistant()
	{
		return designatorAssistant;
	}
	
	public StmAssistantCG getStmAssistant()
	{
		return stmAssistant;
	}
	
	public TypeAssistantCG getTypeAssistant()
	{
		return typeAssistant;
	}
}
