package org.overture.ide.plugins.uml2.vdm2uml;

import java.util.Map;

import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;
import org.overture.ast.expressions.PExp;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.EMapType;
import org.overture.ast.types.EType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.interpreter.assistant.type.PTypeAssistantInterpreter;

public class Vdm2UmlAssociationUtil
{
	public static boolean isSimpleType(PType type)
	{
		if (type instanceof ANamedInvariantType)
		{
			return true;

		}
		return PTypeAssistantInterpreter.isClass(type);// || type.kindPType()==EType.BASIC;
	}

	public static boolean validType(PType type)
	{
		if (PTypeAssistantInterpreter.isClass(type))
		{
			return true;
		}

		switch (type.kindPType())
		{
			case BASIC:
				return false;
			case BRACKET:
				break;
			case CLASS:
				break;
			case FUNCTION:
				break;
			case INVARIANT:
				return type instanceof ANamedInvariantType;
				// ANamedInvariantType nInvType = (ANamedInvariantType) type;
				// return
				// break;
			case MAP:
				SMapType mType = (SMapType) type;
				// return isSimpleType(mType.getFrom())
				// && isSimpleType(mType.getTo());
				return validMapType(mType.getFrom())
						&& validMapType(mType.getTo());
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
				SSeqType seqType = (SSeqType) type;
				return isSimpleType(seqType.getSeqof());
			case SET:
				ASetType setType = (ASetType) type;
				return isSimpleType(setType.getSetof());
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
		return false;
	}

	private static boolean validMapType(PType type)
	{
		if (isSimpleType(type) || PTypeAssistantInterpreter.isClass(type))
		{
			return true;
		}

		switch (type.kindPType())
		{
			case SEQ:
				SSeqType seqType = (SSeqType) type;
				return isSimpleType(seqType.getSeqof());
			case SET:
				ASetType setType = (ASetType) type;
				return isSimpleType(setType.getSetof());
			default:
				return false;
		}
	}

	public static Type getReferenceClass(PType type, Map<String, Class> classes)
	{
		if (PTypeAssistantInterpreter.isClass(type))
		{
			return getType(classes, type);
		}

		switch (type.kindPType())
		{
			case BASIC:
				break;
			case BRACKET:
				break;
			case CLASS:
				break;
			case FUNCTION:
				break;
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					ANamedInvariantType nInvType = (ANamedInvariantType) type;
					return getType(classes, nInvType);
				}
				break;
			case MAP:
				SMapType mType = (SMapType) type;
				return getTypeForMap(classes, mType.getTo());
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
				SSeqType seqType = (SSeqType) type;
				return getType(classes, seqType.getSeqof());
			case SET:
				ASetType setType = (ASetType) type;
				return getType(classes, setType.getSetof());
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

		return null;
	}

	private static Type getType(Map<String, Class> classes, String name)
	{
		if (classes.containsKey(name))
		{
			return classes.get(name);
		}
		for (Class c : classes.values())
		{
			if (name.contains(UmlTypeCreatorBase.NAME_SEPERATOR))
			{
				int index = name.indexOf(UmlTypeCreatorBase.NAME_SEPERATOR);
				if (!c.getName().equals(name.subSequence(0, index)))
				{
					continue;
				} else
				{
					name = name.substring(index
							+ UmlTypeCreatorBase.NAME_SEPERATOR.length());
				}
			}
			Classifier ncl = c.getNestedClassifier(name);
			if (ncl != null)
			{
				return ncl;
			}
		}

		return null;
	}

	static Type getType(Map<String, Class> classes, PType type)
	{
		return getType(classes, UmlTypeCreatorBase.getName(type));
	}
	
	static Type getTypeForMap(Map<String, Class> classes, PType type)
	{
		if(type.kindPType()==EType.SEQ)
		{
			type = ((SSeqType)type).getSeqof();
		}else if (type.kindPType()==EType.SET)
		{
			type =((ASetType)type).getSetof();
		}
		return getType(classes, UmlTypeCreatorBase.getName(type));
	}

	public static void createAssociation(String name, PType defType,AAccessSpecifierAccessSpecifier access,PExp defaultExp ,Map<String, Class> classes, Type class_, boolean readOnly)
	{
		Type referencedClass = Vdm2UmlAssociationUtil.getReferenceClass(defType, classes);

		int lower = Vdm2UmlUtil.extractLower(defType);

		Association association = class_.createAssociation(true, AggregationKind.NONE_LITERAL, name, lower, Vdm2UmlUtil.extractUpper(defType), referencedClass, false, AggregationKind.NONE_LITERAL, "", 1, 1);
		association.setVisibility(Vdm2UmlUtil.convertAccessSpecifierToVisibility(access));

		Property prop = association.getMemberEnd(name, null);
		prop.setIsReadOnly(readOnly);

		//set default
		if (defaultExp != null)
		{
			prop.setDefault(defaultExp.toString());
		}
		//set static
		prop.setIsStatic(access.getStatic() != null);
		
		//set ordered
		prop.setIsOrdered(defType.kindPType()==EType.SEQ);
		prop.setIsUnique(defType.kindPType()!=EType.SEQ &&defType.kindPType()!=EType.MAP);
		
		//set qualifier if map
		if (defType.kindPType()==EType.MAP)
		{
			SMapType mType = (SMapType) defType;
			PType fromType = mType.getFrom();
			PType toType = mType.getTo();
			Property qualifier = prop.createQualifier(null, Vdm2UmlAssociationUtil.getReferenceClass(fromType, classes));
			qualifier.setLower(Vdm2UmlUtil.extractLower(fromType));
			qualifier.setUpper(Vdm2UmlUtil.extractUpper(fromType));
			//set ordered
			qualifier.setIsOrdered(fromType.kindPType()==EType.SEQ);
			qualifier.setIsUnique(fromType.kindPType()!=EType.SEQ &&fromType.kindPType()!=EType.MAP);
			
			prop.setLower(Vdm2UmlUtil.extractLower(toType));
			prop.setUpper(Vdm2UmlUtil.extractUpper(toType));
			//set ordered
			prop.setIsOrdered(toType.kindPType()==EType.SEQ);
			prop.setIsUnique(toType.kindPType()!=EType.SEQ &&toType.kindPType()!=EType.MAP);
			
			//Map unique
			prop.setIsUnique(mType.kindSMapType()==EMapType.INMAP);
			Property targetProp = association.getMemberEnd("", null);
			targetProp.setIsUnique(true);
			
		}
	}

	// public static Class getClassName(PType defType,Map<String, Class> classes)
	// {
	// switch (defType.kindPType())
	// {
	// case CLASS:
	// return classes.get(((AClassType) defType).getName().name);
	// case OPTIONAL:
	// return getClassName(((AOptionalType) defType).getType(),classes);
	// default:
	// break;
	// }
	//
	// return null;
	// }
}
