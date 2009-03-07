{"UseOS|startok|2" |-> ["let p  =new Httpd()
 , rl  =2 
 in os.addProcess(rl, p)
", "let p  =new Httpd()
 , rl  =2 
 in os.bootSequence(rl)
"], "UseOS|startok|3" |-> ["let p  =new Kerneld()
 , rl  =1 
 in os.addProcess(rl, p)
", "let p  =new Kerneld()
 , rl  =1 
 in os.bootSequence(rl)
"], "UseOS|startok|1" |-> ["let p  =new Kerneld()
 , rl  =2 
 in os.addProcess(rl, p)
", "let p  =new Kerneld()
 , rl  =2 
 in os.bootSequence(rl)
"], "UseOS|startok|6" |-> ["let p  =new Httpd()
 , rl  =2 
 in os.addProcess(rl, p)
", "let p  =new Httpd()
 , rl  =2 
 in os.bootSequenceList(rl)
"], "UseOS|startok|7" |-> ["let p  =new Kerneld()
 , rl  =1 
 in os.addProcess(rl, p)
", "let p  =new Kerneld()
 , rl  =1 
 in os.bootSequenceList(rl)
"], "UseOS|startok|4" |-> ["let p  =new Httpd()
 , rl  =1 
 in os.addProcess(rl, p)
", "let p  =new Httpd()
 , rl  =1 
 in os.bootSequence(rl)
"], "UseOS|startok|5" |-> ["let p  =new Kerneld()
 , rl  =2 
 in os.addProcess(rl, p)
", "let p  =new Kerneld()
 , rl  =2 
 in os.bootSequenceList(rl)
"], "UseOS|startok|8" |-> ["let p  =new Httpd()
 , rl  =1 
 in os.addProcess(rl, p)
", "let p  =new Httpd()
 , rl  =1 
 in os.bootSequenceList(rl)
"]}