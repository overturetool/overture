/*
 * #%~
 * VDM Code Generator
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
package org.overture.codegen.vdm2java;

public interface IJavaConstants
{
	public static final String THROWS = "throws";

	public static final String[] RESERVED_WORDS = {
			// Java Keywords
			"abstract", "continue", "for", "new", "switch", "assert", "default",
			"goto", "package", "synchronized", "boolean", "do", "if", "private",
			"this", "break", "double", "implements", "protected", "throw",
			"byte", "else", "import", "public", "throws", "case", "enum",
			"instanceof", "return", "transient", "catch", "extends", "int",
			"short", "try", "char", "final", "interface", "static", "void",
			"class", "finally", "long", "strictfp", "volatile", "const",
			"float", "native", "super", "while" };

	public static final String PACKAGE_JAVA_KEYWORD = "package";

	public static final String JAVA_FILE_EXTENSION = ".java";

	public static final String PUBLIC = "public";
	public static final String PRIVATE = "private";

	public static final String INT = "int";
	public static final String EQUALS = "equals";
	public static final String BOOLEAN = "boolean";
	public static final String HASH_CODE = "hashCode";
	public static final String TO_STRING = "toString";
}
