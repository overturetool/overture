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
		

		switch (type.kindPType())
		{
			case SBasicType.kindPType:
				return false;
			case ABracketType.kindPType:
				break;
			case AClassType.kindPType:
				break;
			case AFunctionType.kindPType:
				return false;
			case SInvariantType.kindPType:
				return type instanceof ANamedInvariantType;
				// ANamedInvariantType nInvType = (ANamedInvariantType) type;
				// return
				// break;
			case SMapType.kindPType:
				SMapType mType = (SMapType) type;
				// return isSimpleType(mType.getFrom())
				// && isSimpleType(mType.getTo());
				return validMapFromType(mType.getFrom())
						&& validMapType(mType.getTo());
			case AOperationType.kindPType:
				return false;
			case AOptionalType.kindPType:
				AOptionalType optionalType = (AOptionalType) type;
				return isSimpleType(optionalType.getType());
			case AParameterType.kindPType:
				return false;
			case AProductType.kindPType:
				return false;
			case AQuoteType.kindPType:
				break;
			case SSeqType.kindPType:
				SSeqType seqType = (SSeqType) type;
				return isSimpleType(seqType.getSeqof());
			case ASetType.kindPType:
				ASetType setType = (ASetType) type;
				return isSimpleType(setType.getSetof());
			case AUndefinedType.kindPType:
			case AUnionType.kindPType:
			case AUnknownType.kindPType:
			case AUnresolvedType.kindPType:
			case AVoidType.kindPType:
			case AVoidReturnType.kindPType:
				return false;

		}
		
		if (PTypeAssistantInterpreter.isClass(type))
		{
			return true;
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
			case SSeqType.kindPType:
				SSeqType seqType = (SSeqType) type;
				return isSimpleType(seqType.getSeqof());
			case ASetType.kindPType:
				ASetType setType = (ASetType) type;
				return isSimpleType(setType.getSetof());
			default:
				return false;
		}
	}
	
	private static boolean validMapFromType(PType type)
	{
		switch (type.kindPType())
		{
			case SEQ:
				SSeqType seqType = (SSeqType) type;
				if(seqType.getSeqof().kindPType()==EType.BASIC)
				{
					return true;
				}
				break;
			case SET:
				ASetType setType = (ASetType) type;
				if(setType.getSetof().kindPType()==EType.BASIC)
				{
					return true;
				}
				break;
			case BASIC:
				return true;
			
		}
		return validMapType(type);
	}

	public static Type getReferenceClass(PType type, Map<String, Class> classes)
	{
		if(type instanceof AOptionalType)
		{
			type = ((AOptionalType) type).getType();
		}
		if (PTypeAssistantInterpreter.isClass(type))
		{
			return getType(classes, type);
		}

		switch (type.kindPType())
		{
			case SBasicType.kindPType:
				return getType(classes, type);
			case ABracketType.kindPType:
				break;
			case AClassType.kindPType:
				break;
			case AFunctionType.kindPType:
				break;
			case SInvariantType.kindPType:
				if (type instanceof ANamedInvariantType)
				{
					ANamedInvariantType nInvType = (ANamedInvariantType) type;
					return getType(classes, nInvType);
				}
				break;
			case SMapType.kindPType:
				SMapType mType = (SMapType) type;
				return getTypeForMap(classes, mType.getTo());
			case AOperationType.kindPType:
				break;
			case AOptionalType.kindPType:
//				AOptionalType optionalType = (AOptionalType) type;
//				return getType(classes, optionalType.getType());
				break;
			case AParameterType.kindPType:
				break;
			case AProductType.kindPType:
				break;
			case AQuoteType.kindPType:
				break;
			case SSeqType.kindPType:
				SSeqType seqType = (SSeqType) type;
				return getType(classes, seqType.getSeqof());
			case ASetType.kindPType:
				ASetType setType = (ASetType) type;
				return getType(classes, setType.getSetof());
			case AUndefinedType.kindPType:
				break;
			case AUnionType.kindPType:
				break;
			case AUnknownType.kindPType:
				break;
			case AUnresolvedType.kindPType:
				break;
			case AVoidType.kindPType:
				break;
			case AVoidReturnType.kindPType:
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
		if(SSeqType.kindPType.equals(type.kindPType()))
		{
			type = ((SSeqType)type).getSeqof();
		}else if (ASetType.kindPType.equals(type.kindPType()))
		{
			type =((ASetType)type).getSetof();
		}
		return getType(classes, UmlTypeCreatorBase.getName(type));
	}

	public static void createAssociation(String name, PType defType,AAccessSpecifierAccessSpecifier access,PExp defaultExp ,Map<String, Class> classes, Class class_, boolean readOnly, UmlTypeCreator utc)
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
		prop.setIsOrdered(SSeqType.kindPType.equals(defType.kindPType()));
		prop.setIsUnique( ! (SSeqType.kindPType.equals(defType.kindPType())
							|| SMapType.kindPType.equals(defType.kindPType())));
		
		//set qualifier if map
		if (SMapType.kindPType.equals(defType.kindPType()))
		{
			SMapType mType = (SMapType) defType;
			PType fromType = mType.getFrom();
			PType toType = mType.getTo();
			
			Property qualifier = prop.createQualifier(null, Vdm2UmlAssociationUtil.getQualifierReferenceClass(class_,fromType, classes,utc));
			qualifier.setLower(Vdm2UmlUtil.extractLower(fromType));
			qualifier.setUpper(Vdm2UmlUtil.extractUpper(fromType));
			//set ordered
			qualifier.setIsOrdered(SSeqType.kindPType.equals(fromType.kindPType()));
			qualifier.setIsUnique( ! (SSeqType.kindPType.equals(fromType.kindPType())
									 || SMapType.kindPType.equals(fromType.kindPType())));
			
			prop.setLower(Vdm2UmlUtil.extractLower(toType));
			prop.setUpper(Vdm2UmlUtil.extractUpper(toType));
			//set ordered
			prop.setIsOrdered(SSeqType.kindPType.equals(toType.kindPType()));
			prop.setIsUnique( ! (SSeqType.kindPType.equals(toType.kindPType())
								|| SMapType.kindPType.equals(toType.kindPType())));
			
			//Map unique
			prop.setIsUnique(AInMapMapType.kindSMapType.equals(mType.kindSMapType()));
			Property targetProp = association.getMemberEnd("", null);
			targetProp.setIsUnique(true);
			
		}
	}
	
	private static Type getQualifierReferenceClass(Class class_, PType type, Map<String, Class> classes, UmlTypeCreator utc)
	{
		PType qualifierType =unfoldSetSeqTypes(type);
		if(qualifierType.kindPType()==EType.BASIC)
		{
			utc.create(class_, qualifierType);
			return utc.getUmlType(qualifierType);
		}
		return getReferenceClass(qualifierType, classes);
	}
	
	private static PType unfoldSetSeqTypes(PType type)
	{
		switch(type.kindPType())
		{
			case SEQ:
				return ((SSeqType)type).getSeqof();
			case SET:
				return ((ASetType)type).getSetof();
		}
		return type;
	}

	// public static Class getClassName(PType defType,Map<String, Class> classes)
	// {
	// switch (defType.kindPType())
	// {
	// case AClassType.kindPType:
	// return classes.get(((AClassType) defType).getName().name);
	// case AOptionalType.kindPType:
	// return getClassName(((AOptionalType) defType).getType(),classes);
	// default:
	// break;
	// }
	//
	// return null;
	// }
}
