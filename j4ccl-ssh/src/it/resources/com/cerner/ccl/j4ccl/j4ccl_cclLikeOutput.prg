drop program j4ccl_cclLikeOutput go
create program j4ccl_cclLikeOutput
/**
  This script produces output which looks like the CCL prompt string and it takes longer to execute than the default 
  expectationTimeout. It shows that the prompt lookalikes do not get interpreted as prompts since a timeout does not occur.
*/

  declare idx = i4 with protect, noconstant(0)
  
  for (idx = 1 to 100)
    select into 'nl:' from prsnl p, code_value c where p.username='SYSTEM' and c.code_value = p.position_cd with nocounter
  endfor
  select into "nl:" from dual detail
    for (idx = 1 to 100)
      call echo("\n  2)")
      call echo("  0)")
      call echo("   1)")
    endfor
  with nocounter
  call pause(25)
  call echo("everything made it")
  set reply->status_data.status = "S"

end go
