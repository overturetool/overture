package org.overture.ast.assistant;

import org.overture.ast.node.INode;

/**
 * {@link IAstAssistant} is an empty interface that is used to identify assistants
 * in Overture.
 * <br><br>
 * Assistants are defined as methods that:
 * - are used in visitor cases as part of an Overture analysis
 * - are used in 2 or more classes
 * - take {@link INode} objects as input
 * 
 * @author ldc
 *
 */
public interface IAstAssistant {

}
