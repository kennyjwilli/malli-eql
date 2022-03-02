# malli-eql

A small, utility library unifying [Malli](https://github.com/metosin/malli) and [EQL](https://edn-query-language.org/).

## Dependency Information 

```clojure
  io.github.kennyjwilli/malli-eql {:git/url "https://github.com/kennyjwilli/malli-eql.git" 
                                   :git/sha "438663e85da97fe124fea86b629aeecef1b4abc2"}
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
