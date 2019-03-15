/*
 * #%~
 * The Overture Abstract Syntax Tree
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
package org.overture.ast.assistant;

import org.overture.ast.analysis.intf.IAnswer;
import org.overture.ast.assistant.definition.PAccessSpecifierAssistant;
import org.overture.ast.assistant.definition.PDefinitionAssistant;
import org.overture.ast.assistant.pattern.PPatternAssistant;
import org.overture.ast.assistant.type.AUnionTypeAssistant;
import org.overture.ast.assistant.type.PTypeAssistant;
import org.overture.ast.assistant.type.SNumericBasicTypeAssistant;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.types.SNumericBasicType;

/**
 * The {@link IAstAssistantFactory} defines the main interface for assistant factories. Assistants are responsible for
 * providing generic functionality that is required on multiple occasions and that (for various reasons) cannot be
 * implemented directly in the visitors. <br>
 * <br>
 * Factories are responsible for providing users with extensible access to the assistant functionality. Each module that
 * needs assistants should create a factory and access a visitor by calling factory.createXAssistant where X is the node
 * type that the functionality acts upon. <br>
 * <br>
 * All factories should subclass either this interface or one of its subclasses. These classes are in a straight
 * hierarchy chain as follows: {@link IAstAssistantFactory} <- ITypeCheckerAssistantFactory <- *PluginLevelFactory <br>
 * <br>
 * By following this inheritance chain, at every level a single factory can provide all assistants. Extensions should
 * hook into the hierarchy by subclassing their extension superclass, if one extist. Otherwise, subclass their Overture
 * counterpart.
 * 
 * @author ldc, kel, gkanos, pvj
 */
public interface IAstAssistantFactory
{
	/**
	 * Creates a new {@link PAccessSpecifierAssistant}. This assistant provides functionality to check if an
	 * PAccessSpecifier is a "static", "public" specifier, etc.
	 * 
	 * @return the p access specifier assistant
	 */
	PAccessSpecifierAssistant createPAccessSpecifierAssistant();

	/**
	 * Creates a new {@link PDefinitionAssistant}. This assistant provides functionality for getting the name of a
	 * definition and for setting its internal class definition.
	 * 
	 * @return the p definition assistant
	 */
	PDefinitionAssistant createPDefinitionAssistant();

	/**
	 * Creates a new {@link PPatternAssistant}. This assistant provides functionality for extracting variable names from
	 * the pattern.
	 * @param fromModule TODO
	 * 
	 * @return the p pattern assistant
	 */
	PPatternAssistant createPPatternAssistant(String fromModule);

	/**
	 * Creates a new {@link ABracketTypeAssistant}. This assistant does nothing and is probably a candidate for
	 * deletion.
	 * 
	 * @return the a bracket type assistant
	 */
	// @Deprecated
	// ABracketTypeAssistant createABracketTypeAssistant();

	/**
	 * Creates a new ANamedInvariantTypeAssistant. This assistant does nothing and is probably a candidate for deletion.
	 * 
	 * @return the a named invariant type assistant
	 */
	// @Deprecated
	// ANamedInvariantTypeAssistant createANamedInvariantTypeAssistant();

	/**
	 * Creates a new {@link AOptionalTypeAssistant}. This assistant does nothing and is probably a candidate for
	 * deletion.
	 * 
	 * @return the a optional type assistant
	 */
	// @Deprecated
	// AOptionalTypeAssistant createAOptionalTypeAssistant();

	/**
	 * Creates a new {@link AParameterTypeAssistant}. This assistant does nothing and is probably a candidate for
	 * deletion.
	 * 
	 * @return the a parameter type assistant
	 */
	// @Deprecated
	// AParameterTypeAssistant createAParameterTypeAssistant();

	/**
	 * Creates a new {@link AUnionTypeAssistant}. This assistant provides functionality for expanding a Union type and
	 * checking if it's numeric.
	 * 
	 * @return the a union type assistant
	 */
	AUnionTypeAssistant createAUnionTypeAssistant();

	/**
	 * Creates a new {@link AUnknownTypeAssistant}. This assistant does nothing and is probably a candidate for
	 * deletion.
	 * 
	 * @return the a unknown type assistant
	 */
	// @Deprecated
	// AUnknownTypeAssistant createAUnknownTypeAssistant();

	/**
	 * Creates a new {@link PTypeAssistant}. This assistant provides functionality to get a type's name and check if a
	 * type is numeric.
	 * 
	 * @return the p type assistant
	 */
	PTypeAssistant createPTypeAssistant();

	/**
	 * Creates a new {@link SNumericBasicTypeAssistant}. This assistant provides functionality to get the weight of a
	 * numeric type.
	 * 
	 * @return the s numeric basic type assistant
	 */
	SNumericBasicTypeAssistant createSNumericBasicTypeAssistant();

	/**
	 * Returns the visitor for locating all variable names in a pattern.
	 * @param fromModule TODO
	 * 
	 * @return the all variable name locator
	 */
	IAnswer<LexNameList> getAllVariableNameLocator(String fromModule);

	/**
	 * Returns the visitor to check if a type is numeric. Probably needs a better name.
	 * @param fromModule TODO
	 * 
	 * @return the numeric finder
	 */
	IAnswer<Boolean> getNumericFinder(String fromModule);

	/**
	 * Returns the visitor that, given a numeric type, gets the the actual {@link SNumericBasicType} associated with it.
	 * @param fromModule TODO
	 * 
	 * @return the numeric basis checker
	 */
	IAnswer<SNumericBasicType> getNumericBasisChecker(String fromModule);

	/**
	 * Return the visitor that gets the hashcode of a type.
	 * 
	 * @return the hash checker
	 */
	IAnswer<Integer> getHashChecker();
}
