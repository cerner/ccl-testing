declare testDoNotRun(null) = null

subroutine testDoNotRun(null)
  declare stat = i4 with protect, noconstant(0)
  set stat = cclutAssertI4Equal(curref, 'this will cause a test failure.', 1, 0)
end
