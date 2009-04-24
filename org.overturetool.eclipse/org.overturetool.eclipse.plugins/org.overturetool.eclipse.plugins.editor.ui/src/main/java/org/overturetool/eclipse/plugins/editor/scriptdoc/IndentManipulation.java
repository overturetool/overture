package org.overturetool.eclipse.plugins.editor.scriptdoc;


/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.eclipse.dltk.compiler.util.ScannerHelper;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultLineTracker;
import org.eclipse.jface.text.ILineTracker;
import org.eclipse.jface.text.IRegion;
import org.eclipse.text.edits.ReplaceEdit;


/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/


/**
 * Alignment management
 * 
 * @since 2.1
 */
 class Alignment {

	// name of alignment
	public String name;
	
	// link to enclosing alignment
	public Alignment enclosing;
	 
	// start location of this alignment
	
	// indentation management
	public int fragmentIndex;
	public int fragmentCount;
	public int[] fragmentIndentations;
	public boolean needRedoColumnAlignment;

	// chunk management
	public int chunkStartIndex;
	public int chunkKind;

	// break management	
	public int originalIndentationLevel;
	public int breakIndentationLevel;
	public int shiftBreakIndentationLevel;
	public int[] fragmentBreaks;
	public boolean wasSplit;
	
	/* 
	 * Alignment modes
	 */
	public static final int M_FORCE = 1; // if bit set, then alignment will be non-optional (default is optional)
	public static final int M_INDENT_ON_COLUMN = 2; // if bit set, broken fragments will be aligned on current location column (default is to break at current indentation level)
	public static final int	M_INDENT_BY_ONE = 4; // if bit set, broken fragments will be indented one level below current (not using continuation indentation)

	// split modes can be combined either with M_FORCE or M_INDENT_ON_COLUMN
	
	/** foobar(#fragment1, #fragment2, <ul>
	 *  <li>    #fragment3, #fragment4 </li>
	 * </ul>
	 */
	public static final int M_COMPACT_SPLIT = 16; // fill each line with all possible fragments

	/** foobar(<ul>
	 * <li>    #fragment1, #fragment2,  </li>
	 * <li>     #fragment5, #fragment4, </li>
	 * </ul>
	 */
	public static final int M_COMPACT_FIRST_BREAK_SPLIT = 32; //  compact mode, but will first try to break before first fragment

	/** foobar(<ul>
	 * <li>     #fragment1,  </li>
	 * <li>     #fragment2,  </li>
	 * <li>     #fragment3 </li>
	 * <li>     #fragment4,  </li>
	 * </ul>
	 */
	public static final int M_ONE_PER_LINE_SPLIT = 32+16; // one fragment per line

	/** 
	 * foobar(<ul>
	 * <li>     #fragment1,  </li>
	 * <li>        #fragment2,  </li>
	 * <li>        #fragment3 </li>
	 * <li>        #fragment4,  </li>
	 * </ul>
	 */ 
	public static final int M_NEXT_SHIFTED_SPLIT = 64; // one fragment per line, subsequent are indented further

	/** foobar(#fragment1, <ul>
	 * <li>      #fragment2,  </li>
	 * <li>      #fragment3 </li>
	 * <li>      #fragment4,  </li>
	 * </ul>
	 */
	public static final int M_NEXT_PER_LINE_SPLIT = 64+16; // one per line, except first fragment (if possible)

	//64+32
	//64+32+16
	
	// mode controlling column alignments
	/** 
	 * <table BORDER COLS=4 WIDTH="100%" >
	 * <tr><td>#fragment1A</td>            <td>#fragment2A</td>       <td>#fragment3A</td>  <td>#very-long-fragment4A</td></tr>
	 * <tr><td>#fragment1B</td>            <td>#long-fragment2B</td>  <td>#fragment3B</td>  <td>#fragment4B</td></tr>
	 * <tr><td>#very-long-fragment1C</td>  <td>#fragment2C</td>       <td>#fragment3C</td>  <td>#fragment4C</td></tr>
	 * </table>
	 */
	public static final int M_MULTICOLUMN = 256; // fragments are on same line, but multiple line of fragments will be aligned vertically
	
	public static final int M_NO_ALIGNMENT = 0;
	
	public int mode;
	
	public static final int SPLIT_MASK = M_ONE_PER_LINE_SPLIT | M_NEXT_SHIFTED_SPLIT | M_COMPACT_SPLIT | M_COMPACT_FIRST_BREAK_SPLIT | M_NEXT_PER_LINE_SPLIT;

	// alignment tie-break rules - when split is needed, will decide whether innermost/outermost alignment is to be chosen
	public static final int R_OUTERMOST = 1;
	public static final int R_INNERMOST = 2;
	public int tieBreakRule;
	
	// alignment effects on a per fragment basis
	public static final int NONE = 0;
	public static final int BREAK = 1;
	
	// chunk kind
	public static final int CHUNK_FIELD = 1;
	public static final int CHUNK_METHOD = 2;
	public static final int CHUNK_TYPE = 3;
	public static final int CHUNK_ENUM = 4;

	// location to align and break on.
	

	
		
	public boolean couldBreak(){
		int i;
		switch(mode & SPLIT_MASK){

			/*  # aligned fragment
			 *  foo(
			 *     #AAAAA, #BBBBB,
			 *     #CCCC);
			 */
			case M_COMPACT_FIRST_BREAK_SPLIT : 
				if (this.fragmentBreaks[0] == NONE) {
					this.fragmentBreaks[0] = BREAK;
					this.fragmentIndentations[0] = this.breakIndentationLevel;
					return wasSplit = true;
				}
				i = this.fragmentIndex;
				do {
					if (this.fragmentBreaks[i] == NONE) {
						this.fragmentBreaks[i] = BREAK;
						this.fragmentIndentations[i] = this.breakIndentationLevel;
						return wasSplit = true;
					}
				} while (--i >= 0);
				break;
			/*  # aligned fragment
			 *  foo(#AAAAA, #BBBBB,
			 *     #CCCC);
			 */
			case M_COMPACT_SPLIT : 
				i = this.fragmentIndex;
				do {
					if (this.fragmentBreaks[i] == NONE) {
						this.fragmentBreaks[i] = BREAK;
						this.fragmentIndentations[i] = this.breakIndentationLevel;						
						return wasSplit = true;
					}
				} while (--i >= 0);
				break;

			/*  # aligned fragment
			 *  foo(
			 *      #AAAAA,
			 *          #BBBBB,
			 *          #CCCC);
			 */
			case M_NEXT_SHIFTED_SPLIT :
				if (this.fragmentBreaks[0] == NONE) {
					this.fragmentBreaks[0] = BREAK;					
					this.fragmentIndentations[0] = this.breakIndentationLevel;
					for (i = 1; i < this.fragmentCount; i++){
						this.fragmentBreaks[i] = BREAK;
						this.fragmentIndentations[i] = this.shiftBreakIndentationLevel;
					}
					return wasSplit = true;
				}
				break;
				
			/*  # aligned fragment
			 *  foo(
			 *      #AAAAA,
			 *      #BBBBB,
			 *      #CCCC);
			 */
			case M_ONE_PER_LINE_SPLIT :
				if (this.fragmentBreaks[0] == NONE) {
					for (i = 0; i < this.fragmentCount; i++){
						this.fragmentBreaks[i] = BREAK;
						this.fragmentIndentations[i] = this.breakIndentationLevel;
					}
					return wasSplit = true;
				}
			/*  # aligned fragment
			 *  foo(#AAAAA,
			 *      #BBBBB,
			 *      #CCCC);
			 */
			case M_NEXT_PER_LINE_SPLIT : 
				if (this.fragmentBreaks[0] == NONE) {
					if (this.fragmentCount > 1
							&& this.fragmentBreaks[1] == NONE) {
						if ((this.mode & M_INDENT_ON_COLUMN) != 0) {
							this.fragmentIndentations[0] = this.breakIndentationLevel;
						}
						for (i = 1; i < this.fragmentCount; i++) {
							this.fragmentBreaks[i] = BREAK;
							this.fragmentIndentations[i] = this.breakIndentationLevel;
						}
						return wasSplit = true;
					}
				}
				break;
		}		
		return false; // cannot split better
	}
	
	public Alignment getAlignment(String targetName) {

		if (targetName.equals(this.name)) return this;
		if (this.enclosing == null) return null;
		
		return this.enclosing.getAlignment(targetName);
	}
		
	// perform alignment effect for current fragment
	public void performFragmentEffect(){
		if ((this.mode & M_MULTICOLUMN) == 0) {
			switch(this.mode & SPLIT_MASK) {
				case Alignment.M_COMPACT_SPLIT :
				case Alignment.M_COMPACT_FIRST_BREAK_SPLIT :
				case Alignment.M_NEXT_PER_LINE_SPLIT :
				case Alignment.M_NEXT_SHIFTED_SPLIT :
				case Alignment.M_ONE_PER_LINE_SPLIT :
					break;
				default:
					return;
			}
		}
		
		
	}					

	// reset fragment indentation/break status	
	public void reset() {

		if (fragmentCount > 0){
			this.fragmentIndentations = new int[this.fragmentCount];
			this.fragmentBreaks = new int[this.fragmentCount];
		}

		// check for forced alignments
		if ((mode & M_FORCE) != 0) {
			couldBreak();
		}		
	}		

	public void toFragmentsString(StringBuffer buffer){
		// default implementation
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer(10);
		buffer
			.append(getClass().getName())
			.append(':')
			.append("<name: ")	//$NON-NLS-1$
			.append(this.name)
			.append(">");	//$NON-NLS-1$
		if (this.enclosing != null) {
			buffer
				.append("<enclosingName: ")	//$NON-NLS-1$
				.append(this.enclosing.name)
				.append('>');
		}
		buffer.append('\n');	

		for (int i = 0; i < this.fragmentCount; i++){
			buffer
				.append(" - fragment ")	//$NON-NLS-1$
				.append(i)
				.append(": ")	//$NON-NLS-1$
				.append("<break: ")	//$NON-NLS-1$
				.append(this.fragmentBreaks[i] > 0 ? "YES" : "NO")	//$NON-NLS-1$	//$NON-NLS-2$
				.append(">")	//$NON-NLS-1$
				.append("<indent: ")	//$NON-NLS-1$
				.append(this.fragmentIndentations[i])
				.append(">\n");	//$NON-NLS-1$
		}
		buffer.append('\n');	
		return buffer.toString();
	}
	
	public void update() {
		for (int i = 1; i < this.fragmentCount; i++){
		    if (this.fragmentBreaks[i] == BREAK) {
		        this.fragmentIndentations[i] = this.breakIndentationLevel;
		    }
		}
	}

	public boolean isWrapped() {
		for (int i = 0, max = this.fragmentCount; i < max; i++) {
			if (this.fragmentBreaks[i] == BREAK) {
				return true;
			}
		}
		return false;
	}
}

/**
 * Constants used to set up the options of the code formatter.
 * <p>
 * This class is not intended to be instantiated or subclassed by clients.
 * </p>
 * 
 * @since 3.0
 */
class DefaultCodeFormatterConstants {

	/**
	 * <pre>
	 * FORMATTER / Value to set a brace location at the end of a line.
	 * </pre>
	 * @see #FORMATTER_BRACE_POSITION_FOR_ANONYMOUS_TYPE_DECLARATION
	 * @see #FORMATTER_BRACE_POSITION_FOR_ARRAY_INITIALIZER
	 * @see #FORMATTER_BRACE_POSITION_FOR_BLOCK
	 * @see #FORMATTER_BRACE_POSITION_FOR_CONSTRUCTOR_DECLARATION
 	 * @see #FORMATTER_BRACE_POSITION_FOR_METHOD_DECLARATION
 	 * @see #FORMATTER_BRACE_POSITION_FOR_SWITCH
	 * @see #FORMATTER_BRACE_POSITION_FOR_TYPE_DECLARATION
	 * @since 3.0
	 */
	public static final String END_OF_LINE = "end_of_line";	//$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Value to set an option to false.
	 * </pre>
	 * @since 3.0
	 */
	public static final String FALSE = "false"; //$NON-NLS-1$
	
	/**
	 * <pre>
	 * FORMATTER / Option to align type members of a type declaration on column
	 *     - option id:         "org.eclipse.jdt.core.formatter.formatter.align_type_members_on_columns"
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           FALSE
	 * </pre>
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGN_TYPE_MEMBERS_ON_COLUMNS = DLTKCore.PLUGIN_ID + ".formatter.align_type_members_on_columns";	 //$NON-NLS-1$
	
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of arguments in allocation expression
	 *     - option id:         "org.eclipse.jdt.core.formatter.alignment_for_arguments_in_allocation_expression"
	 *     - possible values:   values returned by <code>createAlignmentValue(boolean, int, int)</code> call
	 *     - default:           createAlignmentValue(false, WRAP_COMPACT, INDENT_DEFAULT)
	 * </pre>
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_ARGUMENTS_IN_ALLOCATION_EXPRESSION = DLTKCore.PLUGIN_ID + ".formatter.alignment_for_arguments_in_allocation_expression";	 //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of arguments in enum constant
	 *     - option id:         "org.eclipse.jdt.core.formatter.alignment_for_arguments_in_enum_constant"
	 *     - possible values:   values returned by <code>createAlignmentValue(boolean, int, int)</code> call
	 *     - default:           createAlignmentValue(false, WRAP_COMPACT, INDENT_DEFAULT)
	 * </pre>
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.1
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_ARGUMENTS_IN_ENUM_CONSTANT = DLTKCore.PLUGIN_ID + ".formatter.alignment_for_arguments_in_enum_constant";	 //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of arguments in explicit constructor call
	 *     - option id:         "org.eclipse.jdt.core.formatter.alignment_for_arguments_in_explicit_constructor_call"
	 *     - possible values:   values returned by <code>createAlignmentValue(boolean, int, int)</code> call
	 *     - default:           createAlignmentValue(false, WRAP_COMPACT, INDENT_DEFAULT)
	 * </pre>
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_ARGUMENTS_IN_EXPLICIT_CONSTRUCTOR_CALL = DLTKCore.PLUGIN_ID + ".formatter.alignment_for_arguments_in_explicit_constructor_call";	 //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of arguments in method invocation
	 *     - option id:         "org.eclipse.jdt.core.formatter.alignment_for_arguments_in_method_invocation"
	 *     - possible values:   values returned by <code>createAlignmentValue(boolean, int, int)</code> call
	 *     - default:           createAlignmentValue(false, WRAP_COMPACT, INDENT_DEFAULT)
	 * </pre>
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_ARGUMENTS_IN_METHOD_INVOCATION = DLTKCore.PLUGIN_ID + ".formatter.alignment_for_arguments_in_method_invocation";	 //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of arguments in qualified allocation expression
	 *     - option id:         "org.eclipse.jdt.core.formatter.alignment_for_arguments_in_qualified_allocation_expression"
	 *     - possible values:   values returned by <code>createAlignmentValue(boolean, int, int)</code> call
	 *     - default:           createAlignmentValue(false, WRAP_COMPACT, INDENT_DEFAULT)
	 * </pre>
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_ARGUMENTS_IN_QUALIFIED_ALLOCATION_EXPRESSION = DLTKCore.PLUGIN_ID + ".formatter.alignment_for_arguments_in_qualified_allocation_expression";	 //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of assignment
	 *     - option id:         "org.eclipse.jdt.core.formatter.alignment_for_assignment"
	 *     - possible values:   values returned by <code>createAlignmentValue(boolean, int, int)</code> call
	 *     - default:           createAlignmentValue(false, M_NO_ALIGNMENT, INDENT_DEFAULT)
	 * </pre>
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.2
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_ASSIGNMENT  = DLTKCore.PLUGIN_ID + ".formatter.alignment_for_assignment";	 //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of binary expression
	 *     - option id:         "org.eclipse.jdt.core.formatter.alignment_for_binary_expression"
	 *     - possible values:   values returned by <code>createAlignmentValue(boolean, int, int)</code> call
	 *     - default:           createAlignmentValue(false, WRAP_COMPACT, INDENT_DEFAULT)
	 * </pre>
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_BINARY_EXPRESSION = DLTKCore.PLUGIN_ID + ".formatter.alignment_for_binary_expression";	 //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of compact if
	 *     - option id:         "org.eclipse.jdt.core.formatter.alignment_for_compact_if"
	 *     - possible values:   values returned by <code>createAlignmentValue(boolean, int, int)</code> call
	 *     - default:           createAlignmentValue(false, WRAP_ONE_PER_LINE, INDENT_BY_ONE)
	 * </pre>
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_COMPACT_IF = DLTKCore.PLUGIN_ID + ".formatter.alignment_for_compact_if";	 //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of conditional expression
	 *     - option id:         "org.eclipse.jdt.core.formatter.alignment_for_conditional_expression"
	 *     - possible values:   values returned by <code>createAlignmentValue(boolean, int, int)</code> call
	 *     - default:           createAlignmentValue(false, WRAP_ONE_PER_LINE, INDENT_DEFAULT)
	 * </pre>
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_CONDITIONAL_EXPRESSION = DLTKCore.PLUGIN_ID + ".formatter.alignment_for_conditional_expression";	 //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of enum constants
	 *     - option id:        "org.eclipse.jdt.core.formatter.alignment_for_enum_constants"
	 *     - possible values:  values returned by <code>createAlignmentValue(boolean, int, int)</code> call
	 *     - default:          createAlignmentValue(false, WRAP_NO_SPLIT, INDENT_DEFAULT)
	 * </pre>
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.1
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_ENUM_CONSTANTS = DLTKCore.PLUGIN_ID + ".formatter.alignment_for_enum_constants";	 //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of expressions in array initializer
	 *     - option id:         "org.eclipse.jdt.core.formatter.alignment_for_expressions_in_array_initializer"
	 *     - possible values:   values returned by <code>createAlignmentValue(boolean, int, int)</code> call
	 *     - default:           createAlignmentValue(false, WRAP_COMPACT, INDENT_DEFAULT)
	 * </pre>
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_EXPRESSIONS_IN_ARRAY_INITIALIZER = DLTKCore.PLUGIN_ID + ".formatter.alignment_for_expressions_in_array_initializer";	 //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of multiple fields
	 *     - option id:         "org.eclipse.jdt.core.formatter.alignment_for_multiple_fields"
	 *     - possible values:   values returned by <code>createAlignmentValue(boolean, int, int)</code> call
	 *     - default:           createAlignmentValue(false, WRAP_COMPACT, INDENT_DEFAULT)
	 * </pre>
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_MULTIPLE_FIELDS = DLTKCore.PLUGIN_ID + ".formatter.alignment_for_multiple_fields";//$NON-NLS-1$	
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of parameters in constructor declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.alignment_for_parameters_in_constructor_declaration"
	 *     - possible values:   values returned by <code>createAlignmentValue(boolean, int, int)</code> call
	 *     - default:           createAlignmentValue(false, WRAP_COMPACT, INDENT_DEFAULT)
	 * </pre>
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_PARAMETERS_IN_CONSTRUCTOR_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.alignment_for_parameters_in_constructor_declaration";	 //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of parameters in method declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.alignment_for_parameters_in_method_declaration"
	 *     - possible values:   values returned by <code>createAlignmentValue(boolean, int, int)</code> call
	 *     - default:           createAlignmentValue(false, WRAP_COMPACT, INDENT_DEFAULT)
	 * </pre>
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_PARAMETERS_IN_METHOD_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.alignment_for_parameters_in_method_declaration";	 //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of selector in method invocation
	 *     - option id:         "org.eclipse.jdt.core.formatter.alignment_for_selector_in_method_invocation"
	 *     - possible values:   values returned by <code>createAlignmentValue(boolean, int, int)</code> call
	 *     - default:           createAlignmentValue(false, WRAP_COMPACT, INDENT_DEFAULT)
	 * </pre>
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_SELECTOR_IN_METHOD_INVOCATION = DLTKCore.PLUGIN_ID + ".formatter.alignment_for_selector_in_method_invocation";	 //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of superclass in type declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.alignment_for_superclass_in_type_declaration"
	 *     - possible values:   values returned by <code>createAlignmentValue(boolean, int, int)</code> call
	 *     - default:           createAlignmentValue(false, WRAP_NEXT_SHIFTED, INDENT_DEFAULT)
	 * </pre>
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_SUPERCLASS_IN_TYPE_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.alignment_for_superclass_in_type_declaration";	 //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of superinterfaces in enum declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.alignment_for_superinterfaces_in_enum_declaration"
	 *     - possible values:   values returned by <code>createAlignmentValue(boolean, int, int)</code> call
	 *     - default:           createAlignmentValue(false, WRAP_COMPACT, INDENT_DEFAULT)
	 * </pre>
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.1
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_SUPERINTERFACES_IN_ENUM_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.alignment_for_superinterfaces_in_enum_declaration";	 //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of superinterfaces in type declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.alignment_for_superinterfaces_in_type_declaration"
	 *     - possible values:   values returned by <code>createAlignmentValue(boolean, int, int)</code> call
	 *     - default:           createAlignmentValue(false, WRAP_COMPACT, INDENT_DEFAULT)
	 * </pre>
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_SUPERINTERFACES_IN_TYPE_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.alignment_for_superinterfaces_in_type_declaration";	 //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of throws clause in constructor declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.alignment_for_throws_clause_in_constructor_declaration"
	 *     - possible values:   values returned by <code>createAlignmentValue(boolean, int, int)</code> call
	 *     - default:           createAlignmentValue(false, WRAP_COMPACT, INDENT_DEFAULT)
	 * </pre>
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_THROWS_CLAUSE_IN_CONSTRUCTOR_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.alignment_for_throws_clause_in_constructor_declaration";	 //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option for alignment of throws clause in method declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.alignment_for_throws_clause_in_method_declaration"
	 *     - possible values:   values returned by <code>createAlignmentValue(boolean, int, int)</code> call
	 *     - default:           createAlignmentValue(false, WRAP_COMPACT, INDENT_DEFAULT)
	 * </pre>
	 * @see #createAlignmentValue(boolean, int, int)
	 * @since 3.0
	 */
	public static final String FORMATTER_ALIGNMENT_FOR_THROWS_CLAUSE_IN_METHOD_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.alignment_for_throws_clause_in_method_declaration";	 //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to add blank lines after the imports declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.blank_lines_after_imports"
	 *     - possible values:   "&lt;n&gt;", where n is zero or a positive integer
	 *     - default:           "0"
	 * </pre>
	 * @since 3.0
	 */
	public static final String FORMATTER_BLANK_LINES_AFTER_IMPORTS = DLTKCore.PLUGIN_ID + ".formatter.blank_lines_after_imports";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to add blank lines after the package declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.blank_lines_after_package"
	 *     - possible values:   "&lt;n&gt;", where n is zero or a positive integer
	 *     - default:           "0"
	 * </pre>
	 * @since 3.0
	 */
	public static final String FORMATTER_BLANK_LINES_AFTER_PACKAGE = DLTKCore.PLUGIN_ID + ".formatter.blank_lines_after_package";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to add blank lines at the beginning of the method body
	 *     - option id:         "org.eclipse.jdt.core.formatter.number_of_blank_lines_at_beginning_of_method_body"
	 *     - possible values:   "&lt;n&gt;", where n is zero or a positive integer
	 *     - default:           "0"
	 * </pre>
	 * @since 3.0
	 */
	public static final String FORMATTER_BLANK_LINES_AT_BEGINNING_OF_METHOD_BODY = DLTKCore.PLUGIN_ID + ".formatter.number_of_blank_lines_at_beginning_of_method_body"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to add blank lines before a field declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.blank_lines_before_field"
	 *     - possible values:   "&lt;n&gt;", where n is zero or a positive integer
	 *     - default:           "0"
	 * </pre>
	 * @since 3.0
	 */
	public static final String FORMATTER_BLANK_LINES_BEFORE_FIELD = DLTKCore.PLUGIN_ID + ".formatter.blank_lines_before_field";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to add blank lines before the first class body declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.blank_lines_before_first_class_body_declaration"
	 *     - possible values:   "&lt;n&gt;", where n is zero or a positive integer
	 *     - default:           "0"
	 * </pre>
	 * @since 3.0
	 */
	public static final String FORMATTER_BLANK_LINES_BEFORE_FIRST_CLASS_BODY_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.blank_lines_before_first_class_body_declaration";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to add blank lines before the imports declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.blank_lines_before_imports"
	 *     - possible values:   "&lt;n&gt;", where n is zero or a positive integer
	 *     - default:           "0"
	 * </pre>
	 * @since 3.0
	 */
	public static final String FORMATTER_BLANK_LINES_BEFORE_IMPORTS = DLTKCore.PLUGIN_ID + ".formatter.blank_lines_before_imports";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to add blank lines before a member type declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.blank_lines_before_member_type"
	 *     - possible values:   "&lt;n&gt;", where n is zero or a positive integer
	 *     - default:           "0"
	 * </pre>
	 * @since 3.0
	 */
	public static final String FORMATTER_BLANK_LINES_BEFORE_MEMBER_TYPE = DLTKCore.PLUGIN_ID + ".formatter.blank_lines_before_member_type";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to add blank lines before a method declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.blank_lines_before_method"
	 *     - possible values:   "&lt;n&gt;", where n is zero or a positive integer
	 *     - default:           "0"
	 * </pre>
	 * @since 3.0
	 */
	public static final String FORMATTER_BLANK_LINES_BEFORE_METHOD = DLTKCore.PLUGIN_ID + ".formatter.blank_lines_before_method";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to add blank lines before a new chunk
	 *     - option id:         "org.eclipse.jdt.core.formatter.blank_lines_before_new_chunk"
	 *     - possible values:   "&lt;n&gt;", where n is zero or a positive integer
	 *     - default:           "0"
	 * </pre>
	 * @since 3.0
	 */
	public static final String FORMATTER_BLANK_LINES_BEFORE_NEW_CHUNK = DLTKCore.PLUGIN_ID + ".formatter.blank_lines_before_new_chunk";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to add blank lines before the package declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.blank_lines_before_package"
	 *     - possible values:   "&lt;n&gt;", where n is zero or a positive integer
	 *     - default:           "0"
	 * </pre>
	 * @since 3.0
	 */
	public static final String FORMATTER_BLANK_LINES_BEFORE_PACKAGE = DLTKCore.PLUGIN_ID + ".formatter.blank_lines_before_package";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to add blank lines between import groups
	 *     - option id:         "org.eclipse.jdt.core.formatter.blank_lines_between_import_groups"
	 *     - possible values:   "&lt;n&gt;", where n is zero or a positive integer
	 *     - default:           "1"
	 * </pre>
	 * Note: Import groups are defined once "Organize Import" operation has been executed. The code formatter itself
	 * doesn't define the import groups.
	 *
	 * @since 3.3
	 */
	public static final String FORMATTER_BLANK_LINES_BETWEEN_IMPORT_GROUPS = DLTKCore.PLUGIN_ID + ".formatter.blank_lines_between_import_groups";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to add blank lines between type declarations
	 *     - option id:         "org.eclipse.jdt.core.formatter.blank_lines_between_type_declarations"
	 *     - possible values:   "&lt;n&gt;", where n is zero or a positive integer
	 *     - default:           "0"
	 * </pre>
	 * @since 3.0
	 */
	public static final String FORMATTER_BLANK_LINES_BETWEEN_TYPE_DECLARATIONS = DLTKCore.PLUGIN_ID + ".formatter.blank_lines_between_type_declarations";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to position the braces of an annotation type declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.brace_position_for_annotation_type_declaration"
	 *     - possible values:   { END_OF_LINE, NEXT_LINE, NEXT_LINE_SHIFTED, NEXT_LINE_ON_WRAP }
	 *     - default:           END_OF_LINE
	 * </pre>
	 * @see #END_OF_LINE
	 * @see #NEXT_LINE
	 * @see #NEXT_LINE_SHIFTED
	 * @see #NEXT_LINE_ON_WRAP
	 * @since 3.1
	 */
	public static final String FORMATTER_BRACE_POSITION_FOR_ANNOTATION_TYPE_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.brace_position_for_annotation_type_declaration";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to position the braces of an anonymous type declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.brace_position_for_anonymous_type_declaration"
	 *     - possible values:   { END_OF_LINE, NEXT_LINE, NEXT_LINE_SHIFTED, NEXT_LINE_ON_WRAP }
	 *     - default:           END_OF_LINE
	 * </pre>
	 * @see #END_OF_LINE
	 * @see #NEXT_LINE
	 * @see #NEXT_LINE_SHIFTED
	 * @see #NEXT_LINE_ON_WRAP
	 * @since 3.0
	 */
	public static final String FORMATTER_BRACE_POSITION_FOR_ANONYMOUS_TYPE_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.brace_position_for_anonymous_type_declaration";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to position the braces of an array initializer
	 *     - option id:         "org.eclipse.jdt.core.formatter.brace_position_for_array_initializer"
	 *     - possible values:   { END_OF_LINE, NEXT_LINE, NEXT_LINE_SHIFTED, NEXT_LINE_ON_WRAP }
	 *     - default:           END_OF_LINE
	 * </pre>
	 * @see #END_OF_LINE
	 * @see #NEXT_LINE
	 * @see #NEXT_LINE_SHIFTED
	 * @see #NEXT_LINE_ON_WRAP
	 * @since 3.0
	 */
	public static final String FORMATTER_BRACE_POSITION_FOR_ARRAY_INITIALIZER = DLTKCore.PLUGIN_ID + ".formatter.brace_position_for_array_initializer";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to position the braces of a block
	 *     - option id:         "org.eclipse.jdt.core.formatter.brace_position_for_block"
	 *     - possible values:   { END_OF_LINE, NEXT_LINE, NEXT_LINE_SHIFTED, NEXT_LINE_ON_WRAP }
	 *     - default:           END_OF_LINE
	 * </pre>
	 * @see #END_OF_LINE
	 * @see #NEXT_LINE
	 * @see #NEXT_LINE_SHIFTED
	 * @see #NEXT_LINE_ON_WRAP
	 * @since 3.0
	 */
	public static final String FORMATTER_BRACE_POSITION_FOR_BLOCK = DLTKCore.PLUGIN_ID + ".formatter.brace_position_for_block";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to position the braces of a block in a case statement when the block is the first statement following
	 *             the case
	 *     - option id:         "org.eclipse.jdt.core.formatter.brace_position_for_block_in_case"
	 *     - possible values:   { END_OF_LINE, NEXT_LINE, NEXT_LINE_SHIFTED, NEXT_LINE_ON_WRAP }
	 *     - default:           END_OF_LINE
	 * </pre>
	 * @see #END_OF_LINE
	 * @see #NEXT_LINE
	 * @see #NEXT_LINE_SHIFTED
	 * @see #NEXT_LINE_ON_WRAP
	 * @since 3.0
	 */
	public static final String FORMATTER_BRACE_POSITION_FOR_BLOCK_IN_CASE = DLTKCore.PLUGIN_ID + ".formatter.brace_position_for_block_in_case";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to position the braces of a constructor declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.brace_position_for_constructor_declaration"
	 *     - possible values:   { END_OF_LINE, NEXT_LINE, NEXT_LINE_SHIFTED, NEXT_LINE_ON_WRAP }
	 *     - default:           END_OF_LINE
	 * </pre>
	 * @see #END_OF_LINE
	 * @see #NEXT_LINE
	 * @see #NEXT_LINE_SHIFTED
	 * @see #NEXT_LINE_ON_WRAP
	 * @since 3.0
	 */
	public static final String FORMATTER_BRACE_POSITION_FOR_CONSTRUCTOR_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.brace_position_for_constructor_declaration";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to position the braces of an enum constant
	 *     - option id:         "org.eclipse.jdt.core.formatter.brace_position_for_enum_constant"
	 *     - possible values:   { END_OF_LINE, NEXT_LINE, NEXT_LINE_SHIFTED, NEXT_LINE_ON_WRAP }
	 *     - default:           END_OF_LINE
	 * </pre>
	 * @see #END_OF_LINE
	 * @see #NEXT_LINE
	 * @see #NEXT_LINE_SHIFTED
	 * @see #NEXT_LINE_ON_WRAP
	 * @since 3.1
	 */
	public static final String FORMATTER_BRACE_POSITION_FOR_ENUM_CONSTANT = DLTKCore.PLUGIN_ID + ".formatter.brace_position_for_enum_constant";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to position the braces of an enum declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.brace_position_for_enum_declaration"
	 *     - possible values:   { END_OF_LINE, NEXT_LINE, NEXT_LINE_SHIFTED, NEXT_LINE_ON_WRAP }
	 *     - default:           END_OF_LINE
	 * </pre>
	 * @see #END_OF_LINE
	 * @see #NEXT_LINE
	 * @see #NEXT_LINE_SHIFTED
	 * @see #NEXT_LINE_ON_WRAP
	 * @since 3.1
	 */
	public static final String FORMATTER_BRACE_POSITION_FOR_ENUM_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.brace_position_for_enum_declaration";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to position the braces of a method declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.brace_position_for_method_declaration"
	 *     - possible values:   { END_OF_LINE, NEXT_LINE, NEXT_LINE_SHIFTED, NEXT_LINE_ON_WRAP }
	 *     - default:           END_OF_LINE
	 * </pre>
	 * @see #END_OF_LINE
	 * @see #NEXT_LINE
	 * @see #NEXT_LINE_SHIFTED
	 * @see #NEXT_LINE_ON_WRAP
	 * @since 3.0
	 */
	public static final String FORMATTER_BRACE_POSITION_FOR_METHOD_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.brace_position_for_method_declaration";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to position the braces of a switch statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.brace_position_for_switch"
	 *     - possible values:   { END_OF_LINE, NEXT_LINE, NEXT_LINE_SHIFTED, NEXT_LINE_ON_WRAP }
	 *     - default:           END_OF_LINE
	 * </pre>
	 * @see #END_OF_LINE
	 * @see #NEXT_LINE
	 * @see #NEXT_LINE_SHIFTED
	 * @see #NEXT_LINE_ON_WRAP
	 * @since 3.0
	 */
	public static final String FORMATTER_BRACE_POSITION_FOR_SWITCH = DLTKCore.PLUGIN_ID + ".formatter.brace_position_for_switch";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to position the braces of a type declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.brace_position_for_type_declaration"
	 *     - possible values:   { END_OF_LINE, NEXT_LINE, NEXT_LINE_SHIFTED, NEXT_LINE_ON_WRAP }
	 *     - default:           END_OF_LINE
	 * </pre>
	 * @see #END_OF_LINE
	 * @see #NEXT_LINE
	 * @see #NEXT_LINE_SHIFTED
	 * @see #NEXT_LINE_ON_WRAP
	 * @since 3.0
	 */
	public static final String FORMATTER_BRACE_POSITION_FOR_TYPE_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.brace_position_for_type_declaration";	//$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to control whether blank lines are cleared inside comments
	 *     - option id:         "org.eclipse.jdt.core.formatter.comment.clear_blank_lines"
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           FALSE
	 * </pre>
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.1
	 */	
	public final static String FORMATTER_COMMENT_CLEAR_BLANK_LINES = "org.eclipse.jdt.core.formatter.comment.clear_blank_lines"; //$NON-NLS-1$
	
	/**
	 * <pre>
	 * FORMATTER / Option to control whether comments are formatted
	 *     - option id:         "org.eclipse.jdt.core.formatter.comment.format_comments"
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.1
	 */	
	public final static String FORMATTER_COMMENT_FORMAT = "org.eclipse.jdt.core.formatter.comment.format_comments"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to control whether the header comment of a Java source file is formatted
	 *     - option id:         "org.eclipse.jdt.core.formatter.comment.format_header"
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           FALSE
	 * </pre>
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.1
	 */	
	public final static String FORMATTER_COMMENT_FORMAT_HEADER = "org.eclipse.jdt.core.formatter.comment.format_header"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to control whether HTML tags are formatted.
	 *     - option id:         "org.eclipse.jdt.core.formatter.comment.format_html"
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.1
	 */	
	public final static String FORMATTER_COMMENT_FORMAT_HTML = "org.eclipse.jdt.core.formatter.comment.format_html"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to control whether code snippets are formatted in comments
	 *     - option id:         "org.eclipse.jdt.core.formatter.comment.format_source_code"
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.1
	 */	
	public final static String FORMATTER_COMMENT_FORMAT_SOURCE = "org.eclipse.jdt.core.formatter.comment.format_source_code"; //$NON-NLS-1$
	
	/**
	 * <pre>
	 * FORMATTER / Option to control whether description of Javadoc parameters are indented
	 *     - option id:         "org.eclipse.jdt.core.formatter.comment.indent_parameter_description"
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.1
	 */	
	public final static String FORMATTER_COMMENT_INDENT_PARAMETER_DESCRIPTION = "org.eclipse.jdt.core.formatter.comment.indent_parameter_description"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to control whether Javadoc root tags are indented.
	 *     - option id:         "org.eclipse.jdt.core.formatter.comment.indent_root_tags"
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.1
	 */	
	public final static String FORMATTER_COMMENT_INDENT_ROOT_TAGS = "org.eclipse.jdt.core.formatter.comment.indent_root_tags"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to insert an empty line before the Javadoc root tag block
	 *     - option id:         "org.eclipse.jdt.core.formatter.comment.insert_new_line_before_root_tags"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */	
	public final static String FORMATTER_COMMENT_INSERT_EMPTY_LINE_BEFORE_ROOT_TAGS = "org.eclipse.jdt.core.formatter.comment.insert_new_line_before_root_tags"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to insert a new line after Javadoc root tag parameters
	 *     - option id:         "org.eclipse.jdt.core.formatter.comment.insert_new_line_for_parameter"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */	
	public final static String FORMATTER_COMMENT_INSERT_NEW_LINE_FOR_PARAMETER = "org.eclipse.jdt.core.formatter.comment.insert_new_line_for_parameter"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to specify the line length for comments.
	 *     - option id:         "org.eclipse.jdt.core.formatter.comment.line_length"
	 *     - possible values:   "&lt;n&gt;", where n is zero or a positive integer
	 *     - default:           "80"
	 * </pre>
	 * @since 3.1
	 */	
	public final static String FORMATTER_COMMENT_LINE_LENGTH = "org.eclipse.jdt.core.formatter.comment.line_length"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to compact else/if
	 *     - option id:         "org.eclipse.jdt.core.formatter.compact_else_if"
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.0
	 */
	public static final String FORMATTER_COMPACT_ELSE_IF = DLTKCore.PLUGIN_ID + ".formatter.compact_else_if";	//$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to set the continuation indentation
	 *     - option id:         "org.eclipse.jdt.core.formatter.continuation_indentation"
	 *     - possible values:   "&lt;n&gt;", where n is zero or a positive integer
	 *     - default:           "2"
	 * </pre>
	 * @since 3.0
	 */
	public static final String FORMATTER_CONTINUATION_INDENTATION = DLTKCore.PLUGIN_ID + ".formatter.continuation_indentation";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to set the continuation indentation inside array initializer
	 *     - option id:         "org.eclipse.jdt.core.formatter.continuation_indentation_for_array_initializer"
	 *     - possible values:   "&lt;n&gt;", where n is zero or a positive integer
	 *     - default:           "2"
	 * </pre>
	 * @since 3.0
	 */
	public static final String FORMATTER_CONTINUATION_INDENTATION_FOR_ARRAY_INITIALIZER = DLTKCore.PLUGIN_ID + ".formatter.continuation_indentation_for_array_initializer";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to indent body declarations compare to its enclosing annotation declaration header
	 *     - option id:         "org.eclipse.jdt.core.formatter.indent_body_declarations_compare_to_annotation_declaration_header"
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.2
	 */
	public static final String FORMATTER_INDENT_BODY_DECLARATIONS_COMPARE_TO_ANNOTATION_DECLARATION_HEADER = DLTKCore.PLUGIN_ID + ".formatter.indent_body_declarations_compare_to_annotation_declaration_header";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to indent body declarations compare to its enclosing enum constant header
	 *     - option id:         "org.eclipse.jdt.core.formatter.indent_body_declarations_compare_to_enum_constant_header"
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.1
	 */
	public static final String FORMATTER_INDENT_BODY_DECLARATIONS_COMPARE_TO_ENUM_CONSTANT_HEADER = DLTKCore.PLUGIN_ID + ".formatter.indent_body_declarations_compare_to_enum_constant_header";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to indent body declarations compare to its enclosing enum declaration header
	 *     - option id:         "org.eclipse.jdt.core.formatter.indent_body_declarations_compare_to_enum_declaration_header"
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.1
	 */
	public static final String FORMATTER_INDENT_BODY_DECLARATIONS_COMPARE_TO_ENUM_DECLARATION_HEADER = DLTKCore.PLUGIN_ID + ".formatter.indent_body_declarations_compare_to_enum_declaration_header";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to indent body declarations compare to its enclosing type header
	 *     - option id:         "org.eclipse.jdt.core.formatter.indent_body_declarations_compare_to_type_header"
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.0
	 */
	public static final String FORMATTER_INDENT_BODY_DECLARATIONS_COMPARE_TO_TYPE_HEADER = DLTKCore.PLUGIN_ID + ".formatter.indent_body_declarations_compare_to_type_header";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to indent breaks compare to cases
	 *     - option id:         "org.eclipse.jdt.core.formatter.indent_breaks_compare_to_cases"
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.0
	 */
	public static final String FORMATTER_INDENT_BREAKS_COMPARE_TO_CASES = DLTKCore.PLUGIN_ID + ".formatter.indent_breaks_compare_to_cases";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to indent empty lines
	 *     - option id:         "org.eclipse.jdt.core.formatter.indent_empty_lines"
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           FALSE
	 * </pre>
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.2
	 */
	public static final String FORMATTER_INDENT_EMPTY_LINES = DLTKCore.PLUGIN_ID + ".formatter.indent_empty_lines"; //$NON-NLS-1$	
	/**
	 * <pre>
	 * FORMATTER / Option to indent statements inside a block
	 *     - option id:         "org.eclipse.jdt.core.formatter.indent_statements_compare_to_block"
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.0
	 */
	public static final String FORMATTER_INDENT_STATEMENTS_COMPARE_TO_BLOCK = DLTKCore.PLUGIN_ID + ".formatter.indent_statements_compare_to_block"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to indent statements inside the body of a method or a constructor
	 *     - option id:         "org.eclipse.jdt.core.formatter.indent_statements_compare_to_body"
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.0
	 */
	public static final String FORMATTER_INDENT_STATEMENTS_COMPARE_TO_BODY = DLTKCore.PLUGIN_ID + ".formatter.indent_statements_compare_to_body"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to indent switch statements compare to cases
	 *     - option id:         "org.eclipse.jdt.core.formatter.indent_switchstatements_compare_to_cases"
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.0
	 */
	public static final String FORMATTER_INDENT_SWITCHSTATEMENTS_COMPARE_TO_CASES = DLTKCore.PLUGIN_ID + ".formatter.indent_switchstatements_compare_to_cases";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to indent switch statements compare to switch
	 *     - option id:         "org.eclipse.jdt.core.formatter.indent_switchstatements_compare_to_switch"
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           TRUE
	 * </pre>
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.0
	 */
	public static final String FORMATTER_INDENT_SWITCHSTATEMENTS_COMPARE_TO_SWITCH = DLTKCore.PLUGIN_ID + ".formatter.indent_switchstatements_compare_to_switch";	//$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to specify the equivalent number of spaces that represents one indentation 
	 *     - option id:         "org.eclipse.jdt.core.formatter.indentation.size"
	 *     - possible values:   "&lt;n&gt;", where n is zero or a positive integer
	 *     - default:           "4"
	 * </pre>
	 * <p>This option is used only if the tab char is set to MIXED.
	 * </p>
	 * @see #FORMATTER_TAB_CHAR
	 * @since 3.1
	 */
	public static final String FORMATTER_INDENTATION_SIZE = DLTKCore.PLUGIN_ID + ".formatter.indentation.size"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to insert a new line after an annotation
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_new_line_after_annotation"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_NEW_LINE_AFTER_ANNOTATION = DLTKCore.PLUGIN_ID + ".formatter.insert_new_line_after_annotation";//$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to insert a new line after the opening brace in an array initializer
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_new_line_after_opening_brace_in_array_initializer"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_NEW_LINE_AFTER_OPENING_BRACE_IN_ARRAY_INITIALIZER = DLTKCore.PLUGIN_ID + ".formatter.insert_new_line_after_opening_brace_in_array_initializer";//$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to insert a new line at the end of the current file if missing
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_new_line_at_end_of_file_if_missing"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_NEW_LINE_AT_END_OF_FILE_IF_MISSING = DLTKCore.PLUGIN_ID + ".formatter.insert_new_line_at_end_of_file_if_missing";//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a new line before the catch keyword in try statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_new_line_before_catch_in_try_statement"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_NEW_LINE_BEFORE_CATCH_IN_TRY_STATEMENT = DLTKCore.PLUGIN_ID + ".formatter.insert_new_line_before_catch_in_try_statement";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a new line before the closing brace in an array initializer
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_new_line_before_closing_brace_in_array_initializer"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_NEW_LINE_BEFORE_CLOSING_BRACE_IN_ARRAY_INITIALIZER = DLTKCore.PLUGIN_ID + ".formatter.insert_new_line_before_closing_brace_in_array_initializer";//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a new line before the else keyword in if statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_new_line_before_else_in_if_statement"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_NEW_LINE_BEFORE_ELSE_IN_IF_STATEMENT = DLTKCore.PLUGIN_ID + ".formatter.insert_new_line_before_else_in_if_statement";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a new line before the finally keyword in try statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_new_line_before_finally_in_try_statement"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_NEW_LINE_BEFORE_FINALLY_IN_TRY_STATEMENT = DLTKCore.PLUGIN_ID + ".formatter.insert_new_line_before_finally_in_try_statement";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a new line before while in do statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_new_line_before_while_in_do_statement"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_NEW_LINE_BEFORE_WHILE_IN_DO_STATEMENT = DLTKCore.PLUGIN_ID + ".formatter.insert_new_line_before_while_in_do_statement";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a new line in an empty annotation declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_new_line_in_empty_annotation_declaration"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.2
	 */
	public static final String FORMATTER_INSERT_NEW_LINE_IN_EMPTY_ANNOTATION_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.insert_new_line_in_empty_annotation_declaration";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a new line in an empty anonymous type declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_new_line_in_empty_anonymous_type_declaration"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_NEW_LINE_IN_EMPTY_ANONYMOUS_TYPE_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.insert_new_line_in_empty_anonymous_type_declaration";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a new line in an empty block
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_new_line_in_empty_block"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_NEW_LINE_IN_EMPTY_BLOCK = DLTKCore.PLUGIN_ID + ".formatter.insert_new_line_in_empty_block";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a new line in an empty enum constant
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_new_line_in_empty_enum_constant"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_NEW_LINE_IN_EMPTY_ENUM_CONSTANT = DLTKCore.PLUGIN_ID + ".formatter.insert_new_line_in_empty_enum_constant";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a new line in an empty enum declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_new_line_in_empty_enum_declaration"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_NEW_LINE_IN_EMPTY_ENUM_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.insert_new_line_in_empty_enum_declaration";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a new line in an empty method body
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_new_line_in_empty_method_body"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_NEW_LINE_IN_EMPTY_METHOD_BODY = DLTKCore.PLUGIN_ID + ".formatter.insert_new_line_in_empty_method_body";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a new line in an empty type declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_new_line_in_empty_type_declaration"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_NEW_LINE_IN_EMPTY_TYPE_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.insert_new_line_in_empty_type_declaration";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after and in wilcard
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_and_in_type_parameter"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_AND_IN_TYPE_PARAMETER = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_and_in_type_parameter"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after an assignment operator
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_assignment_operator"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_ASSIGNMENT_OPERATOR = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_assignment_operator"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after at in annotation
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_at_in_annotation"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_AT_IN_ANNOTATION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_at_in_annotation"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after at in annotation type declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_at_in_annotation_type_declaration"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_AT_IN_ANNOTATION_TYPE_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_at_in_annotation_type_declaration"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after a binary operator
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_binary_operator"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_BINARY_OPERATOR = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_binary_operator"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the closing angle bracket in type arguments
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_closing_angle_bracket_in_type_arguments"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_CLOSING_ANGLE_BRACKET_IN_TYPE_ARGUMENTS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_closing_angle_bracket_in_type_arguments"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the closing angle bracket in type parameters
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_closing_angle_bracket_in_type_parameters"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_CLOSING_ANGLE_BRACKET_IN_TYPE_PARAMETERS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_closing_angle_bracket_in_type_parameters"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the closing brace of a block
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_closing_brace_in_block"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_CLOSING_BRACE_IN_BLOCK = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_closing_brace_in_block"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the closing parenthesis of a cast expression
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_closing_paren_in_cast"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_CLOSING_PAREN_IN_CAST = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_closing_paren_in_cast"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the colon in an assert statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_colon_in_assert"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COLON_IN_ASSERT = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_colon_in_assert"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after colon in a case statement when a opening brace follows the colon
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_colon_in_case"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COLON_IN_CASE = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_colon_in_case";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the colon in a conditional expression
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_colon_in_conditional"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COLON_IN_CONDITIONAL = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_colon_in_conditional"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after colon in a for statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_colon_in_for"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COLON_IN_FOR = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_colon_in_for";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the colon in a labeled statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_colon_in_labeled_statement"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COLON_IN_LABELED_STATEMENT = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_colon_in_labeled_statement"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in an allocation expression
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_comma_in_allocation_expression"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_ALLOCATION_EXPRESSION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_allocation_expression"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in annotation
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_comma_in_annotation"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_ANNOTATION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_annotation"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in an array initializer
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_comma_in_array_initializer"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_ARRAY_INITIALIZER = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_array_initializer"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in the parameters of a constructor declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_comma_in_constructor_declaration_parameters"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_CONSTRUCTOR_DECLARATION_PARAMETERS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_constructor_declaration_parameters"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in the exception names in a throws clause of a constructor declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_comma_in_constructor_declaration_throws"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_CONSTRUCTOR_DECLARATION_THROWS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_constructor_declaration_throws"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in the arguments of an enum constant
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_comma_in_enum_constant_arguments"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_ENUM_CONSTANT_ARGUMENTS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_enum_constant_arguments"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in enum declarations
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_comma_in_enum_declarations"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_ENUM_DECLARATIONS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_enum_declarations"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in the arguments of an explicit constructor call
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_comma_in_explicitconstructorcall_arguments"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_EXPLICIT_CONSTRUCTOR_CALL_ARGUMENTS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_explicitconstructorcall_arguments"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in the increments of a for statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_comma_in_for_increments"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_FOR_INCREMENTS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_for_increments"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in the initializations of a for statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_comma_in_for_inits"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_FOR_INITS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_for_inits"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in the parameters of a method declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_comma_in_method_declaration_parameters"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_METHOD_DECLARATION_PARAMETERS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_method_declaration_parameters"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in the exception names in a throws clause of a method declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_comma_in_method_declaration_throws"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_METHOD_DECLARATION_THROWS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_method_declaration_throws"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in the arguments of a method invocation
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_comma_in_method_invocation_arguments"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_METHOD_INVOCATION_ARGUMENTS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_method_invocation_arguments"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in multiple field declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_comma_in_multiple_field_declarations"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_MULTIPLE_FIELD_DECLARATIONS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_multiple_field_declarations"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in multiple local declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_comma_in_multiple_local_declarations"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_MULTIPLE_LOCAL_DECLARATIONS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_multiple_local_declarations"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in parameterized type reference
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_comma_in_parameterized_type_reference"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_PARAMETERIZED_TYPE_REFERENCE = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_parameterized_type_reference"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in superinterfaces names of a type header
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_comma_in_superinterfaces"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_SUPERINTERFACES = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_superinterfaces"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in type arguments
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_comma_in_type_arguments"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_TYPE_ARGUMENTS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_type_arguments"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the comma in type parameters
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_comma_in_type_parameters"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_TYPE_PARAMETERS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_type_parameters"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after ellipsis
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_ellipsis"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_ELLIPSIS  = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_ellipsis";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening angle bracket in parameterized type reference
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_opening_angle_bracket_in_parameterized_type_reference"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_ANGLE_BRACKET_IN_PARAMETERIZED_TYPE_REFERENCE = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_opening_angle_bracket_in_parameterized_type_reference";//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening angle bracket in type arguments
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_opening_angle_bracket_in_type_arguments"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_ANGLE_BRACKET_IN_TYPE_ARGUMENTS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_opening_angle_bracket_in_type_arguments";//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening angle bracket in type parameters
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_opening_angle_bracket_in_type_parameters"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_ANGLE_BRACKET_IN_TYPE_PARAMETERS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_opening_angle_bracket_in_type_parameters";//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening brace in an array initializer
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_opening_brace_in_array_initializer"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_BRACE_IN_ARRAY_INITIALIZER = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_opening_brace_in_array_initializer";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening bracket inside an array allocation expression
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_opening_bracket_in_array_allocation_expression"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_BRACKET_IN_ARRAY_ALLOCATION_EXPRESSION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_opening_bracket_in_array_allocation_expression";//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening bracket inside an array reference
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_opening_bracket_in_array_reference"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_BRACKET_IN_ARRAY_REFERENCE = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_opening_bracket_in_array_reference";//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening parenthesis in annotation
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_opening_paren_in_annotation"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_ANNOTATION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_annotation"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening parenthesis in a cast expression
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_opening_paren_in_cast"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_CAST = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_cast"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening parenthesis in a catch
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_opening_paren_in_catch"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_CATCH = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_catch"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening parenthesis in a constructor declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_opening_paren_in_constructor_declaration"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_CONSTRUCTOR_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_constructor_declaration";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening parenthesis in enum constant
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_opening_paren_in_enum_constant"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_ENUM_CONSTANT = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_enum_constant"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening parenthesis in a for statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_opening_paren_in_for"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FOR = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_for"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening parenthesis in an if statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_opening_paren_in_if"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_IF = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_if"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening parenthesis in a method declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_opening_paren_in_method_declaration"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_METHOD_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_method_declaration";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening parenthesis in a method invocation
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_opening_paren_in_method_invocation"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_METHOD_INVOCATION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_method_invocation"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening parenthesis in a parenthesized expression
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_opening_paren_in_parenthesized_expression"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_PARENTHESIZED_EXPRESSION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_parenthesized_expression"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening parenthesis in a switch statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_opening_paren_in_switch"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_SWITCH = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_switch"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening parenthesis in a synchronized statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_opening_paren_in_synchronized"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_SYNCHRONIZED = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_synchronized"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after the opening parenthesis in a while statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_opening_paren_in_while"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_WHILE = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_while"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after a postfix operator
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_postfix_operator"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_POSTFIX_OPERATOR = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_postfix_operator"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after a prefix operator
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_prefix_operator"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_PREFIX_OPERATOR = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_prefix_operator"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after question mark in a conditional expression
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_question_in_conditional"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_QUESTION_IN_CONDITIONAL = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_question_in_conditional"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after question mark in a wildcard
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_question_in_wildcard"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_QUESTION_IN_WILDCARD = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_question_in_wildcard"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after semicolon in a for statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_semicolon_in_for"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_FOR = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_semicolon_in_for"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space after an unary operator
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_after_unary_operator"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_AFTER_UNARY_OPERATOR = DLTKCore.PLUGIN_ID + ".formatter.insert_space_after_unary_operator"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before and in wildcard
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_and_in_type_parameter"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_AND_IN_TYPE_PARAMETER = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_and_in_type_parameter";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before an assignment operator
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_assignment_operator"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_ASSIGNMENT_OPERATOR = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_assignment_operator";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before at in annotation type declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_at_in_annotation_type_declaration"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_AT_IN_ANNOTATION_TYPE_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_at_in_annotation_type_declaration";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before an binary operator
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_binary_operator"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_BINARY_OPERATOR = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_binary_operator";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing angle bracket in parameterized type reference
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_closing_angle_bracket_in_parameterized_type_reference"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_ANGLE_BRACKET_IN_PARAMETERIZED_TYPE_REFERENCE = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_closing_angle_bracket_in_parameterized_type_reference";		//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing angle bracket in type arguments
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_closing_angle_bracket_in_type_arguments"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_ANGLE_BRACKET_IN_TYPE_ARGUMENTS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_closing_angle_bracket_in_type_arguments";		//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing angle bracket in type parameters
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_closing_angle_bracket_in_type_parameters"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_ANGLE_BRACKET_IN_TYPE_PARAMETERS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_closing_angle_bracket_in_type_parameters";		//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing brace in an array initializer
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_closing_brace_in_array_initializer"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_BRACE_IN_ARRAY_INITIALIZER = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_closing_brace_in_array_initializer";		//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing bracket in an array allocation expression
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_closing_bracket_in_array_allocation_expression"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_BRACKET_IN_ARRAY_ALLOCATION_EXPRESSION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_closing_bracket_in_array_allocation_expression";//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing bracket in an array reference
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_closing_bracket_in_array_reference"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_BRACKET_IN_ARRAY_REFERENCE = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_closing_bracket_in_array_reference";//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing parenthesis in annotation
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_closing_paren_in_annotation"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_ANNOTATION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_annotation";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing parenthesis in a cast expression
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_closing_paren_in_cast"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_CAST = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_cast";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing parenthesis in a catch
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_closing_paren_in_catch"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_CATCH = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_catch";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing parenthesis in a constructor declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_closing_paren_in_constructor_declaration"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_CONSTRUCTOR_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_constructor_declaration";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing parenthesis in enum constant
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_closing_paren_in_enum_constant"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_ENUM_CONSTANT = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_enum_constant";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing parenthesis in a for statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_closing_paren_in_for"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FOR = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_for";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing parenthesis in an if statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_closing_paren_in_if"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_IF = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_if";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing parenthesis in a method declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_closing_paren_in_method_declaration"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_METHOD_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_method_declaration";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing parenthesis in a method invocation
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_closing_paren_in_method_invocation"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_METHOD_INVOCATION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_method_invocation"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing parenthesis in a parenthesized expression
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_closing_paren_in_parenthesized_expression"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_PARENTHESIZED_EXPRESSION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_parenthesized_expression"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing parenthesis in a switch statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_closing_paren_in_switch"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_SWITCH = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_switch";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing parenthesis in a synchronized statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_closing_paren_in_synchronized"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_SYNCHRONIZED = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_synchronized";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the closing parenthesis in a while statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_closing_paren_in_while"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_WHILE = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_while";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before colon in an assert statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_colon_in_assert"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_ASSERT = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_colon_in_assert";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before colon in a case statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_colon_in_case"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_CASE = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_colon_in_case";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before colon in a conditional expression
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_colon_in_conditional"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_CONDITIONAL = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_colon_in_conditional";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before colon in a default statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_colon_in_default"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_DEFAULT = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_colon_in_default";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before colon in a for statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_colon_in_for"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_FOR = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_colon_in_for";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before colon in a labeled statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_colon_in_labeled_statement"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_LABELED_STATEMENT = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_colon_in_labeled_statement";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in an allocation expression
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_comma_in_allocation_expression"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_ALLOCATION_EXPRESSION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_allocation_expression";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in annotation
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_comma_in_annotation"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_ANNOTATION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_annotation";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in an array initializer
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_comma_in_array_initializer"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_ARRAY_INITIALIZER = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_array_initializer";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in the parameters of a constructor declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_comma_in_constructor_declaration_parameters"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_CONSTRUCTOR_DECLARATION_PARAMETERS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_constructor_declaration_parameters";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in the exception names of the throws clause of a constructor declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_comma_in_constructor_declaration_throws"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_CONSTRUCTOR_DECLARATION_THROWS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_constructor_declaration_throws";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in the arguments of enum constant
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_comma_in_enum_constant_arguments"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_ENUM_CONSTANT_ARGUMENTS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_enum_constant_arguments";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in enum declarations
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_comma_in_enum_declarations"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_ENUM_DECLARATIONS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_enum_declarations";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in the arguments of an explicit constructor call
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_comma_in_explicitconstructorcall_arguments"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_EXPLICIT_CONSTRUCTOR_CALL_ARGUMENTS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_explicitconstructorcall_arguments";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in the increments of a for statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_comma_in_for_increments"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_FOR_INCREMENTS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_for_increments";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in the initializations of a for statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_comma_in_for_inits"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_FOR_INITS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_for_inits";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in the parameters of a method declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_comma_in_method_declaration_parameters"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_METHOD_DECLARATION_PARAMETERS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_method_declaration_parameters";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in the exception names of the throws clause of a method declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_comma_in_method_declaration_throws"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_METHOD_DECLARATION_THROWS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_method_declaration_throws";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in the arguments of a method invocation
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_comma_in_method_invocation_arguments"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_METHOD_INVOCATION_ARGUMENTS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_method_invocation_arguments";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in a multiple field declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_comma_in_multiple_field_declarations"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_MULTIPLE_FIELD_DECLARATIONS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_multiple_field_declarations";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in a multiple local declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_comma_in_multiple_local_declarations"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_MULTIPLE_LOCAL_DECLARATIONS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_multiple_local_declarations";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in parameterized type reference
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_comma_in_parameterized_type_reference"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_PARAMETERIZED_TYPE_REFERENCE = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_parameterized_type_reference";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in the superinterfaces names in a type header
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_comma_in_superinterfaces"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_SUPERINTERFACES = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_superinterfaces";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in type arguments
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_comma_in_type_arguments"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_TYPE_ARGUMENTS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_type_arguments";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before comma in type parameters
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_comma_in_type_parameters"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_TYPE_PARAMETERS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_type_parameters";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before ellipsis
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_ellipsis"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_ELLIPSIS  = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_ellipsis";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening angle bracket in parameterized type reference
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_opening_angle_bracket_in_parameterized_type_reference"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_ANGLE_BRACKET_IN_PARAMETERIZED_TYPE_REFERENCE  = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_opening_angle_bracket_in_parameterized_type_reference";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening angle bracket in type arguments
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_opening_angle_bracket_in_type_arguments"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_ANGLE_BRACKET_IN_TYPE_ARGUMENTS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_opening_angle_bracket_in_type_arguments";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening angle bracket in type parameters
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_opening_angle_bracket_in_type_parameters"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_ANGLE_BRACKET_IN_TYPE_PARAMETERS = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_opening_angle_bracket_in_type_parameters";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening brace in an annotation type declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_opening_brace_in_annotation_type_declaration"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_ANNOTATION_TYPE_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_opening_brace_in_annotation_type_declaration"; 	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening brace in an anonymous type declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_opening_brace_in_anonymous_type_declaration"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_ANONYMOUS_TYPE_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_opening_brace_in_anonymous_type_declaration"; 	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening brace in an array initializer
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_opening_brace_in_array_initializer"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_ARRAY_INITIALIZER = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_opening_brace_in_array_initializer"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening brace in a block
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_opening_brace_in_block"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_BLOCK = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_opening_brace_in_block";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening brace in a constructor declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_opening_brace_in_constructor_declaration"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_CONSTRUCTOR_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_opening_brace_in_constructor_declaration";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening brace in an enum constant
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_opening_brace_in_enum_constant"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_ENUM_CONSTANT = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_opening_brace_in_enum_constant";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening brace in an enum declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_opening_brace_in_enum_declaration"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_ENUM_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_opening_brace_in_enum_declaration";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening brace in a method declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_opening_brace_in_method_declaration"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_METHOD_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_opening_brace_in_method_declaration";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening brace in a switch statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_opening_brace_in_switch"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_SWITCH = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_opening_brace_in_switch";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening brace in a type declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_opening_brace_in_type_declaration"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_TYPE_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_opening_brace_in_type_declaration";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening bracket in an array allocation expression
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_opening_bracket_in_array_allocation_expression"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACKET_IN_ARRAY_ALLOCATION_EXPRESSION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_opening_bracket_in_array_allocation_expression";//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening bracket in an array reference
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_opening_bracket_in_array_reference"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACKET_IN_ARRAY_REFERENCE = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_opening_bracket_in_array_reference";//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening bracket in an array type reference
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_opening_bracket_in_array_type_reference"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACKET_IN_ARRAY_TYPE_REFERENCE = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_opening_bracket_in_array_type_reference";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening parenthesis in annotation
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_opening_paren_in_annotation"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ANNOTATION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_annotation";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening parenthesis in annotation type member declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_opening_paren_in_annotation_type_member_declaration"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ANNOTATION_TYPE_MEMBER_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_annotation_type_member_declaration";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening parenthesis in a catch
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_opening_paren_in_catch"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_CATCH = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_catch";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening parenthesis in a constructor declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_opening_paren_in_constructor_declaration"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_CONSTRUCTOR_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_constructor_declaration";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening parenthesis in enum constant
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_opening_paren_in_enum_constant"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ENUM_CONSTANT = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_enum_constant";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening parenthesis in a for statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_opening_paren_in_for"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FOR = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_for";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening parenthesis in an if statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_opening_paren_in_if"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_IF = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_if";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening parenthesis in a method declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_opening_paren_in_method_declaration"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_METHOD_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_method_declaration";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening parenthesis in a method invocation
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_opening_paren_in_method_invocation"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_METHOD_INVOCATION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_method_invocation";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening parenthesis in a parenthesized expression
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_opening_paren_in_parenthesized_expression"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_PARENTHESIZED_EXPRESSION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_parenthesized_expression"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening parenthesis in a switch statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_opening_paren_in_switch"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_SWITCH = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_switch";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening parenthesis in a synchronized statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_opening_paren_in_synchronized"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_SYNCHRONIZED = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_synchronized";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before the opening parenthesis in a while statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_opening_paren_in_while"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_WHILE = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_while";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before parenthesized expression in return statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_parenthesized_expression_in_return"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.2
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_PARENTHESIZED_EXPRESSION_IN_RETURN  = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_parenthesized_expression_in_return";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before parenthesized expression in throw statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_parenthesized_expression_in_throw"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * 
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.3
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_PARENTHESIZED_EXPRESSION_IN_THROW  = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_parenthesized_expression_in_throw";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before a postfix operator
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_postfix_operator"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_POSTFIX_OPERATOR = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_postfix_operator";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before a prefix operator
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_prefix_operator"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_PREFIX_OPERATOR = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_prefix_operator";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before question mark in a conditional expression
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_question_in_conditional"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_QUESTION_IN_CONDITIONAL = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_question_in_conditional";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before question mark in a wildcard
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_question_in_wildcard"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_QUESTION_IN_WILDCARD = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_question_in_wildcard"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before semicolon
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_semicolon"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_semicolon";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before semicolon in for statement
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_semicolon_in_for"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_FOR = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_semicolon_in_for";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space before unary operator
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_before_unary_operator"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BEFORE_UNARY_OPERATOR = DLTKCore.PLUGIN_ID + ".formatter.insert_space_before_unary_operator";	//$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to insert a space between brackets in an array type reference
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_between_brackets_in_array_type_reference"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_BRACKETS_IN_ARRAY_TYPE_REFERENCE = DLTKCore.PLUGIN_ID + ".formatter.insert_space_between_brackets_in_array_type_reference";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space between empty braces in an array initializer
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_between_empty_braces_in_array_initializer"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_BRACES_IN_ARRAY_INITIALIZER = DLTKCore.PLUGIN_ID + ".formatter.insert_space_between_empty_braces_in_array_initializer";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space between empty brackets in an array allocation expression
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_between_empty_brackets_in_array_allocation_expression"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_BRACKETS_IN_ARRAY_ALLOCATION_EXPRESSION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_between_empty_brackets_in_array_allocation_expression";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space between empty parenthesis in an annotation type member declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_between_empty_parens_in_annotation_type_member_declaration"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_ANNOTATION_TYPE_MEMBER_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_between_empty_parens_in_annotation_type_member_declaration";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space between empty parenthesis in a constructor declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_between_empty_parens_in_constructor_declaration"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_CONSTRUCTOR_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_between_empty_parens_in_constructor_declaration";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space between empty parenthesis in enum constant
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_between_empty_parens_in_enum_constant"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.1
	 */
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_ENUM_CONSTANT = DLTKCore.PLUGIN_ID + ".formatter.insert_space_between_empty_parens_in_enum_constant";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space between empty parenthesis in a method declaration
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_between_empty_parens_in_method_declaration"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_METHOD_DECLARATION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_between_empty_parens_in_method_declaration";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to insert a space between empty parenthesis in a method invocation
	 *     - option id:         "org.eclipse.jdt.core.formatter.insert_space_between_empty_parens_in_method_invocation"
	 *     - possible values:   { INSERT, DO_NOT_INSERT }
	 *     - default:           DO_NOT_INSERT
	 * </pre>
	 * @see DLTKCore#INSERT
	 * @see DLTKCore#DO_NOT_INSERT
	 * @since 3.0
	 */
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_METHOD_INVOCATION = DLTKCore.PLUGIN_ID + ".formatter.insert_space_between_empty_parens_in_method_invocation";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to keep else statement on the same line
	 *     - option id:         "org.eclipse.jdt.core.formatter.keep_else_statement_on_same_line"
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           FALSE
	 * </pre>
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.0
	 */
	public static final String FORMATTER_KEEP_ELSE_STATEMENT_ON_SAME_LINE = DLTKCore.PLUGIN_ID + ".formatter.keep_else_statement_on_same_line"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to keep empty array initializer one one line
	 *     - option id:         "org.eclipse.jdt.core.formatter.keep_empty_array_initializer_on_one_line"
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           FALSE
	 * </pre>
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.0
	 */
	public static final String FORMATTER_KEEP_EMPTY_ARRAY_INITIALIZER_ON_ONE_LINE = DLTKCore.PLUGIN_ID + ".formatter.keep_empty_array_initializer_on_one_line"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to keep guardian clause on one line
	 *     - option id:         "org.eclipse.jdt.core.formatter.format_guardian_clause_on_one_line"
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           FALSE
	 * </pre>
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.0
	 */
	public static final String FORMATTER_KEEP_GUARDIAN_CLAUSE_ON_ONE_LINE = DLTKCore.PLUGIN_ID + ".formatter.format_guardian_clause_on_one_line";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to keep simple if statement on the one line
	 *     - option id:         "org.eclipse.jdt.core.formatter.keep_imple_if_on_one_line"
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           FALSE
	 * </pre>
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.0
	 */
	public static final String FORMATTER_KEEP_SIMPLE_IF_ON_ONE_LINE = DLTKCore.PLUGIN_ID + ".formatter.keep_imple_if_on_one_line"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to keep then statement on the same line
	 *     - option id:         "org.eclipse.jdt.core.formatter.keep_then_statement_on_same_line"
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           FALSE
	 * </pre>
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.0
	 */
	public static final String FORMATTER_KEEP_THEN_STATEMENT_ON_SAME_LINE = DLTKCore.PLUGIN_ID + ".formatter.keep_then_statement_on_same_line";//$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to specify the length of the page. Beyond this length, the formatter will try to split the code
	 *     - option id:         "org.eclipse.jdt.core.formatter.lineSplit"
	 *     - possible values:   "&lt;n&gt;", where n is zero or a positive integer
	 *     - default:           "80"
	 * </pre>
	 * @since 3.0
	 */
	public static final String FORMATTER_LINE_SPLIT = DLTKCore.PLUGIN_ID + ".formatter.lineSplit"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to specify the number of empty lines to preserve
	 *     - option id:         "org.eclipse.jdt.core.formatter.number_of_empty_lines_to_preserve"
	 *     - possible values:   "&lt;n&gt;", where n is zero or a positive integer
	 *     - default:           "0"
	 * </pre>
	 * @since 3.0
	 */
	public static final String FORMATTER_NUMBER_OF_EMPTY_LINES_TO_PRESERVE = DLTKCore.PLUGIN_ID + ".formatter.number_of_empty_lines_to_preserve";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to specify whether or not empty statement should be on a new line
	 *     - option id:         "org.eclipse.jdt.core.formatter.put_empty_statement_on_new_line"
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           FALSE
	 * </pre>
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.0
	 */
	public static final String FORMATTER_PUT_EMPTY_STATEMENT_ON_NEW_LINE = DLTKCore.PLUGIN_ID + ".formatter.put_empty_statement_on_new_line";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to specify the tabulation size
	 *     - option id:         "org.eclipse.jdt.core.formatter.tabulation.char"
	 *     - possible values:   { TAB, SPACE, MIXED }
	 *     - default:           TAB
	 * </pre>
	 * More values may be added in the future.
	 * 
	 * @see DLTKCore#TAB
	 * @see DLTKCore#SPACE
	 * @see #MIXED
	 * @since 3.0
	 */
	public static final String FORMATTER_TAB_CHAR = DLTKCore.PLUGIN_ID + ".formatter.tabulation.char"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Option to specify the equivalent number of spaces that represents one tabulation 
	 *     - option id:         "org.eclipse.jdt.core.formatter.tabulation.size"
	 *     - possible values:   "&lt;n&gt;", where n is zero or a positive integer
	 *     - default:           "4"
	 * </pre>
	 * @since 3.0
	 */
	public static final String FORMATTER_TAB_SIZE = DLTKCore.PLUGIN_ID + ".formatter.tabulation.size"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / Option to use tabulations only for leading indentations 
	 *     - option id:         "org.eclipse.jdt.core.formatter.use_tabs_only_for_leading_indentations"
	 *     - possible values:   { TRUE, FALSE }
	 *     - default:           FALSE
	 * </pre>
	 * @see #TRUE
	 * @see #FALSE
	 * @since 3.1
	 */
	public static final String FORMATTER_USE_TABS_ONLY_FOR_LEADING_INDENTATIONS = DLTKCore.PLUGIN_ID + ".formatter.use_tabs_only_for_leading_indentations"; //$NON-NLS-1$

	/**
	 * <pre>
	 * FORMATTER / The wrapping is done by indenting by one compare to the current indentation.
	 * </pre>
	 * @since 3.0
	 */
	public static final int INDENT_BY_ONE= 2;
	
	/**
	 * <pre>
	 * FORMATTER / The wrapping is done by using the current indentation.
	 * </pre>
	 * @since 3.0
	 */
	public static final int INDENT_DEFAULT= 0;
	/**
	 * <pre>
	 * FORMATTER / The wrapping is done by indenting on column under the splitting location.
	 * </pre>
	 * @since 3.0
	 */
	public static final int INDENT_ON_COLUMN = 1;
	
	/**
	 * <pre>
	 * FORMATTER / Possible value for the option FORMATTER_TAB_CHAR
	 * </pre>
	 * @since 3.1
	 * @see DLTKCore#TAB
	 * @see DLTKCore#SPACE
	 * @see #FORMATTER_TAB_CHAR
	 */
	public static final String MIXED = "mixed"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Value to set a brace location at the start of the next line with
	 *             the right indentation.
	 * </pre>
	 * @see #FORMATTER_BRACE_POSITION_FOR_ANONYMOUS_TYPE_DECLARATION
	 * @see #FORMATTER_BRACE_POSITION_FOR_ARRAY_INITIALIZER
	 * @see #FORMATTER_BRACE_POSITION_FOR_BLOCK
	 * @see #FORMATTER_BRACE_POSITION_FOR_CONSTRUCTOR_DECLARATION
 	 * @see #FORMATTER_BRACE_POSITION_FOR_METHOD_DECLARATION
 	 * @see #FORMATTER_BRACE_POSITION_FOR_SWITCH
	 * @see #FORMATTER_BRACE_POSITION_FOR_TYPE_DECLARATION
	 * @since 3.0
	 */
	public static final String NEXT_LINE = "next_line"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Value to set a brace location at the start of the next line if a wrapping
	 *             occured.
	 * </pre>
	 * @see #FORMATTER_BRACE_POSITION_FOR_ANONYMOUS_TYPE_DECLARATION
	 * @see #FORMATTER_BRACE_POSITION_FOR_ARRAY_INITIALIZER
	 * @see #FORMATTER_BRACE_POSITION_FOR_BLOCK
	 * @see #FORMATTER_BRACE_POSITION_FOR_CONSTRUCTOR_DECLARATION
 	 * @see #FORMATTER_BRACE_POSITION_FOR_METHOD_DECLARATION
 	 * @see #FORMATTER_BRACE_POSITION_FOR_SWITCH
	 * @see #FORMATTER_BRACE_POSITION_FOR_TYPE_DECLARATION
	 * @since 3.0
	 */
    public static final String NEXT_LINE_ON_WRAP = "next_line_on_wrap"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Value to set a brace location at the start of the next line with
	 *             an extra indentation.
	 * </pre>
	 * @see #FORMATTER_BRACE_POSITION_FOR_ANONYMOUS_TYPE_DECLARATION
	 * @see #FORMATTER_BRACE_POSITION_FOR_ARRAY_INITIALIZER
	 * @see #FORMATTER_BRACE_POSITION_FOR_BLOCK
	 * @see #FORMATTER_BRACE_POSITION_FOR_CONSTRUCTOR_DECLARATION
 	 * @see #FORMATTER_BRACE_POSITION_FOR_METHOD_DECLARATION
 	 * @see #FORMATTER_BRACE_POSITION_FOR_SWITCH
	 * @see #FORMATTER_BRACE_POSITION_FOR_TYPE_DECLARATION
	 * @since 3.0
	 */
	public static final String NEXT_LINE_SHIFTED = "next_line_shifted";	//$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / Value to set an option to true.
	 * </pre>
	 * @since 3.0
	 */
	public static final String TRUE = "true"; //$NON-NLS-1$
	/**
	 * <pre>
	 * FORMATTER / The wrapping is done using as few lines as possible.
	 * </pre>
	 * @since 3.0
	 */
	public static final int WRAP_COMPACT= 1;
	/**
	 * <pre>
	 * FORMATTER / The wrapping is done putting the first element on a new
	 *             line and then wrapping next elements using as few lines as possible.
	 * </pre>
	 * @since 3.0
	 */
	public static final int WRAP_COMPACT_FIRST_BREAK= 2;
	/**
	 * <pre>
	 * FORMATTER / The wrapping is done by putting each element on its own line
	 *             except the first element.
	 * </pre>
	 * @since 3.0
	 */
	public static final int WRAP_NEXT_PER_LINE= 5;
	/**
	 * <pre>
	 * FORMATTER / The wrapping is done by putting each element on its own line.
	 *             All elements are indented by one except the first element.
	 * </pre>
	 * @since 3.0
	 */
	public static final int WRAP_NEXT_SHIFTED= 4;

	/**
	 * <pre>
	 * FORMATTER / Value to disable alignment.
	 * </pre>
	 * @since 3.0
	 */
	public static final int WRAP_NO_SPLIT= 0;
	/**
	 * <pre>
	 * FORMATTER / The wrapping is done by putting each element on its own line.
	 * </pre>
	 * @since 3.0
	 */
	public static final int WRAP_ONE_PER_LINE= 3;

	/*
	 * Private constants. Not in javadoc
	 */
	private static final IllegalArgumentException WRONG_ARGUMENT = new IllegalArgumentException();
	/**
	 * Create a new alignment value according to the given values. This must be used to set up
	 * the alignment options.
	 * 
	 * @param forceSplit the given force value
	 * @param wrapStyle the given wrapping style
	 * @param indentStyle the given indent style
	 * 
	 * @return the new alignement value
	 */
	public static String createAlignmentValue(boolean forceSplit, int wrapStyle, int indentStyle) {
		int alignmentValue = 0; 
		switch(wrapStyle) {
			case WRAP_COMPACT :
				alignmentValue |= Alignment.M_COMPACT_SPLIT;
				break;
			case WRAP_COMPACT_FIRST_BREAK :
				alignmentValue |= Alignment.M_COMPACT_FIRST_BREAK_SPLIT;
				break;
			case WRAP_NEXT_PER_LINE :
				alignmentValue |= Alignment.M_NEXT_PER_LINE_SPLIT;
				break;
			case WRAP_NEXT_SHIFTED :
				alignmentValue |= Alignment.M_NEXT_SHIFTED_SPLIT;
				break;
			case WRAP_ONE_PER_LINE :
				alignmentValue |= Alignment.M_ONE_PER_LINE_SPLIT;
				break;
		}		
		if (forceSplit) {
			alignmentValue |= Alignment.M_FORCE;
		}
		switch(indentStyle) {
			case INDENT_BY_ONE :
				alignmentValue |= Alignment.M_INDENT_BY_ONE;
				break;
			case INDENT_ON_COLUMN :
				alignmentValue |= Alignment.M_INDENT_ON_COLUMN;
		}
		return String.valueOf(alignmentValue);
	}

	

	/**
	 * <p>Return the force value of the given alignment value.
	 * The given alignment value should be created using the <code>createAlignmentValue(boolean, int, int)</code>
	 * API.
	 * </p>
	 *
	 * @param value the given alignment value
	 * @return the force value of the given alignment value
	 * @see #createAlignmentValue(boolean, int, int)
	 * @exception IllegalArgumentException if the given alignment value is null, or if it 
	 * doesn't have a valid format.
	 */
	public static boolean getForceWrapping(String value) {
		if (value == null) {
			throw WRONG_ARGUMENT;
		}
		try {
			int existingValue = Integer.parseInt(value);
			return (existingValue & Alignment.M_FORCE) != 0;
		} catch (NumberFormatException e) {
			throw WRONG_ARGUMENT;
		}
	}
	
	/**
	 * <p>Return the indentation style of the given alignment value.
	 * The given alignment value should be created using the <code>createAlignmentValue(boolean, int, int)</code>
	 * API.
	 * </p>
	 *
	 * @param value the given alignment value
	 * @return the indentation style of the given alignment value
	 * @see #createAlignmentValue(boolean, int, int)
	 * @exception IllegalArgumentException if the given alignment value is null, or if it 
	 * doesn't have a valid format.
	 */
	public static int getIndentStyle(String value) {
		if (value == null) {
			throw WRONG_ARGUMENT;
		}
		try {
			int existingValue = Integer.parseInt(value);
			if ((existingValue & Alignment.M_INDENT_BY_ONE) != 0) {
				return INDENT_BY_ONE;
			} else if ((existingValue & Alignment.M_INDENT_ON_COLUMN) != 0) {
				return INDENT_ON_COLUMN;
			} else {
				return INDENT_DEFAULT;
			}
		} catch (NumberFormatException e) {
			throw WRONG_ARGUMENT;
		}
	}

	
	/**
	 * <p>Return the wrapping style of the given alignment value.
	 * The given alignment value should be created using the <code>createAlignmentValue(boolean, int, int)</code>
	 * API.
	 * </p>
	 *
	 * @param value the given alignment value
	 * @return the wrapping style of the given alignment value
	 * @see #createAlignmentValue(boolean, int, int)
	 * @exception IllegalArgumentException if the given alignment value is null, or if it 
	 * doesn't have a valid format.
	 */
	public static int getWrappingStyle(String value) {
		if (value == null) {
			throw WRONG_ARGUMENT;
		}
		try {
			int existingValue = Integer.parseInt(value) & Alignment.SPLIT_MASK;
			switch(existingValue) {
				case Alignment.M_COMPACT_SPLIT :
					return WRAP_COMPACT;
				case Alignment.M_COMPACT_FIRST_BREAK_SPLIT :
					return WRAP_COMPACT_FIRST_BREAK;
				case Alignment.M_NEXT_PER_LINE_SPLIT :
					return WRAP_NEXT_PER_LINE;
				case Alignment.M_NEXT_SHIFTED_SPLIT :
					return WRAP_NEXT_SHIFTED;
				case Alignment.M_ONE_PER_LINE_SPLIT :
					return WRAP_ONE_PER_LINE;
				default:
					return WRAP_NO_SPLIT;
			}
		} catch (NumberFormatException e) {
			throw WRONG_ARGUMENT;
		}
	}
	/**
	 * <p>Set the force value of the given alignment value and return the new value.
	 * The given alignment value should be created using the <code>createAlignmentValue(boolean, int, int)</code>
	 * API.
	 * </p>
	 *
	 * @param value the given alignment value
	 * @param force the given force value
	 * @return the new alignment value
	 * @see #createAlignmentValue(boolean, int, int)
	 * @exception IllegalArgumentException if the given alignment value is null, or if it 
	 * doesn't have a valid format.
	 */
	public static String setForceWrapping(String value, boolean force) {
		if (value == null) {
			throw WRONG_ARGUMENT;
		}
		try {
			int existingValue = Integer.parseInt(value);
			// clear existing force bit
			existingValue &= ~Alignment.M_FORCE;
			if (force) {
				existingValue |= Alignment.M_FORCE;
			}
			return String.valueOf(existingValue);
		} catch (NumberFormatException e) {
			throw WRONG_ARGUMENT;
		}		
	}
	
	/**
	 * <p>Set the indentation style of the given alignment value and return the new value.
	 * The given value should be created using the <code>createAlignmentValue(boolean, int, int)</code>
	 * API.
	 * </p>
	 *
	 * @param value the given alignment value
	 * @param indentStyle the given indentation style
	 * @return the new alignment value
	 * @see #INDENT_BY_ONE
	 * @see #INDENT_DEFAULT
	 * @see #INDENT_ON_COLUMN
	 * @see #createAlignmentValue(boolean, int, int)
	 * @exception IllegalArgumentException if the given alignment value is null, if the given
	 * indentation style is not one of the possible indentation styles, or if the given
	 * alignment value doesn't have a valid format.
	 */
	public static String setIndentStyle(String value, int indentStyle) {
		if (value == null) {
			throw WRONG_ARGUMENT;
		}
		switch(indentStyle) {
			case INDENT_BY_ONE :
			case INDENT_DEFAULT :
			case INDENT_ON_COLUMN :
				break;
			default :
				throw WRONG_ARGUMENT;
		}
		try {
			int existingValue = Integer.parseInt(value);
			// clear existing indent bits
			existingValue &= ~(Alignment.M_INDENT_BY_ONE | Alignment.M_INDENT_ON_COLUMN);
			switch(indentStyle) {
				case INDENT_BY_ONE :
					existingValue |= Alignment.M_INDENT_BY_ONE;
					break;
				case INDENT_ON_COLUMN :
					existingValue |= Alignment.M_INDENT_ON_COLUMN;
			}
			return String.valueOf(existingValue);
		} catch (NumberFormatException e) {
			throw WRONG_ARGUMENT;
		}
	}
	/**
	 * <p>Set the wrapping style of the given alignment value and return the new value.
	 * The given value should be created using the <code>createAlignmentValue(boolean, int, int)</code>
	 * API.
	 * </p>
	 *
	 * @param value the given alignment value
	 * @param wrappingStyle the given wrapping style
	 * @return the new alignment value
	 * @see #WRAP_COMPACT
	 * @see #WRAP_COMPACT_FIRST_BREAK
	 * @see #WRAP_NEXT_PER_LINE
	 * @see #WRAP_NEXT_SHIFTED
	 * @see #WRAP_NO_SPLIT
	 * @see #WRAP_ONE_PER_LINE
	 * @see #createAlignmentValue(boolean, int, int)
	 * @exception IllegalArgumentException if the given alignment value is null, if the given
	 * wrapping style is not one of the possible wrapping styles, or if the given
	 * alignment value doesn't have a valid format.
	 */
	public static String setWrappingStyle(String value, int wrappingStyle) {
		if (value == null) {
			throw WRONG_ARGUMENT;
		}
		switch(wrappingStyle) {
			case WRAP_COMPACT :
			case WRAP_COMPACT_FIRST_BREAK :
			case WRAP_NEXT_PER_LINE :
			case WRAP_NEXT_SHIFTED :
			case WRAP_NO_SPLIT :
			case WRAP_ONE_PER_LINE :
				break;
			default:
				throw WRONG_ARGUMENT;
		}
		try {
			int existingValue = Integer.parseInt(value);
			// clear existing split bits
			existingValue &= ~(Alignment.SPLIT_MASK);
			switch(wrappingStyle) {
				case WRAP_COMPACT :
					existingValue |= Alignment.M_COMPACT_SPLIT;
					break;
				case WRAP_COMPACT_FIRST_BREAK :
					existingValue |= Alignment.M_COMPACT_FIRST_BREAK_SPLIT;
					break;
				case WRAP_NEXT_PER_LINE :
					existingValue |= Alignment.M_NEXT_PER_LINE_SPLIT;
					break;
				case WRAP_NEXT_SHIFTED :
					existingValue |= Alignment.M_NEXT_SHIFTED_SPLIT;
					break;
				case WRAP_ONE_PER_LINE :
					existingValue |= Alignment.M_ONE_PER_LINE_SPLIT;
					break;
			}
			return String.valueOf(existingValue);
		} catch (NumberFormatException e) {
			throw WRONG_ARGUMENT;
		}
	}
}


public final class IndentManipulation {
	
	private IndentManipulation() {
		// don't instantiate
	}
	
	/**
	 * Returns <code>true</code> if the given character is an indentation character. Indentation character are all whitespace characters
	 * except the line delimiter characters.
	 * 
	 * @param ch the given character
	 * @return Returns <code>true</code> if this the character is a indent character, <code>false</code> otherwise
	 */
	public static boolean isIndentChar(char ch) {
		return ScannerHelper.isWhitespace(ch) && !isLineDelimiterChar(ch);
	}

	/**
	 * Returns <code>true</code> if the given character is a line delimiter character.
	 * 
	 * @param ch the given character
	 * @return Returns <code>true</code> if this the character is a line delimiter character, <code>false</code> otherwise
	 */
	public static boolean isLineDelimiterChar(char ch) { 
		return ch == '\n' || ch == '\r';
	}	
	
	/**
	 * Returns the indentation of the given line in indentation units. Odd spaces are
	 * not counted. This method only analyzes the content of <code>line</code> up to the first
	 * non-whitespace character.
	 * 
	 * @param line the string to measure the indent of
	 * @param tabWidth the width of one tab character in space equivalents
	 * @param indentWidth the width of one indentation unit in space equivalents
	 * @return the number of indentation units that line is indented by
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the given <code>indentWidth</code> is lower or equals to zero</li>
	 * <li>the given <code>tabWidth</code> is lower than zero</li>
	 * <li>the given <code>line</code> is null</li>
	 * </ul>
	 */
	public static int measureIndentUnits(CharSequence line, int tabWidth, int indentWidth) {
		if (indentWidth <= 0 || tabWidth < 0 || line == null) {
			throw new IllegalArgumentException();
		}
		
		int visualLength= measureIndentInSpaces(line, tabWidth);
		return visualLength / indentWidth;
	}
	
	/**
	 * Returns the indentation of the given line in space equivalents.
	 * 
	 * <p>Tab characters are counted using the given <code>tabWidth</code> and every other indent
	 * character as one. This method analyzes the content of <code>line</code> up to the first
	 * non-whitespace character.</p>
	 * 
	 * @param line the string to measure the indent of
	 * @param tabWidth the width of one tab in space equivalents
	 * @return the measured indent width in space equivalents
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the given <code>line</code> is null</li>
	 * <li>the given <code>tabWidth</code> is lower than zero</li>
	 * </ul>
	 */
	public static int measureIndentInSpaces(CharSequence line, int tabWidth) {
		if (tabWidth < 0 || line == null) {
			throw new IllegalArgumentException();
		}
		
		int length= 0;
		int max= line.length();
		for (int i= 0; i < max; i++) {
			char ch= line.charAt(i);
			if (ch == '\t') {
				int reminder= length % tabWidth;
				length += tabWidth - reminder;
			} else if (isIndentChar(ch)) {
				length++;
			} else {
				return length;
			}
		}
		return length;
	}
	
	/**
	 * Returns the leading indentation string of the given line. Note that the returned string
	 * need not be equal to the leading whitespace as odd spaces are not considered part of the
	 * indentation.
	 * 
	 * @param line the line to scan
	 * @param tabWidth the size of one tab in space equivalents
	 * @param indentWidth the width of one indentation unit in space equivalents
	 * @return the indent part of <code>line</code>, but no odd spaces
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the given <code>indentWidth</code> is lower or equals to zero</li>
	 * <li>the given <code>tabWidth</code> is lower than zero</li>
	 * <li>the given <code>line</code> is null</li>
	 * </ul>
	 */
	public static String extractIndentString(String line, int tabWidth, int indentWidth) {
		if (tabWidth < 0 || indentWidth <= 0 || line == null) {
			throw new IllegalArgumentException();
		}
		
		int size= line.length();
		int end= 0;
		
		int spaceEquivs= 0;
		int characters= 0;
		for (int i= 0; i < size; i++) {
			char c= line.charAt(i);
			if (c == '\t') {
				int remainder= spaceEquivs % tabWidth;
				spaceEquivs += tabWidth - remainder;
				characters++;
			} else if (isIndentChar(c)) {
				spaceEquivs++;
				characters++;
			} else {
				break;
			}
			if (spaceEquivs >= indentWidth) {
				end += characters;
				characters= 0;
				spaceEquivs= spaceEquivs % indentWidth;
			}
		}
		if (end == 0) {
			return "";
		} else if (end == size) {
			return line;
		} else {
			return line.substring(0, end);
		}
	}

	
	/**
	 * Removes the given number of indentation units from a given line. If the line 
	 * has less than the given indent, all the available indentation is removed.
	 * If <code>indentsToRemove <= 0</code> the line is returned.
	 *
	 * @param line the line to trim
	 * @param tabWidth the width of one tab in space equivalents
	 * @param indentWidth the width of one indentation unit in space equivalents
	 * @return the trimmed string
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the given <code>indentWidth</code> is lower or equals to zero</li>
	 * <li>the given <code>tabWidth</code> is lower than zero</li>
	 * <li>the given <code>line</code> is null</li>
	 * </ul>
	 */
	public static String trimIndent(String line, int indentUnitsToRemove, int tabWidth, int indentWidth) {
		if (tabWidth < 0 || indentWidth <= 0 || line == null) {
			throw new IllegalArgumentException();
		}
		
		if (indentUnitsToRemove <= 0)
			return line;

		final int spaceEquivalentsToRemove= indentUnitsToRemove * indentWidth;
		
		int start= 0;
		int spaceEquivalents= 0;
		int size= line.length();
		String prefix= null;
		for (int i= 0; i < size; i++) {
			char c= line.charAt(i);
			if (c == '\t') {
				int remainder= spaceEquivalents % tabWidth;
				spaceEquivalents += tabWidth - remainder;
			} else if (isIndentChar(c)) {
				spaceEquivalents++;
			} else {
				// Assert.isTrue(false, "Line does not have requested number of indents");
				start= i;
				break; 
			}
			if (spaceEquivalents == spaceEquivalentsToRemove) {
				start= i + 1;
				break;
			}
			if (spaceEquivalents > spaceEquivalentsToRemove) {
				// can happen if tabSize > indentSize, e.g tabsize==8, indent==4, indentsToRemove==1, line prefixed with one tab
				// this implements the third option
				start= i + 1; // remove the tab
				// and add the missing spaces
				char[] missing= new char[spaceEquivalents - spaceEquivalentsToRemove];
				Arrays.fill(missing, ' ');
				prefix= new String(missing);
				break;
			}
		}
		String trimmed;
		if (start == size)
			trimmed= "";
		else
			trimmed= line.substring(start);
		
		if (prefix == null)
			return trimmed;
		return prefix + trimmed;
	}

	/**
	 * Change the indent of a, possible multiple line, code string. The given number of indent units is removed,
	 * and a new indent string is added.
	 * <p>The first line of the code will not be changed (It is considered to have no indent as it might start in
	 * the middle of a line).</p>
	 * 
	 * @param code the code to change the indent of
	 * @param indentUnitsToRemove the number of indent units to remove from each line (except the first) of the given code
	 * @param tabWidth the size of one tab in space equivalents
	 * @param indentWidth the width of one indentation unit in space equivalents
	 * @param newIndentString the new indent string to be added to all lines (except the first)
	 * @param lineDelim the new line delimiter to be used. The returned code will contain only this line delimiter.
	 * @return the newly indent code, containing only the given line delimiters.
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the given <code>indentWidth</code> is lower or equals to zero</li>
	 * <li>the given <code>tabWidth</code> is lower than zero</li>
	 * <li>the given <code>code</code> is null</li>
	 * <li>the given <code>indentUnitsToRemove</code> is lower than zero</li>
	 * <li>the given <code>newIndentString</code> is null</li>
	 * <li>the given <code>lineDelim</code> is null</li>
	 * </ul>
	 */
	public static String changeIndent(String code, int indentUnitsToRemove, int tabWidth, int indentWidth, String newIndentString, String lineDelim) {
		if (tabWidth < 0 || indentWidth <= 0 || code == null || indentUnitsToRemove < 0 || newIndentString == null || lineDelim == null) {
			throw new IllegalArgumentException();
		}
		
		try {
			ILineTracker tracker= new DefaultLineTracker();
			tracker.set(code);
			int nLines= tracker.getNumberOfLines();
			if (nLines == 1) {
				return code;
			}
			
			StringBuffer buf= new StringBuffer();
			
			for (int i= 0; i < nLines; i++) {
				IRegion region= tracker.getLineInformation(i);
				int start= region.getOffset();
				int end= start + region.getLength();
				String line= code.substring(start, end);
				
				if (i == 0) {  // no indent for first line (contained in the formatted string)
					buf.append(line);
				} else { // no new line after last line
					buf.append(lineDelim);
					buf.append(newIndentString); 
					buf.append(trimIndent(line, indentUnitsToRemove, tabWidth, indentWidth));
				}
			}
			return buf.toString();
		} catch (BadLocationException e) {
			// can not happen
			return code;
		}
	}

	/**
	 * Returns the text edits retrieved after changing the indentation of a, possible multi-line, code string.
	 * 
	 * <p>The given number of indent units is removed, and a new indent string is added.</p>
	 * <p>The first line of the code will not be changed (It is considered to have no indent as it might start in
	 * the middle of a line).</p>
	 * 
	 * @param source The code to change the indent of
	 * @param indentUnitsToRemove the number of indent units to remove from each line (except the first) of the given code
	 * @param tabWidth the size of one tab in space equivalents
	 * @param indentWidth the width of one indentation unit in space equivalents
	 * @param newIndentString the new indent string to be added to all lines (except the first)
	 * @return returns the resulting text edits
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the given <code>indentWidth</code> is lower or equals to zero</li>
	 * <li>the given <code>tabWidth</code> is lower than zero</li>
	 * <li>the given <code>source</code> is null</li>
	 * <li>the given <code>indentUnitsToRemove</code> is lower than zero</li>
	 * <li>the given <code>newIndentString</code> is null</li>
	 * </ul>
	 */
	public static ReplaceEdit[] getChangeIndentEdits(String source, int indentUnitsToRemove, int tabWidth, int indentWidth, String newIndentString) {
		if (tabWidth < 0 || indentWidth <= 0 || source == null || indentUnitsToRemove < 0 || newIndentString == null) {
			throw new IllegalArgumentException();
		}
		
		ArrayList<ReplaceEdit> result= new ArrayList<ReplaceEdit>();
		try {
			ILineTracker tracker= new DefaultLineTracker();
			tracker.set(source);
			int nLines= tracker.getNumberOfLines();
			if (nLines == 1)
				return (ReplaceEdit[])result.toArray(new ReplaceEdit[result.size()]);
			for (int i= 1; i < nLines; i++) {
				IRegion region= tracker.getLineInformation(i);
				int offset= region.getOffset();
				String line= source.substring(offset, offset + region.getLength());
				int length= indexOfIndent(line, indentUnitsToRemove, tabWidth, indentWidth);
				if (length >= 0) {
					result.add(new ReplaceEdit(offset, length, newIndentString));
				} else {
					length= measureIndentUnits(line, tabWidth, indentWidth);
					result.add(new ReplaceEdit(offset, length, "")); //$NON-NLS-1$
				}
			}
		} catch (BadLocationException cannotHappen) {
			// can not happen
		}
		return (ReplaceEdit[])result.toArray(new ReplaceEdit[result.size()]);
	}
	
	/*
	 * Returns the index where the indent of the given size ends.
	 * Returns <code>-1<code> if the line isn't prefixed with an indent of
	 * the given number of indents.
	 */
	private static int indexOfIndent(CharSequence line, int numberOfIndentUnits, int tabWidth, int indentWidth) {
		
		int spaceEquivalents= numberOfIndentUnits * indentWidth;
		
		int size= line.length();
		int result= -1;
		int blanks= 0;
		for (int i= 0; i < size && blanks < spaceEquivalents; i++) {
			char c= line.charAt(i);
			if (c == '\t') {
				int remainder= blanks % tabWidth;
				blanks += tabWidth - remainder;
			} else if (isIndentChar(c)) {
				blanks++;
			} else {
				break;
			}
			result= i;
		}
		if (blanks < spaceEquivalents)
			return -1;
		return result + 1;
	}
	
	/**
	 * Returns the tab width as configured in the given map.
	 * <p>Use {@link org.eclipse.jdt.core.IJavaProject#getOptions(boolean)} to get the most current project options.</p>
	 * 
	 * @param options the map to get the formatter settings from.
	 * 
	 * @return the tab width
	 * @exception IllegalArgumentException if the given <code>options</code> is null
	 */
	public static int getTabWidth(Map options) {
		if (options == null) {
			throw new IllegalArgumentException();
		}
		return getIntValue(options, DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, 4);
	}
	
	/**
	 * Returns the tab width as configured in the given map.
	 * <p>Use {@link org.eclipse.jdt.core.IJavaProject#getOptions(boolean)} to get the most current project options.</p>
	 * 
	 * @param options the map to get the formatter settings from
	 * 
	 * @return the indent width
	 * @exception IllegalArgumentException if the given <code>options</code> is null
	 */
	public static int getIndentWidth(Map options) {
		if (options == null) {
			throw new IllegalArgumentException();
		}
		int tabWidth=getTabWidth(options);
		boolean isMixedMode= DefaultCodeFormatterConstants.MIXED.equals(options.get(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR));
		if (isMixedMode) {
			return getIntValue(options, DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE, tabWidth);
		}
		return tabWidth;
	}
	
	private static int getIntValue(Map options, String key, int def) {
		try {
			return Integer.parseInt((String) options.get(key));
		} catch (NumberFormatException e) {
			return def;
		}
	}
}

