(defproject cryptick "0.1.2"
  :description "Library for retrieving ticker data from crypto-currency-related exchanges"
  :author {:email "jph@hackworth.be" :web "https://hackworth.be"}
  :url "https://github.com/jphackworth/cryptick"
  :license {:name "Mozilla Public License Version 2.0"
            :url "https://www.mozilla.org/MPL/2.0/"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [http-kit "2.1.16"]
                 [cheshire "5.3.1"]])
