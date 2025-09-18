# DNSPod修改记录接口

## 一、接口概述

### 1.1 接口描述
修改域名的DNS解析记录

### 1.2 接口基本信息
- **接口标识**：`DNSPOD_MODIFY_RECORD`
- **请求路径**：`POST /api/dnspod/records/modify`
- **接口描述**：修改指定域名的DNS解析记录
- **认证要求**：需要JWT认证
- **适用业务单元**：域名管理

## 二、请求参数

### 2.1 请求参数列表

| 参数名称   | 必选 | 类型   | 描述                                                         |
| :--------- | :--- | :----- | :----------------------------------------------------------- |
| domain     | 是   | String | 域名，如 example.com                                         |
| recordId   | 是   | Long   | 记录ID，可通过查询接口获取                                   |
| recordType | 是   | String | 记录类型，如 A、CNAME、MX等                                  |
| value      | 是   | String | 记录值，如 IP地址、域名等                                    |
| recordLine | 否   | String | 记录线路，如 "默认"，不传默认为"默认"                        |
| subDomain  | 否   | String | 主机记录，如 www，不传默认为@                                |
| domainId   | 否   | Long   | 域名ID，可通过查询接口获取，如果传递domainId将忽略domain参数 |
| ttl        | 否   | Long   | TTL值，范围1-604800，不同等级域名最小值不同                  |
| mx         | 否   | Long   | MX优先级，当记录类型是MX时必填，范围1-65535                  |
| weight     | 否   | Long   | 权重信息，0到100的整数，0表示关闭                            |
| status     | 否   | String | 记录状态，取值范围为ENABLE和DISABLE，默认为ENABLE            |
| remark     | 否   | String | 记录的备注信息                                               |

### 2.2 请求格式（JSON）

```json
{
  "domain": "vvvv.host",
  "recordId": 2160398569,
  "recordType": "A",
  "value": "192.168.1.1",
  "recordLine": "默认",
  "subDomain": "www",
  "ttl": 600,
  "mx": null,
  "weight": null,
  "status": "ENABLE",
  "remark": "修改后的备注"
}
```

### 2.3 请求示例

```bash
curl -X POST "http://localhost:8080/api/dnspod/records/modify" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "domain": "vvvv.host",
    "recordId": 2160398569,
    "recordType": "A",
    "value": "192.168.1.1",
    "recordLine": "默认",
    "subDomain": "www"
  }'
```

## 三、响应参数

### 3.1 响应参数列表

| 参数名称  | 类型    | 描述                                                         |
| :-------- | :------ | :----------------------------------------------------------- |
| code      | Integer | 业务状态码，200表示成功                                      |
| message   | String  | 响应描述信息                                                 |
| data      | Object  | 响应数据，包含修改记录的结果                                 |
| timestamp | String  | 服务器响应时间戳（ISO 8601格式）                             |

### 3.2 data字段说明

| 参数名称  | 类型    | 描述                                                         |
| :-------- | :------ | :----------------------------------------------------------- |
| recordId  | Integer | 记录ID                                                       |
| requestId | String  | 请求ID，用于问题定位                                         |

### 3.3 响应示例

#### 成功响应
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "recordId": 123456,
    "requestId": "ab4f1426-ea15-42ea-8183-dc1b44151166"
  },
  "timestamp": "2025-08-17T09:30:00.123456789"
}
```

#### 失败响应
```json
{
  "code": 500,
  "message": "修改域名解析记录失败: 调用腾讯云DNSPod修改记录API失败: 记录编号错误。",
  "data": null,
  "timestamp": "2025-08-17T09:30:00.123456789"
}
```

## 四、错误码

| 错误码 | 描述                                                         |
| :----- | :----------------------------------------------------------- |
| 200    | 操作成功                                                     |
| 400    | 请求参数错误                                                 |
| 401    | 未授权，需要登录                                             |
| 403    | 无权限操作                                                   |
| 500    | 服务器内部错误，包括调用腾讯云API失败等                      |

## 五、测试用例

### 5.1 成功修改记录
- **请求参数**：
  ```json
  {
    "domain": "example.com",
    "recordId": 123456,
    "recordType": "A",
    "value": "192.168.1.1",
    "recordLine": "默认",
    "subDomain": "www"
  }
  ```
- **预期结果**：
  - 状态码：200
  - 返回修改成功的记录ID

### 5.2 记录ID不存在
- **请求参数**：
  ```json
  {
    "domain": "example.com",
    "recordId": 999999,
    "recordType": "A",
    "value": "192.168.1.1"
  }
  ```
- **预期结果**：
  - 状态码：500
  - 错误信息：包含"记录编号错误"

### 5.3 未授权访问
- **请求参数**：无认证信息
- **预期结果**：
  - 状态码：401
  - 错误信息：未授权

## 六、注意事项

1. 修改记录时，recordId和domain是必填参数，用于定位要修改的记录
2. 如果提供了domainId，将优先使用domainId而忽略domain参数
3. 修改MX记录时，必须提供mx参数（MX优先级）
4. 权重参数weight的取值范围是0-100，0表示关闭权重
5. 接口需要JWT认证，请确保在请求头中包含有效的Authorization信息
6. **接口格式变更**：现在使用JSON请求体格式，Content-Type为application/json
