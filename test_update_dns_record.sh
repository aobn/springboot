#!/bin/bash

# DNS记录修改接口测试脚本

BASE_URL="http://localhost:8080"

echo "=== DNS记录修改接口测试 ==="

# 1. 检查服务器状态
echo "1. 检查服务器状态..."
SERVER_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health)
if [ "$SERVER_STATUS" != "200" ]; then
    echo "❌ 服务器未运行或不可访问 (HTTP $SERVER_STATUS)"
    exit 1
fi
echo "✅ 服务器运行正常"

# 2. 用户登录获取JWT token
echo ""
echo "2. 用户登录获取JWT token..."
LOGIN_RESPONSE=$(curl -s -X POST "${BASE_URL}/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "123456"
  }')

echo "登录响应: $LOGIN_RESPONSE"

# 提取JWT token（这里需要根据实际响应格式调整）
JWT_TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$JWT_TOKEN" ]; then
    echo "❌ 获取JWT token失败，使用测试token"
    JWT_TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsImVtYWlsIjoiMTIzNDU2NzhAZXhhbXBsZS5jb20iLCJzdWIiOiIxIiwiaWF0IjoxNzU2NDY5OTAxLCJleHAiOjE3NTY1NTYzMDF9.B4mOR1U46MuqUpuiKq93haNwJwuSMAPG3j99yLhzp9U"
fi

echo "✅ JWT Token: ${JWT_TOKEN:0:50}..."

# 3. 查询现有DNS记录
echo ""
echo "3. 查询现有DNS记录..."
echo "📋 获取用户所有DNS记录："
RECORDS_RESPONSE=$(curl -s -X GET "${BASE_URL}/api/user/dns-records" \
  -H "Authorization: Bearer $JWT_TOKEN")

echo "现有记录: $RECORDS_RESPONSE"

# 4. 测试修改DNS记录接口
echo ""
echo "4. 测试修改DNS记录接口..."

# 测试1：修改记录ID=1的A记录
echo "🔧 修改recordId=1的DNS记录（A记录）："
UPDATE_RESPONSE_1=$(curl -s -X PUT "${BASE_URL}/api/user/dns-records/1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "type": "A",
    "value": "192.168.1.100",
    "ttl": 300,
    "remark": "测试修改A记录"
  }')

echo "响应: $UPDATE_RESPONSE_1"

# 测试2：修改为CNAME记录
echo ""
echo "🔧 修改recordId=1为CNAME记录："
UPDATE_RESPONSE_2=$(curl -s -X PUT "${BASE_URL}/api/user/dns-records/1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "type": "CNAME",
    "value": "example.com",
    "ttl": 600,
    "remark": "测试修改CNAME记录"
  }')

echo "响应: $UPDATE_RESPONSE_2"

# 测试3：修改MX记录
echo ""
echo "🔧 修改recordId=1为MX记录："
UPDATE_RESPONSE_3=$(curl -s -X PUT "${BASE_URL}/api/user/dns-records/1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "type": "MX",
    "value": "mail.example.com",
    "ttl": 3600,
    "mx": 10,
    "remark": "测试修改MX记录"
  }')

echo "响应: $UPDATE_RESPONSE_3"

# 测试4：修改不存在的记录
echo ""
echo "🔧 修改不存在的记录（recordId=999）："
NOT_FOUND_RESPONSE=$(curl -s -X PUT "${BASE_URL}/api/user/dns-records/999" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "type": "A",
    "value": "192.168.1.1",
    "ttl": 600
  }')

echo "响应: $NOT_FOUND_RESPONSE"

# 测试5：使用无效token
echo ""
echo "🔧 使用无效token修改记录："
INVALID_TOKEN_RESPONSE=$(curl -s -X PUT "${BASE_URL}/api/user/dns-records/1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer invalid_token" \
  -d '{
    "type": "A",
    "value": "192.168.1.1",
    "ttl": 600
  }')

echo "响应: $INVALID_TOKEN_RESPONSE"

# 测试6：参数验证测试
echo ""
echo "🔧 测试参数验证（无效TTL值）："
VALIDATION_RESPONSE=$(curl -s -X PUT "${BASE_URL}/api/user/dns-records/1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "type": "A",
    "value": "192.168.1.1",
    "ttl": -1
  }')

echo "响应: $VALIDATION_RESPONSE"

# 7. 查询修改后的记录
echo ""
echo "7. 查询修改后的记录..."
echo "📋 获取修改后的DNS记录："
FINAL_RECORDS=$(curl -s -X GET "${BASE_URL}/api/user/dns-records" \
  -H "Authorization: Bearer $JWT_TOKEN")

echo "修改后记录: $FINAL_RECORDS"

echo ""
echo "=== 测试完成 ==="
echo ""
echo "💡 接口说明："
echo "   接口地址: PUT /api/user/dns-records/{id}"
echo "   Content-Type: application/json"
echo "   支持参数: type, value, ttl, mx, weight, status, remark"
echo "   权限验证: 用户只能修改自己的DNS记录"