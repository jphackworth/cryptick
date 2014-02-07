; Copyright (C) 2014 John. P Hackworth <jph@hackworth.be>
;
; This Source Code Form is subject to the terms of the Mozilla Public
; License, v. 2.0. If a copy of the MPL was not distributed with this
; file, You can obtain one at http://mozilla.org/MPL/2.0/.

(ns cryptick.core
   (:require [org.httpkit.client :as http]
            [cheshire.core :refer :all]
            [clojure.string :refer [upper-case lower-case]]))

(def default-options (atom {:content-type "application/json"
                    :user-agent "cryptick 0.1.0"
                    :insecure? false
                    :keepalive -1
                    }))

(def feeds (atom {:btce {:url "https://btc-e.com/api/2" :method :get}
                  :bter {:url "http://data.bter.com/api/1/ticker" :method :get}
                  :havelock {:url "https://www.havelockinvestments.com/r/tickerfull" :method :post}
                  :bitstamp {:url "https://www.bitstamp.net/api/ticker/" :method :get}
                  :okcoin {:url "https://www.okcoin.com/api/ticker.do?symbol=" :method :get}
                  }))

(defn parse-numbers [m] 
  (into {} 
    (for [[k v] m] [k 
      (if-not (instance? java.util.Map v) 
        (if-not (number? v) (read-string v) v) 
        (parse-numbers v))]))) 
                                                    
(defn parse-for [exchange body]
  (case exchange 
    :btce (:ticker (parse-string body true))
    :bter (parse-numbers (dissoc (parse-string body true) :result))
    :bitstamp (parse-numbers (parse-string body true))
    :okcoin (parse-numbers (:ticker (parse-string body true)))
    :havelock (parse-numbers (dissoc (first (vals (parse-string body true))) :name :symbol))
    nil))

(defn url-for [exchange pair]
  (case exchange
    :btce (format "%s/%s/ticker" (:url (exchange @feeds)) (name pair))
    :bter (format "%s/%s" (:url (exchange @feeds)) pair)
    :bitstamp (:url (exchange @feeds))
    :okcoin (format "%s%s" (:url (exchange @feeds)) pair)
    :havelock (:url (exchange @feeds))
    nil
    ))

(defn http-options-for [exchange pair] 
  (case exchange 
    :btce (assoc @default-options :pair pair :exchange exchange)
    :bter (assoc @default-options :pair pair :exchange exchange)
    :bitstamp (assoc @default-options :pair "btc_usd" :exchange exchange)
    :okcoin (assoc @default-options :pair pair :exchange exchange)
    :havelock (assoc @default-options :pair pair :exchange exchange :form-params {:symbol pair})
    nil))
; {:keys [status headers body error opts]}
(defn callback [response]
   (case (:status response) 
     200 (let [{:keys [pair exchange]} (:opts response)]
           (parse-for exchange (:body response)))
     (throw (Exception. response))))

(defn ticker [exchange pair]
  (let [options (http-options-for exchange pair) url (url-for exchange pair)]
    (case (:method (exchange @feeds))
      :get (http/get url options callback)
      :post (http/post url options callback)
      nil)))
