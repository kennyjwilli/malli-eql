# malli-eql

A small, utility library unifying [Malli](https://github.com/metosin/malli) and [EQL](https://edn-query-language.org/).

## Dependency Information 

```clojure
dev.kwill/malli-eql {:mvn/version "0.1.3"}
```

## Usage

```clojure
(require '[kwill.malli-eql :as malli-eql])

(malli-eql/->eql
  [:map
   [:a int?]
   [:b [:map
        [:c int?]]]])
=> [:a {:b [:c]}]
```
