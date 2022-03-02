(ns kwill.malli-eql
  (:require
    [malli.core :as m]))

(def default-hierarchy
  (-> (make-hierarchy)
    (derive :merge :map)
    (derive :map-of :map)
    (derive :vector :sequential)
    (derive :catn :sequential)))

(defn default-*-like?
  [?schema opts schema-type]
  (isa? default-hierarchy (m/type ?schema opts) schema-type))

(defn default-map-like?
  [?schema opts]
  (default-*-like? ?schema opts :map))

(defn default-sequential-like?
  [?schema opts]
  (default-*-like? ?schema opts :sequential))

(defn ->eql
  "Converts a schema to EQL. Optional takes an options map that gets passed to
  all Malli API calls. Additionally, you can pass some parameters unique to this
  library.
    ::map-like? - Returns true if a schema can be treated like a :map type schema.
    ::sequential-like? - Returns true if a schema can be treated like a
      :sequential type schema.
    ::get-parameters - Function passed a map schema's properties are returns the
      EQL parameters to include. By default, uses the Malli properties map, if
      available.
    ::defaultf - Function called with the schema and options map when no builtin
      handler can process the schema type. Returns EQL for the schema."
  ([?schema] (->eql ?schema nil))
  ([?schema {::keys [map-like? sequential-like? get-parameters defaultf]
             :or    {map-like?        default-map-like?
                     sequential-like? default-sequential-like?
                     get-parameters   :properties
                     defaultf         (constantly nil)}
             :as    options}]
   (let [f (fn convert [?schema]
             (let [s (m/deref (m/schema ?schema options))
                   schema-type (m/type s options)]
               (cond
                 (map-like? s options)
                 (mapv (fn [[k properties v]]
                         (let [parameters (get-parameters {:properties properties})
                               k-eql (if parameters
                                       (list k parameters)
                                       k)]
                           (if-let [sub (convert v)]
                             {k-eql sub}
                             k-eql))) (m/children s options))
                 (sequential-like? s options)
                 (convert (first (m/children s options)))
                 (contains? #{:or} schema-type)
                 (into [] (mapcat (fn [child-schema] (convert child-schema))) (m/children s options))
                 :else (defaultf ?schema options))))]
     (f ?schema))))

;; README Examples
(comment
  (->eql
    [:map
     [:a int?]
     [:b [:map
          [:c int?]]]])
  )

(comment
  (def registry (merge (m/default-schemas) (malli.util/schemas)))
  (m/children [:or [:sequential [:map [:a int?]]] [:sequential [:map [:a int?]]]])
  (m/deref [:sequential [:map [:a int?]]])
  (m/type [:or int?])
  (->eql
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
    {:registry registry})
  )
