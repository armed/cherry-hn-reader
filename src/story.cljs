(ns story
  (:require
   ["react" :as React :refer [useState useReducer]]
   ["react-router-dom" :refer [Link useParams]]
   ["./styling" :as styling]
   ["./hn_api" :as hn])
  (:require-macros ["./macros.mjs" :refer [fx]]))

(defn url-link
  [story]
  (when-let [url (some-> (:url story) (js/URL.))]
    #jsx [:span {:className "url"}
          "("
          [:a {:href (:url story)} (.-hostname url)]
          ")"]))

(defn time->date
  [item]
  (some-> (:time item) (* 1000) (js/Date.) (.relative)))

(defn render-story
  [{:keys [story-item color-grey underline]} [id story]]
  (let [title (:title story)
        cls (if title
              story-item
              (str story-item " " color-grey))
        story-link (str import.meta.env.BASE_URL id)]
    #jsx [:div {:key id
                :className cls}
          (if title
            #jsx [:div {:className "story-body"}
                  [:div {:className "item-row"}
                   [Link {:className "title" :to story-link} title]
                   (url-link story)]
                  [:div {:className "item-row"}
                   [:div {:className "info"}
                    (:score story)
                    " points" " | by "
                    (:by story) " | "
                    (time->date story) " | "
                    [Link {:className underline :to story-link}
                     (:descendants story) " comments"]]]]
            "Loading...")]))

(defn scroll-to
  [element]
  (.scrollIntoView element (clj->js {:behaviour "smooth"})))

(defn local-link
  [item-id label]
  (when-let [element (some-> (.getElementById js/document item-id))]
    #jsx [:span " | " [:a {:onClick #(scroll-to element)
                           :aria-hidden "true"} label]]))

(defn toggle-display
  [anchor id]
  (when-let [style (some-> id
                           (js/document.getElementById)
                           (.-style))]
    (let [subs-count (some-> (js/document.getElementById id)
                             (.querySelectorAll ".comment")
                             (.-length))
          elem-hidden? (= "none" (.-display style))]
      (set! (.-display style)
            (if elem-hidden? "block" "none"))
      (if elem-hidden?
        (set! (.-innerHTML anchor) "[-]")
        (set! (.-innerHTML anchor) (str "[" subs-count " more]"))))))

(defn elem-hidden?
  [id]
  (some-> id
          (js/document.getElementById)
          (.-style)
          (.-display)
          (= "none")))

(defn -render-comments
  [comments [prev id next]]
  (let [comment (get comments id)
        root? (not (get comments (:parent comment)))
        {:keys [text dead deleted id by parent]} comment]
    (when id
      (let [kids-id (str id "-kids")
            subs-count (some-> (js/document.getElementById id)
                               (.querySelectorAll ".comment")
                               (.-length))
            hidden? (elem-hidden? id)]
        #jsx [:div {:className "comments" :key id :id id}
              (when-not (or dead deleted)
                #jsx [:div {:className "comment"}
                      [:div {:className "item-row"}
                       [:div {:className "info"}
                        [:span by]
                        [:span (time->date comment)]
                        (local-link (and (not root?) parent) "parent")
                        (local-link prev "prev")
                        (local-link next "next")
                        [:span
                         " | "
                         [:a {:onClick #(toggle-display (-> % .-target) kids-id)}
                          (if hidden? (str "[" subs-count " more]") "[-]")]]]]
                      [:div {:id kids-id}
                       [:div {:dangerouslySetInnerHTML
                              (clj->js {:__html (or text "Loading...")})}]
                       (when-let [kids (seq (:kids comment))]
                         (render-comments comments kids))]])]))))

(defn render-comments
  [comments ids]
  #jsx [:div (map (partial -render-comments comments)
                  (->> (flatten [nil ids nil])
                       (partition 3 1)))])

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

(defn Comments []
  (let [classes (styling/use-styles)
        story-id (.-storyId (useParams))
        [story set-story] (useState nil)
        [comments dispatch-comment] (useReducer comments-reducer {})]

    (fx ((hn/item-fetcher story-id) set-story))

    (fx (add-comment dispatch-comment story) story)

    #jsx [:div {:className (:story-item classes)}
          (when story
            (render-story classes [(:id story) story]))
          (render-comments comments (:kids story))]))
