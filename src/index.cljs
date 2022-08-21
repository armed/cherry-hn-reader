(ns index
  (:require
   ["sugar-date" :as Sugar]
   ["react" :as React]
   ["./styling" :as styling]
   ["react-router-dom" :refer [BrowserRouter Route Routes]]
   ["react-dom/client" :refer [createRoot]]
   ["./stories" :refer [Stories]]
   ["./story" :refer [Comments]]))

(Sugar/extend)

(def ^:const BASE_URL import.meta.env.BASE_URL)

(defn App []
  (let [classes (styling/use-styles)]
    #jsx [:div {:className (:main classes)}
          [:div {:className "header"} "#Cherry_CLJS HN Reader [top 50 stories]"]
          [:div {:className "items"}
           [BrowserRouter
            [Routes
             [Route {:path BASE_URL :element #jsx [Stories]}]
             [Route {:path (str BASE_URL ":storyId") :element #jsx [Comments]}]]]]]))

(def container (createRoot (js/document.getElementById "app")))

(.render container #jsx [App])
