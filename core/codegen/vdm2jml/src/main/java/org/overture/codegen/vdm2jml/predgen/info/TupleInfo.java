package org.overture.codegen.vdm2jml.predgen.info;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.runtime.V2J;
import org.overture.codegen.vdm2jml.JmlGenerator;
import org.overture.codegen.vdm2jml.util.NameGen;

public class TupleInfo extends AbstractTypeInfo
{
	public static final String IS_TUP_METHOD_NAME = "isTup";
	public static final String GET_FIELD_METHOD_NAME = "field";
	
	private List<AbstractTypeInfo> types;
	
	public TupleInfo(boolean optional, List<AbstractTypeInfo> types)
	{
		super(optional);
		this.types = types;
	}
	
	@Override
	public boolean allowsNull()
	{
		if(optional)
		{
			return true;
		}
		
		for(AbstractTypeInfo t : types)
		{
			if(t.allowsNull())
			{
				return true;
			}
		}
		
		return false;
	}

	@Override
	public List<LeafTypeInfo> getLeafTypesRecursively()
	{
		List<LeafTypeInfo> leaves = new LinkedList<>();
		
		for(AbstractTypeInfo t : types)
		{
			leaves.addAll(t.getLeafTypesRecursively());
		}
		
		return leaves;
	}

	@Override
	public String consCheckExp(String enclosingClass, String javaRootPackage, String arg, NameGen nameGen)
	{
		StringBuilder sb = new StringBuilder();
		int tupleSize = types.size();
		
		//e.g. V2J.isTup(t)
		sb.append(consSubjectCheckExtraArg(V2J.class.getSimpleName(), IS_TUP_METHOD_NAME, arg, tupleSize + ""));
		
		for(int i = 0; i < types.size(); i++)
		{
			//e.g. V2J.field(t,2)
			String fieldArg = consSubjectCheckExtraArg(V2J.class.getSimpleName(), GET_FIELD_METHOD_NAME, arg, i + "");
			
			//e.g. Utils.is_nat1(V2J.field(t,2))
			String fieldCheck = types.get(i).consCheckExp(enclosingClass, javaRootPackage, fieldArg, nameGen);
			
			sb.append(JmlGenerator.JML_AND);
			sb.append(fieldCheck);
		}
		
		String tupleCheck = "(" + sb.toString() + ")";
		
		if(allowsNull())
		{
			return "(" + consIsNullCheck(arg) + JmlGenerator.JML_OR + tupleCheck + ")";
		}
		else
		{
			//e.g. (V2J.isTup(t) && Utils.is_nat1(V2J.field(t,1)) && Utils.is_bool(V2J.field(t,2)))
			return tupleCheck;
		}
		
	}

}
