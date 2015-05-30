package org.overture.codegen.vdm2jml;

import java.util.LinkedList;
import java.util.List;

public class NamedTypeInfo extends AbstractTypeInfo
{
	private static String ARG_PLACEHOLDER = "%1$s";

	private String typeName;
	private String defModule;
	private boolean hasInv;
	private List<NamedTypeInfo> namedTypes;
	private List<LeafTypeInfo> leafTypes;

	public NamedTypeInfo(String typeName, String defModule, boolean hasInv, boolean optional)
	{
		super(optional);
		this.typeName = typeName;
		this.defModule = defModule;
		this.hasInv = hasInv;
		this.namedTypes = new LinkedList<NamedTypeInfo>();
		this.leafTypes = new LinkedList<LeafTypeInfo>();
	}

	public String getTypeName()
	{
		return typeName;
	}

	public String getDefModule()
	{
		return defModule;
	}

	public List<NamedTypeInfo> getNamedTypes()
	{
		return namedTypes;
	}

	public boolean hasInv()
	{
		return hasInv;
	}

	public List<LeafTypeInfo> getLeafTypes()
	{
		return leafTypes;
	}

	public boolean contains(NamedTypeInfo other)
	{
		if (equals(other))
		{
			return true;
		} else
		{
			for (NamedTypeInfo n : namedTypes)
			{
				if (n.contains(other))
				{
					return true;
				}
			}
		}

		return false;
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
		
		boolean allowsNull = allowsNull();
		if (!namedTypes.isEmpty())
		{
			sb.append(JmlGenerator.JML_AND);
			
			sb.append('(');

			String orSep = "";
			if(allowsNull)
			{
				sb.append(ARG_PLACEHOLDER + " == null");
				orSep = JmlGenerator.JML_OR;
			}
			
			for (NamedTypeInfo n : namedTypes)
			{
				sb.append(orSep);
				n.consCheckExp(sb, enclosingModule, javaRootPackage);
				orSep = JmlGenerator.JML_OR;
			}
			
			sb.append(')');
		}
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("Module: ");
		sb.append(defModule);
		sb.append('\n');
		sb.append("Type name: ");
		sb.append(typeName);
		sb.append('\n');

		sb.append("Name types: ");
		if (!namedTypes.isEmpty())
		{
			String sep = "";
			for (NamedTypeInfo t : namedTypes)
			{
				sb.append(sep);
				sb.append(t.getDefModule() + "." + t.getTypeName());
				sep = ", ";
			}
		} else
		{
			sb.append("None");
		}
		sb.append(".\n");

		sb.append("Leaf types: ");
		if (!leafTypes.isEmpty())
		{
			String sep = "";
			for (LeafTypeInfo leaf : leafTypes)
			{
				sb.append(sep);
				sb.append(leaf.toString());
				sep = ", ";
			}
		} else
		{
			sb.append("None");
		}

		sb.append(".\n");
		
		sb.append("Allows null: ");
		sb.append(allowsNull());
		sb.append(".\n");
		
		sb.append("Has invariant: " + hasInv());

		return sb.toString();
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
		
		if(optional)
		{
			return true;
		}
		else
		{
			for(LeafTypeInfo leaf : leafTypes)
			{
				if(leaf.allowsNull())
				{
					return true;
				}
			}
			
			for(NamedTypeInfo n : namedTypes)
			{
				if(n.allowsNull())
				{
					return true;
				}
			}
		}
		
		return false;
	}

	@Override
	public List<LeafTypeInfo> getLeafTypesRecursively()
	{
		List<LeafTypeInfo> allLeaftypes = new LinkedList<LeafTypeInfo>();
		
		for(LeafTypeInfo leaf : leafTypes)
		{
			allLeaftypes.addAll(leaf.getLeafTypesRecursively());
		}
		
		for(NamedTypeInfo named : namedTypes)
		{
			allLeaftypes.addAll(named.getLeafTypesRecursively());
		}
		
		return allLeaftypes;
	}
}
