package org.overture.codegen.assistant;

public class AssistantManager
{
	private ExpAssistantCG expAssistant;
	private DeclAssistantCG declAssistant;
	private DesignatorAssistantCG designatorAssistant;
	private StmAssistantCG stmAssistant;
	private TypeAssistantCG typeAssistant;
	private LocationAssistantCG locationAssistant;
	
	public AssistantManager()
	{
		this.expAssistant = new ExpAssistantCG(this);
		this.declAssistant = new DeclAssistantCG(this);
		this.designatorAssistant = new DesignatorAssistantCG(this);
		this.stmAssistant = new StmAssistantCG(this);
		this.typeAssistant = new TypeAssistantCG(this);
		this.locationAssistant = new LocationAssistantCG(this);
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
	
	public LocationAssistantCG getLocationAssistant()
	{
		return locationAssistant;
	}
}
