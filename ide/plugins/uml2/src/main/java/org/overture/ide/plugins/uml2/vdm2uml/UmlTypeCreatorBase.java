package org.overture.ide.plugins.uml2.vdm2uml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.uml2.uml.Type;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;

public class UmlTypeCreatorBase
{
	public final Map<String, Type> types = new HashMap<String, Type>();
	public final String templateSetName = "Set<T>";
	public final String templateSeqName = "Seq<T>";
	public final String templateMapName = "Map<D,R>";
	public final static String NAME_SEPERATOR = "::";

	protected static String getTemplateUnionName(int templateNameCount)
	{
		return getTemplateBaseName("Union",templateNameCount);
	}
	
	protected static String getTemplateProductName(int templateNameCount)
	{
		return getTemplateBaseName("Product",templateNameCount);
	}
	
	private static String getTemplateBaseName(String name,int templateNameCount)
	{
		String[] templateNames = getTemplateNames(templateNameCount);
		 name+= "<";
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
			names[i]= Character.valueOf((char)('A'+i)).toString();
		}
		return names;
	}

	public static String getName(PType type)
	{
		switch (type.kindPType())
		{
			case BASIC:
				return type.toString();
			case BRACKET:
				return getName(((ABracketType) type).getType());
			case CLASS:
				return ((AClassType) type).getName().name;
			case FUNCTION:
				return getName(((AFunctionType) type).getResult());
			case INVARIANT:
			{
				switch (((SInvariantType) type).kindSInvariantType())
				{
					case NAMED:
						return SClassDefinition.class.cast(type.getAncestor(SClassDefinition.class)).getName().name
								+ NAME_SEPERATOR
								+ ((ANamedInvariantType) type).getName().name;
					case RECORD:
						return SClassDefinition.class.cast(type.getAncestor(SClassDefinition.class)).getName().name
								+ NAME_SEPERATOR
								+ ((ARecordInvariantType) type).getName().name;
						
				}
			}
				break;
			case MAP:
				return "Map<" + getName(((SMapType) type).getFrom()) + ","
						+ getName(((SMapType) type).getTo()) + ">";
			case OPERATION:
				return getName(((AOperationType) type).getResult());
			case OPTIONAL:
				return getName(((AOptionalType) type).getType());
			case PARAMETER:
				break;
			case PRODUCT:
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

			}
			case QUOTE:
				return ((AQuoteType) type).getValue().value;
			case SEQ:
				return "Seq<" + getName(((SSeqType) type).getSeqof()) + ">";
			case SET:
				return "Set<" + getName(((ASetType) type).getSetof()) + ">";
			case UNDEFINED:
				break;
			case UNION:
			{
				if (Vdm2UmlUtil.isUnionOfQuotes((AUnionType) type))
				{
					LexNameToken name = ATypeDefinition.class.cast(type.getAncestor(ATypeDefinition.class)).getName();
					return name.module + NAME_SEPERATOR + name.name;
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

			}
			case UNKNOWN:
				break;
			case UNRESOLVED:
				break;
			case VOID:
				return "void";
			case VOIDRETURN:
				break;

		}
		return "unknown";
	}
}
