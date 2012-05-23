package org.overture.ide.plugins.uml2.vdm2uml;

import java.util.LinkedList;
import java.util.Map;

import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.VisibilityKind;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.EDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.assistants.PAccessSpecifierAssistantTC;
import org.overture.ast.definitions.assistants.PDefinitionAssistantTC;
import org.overture.ast.statements.EStm;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.ASeq1SeqType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.EBasicType;
import org.overture.ast.types.EType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SBasicType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.ast.types.assistants.PTypeAssistantTC;

public class Vdm2UmlUtil {
	
	public static VisibilityKind convertAccessSpecifierToVisibility(
			AAccessSpecifierAccessSpecifier accessSpecifier)  {
		
		if(PAccessSpecifierAssistantTC.isPrivate(accessSpecifier))
		{
			return VisibilityKind.PRIVATE_LITERAL;
		}
		else if(PAccessSpecifierAssistantTC.isProtected(accessSpecifier))
		{
			return VisibilityKind.PROTECTED_LITERAL;
		}
		
		return VisibilityKind.PUBLIC_LITERAL;
		
	}

	public static int extractUpper(PType type)  {
		
		int upper =  1;
		
		if(PTypeAssistantTC.isType(type, ASetType.class))
		{
			upper = LiteralUnlimitedNatural.UNLIMITED;
		}
		else if(PTypeAssistantTC.isType(type, ASeqSeqType.class))
		{
			upper = LiteralUnlimitedNatural.UNLIMITED;
		}
		else if(PTypeAssistantTC.isType(type, ASeq1SeqType.class))
		{
			upper = LiteralUnlimitedNatural.UNLIMITED;
		}
		else if(PTypeAssistantTC.isType(type, SMapType.class))
		{
			upper = LiteralUnlimitedNatural.UNLIMITED;
		}
		else if(PTypeAssistantTC.isType(type, AOptionalType.class))
		{
			
		}
		
		return upper;
	}
	
	public static int extractLower(PType type) {
		int lower = 0;
		
		if(PTypeAssistantTC.isType(type, ASetType.class))
		{
		}
		else if(PTypeAssistantTC.isType(type, ASeqSeqType.class))
		{
		}
		else if(PTypeAssistantTC.isType(type, ASeq1SeqType.class))
		{
			lower = 1;
		}
		else if(PTypeAssistantTC.isType(type, SMapType.class))
		{
		
		}
		else if(PTypeAssistantTC.isType(type, AOptionalType.class))
		{
			
		}
		
		return lower;
	}
	
	public static boolean extractIsOrdered(PType type)  {
		Boolean isOrdered = false;
		
		if(PTypeAssistantTC.isType(type, ASetType.class))
		{
			isOrdered = false;
		}
		else if(PTypeAssistantTC.isType(type, ASeqSeqType.class))
		{
			isOrdered = true;
		}
		else if(PTypeAssistantTC.isType(type, ASeq1SeqType.class))
		{
			isOrdered = true;
		}
		else if(PTypeAssistantTC.isType(type, SMapType.class))
		{
			isOrdered = true;
		}
		else if(PTypeAssistantTC.isType(type, AOptionalType.class))
		{
			
		}
		
		return isOrdered;
	}
	
	public static boolean extractIsUnique(PType type)  {
		Boolean isUnique = true;
		
		if(PTypeAssistantTC.isType(type, ASetType.class))
		{
		}
		else if(PTypeAssistantTC.isType(type, ASeqSeqType.class))
		{
			isUnique = false;
		}
		else if(PTypeAssistantTC.isType(type, ASeq1SeqType.class))
		{
			isUnique = false;
		}
		else if(PTypeAssistantTC.isType(type, SMapType.class))
		{
			isUnique = false;
		}
		else if(PTypeAssistantTC.isType(type, AOptionalType.class))
		{
		}
		
		return isUnique;
	}
	
	

//	public static Type convertType(PType type) {
//		switch (type.kindPType()) {
//		case BASIC:
//			return convertBasicType((SBasicType) type);
//		case BRACKET:
//			return convertType(PTypeAssistantTC.deBracket(type));
//		case MAP:
//			return convertType(((SMapType) type).getTo());
//		case OPTIONAL:
//			return convertType(((AOptionalType) type).getType());
//		case CLASS:
//			return new UmlClassNameType(((AClassType)type).getName().name);
//		case SEQ:
//			return convertType(((SSeqType) type).getSeqof());
//		case SET:
//			return convertType(((ASetType) type).getSetof()); 
//		case VOID:
//			return new UmlVoidType();
//		default:
//			assert false : "Should not happen?! maybe it should";
//			break;
//		}
//		return null;
//	}
//
//	private static IUmlType convertBasicType(SBasicType type) throws CGException {
//		switch (type.kindSBasicType()) {
//		case BOOLEAN:
//			PrimitiveType primitiveType = model.createOwnedPrimitiveType(name);
//		case CHAR:
//			return new UmlCharType();
//		case NUMERIC:
//			return convertNumericType((SNumericBasicType) type);
//		case TOKEN:
//			return new UmlIntegerType();
//		default:
//			assert false : "Should not happen";
//			break;
//		}
//		return null;
//	}
//
//	private static IUmlType convertNumericType(SNumericBasicType type) throws CGException {
//		switch (type.kindSNumericBasicType()) {
//		case INT:
//		case NAT:
//		case NATONE:
//		case RATIONAL:
//			return new UmlIntegerType();
//		case REAL:
//			//TODO: Unlimited natural?!? seems weird
//			return new UmlUnlimitedNatural();
//		default:
//			assert false : "Should not happen";
//			break;
//		}
//		return null;
//	}
//
//	public static Vector<IUmlClassNameType> getSuperClasses(SClassDefinition sClass) throws CGException {
//		Vector<IUmlClassNameType> result = new Vector<IUmlClassNameType>();
//		List<LexNameToken> superNames = sClass.getSupernames();
//		
//		for (LexNameToken superName : superNames) {
//			result.add(new UmlClassNameType(superName.name));
//		}
//		
//		return result;
//	}
	
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

//	public static IUmlType convertPropertyType(PType type, String owner) throws CGException {
//		IUmlType result = convertType(type);
//		
//		return result == null ? new UmlClassNameType(owner) : result;
//		
//	}

//	public static EAttribute getDefaultValue(PExp expression) {
//		
//		EAttribute value = null;
//		// UmlProperty.default
//		switch (expression.kindPExp()) {
////		case NIL:
////			result = new UmlLiteralString("nil");
////			break;
////		case CHARLITERAL:
////			ACharLiteralExp charLiteral = (ACharLiteralExp) expression;
////			result = new UmlLiteralString(new Character(charLiteral.getValue().unicode).toString());
////			break;
////		case BOOLEANCONST:
////			ABooleanConstExp boolLiteral = (ABooleanConstExp) expression;
////			result = new UmlLiteralString( new Boolean(boolLiteral.getValue().value).toString());
////			break;
//		case INTLITERAL:
//			AIntLiteralExp intLiteral = (AIntLiteralExp) expression;
//			value = UMLPackage.eINSTANCE.getLiteralInteger_Value();
//			value.setDefaultValue(intLiteral.getValue().value);
//			return value;
//			break;
////		case REALLITERAL:
////			ARealLiteralExp realLiteral = (ARealLiteralExp) expression;
////			result = new UmlLiteralInteger((long)realLiteral.getValue().value);
////			break;
//		case STRINGLITERAL:
//			AStringLiteralExp stringLiteral = (AStringLiteralExp) expression;
//			result = new UmlLiteralString(stringLiteral.getValue().value);
//		default:
//			System.out.println("Not supported value: " + expression.toString() + " " + expression.getLocation().toString() );
//			break;
//		}
//		
//		return result;
//	}
//
//	public static boolean isSimpleType(PType type) {
//		switch (type.kindPType()) {
//
//		case BRACKET:
//			return isSimpleType(((ABracketType) type).getType());
//		case CLASS:
//		case MAP:
//		case PRODUCT:
//		case UNION:
//			return false;
//		case OPTIONAL:
//			return isSimpleType(((AOptionalType)type).getType());
//		case SEQ:
//			return isSimpleType(((SSeqType) type).getSeqof());
//		case SET:
//			return isSimpleType(((ASetType) type).getSetof());
//		default:
//			break;
//		}
//		
//		return true;
//	}
//
//	public static IUmlType getQualifier(PType defType) throws CGException {
//		
//		if(PTypeAssistantTC.isType(defType, SMapType.class))
//		{
//			return convertType(((SMapType) defType).getFrom());
//		}
//		
//		return null;
//	}
//
//	public static String getSimpleTypeName(IUmlType type) {
//		
//		if(type instanceof IUmlBoolType)
//		{
//			return "bool";
//		}
//		else if(type instanceof IUmlIntegerType)
//		{
//			return "int";
//		}
//		else if(type instanceof IUmlCharType)
//		{
//			return "char";
//		}
//		
//		return "String";
//		
//	}
//
//	public static UmlProperty cloneProperty(IUmlProperty property) throws CGException {
//		return new UmlProperty(
//				property.getName(), 
//				property.getVisibility(), 
//				property.getMultiplicity(),
//				property.getType(), 
//				property.getIsReadOnly(),
//				property.getDefault(),
//				property.getIsComposite(),
//				property.getIsDerived(),
//				property.getIsStatic(),
//				property.getOwnerClass(),
//				property.getQualifier());
//	}
//
//	public static Vector<IUmlParameter> buildParameters(
//			AExplicitOperationDefinition pDefinition, PType pType) throws CGException {
//		
//		Vector<IUmlParameter> parameters = new Vector<IUmlParameter>();
//		AOperationType opType = (AOperationType) pType;		
//		List<PType> paramTypes = opType.getParameters();
//		int i = 0;
//		for (PPattern parameter : pDefinition.getParameterPatterns()) {
//			String name = "-";
//			if(parameter.kindPPattern() == EPattern.IDENTIFIER)
//			{
//				name = ((AIdentifierPattern) parameter).getName().name;
//			}
//			
//			PType paramType = paramTypes.get(i++);
//			parameters.add(new UmlParameter(
//					name,
//					Vdm2UmlUtil.convertType(paramType),//TODO: missing type
//					Vdm2UmlUtil.extractMultiplicity(paramType),
//					"", 
//					new UmlParameterDirectionKind(UmlParameterDirectionKindQuotes.IQIN)
//					));
//			
//		}
//		
//		IUmlParameter returnType = new UmlParameter("return", 
//				Vdm2UmlUtil.convertType(opType.getResult()), 
//				Vdm2UmlUtil.extractMultiplicity(opType.getResult()),
//				"", 
//				new UmlParameterDirectionKind(UmlParameterDirectionKindQuotes.IQRETURN));
//		
//		parameters.add(returnType);
//		return parameters;
//		
//	}
//
//	public static Vector<IUmlParameter> buildParameters(
//			LinkedList<APatternListTypePair> patternTypePairs) throws CGException {
//		
//		Vector<IUmlParameter> result = new Vector<IUmlParameter>();
//		
//		for (APatternListTypePair aPair : patternTypePairs) {
//			LinkedList<PPattern> patterns = aPair.getPatterns();
//			PType type = aPair.getType();
//			
//			for (PPattern aPattern : patterns) {
//				String name = "-";
//				
//				if(aPattern.kindPPattern() == EPattern.IDENTIFIER)
//				{
//					name = ((AIdentifierPattern)aPattern).getName().name;
//				}
//				result.add(new UmlParameter(name, 
//						Vdm2UmlUtil.convertType(type),
//						Vdm2UmlUtil.extractMultiplicity(type),
//						"",
//						new UmlParameterDirectionKind(UmlParameterDirectionKindQuotes.IQIN)));
//			}
//		}
//		
//		return result;
//		
//	}
//
//	public static Vector<IUmlParameter> buildFnResult(APatternTypePair result) throws CGException {
//		
//		//TODO
//		Vector<IUmlParameter> parameters = new Vector<IUmlParameter>();
//		
//		IUmlParameter returnType = new UmlParameter("return", 
//				new UmlBoolType(),//TODO: missing type
//				new UmlMultiplicityElement(true, true,(long)0, (long)0),//TODO: missing multiplicity
//				"", 
//				new UmlParameterDirectionKind(UmlParameterDirectionKindQuotes.IQRETURN));
//		
//		parameters.add(returnType);
//		
//		return parameters;
//	}
//
//	public static Vector<IUmlParameter> buildParameters(List<PPattern> first,
//			AFunctionType funcType) throws CGException {
//		Vector<IUmlParameter> result = new Vector<IUmlParameter>();
//		
//		for (PPattern aPattern : first) {
//			String name = "-";
//			
//			if(aPattern.kindPPattern() == EPattern.IDENTIFIER)
//			{
//				name = ((AIdentifierPattern)aPattern).getName().name;
//			}
//			result.add(new UmlParameter(name, 
//					new UmlBoolType(),//TODO: missing type
//					new UmlMultiplicityElement(true, true,(long)0, (long)0),//TODO: missing multiplicity
//					"",
//					new UmlParameterDirectionKind(UmlParameterDirectionKindQuotes.IQIN)));
//		}
//		
//		return result;
//	}

	public static boolean hasPolymorphic(AExplicitFunctionDefinition pDefinition) {

		AFunctionType funcType = (AFunctionType) PDefinitionAssistantTC.getType(pDefinition);
		
		
		for (PType t : funcType.getParameters()) {
			if(PTypeAssistantTC.isType(t, AParameterType.class))
			{
				return true;
			}			
		}
		
		if(PTypeAssistantTC.isType(funcType.getResult(), AParameterType.class))
		{
			return true;
		}
		
		return false;
	}


	

	private static void convertTypeSeq(Model model, SSeqType definitionType) {
		if(definitionType.getSeqof().kindPType() == EType.BASIC)
		{
			if(((SBasicType) definitionType.getSeqof()).kindSBasicType() == EBasicType.CHAR)
			{
				model.createOwnedPrimitiveType("String");
			}
		}
		
	}

	
	
}
