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
package org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin;

import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ti.IContext;
import org.eclipse.dltk.ti.ISourceModuleContext;
import org.eclipse.dltk.ti.goals.GoalEvaluator;
import org.eclipse.dltk.ti.goals.IGoal;

public abstract class OvertureMixinGoalEvaluator extends GoalEvaluator {

	protected final OvertureMixinModel mixinModel;

	/**
	 * @param goal
	 */
	public OvertureMixinGoalEvaluator(IGoal goal) {
		super(goal);
		final IContext context = goal.getContext();
		if (context instanceof ISourceModuleContext) {
			ISourceModule sourceModule = ((ISourceModuleContext) context).getSourceModule();
			mixinModel = OvertureMixinModel.getInstance(sourceModule.getScriptProject());
		} else {
			mixinModel = OvertureMixinModel.getWorkspaceInstance();
		}
	}

}
