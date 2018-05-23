;**************************************************************************************************
; INC Name: test_all_functions.inc
; Component Tested: cclut_simple_math.prg
; Description: Example automated test to test the cclut_simple_math.prg subroutines
 
;*************************************************************************************************
 
;**************************************************************************************************
;** Declare Subroutines
;**************************************************************************************************
;NOTE: Declare all subroutines
declare setup(dummyVar = i2) = null
declare testAdd(dummyVar = i2) = null
declare testSubtract(dummyVar = i2) = null
declare testMultiply(dummyVar = i2) = null
declare testDivide(dummyVar = i2) = null
declare teardown(dummyVar = i2) = null

;**************************************************************************************************
;** Declare Record Structures
;**************************************************************************************************
free record simpleMathRequest
record simpleMathRequest(
  1 numberOne = i4
  1 numberTwo = i4
  1 operation = vc ;ADD, SUBTRACT, MULTIPLY, DIVIDE
)

free record simpleMathReply
record simpleMathReply(
  1 result = f8
%i cclsource:status_block.inc
)

;Used in one of the automated tests to showcase a CCL-E error due to a missing item in the reply
free record bogusReply
record bogusReply(
%i cclsource:status_block.inc
)

;**************************************************************************************************
;** Subroutine Implementations
;**************************************************************************************************
;*****************************
;** SETUP
;*****************************
subroutine setup(dummyVar)
  declare stat = i4 with protect, noconstant(0)
  set stat = initrec(simpleMathRequest)
  set stat = initrec(simpleMathReply)
end ;setup
 
 
 
;***********************************************************************
;Scenario: Test Addition 1 + 2 = 3
;***********************************************************************
subroutine testAdd(dummyVar)
  set simpleMathRequest->numberOne = 1
  set simpleMathRequest->numberTwo = 2
  set simpleMathRequest->operation = "ADD"
 
  execute cclut_simple_math with replace("REQUEST", SIMPLEMATHREQUEST), replace("REPLY", SIMPLEMATHREPLY)
 
  ;Add assert statement to ensure that result is 3
  call cclutAssertf8Equal(CURREF,"testAdd", 3.0, simpleMathReply->result)
end
 
 
;***********************************************************************
;Scenario: Test Subtraction 3 - 2 = 1
;***********************************************************************
subroutine testSubtract(dummyVar)
  set simpleMathRequest->numberOne = 3
  set simpleMathRequest->numberTwo = 2
  set simpleMathRequest->operation = "SUBTRACT"
 
  execute cclut_simple_math with replace("REQUEST", SIMPLEMATHREQUEST), replace("REPLY", SIMPLEMATHREPLY)
 
  ;Add assert statement to ensure that result is 1
  call cclutAssertf8Equal(CURREF,"testSubtract", 1.0, simpleMathReply->result)
end
 
;***********************************************************************
;Scenario: Test Multiplication 2 * 3 = 6
;***********************************************************************
subroutine testMultiply(dummyVar)
  set simpleMathRequest->numberOne = 2
  set simpleMathRequest->numberTwo = 3
  set simpleMathRequest->operation = "MULTIPLY"

  ;A runtime error should occur because cclut_simple_math accesses reply fields not contained in bogusReply 
  execute cclut_simple_math with replace("REQUEST", SIMPLEMATHREQUEST), replace("REPLY", bogusReply)
 
  ;Add assert statement to ensure that result is 6
  call cclutAssertf8Equal(CURREF,"testMultiply", 6.0, simpleMathReply->result)
end
 
;***********************************************************************
;Scenario: Test Division 6 / 2 = 3
;***********************************************************************
subroutine testDivide(dummyVar)
  set simpleMathRequest->numberOne = 6
  set simpleMathRequest->numberTwo = 2
  set simpleMathRequest->operation = "DIVIDE"
 
  execute cclut_simple_math with replace("REQUEST", SIMPLEMATHREQUEST), replace("REPLY", SIMPLEMATHREPLY)
 
  ;Add assert statement to ensure that result is 3
  call cclutAssertf8Equal(CURREF,"testDivide", 3.0, simpleMathReply->result)
end
  
;*****************************
;** TEARDOWN
;*****************************
subroutine teardown(errorMessage)
  rollback
end ;teardown
 
