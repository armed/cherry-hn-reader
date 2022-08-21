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

(defn App []
  (let [classes (styling/use-styles)]
    #jsx [:div {:className (:main classes)}
          [:div {:className "header"} "#Cherry_CLJS HN Reader [top 50 stories]"]
          [:div {:className "items"}
           [BrowserRouter
            [Routes
             [Route {:path "/" :element #jsx [Stories]}]
             [Route {:path "/:storyId" :element #jsx [Comments]}]]]]]))

(def container (createRoot (js/document.getElementById "app")))

(.render container #jsx [App])
