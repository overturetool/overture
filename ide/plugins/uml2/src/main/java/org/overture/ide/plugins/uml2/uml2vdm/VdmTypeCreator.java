package org.overture.ide.plugins.uml2.uml2vdm;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;
import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.lex.LexQuoteToken;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.PType;

public class VdmTypeCreator
{
	final Set<String> basicTypes = new HashSet<String>(Arrays.asList(new String[] {
			"bool", "char", "token", "int", "nat", "nat1", "rat", "real" }));
	final String UNION_TYPE = "Union";
	final String MAP_TYPE = "Map";
	final String SET_TYPE = "Set";
	final String SEQ_TYPE = "Seq";
	final String PRODUCT_TYPE = "Product";

	final static LexLocation location = new LexLocation(new File("generated"), "generating", 0, 0, 0, 0, 0, 0);

	public PType convert(Property p)
	{
		PType type = convert(p.getType());
		if(p.getUpper()==LiteralUnlimitedNatural.UNLIMITED)
		{
			if(p.isOrdered())
			{
				if(p.getLower() ==0)
				{
					type =  AstFactory.newASeqSeqType(location, type);
				}else if(p.getLower()==1)
				{
					type = AstFactory.newASeq1SeqType(location, type);
				}
			}else
			{
				type =  AstFactory.newASetType(location, type);
			}
		}
		
		if(p.getQualifiers().size()==1)
		{
			Property qualifier = p.getQualifiers().get(0);
			PType fromType = convert(qualifier);
			if(p.isUnique())
			{
				type = AstFactory.newAInMapMapType(location, fromType, type);
			}else
			{
				type = AstFactory.newAMapMapType(location, fromType, type);
			}
		}
		return type;
	}
	
	public PType convert(Type type)
	{
		try
		{
			String module = null;
			if(type.getNamespace()!=null && type.getNamespace() instanceof Class)
			{
				module = type.getNamespace().getName();
			}
			return convert(type.getName(),module);
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public PType convert(String type,String module)
	{

		if (basicTypes.contains(type.toLowerCase()))
		{
			return convertBasicType(type);
		} else if (type.startsWith(UNION_TYPE))
		{
			return AstFactory.newAUnionType(location, convertGeneric(type.substring(UNION_TYPE.length())));
		} else if (type.startsWith(PRODUCT_TYPE))
		{
			return AstFactory.newAProductType(location, convertGeneric(type.substring(PRODUCT_TYPE.length())));
		} else if (type.startsWith(MAP_TYPE))
		{
			List<PType> types = convertGeneric(type.substring(MAP_TYPE.length()));
			return AstFactory.newAMapMapType(location, types.get(0), types.get(1));
		} else if (type.startsWith(SET_TYPE))
		{
			return AstFactory.newASetType(location, convertGeneric(type.substring(SET_TYPE.length())).get(0));
		} else if (type.startsWith(SEQ_TYPE))
		{
			return AstFactory.newASeqSeqType(location, convertGeneric(type.substring(SEQ_TYPE.length())).get(0));
		}else if(type.startsWith("<")&& type.endsWith(">"))
		{
			return AstFactory.newAQuoteType(new LexQuoteToken(type.substring(1,type.length()-1), location));
		}
		else
		{
			//String module = "";
			String name = "";
			if (type.contains("::"))
			{
				module = type.substring(0, type.indexOf(":"));
				name = type.substring(type.lastIndexOf(":") + 1);
			} else
			{
				name = type;
			}

			LexNameToken typeName = new LexNameToken(module, name, location);
			return AstFactory.newANamedInvariantType(typeName, null);
		}

	}

	private PType convertBasicType(String type)
	{
		String name = type.toLowerCase();
		if (name.equals("bool"))
		{
			return AstFactory.newABooleanBasicType(location);
		} else if (name.equals("char"))
		{
			return AstFactory.newACharBasicType(location);
		} else if (name.equals("token"))
		{
			return AstFactory.newATokenBasicType(location);
		} else if (name.equals("int"))
		{
			return AstFactory.newAIntNumericBasicType(location);
		} else if (name.equals("nat"))
		{
			return AstFactory.newANatNumericBasicType(location);
		} else if (name.equals("nat1"))
		{
			return AstFactory.newANatOneNumericBasicType(location);
		} else if (name.equals("rat"))
		{
			return AstFactory.newARationalNumericBasicType(location);
		} else if (name.equals("real"))
		{
			return AstFactory.newARealNumericBasicType(location);
		}
		return null;
	}

	/**
	 * The name must start with < and end with > and be separated by comma.
	 * 
	 * @param name
	 * @return
	 */
	private PTypeList convertGeneric(String name)
	{
		String nameNoLessOrGreater = name.substring(1, name.length() - 1);

		String[] typeStrings = nameNoLessOrGreater.split(",");

		PTypeList types = new PTypeList();
		for (String t : typeStrings)
		{
			types.add(convert(t,null));
		}
		return types;
	}

	public PType createRecord(Class innerType)
	{

		List<AFieldField> fields = new Vector<AFieldField>();

		for (Property p : innerType.getOwnedAttributes())
		{
			LexNameToken tagname = new LexNameToken("", p.getName(), location);
			String tag = p.getName();
			PType type = convert(p.getType());
			fields.add(AstFactory.newAFieldField(tagname, tag, type, false));
		}

		return AstFactory.newARecordInvariantType(location, fields);
	}

	public PType createEnumeration(Enumeration elem)
	{
		PTypeList types = new PTypeList();
		for (EnumerationLiteral lit : elem.getOwnedLiterals())
		{
			if(lit.getName().startsWith("<") && lit.getName().endsWith(">"))
			{
				types.add(convert(lit.getName(),null));
			}else
			{
				System.out.println("Problem with conversion of enumeration: "+ elem.getName() );
			}
			
		}
		
		
		return AstFactory.newAUnionType(location, types );
	}
}
