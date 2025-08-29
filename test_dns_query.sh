#!/bin/bash

# DNS记录查询接口测试脚本
# 支持JSON格式传输数据

BASE_URL="http://localhost:8080"

echo "=== DNS记录查询接口测试 ==="

# 1. 用户登录获取JWT token
echo "1. 用户登录获取JWT token..."
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
    echo "❌ 获取JWT token失败，请检查登录接口"
    exit 1
fi

echo "✅ JWT Token获取成功: ${JWT_TOKEN:0:50}..."

echo ""
echo "2. 测试JSON格式查询接口..."

# 2. 查询指定3级域名的所有DNS记录（JSON格式）
echo "📋 查询subdomainId=8的所有DNS记录..."
curl -X POST "${BASE_URL}/api/user/dns-records/query" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "subdomainId": 8
  }' | jq '.' 2>/dev/null || echo "响应格式化失败，原始响应如上"

echo ""
echo "📋 查询所有A记录..."
curl -X POST "${BASE_URL}/api/user/dns-records/query" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "type": "A"
  }' | jq '.' 2>/dev/null || echo "响应格式化失败，原始响应如上"

echo ""
echo "📋 查询指定子域名的A记录（带分页）..."
curl -X POST "${BASE_URL}/api/user/dns-records/query" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "subdomainId": 8,
    "type": "A",
    "offset": 0,
    "limit": 10
  }' | jq '.' 2>/dev/null || echo "响应格式化失败，原始响应如上"

echo ""
echo "📋 查询同步状态为SUCCESS的记录..."
curl -X POST "${BASE_URL}/api/user/dns-records/query" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "syncStatus": "SUCCESS"
  }' | jq '.' 2>/dev/null || echo "响应格式化失败，原始响应如上"

echo ""
echo "=== 测试完成 ==="
echo ""
echo "💡 新接口使用说明："
echo "   接口地址: POST /api/user/dns-records/query"
echo "   Content-Type: application/json"
echo "   支持参数: subdomainId, type, status, syncStatus, offset, limit"
echo ""
echo "💡 原有接口仍然可用："
echo "   接口地址: GET /api/user/dns-records?subdomainId=8"