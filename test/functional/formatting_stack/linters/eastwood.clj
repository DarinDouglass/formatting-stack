(ns functional.formatting-stack.linters.eastwood
  (:require
   [clojure.test :refer [are deftest]]
   [formatting-stack.linters.eastwood :as sut]
   [formatting-stack.protocols.linter :as linter]
   [matcher-combinators.matchers :as matchers]
   [matcher-combinators.test :refer [match?]]))

(deftest lint!
  (let [linter (sut/new {})]
    (are [filename expected] (match? expected
                                     (linter/lint! linter [filename]))
      "test-resources/invalid_syntax.clj"
      []

      "test-resources/eastwood_warning.clj"
      (matchers/in-any-order
       [{:source :eastwood/warn-on-reflection
         :msg "reference to field getPath can't be resolved"
         :line 6
         :column 25
         :filename "test-resources/eastwood_warning.clj"}
        {:source   :eastwood/def-in-def
         :line     3
         :column   13
         :filename "test-resources/eastwood_warning.clj"}]))))
