# Cloudflare获取所有域名接口

## 接口基本信息

- **接口标识**: `CLOUDFLARE_GET_ALL_ZONES`
- **请求路径**: `GET /api/cloudflare/zones`
- **接口描述**: 获取本地数据库中存储的所有Cloudflare域名列表（包含新增的统计字段，不包含复杂的meta、owner、tenant、permissions、plan等字段）
- **认证要求**: 无需认证（公开接口）
- **适用业务单元**: Cloudflare域名管理

## 请求参数

### 请求头
```http
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
  "data": [
    {
      "zone_id": "bd9427cabe2a367df8875f0f726a3e2a",
      "name": "f.b.a.2.8.f.0.7.4.0.1.0.0.2.ip6.arpa",
      "status": "active",
      "paused": false,
      "type": "full",
      "name_servers": [
        "alina.ns.cloudflare.com",
        "carlos.ns.cloudflare.com"
      ],
      "created_on": "2025-08-07 14:35:46",
      "modified_on": "2025-08-07 15:01:18",
      "dns_record_count": 15,
      "dns_record_limit": 200,
      "user_count": 3,
      "user_limit": 100,
      "is_full": false,
      "auto_assign_enabled": true,
      "next_prefix_hex": "0.4",
      "is_active": true,
      "remark": "IPv6反向解析域名",
      "create_time": "2025-09-25 10:30:00",
      "update_time": "2025-09-26 15:20:30"
    },
    {
      "zone_id": "cd8537dabe3b468ef9986g1g837b4f3b",
      "name": "f.c.a.2.8.f.0.7.4.0.1.0.0.2.ip6.arpa",
      "status": "active",
      "paused": false,
      "type": "full",
      "name_servers": [
        "alina.ns.cloudflare.com",
        "carlos.ns.cloudflare.com"
      ],
      "created_on": "2025-08-08 09:15:30",
      "modified_on": "2025-08-08 09:45:12",
      "dns_record_count": 8,
      "dns_record_limit": 200,
      "user_count": 2,
      "user_limit": 100,
      "is_full": false,
      "auto_assign_enabled": true,
      "next_prefix_hex": "0.3",
      "is_active": true,
      "remark": "IPv6反向解析域名",
      "create_time": "2025-09-25 11:00:00",
      "update_time": "2025-09-26 16:10:15"
    }
  ],
  "timestamp": "2025-09-26T16:00:00Z"
}
```

### 失败响应

#### 数据库查询失败（500）
```json
{
  "code": 500,
  "message": "从数据库获取域名列表失败: Connection timeout",
  "data": null,
  "timestamp": "2025-09-26T16:00:00Z"
}
```

## 响应字段说明

### 简化域名对象字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| zone_id | String | Cloudflare Zone ID |
| name | String | 域名名称 |
| status | String | 域名状态(active/pending/initializing/moved/deleted/deactivated) |
| paused | Boolean | 是否暂停 |
| type | String | Zone类型(full/partial) |
| name_servers | Array | Cloudflare名称服务器列表 |
| created_on | String | Cloudflare创建时间(yyyy-MM-dd HH:mm:ss格式) |
| modified_on | String | Cloudflare修改时间(yyyy-MM-dd HH:mm:ss格式) |
| dns_record_count | Integer | DNS记录总数量 |
| dns_record_limit | Integer | DNS记录数量限制 |
| user_count | Integer | 已注册用户数量 |
| user_limit | Integer | 用户数量限制 |
| is_full | Boolean | 是否已满(用户数量达到限制) |
| auto_assign_enabled | Boolean | 是否启用自动分配前缀 |
| next_prefix_hex | String | 下一个可分配的十六进制前缀 |
| is_active | Boolean | 本地启用状态 |
| remark | String | 备注信息 |
| create_time | String | 本地创建时间(yyyy-MM-dd HH:mm:ss格式) |
| update_time | String | 本地更新时间(yyyy-MM-dd HH:mm:ss格式) |

## 业务流程

1. **数据库查询**: 从本地数据库查询所有活跃的Cloudflare域名信息
2. **数据转换**: 将数据库实体转换为简化的响应DTO格式
3. **响应处理**: 返回包含新增统计字段的域名列表
4. **错误处理**: 处理数据库异常等情况并返回相应错误信息

## 调试说明

### 测试用例

#### 测试用例1：成功获取域名列表
```bash
curl -X GET "http://localhost:8001/api/cloudflare/zones" \
  -H "Content-Type: application/json"
```

**预期响应**: 
- code=200
- data包含域名列表信息
- success=true

#### 测试用例2：API调用失败
```bash
curl -X GET "http://localhost:8001/api/cloudflare/zones" \
  -H "Content-Type: application/json"
```

**预期响应**（当Cloudflare API配置错误时）: 
- code=500
- message="调用Cloudflare API失败: Invalid API key"

## 注意事项

1. **公开接口**: 此接口无需认证，任何人都可以访问
2. **API配额**: Cloudflare API有调用频率限制，请合理使用
3. **安全性**: API Key和Email已在配置文件中设置，请确保配置文件安全
4. **错误处理**: 接口会处理网络异常、API异常等各种情况
5. **日志记录**: 所有API调用都会记录详细日志便于调试

## 相关接口

- `GET /api/cloudflare/zones/{zoneName}` - 根据域名名称获取Zone信息
- `GET /api/cloudflare/test` - 测试Cloudflare API连接

## 更新记录

- 2025-09-25: 初始版本，实现基础的域名列表获取功能
- 2025-09-26: 更新为公开接口，移除认证要求
- 2025-09-26: 更新响应格式，返回简化的域名信息，包含新增的统计字段