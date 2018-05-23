/**
 This is an include file.
 */

/**
 @request
 @field person_id
    The ID of the person.
 */
record includeRequest (
    1 person_id = f8
)

/**
 @reply
 @field status
    The status of the call.
    @value "S" The call succeeded.
    @value "F" The call failed.
 */
record includeReply (
    1 status = c1
)

declare included_sub(arg1 = f8) = null

subroutine included_sub(arg1)
    call echo("filler")
end
