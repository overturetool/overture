package org.overture.ide.plugins.uml2.vdm2uml;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.VisibilityKind;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.assistants.PAccessSpecifierAssistantTC;
import org.overture.ast.definitions.assistants.PDefinitionAssistantTC;
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

public class Vdm2Uml {

	//private StatusLog log = null;	
	private Vector<String> filteredClassNames = new Vector<String>();
//	private Set<IUmlClass> nestedClasses = new HashSet<IUmlClass>();
//	private Set<IUmlAssociation> associations = new HashSet<IUmlAssociation>();
//	private Set<IUmlConstraint> constraints = new HashSet<IUmlConstraint>();
	private int runningId = 0;
	private Model modelWorkingCopy = null; 
	
	public Vdm2Uml() {
		
		
	}
	
	private String getNextId() {
		return Integer.toString(runningId++);
	}
	
	public Model init(List<SClassDefinition> classes)
	{
		modelWorkingCopy = UMLFactory.eINSTANCE.createModel();		
		
		try {
			buildUml(classes);
			
			@SuppressWarnings("rawtypes")
			HashSet definitions = model.getDefinitions();
			definitions.addAll(associations);
			definitions.addAll(constraints);
			model.setDefinitions(definitions);
		} catch (CGException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return modelWorkingCopy;
	}

	private void buildUml(List<SClassDefinition> classes)  {
		
		//HashSet<IUmlNode> umlClasses = new HashSet<IUmlNode>();
		
		for (SClassDefinition sClass : classes) {
			
			if(!filteredClassNames.contains(sClass.getName().name))
			{
				buildClass(sClass);	
			}
			
		}
		
		//umlClasses.addAll(nestedClasses);
		//return new UmlModel("Root", umlClasses);
	}

	private void buildClass(SClassDefinition sClass)  {
		
		String name = sClass.getName().name;
		boolean isAbstract = Vdm2UmlUtil.hasSubclassResponsabilityDefinition(sClass.getDefinitions());
		org.eclipse.uml2.uml.Class class_ = modelWorkingCopy.createOwnedClass(name, isAbstract);
		
		boolean isActive = Vdm2UmlUtil.isClassActive(sClass);
		class_.setIsActive(isActive);
		
		class_.setVisibility(VisibilityKind.PUBLIC_LITERAL);
		
		buildDefinitionBlocks(class_,sClass.getDefinitions());
		
		
		//TODO: static seems not to exist for classes
		//boolean isStatic = false;
		
		//TODO: investigate how to declare super classes
		//Vector<IUmlClassNameType> supers = Vdm2UmlUtil.getSuperClasses(sClass);
				
		
		
		
		//return new UmlClass(name,dBlocks,isAbstract,supers,visibility,isStatic,isActive,null);
	}

	

	private void buildDefinitionBlocks(
			Class class_, LinkedList<PDefinition> defs)  {

//		Vector<IUmlDefinitionBlock> result = new Vector<IUmlDefinitionBlock>();
//		
//		HashSet<IUmlProperty> instanceVariables = new HashSet<IUmlProperty>();
//		HashSet<IUmlProperty> valueDefinitions = new HashSet<IUmlProperty>();
//		HashSet<IUmlType> typeDefinitions = new HashSet<IUmlType>();
//		HashSet<IUmlOperation> operationDefinitions = new HashSet<IUmlOperation>();
//		HashSet<IUmlOperation> functionDefinitions = new HashSet<IUmlOperation>();
		
		for (PDefinition pDefinition : defs) {
			switch (pDefinition.kindPDefinition()) {
			case EXPLICITFUNCTION:
				if(!Vdm2UmlUtil.hasPolymorphic((AExplicitFunctionDefinition) pDefinition))
				{
					buildDefinitionBlock((AExplicitFunctionDefinition) pDefinition,class_);
					//functionDefinitions.add(buildDefinitionBlock((AExplicitFunctionDefinition) pDefinition));	
				}
				
				break;
			case IMPLICITFUNCTION:
				break;
			case EXPLICITOPERATION:				
				buildDefinitionBlock((AExplicitOperationDefinition)pDefinition,class_);
				break;
			case IMPLICITOPERATION:
				buildDefinitionBlock((AImplicitOperationDefinition)pDefinition,class_);
				break;
			case INSTANCEVARIABLE:
				buildDefinitionBlock((AInstanceVariableDefinition)pDefinition,class_);
				
				break;
			case TYPE:
				buildDefinitionBlock((ATypeDefinition)pDefinition);
				break;
			case VALUE:
				IUmlProperty valueDef = buildDefinitionBlock((AValueDefinition)pDefinition, linkedList);
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

	private void buildDefinitionBlock(
			AExplicitFunctionDefinition pDefinition, Class class_)  {
		String name = pDefinition.getName().name;
		
		Operation op = class_.createOwnedOperation(name, null, null, null);
		
		
//		IUmlMultiplicityElement multiplicity = new UmlMultiplicityElement(false, false, (long)1,(long)1);
//		 vdm_init_UmlMultiplicityElement();
//		    {
//
//		      setIsOrdered(p1);
//		      setIsUnique(p2);
//		      setLower(p3);
//		      setUpper(p4);
//		    }
		//Setting multiplicity
		op.setIsOrdered(false);
		op.setIsUnique(false);
		op.setLower(1);
		op.setUpper(1);
		
		//setting visibility
		AAccessSpecifierAccessSpecifier access = pDefinition.getAccess();
		VisibilityKind visibility = Vdm2UmlUtil.convertAccessSpecifierToVisibility(access);
		op.setVisibility(visibility);
		
		//setting static?
		boolean isStatic = PAccessSpecifierAssistantTC.isStatic(access);
		op.setIsStatic(isStatic);
		
		
		//TODO: missing type information
//		IUmlType type = Vdm2UmlUtil.convertType(PDefinitionAssistantTC.getType(pDefinition));				
//		AFunctionType funcType = (AFunctionType) PDefinitionAssistantTC.getType(pDefinition);
//		
//		LinkedList<List<PPattern>> pnames = pDefinition.getParamPatternList();
//		
//		Vector<IUmlParameter> params = null;
//		if(pDefinition.getPostcondition() == null)
//		{
//			if(pnames.size() > 0)
//			{
//				params = Vdm2UmlUtil.buildParameters(pnames.getFirst(),funcType);
//			}
//		}
//		else
//		{
//			//TODO: function with post condition == extendedexplicit in OML
//			params = new Vector<IUmlParameter>();
//			
//			IUmlParameter returnType = new UmlParameter("return", 
//					new UmlBoolType(),//TODO: missing type
//					new UmlMultiplicityElement(true, true,(long)0, (long)0),//TODO: missing multiplicity
//					"", 
//					new UmlParameterDirectionKind(UmlParameterDirectionKindQuotes.IQRETURN));
//			params.add(returnType);
//		}
		
		
				
		
//		return new UmlOperation(name, 
//                visibility, 
//                multiplicity, 
//                true,
//                type, 
//                isStatic,
//                new UmlParameters(params));
	}

	private void buildDefinitionBlock(
			AImplicitOperationDefinition pDefinition,Class class_)  {
		
		String name = pDefinition.getName().name;
		Operation op = class_.createOwnedOperation(name, null, null, null);
		
		//visibility
		AAccessSpecifierAccessSpecifier access = pDefinition.getAccess();
		VisibilityKind visibility = Vdm2UmlUtil.convertAccessSpecifierToVisibility(access);
		op.setVisibility(visibility);
		
		
		//multiplicity
		op.setIsOrdered(false);
		op.setIsUnique(false);
		op.setLower(1);
		op.setUpper(1);
		
		//static?
		boolean isStatic = PAccessSpecifierAssistantTC.isStatic(access);
		op.setIsStatic(isStatic);
		
//		IUmlType type = null;
//		LinkedList<APatternListTypePair> patternTypePairs = pDefinition.getParameterPatterns();
//		
//		APatternTypePair result = pDefinition.getResult();
//		
//		Vector<IUmlParameter> params = Vdm2UmlUtil.buildParameters(patternTypePairs);
//		Vector<IUmlParameter> results = Vdm2UmlUtil.buildFnResult(result);
//		params.addAll(results);
//		
//		return new UmlOperation(name,visibility,multiplicity,false,type,isStatic, new UmlParameters(params));
	}

	private void buildDefinitionBlock(
			AExplicitOperationDefinition pDefinition, Class class_) {
		
		String name = pDefinition.getName().name;
		Operation op = class_.createOwnedOperation(name, null, null, null);
		
		//visibility
		AAccessSpecifierAccessSpecifier access = pDefinition.getAccess();
		VisibilityKind visibility = Vdm2UmlUtil.convertAccessSpecifierToVisibility(access);
		op.setVisibility(visibility);
	
		//multiplicity 
		op.setIsOrdered(false);
		op.setIsUnique(false);
		op.setLower(1);
		op.setUpper(1);
		
		//static?
		boolean isStatic = PAccessSpecifierAssistantTC.isStatic(access);
		op.setIsStatic(isStatic);
//		
//		IUmlType type = null;
//		
//		//TODO: I dont get the parameters right now
//		Vector<IUmlParameter> params = null;
//		if(pDefinition.getPostcondition() == null)
//		{
//			params = Vdm2UmlUtil.buildParameters(pDefinition,PDefinitionAssistantTC.getType(pDefinition));	
//		}
//		else
//		{
//			//TODO:operation with post condition == extendedexplicit in OML
//			params = Vdm2UmlUtil.buildParameters(pDefinition,PDefinitionAssistantTC.getType(pDefinition));
//			
//		}
//		
//		return new UmlOperation(name, visibility, multiplicity, false, type, isStatic , new UmlParameters(params));
		
	}

	private void buildDefinitionBlock(ATypeDefinition pDefinition) {
		
		Class class_ = buildClassFromType(pDefinition);
		class_.createO
		modelWorkingCopy.createOwnedType(pDefinition.getName().name, UMLPackage.eINSTANCE.getType() );
		
		//TODO: nested class?
		
		//return new UmlClassNameType(pDefinition.getName().name);
	}

	private Class buildClassFromType(ATypeDefinition pDefinition)  {
		
		String name = pDefinition.getName().name;
		Class class_  = modelWorkingCopy.createOwnedClass(name, false);
		
		PType type = PDefinitionAssistantTC.getType(pDefinition);
		
		
//		IUmlDefinitionBlock classBody = buildTypeDefinitionBlocks(name,type);
//		HashSet<IUmlDefinitionBlock> classBodySet = new HashSet<IUmlDefinitionBlock>(); 
//		classBodySet.add(classBody);
		//TODO: investigate body
		
		//boolean isAbstract = false;
		//Vector<IUmlClassNameType> superClasses = new Vector<IUmlClassNameType>();
		
		VisibilityKind visibility = Vdm2UmlUtil.convertAccessSpecifierToVisibility(pDefinition.getAccess());
		
		class_.setVisibility(visibility);
		class_.setIsActive(false);
		
		//IUmlTemplateSignature templateSignature = null;
		
		return class_;
		//return new UmlClass(name, classBodySet, isAbstract, superClasses, visibility, isStatic, isActive, templateSignature);
		
	}

	private IUmlDefinitionBlock buildTypeDefinitionBlocks(String name,
			PType type)  {
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

	private void buildDefinitionBlock(AInstanceVariableDefinition variable,
			Class class_){
		
		PType defType = PDefinitionAssistantTC.getType(variable);
		String name = variable.getName().name;
		int upper =  Vdm2UmlUtil.extractUpper(defType);
		int lower =  Vdm2UmlUtil.extractLower(defType);
		//TODO:type is null for now
		class_.createOwnedAttribute(name, null, lower, upper);
		
		
		
		PType defType = PDefinitionAssistantTC.getType(variable);
		AAccessSpecifierAccessSpecifier accessSpecifier = variable.getAccess();
		
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
