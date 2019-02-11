(ns formatting-stack.strategies
  (:require
   [clojure.string :as str]
   [formatting-stack.strategies.impl :as impl]))

(defn all-files
  "This strategy unconditionally processes all files. Requires the `tree` binary to be installed."
  []
  (->> (impl/file-entries "tree" "-fi")
       impl/extract-clj-files))

(defn git-completely-staged
  "This strategy processes the new or modified files that are _completely_ staged with git."
  [& {:keys [files]
      :or {files (impl/file-entries "git" "status" "--porcelain")}}]
  (->> files
       (filter #(re-find impl/git-completely-staged-regex %))
       (map #(str/replace-first % impl/git-completely-staged-regex ""))
       impl/extract-clj-files))

(defn git-not-completely-staged
  "This strategy processes all files that are not _completely_ staged with git. Untracked files are also included."
  [& {:keys [files]
      :or {files (impl/file-entries "git" "status" "--porcelain")}}]
  (->> files
       (filter #(re-find impl/git-not-completely-staged-regex %))
       (map #(str/replace-first % impl/git-not-completely-staged-regex ""))
       impl/extract-clj-files))

(defn git-diff-against-default-branch
  "This strategy processes all files that this branch has modified.
  The diff is compared against the `:target-branch` option."
  [& {:keys [target-branch files blacklist]
      :or {target-branch "master"
           files (impl/file-entries "git" "diff" "--name-only" target-branch)
           blacklist (git-not-completely-staged)}}]
  (->> files
       (remove (set blacklist))
       impl/extract-clj-files))
