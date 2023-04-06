### BSN-DDC 开放联盟链跨链网关 OpenAPI

> BSN-DDC 开放联盟链跨链网关 OpenAPI 是跨链服务系统对外开放的接口，如果平台方、算力中心方没有此需求可忽略此文。 

* **通讯协议：** `HTTP`
* **通讯报文数据格式：**`application/json`
* **服务通讯地址：**`https://ddccross.bsnbase.com`

#### 1. 获取 Token
**调用跨链服务系统其它对外接口前，需要先调用本接口获取 token，调用其它接口时需要将 token 作为请求头参数**。token 可以复用，但需要注意 token 是否过期，过期后需再次获取新的 token。

* **接口名称:** `api/crosschain/getToken`

* **请求方式:** `POST`

* **请求参数:**

|序号|参数名称|参数说明|是否必须|参数类型|
|---|---|---|---|---|
|1|accountAddress|链账户地址（包含`0x`前缀）|是|String|
|2|signature|链账户私钥对链账户地址进行签名的结果（签名算法说明见文章末尾）|是|String|

* **响应参数:**

|序号|参数名称|参数说明|是否必须|参数类型 |
|---|---|---|---|---|
|1|code|响应码|是|Integer|
|2|data|token值|是|String|
|3|msg|说明|是|String|

* **请求示例:**

```shell
curl -X POST "http://localhost:9019/api/cross/chain/getToken" -H "accept: */*" -H "token: 1" -H "Content-Type: application/json" -d "{ \"accountAddress\": \"0x50AB82F7EF1DAEB40E8370D7ACCBBE5B345A0F34\", \"signature\": \"0xa9392573e4a708cd7d11af816eb992c47eeab70e22bbcd0c1c03caca843e6afd0x4d8b2ea556e14f692093be287f5449d3cb70dd01a2675a1a23b71a88296277210x1c\"}"
```

* **响应示例:**

```json
{
	"code": 0,
	"msg": "success",
	"data": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIweDUwQUI4MkY3RUYxREFFQjQwRTgzNzBEN0FDQ0JCRTVCMzQ1QTBGMzQiLCJpYXQiOjE2ODA0MjQzNDV9.3er46t_pv0hkQOV3OFDNiFDqYed5l6FvSrquctklyi_XxBuF_bccDEEYxFN79zvDSRDpg6lTYNX6koWJdnHYJw"
}
```

#### 2. 查询支持的跨链框架

查询跨链服务系统已经支持的跨链框架。

**接口名称:**`api/crosschain/supported/framework`

**请求方式:**`GET`

**请求参数:**

|序号|参数名称|参数说明|是否必须|参数类型|
|---|---|---|---|---|
|无|||||

**响应参数:**

|序号|参数名称|参数说明|是否必须|参数类型 |
|---|---|---|---|---|
|1|code|响应码|是|Integer|
|2|data|是|
|2.1|&emsp;&emsp;list||||
|2.1.1|&emsp;&emsp;&emsp;&emsp;originChainId|起始链 Id|是|Integer|
|2.1.2|&emsp;&emsp;&emsp;&emsp;originChainName|起始链名称|是|String|
|2.1.3|&emsp;&emsp;&emsp;&emsp;targetChainId|目标链 Id|是|Integer|
|2.1.4|&emsp;&emsp;&emsp;&emsp;targetChainName|目标链名称|是|String|
|2.1.5|&emsp;&emsp;&emsp;&emsp;ddcType|支持的 DDC 类型（0:721、1:1155、2：全部）|是|Integer|
|3|msg|说明|是|String|

**请求示例:**

```shell
curl -X GET "http://localhost:9019/api/cross/chain/supported/framework" -H "accept: */*" -H "token: eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIweDUwQUI4MkY3RUYxREFFQjQwRTgzNzBEN0FDQ0JCRTVCMzQ1QTBGMzQiLCJpYXQiOjE2ODA0MjQzNDV9.3er46t_pv0hkQOV3OFDNiFDqYed5l6FvSrquctklyi_XxBuF_bccDEEYxFN79zvDSRDpg6lTYNX6koWJdnHYJw"
```

**响应示例:**

```json
{
	"code": 0,
	"msg": "success",
	"data": [{
			"originChainId": 4,
			"originChainName": "武汉",
			"targetChainId": 2,
			"targetChainName": "文昌",
			"ddcType": 2
		},
		{
			"originChainId": 3,
			"originChainName": "泰安",
			"targetChainId": 2,
			"targetChainName": "文昌",
			"ddcType": 2
		}
	]
}
```
#### 3. 查询跨链交易详情

通过起始链 Id、起始链交易哈希、起始链 DDC Id 等参数可以查询跨链交易详情。该接口支持分页查询。

**接口名称:**`api/crosschain/transaction/detail`

**请求方式:**`POST`

**请求参数:**

|序号|参数名称|参数说明|是否必须|参数类型|
|---|---|---|---|---|
|1|originChainId|起始链 Id|是|Integer|
|2|originChainHash|起始链交易哈希|否|String|
|3|originCrossChainId|起始链唯一跨链 Id|否|Integer|
|4|originChainDdcId|起始链 DDC Id|否|Integer|
|4|pageNum|页码（从1开始）|是|Integer|
|4|pageSize|每页数据条数（默认10条）|是|Integer|

**响应参数:**

|序号|参数名称|参数说明|是否必须|参数类型 |
|---|---|---|---|---|
|1|code|响应码|是|Integer|
|2|data|是|||
|2.1|&emsp;&emsp;records||||
|2.1.1|&emsp;&emsp;&emsp;&emsp;originChainId|起始链 Id|是|Integer|
|2.1.2|&emsp;&emsp;&emsp;&emsp;originCrossChainId|起始链唯一跨链 Id|是|Integer|
|2.1.3|&emsp;&emsp;&emsp;&emsp;originChainDdcId|起始链 DDC Id|是|Integer|
|2.1.4|&emsp;&emsp;&emsp;&emsp;originChainDdcOwner|起始链 DDC Owner|是|String|
|2.1.5|&emsp;&emsp;&emsp;&emsp;originChainHash|起始链交易哈希|是|String|
|2.1.6|&emsp;&emsp;&emsp;&emsp;originChainTime|起始链交易时间|是|String|
|2.1.7|&emsp;&emsp;&emsp;&emsp;originChainSender|起始链交易签名账户|是|String|
|2.1.8|&emsp;&emsp;&emsp;&emsp;locked|是否锁定|是|Boolean|
|2.1.9|&emsp;&emsp;&emsp;&emsp;ddcType|DDC 类型|是|String|
|2.1.10|&emsp;&emsp;&emsp;&emsp;ddcUri|DDC URI|是|String|
|2.1.11|&emsp;&emsp;&emsp;&emsp;ddcAmount|DDC 数量|是|Integer|
|2.1.12|&emsp;&emsp;&emsp;&emsp;data|额外参数|是|String|
|2.1.13|&emsp;&emsp;&emsp;&emsp;targetChainDdcOwner|目标链 DDC Owner|是|String|
|2.1.14|&emsp;&emsp;&emsp;&emsp;targetChainId|目标链 Id|是|Integer|
|2.1.15|&emsp;&emsp;&emsp;&emsp;targetChainHash|目标链交易哈希|是|String|
|2.1.16|&emsp;&emsp;&emsp;&emsp;targetChainTime|目标链交易时间|是|String|
|2.1.17|&emsp;&emsp;&emsp;&emsp;targetChainDdcId|目标链DDC Id|是|Integer|
|2.1.18|&emsp;&emsp;&emsp;&emsp;targetChainSender|目标链交易签名账户|是|String|
|2.1.19|&emsp;&emsp;&emsp;&emsp;crossChainFee|跨链费用|是|Integer|
|2.1.20|&emsp;&emsp;&emsp;&emsp;status|跨链状态（0：未确认，1：已确认，2：进行中，3：成功，4：失败）|是|Integer|
|2.2|&emsp;&emsp;total|总数据条数|是|Long|
|2.3|&emsp;&emsp;size|每页数据条数|是|Long|
|2.4|&emsp;&emsp;current|当前页码|是|Long|
|2.5|&emsp;&emsp;pages|总页数|是|Long|
|3|msg|说明|是|String|

**请求示例:**

```shell
curl -X POST "http://localhost:9019/api/cross/chain/transaction/detail" -H "accept: */*" -H "token: eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIweDUwQUI4MkY3RUYxREFFQjQwRTgzNzBEN0FDQ0JCRTVCMzQ1QTBGMzQiLCJpYXQiOjE2ODA0MjQzNDV9.3er46t_pv0hkQOV3OFDNiFDqYed5l6FvSrquctklyi_XxBuF_bccDEEYxFN79zvDSRDpg6lTYNX6koWJdnHYJw" -H "Content-Type: application/json" -d "{ \"originChainDdcId\": 101, \"originChainId\": 4, \"pageNum\": 1, \"pageSize\": 10}"
```

**响应示例:**

```json
{
	"code": 0,
	"msg": "success",
	"data": {
		"records": [{
			"originChainId": 4,
			"originCrossChainId": 17,
			"originChainDdcId": 101,
			"originChainDdcOwner": "0x01959ec212d9726f7782eba3cc831f0163fde156",
			"originChainHash": "0xd8b9fa735fd78b3fbf3169b8893f9dd9a732af2a32e75df2613ff44c66108fe4",
			"originChainTime": "2023-04-02T16:48:26.000+08:00",
			"originChainSender": "0x01959ec212d9726f7782eba3cc831f0163fde156",
			"locked": false,
			"ddcType": "721",
			"ddcUri": "{\"Name\":\"TestName1\",\"Symbol\":\"DDCSymbol\",\"URL\":\"http://www.bsn-test.asia:18000\",\"Issuer\":\"zhangsan\",\"Remarks\":\"TestRemarks\"}",
			"ddcAmount": 1,
			"data": "00",
			"targetChainDdcOwner": "0x605f289400ee47583ede9a7dc9f0c5900a5c6402",
			"targetChainId": 2,
			"targetChainHash": "0x7210e4cf9cc0a59ecda3d5c5307a0f6ac7d8fc40de0de33398ecf77244ae52cc",
			"targetChainTime": "2023-04-02T16:56:57.000+08:00",
			"targetChainDdcId": 1605,
			"targetChainSender": "0x37ecf73d55c11bcaa0668a007ea5ec9947be65cb",
			"crossChainFee": 500,
			"status": 3
		}],
		"total": 1,
		"size": 10,
		"current": 1,
		"pages": 1
	}
}
```

#### 4. 查询需要目标链用户签名确认的跨链交易

通过目标链 Id、目标链账户地址等参数查询需要目标链用户签名确认的跨链交易。该接口支持分页查询。

**接口名称:**`api/crosschain/unconfirmed/transaction`

**请求方式:**`POST`

**请求参数:**

|序号|参数名称|参数说明|是否必须|参数类型|
|---|---|---|---|---|
|1|chainId|目标链 Id|是|Integer|
|2|accountAddress|目标链账户地址|否|String|
|4|pageNum|页码（从1开始）|是|Integer|
|4|pageSize|每页数据条数（默认10条）|是|Integer|

**响应参数:**

|序号|参数名称|参数说明|是否必须|参数类型 |
|---|---|---|---|---|
|1|code|响应码|是|Integer|
|2|data|是|||
|2.1|&emsp;&emsp;records||||
|2.1.1|&emsp;&emsp;&emsp;&emsp;originChainId|起始链 Id|是|Integer|
|2.1.2|&emsp;&emsp;&emsp;&emsp;originCrossChainId|起始链唯一跨链 Id|是|Integer|
|2.1.3|&emsp;&emsp;&emsp;&emsp;originChainDdcId|起始链 DDC Id|是|Integer|
|2.1.4|&emsp;&emsp;&emsp;&emsp;originChainDdcOwner|起始链 DDC Owner|是|String|
|2.1.5|&emsp;&emsp;&emsp;&emsp;originChainHash|起始链交易哈希|是|String|
|2.1.6|&emsp;&emsp;&emsp;&emsp;originChainTime|起始链交易时间|是|String|
|2.1.7|&emsp;&emsp;&emsp;&emsp;originChainSender|起始链交易签名账户|是|String|
|2.1.8|&emsp;&emsp;&emsp;&emsp;locked|是否锁定|是|Boolean|
|2.1.9|&emsp;&emsp;&emsp;&emsp;ddcType|DDC 类型|是|String|
|2.1.10|&emsp;&emsp;&emsp;&emsp;ddcUri|DDC URI|是|String|
|2.1.11|&emsp;&emsp;&emsp;&emsp;ddcAmount|DDC 数量|是|Integer|
|2.1.12|&emsp;&emsp;&emsp;&emsp;data|额外参数|是|String|
|2.1.13|&emsp;&emsp;&emsp;&emsp;targetChainDdcOwner|目标链 DDC Owner|是|String|
|2.1.14|&emsp;&emsp;&emsp;&emsp;targetChainId|目标链 Id|是|Integer|
|2.2|&emsp;&emsp;total|总数据条数|是|Long|
|2.3|&emsp;&emsp;size|每页数据条数|是|Long|
|2.4|&emsp;&emsp;current|当前页码|是|Long|
|2.5|&emsp;&emsp;pages|总页数|是|Long|
|3|msg|说明|是|String|

**请求示例:**

```shell
curl -X POST "http://localhost:9019/api/cross/chain/unconfirmed/transaction" -H "accept: */*" -H "token: eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIweDUwQUI4MkY3RUYxREFFQjQwRTgzNzBEN0FDQ0JCRTVCMzQ1QTBGMzQiLCJpYXQiOjE2ODA0MjQzNDV9.3er46t_pv0hkQOV3OFDNiFDqYed5l6FvSrquctklyi_XxBuF_bccDEEYxFN79zvDSRDpg6lTYNX6koWJdnHYJw" -H "Content-Type: application/json" -d "{ \"accountAddress\": \"0xde35e7cf540bca5f289b4f1b03b9af758f5aa972\", \"chainId\": 3, \"pageNum\": 1, \"pageSize\": 10}"
```

**响应示例:**

```json
{
	"code": 0,
	"msg": "success",
	"data": {
		"records": [{
			"id": 34,
			"originChainId": 4,
			"originCrossChainId": 20,
			"originChainDdcId": 104,
			"originChainDdcOwner": "0x01959ec212d9726f7782eba3cc831f0163fde156",
			"originChainHash": "0x36e2897eb7b6f2350d6cffbd2428316f9892a3be4e12985f9a2907ff6cb4c587",
			"originChainTime": "2023-04-02T17:10:36.000+08:00",
			"originChainSender": "0x01959ec212d9726f7782eba3cc831f0163fde156",
			"locked": true,
			"ddcType": "721",
			"ddcUri": "{\"Name\":\"TestName1\",\"Symbol\":\"DDCSymbol\",\"URL\":\"http://www.bsn-test.asia:18000\",\"Issuer\":\"zhangsan\",\"Remarks\":\"TestRemarks\"}",
			"ddcAmount": 1,
			"data": "00",
			"targetChainDdcOwner": "0xde35e7cf540bca5f289b4f1b03b9af758f5aa972",
			"targetChainId": 3
		}],
		"total": 1,
		"size": 10,
		"current": 1,
		"pages": 1
	}
}
```

#### 5. 目标链用户确认跨链交易
调用跨链服务系统其它对外接口前，需要先调用本接口获取 token，**调用其它接口时需要将 token 作为请求头参数**。token 可以复用，但需要关注 token 的过期时间，过期后需再次获取新的 token。

* **接口名称:** `api/crosschain/confirm/transaction`

* **请求方式:** `POST`

* **请求参数:**

|序号|参数名称|参数说明|是否必须|参数类型|
|---|---|---|---|---|
|1|accountAddress|DDC 在目标链上的 Owner 的目标链账户地址（包含`0x`前缀）|是|String|
|2|signature|DDC 在目标链上的 Owner 的链账户私钥对链账户地址进行签名的结果（签名算法说明见文章末尾）|是|String|
|2|hash|起始链交易哈希|是|String|

* **响应参数:**

|序号|参数名称|参数说明|是否必须|参数类型 |
|---|---|---|---|---|
|1|code|响应码|是|Integer|
|2|data|token值|是|String|
|3|msg|说明|是|String|

**请求示例:**

```shell
curl -X POST "http://localhost:9019/api/cross/chain/unconfirmed/transaction" -H "accept: */*" -H "token: eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIweDUwQUI4MkY3RUYxREFFQjQwRTgzNzBEN0FDQ0JCRTVCMzQ1QTBGMzQiLCJpYXQiOjE2ODA0MjQzNDV9.3er46t_pv0hkQOV3OFDNiFDqYed5l6FvSrquctklyi_XxBuF_bccDEEYxFN79zvDSRDpg6lTYNX6koWJdnHYJw" -H "Content-Type: application/json" -d "{ \"accountAddress\": \"0xde35e7cf540bca5f289b4f1b03b9af758f5aa972\", \"chainId\": 3, \"pageNum\": 1, \"pageSize\": 10}"
```

**响应示例:**

```json
{
	"code": 0,
	"msg": "success",
	"data": {
		"records": [{
			"id": 34,
			"originChainId": 4,
			"originCrossChainId": 20,
			"originChainDdcId": 104,
			"originChainDdcOwner": "0x01959ec212d9726f7782eba3cc831f0163fde156",
			"originChainHash": "0x36e2897eb7b6f2350d6cffbd2428316f9892a3be4e12985f9a2907ff6cb4c587",
			"originChainTime": "2023-04-02T17:10:36.000+08:00",
			"originChainSender": "0x01959ec212d9726f7782eba3cc831f0163fde156",
			"locked": true,
			"ddcType": "721",
			"ddcUri": "{\"Name\":\"TestName1\",\"Symbol\":\"DDCSymbol\",\"URL\":\"http://www.bsn-test.asia:18000\",\"Issuer\":\"zhangsan\",\"Remarks\":\"TestRemarks\"}",
			"ddcAmount": 1,
			"data": "00",
			"targetChainDdcOwner": "0xde35e7cf540bca5f289b4f1b03b9af758f5aa972",
			"targetChainId": 3
		}],
		"total": 1,
		"size": 10,
		"current": 1,
		"pages": 1
	}
}
```

### 响应码说明

|编码|说明|
|---|---|
|0|success|
|10001|参数为空相关异常|
|10002|token 验证相关异常|
|10003|签名验证相关异常|
|10004|目标链用户重复确认跨链交易异常|
|10005|跨链交易不存在|
|19999|其它异常|

### 签名算法说明

平台方、算力中心方需要使用用户链账户私钥对链账户地址进行签名，建议使用 [web3j](https://github.com/web3j/web3j) 实现。大致的实现步骤如下：

1. 链账户私钥、链账户地址需要使用16进制格式，注意链账户地址不要省略`0x`前缀。
2. 使用私钥计算出`SECP-256k1`算法的密钥对`ECKeyPair`。
3. 使用上一步获得的密钥对`ECKeyPair`对链账户地址进行签名，获得签名结果`SignatureData`对象。
4. 将上一步签名结果对象中的`R`、`S`、`V`三个变量的值依次转换为16进制字符串，然后依次拼接得到最终的签名结果字符串。

