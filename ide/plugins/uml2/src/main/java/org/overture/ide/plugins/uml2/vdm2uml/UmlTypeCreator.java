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
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SBasicType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.types.SNumericBasicType;

public class UmlTypeCreator extends UmlTypeCreatorBase
{
	private Model modelWorkingCopy = null;
	
	
	public void setModelWorkingCopy(Model modelWorkingCopy)
	{
		this.modelWorkingCopy = modelWorkingCopy;
	}
	
//	private void addPrimitiveTypes()
//	{
//
//		types.put("int", modelWorkingCopy.createOwnedPrimitiveType("int"));
//		types.put("bool", modelWorkingCopy.createOwnedPrimitiveType("bool"));
//		types.put("nat", modelWorkingCopy.createOwnedPrimitiveType("nat"));
//		types.put("nat1", modelWorkingCopy.createOwnedPrimitiveType("nat1"));
//		types.put("real", modelWorkingCopy.createOwnedPrimitiveType("real"));
//		types.put("char", modelWorkingCopy.createOwnedPrimitiveType("char"));
//		types.put("token", modelWorkingCopy.createOwnedPrimitiveType("token"));
//		types.put("String", modelWorkingCopy.createOwnedPrimitiveType("String"));
//
//	}
	
	public void create(Class class_, LexNameToken name, PType type)
	{
		switch (type.kindPType())
		{
			case UNION:
				createNewUmlUnionType(class_,name, (AUnionType) type);
				break;
			case INVARIANT:
				createNewUmlInvariantType(class_,name, (SInvariantType) type);
				break;
			case BASIC:
				convertBasicType(class_,(SBasicType) type, modelWorkingCopy, types, name);
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
	}

	
	private void createNewUmlUnionType(Class class_, LexNameToken name, AUnionType type)
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

	private void createNewUmlInvariantType(Class class_,LexNameToken name,
			SInvariantType type)
	{
		switch (type.kindSInvariantType())
		{
			case NAMED:
				PType ptype = ((ANamedInvariantType) type).getType();
				create(class_,name, ptype);
				break;
			case RECORD:
				break;
		}

	}


	
	public Type getUmlType(PType type)
	{
		Type result = null;

		switch (type.kindPType())
		{
			case BASIC:
				result = convertBasicType((SBasicType) type, modelWorkingCopy,types);
				break;
			case BRACKET:
				break;
			case FUNCTION:
				break;
			case INVARIANT:
				{
					switch (((SInvariantType)type).kindSInvariantType())
					{
						case NAMED:
						{
							String name = ((ANamedInvariantType) type).getName().name;
							result = types.get(name);
							break;
						}
						case RECORD:
						{
							String name = ((ARecordInvariantType) type).getName().name;
							result = types.get(name);
							break;
						}

					}
				}
				break;
			case MAP:
				break;
			case OPERATION:
				break;
			case OPTIONAL:
				break;
			case PARAMETER:
				break;
			case PRODUCT:
				break;
			case QUOTE:
				break;
			case SEQ:
				// convertTypeSeq(model,(SSeqType) definitionType);
				break;
			case SET:
				break;
			case UNDEFINED:
				break;
			case UNION:
				break;
			case UNKNOWN:
				break;
			case UNRESOLVED:
				break;
			case VOID:
				break;
			case VOIDRETURN:
				break;

		}
		return result;
	}
	
	
	public static void convertBasicType(SBasicType type,
			Model modelWorkingCopy, Map<String, Type> types, LexNameToken name) {

		Type t = convertBasicType(type, modelWorkingCopy, types);
		types.put(name.name, t);
		
	}
	public static Type convertBasicType(Class class_, SBasicType type, Model modelWorkingCopy, Map<String, Type> types, LexNameToken name)  {
		
		
		switch (type.kindSBasicType()) {
		case BOOLEAN:			
			if(!types.containsKey("bool"))
			{
				types.put("bool",modelWorkingCopy.createOwnedPrimitiveType("bool"));
				
			}
			return types.get("bool");			
		case CHAR:
			if(!types.containsKey("char"))
			{
				types.put("char",modelWorkingCopy.createOwnedPrimitiveType("char"));
				
			}
			return types.get("char");	
		case NUMERIC:
			return convertNumericType((SNumericBasicType) type,modelWorkingCopy,types);
		case TOKEN:
			if(!types.containsKey("token"))
			{
				types.put("token",modelWorkingCopy.createOwnedPrimitiveType("token"));
				
			}
			return types.get("token");	
		default:
			assert false : "Should not happen";
			break;
		}
		return null;
	}

	private static Type convertNumericType(SNumericBasicType type, Model modelWorkingCopy, Map<String, Type> types)  {
		switch (type.kindSNumericBasicType()) {
		case INT:
			if(!types.containsKey("int"))
			{
				types.put("int",modelWorkingCopy.createOwnedPrimitiveType("int"));
				
			}
			return types.get("int");	
		case NAT:
			if(!types.containsKey("nat"))
			{
				types.put("nat",modelWorkingCopy.createOwnedPrimitiveType("nat"));
				
			}
			return types.get("nat");	
		case NATONE:
			if(!types.containsKey("nat1"))
			{
				types.put("nat1",modelWorkingCopy.createOwnedPrimitiveType("nat1"));
				
			}
			return types.get("nat1");	
		case RATIONAL:
			if(!types.containsKey("rat"))
			{
				types.put("rat",modelWorkingCopy.createOwnedPrimitiveType("rat"));
				
			}
			return types.get("rat");	
		case REAL:
			if(!types.containsKey("real"))
			{
				types.put("real",modelWorkingCopy.createOwnedPrimitiveType("real"));
				
			}
			return types.get("real");	
		default:
			assert false : "Should not happen";
			break;
		}
		return null;
	}
}
