(ns story
  (:require
   ["react" :as React :refer [useState useReducer]]
   ["react-router-dom" :refer [Link useParams]]
   ["./styling" :as styling]
   ["./hn_api" :as hn])
  (:require-macros ["./macros.mjs" :refer [fx]]))

(defn Comments [props]
  (let [{:strs [comments ids indent]} (js->clj props)]
    #jsx [:div {:key indent :style (clj->js {:border "1px solid silver"})}
          (map
           (fn [id]
             (let [comment (get comments id)]
               #jsx [:div {:key id
                           :style (clj->js {:padding-top "10px"
                                            :margin-left
                                            (str (* 5 (or indent 1)) "px")})}
                     (cond
                       (:text comment) (:text comment)
                       (:deleted comment) "[deleted]"
                       :else "Loading...")
                     (when-let [kids (seq (:kids comment))]
                       #jsx [Comments {:comments comments
                                       :ids kids
                                       :indent (inc indent)}])]))
           ids)]))

(defn comments-reducer
  [state comment]
  (assoc state (:id comment) comment))

(defn add-comment
  [dispatch comment]
  (when (= "comment" (:type comment))
    (dispatch comment))
  (when-let [comment-ids (seq (:kids comment))]
    (doseq [comment-id comment-ids]
      ((hn/item-fetcher comment-id)
       (partial add-comment dispatch)))))

(defn Story []
  (let [{:strs [storyId]} (js->clj (useParams))
        [story set-story] (useState nil)
        [comments dispatch-comment] (useReducer comments-reducer {})]

    (fx ((hn/item-fetcher storyId) set-story))

    (fx (add-comment dispatch-comment story) story)

    #jsx [:div (:title story "Loading...")
          [:div [Comments {:comments comments
                           :ids (keys comments)}]]]))
