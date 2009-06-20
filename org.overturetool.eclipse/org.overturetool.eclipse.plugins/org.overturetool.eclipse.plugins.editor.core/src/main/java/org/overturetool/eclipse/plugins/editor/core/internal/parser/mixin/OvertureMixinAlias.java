package org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.mixin.IMixinElement;
import org.eclipse.dltk.core.mixin.MixinModel;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.OvertureAliasExpression;

public class OvertureMixinAlias implements IOvertureMixinElement {

	private final IMixinElement element;
	private final OvertureMixinModel model;
	private final OvertureAliasExpression alias;
	private final ISourceModule sourceModule;

	public OvertureMixinAlias(OvertureMixinModel model, String key) {
		this.model = model;
		element = model.getRawModel().get(key);
		Assert.isNotNull(element);
		ISourceModule[] sourceModules = element.getSourceModules();
		ISourceModule sourceModule2 = null;
		OvertureAliasExpression alias2 = null;
		for (int j = 0; j < sourceModules.length; j++) {
			ISourceModule module = sourceModules[j];
			Object[] objects = element.getObjects(module);
			for (int i = 0; i < objects.length; i++) {
				OvertureMixinElementInfo info = (OvertureMixinElementInfo) objects[i];
				if (info.getKind() == OvertureMixinElementInfo.K_ALIAS) {
					alias2 = (OvertureAliasExpression) info.getObject();
					sourceModule2 = module;
					break;
				}
			}
		}
		alias = alias2;
		sourceModule = sourceModule2;
		Assert.isNotNull(alias);
	}

	public String getOldKey() {
		String old = alias.getOldValue();
		return element.getParent().getKey() + MixinModel.SEPARATOR + old;
	}

	public IOvertureMixinElement getOldElement() {
		return model.createOvertureElement(getOldKey());
	}

	public String getKey() {
		return element.getKey();
	}

	public String getNewName() {
		return alias.getNewValue();
	}

	public OvertureAliasExpression getAlias() {
		return alias;
	}

	public ISourceModule getSourceModule() {
		return sourceModule;
	}

}
