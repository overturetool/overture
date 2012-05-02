package org.overture.umlMapper;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.assistants.PAccessSpecifierAssistantTC;
import org.overture.ast.definitions.assistants.PDefinitionAssistantTC;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overturetool.umltrans.StatusLog;
import org.overturetool.umltrans.uml.IUmlAssociation;
import org.overturetool.umltrans.uml.IUmlClass;
import org.overturetool.umltrans.uml.IUmlClassNameType;
import org.overturetool.umltrans.uml.IUmlConstraint;
import org.overturetool.umltrans.uml.IUmlDefinitionBlock;
import org.overturetool.umltrans.uml.IUmlModel;
import org.overturetool.umltrans.uml.IUmlMultiplicityElement;
import org.overturetool.umltrans.uml.IUmlNode;
import org.overturetool.umltrans.uml.IUmlProperty;
import org.overturetool.umltrans.uml.IUmlType;
import org.overturetool.umltrans.uml.IUmlValueSpecification;
import org.overturetool.umltrans.uml.IUmlVisibilityKind;
import org.overturetool.umltrans.uml.UmlAssociation;
import org.overturetool.umltrans.uml.UmlClass;
import org.overturetool.umltrans.uml.UmlClassNameType;
import org.overturetool.umltrans.uml.UmlConstraint;
import org.overturetool.umltrans.uml.UmlLiteralString;
import org.overturetool.umltrans.uml.UmlModel;
import org.overturetool.umltrans.uml.UmlProperty;
import org.overturetool.umltrans.uml.UmlVisibilityKind;
import org.overturetool.umltrans.uml.UmlVisibilityKindQuotes;

public class Vdm2Uml {

	private StatusLog log = null;	
	private Vector<String> filteredClassNames = null;
	private Set<IUmlClass> nestedClasses = new HashSet<IUmlClass>();
	private Set<IUmlAssociation> associations = new HashSet<IUmlAssociation>();
	private Set<IUmlConstraint> constraints = new HashSet<IUmlConstraint>();
	private int runningId = 0;
	
	public Vdm2Uml() {
		
		try {
			log = new StatusLog();
		} catch (CGException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		filteredClassNames = new Vector<String>();
	}
	
	private String getNextId() {
		return Integer.toString(runningId++);
	}
	
	public IUmlModel init(List<SClassDefinition> classes)
	{
		IUmlModel model = null;
		
		try {
			model = buildUml(classes);
		} catch (CGException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return model;
	}

	private IUmlModel buildUml(List<SClassDefinition> classes) throws CGException {
		
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
		log.addNewClassInfo(name);
		
		boolean isStatic = false;
		boolean isActive = Vdm2UmlUtil.isClassActive(sClass);
		boolean isAbstract = Vdm2UmlUtil.hasSubclassResponsabilityDefinition(sClass.getDefinitions());
		HashSet<IUmlDefinitionBlock> dBlocks = buildDefinitionBlocks(sClass.getDefinitions(),name);
		Vector<IUmlClassNameType> supers = Vdm2UmlUtil.getSuperClasses(sClass);
		IUmlVisibilityKind visibility = new UmlVisibilityKind(UmlVisibilityKindQuotes.IQPUBLIC);
		
		return new UmlClass(name,dBlocks,isAbstract,supers,visibility,isStatic,isActive,null);
	}

	

	private HashSet<IUmlDefinitionBlock> buildDefinitionBlocks(
			LinkedList<PDefinition> definitions, String owner) throws CGException {

		HashSet<IUmlDefinitionBlock> result = new HashSet<IUmlDefinitionBlock>();
		HashSet<IUmlProperty> instanceVariables = new HashSet<IUmlProperty>();
		
		for (PDefinition pDefinition : definitions) {
			switch (pDefinition.kindPDefinition()) {
			case EXPLICITFUNCTION:
				break;
			case EXPLICITOPERATION:
				break;
			case IMPLICITFUNCTION:
				break;
			case IMPLICITOPERATION:
				break;
			case INSTANCEVARIABLE:
				instanceVariables.add(buildDefinitionBlock((AInstanceVariableDefinition)pDefinition,owner));
				break;
			case TYPE:
				break;
			case VALUE:
				break;
			default:
				break;
			}
		}
		
		return result;
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
