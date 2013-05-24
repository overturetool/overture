package org.overture.ide.plugins.uml2.vdm2uml;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.RedefinableTemplateSignature;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.TemplateBinding;
import org.eclipse.uml2.uml.TemplateParameter;
import org.eclipse.uml2.uml.TemplateParameterSubstitution;
import org.eclipse.uml2.uml.UMLPackage;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AInMapMapType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.ARationalNumericBasicType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.ATokenBasicType;
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
import org.overture.ast.types.SNumericBasicType;
import org.overture.ast.types.SSeqType;
import org.overture.ide.plugins.uml2.UmlConsole;

/**
 * For help see: http://www.eclipse.org/modeling/mdt/uml2/docs/articles/Getting_Started_with_UML2/article.html
 * 
 * @author kel
 */
public class UmlTypeCreator extends UmlTypeCreatorBase
{

	public interface ClassTypeLookup
	{
		Class lookup(AClassType type);

		Class lookup(String className);
	}

	private class TypeMap extends HashMap<String, Classifier>
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			for (java.util.Map.Entry<String, Classifier> entry : this.entrySet())
			{
				sb.append(entry.getKey() + ": " + entry.getValue().getName()
						+ "\n");
			}
			return sb.toString();
		}

		// @Override
		// public Classifier put(String key, Classifier value)
		// {
		// // TODO Auto-generated method stub
		// return super.put(key, value);
		// }
	}

	private Model modelWorkingCopy = null;
	private final Map<String, Classifier> types = new TypeMap();

	private final ClassTypeLookup classLookup;
	private Package bindingPackage;
	private Package combositeTypePackage;
	private Package basicTypePackage;
	private UmlConsole console = null;
	private Map<String, Classifier> templateParameterTypes;

	public UmlTypeCreator(ClassTypeLookup classLookup, UmlConsole console)
	{
		this.classLookup = classLookup;
		this.console = console;
	}

	public void setModelWorkingCopy(Model modelWorkingCopy)
	{
		this.modelWorkingCopy = modelWorkingCopy;
	}

	public void create(Class class_, PType type)
	{
		System.out.println(type + " " + type.kindPType().toString() + " "
				+ getName(type));
		if (types.get(getName(type)) != null)
		{
			return;
		}

		switch (type.kindPType())
		{
			case AUnionType.kindPType:
				createNewUmlUnionType(class_, (AUnionType) type);
				return;
			case SInvariantType.kindPType:
				createNewUmlInvariantType(class_, (SInvariantType) type);
				return;
			case SBasicType.kindPType:
				convertBasicType(class_, (SBasicType) type);
				return;
			case ABracketType.kindPType:
				break;
			case AClassType.kindPType:
				break;
			case AFunctionType.kindPType:
				break;
			case SMapType.kindPType:
				createMapType(class_, (SMapType) type);
				return;
			case AOperationType.kindPType:
				break;
			case AOptionalType.kindPType:
				createOptionalType(class_, (AOptionalType) type);
				return;
				// break;
			case AParameterType.kindPType:
				break;
			case AProductType.kindPType:
				createProductType(class_, (AProductType) type);
				return;
			case AQuoteType.kindPType:
				break;
			case SSeqType.kindPType:
				createSeqType(class_, (SSeqType) type);
				return;
			case ASetType.kindPType:
				createSetType(class_, (ASetType) type);
				return;
			case AUndefinedType.kindPType:
				break;
			case AUnknownType.kindPType:
				types.put(getName(type), getVdmBasicTypePackage().createOwnedPrimitiveType(ANY_TYPE));
				return;
			case AUnresolvedType.kindPType:
				break;
			case AVoidType.kindPType:
				types.put(getName(type), getVdmBasicTypePackage().createOwnedPrimitiveType(VOID_TYPE));
				return;
			case AVoidReturnType.kindPType:

			default:
				break;
		}

		if (type instanceof AClassType
				&& classLookup.lookup((AClassType) type) != null)
		{
			return;
		}

		if (!types.containsKey(getName(type)))
		{
			if (console != null)
			{
				console.err.println("Unable to convert type: "
						+ type
						+ " - Inserting \"Unknown\" type as a replacement and continues");
			}
			Classifier unknownType = modelWorkingCopy.createOwnedPrimitiveType(UNKNOWN_TYPE);
			unknownType.addKeyword(getName(type));
			types.put(getName(type), unknownType);
		}
	}

	private void createOptionalType(Class class_, AOptionalType type)
	{
		// create(class_, type.getType());
		createTemplateType(class_, type, templateOptionalName, new String[] { "T" }, type.getType());
	}

	private void createSetType(Class class_, ASetType type)
	{
		createTemplateType(class_, type, templateSetName, new String[] { "T" }, type.getSetof());
	}

	private void createSeqType(Class class_, SSeqType type)
	{
		createTemplateType(class_, type, templateSeqName, new String[] { "T" }, type.getSeqof());
	}

	private void createMapType(Class class_, SMapType type)
	{
		createTemplateType(class_, type, AInMapMapType.kindSMapType.equals(type.kindSMapType()) ? templateInMapName
				: templateMapName, new String[] { "D", "R" }, type.getFrom(), type.getTo());
	}

	private void createUnionType(Class class_, AUnionType type)
	{
		createTemplateType(class_, type, getTemplateUnionName(type.getTypes().size()), getTemplateNames(type.getTypes().size()), type.getTypes());
	}

	private void createProductType(Class class_, AProductType type)
	{
		createTemplateType(class_, type, getTemplateProductName(type.getTypes().size()), getTemplateNames(type.getTypes().size()), type.getTypes());
	}

	private void createTemplateType(Class class_, PType type,
			String templateName, String[] templateSignatureNames,
			Collection<? extends PType> innertypes)
	{
		createTemplateType(class_, type, templateName, templateSignatureNames, innertypes.toArray(new PType[] {}));
	}

	private void createTemplateType(Class class_, PType type,
			String templateName, String[] templateSignatureNames,
			PType... innertypes)
	{
		if (!types.containsKey(templateName))
		{
			if (combositeTypePackage == null)
			{
				combositeTypePackage = this.modelWorkingCopy.createNestedPackage("Composite VDM Types");
			}
			Class templateSetClass = combositeTypePackage.createOwnedClass(templateName, false);

			RedefinableTemplateSignature templateT = (RedefinableTemplateSignature) templateSetClass.createOwnedTemplateSignature();

			String name = "";
			int i = 0;
			for (String signature : templateSignatureNames)
			{

				if (i > 0)
				{
					name += ",";
				}
				name += signature;
				i++;

				TemplateParameter p = templateT.createOwnedParameter(UMLPackage.Literals.CLASSIFIER_TEMPLATE_PARAMETER);
				Class cc = (Class) p.createOwnedParameteredElement(UMLPackage.Literals.CLASS);
				cc.setName(signature);
			}
			templateT.setName(name);

			types.put(templateName, templateSetClass);
		}

		// check if binding class exists
		Classifier bindingClass = types.get(getName(type));
		if (bindingClass == null)
		{
			if (bindingPackage == null)
			{
				bindingPackage = this.modelWorkingCopy.createNestedPackage("Binding classes");
			}
			Classifier templateSetClass = types.get(templateName);
			bindingClass = bindingPackage.createOwnedClass(getName(type), false);
			TemplateBinding binding = bindingClass.createTemplateBinding(templateSetClass.getOwnedTemplateSignature());

			if (innertypes.length == templateSetClass.getOwnedTemplateSignature().getOwnedParameters().size())
			{
				for (int i = 0; i < innertypes.length; i++)
				{
					TemplateParameterSubstitution substitution = binding.createParameterSubstitution();
					if (getUmlType(innertypes[i]) == null)
					{
						create(class_, innertypes[i]);
					}
					substitution.setActual(getUmlType(innertypes[i]));
					substitution.setFormal(templateSetClass.getOwnedTemplateSignature().getOwnedParameters().get(i));
				}
			}

			// for (PType innerType : innertypes)
			// {
			// TemplateParameterSubstitution substitution = binding.createParameterSubstitution();
			// if (getUmlType(innerType) == null)
			// {
			// create(class_, innerType);
			// }
			// substitution.setActual(getUmlType(innerType));
			// }

			types.put(getName(type), bindingClass);
		}
	}

	private void createNewUmlUnionType(Class class_, AUnionType type)
	{

		if (Vdm2UmlUtil.isUnionOfQuotes(type))
		{

			String qualifiedName = getName(type);
			String simpleName = qualifiedName.substring(qualifiedName.lastIndexOf(':') + 1);
			String className = qualifiedName.substring(0, qualifiedName.indexOf(':'));
			Class owningClass = classLookup.lookup(className);
			Enumeration enumeration = (Enumeration) owningClass.createNestedClassifier(simpleName, UMLPackage.Literals.ENUMERATION);

			// Enumeration enumeration = modelWorkingCopy.createOwnedEnumeration(getName(type));
			for (PType t : type.getTypes())
			{
				if (t instanceof AQuoteType)
				{
					String value = "<" + ((AQuoteType) t).getValue().getValue()
							+ ">";
					enumeration.createOwnedLiteral(value);

					PrimitiveType quoteClass = getVdmBasicTypePackage().createOwnedPrimitiveType(value);
					types.put(value, quoteClass);
				}
			}
			// class_.createNestedClassifier(name.module+"::"+name.name, enumeration.eClass());
			types.put(getName(type), enumeration);
		} else
		{
			// do the constraint XOR
			createUnionType(class_, type);
		}

	}

	private void createNewUmlInvariantType(Class class_, SInvariantType type)
	{
		switch (type.kindSInvariantType())
		{
			case ANamedInvariantType.kindSInvariantType:
			{
				PType ptype = ((ANamedInvariantType) type).getType();

				if (!getName(ptype).equals(getName(type)))
				{
					String simpleName = getName(type);
					simpleName = simpleName.substring(simpleName.lastIndexOf(':') + 1);

					Class owningClass = class_;

					if (!class_.getName().equals(type.getLocation().getModule()))
					{
						owningClass = classLookup.lookup(type.getLocation().getModule());
					}
					Classifier recordClass = owningClass.createNestedClassifier(simpleName, UMLPackage.Literals.CLASS);

					types.put(getName(type), recordClass);
					create(class_, ptype);
					recordClass.createGeneralization(getUmlType(ptype));

				} else
				{
					create(class_, ptype);
				}

				break;
			}

			case ARecordInvariantType.kindSInvariantType:
			{
				String simpleName = getName(type);
				simpleName = simpleName.substring(simpleName.lastIndexOf(':') + 1);

				Class owningClass = class_;

				if (!class_.getName().equals(type.getLocation().getModule()))
				{
					owningClass = classLookup.lookup(type.getLocation().getModule());
				}

				Class recordClass = (Class) owningClass.createNestedClassifier(simpleName, UMLPackage.Literals.CLASS);

				for (AFieldField field : ((ARecordInvariantType) type).getFields())
				{
					create(class_, field.getType());
					recordClass.createOwnedAttribute(field.getTag(), getUmlType(field.getType()));
				}

				Stereotype sterotype = (Stereotype) recordClass.createNestedClassifier("steriotype", UMLPackage.Literals.STEREOTYPE);
				sterotype.setName("record");
				types.put(getName(type), recordClass);

			}
				break;
		}

	}

	public Classifier getUmlType(PType type)
	{

		// while(Vdm2UmlUtil.isOptional(type))
		// {
		// type = ((AOptionalType) type).getType();
		// }

		String name = getName(type);

		if (types.containsKey(name))
		{
			return types.get(name);
		} else if (type instanceof AClassType
				&& classLookup.lookup((AClassType) type) != null)
		{
			return classLookup.lookup((AClassType) type);
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
			case ABooleanBasicType.kindSBasicType:
				typeName = "bool";
				break;
			case ACharBasicType.kindSBasicType:
				typeName = "char";
				break;
			case SNumericBasicType.kindSBasicType:
				convertNumericType((SNumericBasicType) type);
				return;
			case ATokenBasicType.kindSBasicType:
				typeName = "token";
				break;
			default:
				assert false : "Should not happen";
				break;
		}

		if (!types.containsKey(getName(type)))
		{
			types.put(getName(type), getVdmBasicTypePackage().createOwnedPrimitiveType(typeName));

		}
	}

	private void convertNumericType(SNumericBasicType type)
	{
		String typeName = null;
		switch (type.kindSNumericBasicType())
		{
			case AIntNumericBasicType.kindSNumericBasicType:
				typeName = "int";
				break;
			case ANatNumericBasicType.kindSNumericBasicType:
				typeName = "nat";
				break;
			case ANatOneNumericBasicType.kindSNumericBasicType:
				typeName = "nat1";
				break;
			case ARationalNumericBasicType.kindSNumericBasicType:
				typeName = "rat";
				break;
			case ARealNumericBasicType.kindSNumericBasicType:
				typeName = "real";
				break;
			default:
				assert false : "Should not happen";
				return;
		}
		if (!types.containsKey(getName(type)))
		{
			types.put(getName(type), getVdmBasicTypePackage().createOwnedPrimitiveType(typeName));

		}
	}

	private Package getVdmBasicTypePackage()
	{
		if (basicTypePackage == null)
		{
			basicTypePackage = this.modelWorkingCopy.createNestedPackage("Basic VDM Types");
		}
		return basicTypePackage;
	}

	public void addTemplateTypes(Map<String, Classifier> templateParameterTypes)
	{
		this.templateParameterTypes = templateParameterTypes;
		this.types.putAll(templateParameterTypes);
	}

	public void removeTemplateTypees()
	{
		if (this.templateParameterTypes != null)
		{
			for (String name : this.templateParameterTypes.keySet())
			{
				this.types.remove(name);
			}
		}
	}
}
