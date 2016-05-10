/*
 * #%~
 * UML2 Translator
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.plugins.uml2.uml2vdm;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
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
import org.overture.ide.plugins.uml2.UmlConsole;
import org.overture.ide.plugins.uml2.vdm2uml.UmlTypeCreatorBase;

public class VdmTypeCreator
{
	final Set<String> basicTypes = new HashSet<String>(Arrays.asList(new String[] {
			"bool", "char", "token", "int", "nat", "nat1", "rat", "real" }));
	final String UNION_TYPE = "Union<";
	final String MAP_TYPE = "Map<";
	final String SET_TYPE = "Set<";
	final String SEQ_TYPE = "Seq<";
	final String PRODUCT_TYPE = "Product<";
	final String OPTIONAL_TYPE = "Optional<";
	final String INMAP_TYPE = "InMap<";

	final static LexLocation location = new LexLocation(new File("generated"), "generating", 0, 0, 0, 0, 0, 0);
	private UmlConsole console;

	public VdmTypeCreator(UmlConsole console)
	{
		this.console = console;
	}

	public PType convert(Property p)
	{
		PType type = convert(p.getType());
		if (p.getUpper() == LiteralUnlimitedNatural.UNLIMITED)
		{
			if (p.isOrdered())
			{
				if (p.getLower() == 0)
				{
					type = AstFactory.newASeqSeqType(location, type);
				} else if (p.getLower() == 1)
				{
					type = AstFactory.newASeq1SeqType(location, type);
				}
			} else
			{
				type = AstFactory.newASetType(location, type);
			}
		} else if (p.getLower() == 0 && p.getUpper() == 1)
		{
			type = AstFactory.newAOptionalType(location, type);
		}

		if (p.getQualifiers().size() == 1)
		{
			Property qualifier = p.getQualifiers().get(0);
			PType fromType = convert(qualifier);
			if (p.isUnique())
			{
				type = AstFactory.newAInMapMapType(location, fromType, type);
			} else
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
			if (type == null)
			{
				console.err.println("Found no type. Inserting an \"?\" type as a replacement");
				return convert(UmlTypeCreatorBase.ANY_TYPE, null);
			}
			String module = null;
			if (type.getNamespace() != null
					&& type.getNamespace() instanceof Class)
			{
				module = type.getNamespace().getName();
			}

			if (type.getName() == null && type instanceof MinimalEObjectImpl)
			{
				java.lang.reflect.Field f = MinimalEObjectImpl.class.getDeclaredField("eStorage");
				f.setAccessible(true);
				String storageUri = "" + f.get(type);
				System.out.println(storageUri);
				int index = storageUri.lastIndexOf("#");
				if (index == -1)
				{
					console.err.println("Could not decode \"" + storageUri
							+ "\" inseting an \"?\" type as a replacement");
					return convert(UmlTypeCreatorBase.ANY_TYPE, null);
				}
				String typeName = storageUri.substring(index + 1);
				return convert(remapUmlTypes(typeName), module);
			} else if (type.getName() == null)
			{
				console.err.println("Type has no name. Inserting an \"?\" type as a replacement");
				return convert(UmlTypeCreatorBase.ANY_TYPE, null);
			}

			return convert(type.getName(), module);
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private String remapUmlTypes(String name)
	{
		if (name.equalsIgnoreCase("Integer"))
		{
			return "int";
		} else if (name.equalsIgnoreCase("Boolean")
				|| name.equalsIgnoreCase("float")
				|| name.equalsIgnoreCase("long"))
		{
			return "bool";
		} else if (name.equalsIgnoreCase("short")
				|| name.equalsIgnoreCase("byte"))
		{
			return "nat";
		} else if (name.equalsIgnoreCase("String"))
		{
			return "Seq<Char>";
		} else if (name.equalsIgnoreCase("Double"))
		{
			return "real";
		}

		console.err.println("Could not match UML type \""
				+ name
				+ "\" with a VDM type. Inserting an \"?\" type as a replacement");
		return UmlTypeCreatorBase.ANY_TYPE;
	}

	public PType convert(String type, String module)
	{

		if (basicTypes.contains(type.toLowerCase()))
		{
			return convertBasicType(type);
		} else if (type.equals(UmlTypeCreatorBase.VOID_TYPE))
		{
			return AstFactory.newAVoidReturnType(location);
		} else if (type.equals(UmlTypeCreatorBase.ANY_TYPE))
		{
			return AstFactory.newAUnknownType(location);
		} else if (type.startsWith(UNION_TYPE))
		{
			return AstFactory.newAUnionType(location, convertGeneric(type));
		} else if (type.startsWith(PRODUCT_TYPE))
		{
			return AstFactory.newAProductType(location, convertGeneric(type));
		} else if (type.startsWith(MAP_TYPE))
		{
			List<PType> types = convertGeneric(type);
			return AstFactory.newAMapMapType(location, types.get(0), types.get(1));
		} else if (type.startsWith(INMAP_TYPE))
		{
			List<PType> types = convertGeneric(type);
			return AstFactory.newAInMapMapType(location, types.get(0), types.get(1));
		} else if (type.startsWith(SET_TYPE))
		{
			return AstFactory.newASetType(location, convertGeneric(type).get(0));
		} else if (type.startsWith(SEQ_TYPE))
		{
			return AstFactory.newASeqSeqType(location, convertGeneric(type).get(0));
		} else if (type.startsWith("<") && type.endsWith(">"))
		{
			return AstFactory.newAQuoteType(new LexQuoteToken(type.substring(1, type.length() - 1), location));
		} else if (type.startsWith(OPTIONAL_TYPE))
		{
			return AstFactory.newAOptionalType(location, convertGeneric(type).get(0));
		} else
		{
			// String module = "";
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
		String nameNoLessOrGreater = name.substring(name.indexOf("<") + 1, name.length() - 1);

		String[] typeStrings = nameNoLessOrGreater.split(",");

		// decide if the split is valid or if the type is a single generic type
		boolean notSingleGenericType = true;
		for (String part : typeStrings)
		{
			if (part.replaceAll("[^<]", "").length() != part.replaceAll("[^>]", "").length())
			{
				notSingleGenericType = false;
			}
		}

		PTypeList types = new PTypeList();
		if (notSingleGenericType)
		{
			for (String t : typeStrings)
			{
				types.add(convert(t, null));
			}
		} else
		{
			types.add(convert(nameNoLessOrGreater, null));
		}
		return types;
	}

	public PType createRecord(Class innerType)
	{

		List<AFieldField> fields = new ArrayList<AFieldField>();

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
			if (lit.getName().startsWith("<") && lit.getName().endsWith(">"))
			{
				types.add(convert(lit.getName(), null));
			} else
			{
				System.out.println("Problem with conversion of enumeration: "
						+ elem.getName());
			}

		}

		return AstFactory.newAUnionType(location, types);
	}
}
