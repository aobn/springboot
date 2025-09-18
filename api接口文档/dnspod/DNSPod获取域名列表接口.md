# DNSPod获取域名列表接口文档

## 接口基本信息

- **接口标识**：`DNSPOD_GET_DOMAIN_LIST`
- **请求路径**：`GET /api/dnspod/domains` 或 `POST /api/dnspod/domains/list`
- **请求方法**：`GET`（URL参数）或 `POST`（JSON格式）
- **Content-Type**：`application/json`（POST方式）
- **接口描述**：获取腾讯云DNSPod账户下的域名列表
- **认证要求**：无需认证（公开接口）
- **适用业务单元**：域名管理模块

## 请求参数

### 方式一：GET请求（URL参数）

| 参数名称 | 必选 | 类型    | 默认值 | 描述                                                         |
| -------- | ---- | ------- | ------ | ------------------------------------------------------------ |
| type     | 否   | String  | ALL    | 域名分组类型，可取值：ALL（全部）、MINE（我的）、SHARE（共享）、ISMARK（星标）、PAUSE（暂停）、VIP（VIP）、RECENT（最近）、SHARE_OUT（共享给他人）、FREE（免费） |
| offset   | 否   | Integer | 0      | 记录开始的偏移，第一条记录为0，依次类推                      |
| limit    | 否   | Integer | 20     | 要获取的域名数量，比如获取20个，则为20。最大值为3000         |
| groupId  | 否   | Integer | -      | 分组ID，获取指定分组的域名                                   |
| keyword  | 否   | String  | -      | 根据关键字搜索域名                                           |

### 方式二：POST请求（JSON格式）

```json
{
  "type": "ALL",
  "offset": 0,
  "limit": 20,
  "groupId": 1,
  "keyword": "example"
}
```

### 请求示例

#### GET方式（URL参数）

```bash
# 获取所有域名（默认参数）
curl -X GET "http://localhost:8080/api/dnspod/domains"

# 获取前10个域名
curl -X GET "http://localhost:8080/api/dnspod/domains?limit=10"

# 根据关键字搜索域名
curl -X GET "http://localhost:8080/api/dnspod/domains?keyword=example&limit=5"

# 获取指定分组的域名
curl -X GET "http://localhost:8080/api/dnspod/domains?groupId=1&limit=10"
```

#### POST方式（JSON格式）

```bash
# 获取所有域名（默认参数）
curl -X POST "http://localhost:8080/api/dnspod/domains/list" \
  -H "Content-Type: application/json" \
  -d '{}'

# 获取前10个域名
curl -X POST "http://localhost:8080/api/dnspod/domains/list" \
  -H "Content-Type: application/json" \
  -d '{
    "limit": 10
  }'

# 根据关键字搜索域名
curl -X POST "http://localhost:8080/api/dnspod/domains/list" \
  -H "Content-Type: application/json" \
  -d '{
    "keyword": "example",
    "limit": 5
  }'

# 获取指定分组的域名
curl -X POST "http://localhost:8080/api/dnspod/domains/list" \
  -H "Content-Type: application/json" \
  -d '{
    "groupId": 1,
    "limit": 10
  }'
```

## 响应格式

### 成功响应（状态码 200）

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "DomainCountInfo": {
      "AllTotal": 35,
      "DomainTotal": 1,
      "ErrorTotal": 28,
      "GroupTotal": 0,
      "LockTotal": 1,
      "MineTotal": 28,
      "PauseTotal": 1,
      "ShareOutTotal": 4,
      "ShareTotal": 7,
      "SpamTotal": 0,
      "VipExpire": 0,
      "VipTotal": 4
    },
    "DomainList": [
      {
        "CNAMESpeedup": "DISABLE",
        "CreatedOn": "2021-05-06 20:40:39",
        "DNSStatus": "DNSERROR",
        "DomainId": 12614766,
        "EffectiveDNS": [
          "ns3.dnsv5.com",
          "ns4.dnsv5.com"
        ],
        "Grade": "DP_ULTRA",
        "GradeLevel": 10,
        "GradeTitle": "尊享版",
        "GroupId": 1,
        "IsVip": "YES",
        "Name": "example.com",
        "Owner": "qcloud_uin_000000000@qcloud.com",
        "Punycode": "example.com",
        "RecordCount": 0,
        "Remark": "",
        "SearchEnginePush": "NO",
        "Status": "ENABLE",
        "TTL": 600,
        "UpdatedOn": "2023-03-09 11:51:56",
        "VipAutoRenew": "YES",
        "VipEndAt": "2024-01-16 15:56:31",
        "VipStartAt": "2023-01-16 15:56:31",
        "TagList": [
          {
            "TagKey": "app",
            "TagValue": "redis"
          }
        ]
      }
    ],
    "RequestId": "bfb3f27e-4dba-4a5c-9aff-08d1c27d1c61"
  },
  "timestamp": "2025-01-09T10:30:00Z"
}
```

### 响应字段说明

#### DomainCountInfo（域名统计信息）

| 字段名称      | 类型    | 描述           |
| ------------- | ------- | -------------- |
| AllTotal      | Integer | 总域名数量     |
| DomainTotal   | Integer | 当前页域名数量 |
| ErrorTotal    | Integer | 错误域名数量   |
| GroupTotal    | Integer | 分组域名数量   |
| LockTotal     | Integer | 锁定域名数量   |
| MineTotal     | Integer | 我的域名数量   |
| PauseTotal    | Integer | 暂停域名数量   |
| ShareOutTotal | Integer | 共享给他人数量 |
| ShareTotal    | Integer | 共享域名数量   |
| SpamTotal     | Integer | 垃圾域名数量   |
| VipExpire     | Integer | VIP过期数量    |
| VipTotal      | Integer | VIP域名数量    |

#### DomainList（域名列表）

| 字段名称          | 类型     | 描述                                    |
| ----------------- | -------- | --------------------------------------- |
| CNAMESpeedup      | String   | CNAME加速状态（ENABLE/DISABLE）         |
| CreatedOn         | String   | 域名创建时间                            |
| DNSStatus         | String   | DNS状态                                 |
| DomainId          | Long     | 域名ID                                  |
| EffectiveDNS      | Array    | 有效DNS服务器列表                       |
| Grade             | String   | 域名套餐等级                            |
| GradeLevel        | Integer  | 套餐等级数值                            |
| GradeTitle        | String   | 套餐等级标题                            |
| GroupId           | Integer  | 分组ID                                  |
| IsVip             | String   | 是否为VIP域名（YES/NO）                 |
| Name              | String   | 域名名称                                |
| Owner             | String   | 域名所有者                              |
| Punycode          | String   | 域名Punycode编码                        |
| RecordCount       | Integer  | 解析记录数量                            |
| Remark            | String   | 域名备注                                |
| SearchEnginePush  | String   | 搜索引擎推送状态（YES/NO）              |
| Status            | String   | 域名状态（ENABLE/DISABLE）              |
| TTL               | Integer  | 默认TTL值                               |
| UpdatedOn         | String   | 域名最后更新时间                        |
| VipAutoRenew      | String   | VIP自动续费状态（YES/NO）               |
| VipEndAt          | String   | VIP结束时间                             |
| VipStartAt        | String   | VIP开始时间                             |
| TagList           | Array    | 标签列表，包含TagKey和TagValue字段      |

### 失败响应

#### 认证失败（500）

```json
{
  "code": 500,
  "message": "获取域名列表失败: CAM签名/鉴权错误",
  "data": null,
  "timestamp": "2025-01-09T10:31:00Z"
}
```

#### 参数错误（500）

```json
{
  "code": 500,
  "message": "获取域名列表失败: 参数错误",
  "data": null,
  "timestamp": "2025-01-09T10:31:00Z"
}
```

## 测试用例

### 测试用例1：获取所有域名（GET方式）

**请求：**
```bash
curl -X GET "http://localhost:8080/api/dnspod/domains"
```

**预期响应：**
- HTTP状态码：200
- 响应code：200
- data字段包含DomainCountInfo和DomainList

### 测试用例2：获取所有域名（POST方式，JSON格式）

**请求：**
```bash
curl -X POST "http://localhost:8080/api/dnspod/domains/list" \
  -H "Content-Type: application/json" \
  -d '{}'
```

**预期响应：**
- HTTP状态码：200
- 响应code：200
- data字段包含DomainCountInfo和DomainList

### 测试用例3：分页获取域名（JSON格式）

**请求：**
```bash
curl -X POST "http://localhost:8080/api/dnspod/domains/list" \
  -H "Content-Type: application/json" \
  -d '{
    "offset": 0,
    "limit": 5
  }'
```

**预期响应：**
- HTTP状态码：200
- 响应code：200
- DomainList数组长度不超过5

### 测试用例4：关键字搜索域名（JSON格式）

**请求：**
```bash
curl -X POST "http://localhost:8080/api/dnspod/domains/list" \
  -H "Content-Type: application/json" \
  -d '{
    "keyword": "test",
    "limit": 10
  }'
```

**预期响应：**
- HTTP状态码：200
- 响应code：200
- 返回的域名名称包含"test"关键字

### 测试用例5：认证失败场景

**场景：** 腾讯云API密钥配置错误

**预期响应：**
- HTTP状态码：200
- 响应code：500
- message包含认证错误信息

## 接口格式变更说明

**重要变更**：
1. **双接口支持**：同时支持GET（URL参数）和POST（JSON格式）两种方式
2. **Content-Type**：POST方式使用 `application/json`
3. **向后兼容**：保持GET方式不变，新增POST方式
4. **推荐使用**：新项目建议使用POST+JSON方式，提供更好的可读性和安全性

## 错误码说明

| 错误码 | 描述                           | 解决方案                                   |
| ------ | ------------------------------ | ------------------------------------------ |
| 500    | 获取域名列表失败               | 检查腾讯云API密钥配置和网络连接            |
| 500    | CAM签名/鉴权错误               | 检查SecretId和SecretKey是否正确            |
| 500    | 参数错误                       | 检查请求参数格式和取值范围                 |
| 500    | 操作失败，请稍后再试           | 稍后重试或联系技术支持                     |
| 500    | API请求次数超出限制            | 降低请求频率，等待限制解除                 |

## 注意事项

1. **API频率限制**：默认接口请求频率限制为20次/秒
2. **权限要求**：需要腾讯云账户具有DNSPod域名管理权限
3. **环境变量**：确保正确配置TENCENTCLOUD_SECRET_ID和TENCENTCLOUD_SECRET_KEY
4. **分页处理**：建议使用分页参数避免一次性获取过多数据
5. **关键字搜索**：支持模糊匹配，但搜索结果不能超过500条
6. **缓存建议**：域名列表变化不频繁，建议客户端适当缓存结果

## 相关接口

- [获取域名解析记录](./DNSPod域名解析记录接口.md)
- [添加域名解析记录](./DNSPod添加记录接口.md)
- [修改域名解析记录](./DNSPod修改记录接口.md)
- [删除域名解析记录](./DNSPod删除记录接口.md)