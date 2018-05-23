declare testEnforcePredeclare(null) = null

subroutine testEnforcePredeclare(null)
	;this should not cause an error - enforcePredeclare should only apply to the source code
	set undeclared_var = 0
end