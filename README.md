# cryptick

A Clojure library designed to retrieve ticker data from several popular crypto-currency exchanges:

- [btc-e](https://btc-e.com)
- [bter](https://bter.com)
- [okcoin](https://okcoin.com)
- [bitstamp](https://bitstamp.net)
- [havelock](https://www.havelockinvestments.com)

## Usage

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
user=> 
```

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
