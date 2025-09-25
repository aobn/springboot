# Cloudflare接口测试脚本

## 测试环境配置

### 基础配置
- **服务器地址**: http://localhost:8001
- **API基础路径**: /api/cloudflare
- **认证方式**: JWT Bearer Token
- **内容类型**: application/json

### 测试前准备

1. **启动Spring Boot应用**
```bash
# 使用项目提供的启动脚本
./start-springboot.sh
```

2. **获取管理员JWT令牌**
```bash
# 管理员登录获取token
curl -X POST "http://localhost:8001/api/admin/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin"
  }'
```

3. **设置环境变量**
```bash
# 将获取到的token设置为环境变量
export ADMIN_TOKEN="eyJhbGciOiJIUzI1NiJ9..."
export BASE_URL="http://localhost:8001"
```

## 测试用例

### 测试用例1: 获取所有Cloudflare域名列表

#### 测试目标
验证管理员用户能够成功获取Cloudflare账户下的所有域名列表

#### 测试脚本
```bash
#!/bin/bash

echo "=== 测试用例1: 获取所有Cloudflare域名列表 ==="

# 发送请求
response=$(curl -s -w "\n%{http_code}" -X GET "${BASE_URL}/api/cloudflare/zones" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -H "Content-Type: application/json")

# 分离响应体和状态码
http_code=$(echo "$response" | tail -n1)
response_body=$(echo "$response" | sed '$d')

echo "HTTP状态码: $http_code"
echo "响应内容: $response_body"

# 验证结果
if [ "$http_code" = "200" ]; then
    echo "✅ 测试通过: 成功获取域名列表"
    
    # 解析响应JSON，检查关键字段
    success=$(echo "$response_body" | jq -r '.data.success // false')
    zone_count=$(echo "$response_body" | jq -r '.data.result | length // 0')
    
    echo "API调用成功: $success"
    echo "域名数量: $zone_count"
    
    if [ "$success" = "true" ] && [ "$zone_count" -gt "0" ]; then
        echo "✅ 数据验证通过: 成功获取到 $zone_count 个域名"
        
        # 显示域名列表
        echo "域名列表:"
        echo "$response_body" | jq -r '.data.result[] | "- \(.name) (ID: \(.id), 状态: \(.status))"'
    else
        echo "❌ 数据验证失败: 未获取到有效域名数据"
    fi
else
    echo "❌ 测试失败: HTTP状态码不是200"
fi

echo ""
```

#### 预期结果
- HTTP状态码: 200
- 响应code: 200
- data.success: true
- data.result: 包含域名数组
- 每个域名包含id、name、status等字段

### 测试用例2: 根据域名名称获取Zone信息

#### 测试脚本
```bash
#!/bin/bash

echo "=== 测试用例2: 根据域名名称获取Zone信息 ==="

# 测试域名（请根据实际情况修改）
ZONE_NAME="000297.xyz"

# 发送请求
response=$(curl -s -w "\n%{http_code}" -X GET "${BASE_URL}/api/cloudflare/zones/${ZONE_NAME}" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -H "Content-Type: application/json")

# 分离响应体和状态码
http_code=$(echo "$response" | tail -n1)
response_body=$(echo "$response" | sed '$d')

echo "HTTP状态码: $http_code"
echo "响应内容: $response_body"

# 验证结果
if [ "$http_code" = "200" ]; then
    echo "✅ 测试通过: 成功获取域名信息"
    
    # 解析响应JSON
    zone_name=$(echo "$response_body" | jq -r '.data.name // ""')
    zone_id=$(echo "$response_body" | jq -r '.data.id // ""')
    zone_status=$(echo "$response_body" | jq -r '.data.status // ""')
    
    echo "域名名称: $zone_name"
    echo "域名ID: $zone_id"
    echo "域名状态: $zone_status"
    
    if [ "$zone_name" = "$ZONE_NAME" ]; then
        echo "✅ 数据验证通过: 域名信息正确"
    else
        echo "❌ 数据验证失败: 域名信息不匹配"
    fi
else
    echo "❌ 测试失败: HTTP状态码不是200"
fi

echo ""
```

### 测试用例3: 测试Cloudflare API连接

#### 测试脚本
```bash
#!/bin/bash

echo "=== 测试用例3: 测试Cloudflare API连接 ==="

# 发送请求
response=$(curl -s -w "\n%{http_code}" -X GET "${BASE_URL}/api/cloudflare/test" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -H "Content-Type: application/json")

# 分离响应体和状态码
http_code=$(echo "$response" | tail -n1)
response_body=$(echo "$response" | sed '$d')

echo "HTTP状态码: $http_code"
echo "响应内容: $response_body"

# 验证结果
if [ "$http_code" = "200" ]; then
    echo "✅ 测试通过: API连接测试成功"
    
    # 解析响应消息
    message=$(echo "$response_body" | jq -r '.message // ""')
    echo "测试结果: $message"
else
    echo "❌ 测试失败: API连接测试失败"
fi

echo ""
```

### 测试用例4: 权限验证测试（普通用户）

#### 测试脚本
```bash
#!/bin/bash

echo "=== 测试用例4: 权限验证测试（普通用户） ==="

# 首先获取普通用户token
echo "获取普通用户token..."
user_response=$(curl -s -X POST "${BASE_URL}/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "allnotice@qq.com",
    "password": "12345678"
  }')

USER_TOKEN=$(echo "$user_response" | jq -r '.data.token // ""')

if [ -z "$USER_TOKEN" ] || [ "$USER_TOKEN" = "null" ]; then
    echo "❌ 无法获取普通用户token，跳过权限测试"
    echo ""
    return
fi

echo "普通用户token获取成功"

# 使用普通用户token访问Cloudflare API
response=$(curl -s -w "\n%{http_code}" -X GET "${BASE_URL}/api/cloudflare/zones" \
  -H "Authorization: Bearer ${USER_TOKEN}" \
  -H "Content-Type: application/json")

# 分离响应体和状态码
http_code=$(echo "$response" | tail -n1)
response_body=$(echo "$response" | sed '$d')

echo "HTTP状态码: $http_code"
echo "响应内容: $response_body"

# 验证结果
if [ "$http_code" = "403" ]; then
    echo "✅ 权限验证通过: 普通用户被正确拒绝访问"
    
    # 检查错误消息
    message=$(echo "$response_body" | jq -r '.message // ""')
    if [[ "$message" == *"管理员权限"* ]]; then
        echo "✅ 错误消息正确: $message"
    else
        echo "❌ 错误消息不正确: $message"
    fi
else
    echo "❌ 权限验证失败: 普通用户不应该能够访问"
fi

echo ""
```

### 测试用例5: 无效JWT令牌测试

#### 测试脚本
```bash
#!/bin/bash

echo "=== 测试用例5: 无效JWT令牌测试 ==="

# 使用无效token
INVALID_TOKEN="invalid_jwt_token_12345"

# 发送请求
response=$(curl -s -w "\n%{http_code}" -X GET "${BASE_URL}/api/cloudflare/zones" \
  -H "Authorization: Bearer ${INVALID_TOKEN}" \
  -H "Content-Type: application/json")

# 分离响应体和状态码
http_code=$(echo "$response" | tail -n1)
response_body=$(echo "$response" | sed '$d')

echo "HTTP状态码: $http_code"
echo "响应内容: $response_body"

# 验证结果
if [ "$http_code" = "401" ]; then
    echo "✅ JWT验证通过: 无效token被正确拒绝"
else
    echo "❌ JWT验证失败: 无效token不应该被接受"
fi

echo ""
```

## 完整测试脚本

### 一键执行所有测试
```bash
#!/bin/bash

# Cloudflare接口完整测试脚本
# 作者: CodeBuddy
# 日期: 2025-09-25

echo "=========================================="
echo "Cloudflare接口测试开始"
echo "时间: $(date)"
echo "=========================================="

# 配置
BASE_URL="http://localhost:8001"
ADMIN_USERNAME="admin"
ADMIN_PASSWORD="admin"
USER_EMAIL="allnotice@qq.com"
USER_PASSWORD="12345678"
TEST_ZONE_NAME="000297.xyz"

# 获取管理员token
echo "步骤1: 获取管理员JWT令牌..."
admin_response=$(curl -s -X POST "${BASE_URL}/api/admin/login" \
  -H "Content-Type: application/json" \
  -d "{
    \"username\": \"${ADMIN_USERNAME}\",
    \"password\": \"${ADMIN_PASSWORD}\"
  }")

ADMIN_TOKEN=$(echo "$admin_response" | jq -r '.data.token // ""')

if [ -z "$ADMIN_TOKEN" ] || [ "$ADMIN_TOKEN" = "null" ]; then
    echo "❌ 无法获取管理员token，测试终止"
    echo "响应: $admin_response"
    exit 1
fi

echo "✅ 管理员token获取成功"
echo ""

# 执行所有测试用例
source ./test_case_1.sh
source ./test_case_2.sh
source ./test_case_3.sh
source ./test_case_4.sh
source ./test_case_5.sh

echo "=========================================="
echo "Cloudflare接口测试完成"
echo "时间: $(date)"
echo "=========================================="
```

## 测试结果验证

### 成功标准
1. **测试用例1**: HTTP 200，获取到域名列表
2. **测试用例2**: HTTP 200，获取到指定域名信息
3. **测试用例3**: HTTP 200，API连接测试成功
4. **测试用例4**: HTTP 403，普通用户被拒绝访问
5. **测试用例5**: HTTP 401，无效token被拒绝

### 常见问题排查

#### 问题1: Cloudflare API调用失败
- 检查API Key和Email配置是否正确
- 确认网络连接正常
- 查看应用日志获取详细错误信息

#### 问题2: JWT令牌无效
- 确认管理员账户存在且密码正确
- 检查JWT配置是否正确
- 确认令牌未过期

#### 问题3: 权限验证失败
- 确认用户角色设置正确
- 检查权限验证逻辑
- 查看详细错误日志

## 性能测试

### 并发测试脚本
```bash
#!/bin/bash

echo "=== Cloudflare接口并发测试 ==="

# 并发数量
CONCURRENT_REQUESTS=10

# 创建临时文件存储结果
TEMP_DIR="/tmp/cloudflare_test_$$"
mkdir -p "$TEMP_DIR"

# 并发执行请求
for i in $(seq 1 $CONCURRENT_REQUESTS); do
    {
        response=$(curl -s -w "%{http_code}" -X GET "${BASE_URL}/api/cloudflare/zones" \
          -H "Authorization: Bearer ${ADMIN_TOKEN}" \
          -H "Content-Type: application/json")
        
        echo "请求$i: $response" > "$TEMP_DIR/result_$i.txt"
    } &
done

# 等待所有请求完成
wait

# 统计结果
success_count=0
total_count=$CONCURRENT_REQUESTS

for i in $(seq 1 $CONCURRENT_REQUESTS); do
    if grep -q "200$" "$TEMP_DIR/result_$i.txt"; then
        ((success_count++))
    fi
done

echo "并发测试结果:"
echo "总请求数: $total_count"
echo "成功请求数: $success_count"
echo "成功率: $(echo "scale=2; $success_count * 100 / $total_count" | bc)%"

# 清理临时文件
rm -rf "$TEMP_DIR"
```

## 注意事项

1. **API配额限制**: Cloudflare API有调用频率限制，测试时请注意间隔
2. **网络环境**: 确保测试环境能够访问Cloudflare API
3. **数据安全**: 测试脚本中包含敏感信息，请妥善保管
4. **环境依赖**: 测试脚本依赖jq工具解析JSON，请确保已安装
5. **错误处理**: 如遇到测试失败，请查看应用日志获取详细信息