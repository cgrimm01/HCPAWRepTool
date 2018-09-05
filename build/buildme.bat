@pushd build
javac -d . -cp ../lib/* ../src/*.java 
jar xvf ../lib/commons-cli-1.3.1.jar 
jar xvf ../lib/commons-lang-2.1.jar 
jar xvf ../lib/wink-json4j-1.4.jar 
jar cvfm HCPAWRepTool.jar META-INF/HMANIFEST.MF -C ./ .
copy HCPAWRepTool.jar ..\bin
@popd