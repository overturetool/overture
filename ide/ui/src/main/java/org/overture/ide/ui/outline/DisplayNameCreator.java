/*
 * #%~
 * org.overture.ide.ui
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.ui.outline;

import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.PType;

public class DisplayNameCreator
{

	public static String getDisplayName(Object element)
	{
		try
		{
			StringBuilder sb = new StringBuilder();
			if (element instanceof SClassDefinition)
				return ((SClassDefinition) element).getName().getName();
			else if (element instanceof AModuleModules)
				return ((AModuleModules) element).getName().getName();
			else if (element instanceof PDefinition)
			{
				// sb.append(((Definition) element).name.name);
				if (element instanceof SOperationDefinition)
				{

					SOperationDefinition def = (SOperationDefinition) element;

					sb.append(def.getName().getName());
					if (def.getType() instanceof AOperationType)
					{
						AOperationType type = (AOperationType) def.getType();
						if (type.getParameters().size() == 0)
						{
							sb.append("() ");
						} else
						{
							sb.append("(");
							int i = 0;
							while (i < type.getParameters().size() - 1)
							{
								PType definition = (PType) type.getParameters().get(i);
								sb.append(getSimpleTypeString(definition)
										+ ", ");

								i++;
							}
							PType definition = (PType) type.getParameters().get(i);
							sb.append(getSimpleTypeString(definition) + ")");
						}
					}
				} else if (element instanceof SFunctionDefinition)
				{
					SFunctionDefinition def = (SFunctionDefinition) element;

					if (def.getType() instanceof AFunctionType)
					{
						sb.append(def.getName().getName());
						sb.append(getFunctionTypeString((AFunctionType) def.getType()));
					}

				}
			}

			return sb.toString();
		} catch (Exception e)
		{
			e.printStackTrace();
			return "UNRESOLVED_NAME";
		}
	}

	public static String getFunctionTypeString(AFunctionType type)
	{
		StringBuffer sb = new StringBuffer();
		if (type.getParameters().size() == 0)
		{
			sb.append("() ");
		} else
		{
			sb.append("(");
			int i = 0;
			while (i < type.getParameters().size() - 1)
			{
				PType definition = (PType) type.getParameters().get(i);
				sb.append(getSimpleTypeString(definition) + ", ");

				i++;
			}
			PType definition = (PType) type.getParameters().get(i);
			sb.append(getSimpleTypeString(definition) + ")");
			if (type.getResult() instanceof AFunctionType)
			{
				sb.append(getFunctionTypeString((AFunctionType) type.getResult()));
			}
		}
		return sb.toString();
	}

	public static String print(PPattern pattern)
	{

		StringBuilder sb = new StringBuilder();

		if (pattern instanceof AIdentifierPattern)
			sb.append(((AIdentifierPattern) pattern).getName());
		else if (pattern instanceof AIdentifierPattern)
		{
			ARecordPattern recordPattern = (ARecordPattern) pattern;
			sb.append((recordPattern).getTypename());

		} else if (pattern instanceof AIgnorePattern)
			sb.append("-");
		else
			sb.append(pattern.toString());

		return sb.toString();
	}

	private static String processUnresolved(PType definition)
	{

		String defString = definition.toString();

		if (defString.contains("unresolved "))
		{
			defString = defString.replace("(", "");
			defString = defString.replace(")", "");
			defString = defString.replace("unresolved ", "");
			return defString;
		}
		return definition.toString();
	}

	private static String getSimpleTypeString(PType type)
	{
		String typeName = processUnresolved(type);
		typeName = typeName.replace("(", "");
		typeName = typeName.replace(")", "");
		return typeName;
	}
}
