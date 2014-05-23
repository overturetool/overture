package org.overture.ide.plugins.uml2.vdm2uml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.uml2.uml.Type;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AInMapMapType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUndefinedType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.AUnresolvedType;
import org.overture.ast.types.AVoidReturnType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SBasicType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;

public class UmlTypeCreatorBase
{
	public final Map<String, Type> types = new HashMap<String, Type>();
	public static final String templateSetName = "Set<T>";
	public static final String templateSeqName = "Seq<T>";
	public static final String templateMapName = "Map<D,R>";
	public static final String templateInMapName = "InMap<D,R>";
	public static final String VOID_TYPE = "Void";
	public static final String ANY_TYPE = "Any";
	public static final String templateOptionalName = "Optional<T>";
	public static final String NAME_SEPERATOR = "::";
	public static final String UNKNOWN_TYPE = "_Unknown_";

	protected static String getTemplateUnionName(int templateNameCount)
	{
		return getTemplateBaseName("Union", templateNameCount);
	}

	protected static String getTemplateProductName(int templateNameCount)
	{
		return getTemplateBaseName("Product", templateNameCount);
	}

	private static String getTemplateBaseName(String name, int templateNameCount)
	{
		String[] templateNames = getTemplateNames(templateNameCount);
		name += "<";
		for (int i = 0; i < templateNames.length; i++)
		{
			name += templateNames[i];
			if (i < templateNames.length - 1)
			{
				name += ",";
			}
		}
		return name + ">";
	}

	protected static String[] getTemplateNames(int templateNameCount)
	{
		String[] names = new String[templateNameCount];
		for (int i = 0; i < templateNameCount; i++)
		{
			names[i] = Character.valueOf((char) ('A' + i)).toString();
		}
		return names;
	}

	public static String getName(PType type)
	{
		if (type instanceof SBasicType)
		{
			return type.toString();
		} else if (type instanceof ABracketType)
		{
			return getName(((ABracketType) type).getType());
		} else if (type instanceof AClassType)
		{
			return ((AClassType) type).getName().getName();
		} else if (type instanceof AFunctionType)
		{
			return getName(((AFunctionType) type).getResult());
		} else if (type instanceof SInvariantType)
		{
			if (type instanceof ANamedInvariantType)
			{
				return SClassDefinition.class.cast(type.getAncestor(SClassDefinition.class)).getName().getName()
						+ NAME_SEPERATOR
						+ ((ANamedInvariantType) type).getName().getName();
			} else if (type instanceof ARecordInvariantType)
			{
				return SClassDefinition.class.cast(type.getAncestor(SClassDefinition.class)).getName().getName()
						+ NAME_SEPERATOR
						+ ((ARecordInvariantType) type).getName().getName();
			}
		} else if (type instanceof SMapType)
		{
			return (type instanceof AInMapMapType ? "In" : "") + "Map<"
					+ getName(((SMapType) type).getFrom()) + ","
					+ getName(((SMapType) type).getTo()) + ">";
		} else if (type instanceof AOperationType)
		{
			return getName(((AOperationType) type).getResult());
		} else if (type instanceof AOptionalType)
		{
			return "Optional<" + getName(((AOptionalType) type).getType())
					+ ">";
		} else if (type instanceof AParameterType)
		{
			return ((AParameterType) type).getName().getName();
		} else if (type instanceof AProductType)
		{
			String name = "Product<";
			for (Iterator<PType> itr = ((AProductType) type).getTypes().iterator(); itr.hasNext();)
			{
				name += getName(itr.next());
				if (itr.hasNext())
				{
					name += ",";
				}

			}
			return name + ">";
		} else if (type instanceof AQuoteType)
		{
			return ((AQuoteType) type).getValue().getValue();
		} else if (type instanceof SSeqType)
		{
			return "Seq<" + getName(((SSeqType) type).getSeqof()) + ">";
		} else if (type instanceof ASetType)
		{
			return "Set<" + getName(((ASetType) type).getSetof()) + ">";
		} else if (type instanceof AUndefinedType)
		{
		} else if (type instanceof AUnionType)
		{
			if (Vdm2UmlUtil.isUnionOfQuotes((AUnionType) type))
			{
				String namePostfix = "_"
						+ type.toString().replaceAll("[^A-Za-z0-9]", "")
						+ ("_" + type.toString().hashCode()).replace('-', '_');
				PDefinition def = type.getAncestor(PDefinition.class);
				if (def != null)
				{
					if (def instanceof AValueDefinition)
					{
						return def.getLocation().getModule()
								+ NAME_SEPERATOR
								+ ((AValueDefinition) def).getPattern().toString().replace(" ", "").trim()
								+ namePostfix;
					}
					ILexNameToken nameTypeDef = PDefinition.class.cast(def).getName();
					return nameTypeDef.getModule() + NAME_SEPERATOR
							+ nameTypeDef.getName() + namePostfix;
				} else
				{
					String name = "GeneratedUnion";
					for (Iterator<PType> itr = ((AUnionType) type).getTypes().iterator(); itr.hasNext();)
					{
						name += getName(itr.next());
					}
					return name;
				}
			}
			String name = "Union<";
			for (Iterator<PType> itr = ((AUnionType) type).getTypes().iterator(); itr.hasNext();)
			{
				name += getName(itr.next());
				if (itr.hasNext())
				{
					name += ",";
				}

			}
			return name + ">";
		} else if (type instanceof AUnknownType)
		{
			return ANY_TYPE;
		} else if (type instanceof AUnresolvedType)
		{
		} else if (type instanceof AVoidType)
		{
			return VOID_TYPE;
		} else if (type instanceof AVoidReturnType)
		{
		}
		return UNKNOWN_TYPE;
	}
}
