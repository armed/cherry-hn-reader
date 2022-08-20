(ns test)

(defn foo []
  (let [url "www.example.com"]
    #jsx [:a {:href url} "Go"]))