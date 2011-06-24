package org.overturetool.vdmj.runtime.validation;

import org.overturetool.vdmj.definitions.SystemDefinition;
import org.overturetool.vdmj.messages.rtlog.RTMessage.MessageType;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.scheduler.AsyncThread;
import org.overturetool.vdmj.values.OperationValue;

public interface IRuntimeValidatior {

	void init(ClassInterpreter classInterpreter);

	void validate(OperationValue operationValue, MessageType type);

	void bindSystemVariables(SystemDefinition systemDefinition);

	void validateAsync(OperationValue operationValue, AsyncThread t);

	String stop();

}
