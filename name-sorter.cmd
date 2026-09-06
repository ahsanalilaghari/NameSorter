@echo off
rem Runs the built jar. Build it first with: mvnw.cmd package
java -jar "%~dp0target\name-sorter.jar" %*