package com.lausdahl.ast.creator.definitions;

import java.util.List;
import java.util.Set;

import com.lausdahl.ast.creator.java.definitions.JavaName;
import com.lausdahl.ast.creator.methods.Method;

public interface IInterfaceDefinition
{

	public abstract JavaName getName();

	public abstract Set<String> getImports();
	
	Set<IInterfaceDefinition> getSuperDefs();

	public abstract boolean isFinal();

	public abstract boolean isAbstract();
	
	public void setFinal(boolean isFinal);
	
	public void setAbstract(boolean isAbstract);

	public abstract String getJavaSourceCode(StringBuilder sb);

	public abstract String getVdmSourceCode(StringBuilder sb);
	
	public abstract void setTag(String tag);
	
	public abstract String getTag();
	
	public abstract void setGenericArguments(List<String> arguments);
	
	public abstract List<String> getGenericArguments();
	
	public abstract void setAnnotation(String annotation);
	
	public abstract List<Method> getMethods();
	
	public abstract void addMethod(Method m);
	
	public Set<Method> getMethod(String name);
	
	public String getGenericsString();
	
	public static final String copurightHeader ="/*******************************************************************************\n"+
 "* Copyright (c) 2009, 2011 Overture Team and others.\n"+
 "*\n"+
 "* Overture is free software: you can redistribute it and/or modify\n"+
 "* it under the terms of the GNU General Public License as published by\n"+
 "* the Free Software Foundation, either version 3 of the License, or\n"+
 "* (at your option) any later version.\n"+
 "*\n"+
 "* Overture is distributed in the hope that it will be useful,\n"+
 "* but WITHOUT ANY WARRANTY; without even the implied warranty of\n"+
 "* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"+
 "* GNU General Public License for more details.\n"+
 "*\n"+
 "* You should have received a copy of the GNU General Public License\n"+
 "* along with Overture.  If not, see <http://www.gnu.org/licenses/>.\n"+
 "*\n"+ 	
 "* The Overture Tool web-site: http://overturetool.org/\n"+
 "*******************************************************************************/\n";

}