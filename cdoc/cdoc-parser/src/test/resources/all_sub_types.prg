drop program all_sub_types:dba go
create program all_sub_types:dba


/**
 This is an example script with all types of subroutine declarations, predeclared, in-line and undeclared.
 It has arguments and a bound transaction.
 
 @boundTransaction 98765
 @arg This is the first argument.
 @arg This is the second argument.
 */

    declare not_subroutine1 = i4 with protect, noconstant(0)
    declare declared_subroutine_doc (arg1 = vc) = i4 with protect
    declare declared_subroutine_no_doc (arg1 = i4 (ref), arg2 = f8) = vc with protect
    declare not_subroutine2 = f8 with protect, noconstant(0.0)


/**
  test1 structure
*/
free record test1
record test1 (
  1 field1 = f8 
  1 field2 = f8
)

/**
  @reply
  @field field
    A field
 */
free record test
record test (
    1 field = f8
%i cclsource:status_block.inc
%i cclsource:test.inc
    3 more_stuff[*]  ;;;cdoc should not bomb on this (unless it actuall parses test.inc to see it does not end at level 3.
        4 name = vc
        4 id = i4
)

    
    /**
     This is the request record structure.

     @request 
     @field first_list The first list
        @field first_list_ind1 Indicator1 in the first list
        @field first_list_ind2 Indicator2 in the first list
     @field second_list The second list
        @field second_list_vc1 The first name of the member of the second list
            @codeSet 387 The code set for second_list_vc
        @field second_list_vc2 The second name of the member of the second list
            @codeSet 387 The code set for second_list_vc
     @field shared_name The first instance of a shared-name field
        @value 1 Yahoo!
        @value 2 Too blue!
        @value 3 This time's the charm.
     @field shared_name_list
        A list that contains a member with a shared name
        @field shared_name The second field that has a shared name
     @field extra_documentation
        @value A yada yada
        @value B aday aday
        @value C aday yada
        @field extra_extra_documentation
    */
    record the_request (
        1 first_list[*]
            2 first_list_ind1 = i2
            2 first_list_ind2 = i2
        1 second_list[*]
            2 second_list_vc1 = vc
            2 second_list_vc2 = vc
        1 shared_name = c24
        1 shared_name_list[31]
            2 shared_name = f8
    )
    
    /**
     @reply
        What-what?
     @field success_ind The success indicator
    */
    record the_reply (
        1 success_ind = i2
    )
    
    /**
     * This really doesn't have to do with anything
     * @field char_field
     *      This is the character field
     */
    record med_data (
        1 char_field = c1
    )

    /**
    * This is a declared subroutine with documentation.
     @param arg1
        argument one
     @returns
        Some I4 value.
    */
    subroutine declared_subroutine_doc (arg1)
      call echo("declared_subroutine1")
    end


    /**
     * This is a simple subroutine declared in-line that has its documentation wrapped in leading stars.
     * @param person_id The ID of the person
     * @param birth_dt_tm
     *      The date and time of the birth that is retrieved.
     */
    subroutine (get_birth_dt_tm(person_id = f8, birth_dt_tm = dq8(REF)) = null)
        select p.birth_dt_tm from people p
        where p.person_id = person_id
        detail
            birth_dt_tm = p.birth_dt_tm
        with nocounter
    end


    subroutine declared_subroutine_no_doc (arg1, arg2)
      call echo("declared_subroutine2")
    end

    
    /**
     This is a subroutine declared in-line without leading stars in its documentation.
     It also has scope!
     @param person_id
        The ID of the person whose name is to be retrieved.
     @returns
        The last name of the person found
    */
    subroutine (get_last_name(person_id = f8) = vc with protect)
        declare last_name = vc
        select p.last_name
        from people p
        where p.person_id = person_id
        detail
            last_name = p.last_name
        with nocounter
        
        return(last_name)
    end

    subroutine undeclared_subroutine_no_doc(arg1, arg2)
      call echo("undeclared_subroutine_no_doc")
    end
    
    /**
     This subroutine has no declaration, so the information about it
     will be severely limited.
     @param person_id The ID of the person whose SSN is to be retrieved.
     @returns The given person's SSN.
     */
    subroutine get_ssn(person_id)
        declare ssn = vc
        select p.ssn
        from people p
        where p.person_id = person_id
        detail
            ssn = p.ssn
        with nocounter
        
        return(ssn)
    end

end
go
