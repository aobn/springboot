#!/bin/bash

# DNS记录删除接口测试脚本

BASE_URL="http://localhost:8080"
JWT_TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsImVtYWlsIjoiMTIzNDU2NzhAZXhhbXBsZS5jb20iLCJzdWIiOiIxIiwiaWF0IjoxNzU2NDY5OTAxLCJleHAiOjE3NTY1NTYzMDF9.B4mOR1U46MuqUpuiKq93haNwJwuSMAPG3j99yLhzp9U"

echo "=== DNS记录删除接口测试 ==="

# 1. 检查服务器状态
echo "1. 检查服务器状态..."
SERVER_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health)
if [ "$SERVER_STATUS" != "200" ]; then
    echo "❌ 服务器未运行或不可访问 (HTTP $SERVER_STATUS)"
    exit 1
fi
echo "✅ 服务器运行正常"

# 2. 查询现有DNS记录
echo ""
echo "2. 查询现有DNS记录..."
echo "📋 获取用户所有DNS记录："
curl -s -X GET "${BASE_URL}/api/user/dns-records" \
  -H "Authorization: Bearer $JWT_TOKEN" | jq '.' 2>/dev/null || echo "查询失败或响应格式错误"

# 3. 测试POST方式删除接口（按照接口文档）
echo ""
echo "3. 测试POST方式删除接口..."
echo "🗑️ 删除recordId=1的DNS记录（POST方式）："
POST_RESPONSE=$(curl -s -X POST "${BASE_URL}/api/user/dns-records/delete" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "recordId": 1
  }')

echo "响应: $POST_RESPONSE"

# 4. 测试DELETE方式删除接口
echo ""
echo "4. 测试DELETE方式删除接口..."
echo "🗑️ 删除recordId=1的DNS记录（DELETE方式）："
DELETE_RESPONSE=$(curl -s -X DELETE "${BASE_URL}/api/user/dns-records/1" \
  -H "Authorization: Bearer $JWT_TOKEN")

echo "响应: $DELETE_RESPONSE"

# 5. 测试不存在的记录
echo ""
echo "5. 测试删除不存在的记录..."
echo "🗑️ 删除recordId=999的DNS记录（应该返回404）："
NOT_FOUND_RESPONSE=$(curl -s -X POST "${BASE_URL}/api/user/dns-records/delete" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "recordId": 999
  }')

echo "响应: $NOT_FOUND_RESPONSE"

# 6. 测试无效token
echo ""
echo "6. 测试无效token..."
echo "🗑️ 使用无效token删除记录（应该返回401）："
INVALID_TOKEN_RESPONSE=$(curl -s -X POST "${BASE_URL}/api/user/dns-records/delete" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer invalid_token" \
  -d '{
    "recordId": 1
  }')

echo "响应: $INVALID_TOKEN_RESPONSE"

echo ""
echo "=== 测试完成 ==="
echo ""
echo "💡 接口说明："
echo "   POST方式: POST /api/user/dns-records/delete (JSON格式)"
echo "   DELETE方式: DELETE /api/user/dns-records/{id} (路径参数)"
echo "   两种方式都支持，可以根据需要选择使用"