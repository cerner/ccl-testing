drop program no_top_level_doc go
create program no_top_level_doc

;create program wtf -- if you really must have this situation, just put a non-blank line between the create program and the doc.
/**
 This is actually documentation belonging to the subroutine
*/
subroutine test(void)
    call echo("hello")
end

end
go
