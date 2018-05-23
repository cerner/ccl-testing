drop program j4ccl_test_init go
create program j4ccl_test_init
/**
 This script tests to make sure that uninitialized values
 match their original explicit initializations.
 */

if(not validate(request))
    record request (
        1 list_field[*]
            2 dummy_var = i4
        1 i2_field = i2
        1 i4_field = i4
        1 f8_field = f8
        1 vc_field = vc
        1 char_field = c16
        1 dq8_field = dq8
    )
endif

if(not validate(reply))
    record reply (
        1 list_matches = i2
        1 i2_matches = i2
        1 i4_matches = i2
        1 f8_matches = i2
        1 vc_matches = i2
        1 char_matches = i2
        1 dq8_matches = i2
    )
endif

if(size(request->list_field, 5) = 0)
    set reply->list_matches = 1
else
    set reply->list_matches = 0
endif

if(request->i2_field = 0)
    set reply->i2_matches = 1
else
    set reply->i2_matches = 0
endif

if(request->i4_field = 0)
    set reply->i4_matches = 1
else
    set reply->i4_matches = 0
endif

if(request->f8_field = 0.0)
    set reply->f8_matches = 1
else
    set reply->f8_matches = 0
endif

if(request->vc_field = "")
    set reply->vc_matches = 1
else
    set reply->vc_matches = 0
endif

if(request->char_field = "")
    set reply->char_matches = 1
else
    set reply->char_matches = 0
endif

if(cnvtdatetime(request->dq8_field) = cnvtdatetime('01-JAN-1900 00:00:00.000'))
    set reply->dq8_matches = 1
else
    set reply->dq8_matches = 0
endif

end go
