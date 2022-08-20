(ns macros
  (:require
   ["react" :as React]))

(defmacro fx
  "Convenient wrapper arount react/useEffect"
  ([body]
   `(React/useEffect
     (fn []
       ~body
       js/undefined)
     #js []))
  ([body & watches]
   `(React/useEffect
     (fn []
       ~body
       js/undefined)
     #js [~@watches])))