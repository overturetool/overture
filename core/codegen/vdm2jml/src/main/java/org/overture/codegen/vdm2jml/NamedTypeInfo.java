package org.overture.codegen.vdm2jml;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.types.PType;

public class NamedTypeInfo
{
	private static String ARG_PLACEHOLDER = "%1$s";

	private String typeName;
	private String defModule;
	private boolean hasInv;
	private List<NamedTypeInfo> namedTypes;
	private List<PType> leafTypes;

	public NamedTypeInfo(String typeName, String defModule, boolean hasInv)
	{
		this.typeName = typeName;
		this.defModule = defModule;
		this.hasInv = hasInv;
		this.namedTypes = new LinkedList<NamedTypeInfo>();
		this.leafTypes = new LinkedList<PType>();
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

	public List<PType> getLeafTypes()
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

	public String consCheckExp()
	{
		StringBuilder sb = new StringBuilder();
		consCheckExp(sb);
		return sb.toString();
	}

	private void consCheckExp(StringBuilder sb)
	{
		sb.append(defModule);
		sb.append(".");
		sb.append(JmlGenerator.JML_INV_PREFIX);
		sb.append(typeName);
		sb.append('(');
		sb.append(ARG_PLACEHOLDER);
		sb.append(')');

		if (!namedTypes.isEmpty())
		{
			sb.append(JmlGenerator.JML_AND);
			sb.append('(');

			String orSep = "";
			for (NamedTypeInfo n : namedTypes)
			{
				sb.append(orSep);
				n.consCheckExp(sb);
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
			for (PType leaf : leafTypes)
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

		return sb.toString();
	}
}
