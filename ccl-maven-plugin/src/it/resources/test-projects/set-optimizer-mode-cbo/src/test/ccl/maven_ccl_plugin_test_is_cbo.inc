declare testIsCbo(null) = null

/**
 Tests to make sure that the current environment is set to CBO
 */
subroutine testIsCbo(null)
    free record testReply
    record testReply (
        1 optimizer_mode = vc
    )
    execute maven_ccl_plugin_get_mode with replace("REPLY", "TESTREPLY")
    declare stat = i4 with protect, noconstant(0)
    set stat = cclutAssertvcEqual(curref, 'Checking for CBO', 'ALL_ROWS', testReply->optimizer_mode)
end
