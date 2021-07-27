drop program j4ccl_testArgumentCall go
create program j4ccl_testArgumentCall

/* 
 * reply (
 *  1 values[1]
 *      2 f8Arg = f8
 *      2 singleQuoteArg = vc
 *      2 doubleQuoteArg = vc
 *      2 integerArg = i4
 *  1 types[4]
 *      2 argType = vc
 * )
 */

set reply->types[1]->argType = reflect($1)
set reply->types[2]->argType = reflect($2)
set reply->types[3]->argType = reflect($3)
set reply->types[4]->argType = reflect($4)
 
set reply->f8Arg = cnvtreal($1)
set reply->singleQuoteArg = $2
set reply->doubleQuoteArg = $3
set reply->integerArg = cnvtint($4)


end
go
