@echo off
mode con lines=22 cols=95
title slow_sql_statistics    -- made by Abe
echo.
echo.
echo ������Ҫͳ�Ƶ�ʱ����������� 1h30m
echo.
:select
set index=
set /p index=������:
set index=%index: =%
if "%index%" equ " =" set index=
if defined index (
    java -jar slow_sql_statistics.jar %index%
) else goto select
echo ��������˳�
pause>nul 2>nul