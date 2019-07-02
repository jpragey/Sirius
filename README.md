# Sirius language
## Status
Currently it reaches the 'Hello world' level (and can't do much more yet).
Licence is [Apache v2](https://www.apache.org/licenses/LICENSE-2.0.html).

## Compilation
To compile it, you need java 11, maven 3.6.0+ and some recent git.

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

java -cp ../dist/lib/ -jar ../dist/lib/org.sirius.compiler-0.0.1-SNAPSHOT.jar compile --module modulesDir hello.sirius
java -jar modulesDir/unnamed.jar 
```
