drop program cdoc_cli_script go
create program cdoc_cli_script
/**
 This is the definition of a script that takes command-line arguments.
 @arg This should be the first argument.
 @arg This should be the second argument.
 */
 
declare hasDeclaration(arg1 = c23, arg2 = i4(REF)) = c32
declare hasNoArgs(null) = i2

execute example_test_script

set valueName = "example_execute_value"
execute value(valueName)

/**
 This is a subroutine with a declaration.
 @param arg1
    This is the first argument of the subroutine.
 @param arg2
    This is the second argument of the subroutine.
 @returns The word "useless!"
 */
subroutine hasDeclaration(arg1, arg2)
  call echo("This really doesn't serve much of a purpose")
  return ("useless!")
end

/**
 This subroutine has no declaration.
 @param noDeclarationArg
    This is the only argument in this subroutine.
 @returns 0
 */
subroutine hasNoDeclaration(noDeclarationArg)
  return(0)
end

/**
 This subroutine takes no arguments.
 @returns The value of -1
 */
subroutine hasNoArgs(null)
    return(-1)
end

end
go
