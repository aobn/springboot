## 这是cloudflare的更新记录接口请求真实示例

curl --request PATCH \
  --url 'https://api.cloudflare.com/client/v4/zones/$ZONE_ID/dns_records/$DNS_RECORD_ID' \
  --header 'Accept: */*' \
  --header 'Accept-Encoding: gzip, deflate, br' \
  --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsImVtYWlsIjoiYWxsbm90aWNlQHFxLmNvbSIsInN1YiI6IjIiLCJpYXQiOjE3NTg5NjQyMDEsImV4cCI6MTc1OTA1MDYwMX0.YHawvZK7mmPkVLc1FjgUADShYCQzeF4bAGNJE_7v6XY' \
  --header 'Connection: keep-alive' \
  --header 'Content-Type: application/json' \
  --header 'Cookie: __cflb=0H28vgHxwvgAQtjUGUFqYFDiSDreGJnUz3xCdayHXuf;__cf_bm=bPCU1Y8rYKAmWU7gKl7.sA16_e.26bfCWWRunL.r8ms-1758976180-1.0.1.1-6CYnZGt7i19LfGZfJ_1k5DaQvj.dk1GP2A.O_mggsYKufKglQBVQqEZ9sfA22erEi6X5kc30TjUZ91_lbFvqTM9GfrNiJit1RHc5qvM_gig;_cfuvid=RjX3aoYVsJWzbSFKwUWjP8w_EY.4oevgap.b9Mft0NA-1758976180671-0.0.1.1-604800000' \
  --header 'User-Agent: PostmanRuntime-ApipostRuntime/1.1.0' \
  --header 'X-Auth-Email: $CLOUDFLARE_EMAIL' \
  --header 'X-Auth-Key: $CLOUDFLARE_API_KEY' \
  --data '{
          "name": "example.com",
          "ttl": 3600,
          "type": "A",
          "comment": "Domain verification record",
          "content": "198.51.100.4",
          "proxied": true
        }'

其中 name是数据表cloudflare_dns_record中的name字段
$ZONE_ID是数据表cloudflare_dns_record中的zone_id字段
$DNS_RECORD_ID是数据表cloudflare_dns_record中的record_id字段

## 这是真实响应示例
{
	"result": {
		"id": "708f3ce274d2dc61cbb3b6356b8567ee",
		"name": "0.1.f.c.a.2.8.f.0.7.4.0.1.0.0.2.ip6.arpa",
		"type": "NS",
		"content": "dddddd.com",
		"proxiable": false,
		"proxied": false,
		"ttl": 1,
		"settings": {},
		"meta": {},
		"comment": "Domain verification record",
		"tags": [],
		"created_on": "2025-09-27T11:16:28.806852Z",
		"modified_on": "2025-09-27T12:38:27.293649Z",
		"comment_modified_on": "2025-09-27T12:38:27.293649Z"
	},
	"success": true,
	"errors": [],
	"messages": []
}