(ns tasks
  (:require
   [babashka.tasks :refer [shell]]
   [clojure.string :as str]))

(defn watch-cljs [{:keys []}]
  (let [watch (requiring-resolve 'pod.babashka.fswatcher/watch)]
    (watch "src"
           (fn [{:keys [type path]}]
             (when
              (and (#{:write :write|chmod} type)
                   (str/ends-with? path ".cljs"))
               (shell {:continue true} "bun run" "cherry" "compile" path)))
           {:recursive true})
    @(promise)))
