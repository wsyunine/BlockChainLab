#!/bin/bash
# ERC20Test 合约 API 测试脚本
# 使用方法: ./test-erc20-api.sh

BASE_URL="http://localhost:8080/api/erc20"

echo "=========================================="
echo "ERC20Test 合约 API 测试"
echo "=========================================="
echo ""

# 颜色定义
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 1. 部署合约
echo -e "${BLUE}1. 部署 ERC20Test 合约...${NC}"
DEPLOY_RESPONSE=$(curl -s -X POST "${BASE_URL}/deploy")
echo "$DEPLOY_RESPONSE" | python3 -m json.tool
CONTRACT_ADDRESS=$(echo "$DEPLOY_RESPONSE" | python3 -c "import sys, json; print(json.load(sys.stdin).get('contractAddress', ''))")
echo -e "${GREEN}合约地址: ${CONTRACT_ADDRESS}${NC}"
echo ""

if [ -z "$CONTRACT_ADDRESS" ]; then
    echo -e "${YELLOW}警告: 未获取到合约地址，请检查部署是否成功${NC}"
    exit 1
fi

# 2. 加载合约
echo -e "${BLUE}2. 加载合约...${NC}"
curl -s -X POST "${BASE_URL}/load?address=${CONTRACT_ADDRESS}" | python3 -m json.tool
echo ""

# 3. 获取合约地址
echo -e "${BLUE}3. 获取当前合约地址...${NC}"
curl -s "${BASE_URL}/address" | python3 -m json.tool
echo ""

# 4. 查询初始余额（使用配置中的私钥对应的地址）
echo -e "${BLUE}4. 查询初始余额...${NC}"
# 注意：这里需要替换为实际的账户地址
ACCOUNT_ADDRESS="0x3921618F3eaA5e8306382e965742722DDE0535Df"  # 请替换为你的账户地址
echo "查询地址: ${ACCOUNT_ADDRESS}"
curl -s "${BASE_URL}/balance?account=${ACCOUNT_ADDRESS}" | python3 -m json.tool
echo ""

# 5. 铸造代币
echo -e "${BLUE}5. 铸造 1000 个代币...${NC}"
MINT_VALUE="1000000000000000000000"  # 1000 * 10^18
MINT_RESPONSE=$(curl -s -X POST "${BASE_URL}/mint?to=${ACCOUNT_ADDRESS}&value=${MINT_VALUE}")
echo "$MINT_RESPONSE" | python3 -m json.tool
TX_HASH=$(echo "$MINT_RESPONSE" | python3 -c "import sys, json; print(json.load(sys.stdin).get('transactionHash', ''))")
echo -e "${GREEN}交易哈希: ${TX_HASH}${NC}"
echo "等待交易确认..."
sleep 3
echo ""

# 6. 再次查询余额（验证铸造）
echo -e "${BLUE}6. 查询余额（验证铸造）...${NC}"
curl -s "${BASE_URL}/balance?account=${ACCOUNT_ADDRESS}" | python3 -m json.tool
echo ""

# 7. 转账（需要另一个地址）
echo -e "${BLUE}7. 转账 100 个代币到另一个地址...${NC}"
RECIPIENT_ADDRESS="0x0885c075630645205aD4c4124dfD20A74d170301"  # 请替换为接收地址
TRANSFER_VALUE="100000000000000000000"  # 100 * 10^18
TRANSFER_RESPONSE=$(curl -s -X POST "${BASE_URL}/transfer?to=${RECIPIENT_ADDRESS}&value=${TRANSFER_VALUE}")
echo "$TRANSFER_RESPONSE" | python3 -m json.tool
echo "等待交易确认..."
sleep 3
echo ""

# 8. 查询接收地址余额
echo -e "${BLUE}8. 查询接收地址余额...${NC}"
curl -s "${BASE_URL}/balance?account=${RECIPIENT_ADDRESS}" | python3 -m json.tool
echo ""

# 9. 授权
echo -e "${BLUE}9. 授权另一个地址可以花费 500 个代币...${NC}"
SPENDER_ADDRESS="0x3921618F3eaA5e8306382e965742722DDE0535Df"  # 请替换为被授权地址
APPROVE_VALUE="500000000000000000000"  # 500 * 10^18
APPROVE_RESPONSE=$(curl -s -X POST "${BASE_URL}/approve?spender=${SPENDER_ADDRESS}&value=${APPROVE_VALUE}")
echo "$APPROVE_RESPONSE" | python3 -m json.tool
echo "等待交易确认..."
sleep 3
echo ""

# 10. 从授权账户转账（transferFrom）
echo -e "${BLUE}10. 从授权账户转账 50 个代币...${NC}"
FROM_ADDRESS="${ACCOUNT_ADDRESS}"
TO_ADDRESS="${RECIPIENT_ADDRESS}"
TRANSFER_FROM_VALUE="50000000000000000000"  # 50 * 10^18
TRANSFER_FROM_RESPONSE=$(curl -s -X POST "${BASE_URL}/transferFrom?from=${FROM_ADDRESS}&to=${TO_ADDRESS}&value=${TRANSFER_FROM_VALUE}")
echo "$TRANSFER_FROM_RESPONSE" | python3 -m json.tool
echo "等待交易确认..."
sleep 3
echo ""

# 11. 最终余额查询
echo -e "${BLUE}11. 最终余额查询...${NC}"
echo "发送地址余额:"
curl -s "${BASE_URL}/balance?account=${ACCOUNT_ADDRESS}" | python3 -m json.tool
echo ""
echo "接收地址余额:"
curl -s "${BASE_URL}/balance?account=${RECIPIENT_ADDRESS}" | python3 -m json.tool
echo ""

echo -e "${GREEN}=========================================="
echo "测试完成！"
echo "==========================================${NC}"

