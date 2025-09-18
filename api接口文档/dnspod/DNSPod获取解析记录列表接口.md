# DNSPod获取解析记录列表接口文档

## 接口概述

本接口基于腾讯云DNSPod API的DescribeRecordList功能，用于获取指定域名下的解析记录列表。支持多种查询条件和排序方式。

## 接口信息

- **接口标识**: `DNSPOD_GET_RECORD_LIST`
- **请求路径**: `POST /api/dnspod/records/list`
- **请求方法**: `POST`
- **Content-Type**: `application/json`
- **接口描述**: 获取域名的解析记录列表，支持分页、排序、筛选等功能
- **认证要求**: 无需认证（当前配置）
- **适用业务单元**: DNS解析记录管理

## 请求参数

### JSON请求体格式

```json
{
  "domain": "cblog.eu",
  "domainId": 98251198,
  "subdomain": "www",
  "recordType": "A",
  "recordLine": "默认",
  "recordLineId": "0",
  "groupId": 1,
  "keyword": "test",
  "sortField": "type",
  "sortType": "ASC",
  "offset": 0,
  "limit": 10
}
```

### 参数说明

| 参数名 | 类型 | 必填 | 描述 | 示例值 |
|--------|------|------|------|--------|
| domain | String | 是 | 域名名称 | cblog.eu |
| domainId | Integer | 否 | 域名ID（优先级比domain高） | 98251198 |
| subdomain | String | 否 | 解析记录的主机头 | www |
| recordType | String | 否 | 记录类型（A、CNAME、NS、AAAA等） | A |
| recordLine | String | 否 | 线路名称 | 默认 |
| recordLineId | String | 否 | 线路ID（优先级比recordLine高） | 0 |
| groupId | Integer | 否 | 分组ID | 1 |
| keyword | String | 否 | 关键字搜索（支持搜索主机头和记录值） | test |
| sortField | String | 否 | 排序字段（name,line,type,value,weight,mx,ttl,updated_on） | type |
| sortType | String | 否 | 排序方式（ASC或DESC，默认ASC） | ASC |
| offset | Integer | 否 | 偏移量（默认0） | 0 |
| limit | Integer | 否 | 限制数量（默认100，最大3000） | 10 |

## 响应格式

### 成功响应（200）

```json
{
  "code": 200,
  "message": "获取解析记录列表成功",
  "data": {
    "skipSign": false,
    "requestId": "xxx-xxx-xxx",
    "recordCountInfo": {
      "skipSign": false,
      "subdomainCount": 2,
      "totalCount": 2,
      "listCount": 2
    },
    "recordList": [
      {
        "recordId": 556507778,
        "value": "f1g1ns1.dnspod.net.",
        "status": "ENABLE",
        "updatedOn": "2021-03-28 11:27:09",
        "name": "@",
        "line": "默认",
        "lineId": "0",
        "type": "NS",
        "weight": null,
        "monitorStatus": "",
        "remark": "",
        "ttl": 86400,
        "mx": 0,
        "defaultNS": true
      }
    ]
  },
  "timestamp": "2025-08-21T08:55:00Z"
}
```

### 失败响应

#### 域名格式错误（500）

```json
{
  "code": 500,
  "message": "获取解析记录列表失败: 调用腾讯云DNSPod API失败: 域名不正确，请输入主域名，如 dnspod.cn。",
  "data": null,
  "timestamp": "2025-08-21T08:55:00Z"
}
```

#### 权限不足（500）

```json
{
  "code": 500,
  "message": "获取解析记录列表失败: 调用腾讯云DNSPod API失败: 域名不在您的名下。",
  "data": null,
  "timestamp": "2025-08-21T08:55:00Z"
}
```

## 使用示例

### 示例1：获取域名的所有解析记录

```bash
curl -X POST "http://localhost:8080/api/dnspod/records/list" \
  -H "Content-Type: application/json" \
  -d '{
    "domain": "cblog.eu"
  }'
```

### 示例2：获取A记录

```bash
curl -X POST "http://localhost:8080/api/dnspod/records/list" \
  -H "Content-Type: application/json" \
  -d '{
    "domain": "cblog.eu",
    "recordType": "A"
  }'
```

### 示例3：分页查询

```bash
curl -X POST "http://localhost:8080/api/dnspod/records/list" \
  -H "Content-Type: application/json" \
  -d '{
    "domain": "cblog.eu",
    "limit": 10,
    "offset": 0
  }'
```

### 示例4：关键字搜索

```bash
curl -X POST "http://localhost:8080/api/dnspod/records/list" \
  -H "Content-Type: application/json" \
  -d '{
    "domain": "cblog.eu",
    "keyword": "www"
  }'
```

### 示例5：按类型排序

```bash
curl -X POST "http://localhost:8080/api/dnspod/records/list" \
  -H "Content-Type: application/json" \
  -d '{
    "domain": "cblog.eu",
    "sortField": "type",
    "sortType": "ASC"
  }'
```

### 示例6：复合查询

```bash
curl -X POST "http://localhost:8080/api/dnspod/records/list" \
  -H "Content-Type: application/json" \
  -d '{
    "domain": "cblog.eu",
    "recordType": "A",
    "sortField": "name",
    "sortType": "DESC",
    "limit": 5,
    "offset": 0
  }'
```

## 注意事项

1. **域名格式要求**: 腾讯云DNSPod API对域名格式有严格要求，必须是有效的主域名格式
2. **权限验证**: 只能查询当前账户下的域名解析记录
3. **API限制**: 默认接口请求频率限制为100次/秒
4. **数据延迟**: 新添加的解析记录存在短暂的索引延迟，如果查询不到新增记录，请在30秒后重试
5. **记录数量**: API获取的记录总条数会比控制台多2条NS记录
6. **Content-Type**: 必须使用`application/json`格式发送请求

## 错误码说明

| 错误码 | 描述 | 解决方案 |
|--------|------|----------|
| InvalidParameter.DomainInvalid | 域名不正确 | 请输入有效的主域名格式 |
| FailedOperation.NotDomainOwner | 域名不在您的名下 | 请确认域名所有权 |
| InvalidParameterValue.LimitInvalid | 分页长度数量错误 | 请检查limit参数范围 |
| InvalidParameter.ResultMoreThan500 | 搜索结果大于500条 | 请增加关键字缩小搜索范围 |
| RequestLimitExceeded | 请求次数超过频率限制 | 请降低请求频率 |

## 兼容性说明

为了保持向后兼容性，系统同时支持两种调用方式：

1. **新版本（推荐）**: `POST /api/dnspod/records/list` - 使用JSON格式请求体
2. **旧版本**: `GET /api/dnspod/records/list` - 使用URL查询参数

建议新项目使用POST方式的JSON格式，旧项目可以继续使用GET方式。

## 相关接口

- [DNSPod获取域名列表接口](./DNSPod获取域名列表接口.md)
- [DNSPod添加记录接口](./DNSPod添加记录接口.md)
- [DNSPod修改记录接口](./DNSPod修改记录接口.md)
- [DNSPod删除记录接口](./DNSPod删除记录接口.md)

## 版本历史

| 版本 | 日期 | 变更内容 |
|------|------|----------|
| v1.0.0 | 2025-08-21 | 初始版本，实现基础的解析记录列表查询功能 |
| v1.1.0 | 2025-08-21 | 新增JSON格式支持，使用POST方法和application/json Content-Type |