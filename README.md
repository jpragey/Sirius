# Sirius language
## Status
Currently it reaches the 'Hello world' level (and can't do much more yet).
Licence is [Apache v2](https://www.apache.org/licenses/LICENSE-2.0.html).

## Compilation
To compile it, you need java 11+, maven 3.6.0+ and some recent git.
To install the full JDK (the JRE alone is not enough):

```bash
sudo apt install openjdk-17-jdk git maven
```

If you want to (re)create the doc site, you will need a few more packages:

```bash
sudo apt install graphviz plantuml
```
Then get Sirius source and compile it:

```bash
git clone https://github.com/jpragey/sirius
cd sirius
mvn clean install
```

To create an "hello world" demo:

```bash
mkdir demo
cd demo
(cat - <<EOF
#!/bin/sh
module org.foo "1.0" {}
void myMain() {
	println("Hello from Sirius!");
}
EOF
) > hello.sirius

# Compile it
java -cp "../dist/lib/*" org.sirius.compiler.core.Main compile --module modulesDir --main org.foo.myMain hello.sirius

# Run it as a usual java 9 module
java --module-path ../dist/lib/org.sirius.sdk-0.0.1-SNAPSHOT.jar:../dist/lib/org.sirius.runtime-0.0.1-SNAPSHOT.jar:modulesDir/org/foo.jar \
  --module org.foo/org.foo.JvmPackage
```
NB: JvmPackage is a fake class that holds top-level functions - name to be changed at some point.

To create the doc site:

```bash
mvn site site:stage -P Site
```
And you get the result in `target/staging/`.


