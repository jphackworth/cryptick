# cryptick

A Clojure library designed to retrieve ticker data from several popular crypto-currency exchanges:

- [btc-e](https://btc-e.com)
- [bter](https://bter.com)
- [okcoin](https://okcoin.com)
- [bitstamp](https://bitstamp.net)
- [havelock](https://www.havelockinvestments.com)

Cryptick is intended to retrieve ticker data, fast, from various exchanges. No authentication or API credentials are required.

http-kit is used for fast asynchronous requests. Standard (ticker ...) returns a promise. Use @(ticker ...) to block for the result.

When the response is received, additional processing of the JSON is performed. This will ensure that all numeric fields are
returned as numbers, rather than strings.

## Usage

### In Project

[cryptick "0.1.0"]

```clojure
(:require [cryptick.core :refer [ticker])
@(ticker :btce "btc_eur") ; (ticker exchange pair)
```

**Valid exchanges**:

- :btce
- :bter
- :okcoin
- :bitstamp
- :havelock

**Valid currency pairs**:

Please check with each site as to valid currency pairs. 

In the havelock's case, use the market symbol for the pair argument.

Since bitstamp only does btc_usd, the pair is hardcoded, but you still need to specify it.

### REPL example

```
user=> (use 'cryptick.core)
nil
user=> @(ticker :bitstamp "btc_usd")
{:high 803.0, :last 728.0, :timestamp 1391754578, :bid 728.76, :volume 29758.15669141, :low 711.0, :ask 735.72}
user=> @(ticker :bter "doge_btc")
{:last 1.6E-6, :vol_doge 1.73216891912E8, :vol_btc 288.07833036, :low 1.57E-6, :buy 1.59E-6, :sell 1.61E-6, :avg 1.66E-6, :high 1.72E-6}
user=> @(ticker :okcoin "ltc_cny")
{:buy 119.41, :high 127.47, :last 119.4, :low 118.11, :sell 119.43, :vol 2307633.95599992}
user=> @(ticker :havelock "AM1")
{:last 0.496, :units 19322, :1d {:min 0.49, :max 0.55, :vwap 0.50577136, :vol 160, :btc 80.92341737}, :7d {:min 0.49, :max 0.648, :vwap 0.54630067, :vol 571, :btc 311.93768258}, :30d {:min 0.34, :max 0.745, :vwap 0.53086017, :vol 4620, :btc 2452.57398638}} 
user=> (def x (ticker :bitstamp "btc_usd"))
#'user/x
user=> x
#<core$promise$reify__6310@2c7e895e: {:high 803.0, :last 699.0, :timestamp 1391756391, :bid 697.64, :volume 35995.68283103, :low 666.67, :ask 699.0}>
user=> @x
{:high 803.0, :last 699.0, :timestamp 1391756391, :bid 697.64, :volume 35995.68283103, :low 666.67, :ask 699.0}
user=> 
```

## License

Copyright © 2014 John P. Hackworth <jph@hackworth.be>

Distributed under the Mozilla Public License Version 2.0
