(ns country.list
  (:require [clojure.string :as st])
  (:import java.util.regex.Pattern
           java.net.URL
           java.nio.charset.Charset
           (java.io BufferedReader InputStreamReader)))

(defrecord country [short-name full-name country-code
                    capital citizenship adjective
                    currency currency-code subunit])

(defn as-utf8-stream [stream]
  (BufferedReader. (InputStreamReader. stream (Charset/forName "utf8"))))

(defn partition-rows [col]
  (butlast (rest (st/split col #"(</?TR.*?>)"))))

(defn note-regex [code]
  (Pattern/compile
   (str "\\((<SUP>)?<A HREF=\"#fn-" code "\".*?A>(</SUP>)?\\)")
     Pattern/CASE_INSENSITIVE))

(defn replace-notes [string]
  ((comp
    #(st/replace % (note-regex "5c") "FY")
    #(st/replace % (note-regex "tk2") "No capital")
    #(st/replace % (note-regex "tf2") "Saint-Pierre")
    #(st/replace % (note-regex "hk3") "Beijing")
    #(st/replace % (note-regex "il1") "Jerusalem"))
  string))

(defn replace-nbsp [string]
  (st/replace string "&nbsp;" " "))

(def tags-regex
  (Pattern/compile (st/join "|" ["<EM>"
                          "<SPAN CLASS=\"noItalic\".*SUP.*SPAN>"
                          "<SPAN.*?>"
                          "</SPAN>"
                          "</?+STRONG.*?>"
                          "\\(?<A.*?A>\\)?"
                          "<BR ?/?>"
                          "<WBR ?/?>"])))

(defn remove-tags [col]
  (map #(replace-nbsp
          (st/replace % tags-regex "")) col))

(defn partition-cols [col]
  (map
    #(remove empty?
             (map st/trim
                  (remove-tags
                    (st/split % #"(</?TD.*?>)")))) col))

(defn country-row? [word]
  (< -1 (.indexOf word "STRONG")))

(defn remove-currnecy-only-rows [countries]
  (remove #(= 3 (count %)) countries))

(defn map-countries [countries]
  (map #(apply ->country  %) countries))

(defn drop-wrong-records [countries]
  (let [f #(not= (count %) 9)
        wrong-rec (filter f countries)]
    (if (not (empty? wrong-rec))
      (do
        (println "WARN! Can't parse some countries and will skip them:")
        (println wrong-rec)
        (remove f countries))
      countries)))


(defn read-countries[address]
  (with-open [stream (as-utf8-stream (.openStream (URL. address)))]
      (apply str (line-seq stream))))

(defn process-countries [url]
  (map-countries
   (drop-wrong-records
    (remove-currnecy-only-rows
      (partition-cols
        (filter country-row?
                (partition-rows
                  (replace-notes
                    (read-countries url)))))))))

(defn parse-countries [lang-symbol]
  (let [lang (case lang-symbol :lt "lt" :en "en")]
    (process-countries
     (str "http://publications.europa.eu/code/" lang "/" lang "-5000500.htm"))))
