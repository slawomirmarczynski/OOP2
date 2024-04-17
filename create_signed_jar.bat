@echo off
setlocal

REM Skrypt BAT (MS DOS BATCH) do podpisywania pliku JAR.
REM Podpisywany jest jeden plik, nazwe (bez jar) nalezy podac jako parametr.
REM Plik musi byc w podkatalogu plugins.
REM W razie potrzeby zostanie tez utworzony nowy klucz do podpisu.
REM UWAGA: TO JEST JEDNYNIE DEMONSTRACJA TECHNIKI, NIE UZYWAC W PRODUKCJI.

set UNSIGNED_JAR=plugins\%1.jar
set SIGNED_JAR=plugins\%1signed.jar
set KEYSTORE=myPrivateKeystore.jks
set ALIAS=myAlias
set PASSWORD=123456
set PUBLIC_KEY=myKey.pub
set TRUSTSTORE=myTrustStore.jks

if not exist %UNSIGNED_JAR% (
    echo create_signed_jar jarfilename
    pause
    goto :end
)

if not exist %KEYSTORE% (
    echo Generowanie klucza...
    keytool -genkey -alias %ALIAS% -keyalg rsa -validity 366000 -keystore %KEYSTORE% -storepass %PASSWORD%
)

if not exist %PUBLIC_KEY% (    echo Eksportowanie certyfikatu...
    keytool -exportcert -keystore %KEYSTORE% -alias %ALIAS% -file %PUBLIC_KEY% -storepass %PASSWORD%
)

echo Importowanie certyfikatu...
keytool -import -keystore %TRUSTSTORE% -alias %ALIAS% -file %PUBLIC_KEY% -storepass %PASSWORD%

echo Podpisywanie JAR...
jarsigner -keystore %KEYSTORE% -signedjar %SIGNED_JAR% %UNSIGNED_JAR% %ALIAS% -storepass %PASSWORD%

echo Werifikacja podpisu JAR...
jarsigner %SIGNED_JAR% -verify -verbose -certs

:end
endlocal
goto :eof
