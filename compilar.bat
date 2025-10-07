@echo off
echo COMPILADOR DO PROJETO DE OO

echo Antes de compilar, verifique se o Apache Maven esta instalado em seu computador.
pause

echo Compilando...
call mvn clean package

IF %ERRORLEVEL% NEQ 0 (
    echo.
    echo Erro em compilar o projeto.
    pause
    exit /b
)

echo.
echo Compilacao feita com sucesso!!

echo Inicializando SGH - FCTE
java -jar target\SistemaDeGestaoHospitalar-1.0-SNAPSHOT.jar

echo.
echo Finalizado...
pause