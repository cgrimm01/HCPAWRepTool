@echo off
IF [%1]==[] goto start
mode con: cols=130 lines=100
java -jar hcpawreptool.jar -h
goto :eof

:start
cmd /K call start_cmd.bat 1

