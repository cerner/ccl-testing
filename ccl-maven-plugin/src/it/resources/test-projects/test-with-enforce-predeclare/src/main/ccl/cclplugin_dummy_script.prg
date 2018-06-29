drop program cclplugin_dummy_script go
create program cclplugin_dummy_script
  set an_undeclared_var = 0
  call echo("dummy material")
end
go
