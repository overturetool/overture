package org.overture.umlMapper;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.APatternTypePair;
import org.overture.ast.patterns.EPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.definition.PAccessSpecifierAssistantTC;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;
import org.overturetool.umltrans.uml.IUmlAssociation;
import org.overturetool.umltrans.uml.IUmlClass;
import org.overturetool.umltrans.uml.IUmlClassNameType;
import org.overturetool.umltrans.uml.IUmlConstraint;
import org.overturetool.umltrans.uml.IUmlDefinitionBlock;
import org.overturetool.umltrans.uml.IUmlModel;
import org.overturetool.umltrans.uml.IUmlMultiplicityElement;
import org.overturetool.umltrans.uml.IUmlNode;
import org.overturetool.umltrans.uml.IUmlOperation;
import org.overturetool.umltrans.uml.IUmlParameter;
import org.overturetool.umltrans.uml.IUmlProperty;
import org.overturetool.umltrans.uml.IUmlTemplateSignature;
import org.overturetool.umltrans.uml.IUmlType;
import org.overturetool.umltrans.uml.IUmlValueSpecification;
import org.overturetool.umltrans.uml.IUmlVisibilityKind;
import org.overturetool.umltrans.uml.UmlAssociation;
import org.overturetool.umltrans.uml.UmlBoolType;
import org.overturetool.umltrans.uml.UmlClass;
import org.overturetool.umltrans.uml.UmlClassNameType;
import org.overturetool.umltrans.uml.UmlConstraint;
import org.overturetool.umltrans.uml.UmlIntegerType;
import org.overturetool.umltrans.uml.UmlLiteralString;
import org.overturetool.umltrans.uml.UmlModel;
import org.overturetool.umltrans.uml.UmlMultiplicityElement;
import org.overturetool.umltrans.uml.UmlNestedClassifiers;
import org.overturetool.umltrans.uml.UmlOperation;
import org.overturetool.umltrans.uml.UmlOwnedOperations;
import org.overturetool.umltrans.uml.UmlOwnedProperties;
import org.overturetool.umltrans.uml.UmlParameter;
import org.overturetool.umltrans.uml.UmlParameterDirectionKind;
import org.overturetool.umltrans.uml.UmlParameterDirectionKindQuotes;
import org.overturetool.umltrans.uml.UmlParameters;
import org.overturetool.umltrans.uml.UmlProperty;
import org.overturetool.umltrans.uml.UmlVisibilityKind;
import org.overturetool.umltrans.uml.UmlVisibilityKindQuotes;

public class Vdm2Uml {

	//private StatusLog log = null;	
	private Vector<String> filteredClassNames = new Vector<String>();
	private Set<IUmlClass> nestedClasses = new HashSet<IUmlClass>();
	private Set<IUmlAssociation> associations = new HashSet<IUmlAssociation>();
	private Set<IUmlConstraint> constraints = new HashSet<IUmlConstraint>();
	private int runningId = 0;
	
	public Vdm2Uml() {
		
		
	}
	
	private String getNextId() {
		return Integer.toString(runningId++);
	}
	
	public IUmlModel init(List<SClassDefinition> classes)
	{
		UmlModel model = null;
		
		try {
			model = buildUml(classes);
			@SuppressWarnings("rawtypes")
			HashSet definitions = model.getDefinitions();
			definitions.addAll(associations);
			definitions.addAll(constraints);
			model.setDefinitions(definitions);
		} catch (CGException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return model;
	}

	private UmlModel buildUml(List<SClassDefinition> classes) throws CGException {
		
		HashSet<IUmlNode> umlClasses = new HashSet<IUmlNode>();
		
		for (SClassDefinition sClass : classes) {
			
			if(!filteredClassNames.contains(sClass.getName().name))
			{
				umlClasses.add(buildClass(sClass));	
			}
			
		}
		
		umlClasses.addAll(nestedClasses);
		return new UmlModel("Root", umlClasses);
	}

	private IUmlClass buildClass(SClassDefinition sClass) throws CGException {
		
		String name = sClass.getName().name;
		//TODO: log - log.addNewClassInfo(name);
		
		boolean isStatic = false;
		boolean isActive = Vdm2UmlUtil.isClassActive(sClass);
		boolean isAbstract = Vdm2UmlUtil.hasSubclassResponsabilityDefinition(sClass.getDefinitions());
		Vector<IUmlDefinitionBlock> dBlocksSeq = buildDefinitionBlocks(sClass.getDefinitions(),name);
		HashSet<IUmlDefinitionBlock> dBlocks = new HashSet<IUmlDefinitionBlock>(dBlocksSeq);
		Vector<IUmlClassNameType> supers = Vdm2UmlUtil.getSuperClasses(sClass);
		IUmlVisibilityKind visibility = new UmlVisibilityKind(UmlVisibilityKindQuotes.IQPUBLIC);
		
		return new UmlClass(name,dBlocks,isAbstract,supers,visibility,isStatic,isActive,null);
	}

	

	private Vector<IUmlDefinitionBlock> buildDefinitionBlocks(
			LinkedList<PDefinition> definitions, String owner) throws CGException {

		Vector<IUmlDefinitionBlock> result = new Vector<IUmlDefinitionBlock>();
		
		HashSet<IUmlProperty> instanceVariables = new HashSet<IUmlProperty>();
		HashSet<IUmlProperty> valueDefinitions = new HashSet<IUmlProperty>();
		HashSet<IUmlType> typeDefinitions = new HashSet<IUmlType>();
		HashSet<IUmlOperation> operationDefinitions = new HashSet<IUmlOperation>();
		HashSet<IUmlOperation> functionDefinitions = new HashSet<IUmlOperation>();
		
		for (PDefinition pDefinition : definitions) {
			switch (pDefinition.kindPDefinition()) {
			case EXPLICITFUNCTION:
				if(!Vdm2UmlUtil.hasPolymorphic((AExplicitFunctionDefinition) pDefinition))
				{
					functionDefinitions.add(buildDefinitionBlock((AExplicitFunctionDefinition) pDefinition));	
				}
				
				break;
			case IMPLICITFUNCTION:
				break;
			case EXPLICITOPERATION:				
				operationDefinitions.add(buildDefinitionBlock((AExplicitOperationDefinition)pDefinition));
				break;
			case IMPLICITOPERATION:
				operationDefinitions.add(buildDefinitionBlock((AImplicitOperationDefinition)pDefinition));
				break;
			case INSTANCEVARIABLE:
				IUmlProperty instVar = buildDefinitionBlock((AInstanceVariableDefinition)pDefinition,owner);
				if(instVar != null)
				{
					instanceVariables.add(instVar);	
				}
				break;
			case TYPE:
				typeDefinitions.add(buildDefinitionBlock((ATypeDefinition)pDefinition));
				break;
			case VALUE:
				IUmlProperty valueDef = buildDefinitionBlock((AValueDefinition)pDefinition, owner);
				if(valueDef != null)
				{
					valueDefinitions.add(valueDef);	
				}
				break;
			default:
				break;
			}
		}
		
		result.add(new UmlOwnedProperties(instanceVariables));
		result.add(new UmlOwnedProperties(valueDefinitions));
		result.add(new UmlNestedClassifiers(typeDefinitions));
		result.add(new UmlOwnedOperations(operationDefinitions));
		result.add(new UmlOwnedOperations(functionDefinitions));
		return result;
	}

	private IUmlOperation buildDefinitionBlock(
			AExplicitFunctionDefinition pDefinition) throws CGException {
		
		String name = pDefinition.getName().name;
		AAccessSpecifierAccessSpecifier access = pDefinition.getAccess();
		IUmlVisibilityKind visibility = Vdm2UmlUtil.convertAccessSpecifierToVisibility(access);
		IUmlMultiplicityElement multiplicity = new UmlMultiplicityElement(false, false, (long)1,(long)1);
		IUmlType type = Vdm2UmlUtil.convertType(PDefinitionAssistantTC.getType(pDefinition));
		boolean isStatic = PAccessSpecifierAssistantTC.isStatic(access);
		
		AFunctionType funcType = (AFunctionType) PDefinitionAssistantTC.getType(pDefinition);
		
		LinkedList<List<PPattern>> pnames = pDefinition.getParamPatternList();
		
		Vector<IUmlParameter> params = null;
		if(pDefinition.getPostcondition() == null)
		{
			if(pnames.size() > 0)
			{
				params = Vdm2UmlUtil.buildParameters(pnames.getFirst(),funcType);
			}
		}
		else
		{
			//TODO: function with post condition == extendedexplicit in OML
			params = new Vector<IUmlParameter>();
			
			IUmlParameter returnType = new UmlParameter("return", 
					new UmlBoolType(),//TODO: missing type
					new UmlMultiplicityElement(true, true,(long)0, (long)0),//TODO: missing multiplicity
					"", 
					new UmlParameterDirectionKind(UmlParameterDirectionKindQuotes.IQRETURN));
			params.add(returnType);
		}
		
		
				
		
		return new UmlOperation(name, 
                visibility, 
                multiplicity, 
                true,
                type, 
                isStatic,
                new UmlParameters(params));
	}

	private IUmlOperation buildDefinitionBlock(
			AImplicitOperationDefinition pDefinition) throws CGException {
		
		String name = pDefinition.getName().name;
		AAccessSpecifierAccessSpecifier access = pDefinition.getAccess();
		IUmlVisibilityKind visibility = Vdm2UmlUtil.convertAccessSpecifierToVisibility(access);
		IUmlMultiplicityElement multiplicity = new UmlMultiplicityElement(false, false, (long)1,(long)1);
		IUmlType type = null;
		boolean isStatic = PAccessSpecifierAssistantTC.isStatic(access);
		
		
		LinkedList<APatternListTypePair> patternTypePairs = pDefinition.getParameterPatterns();
		
		APatternTypePair result = pDefinition.getResult();
		
		Vector<IUmlParameter> params = Vdm2UmlUtil.buildParameters(patternTypePairs);
		Vector<IUmlParameter> results = Vdm2UmlUtil.buildFnResult(result);
		params.addAll(results);
		
		return new UmlOperation(name,visibility,multiplicity,false,type,isStatic, new UmlParameters(params));
	}

	private IUmlOperation buildDefinitionBlock(
			AExplicitOperationDefinition pDefinition) throws CGException {
		
		String name = pDefinition.getName().name;
		AAccessSpecifierAccessSpecifier access = pDefinition.getAccess();
		IUmlVisibilityKind visibility = Vdm2UmlUtil.convertAccessSpecifierToVisibility(access);
		IUmlMultiplicityElement multiplicity = new UmlMultiplicityElement(false, false, (long)1,(long)1);
		IUmlType type = null;
		boolean isStatic = PAccessSpecifierAssistantTC.isStatic(access);
		//TODO: I dont get the parameters right now
		Vector<IUmlParameter> params = null;
		if(pDefinition.getPostcondition() == null)
		{
			params = Vdm2UmlUtil.buildParameters(pDefinition,PDefinitionAssistantTC.getType(pDefinition));	
		}
		else
		{
			//TODO:operation with post condition == extendedexplicit in OML
			params = Vdm2UmlUtil.buildParameters(pDefinition,PDefinitionAssistantTC.getType(pDefinition));
			
		}
		
		return new UmlOperation(name, visibility, multiplicity, false, type, isStatic , new UmlParameters(params));
		
	}

	private IUmlType buildDefinitionBlock(ATypeDefinition pDefinition) throws CGException {
		
		nestedClasses.add(buildClassFromType(pDefinition));
		
		return new UmlClassNameType(pDefinition.getName().name);
	}

	private IUmlClass buildClassFromType(ATypeDefinition pDefinition) throws CGException {
		PType type = PDefinitionAssistantTC.getType(pDefinition);
		String name = pDefinition.getName().name;
		
		IUmlDefinitionBlock classBody = buildTypeDefinitionBlocks(name,type);
		HashSet<IUmlDefinitionBlock> classBodySet = new HashSet<IUmlDefinitionBlock>(); 
		classBodySet.add(classBody);
		boolean isAbstract = false;
		Vector<IUmlClassNameType> superClasses = new Vector<IUmlClassNameType>();
		IUmlVisibilityKind visibility = Vdm2UmlUtil.convertAccessSpecifierToVisibility(pDefinition.getAccess());
		boolean isStatic = false;
		boolean isActive = false;
		IUmlTemplateSignature templateSignature = null;
		
		return new UmlClass(name, classBodySet, isAbstract, superClasses, visibility, isStatic, isActive, templateSignature);
		
	}

	private IUmlDefinitionBlock buildTypeDefinitionBlocks(String name,
			PType type) throws CGException {
		HashSet<IUmlDefinitionBlock> accumulator = new HashSet<IUmlDefinitionBlock>();
		
		switch (type.kindPType()) {
		case UNION:
			AUnionType unionType = (AUnionType) type;
			for (PType itType : unionType.getTypes()) {
				accumulator.add(buildTypeDefinitionBlocks(name, itType));
			}
			
			HashSet<IUmlProperty> allProperties = new HashSet<IUmlProperty>();
			for (IUmlDefinitionBlock ownedProperties : accumulator) {
				allProperties.addAll(((UmlOwnedProperties)ownedProperties).getPropetityList());
			}
			return new UmlOwnedProperties(allProperties);
			
		case QUOTE:
			AQuoteType quoteType = (AQuoteType) type;
			HashSet<IUmlProperty> props = buildValueFromQuoteType(name,quoteType);
			return new UmlOwnedProperties(props);
		default:
			return new UmlOwnedProperties(new HashSet<IUmlProperty>());
		}
	}

	private HashSet<IUmlProperty> buildValueFromQuoteType(String ownerName,
			AQuoteType quoteType) throws CGException {
		
		String name = quoteType.getValue().value;
		IUmlVisibilityKind visibility = new UmlVisibilityKind(UmlVisibilityKindQuotes.IQPUBLIC);
		IUmlMultiplicityElement multiplicity = new UmlMultiplicityElement(true,false,(long)0, null);
		IUmlType type = new UmlIntegerType();
		boolean isReadOnly = true;
		IUmlValueSpecification defaultValue = null;
		boolean isSimple = Vdm2UmlUtil.isSimpleType(quoteType);
		boolean isDerived = false;
		boolean isStatic = true;
		IUmlType qualifier = null;
		
		IUmlProperty property = new UmlProperty(name, 
				visibility, 
				multiplicity, 
				type,
				isReadOnly,
				defaultValue,
				isSimple,
				isDerived,
				isStatic,
				ownerName, 
				qualifier);
		
		HashSet<IUmlProperty> result = new HashSet<IUmlProperty>();
		result.add(property);
		return result;
	}

	private IUmlProperty buildDefinitionBlock(AValueDefinition value,
			String owner) throws CGException {
		
		AAccessSpecifierAccessSpecifier accessSpecifier = value.getAccess();
		
		if(value.getExpType() == null)
		{
			//TODO: status log log.mappingNotSupported(item);
			return null;
		}
		
		PPattern pattern = value.getPattern();
		
		if(pattern.kindPPattern() == EPattern.IDENTIFIER)
		{
			AIdentifierPattern identPattern = (AIdentifierPattern) pattern;
			
			String name = identPattern.getName().name;
			IUmlVisibilityKind visibility = Vdm2UmlUtil.convertAccessSpecifierToVisibility(accessSpecifier);
			PType valueType = value.getType();
			
			IUmlMultiplicityElement multiplicity = Vdm2UmlUtil.extractMultiplicity(valueType);
			IUmlType type = Vdm2UmlUtil.convertPropertyType(valueType,owner);
			boolean isReadOnly = true;
			IUmlValueSpecification defaultValue = Vdm2UmlUtil.getDefaultValue(value.getExpression());
			boolean isSimple = Vdm2UmlUtil.isSimpleType(valueType);
			boolean isDerived = false;
			boolean isStatic = PAccessSpecifierAssistantTC.isStatic(accessSpecifier);
			IUmlType qualifier = Vdm2UmlUtil.getQualifier(valueType);
			
			IUmlProperty property = new UmlProperty(
					name,
					visibility,
					multiplicity,
					type,
					isReadOnly,
					defaultValue,
					isSimple,
					isDerived,
					isStatic,
					owner,
					qualifier);
			
			if(!isSimple)
			{
				createAssociationFromProperty(property, valueType);
				return null;
			}
			else
			{
				return property;
			}
		}
		else
		{
			//TODO: report warning that value defs without pattern identifiers are not supported
			return null;
		}
		
	}

	private IUmlProperty buildDefinitionBlock(AInstanceVariableDefinition variable,
			String owner) throws CGException {
		PType defType = PDefinitionAssistantTC.getType(variable);
		AAccessSpecifierAccessSpecifier accessSpecifier = variable.getAccess();
		String name = variable.getName().name;
		IUmlVisibilityKind visibility = Vdm2UmlUtil.convertAccessSpecifierToVisibility(accessSpecifier);
		IUmlMultiplicityElement multiplicity = Vdm2UmlUtil.extractMultiplicity(defType);
		IUmlType type = Vdm2UmlUtil.convertPropertyType(defType,owner);
		boolean isReadOnly = false;
		IUmlValueSpecification defaultValue = Vdm2UmlUtil.getDefaultValue(variable.getExpression());
		boolean isSimple = Vdm2UmlUtil.isSimpleType(defType);
		boolean isDerived = false;
		boolean isStatic = PAccessSpecifierAssistantTC.isStatic(accessSpecifier);
		IUmlType qualifier = Vdm2UmlUtil.getQualifier(defType);
		
		//fixme: need to add isUnique
		IUmlProperty result = new UmlProperty(name, visibility , multiplicity, type, isReadOnly, defaultValue , isSimple, isDerived, isStatic, owner, qualifier);
		
		if(!isSimple)
		{
			createAssociationFromProperty(result,defType);
			return null;
		}
		else
		{
			return result;	
		}
		
	}

	private void createAssociationFromProperty(IUmlProperty property,
			PType defType) throws CGException {
		
		switch (defType.kindPType()) {
		case PRODUCT:
			createAssociationFromPropertyProductType(property,defType);
		case UNION:
			createAssociationFromPropertyUnionType(property,defType);
		default:
			createAssociationFromPropertyGeneral(property,defType);
			break;
		}
		
	}

	private void createAssociationFromPropertyUnionType(IUmlProperty property,
			PType defType) throws CGException {
		String name = property.getName();
		UmlProperty prop = Vdm2UmlUtil.cloneProperty(property);
		
		HashSet<IUmlProperty> props = new HashSet<IUmlProperty>();
		props.addAll(createEndProperty(defType,name));
		
		prop.setName("");
		HashSet<IUmlProperty> propSet = new HashSet<IUmlProperty>();
		propSet.add(prop);
		
		if(props.size() > 1)
		{
			HashSet<IUmlAssociation> assocs = new HashSet<IUmlAssociation>();
			
			for (IUmlProperty p : props) {
				HashSet<IUmlProperty> pSet = new HashSet<IUmlProperty>();
				pSet.add(p);
				assocs.add(new UmlAssociation(pSet, propSet, null, getNextId()));
			}
			associations.addAll(assocs);
			
			HashSet<String> idsSet = new HashSet<String>();
			for (IUmlAssociation association : assocs) {
				idsSet.add(association.getId());
			}
			
			constraints.add(new UmlConstraint(idsSet, new UmlLiteralString("xor")));
		}
	}

	private void createAssociationFromPropertyProductType(
			IUmlProperty property, PType defType) throws CGException {
		
		String name = property.getName();
		UmlProperty prop = Vdm2UmlUtil.cloneProperty(property);
		
		HashSet<IUmlProperty> props = new HashSet<IUmlProperty>();
		props.addAll(createEndProperty(defType,name));
		
		prop.setName("");
		HashSet<IUmlProperty> propSet = new HashSet<IUmlProperty>();
		propSet.add(prop);
		
		if(props.size() > 1)
		{
			associations.add(new UmlAssociation(props, propSet, null, getNextId()));
		}
		
	}

	private HashSet<IUmlProperty> createEndProperty(PType defType, String name) throws CGException {
		
		HashSet<IUmlProperty> result = new HashSet<IUmlProperty>();
		
		switch (defType.kindPType()) {
		case PRODUCT:
			AProductType productType = (AProductType) defType;
			for (PType type : productType.getTypes()) {
				result.addAll(createEndProperty(type, name));
			}
			break;
		case UNION:
			AUnionType unionType = (AUnionType) defType;
			for (PType type : unionType.getTypes()) {
				result.addAll(createEndProperty(type, name));
			}
			break;
		default:
			result.add(new UmlProperty(
					name, 
					new UmlVisibilityKind(UmlVisibilityKindQuotes.IQPRIVATE),
					Vdm2UmlUtil.extractMultiplicity(defType), 
					Vdm2UmlUtil.convertType(defType), 
					null,
					null,
					false,
					null,
					null,
					"Implementation postponed", 
					Vdm2UmlUtil.getQualifier(defType)));
			break;
		}
		
		return result;
	}

	private void createAssociationFromPropertyGeneral(IUmlProperty property,
			PType defType) throws CGException {
		
		String ownerClassName = null;
		
		if(property.getType() instanceof IUmlClassNameType)
		{
			ownerClassName = ((IUmlClassNameType)property.getType()).getName();
		}
		else
		{
			ownerClassName = Vdm2UmlUtil.getSimpleTypeName(property.getType());
		}
		
		IUmlProperty propOtherEnd = new UmlProperty("", 
				new  UmlVisibilityKind(UmlVisibilityKindQuotes.IQPRIVATE),
				null,
				new UmlClassNameType(property.getOwnerClass()),
				null,
				null,
				false,
				null,
				null,
				ownerClassName,
				null);
		
		HashSet<IUmlProperty> propertyThisEndSet = new HashSet<IUmlProperty>();
		propertyThisEndSet.add(property);
		
		HashSet<IUmlProperty> propertyOtherEndSet = new HashSet<IUmlProperty>();
		propertyOtherEndSet.add(propOtherEnd);
		
		associations.add(new UmlAssociation(propertyOtherEndSet,propertyThisEndSet, null, getNextId()));
	}

	

	

	


	

	
	
}
