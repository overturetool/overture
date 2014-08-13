/***************************************************************************
 *
 *	Copyright (c) 2009 IHA
 *
 *	Author: Kenneth Lausdahl and Augusto Ribeiro
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 **************************************************************************/

package org.overture.interpreter.runtime.validation;

import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.messages.rtlog.RTMessage.MessageType;
import org.overture.interpreter.runtime.ClassInterpreter;
import org.overture.interpreter.scheduler.AsyncThread;
import org.overture.interpreter.values.OperationValue;

public interface IRuntimeValidatior
{

	void init(ClassInterpreter classInterpreter);

	void validate(OperationValue operationValue, MessageType type);

	void bindSystemVariables(ASystemClassDefinition systemDefinition,
			IInterpreterAssistantFactory af);

	void validateAsync(OperationValue operationValue, AsyncThread t);

	String stop();

}
