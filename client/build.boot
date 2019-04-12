(def project 'clojure.scripting.client)
(def version "0.1.0-SNAPSHOT")

(set-env! :source-paths   #{"src"}
          :dependencies   '[[org.clojure/clojure "RELEASE"]])

(task-options!
 aot {:namespace   #{'clojure.scripting.client}}
 pom {:project     project
      :version     version
      :description "Client for Clojure scripts"
      :url         "https://github.com/jeroenvandijk/clojure-scripting"
      :scm         {:url "https://github.com/jeroenvandijk/clojure-scripting"}
      :license     {"Eclipse Public License"
                    "http://www.eclipse.org/legal/epl-v10.html"}}
 repl {:init-ns    'clojure.scripting.client}
 jar {:main        'clojure.scripting.client
      :file        (str "clojure-scripting-client-" version "-standalone.jar")})

(deftask build
  "Build the project locally as a JAR."
  [d dir PATH #{str} "the set of directories to write to (target)."]
  (let [dir (if (seq dir) dir #{"target"})]
    (comp (aot) (pom) (uber) (jar) (target :dir dir))))
