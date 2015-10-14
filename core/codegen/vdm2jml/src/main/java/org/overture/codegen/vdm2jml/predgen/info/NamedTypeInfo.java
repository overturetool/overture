package org.overture.codegen.vdm2jml.predgen.info;


import java.util.List;

import org.overture.codegen.vdm2jml.JmlGenerator;
import org.overture.codegen.vdm2jml.util.NameGen;

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
	public String consCheckExp(String enclosingModule, String javaRootPackages, String arg, NameGen nameGen)
	{
		StringBuilder sb = new StringBuilder();
//		// If the type is not defined in the enclosing class we use the absolute name
//		// to refer to the invariant method
//		if(!defModule.equals(enclosingModule))
//		{
//			sb.append(javaRootPackage);
//			sb.append('.');
//			sb.append(defModule);
//			sb.append(".");
//		}
		
		if (allowsNull())
		{
			sb.append(consIsNullCheck(arg));
			sb.append(JmlGenerator.JML_OR);
		}
		
		if (domainType != null)
		{
			sb.append(domainType.consCheckExp(enclosingModule, javaRootPackages, arg, nameGen));
			sb.append(JmlGenerator.JML_AND);
		}

		sb.append(JmlGenerator.INV_PREFIX);
		sb.append(defModule);
		sb.append("_");
		sb.append(typeName);

		sb.append('(');
		sb.append(arg);
		sb.append(')');
		
		return "(" + sb.toString() + ")";
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
		StringBuilder sb = new StringBuilder();
		
		sb.append('(');
		
		if(optional)
		{
			sb.append("[");
		}
		
		sb.append(this.typeName);
		sb.append(" = ");
		sb.append(domainType);
		
		if(optional)
		{
			sb.append("]");
		}
		
		sb.append(')');
		
		return sb.toString();
	}
}
