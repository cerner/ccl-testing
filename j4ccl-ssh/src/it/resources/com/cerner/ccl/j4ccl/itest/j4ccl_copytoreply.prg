drop program j4ccl_copyToReply go
create program j4ccl_copyToReply

/*
 * request (
 *  1 source = vc
 * )
 */
 
/*
 * reply (
 *  1 target = vc
 * )
 */

set reply->target = request->source

end go