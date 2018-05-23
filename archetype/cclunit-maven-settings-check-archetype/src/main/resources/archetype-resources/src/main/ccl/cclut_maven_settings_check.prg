drop program cclut_maven_settings_check go
create program cclut_maven_settings_check
  call echo("cclut_maven_settings_check")
  record reply(1 someVC = vc) with persistscript
  set reply->someVC = "some varchar"
end go