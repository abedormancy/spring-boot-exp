@echo off
mode con lines=22 cols=95
title slow_sql_statistics    -- made by Abe
echo.
echo.
echo 输入需要统计的时间度量，例如 1h30m
echo.
:select
set index=
set /p index=请输入:
set index=%index: =%
if "%index%" equ " =" set index=
if defined index (
    java -jar slow_sql_statistics.jar %index%
) else goto select
echo 按任意键退出
pause>nul 2>nul