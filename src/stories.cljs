(ns stories
  (:require
   ["react" :as React :refer [useState useReducer]]
   ["./styling" :as styling]
   ["./hn_api" :as hn]
   ["./story" :as Story])
  (:require-macros ["./macros.mjs" :refer [fx]]))

(defn stories-reducer
  [state [type data]]
  (case type
    :reset data
    :story (assoc state (:id data) data)
    {}))

(defn Stories []
  (let [classes (styling/use-styles)
        [top-50 set-top-50] (useState [])
        [stories dispatch] (useReducer stories-reducer (sorted-map))]

    (fx (let [fetcher (hn/fetcher "/topstories.json")]
          (fetcher (fn [xs] (set-top-50 (take 50 xs))))))

    (fx (do (dispatch [:reset (->> top-50
                                   (map (fn [id] {id nil}))
                                   (into (sorted-map)))])
            (doseq [story-id top-50]
              (let [fetcher (hn/item-fetcher story-id)]
                (fetcher #(dispatch [:story %])))))
        top-50)

    (->> top-50
         (map (fn [id] [id (get stories id)]))
         (map (partial Story/render-story classes)))))