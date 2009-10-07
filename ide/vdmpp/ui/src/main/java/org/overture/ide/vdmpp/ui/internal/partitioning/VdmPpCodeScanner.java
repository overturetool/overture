package org.overture.ide.vdmpp.ui.internal.partitioning;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.dltk.ui.text.AbstractScriptScanner;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.overture.ide.ui.partitioning.IVdmColorConstants;

//import org.overture.ide.vdmsl.ui.scriptcolor.provider.IScriptColorProvider;
//import org.overture.ide.vdmsl.ui.scriptcolor.provider.IScriptColorTokenLocator;
//import org.overture.ide.vdmsl.ui.scriptcolor.provider.VdmSlKeywordRules;
import org.overture.ide.ui.scriptcolor.provider.*;

public class VdmPpCodeScanner extends AbstractScriptScanner implements IScriptColorTokenLocator
{


	private static List<IScriptColorProvider> providers = new Vector<IScriptColorProvider>();
	static
	{
		
		providers.add(new VdmKeywordRules(new VdmPpKeywords()));
	}



	public VdmPpCodeScanner(IColorManager manager, IPreferenceStore store)
	{
		super(manager, store);
		initialize();
	}

	@Override
	protected String[] getTokenProperties()
	{
		//return fgTokenProperties;
		List<String> tokenProperties = new Vector<String>();
		for(IScriptColorProvider s : providers){
			tokenProperties.addAll(s.getTokenProperties());
		}
		
		String[] p = new String[tokenProperties.size()];
		tokenProperties.toArray(p);
		return p;
	}

	@Override
	protected List createRules()
	{
		List<IRule> rules = new ArrayList<IRule>();
		IToken other = getColorToken(IVdmColorConstants.VDM_DEFAULT);
		
		for(IScriptColorProvider s : providers){
			rules.addAll(s.getRules(this));
		}

	//	rules.add(wordRule);

		// rules.add(new FloatNumberRule(number));

//		for (int i = 0; i < providers.length; i++)
//		{
//			IRule[] r = providers[i].getRules();
//			if (r != null)
//			{
//				for (int j = 0; j < r.length; j++)
//				{
//					rules.add(r[j]);
//				}
//			}
//		}
		setDefaultReturnToken(other);
		return rules;
	}



	public IToken getColorToken(String key)
	{
		return getToken(key);
	}
}
