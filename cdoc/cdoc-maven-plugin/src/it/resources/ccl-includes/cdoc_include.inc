/**
 This is a description of the include file.
 */

/**
 @request
 @field person_id
    The ID of the person.
 */
record incRequest (
    1 person_id = f8
%i cclsource:test_include.inc
)

/**
 @reply
 @field status
    The status of the include file.
 */
record incReply (
    1 status = c1
)

declare some_sub(arg1 = f8, arg2 = vc) = c32

/**
 A subroutine.
 @param arg1
    The first argument.
 @param arg2
    The second argument.
 @returns
    "filler"
 */
subroutine some_sub(arg1, arg2)
    return("filler")
end