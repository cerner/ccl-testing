declare testIt(null) = null with public

subroutine testIt(null)
  execute cclut_maven_settings_check with replace("REPLY", checkReply)
  set stat = cclutAssertVCEqual(CURREF, "expected some varchar", checkReply->someVC, "some varchar")
end