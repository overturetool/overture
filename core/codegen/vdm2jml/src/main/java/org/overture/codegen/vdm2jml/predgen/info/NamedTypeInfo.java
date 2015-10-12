package org.overture.codegen.vdm2jml.predgen.info;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.vdm2jml.JmlGenerator;

public class NamedTypeInfo extends AbstractTypeInfo
{
	private String typeName;
	private String defModule;
	private boolean hasInv;
	private AbstractTypeInfo domainType; // T = <domainType>
	
	public NamedTypeInfo(String typeName, String defModule, boolean hasInv, boolean optional, AbstractTypeInfo domainType)
	{
		super(optional);
		this.typeName = typeName;
		this.defModule = defModule;
		this.hasInv = hasInv;
		this.domainType = domainType;
	}

	public String getTypeName()
	{
		return typeName;
	}

	public String getDefModule()
	{
		return defModule;
	}

	public boolean hasInv()
	{
		return hasInv;
	}
	
	public boolean contains(AbstractTypeInfo other)
	{
		//TODO: Returning false leads to redundant dynamic type check which is not optimal
		return false;
		
//		if (equals(other))
//		{
//			return true;
//		} else
//		{
//			for (NamedTypeInfo n : namedTypes)
//			{
//				if (n.contains(other))
//				{
//					return true;
//				}
//			}
//		}
//
//		return false;
	}

	@Override
	public int hashCode()
	{
		int hashCode = 0;

		if (defModule != null)
		{
			hashCode += defModule.hashCode();
		}

		if (typeName != null)
		{
			hashCode += typeName.hashCode();
		}

		return hashCode;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof NamedTypeInfo))
		{
			return false;
		}

		NamedTypeInfo other = (NamedTypeInfo) obj;

		return isSameTypeDef(this, other.defModule, other.typeName);
	}

	public static boolean isSameTypeDef(NamedTypeInfo typeData,
			String otherDefModule, String otherTypeName)
	{
		if (typeData.defModule == null && otherDefModule != null
				|| typeData.defModule != null
				&& !typeData.defModule.equals(otherDefModule))
		{
			return false;
		}

		if (typeData.typeName == null && otherTypeName != null
				|| typeData.typeName != null
				&& !typeData.typeName.equals(otherTypeName))
		{
			return false;
		}

		// The defining module and the type name should form unique identification

		return true;
	}

	@Override
	public String consCheckExp(String enclosingModule, String javaRootPackages)
	{
		StringBuilder sb = new StringBuilder();
		consCheckExp(sb, enclosingModule, javaRootPackages);
		return sb.toString();
	}
	
	private void consCheckExp(StringBuilder sb, String enclosingModule, String javaRootPackage)
	{
//		// If the type is not defined in the enclosing class we use the absolute name
//		// to refer to the invariant method
//		if(!defModule.equals(enclosingModule))
//		{
//			sb.append(javaRootPackage);
//			sb.append('.');
//			sb.append(defModule);
//			sb.append(".");
//		}

		sb.append(JmlGenerator.INV_PREFIX);
		sb.append(defModule);
		sb.append("_");
		sb.append(typeName);

		sb.append('(');
		sb.append(ARG_PLACEHOLDER);
		sb.append(')');
		
		if (domainType != null)
		{
			//TODO: remvoe eventually
			if(!onlyBasics())
			{
				sb.append(JmlGenerator.JML_AND);
				sb.append(domainType.consCheckExp(enclosingModule, javaRootPackage));
				
			}
		}
	}

	//TODO: REMOVE THIS
	private boolean onlyBasics()
	{
		if(domainType instanceof LeafTypeInfo)
		{
			return true;
		}
		
		if(domainType instanceof UnionInfo)
		{
			for(AbstractTypeInfo t : ((UnionInfo) domainType).getTypes())
			{
				if(!(t instanceof LeafTypeInfo))
				{
					return false;
				}
			}
			
			return true;
		}
		
		return false;
	}

	@Override
	public boolean allowsNull()
	{
		// This type allows null either if it is
		// 1) optional or
		// 2) any child type allows null
		//
		// Example. Given:
		// N = nat; C = [char]; CN = C|N
		// Then CN allows null. C allows null. N does not allow null.

		if (optional)
		{
			return true;
		}

		if(domainType != null)
		{
			return domainType.allowsNull();
		}
		
		return false;
	}

	@Override
	public List<LeafTypeInfo> getLeafTypesRecursively()
	{
		return domainType.getLeafTypesRecursively();
	}
	
	@Override
	public String toString()
	{
		return "(" + this.typeName + " = " + domainType + ")";
	}
}
