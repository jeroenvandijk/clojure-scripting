(ns clojure.scripting.lib)

(defn add-dependencies
  "A helper function to lazily load dependencies using Pomegranate."
  [& args]
  (when-not (find-ns 'cemerick.pomegranate)
    (require '[cemerick.pomegranate]))
  (apply (resolve 'cemerick.pomegranate/add-dependencies)
    (concat args
      [:repositories (merge @(resolve 'cemerick.pomegranate.aether/maven-central) {"clojars" "https://clojars.org/repo"})])))