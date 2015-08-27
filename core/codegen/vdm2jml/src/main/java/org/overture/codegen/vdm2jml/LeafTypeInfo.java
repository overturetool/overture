package org.overture.codegen.vdm2jml;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.types.PType;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.logging.Logger;

public class LeafTypeInfo extends AbstractTypeInfo
{
	private PType type;
	
	public LeafTypeInfo(PType type, boolean optional)
	{
		super(optional);
		this.type = type;
	}
	
	public PType getType()
	{
		return type;
	}

	@Override
	public boolean allowsNull()
	{
		return optional;
	}
	
	@Override
	public String toString()
	{
		if(optional)
		{
			return "[" + type.toString() + "]";
		}
		else
		{
			return type.toString();
		}
	}
	
	public STypeCG toIrType(IRInfo info)
	{
		try
		{
			STypeCG irType = type.apply(info.getTypeVisitor(), info);
			
			if(irType != null)
			{
				irType.setOptional(optional);
			}
			
			return irType;
		} catch (AnalysisException e)
		{
			Logger.getLog().printErrorln("Problems encountered while attempting "
					+ "to construct the IR type from a VDM type: "
					+ e.getMessage()
					+ " in '"
					+ this.getClass().getSimpleName() + "'");
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public List<LeafTypeInfo> getLeafTypesRecursively()
	{
		List<LeafTypeInfo> types = new LinkedList<LeafTypeInfo>();
		types.add(this);
		
		return types;
	}
}
