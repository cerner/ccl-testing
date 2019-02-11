drop program sample_script go
create program sample_script
/**
    Script to demonstrate that whitenoise
     - acknowleges a variable usage made within a report writer section (varA is not unused).
     - acknowleges a subroutine call made within a report writer section (var0 is not unused).
     - acknowleges variable usages through descending scopes (var4 is not unused).
     - recognizes when there is an intervening variable declaration (global var2 is unused b/c local var2 in sub2 hides it).
*/

    declare varA = i4 with protect, noconstant(0)
    declare var0 = i4 with protect, noconstant(0)
    declare var2 = i4 with protect, noconstant(0)
    declare var4 = i4 with protect, noconstant(0)

    subroutine(sub0(null) = null)
        call echo(var0)
    end

    subroutine(sub1(null) = null)
        call sub2(null)
    end
    subroutine(sub2(null) = null)
        declare var2 = i4 with protect, noconstant(0)
        call sub3(null)
    end
    subroutine(sub3(null) = null)
        call sub4(null)
    end
    subroutine(sub4(null) = null)
        call echo(var2)
        call echo(var4)
    end


    select into noforms from dual detail call echo(varA) with nocounter
    select into noforms from dual detail call sub0(null) with nocounter


    call sub1(null)

end go
