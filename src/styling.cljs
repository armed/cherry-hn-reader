(ns styling
  (:require ["react-jss" :as jss]
            ["color" :as color]))

(defn create-styles
  [style-map]
  (let [use-styles (jss/createUseStyles (clj->js style-map))]
    (fn []
      (js->clj (use-styles) :keywordize-keys true))))

(def bg-color "rgb(246, 246, 239)")
(def dark-bg (-> bg-color color (.darken 0.1) .hex))
(def font-color "#222")
(def light-color (-> font-color color (.lighten 3) .hex))

(def css-underline
  {:cursor :pointer
   :text-decoration :none
   "&:hover" {:text-decoration :underline}})

(def css-a
  (merge {:white-space "nowrap"
          :overflow "hidden"
          :color font-color
          :text-overflow "ellipsis"}
         css-underline))

(def css-a-light
  (assoc css-a :color light-color))

(def use-styles
  (create-styles
   {"@global" {:body
               {:color font-color
                :background-color bg-color
                :font-size "10pt"
                :font-family "Verdana, Geneva, sand-serif"}}

    :title {:font-weight "600"
            :background-color "#ff6600"
            :padding "10px 5px"
            :margin-bottom "10px"}

    :underline css-underline

    :story-item
    {:padding "3px"
     :margin-bottom "5px"
     "& .story-row" {:display :flex
                     :align-items :baseline
                     :flex-direction :row
                     :flex-wrap :wrap
                     :align-content :center
                     :justify-content :flex-start
                     "& > a" {:margin-right "3px"}
                     "& > .title" css-a
                     "& > .info" (merge {:font-size "8pt"
                                         :color light-color}
                                        {"& a" css-a-light})
                     "& .date" {:color light-color}
                     "& .url"
                     (merge {:font-size "8pt"}
                            {"& a" css-a-light})}}}))
