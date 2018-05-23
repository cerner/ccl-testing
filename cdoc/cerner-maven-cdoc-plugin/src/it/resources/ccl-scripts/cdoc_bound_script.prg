drop program cdoc_bound_script go
create program cdoc_bound_script
/**
 This is a script that is bound to a transaction.
 @boundTransaction 12349876
 */
 
/**
 This is the request record structure
 @request
 @field rootChar
    This is a character field at the root of the record structure.
 @field rootList
    This is a list at the root of the record structure
    @field nested_field
        This is an I2 in the nested list
        @value 0 False
        @value 1 True
        @optional
    @field nested_list
        This is a list nested within a list - it's almost I N C E P T I O N
        @field nested_nested_field
            A date field within the nested list
 @field rootF8
    This is an F8 in the root of the record structure.
    @codeSet 81 This is codeset 81.
    @codeSet 237 What does this codeset do?
 */
record cdocRequest (
    1 rootChar = c18
    1 rootList[*]
        2 nested_field = i2
        2 nested_list[1]
            3 nested_nested_field = dq8
            3 inception_list[*]
                4 inception_ind = i2
        2 nested_dq8 = dq8
    1 rootF8 = f8
)

/**
 The reply record structure
 @reply
 @field status
    A single-character status indicator of the result of this script.
 */
record cdocReply (
    1 status = c1
%i cclsource:status_block.inc
)

set stat = callprg("test_script")

declare some_subroutine(arg1 = f8, arg2 = c234) = c74

/**
 A subroutine.
 @param arg1 Argument 1
 @param arg2 Argument 2
 @returns
    74 characters
 */
subroutine some_subroutine(arg1, arg2)
    call echo("filler")
    return("74 characters")
end

end
go
