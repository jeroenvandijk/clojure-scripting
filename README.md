# Clojure scripting

Using Clojure for scripting... Yes we have Planck and Lumo, but what if we could have JVM Clojure with super fast boottime?

By using GraalVM to generate a thin client that talks to a JVM process via Clojure prepl we can have this!

## Demo
After the setup (see next section below) you will get this:

```
time examples/helloworld.clj
Hello World!
examples/helloworld.clj  0.01s user 0.01s system 65% cpu 0.035 total
```

Only the first time the server needs to be booted:
```
time examples/helloworld.clj
*** starting scripting server *** [1163ms]

Hello World!
examples/helloworld.clj  0.03s user 0.04s system 5% cpu 1.257 total
```
 
## Installation
 
Assumption is that you have `boot-clj` [[see instructions](https://github.com/boot-clj/boot#install)], `make` installed. And that you lein installed [leiningen](https://leiningen.org/#install)`.
 
```
make client-binary
make server-jar
```
 
You are now ready for some fast Clojure.
 
To kill the server:
 
```
make kill-server-processes
``` 

 
## Docker Installation
 
Assumption is that you have `docker` [[see instructions](https://docs.docker.com/install/)] installed.
 
```
./build.sh
```
 
 ```
./run.sh bin
 ```
 
You are now ready for some fast Clojure.
 
To kill the server:
 
```
make kill-server-processes
``` 


Copyright (c) Jeroen van Dijk, Adgoji and contributors

Distributed under the Eclipse Public License 1.0 (same as Clojure).
 
