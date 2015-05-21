package org.overture.codegen.vdm2jml;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;

// TODO: This class must take optional types into account - possibly by updating the create method.
// TODO: Guard against type recursion in the dependency tree
public class NamedTypeInvDepCalculator extends DepthFirstAnalysisAdaptor
{
	private List<NamedTypeInfo> typeInfoList;

	public NamedTypeInvDepCalculator()
	{
		super();
		this.typeInfoList = new LinkedList<NamedTypeInfo>();
	}

	public List<NamedTypeInfo> getTypeDataList()
	{
		return typeInfoList;
	}

	public static NamedTypeInfo findTypeInfo(List<NamedTypeInfo> typeInfoList,
			String defModule, String typeName)
	{
		for (NamedTypeInfo t : typeInfoList)
		{
			if (NamedTypeInfo.isSameTypeDef(t, defModule, typeName))
			{
				return t;
			}
		}

		return null;
	}

	public static List<NamedTypeInfo> onlyDisjointTypes(List<NamedTypeInfo> typeInfo)
	{
		List<NamedTypeInfo> disjointTypes = new LinkedList<NamedTypeInfo>();

		for(NamedTypeInfo t : typeInfo)
		{
			if(!contains(disjointTypes, t))
			{
				removeSmallerTypes(disjointTypes, t);
				disjointTypes.add(t);
			}
		}
		
		return disjointTypes;
	}
	
	public static void removeSmallerTypes(List<NamedTypeInfo> disjointTypes,
			NamedTypeInfo subject)
	{
		List<NamedTypeInfo> toRemove = new LinkedList<NamedTypeInfo>();
		
		for(NamedTypeInfo nexType : disjointTypes)
		{
			if(subject.contains(nexType))
			{
				toRemove.add(nexType);
			}
		}
		
		disjointTypes.removeAll(toRemove);
	}

	public static boolean contains(List<NamedTypeInfo> typeInfoList, NamedTypeInfo subject)
	{
		for(NamedTypeInfo nextType : typeInfoList)
		{
			if(nextType.contains(subject))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean containsExactly(ANamedInvariantType node)
	{
		String module = node.getName().getModule();
		String typeName = node.getName().getName();

		for (NamedTypeInfo t : typeInfoList)
		{
			if (NamedTypeInfo.isSameTypeDef(t, module, typeName))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public void caseANamedInvariantType(ANamedInvariantType node)
			throws AnalysisException
	{
		// Avoid unnecessary construction
		if (!containsExactly(node))
		{
			typeInfoList.addAll(create(node, null));
		}
	}

	private static PType resolve(PType type)
	{
		while (type instanceof AOptionalType || type instanceof ABracketType)
		{
			if (type instanceof AOptionalType)
			{
				type = ((AOptionalType) type).getType();
			} else if (type instanceof ABracketType)
			{
				type = ((ABracketType) type).getType();
			}
		}

		return type;
	}

	private static List<NamedTypeInfo> create(PType type, NamedTypeInfo previous)
	{
		type = resolve(type);

		List<NamedTypeInfo> data = new LinkedList<NamedTypeInfo>();

		if (type instanceof ANamedInvariantType)
		{
			ANamedInvariantType namedType = (ANamedInvariantType) type;
			NamedTypeInfo typeData = new NamedTypeInfo(namedType.getName().getName(), namedType.getName().getModule(), namedType.getInvDef() != null);

			typeData.getNamedTypes().addAll(create(namedType.getType(), typeData));

			data.add(typeData);
		} else if (type instanceof AUnionType)
		{
			for (PType t : ((AUnionType) type).getTypes())
			{
				data.addAll(create(t, previous));
			}
		} else
		{
			if (previous != null)
			{
				previous.getLeafTypes().add(type);
			}
		}

		return data;
	}
}
