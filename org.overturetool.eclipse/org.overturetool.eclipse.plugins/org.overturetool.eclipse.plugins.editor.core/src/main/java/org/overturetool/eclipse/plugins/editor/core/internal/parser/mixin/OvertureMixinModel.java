package org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.IShutdownListener;
import org.eclipse.dltk.core.mixin.IMixinElement;
import org.eclipse.dltk.core.mixin.MixinModel;
import org.eclipse.dltk.ti.types.ClassType;
import org.overturetool.eclipse.plugins.editor.core.OvertureLanguageToolkit;
import org.overturetool.eclipse.plugins.editor.core.OverturePlugin;

public class OvertureMixinModel implements IShutdownListener {

	private static OvertureMixinModel instance;

	public static OvertureMixinModel getWorkspaceInstance() {
		synchronized (instances) {
			if (instance == null)
				instance = new OvertureMixinModel(null);
			return instance;
		}
	}

	private static final Map instances = new HashMap();

	public static OvertureMixinModel getInstance(IScriptProject project) {
		Assert.isNotNull(project);
		synchronized (instances) {
			OvertureMixinModel mixinModel = (OvertureMixinModel) instances.get(project);
			if (mixinModel == null) {
				mixinModel = new OvertureMixinModel(project);
				instances.put(project, mixinModel);
			}
			return mixinModel;
		}
	}

	/**
	 * @param key
	 * @return
	 */
	public static void clearKeysCache(String key) {
		synchronized (instances) {
			if (instance != null) {
				instance.getRawModel().clearKeysCache(key);
			}
			for (Iterator i = instances.values().iterator(); i.hasNext();) {
				OvertureMixinModel mixinModel = (OvertureMixinModel) i.next();
				mixinModel.getRawModel().clearKeysCache(key);
			}
		}
	}

	private final MixinModel model;

	private OvertureMixinModel(IScriptProject project) {
		model = new MixinModel(OvertureLanguageToolkit.getDefault(), project);
		OverturePlugin.getDefault().addShutdownListener(this);
	}

	public MixinModel getRawModel() {
		return model;
	}

	public OvertureMixinClass createOvertureClass(OvertureClassType type) {
		return (OvertureMixinClass) createOvertureElement(type.getModelKey());
	}

	public IOvertureMixinElement createOvertureElement(String key) {
		if (key.equals("Object")) { //$NON-NLS-1$
			return new OvertureObjectMixinClass(this, true);
		} else if (key.equals("Object%")) { //$NON-NLS-1$
			return new OvertureObjectMixinClass(this, false);
		}
		IMixinElement mixinElement = model.get(key);
		if (mixinElement != null) {
			return createOvertureElement(mixinElement);
		}
		return null;
	}

	public IOvertureMixinElement createOvertureElement(IMixinElement element) {
		Assert.isNotNull(element);
		if (element.getKey().equals("Object")) { //$NON-NLS-1$
			return new OvertureObjectMixinClass(this, true);
		} else if (element.getKey().equals("Object%")) { //$NON-NLS-1$
			return new OvertureObjectMixinClass(this, false);
		}
		Object[] objects = element.getAllObjects();
		if (objects == null)
			return null;
		for (int i = 0; i < objects.length; i++) {
			OvertureMixinElementInfo obj = (OvertureMixinElementInfo) objects[i];
			if (obj == null || obj.getObject() == null)
				continue;
			switch (obj.getKind()) {
			case OvertureMixinElementInfo.K_CLASS:
			case OvertureMixinElementInfo.K_SUPER:
			case OvertureMixinElementInfo.K_VIRTUAL:
				return new OvertureMixinClass(this, element.getKey(), false);
			case OvertureMixinElementInfo.K_MODULE:
				return new OvertureMixinClass(this, element.getKey(), true);
			case OvertureMixinElementInfo.K_METHOD:
				return new OvertureMixinMethod(this, element.getKey());
			case OvertureMixinElementInfo.K_ALIAS:
				return new OvertureMixinAlias(this, element.getKey());
			case OvertureMixinElementInfo.K_VARIABLE:
				return new OvertureMixinVariable(this, element.getKey());
			}
		}
		return null;
	}

	/*
	 * @see org.eclipse.dltk.core.IShutdownListener#shutdown()
	 */
	public void shutdown() {
		model.stop();
	}

}
