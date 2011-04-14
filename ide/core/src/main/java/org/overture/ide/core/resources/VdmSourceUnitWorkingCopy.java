package org.overture.ide.core.resources;


public class VdmSourceUnitWorkingCopy extends VdmSourceUnit implements
		IVdmSourceUnit
{

	IVdmSourceUnit sourceUnit = null;

	public VdmSourceUnitWorkingCopy(VdmSourceUnit vdmSourceUnit)
	{
		super(vdmSourceUnit.getProject(),vdmSourceUnit.getFile());
		this.sourceUnit = vdmSourceUnit;
		this.parseList.addAll(this.sourceUnit.getParseList());
//		this.allLocation.addAll(this.sourceUnit.);
		this.locationToAstNodeMap.putAll(this.sourceUnit.getLocationToAstNodeMap());
	}
	
	
	public void commit()
	{
		this.sourceUnit.reconcile(this.parseList, this.allLocation, this.locationToAstNodeMap, this.parseErrors);
	}
	
	@Override
	protected void fireChangedEvent()
	{
		//do not fire from working copy
	}
	
	@Override
	public String toString()
	{
		return super.toString()+ " - Working copy";
	}

}
