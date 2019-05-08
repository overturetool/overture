theory DEFAULT
  imports VDMToolkit
begin

type_synonym Schedule = "(Period) \<rightharpoonup> Expert VDMSet
"


type_synonym Period = "VDMToken
"


record Expert =
        expert_expertid :: (ExpertId)
        expert_quali :: Qualification
 VDMSet

    


type_synonym ExpertId = "VDMToken
"


datatype Qualification = <Bio>| <Chem>| <Elec>| <Mech>


record Alarm =
        alarm_alarmtext :: char VDMSeq

        alarm_quali :: Qualification

    



definition
	NumberOfExperts :: "(Period) \<Rightarrow> Plant \<Rightarrow> VDMNat
"
    where
    "NumberOfExperts peri plant  \<equiv> card ((plant_schedule plant)peri)"


definition
	ExpertIsOnDuty :: "Expert \<Rightarrow> Plant \<Rightarrow> (Period) VDMSet
"
    where
    "ExpertIsOnDuty ex Plant_(sch, _)  \<equiv> {peri | peri \<in> dom (sch)  & (ex \<in> sch<peri>)}"


definition
	QualificationOK :: "Expert VDMSet
 \<Rightarrow> Qualification
 \<Rightarrow> \<bool>"
    where
    "QualificationOK exs reqquali  \<equiv> (exists ex \<in> exs & (reqquali \<in> (ex_quali ex)))"

record Plant =
        plant_schedule :: (Period) \<rightharpoonup> Expert VDMSet

        plant_alarms :: Alarm VDMSet

    



definition
	inv_Plant :: "Plant \<Rightarrow> \<bool>"
    where
    "inv_Plant Plant_(schedule, alarms) \<equiv> ((isa_invTrue (plant_schedule p) \<and> isa_invSetElems isa_invTrue (plant_alarms p)) \<and> (forall a \<in> alarms & (forall peri \<in> dom (schedule) & QualificationOK schedule<peri> (a_quali a))))"


definition
	pre_NumberOfExperts :: "(Period) \<Rightarrow> Plant \<Rightarrow> \<bool>"
    where
    "pre_NumberOfExperts peri plant  \<equiv> ((isa_invTrue peri \<and> isa_invTrue plant) \<and> (peri \<in> dom ((plant_schedule plant))))"


definition
	post_NumberOfExperts :: "(Period) \<Rightarrow> Plant \<Rightarrow> VDMNat
 \<Rightarrow> \<bool>"
    where
    "post_NumberOfExperts peri plant RESULT  \<equiv> (isa_invTrue peri \<and> (isa_invTrue plant \<and> isa_invVDMNat RESULT))"


definition
	pre_ExpertIsOnDuty :: "Expert \<Rightarrow> Plant \<Rightarrow> \<bool>"
    where
    "pre_ExpertIsOnDuty ex Plant_(sch, _)  \<equiv> (isa_invTrue ex \<and> isa_invTrue Plant_(sch, -))"


definition
	post_ExpertIsOnDuty :: "Expert \<Rightarrow> Plant \<Rightarrow> (Period) VDMSet
 \<Rightarrow> \<bool>"
    where
    "post_ExpertIsOnDuty ex Plant_(sch, _) RESULT  \<equiv> (isa_invTrue ex \<and> (isa_invTrue Plant_(sch, -) \<and> isa_invSetElems isa_invTrue RESULT))"


definition
	pre_ExpertToPage :: "Alarm \<Rightarrow> (Period) \<Rightarrow> Plant \<Rightarrow> \<bool>"
    where
    "pre_ExpertToPage a peri plant  \<equiv> ((isa_invTrue a \<and> (isa_invTrue peri \<and> isa_invTrue plant)) \<and> ((peri \<in> dom ((plant_schedule plant))) \<and> (a \<in> (plant_alarms plant))))"


definition
	post_ExpertToPage :: "Alarm \<Rightarrow> (Period) \<Rightarrow> Plant \<Rightarrow> Expert \<Rightarrow> \<bool>"
    where
    "post_ExpertToPage a peri plant r  \<equiv> ((isa_invTrue a \<and> (isa_invTrue peri \<and> (isa_invTrue plant \<and> isa_invTrue r))) \<and> ((r \<in> (plant_schedule plant)peri) \<and> ((a_quali a) \<in> (r_quali r))))"


definition
	pre_QualificationOK :: "Expert VDMSet
 \<Rightarrow> Qualification
 \<Rightarrow> \<bool>"
    where
    "pre_QualificationOK exs reqquali  \<equiv> (isa_invSetElems isa_invTrue exs \<and> inv_Qualification reqquali)"


definition
	post_QualificationOK :: "Expert VDMSet
 \<Rightarrow> Qualification
 \<Rightarrow> \<bool> \<Rightarrow> \<bool>"
    where
    "post_QualificationOK exs reqquali RESULT  \<equiv> (isa_invSetElems isa_invTrue exs \<and> (inv_Qualification reqquali \<and> isa_invTrue RESULT))"

end
