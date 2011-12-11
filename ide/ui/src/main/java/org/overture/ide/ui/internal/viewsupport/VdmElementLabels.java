/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.ide.ui.internal.viewsupport;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.osgi.util.TextProcessor;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;
import org.overturetool.vdmj.definitions.ImplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ImplicitOperationDefinition;
import org.overturetool.vdmj.definitions.InheritedDefinition;
import org.overturetool.vdmj.definitions.InstanceVariableDefinition;
import org.overturetool.vdmj.definitions.LocalDefinition;
import org.overturetool.vdmj.definitions.NamedTraceDefinition;
import org.overturetool.vdmj.definitions.PerSyncDefinition;
import org.overturetool.vdmj.definitions.RenamedDefinition;
import org.overturetool.vdmj.definitions.TypeDefinition;
import org.overturetool.vdmj.definitions.UntypedDefinition;
import org.overturetool.vdmj.definitions.ValueDefinition;
import org.overturetool.vdmj.modules.Import;
import org.overturetool.vdmj.modules.ImportAll;
import org.overturetool.vdmj.modules.ImportFromModule;
import org.overturetool.vdmj.modules.ImportedType;
import org.overturetool.vdmj.modules.ImportedValue;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.modules.ModuleImports;
import org.overturetool.vdmj.types.Field;
import org.overturetool.vdmj.types.FunctionType;
import org.overturetool.vdmj.types.NamedType;
import org.overturetool.vdmj.types.OperationType;
import org.overturetool.vdmj.types.ProductType;
import org.overturetool.vdmj.types.RecordType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.UnresolvedType;
import org.overturetool.vdmj.types.VoidType;

public class VdmElementLabels {

	/**
	 * Specifies to apply color styles to labels. This flag only applies to
	 * methods taking or returning a {@link StyledString}.
	 * 
	 * @since 3.4
	 */
	public final static long COLORIZE = 1L << 55;

	/**
	 * Default options (M_PARAMETER_TYPES, M_APP_TYPE_PARAMETERS &
	 * T_TYPE_PARAMETERS enabled)
	 */
	public final static long ALL_DEFAULT = 1231;

	/**
	 * Returns the styled string for the given resource. The returned label is
	 * BiDi-processed with {@link TextProcessor#process(String, String)}.
	 * 
	 * @param resource
	 *            the resource
	 * @return the styled string
	 * @since 3.4
	 */
	// private static StyledString getStyledResourceLabel(IResource resource) {
	// StyledString result = new StyledString(resource.getName());
	// return Strings.markLTR(result);
	// }

	// TODO: this map should be deleted when the AST fix is made
	// so that definitions contain a reference to the module they belong
	static Map<String, Module> activeModule = new HashMap<String, Module>();

	public static StyledString getStyledTextLabel(Object element, long flags) {
		// if (element instanceof IVdmElement) {
		// return getElementLabel((IVdmElement) element, flags);
		// }
		if (element instanceof ClassDefinition) {
			// activeModule = null;
			return getClassDefinitionLabel((ClassDefinition) element, flags);
		}
		if (element instanceof Module) {
			activeModule.put(((Module) element).getName(), ((Module) element));
			return getModuleLabel((Module) element, flags);
		}
		if (element instanceof InstanceVariableDefinition) {
			return getInstanceVariableDefinitionLabel((InstanceVariableDefinition) element);
		}
		if (element instanceof TypeDefinition) {
			return getTypeDefinitionLabel((TypeDefinition) element);
		}
		if (element instanceof ExplicitOperationDefinition) {
			return getExplicitOperationDefinitionLabel((ExplicitOperationDefinition) element);
		}
		if (element instanceof ImplicitOperationDefinition) {
			return getImplicitOperationDefinitionLabel((ImplicitOperationDefinition) element);
		}

		if (element instanceof ExplicitFunctionDefinition) {
			return getExplicitFunctionDefinitionLabel((ExplicitFunctionDefinition) element);
		}

		if (element instanceof ImplicitFunctionDefinition) {
			return getImplicitFunctionDefinitionLabel((ImplicitFunctionDefinition) element);
		}
		if (element instanceof LocalDefinition) {
			return getLocalDefinitionLabel((LocalDefinition) element);
		}
		if (element instanceof ValueDefinition) {
			System.out.println("VALUE DEF");
		}
		if (element instanceof NamedTraceDefinition) {
			return getNamedTraceDefinitionLabel((NamedTraceDefinition) element);
		}

		if (element instanceof UntypedDefinition) {
			return getUntypedDefinition((UntypedDefinition) element);
		}

		if (element instanceof ModuleImports) {
			StyledString result = new StyledString();
			result.append("import definitions");
			return result;
		}

		if (element instanceof ImportFromModule) {
			return getImportFromModuleLabel((ImportFromModule) element);
		}

		if (element instanceof Import) {
			return getImportLabel((Import) element);
		}

		if (element instanceof Field) {
			StyledString result = new StyledString();
			result.append(((Field) element).tag);
			result.append(" : " + processUnresolved(((Field) element).type),
					StyledString.DECORATIONS_STYLER);
			return result;
		}

		if (element instanceof PerSyncDefinition) {
			return getPerSyncDefinitionLabel((PerSyncDefinition) element);
		}

		if (element instanceof InheritedDefinition) {
			return getInheritedDefinition((InheritedDefinition) element, flags);
		}

		if (element instanceof ImportsContainer) {
			return getStyledTextLabel(
					(ModuleImports) ((ImportsContainer) element).getImports(),
					flags);
		}

		if (element instanceof RenamedDefinition) {
			return getRenamedDefinitionLabel((RenamedDefinition) element, flags);
		}

		StyledString result = new StyledString();
		result.append("Unsupported type reached: " + element);
		return result;

	}

	private static StyledString getRenamedDefinitionLabel(
			RenamedDefinition element, long flags) {

		return getStyledTextLabel(element.def, flags);

	}

	private static StyledString getInheritedDefinition(
			InheritedDefinition element, long flags) {

		StyledString res = getStyledTextLabel(element.superdef, flags);

		return res;
	}

	private static StyledString getPerSyncDefinitionLabel(
			PerSyncDefinition element) {
		StyledString result = new StyledString();
		result.append(element.getName());
		result.append(" : sync predicate", StyledString.DECORATIONS_STYLER);
		return result;
	}

	private static StyledString getImplicitOperationDefinitionLabel(
			ImplicitOperationDefinition element) {
		StyledString result = new StyledString();

		result.append(element.getName());

		if (element.getType() instanceof OperationType) {
			OperationType type = (OperationType) element.getType();
			if (type.parameters.size() == 0) {
				result.append("() ");
			} else {
				result.append("(");
				int i = 0;
				while (i < type.parameters.size() - 1) {
					Type definition = (Type) type.parameters.elementAt(i);
					result.append(getSimpleTypeString(definition) + ", ");

					i++;
				}
				Type definition = (Type) type.parameters.elementAt(i);
				result.append(getSimpleTypeString(definition) + ")");
			}
		}

		if (element.type.result instanceof VoidType) {
			result.append(" : ()", StyledString.DECORATIONS_STYLER);
		} else {
			result.append(" : " + getSimpleTypeString(element.type.result),
					StyledString.DECORATIONS_STYLER);
		}

		return result;
	}

	private static StyledString getImplicitFunctionDefinitionLabel(
			ImplicitFunctionDefinition element) {
		StyledString result = new StyledString();
		result.append(element.getName());

		if (element.getType() instanceof FunctionType) {
			FunctionType type = (FunctionType) element.getType();
			if (type.parameters.size() == 0) {
				result.append("() ");
			} else {
				result.append("(");
				int i = 0;
				while (i < type.parameters.size() - 1) {
					Type definition = (Type) type.parameters.elementAt(i);
					result.append(getSimpleTypeString(definition) + ", ");

					i++;
				}
				Type definition = (Type) type.parameters.elementAt(i);
				result.append(getSimpleTypeString(definition) + ")");
			}
		}

		if (element.type.result instanceof VoidType) {
			result.append(" : ()", StyledString.DECORATIONS_STYLER);
		} else {
			result.append(" : " + getSimpleTypeString(element.type.result),
					StyledString.DECORATIONS_STYLER);

		}

		return result;
	}

	private static StyledString getImportLabel(Import element) {

		StyledString result = new StyledString();

		if (element instanceof ImportAll) {
			result.append(element.name.toString());
		} else if (element instanceof ImportedType) {
			
			
			ImportedType type = (ImportedType) element;
			result.append(type.name.toString());
			if(type.def!=null)
			{
				result.append(" : " + getSimpleTypeString(type.def.getType()), StyledString.DECORATIONS_STYLER);
			}else
			{
				result.append(" : " , StyledString.DECORATIONS_STYLER);
			}
			
			result.append(type.renamed.name, StyledString.DECORATIONS_STYLER);
		} else if (element instanceof ImportedValue) {
			ImportedValue value = (ImportedValue) element;
			result.append(value.name.toString());
			if (value.type != null) {
				String typeString = value.type.toString();
				typeString = typeString.replaceFirst("[(]", "");
				typeString = typeString.substring(0, typeString.length() - 1);
				result.append(" : " + typeString);
			}
		}

		if (element.renamed != null) {
			result.append(" (renamed as: " + element.renamed.name + ")",
					StyledString.DECORATIONS_STYLER);
		}
		return result;
	}

	private static StyledString getImportFromModuleLabel(
			ImportFromModule element) {
		StyledString result = new StyledString();

		result.append(element.name.toString());

		return result;
	}

	private static StyledString getNamedTraceDefinitionLabel(
			NamedTraceDefinition element) {
		StyledString result = new StyledString();
		result.append(element.getName());
		return result;
	}

	private static StyledString getUntypedDefinition(UntypedDefinition element) {
		StyledString result = new StyledString();
		result.append(element.getName());
		DefinitionList definitions = null;
		if (element.classDefinition != null) {
			ClassDefinition classDef = element.classDefinition;
			definitions = classDef.definitions;
		} else {
			if (activeModule != null)
				definitions = activeModule.get(element.name.module).defs;
		}
		if (definitions != null) {
			for (Definition def : definitions) {
				if (def instanceof ValueDefinition) {
					for (int i = 0; i < def.getDefinitions().size(); i++) {
						if (def.getDefinitions().get(i).getLocation()
								.equals(element.getLocation())) {
							Type type = ((ValueDefinition) def).type;
							if (type instanceof ProductType) {
								return result
										.append(" : "
												+ getSimpleTypeString(((ProductType) type).types
														.get(i)),
												StyledString.DECORATIONS_STYLER);
							} else if (type instanceof UnresolvedType) {
								return result.append(" : "
										+ getSimpleTypeString(type),
										StyledString.DECORATIONS_STYLER);
							} else if (type != null) {
								return result.append(" : "
										+ getSimpleTypeString(type),
										StyledString.DECORATIONS_STYLER);
							} else {
								// System.err.println("Could not translate type");
								return result.append(" : "
										+ ((ValueDefinition) def).exp,
										StyledString.DECORATIONS_STYLER);
							}
						}
					}
				}
			}
		}

		// Type elemType = element.getType();
		// result.append(" : " + getSimpleTypeString(elemType),
		// StyledString.DECORATIONS_STYLER);
		return result;
	}

	private static StyledString getLocalDefinitionLabel(LocalDefinition element) {
		StyledString result = new StyledString();
		result.append(element.getName());
		if (element.type.location.module.toLowerCase().equals("default")) {
			result.append(" : " + getSimpleTypeString(element.type),
					StyledString.DECORATIONS_STYLER);
		} else {
			result.append(" : " + element.type.location.module + "`"
					+ getSimpleTypeString(element.type),
					StyledString.DECORATIONS_STYLER);
		}
		return result;
	}

	private static StyledString getExplicitFunctionDefinitionLabel(
			ExplicitFunctionDefinition element) {
		StyledString result = new StyledString();
		result.append(element.getName());

		if (element.getType() instanceof FunctionType) {
			FunctionType type = (FunctionType) element.getType();
			if (type.parameters.size() == 0) {
				result.append("() ");
			} else {
				result.append("(");
				int i = 0;
				while (i < type.parameters.size() - 1) {
					Type definition = (Type) type.parameters.elementAt(i);
					result.append(getSimpleTypeString(definition) + ", ");

					i++;
				}
				Type definition = (Type) type.parameters.elementAt(i);
				result.append(getSimpleTypeString(definition) + ")");
			}
		}

		if (element.type.result instanceof VoidType) {
			result.append(" : ()", StyledString.DECORATIONS_STYLER);
		} else {
			result.append(" : " + getSimpleTypeString(element.type.result),
					StyledString.DECORATIONS_STYLER);

		}

		return result;
	}

	private static StyledString getTypeDefinitionLabel(TypeDefinition element) {
		StyledString result = new StyledString();
		if (element.type instanceof RecordType) {

			result.append(element.getName());
			result.append(" : record type", StyledString.DECORATIONS_STYLER);
		} else if (element.type instanceof NamedType) {

			result.append(element.getName());
			result.append(" : "
					+ getSimpleTypeString(((NamedType) element.type).type),
					StyledString.DECORATIONS_STYLER);

		}
		return result;
	}

	private static StyledString getExplicitOperationDefinitionLabel(
			ExplicitOperationDefinition element) {
		StyledString result = new StyledString();

		result.append(element.getName());

		if (element.getType() instanceof OperationType) {
			OperationType type = (OperationType) element.getType();
			if (type.parameters.size() == 0) {
				result.append("() ");
			} else {
				result.append("(");
				int i = 0;
				while (i < type.parameters.size() - 1) {
					Type definition = (Type) type.parameters.elementAt(i);
					result.append(getSimpleTypeString(definition) + ", ");

					i++;
				}
				Type definition = (Type) type.parameters.elementAt(i);
				result.append(getSimpleTypeString(definition) + ")");
			}
		}

		if (element.type.result instanceof VoidType) {
			result.append(" : ()", StyledString.DECORATIONS_STYLER);
		} else {
			result.append(" : " + getSimpleTypeString(element.type.result),
					StyledString.DECORATIONS_STYLER);

		}

		return result;
	}

	private static StyledString getInstanceVariableDefinitionLabel(
			InstanceVariableDefinition element) {
		StyledString result = new StyledString();
		result.append(element.getName());
		result.append(" : " + getSimpleTypeString(element.type),
				StyledString.DECORATIONS_STYLER);
		return result;
	}

	private static StyledString getClassDefinitionLabel(
			ClassDefinition element, long flags) {
		StyledString result = new StyledString();
		result.append(element.getName());
		return result;
	}

	private static StyledString getModuleLabel(Module element, long flags) {
		StyledString result = new StyledString();
		result.append(element.getName());
		return result;
	}

	public static String getTextLabel(Object element, long evaluateTextFlags) {
		// TODO Auto-generated method stub
		return null;
	}

	private static String processUnresolved(Type definition) {

		String defString = definition.toString();

		if (defString.contains("unresolved ")) {
			defString = defString.replace("(", "");
			defString = defString.replace(")", "");
			defString = defString.replace("unresolved ", "");
			return defString;
		}
		return definition.toString();
	}

	private static String getSimpleTypeString(Type type) {
		String typeName = processUnresolved(type);
		typeName = typeName.replace("(", "");
		typeName = typeName.replace(")", "");
		return typeName;
	}
}
