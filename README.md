## Signals!

[![Build Status](https://github.com/iGEL/signals/actions/workflows/build-deploy.yml/badge.svg?branch=main)](https://github.com/iGEL/signals/actions/workflows/build-deploy.yml)
[![Demo: Try yourself!](https://img.shields.io/badge/Demo:-Try%20yourself!-blue.svg)](https://igel.github.io/signals/)

This is a toy project written in [ClojureScript](https://clojurescript.org/).
It renders some German railway signals as SVG. You can [try it yourself](https://igel.github.io/signals/)!

![](https://media1.giphy.com/media/l1KVb2dUcmuGG4tby/giphy.gif)

[![](/example.webp)](https://igel.github.io/signals/)

### Setup

To be written :scream:

### Usage in your own project

The project is still being developed, so it can't yet be used from JavaScript
directly. But you can use the signals from cljs.

```cljs
(def !distant (atom {:type :distant
                     :system :ks
                     :distant {:aspect :stop}}))
(def !combination (atom {:type :combination
                         :system :hv-light
                         :distant {:aspect :proceed}
                         :main {:aspect :stop}}))
(def !main (atom {:type :main
                  :system :hv-semaphore
                  :main {:aspect :proceed}}))
```

To display the signals, you need to include the
[signals.css](https://igel.github.io/signals/signals.css) (or
[signals.styl](src/signals/signals.styl) if you use
[Stylus](https://stylus-lang.com/)) and create a React component returning a
`svg` element. Inside, you have to once include `signals.signal/defs`, then you
can call `signals.signal/signal` passing the signal you want with the `:signal`
key.

Here an example using [UIx](https://github.com/pitch-io/uix):

```cljs
(require '[signals.signal :as signal])

($ :svg {:version "1.1"
         :viewBox "0 0 140 600"
         :width "200"
         :height "600"}
   ($ signal/defs)
   ($ signal/signal {:signal @!main}))
```

You can configure signals by adding features to the top level, to the
`:distant` or `:main` part of the signal.

| Attribute            | where              | Description                                                                      |
| -------------------- | ------------------ | ------------------------------------------------------------------------------- |
| `:system`            | top level          | Type of the signal system, can be `:ks`, `:hv-light`, `:hv-semaphore`, or `:hl` |
| `:type`              | top level          | Type of the signal, can be `:distant`, `:main`, or `:combination` |
| `:aspect`            | `:distant`/`:main` | The aspect of the signal, valid values are `:proceed`, `:stop`, `:stop+sh1`, `:stop+zs1`, or `:stop+zs7` |
| `:speed-limit`       | `:distant`/`:main` | The current speed limit. Only displayed with a `:zs3` or `:slow-speed-lights` and `:proceed` aspect |
| `:slow-speed-lights` | `:distant`/`:main` | A vector of possible speeds. For `:hv-light` & `:hv-semaphore`, only `40` is supported, for `:hl`, it can be `40`, `60` and `100`. `:ks` ignores this |
| `:zs3`               | `:distant`/`:main` | Does the signal have a zs3? Possible values are `nil` (no zs3), `:display` or `:sign`. Ignored for `:hl` |
| `:sh1?`              | `:main`            | Boolean, set to `true` when the signal when it's capable to display a `Sh1`/`Ra12` aspect |
| `:zs1?`              | `:main`            | Boolean, set to `true` if the signal has a Zs1 |
| `:zs7?`              | `:main`            | Boolean, set to `true` when the signal has a Zs7 |
| `:distant-addition`  | `:distant`         | Can be `nil` (nothing), `:repeater` (if this is a repeater), or `:shortened-break-path` when the break path is shortened |

Example:
```cljs
(swap! !combination #(-> %1
                         (assoc :system :hv-semaphore)
                         (assoc-in [:main :aspect] :stop+sh1)
                         (assoc-in [:main :sh1?] true)
                         (assoc-in [:main :zs1?] true)
                         (assoc-in [:main :slow-speed-lights] [40])
                         (assoc-in [:main :speed-limit] 40)
                         (assoc-in [:distant :aspect] :proceed)
                         (assoc-in [:distant :distant-addition] :shortened-break-path)))
```
