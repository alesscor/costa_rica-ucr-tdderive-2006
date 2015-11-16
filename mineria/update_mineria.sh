#!/bin/sh
cd src
jar -cvf mineria_src.jar mineria
mv mineria_src.jar ../../lib


cd ../bin
jar -cvf mineria.jar mineria
mv mineria.jar ../../lib

cd ..
