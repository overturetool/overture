package org.overture.interpreter.runtime.state;

import org.overture.interpreter.values.NameValuePairMap;
import org.overture.interpreter.runtime.IRuntimeState;

public class SClassDefinitionRuntime implements IRuntimeState {

	/** The private or protected static values in the class. */
	private NameValuePairMap privateStaticValues = null;
	/** The public visible static values in the class. */
	private NameValuePairMap publicStaticValues = null;
	/** True if the class' static members are initialized. */
	protected boolean staticInit = false;
	/** True if the class' static values are initialized. */
	protected boolean staticValuesInit = false;
	
}
