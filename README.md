# cryptick

![version](https://clojars.org/cryptick/latest-version.svg)

A Clojure library designed to retrieve ticker data from several popular crypto-currency exchanges:

- [btc-e](https://btc-e.com)
- [bter](https://bter.com)
- [okcoin](https://okcoin.com)
- [bitstamp](https://bitstamp.net)
- [havelock](https://www.havelockinvestments.com)
- [bitcoincharts](http://bitcoincharts.com/about/markets-api/)

No authentication or API credentials are required.

http-kit is used for asynchronous requests. Standard (ticker ...) returns a promise. Use @(ticker ...) to block for the result.

When the response is received, additional processing of the JSON is performed. This will ensure that all numeric fields are
returned as numbers, rather than strings.

## Usage

### Dependencies

Cryptick requires: 

```Clojure
[org.clojure/clojure "1.5.1"] 
[http-kit "2.1.16"] 
[cheshire "5.3.1"]
```

### In Namespace

```Clojure
(ns myapp.core 
  (:require [cryptick.core :refer [ticker]]))
```

## Ticker details

(cryptick.core/ticker) requires an exchange argument, and an optional pair argument. If you specify an invalid exchange, or 
do not include a pair when required, an exception will be thrown.

### Exchange Support

- **:btce** - Currency pair required. Examples include "btc_usd", "ltc_btc"
- **:bter** - Currency pair required. Examples include "btc_cny", "doge_btc"
- **:okcoin** - Currency pair required. Examples include "btc_cny", "ltc_btc"
- **:bitstamp** - No pair required. Returns market information for "btc_usd"
- **:havelock** - Pair (fund symbol) argument is optional. If no pair is specified, full ticker data for all Havelock funds is returned. Otherwise returned data is limited to specified fund. Examples include "am1", "hif", "peta"
- **:bitcoincharts-weighted-prices** - Pair (currency) argument optional. If no pair is specified, full weighted pricing data is returned. Otherwise returned data is limited to specific currency. Examples include "USD", "EUR", "JPY". Pricing is cached and requests should be limited to once per 15 minutes. 
- **:bitcoincharts-markets** - Pair (market+currency) argument optional. If no pair is specified, full markets data is returned. Otherwise returned data is limited to specified market+currency data. Examples include "localbtcUSD", "btceurEUR", "btcnCNY", "mtgoxCAD", "bitstampUSD"

Please check with each site's API as to all valid currency pairs. Examples have been provided above.

### REPL examples

```Clojure
user=> (use 'cryptick.core)
nil

user=> @(ticker :bitstamp)
{:high 803.0, :last 728.0, :timestamp 1391754578, :bid 728.76, :volume 29758.15669141, :low 711.0, :ask 735.72}

user=> @(ticker :bter "doge_btc")
{:last 1.6E-6, :vol_doge 1.73216891912E8, :vol_btc 288.07833036, :low 1.57E-6, :buy 1.59E-6, :sell 1.61E-6, :avg 1.66E-6, :high 1.72E-6}

user=> @(ticker :okcoin "ltc_cny")
{:buy 119.41, :high 127.47, :last 119.4, :low 118.11, :sell 119.43, :vol 2307633.95599992}

user=> @(ticker :havelock "AM1")
{:last 0.496, :units 19322, :1d {:min 0.49, :max 0.55, :vwap 0.50577136, :vol 160, :btc 80.92341737}, :7d {:min 0.49, :max 0.648, :vwap 0.54630067, :vol 571, :btc 311.93768258}, :30d {:min 0.34, :max 0.745, :vwap 0.53086017, :vol 4620, :btc 2452.57398638}}

user=> @(ticker :bitcoincharts-markets "btceurEUR")
{:symbol "btceurEUR", :latest_trade 1392185592, :currency "EUR", :ask 512.15, :close 501.42, :currency_volume 2042.903815074, :low 465.31, :avg 494.8713615859256, :high 529.76, :volume 4.12815122, :bid 490.01}

user=> @(ticker :bitcoincharts-weighted-prices "jpy")
{:JPY {:7d 69014.32, :30d 83873.80, :24h 58604.80}, :timestamp 1392186066}

user=> (def x (ticker :bitstamp))
#'user/x

user=> x
#<core$promise$reify__6310@2c7e895e: {:high 803.0, :last 699.0, :timestamp 1391756391, :bid 697.64, :volume 35995.68283103, :low 666.67, :ask 699.0}>

user=> @x
{:high 803.0, :last 699.0, :timestamp 1391756391, :bid 697.64, :volume 35995.68283103, :low 666.67, :ask 699.0}
```

## License

Copyright © 2014 John P. Hackworth <jph@hackworth.be>

Distributed under the Mozilla Public License Version 2.0
