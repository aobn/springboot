# Cloudflare获取所有域名接口

## 接口基本信息

- **接口标识**: `CLOUDFLARE_GET_ALL_ZONES`
- **请求路径**: `GET /api/cloudflare/zones`
- **接口描述**: 获取Cloudflare账户下的所有域名(Zone)列表
- **认证要求**: 需要管理员权限
- **适用业务单元**: Cloudflare域名管理

## 请求参数

### 请求头
```http
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

### 请求参数
无需请求参数

## 响应示例

### 成功响应（状态码 200）

```json
{
  "code": 200,
  "message": "成功获取Cloudflare域名列表",
  "data": {
    "result": [
      {
        "id": "bd9427cabe2a367df8875f0f726a3e2a",
        "name": "000297.xyz",
        "status": "active",
        "paused": false,
        "type": "full",
        "development_mode": 0,
        "name_servers": [
          "alina.ns.cloudflare.com",
          "carlos.ns.cloudflare.com"
        ],
        "original_name_servers": [
          "launch2.spaceship.net",
          "launch1.spaceship.net"
        ],
        "original_registrar": null,
        "original_dnshost": null,
        "modified_on": "2025-08-07T15:01:18.308896Z",
        "created_on": "2025-08-07T14:35:46.809045Z",
        "activated_on": "2025-08-07T15:01:18.308896Z",
        "vanity_name_servers": [],
        "vanity_name_servers_ips": null,
        "meta": {
          "step": 2,
          "custom_certificate_quota": 0,
          "page_rule_quota": 3,
          "phishing_detected": false
        },
        "owner": {
          "id": null,
          "type": "user",
          "email": null
        },
        "account": {
          "id": "3025192dbe43c46336fb61944e51f413",
          "name": "Xianhuawork@163.com's Account"
        },
        "plan": {
          "id": "0feeeeeeeeeeeeeeeeeeeeeeeeeeeeee",
          "name": "Free Website",
          "price": 0,
          "currency": "USD",
          "frequency": "",
          "is_subscribed": false,
          "can_subscribe": false,
          "legacy_id": "free",
          "legacy_discount": false,
          "externally_managed": false
        }
      }
    ],
    "result_info": {
      "page": 1,
      "per_page": 20,
      "total_pages": 1,
      "count": 5,
      "total_count": 5
    },
    "success": true,
    "errors": [],
    "messages": []
  },
  "timestamp": "2025-09-25T21:00:00Z"
}
```

### 失败响应

#### 权限不足（403）
```json
{
  "code": 403,
  "message": "需要管理员权限才能访问Cloudflare API",
  "data": null,
  "timestamp": "2025-09-25T21:00:00Z"
}
```

#### 未授权（401）
```json
{
  "code": 401,
  "message": "JWT令牌无效或已过期",
  "data": null,
  "timestamp": "2025-09-25T21:00:00Z"
}
```

#### Cloudflare API调用失败（500）
```json
{
  "code": 500,
  "message": "Cloudflare API调用失败: Invalid API key",
  "data": null,
  "timestamp": "2025-09-25T21:00:00Z"
}
```

## 响应字段说明

### Zone对象字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | Cloudflare Zone ID |
| name | String | 域名名称 |
| status | String | 域名状态(active/pending/initializing/moved/deleted/deactivated) |
| paused | Boolean | 是否暂停 |
| type | String | Zone类型(full/partial) |
| development_mode | Integer | 开发模式剩余时间(秒) |
| name_servers | Array | Cloudflare名称服务器列表 |
| original_name_servers | Array | 原始名称服务器列表 |
| created_on | String | 创建时间(ISO 8601格式) |
| modified_on | String | 修改时间(ISO 8601格式) |
| activated_on | String | 激活时间(ISO 8601格式) |

### Meta对象字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| step | Integer | 设置步骤 |
| custom_certificate_quota | Integer | 自定义证书配额 |
| page_rule_quota | Integer | 页面规则配额 |
| phishing_detected | Boolean | 是否检测到钓鱼 |

### Account对象字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 账户ID |
| name | String | 账户名称 |

### Plan对象字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 计划ID |
| name | String | 计划名称 |
| price | Double | 价格 |
| currency | String | 货币 |
| is_subscribed | Boolean | 是否已订阅 |
| can_subscribe | Boolean | 是否可订阅 |

### ResultInfo对象字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| page | Integer | 当前页码 |
| per_page | Integer | 每页数量 |
| total_pages | Integer | 总页数 |
| count | Integer | 当前页数量 |
| total_count | Integer | 总数量 |

## 业务流程

1. **用户身份验证**: 验证JWT令牌有效性
2. **权限验证**: 检查用户是否具有管理员权限
3. **调用Cloudflare API**: 使用配置的API Key和Email调用Cloudflare API
4. **响应处理**: 解析API响应并返回标准格式
5. **错误处理**: 处理各种异常情况并返回相应错误信息

## 调试说明

### 测试用例

#### 测试用例1：成功获取域名列表
```bash
curl -X GET "http://localhost:8001/api/cloudflare/zones" \
  -H "Authorization: Bearer <ADMIN_JWT_TOKEN>" \
  -H "Content-Type: application/json"
```

**预期响应**: 
- code=200
- data包含域名列表信息
- success=true

#### 测试用例2：非管理员用户访问
```bash
curl -X GET "http://localhost:8001/api/cloudflare/zones" \
  -H "Authorization: Bearer <USER_JWT_TOKEN>" \
  -H "Content-Type: application/json"
```

**预期响应**: 
- code=403
- message="需要管理员权限才能访问Cloudflare API"

#### 测试用例3：无效JWT令牌
```bash
curl -X GET "http://localhost:8001/api/cloudflare/zones" \
  -H "Authorization: Bearer invalid_token" \
  -H "Content-Type: application/json"
```

**预期响应**: 
- code=401
- message="JWT令牌无效或已过期"

## 注意事项

1. **权限要求**: 只有管理员用户才能调用此接口
2. **API配额**: Cloudflare API有调用频率限制，请合理使用
3. **安全性**: API Key和Email已在配置文件中设置，请确保配置文件安全
4. **错误处理**: 接口会处理网络异常、API异常等各种情况
5. **日志记录**: 所有API调用都会记录详细日志便于调试

## 相关接口

- `GET /api/cloudflare/zones/{zoneName}` - 根据域名名称获取Zone信息
- `GET /api/cloudflare/test` - 测试Cloudflare API连接

## 更新记录

- 2025-09-25: 初始版本，实现基础的域名列表获取功能