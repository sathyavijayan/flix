(ns flix.imdb
  (:require [clojure.string :as str]))



(def HEADERS
  [:rank
   :title
   :genre ;;vector
   :description
   :director
   :actors ;;vector
   :year
   :runtime
   :rating
   :votes
   :revenue
   :metascore])



(defn str->num
  [s]
  (if (seq s)
    (read-string s)
    nil))



(defn row->movie
  [row]
  (as->
    row $
    (str/split $ #",(?=([^\"]*\"[^\"]*\")*[^\"]*$)")
    (update $ 2 #(str/replace % #"\"" ""))
    (update $ 2 #(str/split % #","))
    (update $ 2 set)
    (update $ 5 #(str/replace % #"\"" ""))
    (update $ 5 #(str/split % #", "))
    (update $ 5 set)
    (update $ 10 str->num)
    (interleave HEADERS $)
    (apply hash-map $)))



(defn get-imdb-db
  [path]
  (->> (slurp path)
       str/split-lines
       rest
       (map row->movie)
       (map (juxt :title identity))
       (into {})))



(defn top-grossing
  [db year genre]
  (->> (filter #(and (get (:genre %) genre)
                   (:revenue %)
                   (= (:year %) year))
               (vals db))
       (sort-by #(:revenue %))
       reverse
       (map #(select-keys % [:title :revenue :year]))
       (take 10)))
