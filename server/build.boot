(def project 'clojure.scripting.server.main)
(def version "0.1.0-SNAPSHOT")

(set-env! :source-paths   #{"src"}
          :dependencies   '[[org.clojure/clojure "RELEASE"]
                            [seancorfield/boot-tools-deps "0.4.7"]
                            ])

(def jar-file (str "clojure-scripting-server-" version "-standalone.jar"))

(task-options!
 aot {:namespace   #{'clojure.scripting.server.main 'clojure.scripting.server.lib}}
 pom {:project     project
      :version     version
      :description "Backend for Clojure scripts"
      :url         "https://github.com/jeroenvandijk/clojure-scripting"
      :scm         {:url "https://github.com/jeroenvandijk/clojure-scripting"}
      :license     {"Eclipse Public License"
                    "http://www.eclipse.org/legal/epl-v10.html"}}
 repl {:init-ns    'clojure.scripting.server.main}
 jar {:main        'clojure.scripting.server.main
      :file        jar-file})

(require '[boot-tools-deps.core :refer [deps]])

(deftask build
  "Build the project locally as a JAR."
  [d dir PATH #{str} "the set of directories to write to (target)."]
  (let [dir (if (seq dir) dir #{"target"})]
    (comp (deps :quick-merge true) (aot :all true) (pom) (uber) (jar :file jar-file) (sift :include #{(re-pattern jar-file)}) (target :dir dir))))


