if [ -n "$1" ] 
then
  startPoint="$1" 
else  
  startPoint="." 
fi
find "$startPoint" -regex ".*\(\.java\|i18n-resoureces\.properties\)$" -print | xargs cat | sed -e 's#".*"##g' | c:/cygwin64/usr/local/bin/aspell list --ignore=3 --run-together --run-together-limit=5 --add-wordlists=spelling-exclusions.txt --add-filter=email | sort | uniq > spelling-issues.log
#c:/cygwin64/usr/local/bin/aspell
sleep 5