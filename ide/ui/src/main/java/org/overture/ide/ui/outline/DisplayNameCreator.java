package org.overture.ide.ui.outline;

import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;
import org.overturetool.vdmj.definitions.LocalDefinition;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.patterns.IdentifierPattern;
import org.overturetool.vdmj.patterns.IgnorePattern;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.patterns.PatternList;
import org.overturetool.vdmj.patterns.RecordPattern;
import org.overturetool.vdmj.types.Type;

public class DisplayNameCreator {

	public static String getDisplayName(Object element)
	{
		try
		{
			StringBuilder sb = new StringBuilder();
			if (element instanceof ClassDefinition)
				return ((ClassDefinition) element).name.name;
			else if (element instanceof Module)
				return ((Module) element).name.name;
			else if (element instanceof Definition)
			{
				sb.append(((Definition) element).name.name);
				if (element instanceof ExplicitOperationDefinition)
				{
					ExplicitOperationDefinition def = (ExplicitOperationDefinition) element;
					sb.append("(");
					for (int i = 0; i < def.parameterPatterns.size(); i++)
					{
						if (def.parameterPatterns.size() <= i)
							sb.append("UNRESOLVED");
						else
						{
							if (def.paramDefinitions != null
									&& def.paramDefinitions.get(i) instanceof LocalDefinition)
								sb.append(((LocalDefinition) def.paramDefinitions.get(i)).type
										+ " " + def.parameterPatterns.get(i));
							else if (def.paramDefinitions != null)
								sb.append(def.paramDefinitions.get(i).name.name
										+ " " + def.parameterPatterns.get(i));
							else 	
							{
								
								if(def.type!=null)
								{
									StringBuilder parametersSb= new StringBuilder();
									for(Type t : def.type.parameters)
									{
										sb.append(t.toString().replace("unresolved ", "")+ " %s");
									}
									List<String> patterns = new Vector<String>();
									for(Pattern pattern: def.parameterPatterns)
									{
										patterns.add(pattern.toString());
									}
									sb.append(String.format(parametersSb.toString(),patterns));
									
								}
								else
									sb.append("UNRESOLVED");
							}
						}
						if (i + 1 < def.parameterPatterns.size())
							sb.append(", ");
					}
					sb.append(")");
				} else if (element instanceof ExplicitFunctionDefinition)
				{
					ExplicitFunctionDefinition def = (ExplicitFunctionDefinition) element;
					sb.append("(");
					for (int i = 0; i < def.paramPatternList.size(); i++)
					{
						if (def.paramPatternList.get(i) instanceof PatternList)
						{
							PatternList patternList = def.paramPatternList.get(i);

							for (int j = 0; j < patternList.size(); j++)
							{
								Pattern pattern=patternList.get(j); 
								sb.append(def.type.parameters.get(j)
										.toString()
										.replace("unresolved ", "")
										+ " ");
								sb.append(	print(pattern));
								if (j + 1 < patternList.size())
									sb.append(", ");
							}
						}
					}
					sb.append(")");
				}

			}

			return sb.toString();
		} catch (Exception e)
		{
			e.printStackTrace();
			return "UNRESOLVED_NAME";
		}
		
		
	}
	public static String print(Pattern pattern)
		{
			
			StringBuilder sb = new StringBuilder();	

				if (pattern instanceof IdentifierPattern)
					sb.append(((IdentifierPattern) pattern).name);
				else if (pattern instanceof RecordPattern)
				{
					RecordPattern recordPattern =(RecordPattern) pattern; 
					sb.append((recordPattern).typename);
					
					
				}
				else if (pattern instanceof IgnorePattern)
					sb.append("-");
				else
					sb.append(pattern.toString());
			
			return sb.toString();
		}
}
