FROM clojure:boot-2.8.2

RUN apt-get -y update
RUN apt-get -y install build-essential

RUN useradd -ms /bin/bash clj; exit 0
WORKDIR /home/clj
RUN mkdir clojure-scripting
WORKDIR clojure-scripting
COPY . .
RUN chown -R clj:clj /home/clj
USER clj
WORKDIR /home/clj/clojure-scripting/
RUN boot --help

RUN make client-jar
RUN make server-jar

FROM oracle/graalvm-ce:19.2.0
LABEL maintainer "price clark <price@eucleo.com>"

RUN yum -y update
RUN yum -y install make
RUN gu install native-image

RUN mkdir -p /root/resources
WORKDIR /root/resources

COPY --from=0 /home/clj/clojure-scripting/client/target/clojure-scripting-client-0.1.0-SNAPSHOT-standalone.jar .
RUN native-image --report-unsupported-elements-at-runtime --initialize-at-build-time -J-Xmx3Ga -J-Xms3G -H:Name="clojure-script.bin" -jar clojure-scripting-client-0.1.0-SNAPSHOT-standalone.jar

COPY --from=0 /home/clj/clojure-scripting/bin/clojure-script-server.jar .
COPY --from=0 /home/clj/clojure-scripting/bin/clojure-script .
COPY --from=0 /home/clj/clojure-scripting/bin/clojure-script-server .
RUN mv clojure-scripting-client-0.1.0-SNAPSHOT-standalone clojure-script.bin
RUN rm clojure-scripting-client-0.1.0-SNAPSHOT-standalone.jar

CMD ["/bin/bash", "-c", "cp /root/resources/* /mnt && chown -R $(stat -c \"%u:%g\" $MNT) /mnt"]
