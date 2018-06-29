drop program maven_ccl_plugin_get_mode go
create program maven_ccl_plugin_get_mode
/**
 Retrieves the current optimizer mode
 */

/**
 @reply
 @field optimizer_mode
    The current, in-use Oracle optimizer mode.
 */
record reply (
    1 optimizer_mode = vc
)

select into "nl:"
    v.value
from v$parameter v
where v.name = 'optimizer_mode'
detail
    reply->optimizer_mode = v.value
with nocounter

end
go
