package org.overture.ide.debug.core;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.dltk.debug.core.DLTKDebugPlugin;
import org.eclipse.dltk.debug.core.model.ArrayScriptType;
import org.eclipse.dltk.debug.core.model.AtomicScriptType;
import org.eclipse.dltk.debug.core.model.ComplexScriptType;
import org.eclipse.dltk.debug.core.model.IScriptType;
import org.eclipse.dltk.debug.core.model.IScriptTypeFactory;
import org.eclipse.dltk.debug.core.model.IScriptValue;
import org.eclipse.dltk.debug.core.model.StringScriptType;

public class OvertureTypeFactory implements IScriptTypeFactory {
	private static final String[] atomicTypes = { 
						"real", "int", "nat", "nat1",
						"bool", 
						"char"};
	
	public OvertureTypeFactory() {
	}
	

	public IScriptType buildType(String type) {
		for (int i = 0; i < atomicTypes.length; ++i) {
			if (atomicTypes[i].equals(type)) {
				return new AtomicScriptType(type);
			}
		}
		if (STRING.equalsIgnoreCase(type)) {
			return new StringScriptType(type);
		}

		if ("javaarray".equals(type) || "array".equals(type)) {
			return new ArrayScriptType();
		}

		if ("string".equals(type)) {
			return new StringScriptType("string");
		}
		return new ComplexScriptType(type) {
			@Override
			public String formatValue(IScriptValue value) {
				StringBuffer sb = new StringBuffer();
				sb.append(value.getRawValue());
				String id = value.getInstanceId();
				if (id != null) {
					sb.append(" (id = " + id + ")"); // TODO add constant
				}

				return sb.toString();
			}

			/**
			 * @see org.eclipse.dltk.debug.core.model.ComplexScriptType#formatDetails(org.eclipse.dltk.debug.core.model.IScriptValue)
			 */
			@Override
			public String formatDetails(IScriptValue value) {
				StringBuffer sb = new StringBuffer();
				sb.append(value.getRawValue());
				String id = value.getInstanceId();
				if (id != null) {
					sb.append(" (id = " + id + ")");
				}
				try {
					IVariable[] variables = value.getVariables();
					if (variables.length > 0) {
						sb.append(" {");
						for (int i = 0; i < variables.length; i++) {
							sb.append(variables[i].getName());
							sb.append(":");
							sb.append(variables[i].getValue().getValueString());
							sb.append(",");
						}
						sb.setLength(sb.length() - 1);
						sb.append("}");
					}
				} catch (DebugException ex) {
					DLTKDebugPlugin.logWarning(
							"error creating variable details", ex);
				}
				return sb.toString();
			}
		};
	}
}