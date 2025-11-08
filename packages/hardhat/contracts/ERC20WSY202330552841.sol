// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

contract ERC20WSY202330552841 {
    // 代币基本信息
    string private _name;
    string private _symbol;
    uint8 private _decimals;
    
    // 代币总量
    uint256 private _totalSupply;
    
    // 余额映射
    mapping(address => uint256) private _balances;
    
    // 授权映射 (owner => (spender => amount))
    mapping(address => mapping(address => uint256)) private _allowances;
    
    // 事件定义
    event Transfer(address indexed from, address indexed to, uint256 value);
    event Approval(address indexed owner, address indexed spender, uint256 value);
    
    // 构造函数
    constructor() {
        _name = "WSY Token";  // 修改为 "WSY Token"
        _symbol = "WSY";      // 修改为 "WSY"
        _decimals = 18;
        _totalSupply = 0;
    }
    
    // ERC20 标准函数
    function name() public view returns (string memory) {
        return _name;
    }
    
    function symbol() public view returns (string memory) {
        return _symbol;
    }
    
    function decimals() public view returns (uint8) {
        return _decimals;
    }
    
    function totalSupply() public view returns (uint256) {
        return _totalSupply;
    }
    
    function balanceOf(address account) public view returns (uint256) {
        return _balances[account];
    }
    
    function transfer(address to, uint256 value) public returns (bool) {
        require(to != address(0), "ERC20: transfer to the zero address");
        require(_balances[msg.sender] >= value, "ERC20: insufficient balance");
        
        _balances[msg.sender] -= value;
        _balances[to] += value;
        
        emit Transfer(msg.sender, to, value);
        return true;
    }
    
    function transferFrom(address from, address to, uint256 value) public returns (bool) {
        require(from != address(0), "ERC20: transfer from the zero address");
        require(to != address(0), "ERC20: transfer to the zero address");
        require(_balances[from] >= value, "ERC20: insufficient balance");
        require(_allowances[from][msg.sender] >= value, "ERC20: insufficient allowance");
        
        _balances[from] -= value;
        _balances[to] += value;
        _allowances[from][msg.sender] -= value;
        
        emit Transfer(from, to, value);
        return true;
    }
    
    function approve(address spender, uint256 value) public returns (bool) {
        require(spender != address(0), "ERC20: approve to the zero address");
        
        _allowances[msg.sender][spender] = value;
        
        emit Approval(msg.sender, spender, value);
        return true;
    }
    
    function allowance(address owner, address spender) public view returns (uint256) {
        return _allowances[owner][spender];
    }
    
    function mint(address to, uint256 value) public {
        require(to != address(0), "ERC20: mint to the zero address");
        
        _totalSupply += value;
        _balances[to] += value;
        
        emit Transfer(address(0), to, value);
    }
    
    function burn(address from, uint256 value) public {
        require(from != address(0), "ERC20: burn from the zero address");
        require(_balances[from] >= value, "ERC20: burn amount exceeds balance");
        
        _balances[from] -= value;
        _totalSupply -= value;
        
        emit Transfer(from, address(0), value);
    }
    
    function burn(uint256 value) public {
        require(_balances[msg.sender] >= value, "ERC20: burn amount exceeds balance");
        
        _balances[msg.sender] -= value;
        _totalSupply -= value;
        
        emit Transfer(msg.sender, address(0), value);
    }
}