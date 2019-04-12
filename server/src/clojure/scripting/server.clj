(ns clojure.scripting.server
  (:require [clojure.datafy]
            [clojure.pprint]
            [clojure.core.server]
            [clj-pid.core :as pid]))

(defn- with-ns-isolation [filename f]
  (let [prev-ns (.getName *ns*)
        temp-ns-sym (symbol (str filename))
        temp-ns (create-ns temp-ns-sym)]
    (try
      (in-ns temp-ns-sym)
      (require '[clojure.core :refer :all])
      (require '[clojure.scripting.lib :refer :all])
      (f)
      (finally
        (in-ns prev-ns)
        (remove-ns temp-ns-sym)))))

(defn- throw-execution-error [ex]
  (throw (ex-info "Execution error (see data)"
                  {:exception ex
                   :error-message (with-out-str
                                    (println)
                                    (println "Exception:")
                                    (clojure.pprint/pprint ex))
                   :exit-code 1})))


(defn load-script [f]  
  (let [old-security (System/getSecurityManager)
        custom-security (proxy [java.lang.SecurityManager] []
                          (checkExit [status]
                            (throw (SecurityException. "See cause"
                                                       (ex-info "status wrapper" {::exit-code status}))))
                          (checkPermission
                            ([_])
                            ([_ _])))]

    (System/setSecurityManager custom-security)

    (try
      (with-ns-isolation f #(load-file f))

      (catch Throwable e
        (let [ex (clojure.datafy/datafy e)]
          (if-let [status (get-in ex [:data ::exit-code])]
            (throw (ex-info "No real exception: System exit call "
                            {:exception ex
                             :exit-code status}))
            (throw-execution-error ex))))

      (finally
        (when old-security
          (System/setSecurityManager old-security))))))

(defn start-script-server [pid-path port]
  (clojure.core.server/start-server {:port port
                                     :name "clojure-script-server"
                                     :accept 'clojure.core.server/io-prepl})

  (let [pid-file (str pid-path "/clojure-script-server-" port ".pid")]
    (spit pid-file (pid/current))
    (pid/delete-on-shutdown! pid-file))

  @(promise))

(def -main start-script-server)
