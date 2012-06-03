(ns world.country.list
  (:require [clojure.contrib.string :as st])
  (:require [clojure.string :as stt]))

(defn partition-rows [col]
  (butlast (rest (stt/split col #"(</?TR.*?>)"))))

(defn handle-macedonia [string]
  (stt/replace string #"\(<A HREF=\"#fn-5c\".*?A>\)" "FY"))

(defn handle-tokelau [string]
  (stt/replace string #"\((<SUP>)?<A HREF=\"#fn-tk2\".*?A>(</SUP>)?\)" "No capital"))

(defn replace-nbsp [string]
  (stt/replace string "&nbsp;" " "))

(defn remove-trash [col]
  (map #(replace-nbsp 
          (stt/replace % #"<EM>|<SPAN CLASS=\"noItalic\".*SUP.*SPAN>|<SPAN.*?>|</SPAN>|</?+STRONG>|\(?<A.*?A>\)?|<BR ?/?>" "")) col))

(defn partition-cols [col]
  (map 
    #(remove empty? 
             (map stt/trim 
                  (remove-trash 
                    (stt/split % #"(</?TD.*?>)")))) col))

(defn country-row? [word]
  (< -1 (.indexOf word "STRONG")))

(defn remove-currency-cols [col]
  (remove #(= 3 (count %)) col))

(defn map-keys [col]
  (map  #(zipmap `(:short-name :full-name :country-code :capital :citizenship :adjective :currency :currency-code :subunit) %) col))

(defn fetch-url[address]
  (with-open [stream (.openStream (java.net.URL. address))]
    (let  [buf (java.io.BufferedReader. 
                 (java.io.InputStreamReader. stream (java.nio.charset.Charset/forName "utf8")))]
      (apply str (line-seq buf)))))

(defn parse-iso [url]
  (map-keys 
    (remove-currency-cols
      (partition-cols 
        (filter country-row? 
                (partition-rows
                  (handle-tokelau
                    (handle-macedonia 
                    (fetch-url url)))))))))

(defn parse-countries [lang-symbol]
  (let [lang (case lang-symbol :lt "lt" :en "en")] 
    (parse-iso (str "http://publications.europa.eu/code/" lang "/" lang "-5000500.htm"))))
