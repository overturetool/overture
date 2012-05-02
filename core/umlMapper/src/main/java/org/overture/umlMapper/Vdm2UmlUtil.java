package org.overture.umlMapper;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.EDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.assistants.PAccessSpecifierAssistantTC;
import org.overture.ast.definitions.assistants.PDefinitionAssistantTC;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.ARealLiteralExp;
import org.overture.ast.expressions.AStringLiteralExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.EStm;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AInMapMapType;
import org.overture.ast.types.AMapMapType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.ASeq1SeqType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SBasicType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SNumericBasicType;
import org.overture.ast.types.SSeqType;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overturetool.umltrans.uml.IUmlBoolType;
import org.overturetool.umltrans.uml.IUmlCharType;
import org.overturetool.umltrans.uml.IUmlClassNameType;
import org.overturetool.umltrans.uml.IUmlIntegerType;
import org.overturetool.umltrans.uml.IUmlMultiplicityElement;
import org.overturetool.umltrans.uml.IUmlProperty;
import org.overturetool.umltrans.uml.IUmlType;
import org.overturetool.umltrans.uml.IUmlValueSpecification;
import org.overturetool.umltrans.uml.IUmlVisibilityKind;
import org.overturetool.umltrans.uml.UmlBoolType;
import org.overturetool.umltrans.uml.UmlCharType;
import org.overturetool.umltrans.uml.UmlClassNameType;
import org.overturetool.umltrans.uml.UmlIntegerType;
import org.overturetool.umltrans.uml.UmlLiteralInteger;
import org.overturetool.umltrans.uml.UmlLiteralString;
import org.overturetool.umltrans.uml.UmlMultiplicityElement;
import org.overturetool.umltrans.uml.UmlProperty;
import org.overturetool.umltrans.uml.UmlUnlimitedNatural;
import org.overturetool.umltrans.uml.UmlValueSpecification;
import org.overturetool.umltrans.uml.UmlVisibilityKind;
import org.overturetool.umltrans.uml.UmlVisibilityKindQuotes;
import org.overturetool.umltrans.uml.UmlVoidType;
import org.overturetool.vdmj.lex.LexNameToken;

public class Vdm2UmlUtil {
	
	public static IUmlVisibilityKind convertAccessSpecifierToVisibility(
			AAccessSpecifierAccessSpecifier accessSpecifier) throws CGException {
		
		if(PAccessSpecifierAssistantTC.isPrivate(accessSpecifier))
		{
			return new UmlVisibilityKind(UmlVisibilityKindQuotes.IQPRIVATE);
		}
		else if(PAccessSpecifierAssistantTC.isProtected(accessSpecifier))
		{
			return new UmlVisibilityKind(UmlVisibilityKindQuotes.IQPROTECTED);
		}
		
		return new UmlVisibilityKind(UmlVisibilityKindQuotes.IQPUBLIC);
		
	}

	public static IUmlMultiplicityElement extractMultiplicity(PType type) throws CGException {
		Boolean isOrdered = false;
		Boolean isUnique = true;
		Long lower = (long) 1;
		Long upper = (long) 1;
		
		if(PTypeAssistant.isType(type, ASetType.class))
		{
			upper = null;
			lower = (long) 0;
			isOrdered = false;
		}
		else if(PTypeAssistant.isType(type, ASeqSeqType.class))
		{
			lower = (long) 0;
			upper = null;
			isOrdered = true;
			isUnique = false;
		}
		else if(PTypeAssistant.isType(type, ASeq1SeqType.class))
		{
			lower = (long) 1;
			upper = null;
			isOrdered = true;
			isUnique = false;
		}
		else if(PTypeAssistant.isType(type, SMapType.class))
		{
			isOrdered = true;
			upper = null;
			lower = (long) 0;
			isUnique = false;
		}
		else if(PTypeAssistant.isType(type, AOptionalType.class))
		{
			upper = (long) 1;
			lower = (long) 0;
		}
		
		return new UmlMultiplicityElement(isOrdered, isUnique, lower, upper);
	}

	public static IUmlType convertType(PType type) throws CGException {
		switch (type.kindPType()) {
		case BASIC:
			return convertBasicType((SBasicType) type);
		case BRACKET:
			return convertType(PTypeAssistant.deBracket(type));
		case MAP:
			return convertType(((SMapType) type).getTo());
		case OPTIONAL:
			return convertType(((AOptionalType) type).getType());
		case CLASS:
			return new UmlClassNameType(((AClassType)type).getName().name);
		case SEQ:
			return convertType(((SSeqType) type).getSeqof());
		case SET:
			return convertType(((ASetType) type).getSetof()); 
		case VOID:
			return new UmlVoidType();
		default:
			assert false : "Should not happen?! maybe it should";
			break;
		}
		return null;
	}

	private static IUmlType convertBasicType(SBasicType type) throws CGException {
		switch (type.kindSBasicType()) {
		case BOOLEAN:
			return new UmlBoolType();
		case CHAR:
			return new UmlCharType();
		case NUMERIC:
			return convertNumericType((SNumericBasicType) type);
		case TOKEN:
			return new UmlIntegerType();
		default:
			assert false : "Should not happen";
			break;
		}
		return null;
	}

	private static IUmlType convertNumericType(SNumericBasicType type) throws CGException {
		switch (type.kindSNumericBasicType()) {
		case INT:
		case NAT:
		case NATONE:
		case RATIONAL:
			return new UmlIntegerType();
		case REAL:
			//TODO: Unlimited natural?!? seems weird
			return new UmlUnlimitedNatural();
		default:
			assert false : "Should not happen";
			break;
		}
		return null;
	}

	public static Vector<IUmlClassNameType> getSuperClasses(SClassDefinition sClass) throws CGException {
		Vector<IUmlClassNameType> result = new Vector<IUmlClassNameType>();
		List<LexNameToken> superNames = sClass.getSupernames();
		
		for (LexNameToken superName : superNames) {
			result.add(new UmlClassNameType(superName.name));
		}
		
		return result;
	}
	
	public static boolean isClassActive(SClassDefinition sClass) {
		
		for (PDefinition def : sClass.getDefinitions()) {
			if(def.kindPDefinition() == EDefinition.THREAD)
				return true;
		}
		return false;
	}
	
	public static boolean hasSubclassResponsabilityDefinition(
			LinkedList<PDefinition> definitions) {
		
		for (PDefinition pDefinition : definitions) {
			if(isSubclassResponsability(pDefinition))
				return true;
		}
		
		return false;
	}
	
	private static boolean isSubclassResponsability(PDefinition pDefinition) {
		
		if(PDefinitionAssistantTC.isOperation(pDefinition))
		{
			if(pDefinition instanceof AExplicitOperationDefinition)
			{
				if(((AExplicitOperationDefinition)pDefinition).getBody().kindPStm() == EStm.SUBCLASSRESPONSIBILITY)
				{
					return true;
				}
			}
			else if(pDefinition instanceof AImplicitOperationDefinition)
			{				
				PStm body = ((AImplicitOperationDefinition)pDefinition).getBody();
				//implicit operations may or may not have a body
				if(body == null)
				{
					return true;
				}
				else
				{
					if(body.kindPStm() == EStm.SUBCLASSRESPONSIBILITY)
					{
						return true;
					}
				}
			}
		}
		
		return false;
	}

	public static IUmlType convertPropertyType(PType type, String owner) throws CGException {
		IUmlType result = convertType(type);
		
		return result == null ? new UmlClassNameType(owner) : result;
		
	}

	public static IUmlValueSpecification getDefaultValue(PExp expression) throws CGException {
		IUmlValueSpecification result = null;
		
		switch (expression.kindPExp()) {
		case INTLITERAL:
			AIntLiteralExp intLiteral = (AIntLiteralExp) expression;
			result = new UmlLiteralInteger(intLiteral.getValue().value);
			break;
		case REALLITERAL:
			ARealLiteralExp realLiteral = (ARealLiteralExp) expression;
			result = new UmlLiteralInteger((long)realLiteral.getValue().value);
			break;
		case STRINGLITERAL:
			AStringLiteralExp stringLiteral = (AStringLiteralExp) expression;
			result = new UmlLiteralString(stringLiteral.getValue().value);
		default:
			break;
		}
		
		return result;
	}

	public static boolean isSimpleType(PType type) {
		switch (type.kindPType()) {

		case BRACKET:
			return isSimpleType(((ABracketType) type).getType());
		case CLASS:
		case MAP:
		case PRODUCT:
		case UNION:
			return false;
		case OPTIONAL:
			return isSimpleType(((AOptionalType)type).getType());
		case SEQ:
			return isSimpleType(((SSeqType) type).getSeqof());
		case SET:
			return isSimpleType(((ASetType) type).getSetof());
		default:
			break;
		}
		
		return true;
	}

	public static IUmlType getQualifier(PType defType) throws CGException {
		
		if(PTypeAssistant.isType(defType, SMapType.class))
		{
			return convertType(((SMapType) defType).getFrom());
		}
		
		return null;
	}

	public static String getSimpleTypeName(IUmlType type) {
		
		if(type instanceof IUmlBoolType)
		{
			return "bool";
		}
		else if(type instanceof IUmlIntegerType)
		{
			return "int";
		}
		else if(type instanceof IUmlCharType)
		{
			return "char";
		}
		
		return "String";
		
	}

	public static UmlProperty cloneProperty(IUmlProperty property) throws CGException {
		return new UmlProperty(
				property.getName(), 
				property.getVisibility(), 
				property.getMultiplicity(),
				property.getType(), 
				property.getIsReadOnly(),
				property.getDefault(),
				property.getIsComposite(),
				property.getIsDerived(),
				property.getIsStatic(),
				property.getOwnerClass(),
				property.getQualifier());
	}
}
