# 用户获取所有Cloudflare域名接口文档

## 接口概述
- **接口名称**: 用户获取所有Cloudflare域名
- **接口路径**: `/api/user/cloudflare/zones/all`
- **请求方法**: `GET`
- **接口描述**: 从数据库获取所有可用的Cloudflare域名列表
- **认证要求**: 无需认证，公开接口

## 请求参数

### Headers
无需特殊Headers

### 请求参数
无需请求参数

## 响应格式

### 成功响应 (200)
```json
{
  "success": true,
  "message": "获取所有域名信息成功",
  "data": [
    {
      "zone_id": "023e105f4ecef8ad9ca31a8372d0c353",
      "name": "example.com",
      "status": "active",
      "paused": false,
      "type": "full",
      "name_servers": [
        "ns1.cloudflare.com",
        "ns2.cloudflare.com"
      ],
      "created_on": "2023-01-15 10:30:00",
      "modified_on": "2023-09-26 14:20:00",
      "dns_record_count": 15,
      "dns_record_limit": 200,
      "user_count": 3,
      "user_limit": 100,
      "is_full": false,
      "auto_assign_enabled": true,
      "next_prefix_hex": "0.1",
      "is_active": true,
      "remark": "主域名",
      "create_time": "2023-01-15 10:30:00",
      "update_time": "2023-09-26 14:20:00"
    },
    {
      "zone_id": "456e789f0ecef8ad9ca31a8372d0c789",
      "name": "test.com",
      "status": "active",
      "paused": false,
      "type": "full",
      "name_servers": [
        "ns3.cloudflare.com",
        "ns4.cloudflare.com"
      ],
      "created_on": "2023-02-20 15:45:00",
      "modified_on": "2023-09-25 09:10:00",
      "dns_record_count": 8,
      "dns_record_limit": 200,
      "user_count": 1,
      "user_limit": 100,
      "is_full": false,
      "auto_assign_enabled": true,
      "next_prefix_hex": "0.2",
      "is_active": true,
      "remark": "测试域名",
      "create_time": "2023-02-20 15:45:00",
      "update_time": "2023-09-25 09:10:00"
    }
  ],
  "timestamp": "2025-09-29T17:04:49"
}
```

### 错误响应

#### 500 服务器错误
```json
{
  "success": false,
  "message": "系统错误，请稍后重试",
  "data": null,
  "timestamp": "2025-09-29T17:04:49"
}
```

## 响应字段说明

| 字段名 | 类型 | 描述 |
|--------|------|------|
| zone_id | String | Cloudflare Zone ID |
| name | String | 域名名称 |
| status | String | 域名状态 (active/pending/initializing等) |
| paused | Boolean | 是否暂停 |
| type | String | Zone类型 (full/partial) |
| name_servers | Array | 名称服务器列表 |
| created_on | String | Cloudflare创建时间 |
| modified_on | String | Cloudflare修改时间 |
| dns_record_count | Integer | DNS记录总数量 |
| dns_record_limit | Integer | DNS记录数量限制 |
| user_count | Integer | 已注册用户数量 |
| user_limit | Integer | 用户数量限制 |
| is_full | Boolean | 是否已满 |
| auto_assign_enabled | Boolean | 是否启用自动分配前缀 |
| next_prefix_hex | String | 下一个可分配的十六进制前缀 |
| is_active | Boolean | 本地启用状态 |
| remark | String | 备注信息 |
| create_time | String | 本地创建时间 |
| update_time | String | 本地更新时间 |

## 使用示例

### cURL 请求示例
```bash
curl -X GET "http://localhost:8001/api/user/cloudflare/zones/all"
```

### JavaScript 请求示例
```javascript
const response = await fetch('/api/user/cloudflare/zones/all', {
  method: 'GET',
  headers: {
    'Content-Type': 'application/json'
  }
});

const result = await response.json();
if (result.success) {
  console.log('域名列表:', result.data);
} else {
  console.error('获取失败:', result.message);
}
```

## 注意事项

1. **认证要求**: 无需认证，公开接口
2. **数据来源**: 数据来自本地数据库，不是实时从Cloudflare API获取
3. **权限控制**: 所有用户都可以查看域名列表
4. **数据过滤**: 只返回状态为活跃(is_active=true)的域名
5. **性能考虑**: 数据从数据库缓存中获取，响应速度较快

## 错误码说明

| 错误码 | 描述 | 解决方案 |
|--------|------|----------|
| 500 | 数据库连接失败或其他系统错误 | 检查系统状态，稍后重试 |

## 更新日志

- **v1.0.0** (2025-09-29): 初始版本，支持获取所有Cloudflare域名信息