/*******************************************************************************
 *
 *	Copyright (c) 2008 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
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
 ******************************************************************************/

package junit;

import java.io.File;
import java.net.URL;

import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.pog.ProofObligation;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.syntax.ClassReader;
import org.overturetool.vdmj.typechecker.ClassTypeChecker;
import org.overturetool.vdmj.typechecker.TypeChecker;

import junit.framework.TestCase;

public class PogTest extends TestCase
{
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	private String[] expected =
	{
		"forall m1, m2 in set {{1 |-> 2}, {2 |-> 3}} &\n  forall d3 in set dom m1, d4 in set dom m2 &\n    d3 = d4 => m1(d3) = m2(d4)\n",
		"forall arg1:(int * int), arg2:seq of (int) &\n  (exists mk_(i, j):(int * int) & arg1 = mk_(i, j)) and\n  (exists [k]:seq of (int) & arg2 = [k])\n",
		"(forall mk_(i, j):(int * int), [k]:seq of (int) &\n  i in set dom m)\n",
		"forall arg1:(int * int) &\n  (exists mk_(i, j):(int * int) & arg1 = mk_(i, j))\n",
		"(forall mk_(i, j):(int * int) &\n  i in set dom m)\n",
		"(let x:nat1 = 123 in\n  -1 in set dom m)\n",
		"(let x:nat1 = 123 in\n  ((m(-1) > 0) =>\n    1 in set dom m))\n",
		"(let x:nat1 = 123 in\n  (not (m(-1) > 0) => \n    ((m(-2) > 0) =>\n      2 in set dom m)))\n",
		"(let x:nat1 = 123 in\n  (not (m(-1) > 0) => \n    (not (m(-2) > 0) => \n      (((x < 0) or ((x > 10) or (x = 100))) =>\n        3 in set dom m))))\n",
		"(let x:nat1 = 123 in\n  (not (m(-1) > 0) => \n    (not (m(-2) > 0) => \n      (not ((x < 0) or ((x > 10) or (x = 100))) => \n        999 in set dom m))))\n",
		"(forall a:int, b:int & (a < b) =>\n  a in set dom m)\n",
		"(forall a:int, b:int &\n  pre_prepostf(a, b) => post_prepostf(a, b, (a + b)))\n",
		"forall arg1:(int * int) &\n  pre_prepostfi(arg1) =>\n    (exists mk_(a, b):(int * int) & arg1 = mk_(a, b))\n",
		"(forall mk_(a, b):(int * int), c:(int * int) &\n  pre_prepostfi(mk_(a, b), c) => exists r:int & post_prepostfi(mk_(a, b), c, r))\n",
		"(forall x:seq of (int) &\n  (exists [a]:seq of (int) & [a] = (x ^ [-999]) =>\n  let [a] = (x ^ [-999]) in\n    a in set dom m))\n",
		"(forall x:seq of (int) &\n  (not exists [a]:seq of (int) & [a] = (x ^ [-999]) =>\n    (exists [a, b]:seq of (int) & [a, b] = (x ^ [-999]) =>\n    let [a, b] = (x ^ [-999]) in\n      (a + b) in set dom m)))\n",
		"(forall x:seq of (int) &\n  (not exists [a]:seq of (int) & [a] = (x ^ [-999]) =>\n    (not exists [a, b]:seq of (int) & [a, b] = (x ^ [-999]) =>\n      (exists [a] ^ [b]:seq of (int) & [a] ^ [b] = (x ^ [-999]) =>\n      let [a] ^ [b] = (x ^ [-999]) in\n        (a + b) in set dom m))))\n",
		"(forall x:seq of (int) &\n  (not exists [a]:seq of (int) & [a] = (x ^ [-999]) =>\n    (not exists [a, b]:seq of (int) & [a, b] = (x ^ [-999]) =>\n      (not exists [a] ^ [b]:seq of (int) & [a] ^ [b] = (x ^ [-999]) =>\n        m(999) in set dom m))))\n",
		"(forall x:seq of (int) &\n  (not exists [a]:seq of (int) & [a] = (x ^ [-999]) =>\n    (not exists [a, b]:seq of (int) & [a, b] = (x ^ [-999]) =>\n      (not exists [a] ^ [b]:seq of (int) & [a] ^ [b] = (x ^ [-999]) =>\n        999 in set dom m))))\n",
		"forall arg1:(int * int) &\n  (exists mk_(i, any1):(int * int) & arg1 = mk_(i, any1))\n",
		"(forall mk_(i, -):(int * int) &\n  exists x in set {m(1), 2, 3} & (m(x) < i))\n",
		"(forall mk_(i, -):(int * int) &\n  1 in set dom m)\n",
		"(forall mk_(i, -):(int * int) &\n  (forall x in set {m(1), 2, 3} &\n    x in set dom m))\n",
		"(forall mk_(i, -):(int * int) &\n  (forall x in set {m(1), 2, 3} & (m(x) < i) =>\n    x in set dom m))\n",
		"1 in set dom m\n",
		"(let x:int = m(1) in\n  x in set dom m)\n",
		"(forall x:int &\n  x in set dom m)\n",
		"1 in set dom m\n",
		"2 in set dom m\n",
		"(def x = m(1); y = m(2) in\n  x in set dom m)\n",
		"(def x = m(1); y = m(2) in\n  y in set dom m)\n",
		"exists1 x in set {1, 2, 3} & (x < 10)\n",
		"(forall n:nat &\n  n > 1 => forall arg:nat & pre_f1(arg) => pre_f1(f1(arg)))\n",
		"(forall x:(int -> int), n:nat &\n  n > 1 => forall arg:nat & pre_(x, arg) => pre_(x, x(arg)))\n",
		"(forall n:nat &\n  n = 0 or n = 1 or rng(m) subset dom(m))\n",
		"(forall x:map (int) to (int), n:nat &\n  n = 0 or n = 1 or rng(x) subset dom(x))\n",
		"forall arg:int & pre_f2(arg) => pre_f1(f2(arg))\n",
		"(forall x:(int -> int), y:(int -> int) &\n  forall arg:int & pre_(y, arg) => pre_(x, y(arg)))\n",
		"(forall x:(int -> int) &\n  forall arg:int & pre_f2(arg) => pre_(x, f2(arg)))\n",
		"(forall x:(int -> int) &\n  forall arg:int & pre_(x, arg) => pre_f1(x(arg)))\n",
		"rng(m) subset dom(m)\n",
		"(forall x:map (int) to (int), y:map (int) to (int) &\n  rng(y) subset dom(x))\n",
		"(forall x:map (int) to (int) &\n  rng(m) subset dom(x))\n",
		"(forall i:int &\n  pre_f1(i))\n",
		"(forall x:(int -> int) &\n  pre_(x, 123))\n",
		"(forall i:int &\n  i in set inds sq)\n",
		"(forall x:seq of (int) &\n  123 in set inds x)\n",
		"(forall s:set of (set of (int)) &\n  s <> {})\n",
		"(forall s:seq of (int) &\n  (not (s = []) => \n    s <> []))\n",
		"(forall i:int &\n  i <> 0)\n",
		"(forall i:int &\n  is_int((123 / i)))\n",
		"(forall i:int &\n  i <> 0)\n",
		"forall m1, m2 in set {{a |-> b} | a:int, b in set {1, 2, 3} & (a < 10)} &\n  forall d3 in set dom m1, d4 in set dom m2 &\n    d3 = d4 => m1(d3) = m2(d4)\n",
		"exists finmap1:map nat to (map (int) to (nat1)) &\n  forall a:int, b in set {1, 2, 3} &\n    (a < 10) => exists findex2 in set dom finmap1 & finmap1(findex2) = {a |-> b}\n",
		"exists finmap1:map nat to (int) &\n  forall a:int, b in set {1, 2, 3} &\n    (a < 10) => exists findex2 in set dom finmap1 & finmap1(findex2) = (a + b)\n",
		"(forall a:map (int) to (int), b:map (int) to (int) &\n  forall ldom1 in set dom a, rdom2 in set dom b &\n  ldom1 = rdom2 => a(ldom1) = b(rdom2))\n",
		"(forall x:int &\n  forall m1, m2 in set {{1 |-> 2}, {2 |-> 3}, {x |-> 4}} &\n    forall d3 in set dom m1, d4 in set dom m2 &\n      d3 = d4 => m1(d3) = m2(d4))\n",
		"forall m1, m2 in set {{{a} |-> a} | a in set {1, 2, 3} & (a < 10)} &\n  forall d3 in set dom m1, d4 in set dom m2 &\n    d3 = d4 => m1(d3) = m2(d4)\n",
		"forall m1, m2 in set {{1 |-> 2}, {2 |-> 3}} &\n  forall d3 in set dom m1, d4 in set dom m2 &\n    d3 = d4 => m1(d3) = m2(d4)\n",
		"(forall n:nat &\n  (not (n = 1) => \n    (n - 1) >= 0))\n",
		"dom {1 |-> false} subset inds [2, true, 7.8]\n",
		"is_(([2, true, 7.8] ++ {1 |-> false}), seq of ((bool | nat)))\n",
		"(forall t:((nat * nat * nat) | (nat * nat)) &\n  not is_(t, (nat * nat)))\n",
		"(forall u:U &\n  exists mk_(a, b):U & mk_(a, b) = u)\n",
		"(forall u:U &\n  is_(u, (nat * nat)))\n",
		"(is_([0, 1, 2, true], T1) and ((is_(0, bool))\nand (is_(1, bool))\nand (is_(2, bool)))) or\n(is_([0, 1, 2, true], T2) and inv_T2([0, 1, 2, true]) and ((0 > 0)\nand (true > 0)))\n",
		"(forall t:(T1 | T2) &\n  (is_([0, 1, 2, true], T1) and ((is_(0, bool))\n  and (is_(1, bool))\n  and (is_(2, bool)))) or\n  (is_([0, 1, 2, true], T2) and inv_T2([0, 1, 2, true]) and ((0 > 0)\n  and (true > 0))))\n",
		"(forall a:(T1 | T2 | int) &\n  (is_(a, T1)) or\n  (is_(a, T2) and inv_T2(a) and (is_(a, seq of (nat1)))))\n",
		"is_({1 |-> \"2\"}, inmap nat1 to seq of (char))\n",
		"(forall n:nat1, x:nat1 &\n  (not (n < 2) => \n    (n - 1) > 0))\n",
		"(forall n:nat1, x:nat1 &\n  (not (n < 2) => \n    (x - 1) > 0))\n",
		"(forall n:nat1, x:nat1 &\n  (not (n < 2) => \n    id(n, x) > id((n - 1), (x - 1))))\n",
		"(forall x:nat1, y:nat1 &\n  (x - y) >= 0)\n",
		"forall arg1:(nat1 * nat1) &\n  (exists mk_(n, x):(nat1 * nat1) & arg1 = mk_(n, x))\n",
		"(forall mk_(n, x):(nat1 * nat1) &\n  (not (n < 2) => \n    ((n - 1) > 0) and ((x - 1) > 0)))\n",
		"(forall mk_(n, x):(nat1 * nat1) &\n  (not (n < 2) => \n    id2(mk_(n, x)) > id2(mk_((n - 1), (x - 1)))))\n",
		"forall arg1:(nat1 * nat1) &\n  (exists mk_(x, y):(nat1 * nat1) & arg1 = mk_(x, y))\n",
		"(forall mk_(x, y):(nat1 * nat1) &\n  (x - y) >= 0)\n",
		"forall arg1:(nat1 * nat1) &\n  (exists mk_(n, x):(nat1 * nat1) & arg1 = mk_(n, x))\n",
		"(forall mk_(n, x):(nat1 * nat1) &\n  (not (n < 2) => \n    ((n - 1) > 0) and ((x - 1) > 0)))\n",
		"(forall mk_(n, x):(nat1 * nat1) &\n  (not (n < 2) => \n    id3(mk_(n, x)) LEX2> id3(mk_((n - 1), (x - 1)))))\n",
		"forall arg1:(nat1 * nat1) &\n  (exists mk_(x, y):(nat1 * nat1) & arg1 = mk_(x, y))\n",
		"1 in set dom m\n",
		"1 in set dom m\n",
		"2 in set dom m\n",
		"3 in set dom m\n",
		"while (x > 0) do ...\n",
		"iv := (iv + 1)\n"
	};

	public void testPOG() throws Exception
	{
		URL rurl = getClass().getResource("/pogtest/pog.vpp");
		String file = rurl.getPath();

		LexTokenReader ltr = new LexTokenReader(new File(file), Dialect.VDM_PP);
		ClassReader cr = new ClassReader(ltr);
		ClassList classes = cr.readClasses();

		TypeChecker typeChecker = new ClassTypeChecker(classes);
		typeChecker.typeCheck();
		TypeChecker.printErrors(Console.out);
		assertEquals("Type check errors", 0, TypeChecker.getErrorCount());

		ProofObligationList polist = classes.getProofObligations();
		assertEquals("POs generated", 88, polist.size());
		int i = 0;

		for (ProofObligation po: polist)
		{
//			Console.out.println(po.toString());
			assertEquals("PO #" + i+1, expected[i], po.value);
			i++;

//			Console.out.println("\"" + po.value.replaceAll("\n", "\\\\n") + "\",");
		}
	}
}
