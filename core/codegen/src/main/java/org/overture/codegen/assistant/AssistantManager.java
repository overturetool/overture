package org.overture.codegen.assistant;

public class AssistantManager
{
	private ExpAssistantCG expAssistant;
	private DeclAssistantCG declAssistant;
	private StmAssistantCG stmAssistant;
	private TypeAssistantCG typeAssistant;
	private LocationAssistantCG locationAssistant;
	private BindAssistantCG bindAssistant;
	
	public AssistantManager()
	{
		this.expAssistant = new ExpAssistantCG(this);
		this.declAssistant = new DeclAssistantCG(this);
		this.stmAssistant = new StmAssistantCG(this);
		this.typeAssistant = new TypeAssistantCG(this);
		this.locationAssistant = new LocationAssistantCG(this);
		this.bindAssistant = new BindAssistantCG(this);
	}

	public ExpAssistantCG getExpAssistant()
	{
		return expAssistant;
	}

	public DeclAssistantCG getDeclAssistant()
	{
		return declAssistant;
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
	
	public BindAssistantCG getBindAssistant()
	{
		return bindAssistant;
	}
}
