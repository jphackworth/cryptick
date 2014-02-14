; Copyright (C) 2014 John. P Hackworth <jph@hackworth.be>
;
; This Source Code Form is subject to the terms of the Mozilla Public
; License, v. 2.0. If a copy of the MPL was not distributed with this
; file, You can obtain one at http://mozilla.org/MPL/2.0/.

(ns cryptick.core
  (:require [org.httpkit.client :as http]
            [cheshire.core :refer :all]
            [clojure.string :refer [upper-case lower-case]]))

(def default-options
  (atom
   {:content-type "application/json"
    :user-agent "cryptick 0.1.2"
    :insecure? false
    :keepalive -1
    }))

(def feeds
  (atom
   {:btce
      {:url "https://btc-e.com/api/2"
       :method :get
       :pair-required? true
       :pair-example "btc_usd"
       :parse-for (fn [ticker pair]
                    (:ticker ticker))}

    :bter
      {:url "http://data.bter.com/api/1/ticker"
       :method :get
       :pair-required? true
       :pair-example "doge_btc"
       :parse-for (fn [ticker pair]
                    (parse-numbers
                     (dissoc ticker :result)))}

    :havelock
      {:url "https://www.havelockinvestments.com/r/tickerfull"
       :method :post
       :parse-for (fn [ticker pair]
                    (if (nil? pair)
                      (parse-numbers ticker)
                      (get (parse-numbers ticker)
                           (keyword (upper-case pair)))))}

    :bitstamp
      {:url "https://www.bitstamp.net/api/ticker/"
       :method :get
       :parse-for (fn [ticker pair]
                    (parse-numbers ticker))}

    :okcoin
      {:url "https://www.okcoin.com/api/ticker.do?symbol="
       :method :get
       :pair-required? true
       :pair-example "ltc_cny"
       :parse-for (fn [ticker pair]
                    (parse-numbers (:ticker ticker)))}

    :bitcoincharts-weighted-prices
      {:url "http://api.bitcoincharts.com/v1/weighted_prices.json"
       :method :get
       :limits "Max queries, once every 15 minutes"
       :parse-for (fn [ticker pair]
                    (if-not (nil? pair)
                      (->> pair
                           (upper-case)
                           (keyword)
                           [:timestamp]
                           (select-keys ticker)
                           (parse-numbers))
                      (parse-numbers ticker)))}

    :bitcoincharts-markets
      {:url "http://api.bitcoincharts.com/v1/markets.json"
       :method :get
       :limits "Max queries, once every 15 minutes"
       :parse-for (fn [ticker pair]
                    (if-not (nil? pair)
                      (->> ticker
                           (filter #(= pair (:symbol %)))
                           (first))
                      ticker))
                    }}))

(defn parse-numbers [m]
  (into {}
        (for [[k v] m]
          [k
           (cond (instance? java.util.Map v)
                   (parse-numbers v)

                 (or (nil? v)
                     (number? v))
                   v

                 (and (instance? java.lang.String v)
                      (->> v
                           (re-matches #"^(\d*\.?\d*)$")))
                   (Double/parseDouble v)

                 true
                   v)]))


(defn parse-for
  "Parses a ticker query to the argument exchange given a pair"

  [exchange ticker & [pair]]
  ((-> @feeds
       (get exchange)
       (get :parse-for))
   ticker pair))


(defn url-for [exchange & [pair]]
  (case exchange
    :btce (format "%s/%s/ticker" (:url (exchange @feeds)) (lower-case (name pair)))
    :bter (format "%s/%s" (:url (exchange @feeds)) pair)
    :okcoin (format "%s%s" (:url (exchange @feeds)) (lower-case pair))
    :bitstamp (:url (exchange @feeds))
    :havelock (:url (exchange @feeds))
    :bitcoincharts-weighted-prices (:url (exchange @feeds))
    :bitcoincharts-markets (:url (exchange @feeds))
    nil))

(defn http-options-for [exchange & [pair]]
  (case exchange
    :bitstamp (assoc @default-options :pair "btc_usd" :exchange exchange :method (:method (exchange @feeds)))
    :havelock (if (nil? pair)
                (assoc @default-options :pair pair :exchange exchange :method :get)
                (assoc @default-options :pair pair :exchange exchange :form-params {:symbol pair} :method :post))
    (assoc @default-options :pair pair :exchange exchange :method (:method (exchange @feeds)))))

(defn callback [{:keys [status headers body error opts] :as response}]
  (if error
    (throw (Exception. (format "Request failed, error: %s" error)))
    (case status
     200 (let [{:keys [pair exchange]} (:opts response)]
           (let [ticker (parse-string body true)]
             (if-not (nil? ticker) (parse-for exchange ticker pair))))
     (throw (Exception. (format "Request failed, response code: %s" status))))))

(defn ticker [exchange & [pair]]
  (if-not (contains? @feeds exchange)
    (throw (Exception. (format "Invalid exchange specified: %s" exchange)))
    (if (and (:pair-required? (exchange @feeds)) (nil? pair))
      (throw (Exception. (format "Currency pair must be specified for %s. Example: \"%s\"" exchange (:pair-example (exchange @feeds)))))
      (let [options (http-options-for exchange pair) url (url-for exchange pair)]
        (case (:method options)
          :get (http/get url options callback)
          :post (http/post url options callback)
          nil)))))
