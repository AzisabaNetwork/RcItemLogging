# RcItemLogging

## 概要
rcpve向けのログプラグインです。

## 機能
- アイテムの流れを記録するためのAPI

## リリース方法
Actions -> Releaseの`Run workflow`を実行して、リリースを行うようにしてください。\
※ 自動的に[Azisaba Maven](https://repo.azisaba.net/)にアップロードされますので、バージョンを間違えないように気をつけてください。

## ログフォーマット

`YYYY/MM/DD-hh:mm:ss, %event_type%, %event_from% to %event_to%, %message%`

例: `2025/02/09-12:23:34, shopkeeper_trade, shopkeeper:testshop to testuser, OAK_LOG -16 -> OAK_PLANKS +64`