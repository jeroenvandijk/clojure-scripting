client-jar:
	cd client && \
	boot build

kill-server-processes:
	cat bin/clojure-script-server-*.pid | xargs kill -9

server-jar:
	cd server && \
	boot build
