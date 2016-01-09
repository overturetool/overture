theory DEFAULT_POs
imports DEFAULT
begin

(* f: non-zero obligation @ in 'DEFAULT' (isapog.vdmsl) at line 4:12
(forall x:int, y:int & ((y <> 0) => (y <> 0))) *)
lemma POf1: "+|(forall x : @int, y : @int & (not ((^y^ <> 0)) or (^y^ <> 0)))|+" by vdm_auto_tac

end