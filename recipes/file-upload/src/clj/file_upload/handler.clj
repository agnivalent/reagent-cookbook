(ns file-upload.handler
    (:require [compojure.core :refer [GET POST DELETE defroutes]]
              [compojure.route :refer [not-found]]
              [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
              [ring.middleware.json :refer [wrap-json-response]]
              [ring.util.response :refer [file-response content-type]]
              [clojure.java.io :as io]))

(def files (atom #{}))

(defn ok [body] (content-type 
                 {:status 200 :body body} 
                 "application/json"))

(defn upload [request]
  (let [file-params (get-in request [:multipart-params "file"])
        filename (:filename file-params)]
    (io/copy (:tempfile file-params) (io/file (str "/tmp/" filename)))
    (swap! files conj filename)
    (ok filename)))

(defn get-tempfile [filename] 
  {:body (io/file (str "/tmp/" filename))})

(defn delete-tempfile [filename] 
  (if-let [result (io/delete-file (str "/tmp/" filename))]
    (do (swap! files disj filename) 
        (ok (str result)))))

(defroutes app-routes
  (GET "/" (file-response "index.html" {:root public}))
  (GET "/list-files" [] (ok (vec @files)))
  (POST "/upload" [] upload)
  (GET "/files/:filename" [request filename] (get-tempfile filename))
  (DELETE "/files/:filename" [request filename] (delete-tempfile filename)))

(def app (wrap-json-response (wrap-defaults app-routes (assoc site-defaults :security false))))
