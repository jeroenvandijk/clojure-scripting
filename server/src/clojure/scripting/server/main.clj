(ns clojure.scripting.server.main
  (:gen-class)
  (:require [clojure.scripting.server :as server]))

(defn -main [pid-path port]
  (server/start-script-server pid-path (Long/parseLong port)))
