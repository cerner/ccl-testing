drop program ccl_xml_translator go
create program ccl_xml_translator

/**
 @request
 @field programs
    A list of programs to be translated.
    @field program_name
       The program to be translated into XML.
 */
if(not validate(request))
    record request (
        1 programs[*]
            2 program_name = vc
    )
endif

/**
 @reply
 @field programs
    A list of programs that were translated.
    @field program_name
        The name of the program that was translated.
    @field translation_xml_file
        The location on the local server disk of a file containing the translated XML.
 */
if(not validate(reply))
    record reply (
        1 programs[*]
            2 program_name = vc
            2 translation_xml_file = vc
%i cclsource:status_block.inc
    )
endif

declare cxt_translateOutput = vc with protect, noconstant("")
declare cxt_errorMessage    = vc with protect, noconstant("")
declare cxt_loopIndex       = i4 with protect, noconstant(0)
declare cxt_stat            = i2 with protect, noconstant(0)

set reply->status_data.status = 'F'
set cxt_stat = alterlist(reply->programs, size(request->programs, 5))

for(cxt_loopIndex = 1 to size(request->programs, 5))
    set cxt_translateOutput = build(logical("cer_temp"), "/", currdbhandle, "_", 
        request->programs[cxt_loopIndex].program_name, ".xml")
    call parser(concat("translate into '", cxt_translateOutput, "' ", 
        request->programs[cxt_loopIndex].program_name, " with xml go"))

    if(error(cxt_errorMessage, 0) != 0)
        set reply->status_data.status = 'F'
        set reply->status_data.subeventstatus.TargetObjectValue = cxt_errorMessage
        go to exit_script
    endif
    
    set reply->programs[cxt_loopIndex].program_name = request->programs[cxt_loopIndex].program_name
    set reply->programs[cxt_loopIndex].translation_xml_file = cxt_translateOutput
endfor

set reply->status_data.status = 'S'

#exit_script

end
go
