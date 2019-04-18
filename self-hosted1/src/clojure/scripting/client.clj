(ns clojure.scripting.client
  (:gen-class)
  (:import [java.io
            InputStreamReader
            BufferedReader
            PrintWriter]
           [java.net Socket]
           [javax.script
            ScriptEngineManager
            ScriptException
            SimpleBindings]))

;; GraalVM doesn't like reflections
(set! *warn-on-reflection* true)

(defn -main [& [^String engine f]]
  (prn "Version 0" "engine" engine)
  (let [script-engine (.getEngineByName (ScriptEngineManager.) engine)
        bindings (SimpleBindings.)
        ^String script (slurp f)]
    (try
      (prn (.eval script-engine script, bindings))
      (catch ScriptException e
        (println "Error " (.getMessage e)))
      )))
