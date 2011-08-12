package org.overturetool.VDM2JavaCG.Utilities;

import java.lang.Math;
import java.util.HashMap;
import java.util.Vector;

public class Utils {
	
	public static Boolean implication(Boolean p1, Boolean p2) {
		if (p1) {
			return p2;
		}
		else
			return true;
	}
	public static Boolean biimplication (Boolean p1, Boolean p2) {
		if (p1) {
			return p2;
		}
		else
			return !p2;
	}
	public static Integer div (Integer i1, Integer i2) {
		if ((i1/i2) < 0) {
			return (int) -Math.floor(Math.abs(-i1/i2));
		}
		else 
			return (int) Math.floor(Math.abs(i1/i2));
	}
	public static Integer rem (Integer i1, Integer i2) {
		return (i1 - i2 * (div(i1,i2)));
	}
	public static Integer mod (Integer i1,Integer i2) {
		return (int) (i1 - i2 * (Math.floor(i1/i2)));
	}
	public static <K> HashMap<K,K> StarStar(HashMap<K,K> hm, Integer i) {
		return CGCollections.Iteration(hm, i);
	}
	public static double StarStar(double x, double n) {
		return Math.pow(x, n);
	}
	public static <T> Vector<T> PlusPlus(Vector<T> v, HashMap<Integer, T> hm) {
		return CGCollections.SeqModification(v, hm);
	}
	public static <K,V> HashMap<K,V> PlusPlus(HashMap<K,V> h1, HashMap<K,V> h2) {
		return CGCollections.Override(h1, h2);
	}
	
	//------- Can not compare the type parameters of Set types, Map types, Seqtypes etc.-------------
	public static <T> boolean Is(T t, String str) {
		if (t.getClass().getSimpleName().equals(str)) {
			return true;
		}
		else if (str.contains(t.getClass().getSimpleName()))
			return true;
		else
			return false;
	}
}
