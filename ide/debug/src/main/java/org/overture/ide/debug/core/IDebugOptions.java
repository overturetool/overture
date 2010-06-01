/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.overture.ide.debug.core;

import org.overture.ide.debug.core.model.IVdmStackFrame;


public interface IDebugOptions {

	abstract class Option {
		private final String name;

		public Option(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	}

	public class BooleanOption extends Option {
		private final boolean defaultValue;

		public BooleanOption(String name, boolean defaultValue) {
			super(name);
			this.defaultValue = defaultValue;
		}

		public boolean getDefaultValue() {
			return defaultValue;
		}

	}

	public class IntegerOption extends Option {
		private final int defaultValue;

		public IntegerOption(String name, int defaultValue) {
			super(name);
			this.defaultValue = defaultValue;
		}

		public int getDefaultValue() {
			return defaultValue;
		}

	}

	public class StringOption extends Option {
		private final String defaultValue;

		public StringOption(String name, String defaultValue) {
			super(name);
			this.defaultValue = defaultValue;
		}

		public String getDefaultValue() {
			return defaultValue;
		}

	}

	boolean get(BooleanOption option);

	int get(IntegerOption option);

	String get(StringOption option);

	/**
	 * Filter the specified stack frames before they are returned to the client.
	 * Implementation should return copy of the array even if there are no
	 * modifications.
	 * 
	 * @param frames
	 * @return
	 */
	IVdmStackFrame[] filterStackLevels(IVdmStackFrame[] frames);

	/**
	 * @param stackFrames
	 * @return
	 */
	boolean isValidStack(IVdmStackFrame[] frames);

}
