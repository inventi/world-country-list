(ns world.country.test.list
  (:use [world.country.list])
  (:use [clojure.test])
  (:require [clojure.contrib.string :as st])
  (:require [clojure.string :as string]))

(def lt (parse-countries :lt))
(def en (parse-countries :en))

(deftest test-same-size
         (testing "EN and LT country lists are the same size"
                  (= (count lt) (count en))))

(defn not-empty-info? [country-info]
  (complement (every? #(empty? (string/trim %)) country-info)))

(defn no-tags? [country-info]
  (empty? (re-matches #".*?(<|>)+.*?" (str country-info))))

(defn country-code-2? [code]
  (= 2 (count (:country-code code))))

(defn check-map-values [countries]
         (doseq [country-map countries]
           (is (not-empty-info? (vals country-map)))
           (is (no-tags? (vals country-map)))
           (is (country-code-2? country-map))))

(deftest test-map-values
         (testing "LT should have correct map values"
                  (check-map-values lt))
         (testing "EN should have correct map values"
                  (check-map-values en)))


