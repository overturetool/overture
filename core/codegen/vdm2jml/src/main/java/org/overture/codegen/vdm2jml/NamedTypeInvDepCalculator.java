package org.overture.codegen.vdm2jml;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.logging.Logger;

public class NamedTypeInvDepCalculator extends DepthFirstAnalysisAdaptor
{
	private List<NamedTypeInfo> typeInfoList;
	private IRInfo info;
	
	public NamedTypeInvDepCalculator(IRInfo info)
	{
		super();
		this.info = info;
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

	public static List<NamedTypeInfo> onlyDisjointTypes(
			List<NamedTypeInfo> typeInfo)
	{
		List<NamedTypeInfo> disjointTypes = new LinkedList<NamedTypeInfo>();

		for (NamedTypeInfo t : typeInfo)
		{
			if (!contains(disjointTypes, t))
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

		for (NamedTypeInfo nexType : disjointTypes)
		{
			if (subject.contains(nexType))
			{
				toRemove.add(nexType);
			}
		}

		disjointTypes.removeAll(toRemove);
	}

	public static boolean contains(List<NamedTypeInfo> typeInfoList,
			NamedTypeInfo subject)
	{
		for (NamedTypeInfo nextType : typeInfoList)
		{
			if (nextType.contains(subject))
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
			typeInfoList.addAll(create(info, node, null, new HashSet<PType>()));
		}
	}

	private static List<NamedTypeInfo> create(IRInfo info, PType type, NamedTypeInfo previous, Set<PType> visited)
	{
		if(visited.contains(type))
		{
			return new LinkedList<NamedTypeInfo>(); 
		}
		else
		{
			visited.add(type);
		}
		
		boolean optional = false;
		while (type instanceof AOptionalType || type instanceof ABracketType)
		{
			if (type instanceof AOptionalType)
			{
				type = ((AOptionalType) type).getType();
				optional = true;
			} else if (type instanceof ABracketType)
			{
				type = ((ABracketType) type).getType();
			}
		}

		List<NamedTypeInfo> data = new LinkedList<NamedTypeInfo>();

		if (type instanceof ANamedInvariantType)
		{
			ANamedInvariantType namedType = (ANamedInvariantType) type;
			
			NamedTypeInfo typeData = new NamedTypeInfo(namedType.getName().getName(),
					namedType.getName().getModule(), namedType.getInvDef() != null, optional);

			typeData.getNamedTypes().addAll(create(info, namedType.getType(), typeData, visited));

			data.add(typeData);
		} else if (type instanceof AUnionType)
		{
			// Say we are visiting a union type that is optional, e.g.
			//T = [S | nat];
			// Then we mark the previous type, e.g T as optional
			if(optional && previous != null)
			{
				previous.makeOptional();
			}
			
			for (PType t : ((AUnionType) type).getTypes())
			{
				data.addAll(create(info, t, previous, visited));
			}
		} else
		{
			if (previous != null)
			{
				previous.getLeafTypes().add(new LeafTypeInfo(toIrType(type, info), optional));
			}
		}

		return data;
	}
	
	public static STypeCG toIrType(PType type, IRInfo info)
	{
		try
		{
			STypeCG irType = type.apply(info.getTypeVisitor(), info);
			
			if(irType != null)
			{
				irType.setOptional(false);
			}
			
			return irType;
			
		} catch (AnalysisException e)
		{
			Logger.getLog().printErrorln("Problems encountered while attempting "
					+ "to construct the IR type from a VDM type: "
					+ e.getMessage() + " in '"
					+ LeafTypeInfo.class.getSimpleName() + "'");
			e.printStackTrace();
			return null;
		}
	}
}
