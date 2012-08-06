package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlTraceDefinitionItem extends IOmlTraceDefinition
{
	abstract Vector getBind() throws CGException;
	abstract IOmlTraceCoreDefinition getTest() throws CGException;
	abstract IOmlTraceRepeatPattern getRegexpr() throws CGException;
	abstract Boolean hasRegexpr() throws CGException;
}

