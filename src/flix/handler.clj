(ns flix.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [aleph.http :as http]
            [flix.imdb :as imdb]
            [schema.core :as s]))

(s/defschema Pizza
  {:name s/Str
   (s/optional-key :description) s/Str
   :size (s/enum :L :M :S)
   :origin {:country (s/enum :FI :PO)
            :city s/Str}})

(defn app
  [db]
  (api
    {:swagger
     {:ui "/"
      :spec "/swagger.json"
      :data {:info {:title "Flix"
                    :description "Compojure Api example"}
             :tags [{:name "api", :description "some apis"}]}}}

    (context "/api" []
      :tags ["api"]

      (GET "/plus" []
        :return {:result Long}
        :query-params [x :- Long, y :- Long]
        :summary "adds two numbers together"
        (ok {:result (+ x y)}))

      (POST "/edddho" []
        :return Pizza
        :body [pizza Pizza]
        :summary "echoes a Pizza"
        (ok pizza)))))


(comment

  (def db (imdb/get-imdb-db "resources/imdb.csv"))

  (def server (http/start-server (#'app db) {:port 8080}))

  (.close server)


  )
