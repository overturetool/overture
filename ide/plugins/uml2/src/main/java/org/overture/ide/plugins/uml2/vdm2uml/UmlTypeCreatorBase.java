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
	public	 static final String UNKNOWN_TYPE = "_Unknown_";

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
			case SBasicType.kindPType:
				return type.toString();
			case ABracketType.kindPType:
				return getName(((ABracketType) type).getType());
			case AClassType.kindPType:
				return ((AClassType) type).getName().name;
			case AFunctionType.kindPType:
				return getName(((AFunctionType) type).getResult());
			case SInvariantType.kindPType:
			{
				switch (((SInvariantType) type).kindSInvariantType())
				{
					case ANamedInvariantType.kindSInvariantType:
						return SClassDefinition.class.cast(type.getAncestor(SClassDefinition.class)).getName().name
								+ NAME_SEPERATOR
								+ ((ANamedInvariantType) type).getName().name;
					case ARecordInvariantType.kindSInvariantType:
						return SClassDefinition.class.cast(type.getAncestor(SClassDefinition.class)).getName().name
								+ NAME_SEPERATOR
								+ ((ARecordInvariantType) type).getName().name;
						
				}
			}
				break;
			case SMapType.kindPType:
				return (AInMapMapType.kindSMapType.equals(((SMapType)type).kindSMapType())?"In":"")
						+ "Map<" + getName(((SMapType) type).getFrom()) + ","
						+ getName(((SMapType) type).getTo()) + ">";
			case AOperationType.kindPType:
				return getName(((AOperationType) type).getResult());
			case AOptionalType.kindPType:
				return "Optional<"+getName(((AOptionalType) type).getType())+">";
			case AParameterType.kindPType:
				return ((AParameterType)type).getName().name;
			case AProductType.kindPType:
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
			case AQuoteType.kindPType:
				return ((AQuoteType) type).getValue().value;
			case SSeqType.kindPType:
				return "Seq<" + getName(((SSeqType) type).getSeqof()) + ">";
			case ASetType.kindPType:
				return "Set<" + getName(((ASetType) type).getSetof()) + ">";
			case AUndefinedType.kindPType:
				break;
			case AUnionType.kindPType:
			{
				
				if (Vdm2UmlUtil.isUnionOfQuotes((AUnionType) type))
				{
					ATypeDefinition typeDef = type.getAncestor(ATypeDefinition.class);
					if(typeDef!=null)
					{
						LexNameToken nameTypeDef = ATypeDefinition.class.cast(typeDef).getName();
						return nameTypeDef.module + NAME_SEPERATOR + nameTypeDef.name;
					}else
				{
					String name="GeneratedUnion";
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

			}
			case AUnknownType.kindPType:
				return ANY_TYPE;
			case AUnresolvedType.kindPType:
				break;
			case AVoidType.kindPType:
				return VOID_TYPE;
			case AVoidReturnType.kindPType:
				break;

		}
		return UNKNOWN_TYPE;
	}
}
