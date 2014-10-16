(ns rethinkdb.net
  (:require [clojure.data.json :as json]
            [rethinkdb.query-builder :refer [query->json]]
            [rethinkdb.utils :refer [str->bytes int->bytes bytes->int pp-bytes]]))

(defn send-int [out i n]
  (.write out (int->bytes i n) 0 n))

(defn send-str [out s]
  (let [n (count s)]
    (.write out (str->bytes s) 0 n)))

(defn read-str [in n]
  (let [resp (byte-array n)]
    (.readFully in resp 0 n)
    (String. resp)))

(defn read-init-response [in]
  (let [resp (byte-array 4096)]
    (.read in resp 0 4096)
    (clojure.string/trim (String. resp))))

(defn read-response [in]
  (let [token (byte-array 8)
        length (byte-array 4)]
    (.read in token 0 8)
    (.read in length 0 4)
    (let [length (bytes->int length 4)
          json (read-str in length)]
      (json/read-str json))))

(defn send-query [{:keys [out in token] :as conn} query]
  (let [n (count query)]
    (send-int out token 8)
    (send-int out n 4)
    (send-str out query)
    (let [{type "t" resp "r"} (read-response in)]
      (condp = type
        1 (first resp)
        2 resp
        3 (lazy-cat resp (send-query conn (query->json :CONTINUE)))
        (throw (Exception. (first resp)))))))

(defn send-start-query [conn args]
  (send-query conn (query->json :START args)))