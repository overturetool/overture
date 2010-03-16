package org.overture.ide.ui.internal.viewsupport;

import java.awt.Font;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.overture.ide.core.IVdmElement;
import org.overture.ide.core.utility.IVdmProject;
import org.overture.ide.ui.internal.VdmUIMessages;
import org.overture.ide.ui.internal.util.Strings;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;
import org.overturetool.vdmj.definitions.InstanceVariableDefinition;
import org.overturetool.vdmj.definitions.LocalDefinition;
import org.overturetool.vdmj.definitions.TypeDefinition;
import org.overturetool.vdmj.types.InvariantType;
import org.overturetool.vdmj.types.NamedType;
import org.overturetool.vdmj.types.OperationType;
import org.overturetool.vdmj.types.Type;

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
	private static StyledString getStyledResourceLabel(IResource resource) {
		StyledString result = new StyledString(resource.getName());
		return Strings.markLTR(result);
	}

	public static StyledString getStyledTextLabel(Object element, long flags) {
//		if (element instanceof IVdmElement) {
//			return getElementLabel((IVdmElement) element, flags);
//		}
		if (element instanceof ClassDefinition) {
			return getClassDefinitionLabel((ClassDefinition) element, flags);
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
		if (element instanceof ExplicitFunctionDefinition) {
			return getExplicitFunctionDefinitionLabel((ExplicitFunctionDefinition) element);
		}
		if (element instanceof LocalDefinition) {
			return getLocalDefinitionLabel((LocalDefinition) element);
		}

		return null;
	}

	private static StyledString getLocalDefinitionLabel(LocalDefinition element) {
		StyledString result = new StyledString();
		result.append(element.name.name);
		result.append(" : " + "Missing_Class_Name`" + getSimpleTypeString(element.type),
				StyledString.DECORATIONS_STYLER);
		return result;
	}

	private static StyledString getExplicitFunctionDefinitionLabel(
			ExplicitFunctionDefinition element) {
		StyledString result = new StyledString();
		result.append(element.name.name);
		return result;
	}

	private static StyledString getTypeDefinitionLabel(TypeDefinition element) {
		StyledString result = new StyledString();
		result.append(element.name.name);
		result.append(" : " + getSimpleTypeString(((NamedType) element.type).type),
				StyledString.DECORATIONS_STYLER);
		return result;
	}

	private static StyledString getExplicitOperationDefinitionLabel(
			ExplicitOperationDefinition element) {
		StyledString result = new StyledString();

		result.append(element.name.name);
				
		if (element.getType() instanceof OperationType) {
			OperationType type = (OperationType) element.getType();
			if (type.parameters.size() == 0) {
				result.append("() ");
			} else {
				result.append("(");
				int i=0;
				while(i< type.parameters.size()-1)
				{
					Type definition = (Type) type.parameters.elementAt(i);
					result.append(getSimpleTypeString(definition) + ", ");
					
					i++;
				}
				Type definition = (Type) type.parameters.elementAt(i);
				result.append(getSimpleTypeString(definition) + ")");				
			}
		}
				
		result.append(" : " + getSimpleTypeString(element.type.result), StyledString.DECORATIONS_STYLER);

		
		return result;
	}

	private static StyledString getInstanceVariableDefinitionLabel(
			InstanceVariableDefinition element) {
		StyledString result = new StyledString();
		result.append(element.name.name);
		result.append(" : " + getSimpleTypeString(element.type),
				StyledString.DECORATIONS_STYLER);
		return result;
	}

	private static StyledString getClassDefinitionLabel(
			ClassDefinition element, long flags) {
		StyledString result = new StyledString();
		result.append(element.name.name);
		return result;
	}

	

	private static void getElementLabel(IVdmElement element, long flags,
			StyledString result) {
		new VdmElementLabelComposer(result).appendElementLabel(element, flags);

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
	
	private static String getSimpleTypeString(Type type)
	{		
		String typeName = processUnresolved(type);
		typeName = typeName.replace("(", "");
		typeName = typeName.replace(")", "");
		return typeName;
	}
}
