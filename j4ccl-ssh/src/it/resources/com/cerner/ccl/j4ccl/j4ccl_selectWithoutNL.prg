drop program j4ccl_selectwithoutnl go
create program j4ccl_selectwithoutnl 
  declare disp = vc with protect, noconstant("")
  select from code_value cv where cv.code_set = 8 detail disp = cv.display with nocounter
end go