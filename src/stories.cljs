(ns stories
  (:require
   ["react" :as React :refer [useState useReducer]]
   ["react-router-dom" :refer [Link]]
   ["./styling" :as styling]
   ["./hn_api" :as hn])
  (:require-macros ["./macros.mjs" :refer [fx]]))

(defn url-link
  [story]
  (let [url (some-> (:url story) (js/URL.))]
    (when url
      #jsx [:span {:className "url"}
            "("
            [:a {:href (:url story)} (.-hostname url)]
            ")"])))

(defn story-item
  [{:keys [story-item color-grey underline]} [key story]]
  (let [title (:title story)
        cls (if title
              story-item
              (str story-item " " color-grey))
        story-link (str "/" key)
        date (some-> (:time story) (* 1000) (js/Date.))]
    #jsx [:div {:key key
                :className cls}
          (if title
            #jsx [:div
                  [:div {:className "story-row"}
                   [Link {:className "title" :to story-link} title]
                   (url-link story)]
                  [:div {:className "story-row"}
                   [:div {:className "info"}
                    (:score story)
                    " points" " | by "
                    (:by story) " | "
                    (.relative date) " | "
                    [Link {:className underline :to story-link}
                     (:descendants story) " comments"]]]]
            "Loading...")]))

(defn stories-reducer
  [state [type data]]
  (case type
    :reset data
    :story (assoc state (:id data) data)
    {}))

(defn Stories []
  (let [[top-story-ids set-top-story-ids] (useState [])
        [stories dispatch] (useReducer stories-reducer {})
        classes (styling/use-styles)]

    (fx (let [fetcher (hn/fetcher "/topstories.json")]
          (fetcher set-top-story-ids)))

    (fx (let [first-10 (take 10 top-story-ids)]
          (dispatch [:reset (->> first-10
                                 (map (fn [id] {id nil}))
                                 (into {}))])
          (doseq [story-id first-10]
            (let [fetcher (hn/item-fetcher story-id)]
              (fetcher #(dispatch [:story %])))))
        top-story-ids)

    #jsx [:div
          [:div {:className (:title classes)} "#CherryJS HN Reader"]
          (map (partial story-item classes) stories)]))