(ns clojure.scripting.client
  (:gen-class)
  (:import [java.io
            InputStreamReader
            BufferedReader
            PrintWriter]
           [java.net Socket]
           [javax.script
            ScriptEngineManager
            ScriptEngine
            ScriptException
            SimpleBindings
            Compilable]

           )
  )

;; GraalVM doesn't like reflections
(set! *warn-on-reflection* true)

;; java -jar target/clojure-scripting-client-0.1.0-SNAPSHOT-standalone.jar examples/hello.js
;; "hi 100000 something ,,, hello"

;; With GraalVM
;; ./clojure-scripting-client-0.1.0-SNAPSHOT-standalone examples/hello.js
;; Exception in thread "main" java.lang.IllegalArgumentException: The value 'DynamicObject<JSFunction>@66bfe312' cannot be passed from one context to another. The current context is 0x26a47684 and the argument value originates from context 0x22f1de0e.
;; 	at com.oracle.truffle.polyglot.PolyglotLanguageContext.migrateValue(PolyglotLanguageContext.java:804)
;; 	at com.oracle.truffle.polyglot.PolyglotLanguageContext.migrateHostWrapper(PolyglotLanguageContext.java:815)
;; 	at com.oracle.truffle.polyglot.PolyglotLanguageContext.toGuestValue(PolyglotLanguageContext.java:780)
;; 	at com.oracle.truffle.polyglot.PolyglotLanguageContext$ToGuestValueNode.doCached(PolyglotLanguageContext.java:666)
(defn -main [& [f]]
  (let [script-engine (.getEngineByName (ScriptEngineManager.) "js")

        FUNCTIONS "function() { return \"hello\"; } "
        compiled (.compile ^Compilable (cast Compilable script-engine) FUNCTIONS)

        ^String script (slurp f)
        sayHello (.eval compiled)]
    (try
      (let [bindings (doto (SimpleBindings.)
                       (.put "sayHello" sayHello))]
        (prn (.eval ^ScriptEngine script-engine script, bindings)))
      (catch ScriptException e
        (println "Error " (.getMessage e)))
      )))



;; Another attempt, but not sure how this works

#_ (import '[org.graalvm.polyglot Context])
#_(defn -main [& [f]]
  (prn "---")
  (let [context (Context/create (into-array String ["js"]))
        _ (prn "0")
        jsBindings (.getBindings context "js")
        _ (prn "1")
        _ (.. context (eval "js" "var foo = function() { return 42; }"))
        ]
    _ (prn "2")

    (prn  ( .getMember jsBindings "foo") )))
