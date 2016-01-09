theory DEFAULT
  imports utp_vdm
begin

vdmefun f
  inp x :: "@int" and y :: "@int"
  out "@real"
  is "((1.0 * ^x^) / ^y^)"

end