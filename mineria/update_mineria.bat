rem cd src
rem jar -cvf mineria_src.jar mineria
rem mv mineria_src.jar ../../lib


rem cd ..\bin
rem jar -cvf mineria.jar mineria
rem mv mineria.jar ../../lib

rem cd ..
cd src
"C:\Archivos de programa\Java\jdk1.5.0_07\bin\jar" -cvf mineria_src.jar mineria
move mineria_src.jar ..\..\lib


cd ..\bin
"C:\Archivos de programa\Java\jdk1.5.0_07\bin\jar" -cvf mineria.jar mineria
move mineria.jar ..\..\lib

cd ..

