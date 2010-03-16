package org.overture.ide.core;

public class VdmElementDelta implements IVdmElementDelta
{
	private int kind;
	private IVdmElement element;

	public VdmElementDelta(IVdmElement element, int kind) {
		this.element = element;
		this.kind = kind;

	}

	public IVdmElement getElement()
	{
		return this.element;
	}

	public int getKind()
	{
		return this.kind;
	}

}
