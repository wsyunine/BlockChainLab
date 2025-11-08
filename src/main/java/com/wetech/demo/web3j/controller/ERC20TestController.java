package com.wetech.demo.web3j.controller;

import com.wetech.demo.web3j.service.ERC20TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/erc20")
@RequiredArgsConstructor
public class ERC20TestController {

    private final ERC20TestService erc20Service;

    /**
     * Deploy a new ERC20Test contract
     * @return the address of the deployed contract
     */
    @PostMapping("/deploy")
    public CompletableFuture<ResponseEntity<Map<String, String>>> deployContract() {
        return erc20Service.deployContract()
                .thenApply(address -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("contractAddress", address);
                    return ResponseEntity.ok(response);
                });
    }

    /**
     * Load an existing contract
     * @param address the address of the contract to load
     * @return a success message
     */
    @PostMapping("/load")
    public ResponseEntity<Map<String, String>> loadContract(@RequestParam String address) {
        erc20Service.loadContract(address);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Contract loaded successfully");
        response.put("contractAddress", address);
        return ResponseEntity.ok(response);
    }

    /**
     * Mint tokens to an address
     * @param to the address to mint tokens to
     * @param value the amount of tokens to mint
     * @return the transaction receipt details
     */
    @PostMapping("/mint")
    public CompletableFuture<ResponseEntity<Map<String, String>>> mint(
            @RequestParam String to,
            @RequestParam String value) {
        BigInteger amount = new BigInteger(value);
        return erc20Service.mint(to, amount)
                .thenApply(receipt -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("transactionHash", receipt.getTransactionHash());
                    response.put("blockNumber", receipt.getBlockNumber().toString());
                    response.put("gasUsed", receipt.getGasUsed().toString());
                    response.put("status", receipt.getStatus());
                    response.put("contractAddress", erc20Service.getContractAddress());
                    response.put("to", to);
                    response.put("value", value);
                    
                    // 检查交易状态
                    if (!"0x1".equals(receipt.getStatus())) {
                        response.put("error", "Transaction failed");
                        log.warn("Mint transaction failed: {}", receipt.getTransactionHash());
                    }
                    
                    return ResponseEntity.ok(response);
                })
                .exceptionally(ex -> {
                    log.error("Mint failed", ex);
                    Map<String, String> response = new HashMap<>();
                    response.put("error", ex.getMessage());
                    String errorMsg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    response.put("message", "Mint failed: " + errorMsg);
                    return ResponseEntity.status(500).body(response);
                });
    }

    /**
     * Transfer tokens to an address
     * @param to the address to transfer tokens to
     * @param value the amount of tokens to transfer
     * @return the transaction receipt details
     */
    @PostMapping("/transfer")
    public CompletableFuture<ResponseEntity<Map<String, String>>> transfer(
            @RequestParam String to,
            @RequestParam String value) {
        BigInteger amount = new BigInteger(value);
        String fromAddress = erc20Service.getAccountAddress();
        
        return erc20Service.transfer(to, amount)
                .thenApply(receipt -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("transactionHash", receipt.getTransactionHash());
                    response.put("blockNumber", receipt.getBlockNumber().toString());
                    response.put("gasUsed", receipt.getGasUsed().toString());
                    response.put("status", receipt.getStatus());
                    response.put("contractAddress", erc20Service.getContractAddress());
                    response.put("from", fromAddress);
                    response.put("to", to);
                    response.put("value", value);
                    
                    // 检查交易状态
                    if (!"0x1".equals(receipt.getStatus())) {
                        response.put("error", "Transaction failed");
                        log.warn("Transfer transaction failed: {}", receipt.getTransactionHash());
                    }
                    
                    return ResponseEntity.ok(response);
                })
                .exceptionally(ex -> {
                    log.error("Transfer failed", ex);
                    Map<String, String> response = new HashMap<>();
                    response.put("error", ex.getMessage());
                    String errorMsg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    response.put("message", "Transfer failed: " + errorMsg);
                    response.put("from", fromAddress);
                    response.put("to", to);
                    response.put("value", value);
                    response.put("hint", "Make sure the 'from' address has sufficient token balance. Use GET /api/erc20/account to check the from address, and GET /api/erc20/balance?account=<from> to check balance.");
                    return ResponseEntity.status(500).body(response);
                });
    }

    /**
     * Get the balance of an address
     * @param account the address to check balance for
     * @return the balance
     */
    @GetMapping("/balance")
    public CompletableFuture<ResponseEntity<Map<String, String>>> balanceOf(@RequestParam String account) {
        return erc20Service.balanceOf(account)
                .thenApply(balance -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("account", account);
                    response.put("balance", balance.toString());
                    response.put("contractAddress", erc20Service.getContractAddress());
                    return ResponseEntity.ok(response);
                });
    }

    /**
     * Approve a spender to spend tokens
     * @param spender the address to approve
     * @param value the amount of tokens to approve
     * @return the transaction receipt details
     */
    @PostMapping("/approve")
    public CompletableFuture<ResponseEntity<Map<String, String>>> approve(
            @RequestParam String spender,
            @RequestParam String value) {
        BigInteger amount = new BigInteger(value);
        return erc20Service.approve(spender, amount)
                .thenApply(receipt -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("transactionHash", receipt.getTransactionHash());
                    response.put("blockNumber", receipt.getBlockNumber().toString());
                    response.put("gasUsed", receipt.getGasUsed().toString());
                    response.put("status", receipt.getStatus());
                    response.put("contractAddress", erc20Service.getContractAddress());
                    response.put("spender", spender);
                    response.put("value", value);
                    
                    // 检查交易状态
                    if (!"0x1".equals(receipt.getStatus())) {
                        response.put("error", "Transaction failed");
                        log.warn("Approve transaction failed: {}", receipt.getTransactionHash());
                    }
                    
                    return ResponseEntity.ok(response);
                })
                .exceptionally(ex -> {
                    log.error("Approve failed", ex);
                    Map<String, String> response = new HashMap<>();
                    response.put("error", ex.getMessage());
                    String errorMsg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    response.put("message", "Approve failed: " + errorMsg);
                    return ResponseEntity.status(500).body(response);
                });
    }

    /**
     * Transfer tokens from one address to another (requires approval)
     * @param from the address to transfer from
     * @param to the address to transfer to
     * @param value the amount of tokens to transfer
     * @return the transaction receipt details
     */
    @PostMapping("/transferFrom")
    public CompletableFuture<ResponseEntity<Map<String, String>>> transferFrom(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String value) {
        BigInteger amount = new BigInteger(value);
        return erc20Service.transferFrom(from, to, amount)
                .thenApply(receipt -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("transactionHash", receipt.getTransactionHash());
                    response.put("blockNumber", receipt.getBlockNumber().toString());
                    response.put("gasUsed", receipt.getGasUsed().toString());
                    response.put("status", receipt.getStatus());
                    response.put("contractAddress", erc20Service.getContractAddress());
                    response.put("from", from);
                    response.put("to", to);
                    response.put("value", value);
                    
                    // 检查交易状态
                    if (!"0x1".equals(receipt.getStatus())) {
                        response.put("error", "Transaction failed");
                        log.warn("TransferFrom transaction failed: {}", receipt.getTransactionHash());
                    }
                    
                    return ResponseEntity.ok(response);
                })
                .exceptionally(ex -> {
                    log.error("TransferFrom failed", ex);
                    Map<String, String> response = new HashMap<>();
                    response.put("error", ex.getMessage());
                    String errorMsg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    response.put("message", "TransferFrom failed: " + errorMsg);
                    return ResponseEntity.status(500).body(response);
                });
    }

    /**
     * Get the address of the currently loaded contract
     * @return the contract address
     */
    @GetMapping("/address")
    public ResponseEntity<Map<String, String>> getContractAddress() {
        String address = erc20Service.getContractAddress();
        Map<String, String> response = new HashMap<>();
        if (address != null) {
            response.put("contractAddress", address);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "No contract loaded");
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Get the account address used for transactions (from configured private key)
     * @return the account address
     */
    @GetMapping("/account")
    public ResponseEntity<Map<String, String>> getAccountAddress() {
        String address = erc20Service.getAccountAddress();
        Map<String, String> response = new HashMap<>();
        response.put("accountAddress", address);
        response.put("message", "This is the address that will be used as 'from' in transfer operations");
        return ResponseEntity.ok(response);
    }
}