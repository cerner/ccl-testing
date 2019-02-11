drop program j4ccl_testRequestReply go
create program j4ccl_testRequestReply
/*
 record request (
    1 vc_field = vc
 ) go
*/

/*
 record reply (
    1 vc_field = vc
 )
*/

set reply->vc_field = request->vc_field

end go