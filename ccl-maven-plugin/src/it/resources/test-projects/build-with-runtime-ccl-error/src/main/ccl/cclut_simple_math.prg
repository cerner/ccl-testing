drop program cclut_simple_math go
create program cclut_simple_math
 
;**********************************************************************
;DECLARE INCLUDE FILES
;********************************************************************** 
 
;**********************************************************************
;DECLARE RECORD STRUCTURES
;**********************************************************************
/*
record request(
  1 numberOne = i4
  1 numberTwo = i4
  1 operation = vc ;ADD, SUBTRACT, MULTIPLY, DIVIDE
)
*/
 
if(not(validate(reply)))
record reply
(
1 result = f8
%i cclsource:status_block.inc
)
endif
 
;**********************************************************************
;DECLARE SUBROUTINES
;**********************************************************************
declare addNumbers(pNumOne = i4, pNumTwo = i4) = f8
declare subtractNumbers(pNumOne = i4, pNumTwo = i4) = f8
declare multiplyNumbers(pNumOne = i4, pNumTwo = i4) = f8
declare divideNumbers(pNumOne = i4, pNumTwo = i4) = f8
 
 
;**********************************************************************
;BEGIN MAIN LOGIC
;**********************************************************************
set reply->status_data.status = 'S'
case (cnvtupper(trim(request->operation,3)))
of "ADD":
  set reply->result =  addNumbers(request->numberOne, request->numberTwo)
of "SUBTRACT":
  set reply->result = subtractNumbers(request->numberOne, request->numberTwo)
of "MULTIPLY":
  set reply->result = multiplyNumbers(request->numberOne, request->numberTwo)
of "DIVIDE":
  set reply->result = divideNumbers(request->numberOne, request->numberTwo)
else
  set reply->status_data.status = 'F'
endcase
 
go to exit_script
 
;pNumTwo is added to pNumOne
subroutine addNumbers(pNumOne, pNumTwo)
  declare result = f8 with protect, noconstant(0.0)
  set result = pNumOne + pNumTwo
  return(result)
end
 
;pNumTwo is subtracted from pNumOne
subroutine subtractNumbers(pNumOne, pNumTwo)
  declare result = f8 with protect, noconstant(0.0)
  set result = pNumOne - pNumTwo
  return(result)
end
 
;pNumOne is multiplied by pNumTwo
subroutine multiplyNumbers(pNumOne, pNumTwo)
  declare result = f8 with protect, noconstant(0.0)
  set result = pNumOne * pNumTwo
  return(result)
end
 
;pNumOne is divided by pNumTwo
;Division by 0 returns -1
subroutine divideNumbers(pNumOne, pNumTwo)
  declare result = f8 with protect, noconstant(0.0)
  if(pNumTwo = 0)
    set result = -1
  else
    set result = pNumOne / pNumTwo
  endif
 
  return(result)
end
 
#exit_script
 
end
go