(ns hn-api)

(def base-url "https://hacker-news.firebaseio.com/v0")

(defn fetcher
  [uri]
  ^:async
  (fn [cbl]
    (let [resp (js/await (js/fetch (str base-url uri)))
          json (js/await (.json resp))
          data (js->clj json :keywordize-keys true)]
      (cbl data))))

(defn item-fetcher
  [item-id]
  (fetcher (str "/item/" item-id ".json")))
