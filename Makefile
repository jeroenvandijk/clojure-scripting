client-binary:
	cd client && \
	boot build && \
	~/bin/graalvm-ce-1.0.0-rc15/Contents/Home/bin/native-image -jar target/clojure-scripting-client-0.1.0-SNAPSHOT-standalone.jar; \
	mv clojure-scripting-client-0.1.0-SNAPSHOT-standalone ../bin/clojure-script.bin

kill-server-processes:
	cat bin/clojure-script-server-*.pid | xargs kill -9
	
server-jar:
	cd server && \
	boot build && \
	mv target/clojure-scripting-server-0.1.0-SNAPSHOT-standalone.jar ../bin/clojure-script-server.jar
		
self-hosted1-binary:
	cd self-hosted1 && \
	boot build && \
	~/bin/graalvm-ce-1.0.0-rc15/Contents/Home/bin/native-image  --language:js -jar target/clojure-scripting-client-0.1.0-SNAPSHOT-standalone.jar; \
		
self-hosted2-binary:
	cd self-hosted2 && \
	boot build && \
	~/bin/graalvm-ce-1.0.0-rc15/Contents/Home/bin/native-image  --language:js -jar target/clojure-scripting-client-0.1.0-SNAPSHOT-standalone.jar; \
		

	
	