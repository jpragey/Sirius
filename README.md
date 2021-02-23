# Sirius language
## Status
Currently it reaches the 'Hello world' level (and can't do much more yet).
Licence is [Apache v2](https://www.apache.org/licenses/LICENSE-2.0.html).

## Compilation
To compile it, you need java 11+, maven 3.6.0+ and some recent git.
To install the full JDK (the JRE alone is not enough):

```bash
sudo apt install openjdk-11-jdk
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
void main(String[] args) {
    println("Hello world!");
}
EOF
) > hello.sirius

java -cp "/home/jpragey/eclipse-workspace/Sirius/dist/lib/*" org.sirius.compiler.core.Main compile --module modulesDir hello.sirius
java -jar modulesDir/unnamed.jar 
```
To create the site (and doc):

```bash
mvn site site:stage
```
And you get the result in `target/staging/`.


