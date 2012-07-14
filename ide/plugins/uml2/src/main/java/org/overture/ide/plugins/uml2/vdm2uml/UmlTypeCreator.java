package org.overture.ide.plugins.uml2.vdm2uml;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Type;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SBasicType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.types.SNumericBasicType;

public class UmlTypeCreator extends UmlTypeCreatorBase
{
	private Model modelWorkingCopy = null;
	private final Map<String, Type> types = new HashMap<String, Type>();

	public void setModelWorkingCopy(Model modelWorkingCopy)
	{
		this.modelWorkingCopy = modelWorkingCopy;
	}

	// private void addPrimitiveTypes()
	// {
	//
	// types.put("int", modelWorkingCopy.createOwnedPrimitiveType("int"));
	// types.put("bool", modelWorkingCopy.createOwnedPrimitiveType("bool"));
	// types.put("nat", modelWorkingCopy.createOwnedPrimitiveType("nat"));
	// types.put("nat1", modelWorkingCopy.createOwnedPrimitiveType("nat1"));
	// types.put("real", modelWorkingCopy.createOwnedPrimitiveType("real"));
	// types.put("char", modelWorkingCopy.createOwnedPrimitiveType("char"));
	// types.put("token", modelWorkingCopy.createOwnedPrimitiveType("token"));
	// types.put("String", modelWorkingCopy.createOwnedPrimitiveType("String"));
	//
	// }

	public void create(Class class_, LexNameToken name, PType type)
	{
		System.out.println(type + " " + type.kindPType().toString() + " "
				+ getName(type));
		if (types.get(getName(type)) != null)
		{
			return;
		}

		switch (type.kindPType())
		{
			case UNION:
				createNewUmlUnionType(class_, name, (AUnionType) type);
				return;
			case INVARIANT:
				createNewUmlInvariantType(class_, name, (SInvariantType) type);
				return;
			case BASIC:
				convertBasicType(class_, (SBasicType) type);
				return;
			case BRACKET:
			case CLASS:
			case FUNCTION:
			case MAP:
			case OPERATION:
			case OPTIONAL:
			case PARAMETER:
			case PRODUCT:
			case QUOTE:
			case SEQ:
			case SET:
			case UNDEFINED:
			case UNKNOWN:
			case UNRESOLVED:
			case VOID:
			case VOIDRETURN:

			default:
				break;
		}
		if (!types.containsKey(getName(type)))
		{
			Type unknownType = modelWorkingCopy.createOwnedPrimitiveType("Unknown");
			unknownType.addKeyword(getName(type));
			types.put(getName(type), unknownType);
		}
	}

	private void createNewUmlUnionType(Class class_, LexNameToken name,
			AUnionType type)
	{

		if (Vdm2UmlUtil.isUnionOfQuotes(type))
		{

			Enumeration enumeration = modelWorkingCopy.createOwnedEnumeration(name.name);
			for (PType t : type.getTypes())
			{
				if (t instanceof AQuoteType)
				{
					enumeration.createOwnedLiteral(((AQuoteType) t).getValue().value);
				}
			}
			class_.createNestedClassifier(name.name, enumeration.eClass());
			types.put(name.name, enumeration);
		} else
		{
			// do the constraint XOR

		}

	}

	private void createNewUmlInvariantType(Class class_, LexNameToken name,
			SInvariantType type)
	{
		switch (type.kindSInvariantType())
		{
			case NAMED:
				PType ptype = ((ANamedInvariantType) type).getType();
				create(class_, name, ptype);
				break;
			case RECORD:
				break;
		}

	}

	public Type getUmlType(PType type)
	{
		String name = getName(type);

		if (types.containsKey(name))
		{
			return types.get(name);
		}
		// else
		// {
		// System.err.println("Trying to find unknown type: "+name);
		return null;
		// }
	}

	private void convertBasicType(Class class_, SBasicType type)
	{
		String typeName = null;
		switch (type.kindSBasicType())
		{
			case BOOLEAN:
				typeName = "bool";
				break;
			case CHAR:
				typeName = "char";
				break;
			case NUMERIC:
				convertNumericType((SNumericBasicType) type);
				return;
			case TOKEN:
				typeName = "token";
				break;
			default:
				assert false : "Should not happen";
				break;
		}

		if (!types.containsKey(getName(type)))
		{
			types.put(getName(type), modelWorkingCopy.createOwnedPrimitiveType(typeName));

		}
	}

	private void convertNumericType(SNumericBasicType type)
	{
		String typeName = null;
		switch (type.kindSNumericBasicType())
		{
			case INT:
				typeName = "int";
				break;
			case NAT:
				typeName = "nat";
				break;
			case NATONE:
				typeName = "nat1";
				break;
			case RATIONAL:
				typeName = "rat";
				break;
			case REAL:
				typeName = "real";
				break;
			default:
				assert false : "Should not happen";
				return;
		}
		if (!types.containsKey(getName(type)))
		{
			types.put(getName(type), modelWorkingCopy.createOwnedPrimitiveType(typeName));

		}
	}
}
