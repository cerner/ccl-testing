drop program sample_script_with_competition:dba go
create program sample_script_with_competition:dba
/**
  This is documentation for the script
*/
/*
    This is a comment that resemble a 
    CREATE PROGRAM line which we don't want to find
*/
    call echo("sample_script_with_competition");
end go