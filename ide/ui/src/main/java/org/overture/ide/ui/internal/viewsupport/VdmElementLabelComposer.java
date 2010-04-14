package org.overture.ide.ui.internal.viewsupport;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.overture.ide.core.IVdmElement;

import org.overturetool.vdmj.definitions.ClassDefinition;

public class VdmElementLabelComposer {

	/**
	 * An adapter for buffer supported by the label composer.
	 */
	public static abstract class FlexibleBuffer {

		/**
		 * Appends the string representation of the given character to the buffer.
		 *
		 * @param ch the character to append
		 * @return a reference to this object
		 */
		public abstract FlexibleBuffer append(char ch);

		/**
		 * Appends the given string to the buffer.
		 *
		 * @param string the string to append
		 * @return a reference to this object
		 */
		public abstract FlexibleBuffer append(String string);

		/**
		 * Returns the length of the the buffer.
		 *
		 * @return the length of the current string
		 */
		public abstract int length();

		/**
		 * Sets a styler to use for the given source range. The range must be subrange of actual
		 * string of this buffer. Stylers previously set for that range will be overwritten.
		 *
		 * @param offset the start offset of the range
		 * @param length the length of the range
		 * @param styler the styler to set
		 *
		 * @throws StringIndexOutOfBoundsException if <code>start</code> is less than zero, or if
		 *             offset plus length is greater than the length of this object.
		 */
		public abstract void setStyle(int offset, int length, Styler styler);
	}

	public static class FlexibleStringBuffer extends FlexibleBuffer {
		private final StringBuffer fStringBuffer;

		public FlexibleStringBuffer(StringBuffer stringBuffer) {
			fStringBuffer= stringBuffer;
		}

		public FlexibleBuffer append(char ch) {
			fStringBuffer.append(ch);
			return this;
		}

		public FlexibleBuffer append(String string) {
			fStringBuffer.append(string);
			return this;
		}

		public int length() {
			return fStringBuffer.length();
		}

		public void setStyle(int offset, int length, Styler styler) {
			// no style
		}

		public String toString() {
			return fStringBuffer.toString();
		}
	}

	public static class FlexibleStyledString extends FlexibleBuffer {
		private final StyledString fStyledString;

		public FlexibleStyledString(StyledString stringBuffer) {
			fStyledString= stringBuffer;
		}

		public FlexibleBuffer append(char ch) {
			fStyledString.append(ch);
			return this;
		}

		public FlexibleBuffer append(String string) {
			fStyledString.append(string);
			return this;
		}

		public int length() {
			return fStyledString.length();
		}

		public void setStyle(int offset, int length, Styler styler) {
			fStyledString.setStyle(offset, length, styler);
		}

		public String toString() {
			return fStyledString.toString();
		}
	}


	/**
	 * Additional delimiters used in this class, for use in {@link Strings#markLTR(String, String)}
	 * or {@link Strings#markLTR(StyledString, String)}.
	 *
	 * @since 3.5
	 */
	public static final String ADDITIONAL_DELIMITERS= "<>(),?{} "; //$NON-NLS-1$

//	private final static long QUALIFIER_FLAGS= VdmElementLabels.P_COMPRESSED | VdmElementLabels.USE_RESOLVED;

	private static final Styler QUALIFIER_STYLE= StyledString.QUALIFIER_STYLER;
	private static final Styler COUNTER_STYLE= StyledString.COUNTER_STYLER;
	private static final Styler DECORATIONS_STYLE= StyledString.DECORATIONS_STYLER;


	/*
	 * Package name compression
	 */
	private static String fgPkgNamePattern= ""; //$NON-NLS-1$
	private static String fgPkgNamePrefix;
	private static String fgPkgNamePostfix;
	private static int fgPkgNameChars;
	private static int fgPkgNameLength= -1;

	private final FlexibleBuffer fBuffer;

	private static final boolean getFlag(long flags, long flag) {
		return (flags & flag) != 0;
	}

	/**
	 * Creates a new java element composer based on the given buffer.
	 *
	 * @param buffer the buffer
	 */
	public VdmElementLabelComposer(FlexibleBuffer buffer) {
		fBuffer= buffer;
	}

	/**
	 * Creates a new java element composer based on the given buffer.
	 *
	 * @param buffer the buffer
	 */
	public VdmElementLabelComposer(StyledString buffer) {
		this(new FlexibleStyledString(buffer));
	}

	/**
	 * Creates a new java element composer based on the given buffer.
	 *
	 * @param buffer the buffer
	 */
	public VdmElementLabelComposer(StringBuffer buffer) {
		this(new FlexibleStringBuffer(buffer));
	}

	public void appendElementLabel(IVdmElement element, long flags) {
		if(element instanceof ClassDefinition){
			appendClassDefinitionLabel((ClassDefinition) element,flags);
		}
		else 
		{
			fBuffer.append("THIS IS A TEST");
		}
		
	}

	private void appendClassDefinitionLabel(ClassDefinition element, long flags) {
		// TODO Auto-generated method stub
		fBuffer.append(element.name.name);
	}


}
