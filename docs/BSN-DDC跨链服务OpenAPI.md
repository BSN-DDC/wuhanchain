### BSN-DDC基础网络跨链服务OpenAPI

> 跨链服务特指 BSN 开放联盟链的官方 DDC 跨向以太坊主网的中心化服务，如果您没有此需求可忽略此文。 

BSN-DDC 基础网络体系内通过 Poly 中继链实现起始链和目标链之间的数据互通。我们为了简化您的对接过程，特此对中继链上的跨链数据进行了整合封装，以 OpenAPI 的方式进行开放。

*  **通讯协议：** `HTTP`
* **通讯报文数据格式：**`application/json`
* **服务通讯地址：**`https://hk.bsngate.com/api/8ef04e41fecd6567ba9fca3fa060ff0c/bsncross/rest/`

--

#### 1. 获取 Token
首次接入跨链服务，需要调用本接口获取 Token，调用其它接口都必须填送此 Token。您需要关注 Token 的过期时间，失效后需再次获取新的 Token。

* **接口名称:** `api/v1/getToken`

* **请求方式:** `POST`

* **请求参数:**

| 序号 | 业务参数名称 | 参数说明 | 是否必须 | 参数类型 |
| -------- | -------- | ----- | -------- | -------- |
| 1 | tx_signer |对跨链数据进行签名的账户地址 | Yes | string |

* **响应参数:**

| 序号 | Header 参数名称 | 参数说明 | 是否必须 | 参数类型 |
| -------- | -------- | ----- | ----- | ----- |
| 1 | session_id | Token 值 | Yes | string |
| 2 | tx_signer | 对跨链数据进行签名的账户地址 | Yes | string |

| 序号 | 业务参数名称 | 参数说明 | 是否必须 | 参数类型 |
| -------- | -------- | ----- |----- | ----- |
| 1 | code | 返回码 | Yes | string ||
| 2 | data | 业务参数 | Yes | object ||
| &emsp;&emsp; 2.1| &emsp;&emsp;&emsp;&emsp;expire_time | 过期时间 | Yes | string ||
| 3 | msg | 说明 | Yes | string ||


* **请求示例:**

```shell
curl --location --request POST 'https://hk.bsngate.com/api/8ef04e41fecd6567ba9fca3fa060ff0c/bsncross/rest/api/v1/getToken' \
--header 'Content-Type: application/json' \
--data '{
  "tx_signer":"0x9bde88224e7cf3ada6045fc0236d10b8cd5a94da"
}'
```

* **响应示例:**

```curl
session_id=MTY2MTg0MjkyMnxEdi1CQkFFQ180SUFBUkFCRUFBQV85WF9nZ0FCQm5OMGNtbHVad3dIQUFWMGIydGxiZ1p6ZEhKcGJtY01fN2NBXzdSbGVVcG9Za2RqYVU5cFNrbFZla2t4VG1sSmMwbHVValZqUTBrMlNXdHdXRlpEU2prdVpYbEtWV1ZHVG5CYU1qVnNZMmxKTmtscVFqUlBWMHByV2xSbk5FMXFTVEJhVkdScVdtcE9hRnBIUlRKTlJGRXhXbTFOZDAxcVRUSmFSRVYzV1dwb2FscEVWbWhQVkZKcldWTkpjMGx0VmpSalEwazJUVlJaTWsxVVp6Qk9SR041VFc0d0xsOHdabFo1TmxwRWR6ZzJVV0k1V1RkUUxWOUNjbk5LVUhGbGEwUmhaSGsyWTA1MmFURlNOMWR5YkdNPXyuTvyXsoIlX5XpQiLCqP5E3MPXSDLcWLbQx9FRdiSQ5w==; Path=/; Expires=Thu, 29 Sep 2022 07:02:02 GMT; Max-Age=2592000
tx_signer=MTY2MTg0MjkyMnxEdi1CQkFFQ180SUFBUkFCRUFBQVRmLUNBQUVHYzNSeWFXNW5EQXNBQ1hSNFgzTnBaMjVsY2daemRISnBibWNNTEFBcU1IZzVZbVJsT0RneU1qUmxOMk5tTTJGa1lUWXdORFZtWXpBeU16WmtNVEJpT0dOa05XRTVOR1JofG3fmqeSDw8S7-RUyxO9IrDUY8XJd1RE8Sh_tRRf540l; Path=/; Expires=Thu, 29 Sep 2022 07:02:02 GMT; Max-Age=2592000

```


```json
{
    "code": "0000",
    "data": {
        "expire_time": "2022-08-30 15:20:40"
    },
    "msg": "success"
}
```

#### 2. 查询跨链状态

获取 Token 后，通过起始链侧链 ID 和签名账户地址查询跨链数据的跨链状态。

**接口名称:**`api/v1/queryStatus`

**请求方式:**`GET`

**请求参数:**

| 序号 | Header 参数名称 | 参数说明 | 是否必须 | 参数类型 |
| -------- | -------- | ----- | -------- | -------- |
| 1 | session_id | 获取 Token 接口返回的 session_id | Yes | string |
| 2 | tx_signer | 获取 Token 接口请求参数内的 tx_signer | Yes | string |
| 3 | from_chainid | 武汉链的侧链ID：1003650780676003 | Yes | string |
| 4 | cross_chain_id | 跨链交易ID | No | string |
| 5 | page_size | 单页交易笔数 | No | string |
| 6 | page_num | 页数 | No | string |
| 7 | default_pagination | 不分页：false；分页：true | No | string |


**响应参数:**


| 序号 | 业务参数名称 | 参数说明 | 是否必须 | 类型 |
| -------- | -------- | ----- | ----- | ----- |
|1|code|响应码|Yes|string|
|2|data||Yes|object|
|2.1|&emsp;&emsp;list|| No |array|
|2.1.1|&emsp;&emsp;&emsp;&emsp;cross_chain_id|跨链交易ID| No |integer(int32)|
|2.1.2|&emsp;&emsp;&emsp;&emsp;tx_status|跨链状态-0:未知；1:成功；2:失败；3:待签名；4:已签名| No |integer(int32)|
|2.2|&emsp;&emsp;total|跨链数据条数| No    |integer(int32)|
|3| msg | 说明 | Yes | string |

**请求示例:**


```shell
curl --location --request GET 'https://hk.bsngate.com/api/8ef04e41fecd6567ba9fca3fa060ff0c/bsncross/rest/api/v1/queryStatus?from_chainid=1003650780676003&default_pagination=false' \
--header 'Cookie: session_id=MTY2MTg0MjkyMnxEdi1CQkFFQ180SUFBUkFCRUFBQV85WF9nZ0FCQm5OMGNtbHVad3dIQUFWMGIydGxiZ1p6ZEhKcGJtY01fN2NBXzdSbGVVcG9Za2RqYVU5cFNrbFZla2t4VG1sSmMwbHVValZqUTBrMlNXdHdXRlpEU2prdVpYbEtWV1ZHVG5CYU1qVnNZMmxKTmtscVFqUlBWMHByV2xSbk5FMXFTVEJhVkdScVdtcE9hRnBIUlRKTlJGRXhXbTFOZDAxcVRUSmFSRVYzV1dwb2FscEVWbWhQVkZKcldWTkpjMGx0VmpSalEwazJUVlJaTWsxVVp6Qk9SR041VFc0d0xsOHdabFo1TmxwRWR6ZzJVV0k1V1RkUUxWOUNjbk5LVUhGbGEwUmhaSGsyWTA1MmFURlNOMWR5YkdNPXyuTvyXsoIlX5XpQiLCqP5E3MPXSDLcWLbQx9FRdiSQ5w==; tx_signer=MTY2MTg0MjkyMnxEdi1CQkFFQ180SUFBUkFCRUFBQVRmLUNBQUVHYzNSeWFXNW5EQXNBQ1hSNFgzTnBaMjVsY2daemRISnBibWNNTEFBcU1IZzVZbVJsT0RneU1qUmxOMk5tTTJGa1lUWXdORFZtWXpBeU16WmtNVEJpT0dOa05XRTVOR1JofG3fmqeSDw8S7-RUyxO9IrDUY8XJd1RE8Sh_tRRf540l'
```

**响应示例:**

```json
{
    "code": "0000",
    "data": {
        "list": [
            {
                "cross_chain_id": 8,
                "tx_status": 2
            },
            {
                "cross_chain_id": 9,
                "tx_status": 2
            }
        ],
        "total": 2
    },
    "msg": "success"
}
```
## 3-查询跨链待签名数据

获取 Token 后，通过起始链侧链 ID 和签名账户地址查询待签名的跨链数据。

**接口名称:**`api/v1/queryTx`

**请求方式:**`GET`

**请求参数:**


| 序号 | Header 参数名称 | 参数说明 | 是否必须 | 参数类型 |
| -------- | -------- | ----- | -------- | -------- |
| 1 | session_id |获取 Token 接口返回的 session_id | Yes | string |
| 2 | tx_signer |获取 Token 接口请求参数内的 tx_signer | Yes | string |
| 3 | from_chainid |武汉链的侧链ID：1003650780676003 | Yes | string |
| 4 | cross_chain_id |跨链交易ID | No | string |
| 5 | page_size |单页交易笔数 | No | string |
| 6 | page_num |页数 | No | string |
| 7 | default_pagination |不分页：false；分页：true | No | string |

**响应参数:**

| 序号 | 业务参数名称 | 参数说明 | 是否必须 | 类型 |
| -------- | -------- | ----- |----- |----- |
|1|code|成功：0000，签名者名下没有待签名数据：9003|Yes|string|
|2|data||Yes|object|
|2.1|&emsp;&emsp;list||No|array|
|2.1.1|&emsp;&emsp;&emsp;&emsp;cross_chain_id|跨链交易ID|No|integer(int32)|
|2.1.2|&emsp;&emsp;&emsp;&emsp;dynamic_fee_tx|待签名数据|No|string|
|2.2|&emsp;&emsp;total|跨链数据条数|No|integer(int32)|
|3|msg| 说明 |Yes|string|




**请求示例:**


```shell
curl --location --request GET 'https://hk.bsngate.com/api/8ef04e41fecd6567ba9fca3fa060ff0c/bsncross/rest/api/v1/queryTx?from_chainid=1003650780676003&default_pagination=false' \
--header 'Cookie: session_id=MTY2MTg0MjkyMnxEdi1CQkFFQ180SUFBUkFCRUFBQV85WF9nZ0FCQm5OMGNtbHVad3dIQUFWMGIydGxiZ1p6ZEhKcGJtY01fN2NBXzdSbGVVcG9Za2RqYVU5cFNrbFZla2t4VG1sSmMwbHVValZqUTBrMlNXdHdXRlpEU2prdVpYbEtWV1ZHVG5CYU1qVnNZMmxKTmtscVFqUlBWMHByV2xSbk5FMXFTVEJhVkdScVdtcE9hRnBIUlRKTlJGRXhXbTFOZDAxcVRUSmFSRVYzV1dwb2FscEVWbWhQVkZKcldWTkpjMGx0VmpSalEwazJUVlJaTWsxVVp6Qk9SR041VFc0d0xsOHdabFo1TmxwRWR6ZzJVV0k1V1RkUUxWOUNjbk5LVUhGbGEwUmhaSGsyWTA1MmFURlNOMWR5YkdNPXyuTvyXsoIlX5XpQiLCqP5E3MPXSDLcWLbQx9FRdiSQ5w==; tx_signer=MTY2MTg0MjkyMnxEdi1CQkFFQ180SUFBUkFCRUFBQVRmLUNBQUVHYzNSeWFXNW5EQXNBQ1hSNFgzTnBaMjVsY2daemRISnBibWNNTEFBcU1IZzVZbVJsT0RneU1qUmxOMk5tTTJGa1lUWXdORFZtWXpBeU16WmtNVEJpT0dOa05XRTVOR1JofG3fmqeSDw8S7-RUyxO9IrDUY8XJd1RE8Sh_tRRf540l'
```


**响应示例:**

```json
{
    "code": "0000",
    "data": {
        "list": [
            {
                "cross_chain_id": 26,
                "dynamic_fee_tx": "{\"ChainID\":3,\"Nonce\":0,\"GasTipCap\":1500000000,\"GasFeeCap\":1500000014,\"Gas\":0,\"To\":\"0x5f5d94ea7d4eb5d20621344a1c0e0c1b627913f8\",\"Value\":0,\"Data\":\"1FDgTAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAiAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAARAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABGAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABWv1XASAmwevjckk5+BMgafwgrX2cVmzzWDuO7KB96WtZrQ7mWKO/QqjQkAMAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACJIOb5M3AP3dsFA6Uel3m0C/Vaz5ko9BIfwLqL75CDkGR2FFvqKtB+p2sNKQUfMUlb0BzaNu4ao4YBAAAAAAAU916zUV7Qmpm0K/8DO1As5H/XOvAPcmVjZWl2ZUNyb3NzRERDqRoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFJveiCJOfPOtpgRfwCNtELjNWpTaARSb3ogiTnzzraYEX8AjbRC4zVqU2hSb3ogiTnzzraYEX8AjbRC4zVqU2hoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADdXJsBGRhdGEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABxQAAAADbBW3RAAAAAM3GpjW+ps/X8mJ9sanmfivNG0y1Bu3/HjRMZjPGfUEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADFsn2ZOrgGxA6Q3A1zWrQfNntkn0lMUGXHPhdMpyoNFHPuqdTgEjqdcLKD6qQ1G7sQisbcpTZq6pJRjenrqe/XInENYy2WJgDfqevckXGQa/0SAXsibGVhZGVyIjozLCJ2cmZfdmFsdWUiOiJCRUdCQnpNRnFzTXNaWHN5K1BMT1czTjRUQWg1bjNJWWFDZkFyZG9HT0RzOEFuVGRQQlRWS3RuWGMzc2xMQlJ0dXVnTTdQMEc0VUNuZllaOEs1TTlRbDg9IiwidnJmX3Byb29mIjoieEZFaXR1dDA2UkdSU1ZmN0dzaFdoc2Z4V2UyTEtPbU55WHk4ZTlSSGhNYW9CUjBQaXRQNjhDVnhNaHpVbG1BdkswcnI0Q3JXeDBuL09ad1k3b0NqZWc9PSIsImxhc3RfY29uZmlnX2Jsb2NrX251bSI6MjUyMDAwMCwibmV3X2NoYWluX2NvbmZpZyI6bnVsbH0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwySdc4ttjzck8wFP+Tj/sPeKZOOeO4A+uXaQ/C9hZMOFRot1I6WkMKuNXNsY30RCe9eCx5U6S9mVXVTSsBcsda0AMRJ3auPPcruIWoYh+pb3uSj7NFSfeviJWTEnCjWXhGAjMHeMnBeYwVs4uUBjEJM2PMXAfNSzImZhb4ZbjxULwgFVdvn6HrIZMMaImK+uDjVc/7EYevmAgrekPtZKcpVLrRkHnzEzkpkGNLC7NhXRUXcX0lVFXh92GlBQ2RsqflCZAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"AccessList\":null,\"v\":null,\"r\":null,\"s\":null}"
            }
        ],
        "total": 1
    },
    "msg": "success"
}
```


## 4-发送交易到以太坊

查询得到待签名的跨链数据后，请参见 [签名示例代码](更新github地址！！！！)，本地完成对跨链数据的签名，然后通过调用本接口向以太坊主网提交跨链数据。

**注意:** 如果您不通过此接口向以太坊主网发起交易，那么以太坊上交易成功，则 BSN-DDC 跨链服务也可识别，但是如果以太坊上交易失败，那么 BSN-DDC 跨链服务则无法识别，该笔跨链数据的跨链状态仍为“跨链中”，所以不会向您发起退款。如遇此问题，您可以继续通过此接口再次向以太坊提交交易，BSN-DDC 跨链服务将自动识别并更新跨链状态。

**接口名称:**`api/v1/sendTx`

**请求方式:**`POST`

**请求参数:**


| 序号 | Header 参数名称 | 参数说明 | 是否必须 | 数据类型 |
| -------- | -------- | ----- | -------- | -------- |
| 1                 | session_id                  | 获取 Token 接口返回的 session_id | Yes     | string                |
| 2                  | tx_signer                   | 获取 Token 接口请求参数内的 tx_signer | Yes     |string|



| 序号 | 业务参数名称 | 参数说明 | 是否必须 | 数据类型 |
| -------- | -------- | ----- | -------- | -------- |
|1|transaction_str|签名数据|Yes|string|
|2|from_chain_id|武汉链的侧链ID：1003650780676003|Yes|string|
|3|cross_chain_id|跨链交易ID|Yes|string|

**响应参数:**


| 序号 | 业务参数名称 | 参数说明 | 是否必须 | 类型 |
| -------- | -------- | ----- |----- |----- |
| 1    | code     | 响应码 | Yes | string |
|2|data|数据|No|string|
|3|msg|消息|Yes|string|

**请求示例:**


```shell
curl --location --request POST 'https://hk.bsngate.com/api/8ef04e41fecd6567ba9fca3fa060ff0c/bsncross/rest/api/v1/sendTx' \
--header 'Content-Type: application/json' \
--header 'Cookie: session_id=MTY2NTIxNzczNnxEdi1CQkFFQ180SUFBUkFCRUFBQV85WF9nZ0FCQm5OMGNtbHVad3dIQUFWMGIydGxiZ1p6ZEhKcGJtY01fN2NBXzdSbGVVcG9Za2RqYVU5cFNrbFZla2t4VG1sSmMwbHVValZqUTBrMlNXdHdXRlpEU2prdVpYbEtWV1ZHVG5CYU1qVnNZMmxKTmtscVFqUlBWMHByV2xSbk5FMXFTVEJhVkdScVdtcE9hRnBIUlRKTlJGRXhXbTFOZDAxcVRUSmFSRVYzV1dwb2FscEVWbWhQVkZKcldWTkpjMGx0VmpSalEwazJUVlJaTWs1VVNYaFBWRlY2VG00d0xqSnpXV3N6WHpoWVZXNXRhRkpsVFRSSk1sVmtjVzV3UWpaR00xZDBMWFpqZG1kNGRrSlhSR05XWm1NPXwtFIeTYzNiK_zZMHWT_e08XDYW4ALDBuRLKCHFVe0zjg==; tx_signer=MTY2NTIxNzczNnxEdi1CQkFFQ180SUFBUkFCRUFBQVRmLUNBQUVHYzNSeWFXNW5EQXNBQ1hSNFgzTnBaMjVsY2daemRISnBibWNNTEFBcU1IZzVZbVJsT0RneU1qUmxOMk5tTTJGa1lUWXdORFZtWXpBeU16WmtNVEJpT0dOa05XRTVOR1JofBGcbFi2gD82S82RQBwuK0di-3kj-OYdxK_YonKnV1SD' \
--data '{
  "transaction_str": "{\"type\":\"0x2\",\"nonce\":\"0x1cc9\",\"gasPrice\":null,\"maxPriorityFeePerGas\":\"0x59682eff\",\"maxFeePerGas\":\"0x59682f0f\",\"gas\":\"0x7a120\",\"value\":\"0x0\",\"input\":\"0xd450e04c00000000000000000000000000000000000000000000000000000000000000a00000000000000000000000000000000000000000000000000000000000000220000000000000000000000000000000000000000000000000000000000000042000000000000000000000000000000000000000000000000000000000000004400000000000000000000000000000000000000000000000000000000000000460000000000000000000000000000000000000000000000000000000000000015afd5701202c4997ea721153255ddd497b02cdaaa249fdd4eb95425620f66b9fdbef18c0b9a3bf42a8d090030020000000000000000000000000000000000000000000000000000000000000008a20fa4e7217b0773cf049beca5f8928ea1ebdca91942fe890f2d9cba7970867b61a145bea2ad07ea76b0d29051f31495bd01cda36ee1aa38601000000000014f75eb3515ed09a99b42bff033b502ce47fd73af00f7265636569766543726f7373444443a91b00000000000000000000000000000000000000000000000000000000000000149bde88224e7cf3ada6045fc0236d10b8cd5a94da01149bde88224e7cf3ada6045fc0236d10b8cd5a94da149bde88224e7cf3ada6045fc0236d10b8cd5a94da1b0000000000000000000000000000000000000000000000000000000000000005000000000000000000000000000000000000000000000000000000000000000375726c046461746100000000000000000000000000000000000000000000000000000000000000000000000001c500000000db056dd1000000000e6a9468e7a223926b1481ebb096f683cd9c619cfc0ebe73213c49be9d383a3c00000000000000000000000000000000000000000000000000000000000000005a671cd636dfeb553f4f5513c2db711cce1e99e3ec903d3f9156cc59f584804dbeb02bb2a6379ebef755b321e1a45e833e5aff67688f382aa41ca12e0e5727bb8bbe0d6359a626003872444826a6749ffd12017b226c6561646572223a342c227672665f76616c7565223a22424b37356a3245743635507076306e7a594a362b73447257346a42724d52324c39596c4b5255662f66496e744a526255363271445049796e6d684c615959762b694144764f432f4b594d4a6f6a6e4e534b65494b47336f3d222c227672665f70726f6f66223a22366456517a546d5030554265445677424571763536774559537874464f6a364171594648425762787432486f4e2b464f397838444c45344e6463356172584d79505a4a66766c59586a42394c4637372f6159733377673d3d222c226c6173745f636f6e6669675f626c6f636b5f6e756d223a323532303030302c226e65775f636861696e5f636f6e666967223a6e756c6c7d00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000c34a0a5e40968b87cc24876ff15857e42348b54a328134b70e5bc0b1a2d4c903f14c8dbc75d6139918bc1f433eee55f0e631b46cc67dd61b6779296f07b1c0df9a01c289790f68c19f74e2e2b0cf58c6555a86c67b8212d773c500334b44018dde0e01fe71b23d1a65db77aa47f2988e3cccc1cd1138421411f32d07397879ca89d100d910dd77db6d2a06bf6bcd725a48774e1dd8cb463897d3375187518499695e3e23a9ce6afdd2e2291e8bc8564a54b6f15889d20c27bb074fcceda32d3138de78000000000000000000000000000000000000000000000000000000000000\",\"v\":\"0x1\",\"r\":\"0x59375d2f4189d97ccf7389cbf928e99ce19033a696daec0616cbe4fa32c693ee\",\"s\":\"0x4d459de7fe41b4bccc338d23ca61f786b37d53dad3b1df3a720af878585a52cc\",\"to\":\"0x5f5d94ea7d4eb5d20621344a1c0e0c1b627913f8\",\"chainId\":\"0x3\",\"accessList\":[],\"hash\":\"0xe46a993fd32db138a565f6e390d6a91823abc73993487a1767ee03cbf0a24dcb\"}",
  "from_chain_id": "1003650780676003",
  "cross_chain_id": "27"
}'
```

**响应示例:**

```json
{
    "code": "0000",
    "data": "",
    "msg": "success"
}
```



### 响应码说明


|编码 | 说明                                                                           |
|---|------------------------------------------------------------------------------|
|0000| success                                                       |
|9001| 参数不能为空                                               |
|9002| 解析跨链数据失败，请检查数据数据结构 |
|9003| 未查到该签名账户对应的跨链数据                                                    |
|9004| 业务参数填写有误                                                 |
|9005| 不是标准的 JSON 格式                                               |
|9006| 跨链数据转换 JSON 格式失败                                  |
|9010| 获取跨链数据失败                       |
|9011| 以太坊主网节点异常                                                  |
|9012| 提交交易异常                                             |
|9013| 查询跨链状态异常                           |
|9014| 未查到相关数据                            |

