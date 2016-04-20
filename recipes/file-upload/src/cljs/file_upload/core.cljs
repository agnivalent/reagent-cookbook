(ns file-upload.core
    (:require [reagent.core :as r]
              [goog.events :as gev]
              [ajax.core :as a])
    (:import goog.net.IframeIo
             goog.net.EventType
             [goog.events EventType]))

(def files (r/atom []))

(defn delete-file! [name]
  (a/DELETE (str "/files/" name)
            {:handler #(swap! files (fn [files] (remove #{name} files)))}))

(defn upload-file! [upload-form-id status]
  (reset! status nil)
  (let [io (IframeIo.)]
    (gev/listen io goog.net.EventType.SUCCESS
                #(do
                   (swap! files conj (.getResponseText io))
                   (reset! status [:span "File uploaded successfully"])))
    (gev/listen io goog.net.EventType.ERROR
                #(reset! status [:span "Error uploading"]))
    (.setErrorChecker io #(= "error" (.getResponseText io)))
    (.sendFromForm io
                   (.getElementById js/document upload-form-id)
                   "/upload")))

(defn delete-component [name]
  (let [status (r/atom :button)]
    (fn []
      (condp = @status
        :button [:button {:on-click #(reset! status :confirm)} "Delete"]
        :confirm [:div "Confirm"
                  [:button {:on-click #(delete-file! name)} "Yes"]
                  [:button {:on-click #(reset! status :button)} "No"]]))))

(defn file-item [name]
  [:tr
   [:td [:a {:href (str "/files/" name)} name]]
   [:td [delete-component name]]])


(defn files-list []
  [:table
    (for [name @files] ^{:key name} [file-item name])])


(defn upload-page []
  (let [status (r/atom nil)
        form-id "upload-form"]
    (a/GET "/list-files" {:handler #(reset! files %)})
    (fn []
      [:div
       [:h2 "Available files"]
       [files-list]
       [:hr]
       [:form {:id form-id
               :enc-type "multipart/form-data"
               :method "POST"}
        [:label {:for "file"} "File to upload"]
        [:input {:id "file" :name "file" :type "file"}]]
       [:button {:on-click #(upload-file! form-id status)} "Upload"]
       (when @status @status)])))

(defn ^:export main []
  (r/render [upload-page]
            (.getElementById js/document "app")))

