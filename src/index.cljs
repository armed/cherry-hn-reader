(ns index
  (:require
   ["sugar-date" :as Sugar]
   ["react" :as React]
   ["react-router-dom" :refer [BrowserRouter Route Routes]]
   ["react-dom/client" :refer [createRoot]]
   ["./stories" :refer [Stories]]
   ["./story" :refer [Story]]))

(Sugar/extend)

(defn App []
  #jsx [BrowserRouter
        [Routes
         [Route {:path "/" :element #jsx [Stories]}]
         [Route {:path "/:storyId" :element #jsx [Story]}]]])

(def container (createRoot (js/document.getElementById "app")))

(.render container (App))
