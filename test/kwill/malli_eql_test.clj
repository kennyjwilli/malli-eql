(ns kwill.malli-eql-test
  (:require
    [clojure.test :refer :all]
    [kwill.malli-eql :as malli-eql]
    [malli.core :as m]
    [malli.util :as mu]))

(def registry (merge (m/default-schemas) (mu/schemas)))

(deftest ->eql-test
  (is (= [] (malli-eql/->eql [:map]))
    "empty map is empty eql")
  (is (= [:a] (malli-eql/->eql [:map [:a int?]]))
    "simple map")
  (is (= '[(:a {:foo "bar"})]
        (malli-eql/->eql [:map [:a {:foo "bar"} int?]]))
    "simple map with options")
  (is (= [:a]
        (malli-eql/->eql [:map [:a {:foo "bar"} int?]]
          {::malli-eql/get-parameters (constantly nil)}))
    "simple map, ignoring parameters")
  (is (= [:aa
          :a
          {:b [:c]}
          {:seq [:c]}
          :d]
        (malli-eql/->eql
          [:or
           [:map [:aa int?]]
           [:merge
            [:map
             [:a int?]
             [:b [:map
                  [:c int?]]]
             [:seq [:map
                    [:c int?]]]]
            [:map
             [:d int?]]]]
          {:registry registry}))
    "simple map"))
