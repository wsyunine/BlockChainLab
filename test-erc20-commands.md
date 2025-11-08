# ERC20Test 合约 API 测试命令

## 快速测试命令

### 1. 部署合约
```bash
curl -X POST http://localhost:8080/api/erc20/deploy | python3 -m json.tool
```

### 2. 加载合约（替换 YOUR_CONTRACT_ADDRESS）
```bash
curl -X POST "http://localhost:8080/api/erc20/load?address=YOUR_CONTRACT_ADDRESS" | python3 -m json.tool
```

### 3. 获取当前合约地址
```bash
curl -s http://localhost:8080/api/erc20/address | python3 -m json.tool
```

### 4. 查询余额（替换 YOUR_ACCOUNT_ADDRESS）
```bash
curl -s "http://localhost:8080/api/erc20/balance?account=YOUR_ACCOUNT_ADDRESS" | python3 -m json.tool
```

### 5. 铸造代币（替换 YOUR_ACCOUNT_ADDRESS）
```bash
# 铸造 1000 个代币（1000 * 10^18 wei）
curl -X POST "http://localhost:8080/api/erc20/mint?to=YOUR_ACCOUNT_ADDRESS&value=1000000000000000000000" | python3 -m json.tool
```

### 6. 转账（替换 RECIPIENT_ADDRESS）
```bash
# 转账 100 个代币（100 * 10^18 wei）
curl -X POST "http://localhost:8080/api/erc20/transfer?to=RECIPIENT_ADDRESS&value=100000000000000000000" | python3 -m json.tool
```

### 7. 授权（替换 SPENDER_ADDRESS）
```bash
# 授权 500 个代币（500 * 10^18 wei）
curl -X POST "http://localhost:8080/api/erc20/approve?spender=SPENDER_ADDRESS&value=500000000000000000000" | python3 -m json.tool
```

### 8. 从授权账户转账（替换 FROM_ADDRESS 和 TO_ADDRESS）
```bash
# 从授权账户转账 50 个代币（50 * 10^18 wei）
curl -X POST "http://localhost:8080/api/erc20/transferFrom?from=FROM_ADDRESS&to=TO_ADDRESS&value=50000000000000000000" | python3 -m json.tool
```

## 完整测试流程示例

```bash
# 1. 部署合约并保存地址
CONTRACT_ADDRESS=$(curl -s -X POST http://localhost:8080/api/erc20/deploy | python3 -c "import sys, json; print(json.load(sys.stdin)['contractAddress'])")
echo "合约地址: $CONTRACT_ADDRESS"

# 2. 加载合约
curl -X POST "http://localhost:8080/api/erc20/load?address=$CONTRACT_ADDRESS"

# 3. 设置账户地址（请替换为你的实际地址）
ACCOUNT="0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266"

# 4. 查询初始余额
curl -s "http://localhost:8080/api/erc20/balance?account=$ACCOUNT" | python3 -m json.tool

# 5. 铸造 1000 个代币
curl -X POST "http://localhost:8080/api/erc20/mint?to=$ACCOUNT&value=1000000000000000000000" | python3 -m json.tool

# 6. 等待几秒后查询余额
sleep 3
curl -s "http://localhost:8080/api/erc20/balance?account=$ACCOUNT" | python3 -m json.tool

# 7. 转账到另一个地址
RECIPIENT="0x70997970C51812dc3A010C7d01b50e0d17dc79C8"
curl -X POST "http://localhost:8080/api/erc20/transfer?to=$RECIPIENT&value=100000000000000000000" | python3 -m json.tool

# 8. 查询接收地址余额
sleep 3
curl -s "http://localhost:8080/api/erc20/balance?account=$RECIPIENT" | python3 -m json.tool
```

## 使用测试脚本

运行完整测试脚本（需要先修改脚本中的账户地址）：

```bash
./test-erc20-api.sh
```

## 注意事项

1. **地址格式**: 所有地址必须是有效的以太坊地址格式（0x 开头，42 个字符）
2. **数值单位**: value 参数使用 wei 单位（1 token = 10^18 wei）
3. **交易确认**: 区块链交易需要时间确认，建议在查询余额前等待几秒
4. **Gas 费用**: 确保账户有足够的测试币支付 Gas 费用
5. **合约加载**: 使用合约前必须先部署或加载合约

## 常见数值转换

- 1 个代币 = 1000000000000000000 wei (10^18)
- 100 个代币 = 100000000000000000000 wei
- 1000 个代币 = 1000000000000000000000 wei

