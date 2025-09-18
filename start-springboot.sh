#!/bin/bash

# 输出彩色文本的函数
print_green() {
    echo -e "\033[0;32m$1\033[0m"
}

print_yellow() {
    echo -e "\033[0;33m$1\033[0m"
}

print_red() {
    echo -e "\033[0;31m$1\033[0m"
}

# 显示脚本开始执行的信息
print_green "===== Spring Boot 应用启动脚本 ====="
print_yellow "$(date '+%Y-%m-%d %H:%M:%S') 开始执行..."

# 检查并杀掉占用8001端口的进程
print_yellow "正在检查8001端口占用情况..."

# 使用lsof命令查找占用8001端口的进程
if command -v lsof > /dev/null 2>&1; then
    PORT_PID=$(lsof -i:8001 -sTCP:LISTEN -t)
    if [ -n "$PORT_PID" ]; then
        print_red "发现占用8001端口的进程: $PORT_PID，正在终止..."
        kill -9 $PORT_PID
        print_green "进程已终止"
    else
        print_green "8001端口未被占用"
    fi
# 如果没有lsof命令，尝试使用fuser命令
elif command -v fuser > /dev/null 2>&1; then
    print_yellow "使用fuser检查端口..."
    fuser -k 8001/tcp 2>/dev/null || print_green "8001端口未被占用"
# 如果两个命令都不可用，则使用netstat
elif command -v netstat > /dev/null 2>&1; then
    print_yellow "使用netstat检查端口..."
    PORT_PID=$(netstat -tulpn 2>/dev/null | grep ":8001" | awk '{print $7}' | cut -d'/' -f1)
    if [ -n "$PORT_PID" ] && [ "$PORT_PID" != "" ]; then
        print_red "发现占用8001端口的进程: $PORT_PID，正在终止..."
        kill -9 $PORT_PID
        print_green "进程已终止"
    else
        print_green "8001端口未被占用"
    fi
else
    print_red "警告: 无法检查端口占用，lsof、fuser和netstat命令均不可用"
fi

# 等待端口完全释放
sleep 1

# 启动Spring Boot应用
print_yellow "正在启动Spring Boot应用..."
cd "$(dirname "$0")" || exit 1

# 检查是否存在Maven包装器
if [ -f "./mvnw" ]; then
    print_green "使用Maven包装器启动应用..."
    ./mvnw spring-boot:run
# 否则使用系统安装的Maven
elif command -v mvn > /dev/null 2>&1; then
    print_green "使用Maven启动应用..."
    mvn spring-boot:run
# 如果没有Maven，检查Gradle
elif [ -f "./gradlew" ]; then
    print_green "使用Gradle包装器启动应用..."
    ./gradlew bootRun
# 否则使用系统安装的Gradle
elif command -v gradle > /dev/null 2>&1; then
    print_green "使用Gradle启动应用..."
    gradle bootRun
else
    print_red "错误: 无法找到Maven或Gradle，无法启动应用"
    exit 1
fi