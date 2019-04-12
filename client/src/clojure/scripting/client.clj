(ns clojure.scripting.client
  (:gen-class)
  (:import [java.io
            InputStreamReader
            BufferedReader
            PrintWriter]
           [java.net Socket]))

;; GraalVM doesn't like reflections
(set! *warn-on-reflection* true)

(defn load-script [out f]
  (binding [*out* out]
    (prn (list 'do
               '(in-ns 'clojure.scripting.server)
               (list 'load-script f)))))

(require 'clojure.edn)

(defn handle-prepl-line [line]
  (let [{:keys [tag]
         value :val
         :as prepl-data} (clojure.edn/read-string line)]
    (case tag
      :out (do
             (print value)
             (flush))

      :ret (when (:exception prepl-data)
             (let [exception-data (clojure.edn/read-string value)
                   {:keys [exit-code
                           error-message]} (:data exception-data)]
               ;; We have limited printing facilities due to GraalVM, delegate to server process
               (when error-message
                 (println error-message))
               (System/exit (or exit-code 1))))

      ;; Nothing?
      nil)))

(defn read-output-stream [^java.io.BufferedReader in]
  (try
    (loop [c nil]
      (when-let [line (.readLine in)]
        (handle-prepl-line line)

        (recur line)))
    (System/exit 0)
    (catch Throwable t
      (println "Failure in loop " (ex-message t)))))

(def ^String host "127.0.0.1")

(defn connect-to-socket [port]
  (try
    (java.net.Socket. host ^long port)
    (catch java.net.ConnectException _)))

(defn connect-with-retry [port]
  (let [start (System/nanoTime)]
    (loop [i 1000]
      (if (zero? i)
        (throw (ex-info (str "Unable to connect within " (long (/ (- (System/nanoTime) start) 1000 10000)) "ms") {:host host :port port}))
        (if-let [connection (connect-to-socket port)]
          connection
          (do
            (Thread/sleep 2)
            (recur (dec i))))))))

(require 'clojure.java.shell)

(defn -main [^String server-command ^String port-arg ^String filename]
  (let [port (Long/parseLong port-arg)
        ^java.net.Socket s
        (or (connect-to-socket port)
            (do
              (print "*** starting scripting server ***")(flush)
              (let [start (System/nanoTime)
                    proc (.exec (Runtime/getRuntime) ^"[Ljava.lang.String;" (into-array [server-command (str port)]))
                    _ (.close (.getOutputStream proc))
                    s0 (connect-with-retry port)]
                (print (str " [" (long (/ (- (System/nanoTime) start) 1000 1000)) "ms]\n\n")) (flush)
                s0)))]
    (try
      (if (.isConnected s)
        (let [in (BufferedReader. (InputStreamReader. (.getInputStream s)))
              out (PrintWriter. (.getOutputStream s) true)]
          (let [script (.getAbsolutePath (clojure.java.io/file filename))]
            (load-script out script)
            (binding [*out* out]
              (println :repl/quit)))
          (read-output-stream in))
        (println "Not connected"))
      (finally
        (.close s)))))
