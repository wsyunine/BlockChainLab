package com.wetech.demo.web3j.service;

import com.wetech.demo.web3j.contracts.erc20test.ERC20Test;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ERC20TestService {

    private final Web3j web3j;
    private final Credentials credentials;
    private final ContractGasProvider gasProvider;
    
    private ERC20Test contract;
    
    /**
     * -- GETTER --
     * Get the address of the currently loaded contract
     *
     * @return the contract address
     */
    @Getter
    private String contractAddress;

    /**
     * Deploy the ERC20Test contract to the blockchain
     * @return the address of the deployed contract
     */
    public CompletableFuture<String> deployContract() {
        log.info("Deploying ERC20Test contract...");
        return ERC20Test.deploy(web3j, credentials, gasProvider)
                .sendAsync()
                .thenApply(contract -> {
                    this.contract = contract;
                    this.contractAddress = contract.getContractAddress();
                    log.info("ERC20Test contract deployed to: {}", contractAddress);
                    return contractAddress;
                });
    }

    /**
     * Load an existing contract from the blockchain
     * @param contractAddress the address of the contract to load
     */
    public void loadContract(String contractAddress) {
        log.info("Loading ERC20Test contract from address: {}", contractAddress);
        this.contract = ERC20Test.load(contractAddress, web3j, credentials, gasProvider);
        this.contractAddress = contractAddress;
    }

    /**
     * Mint tokens to an address
     * @param to the address to mint tokens to
     * @param value the amount of tokens to mint
     * @return the transaction receipt
     */
    public CompletableFuture<TransactionReceipt> mint(String to, BigInteger value) {
        if (contract == null) {
            throw new IllegalStateException("Contract not deployed or loaded");
        }
        log.info("Minting {} tokens to address {} in contract at address: {}", value, to, contractAddress);
        return contract.mint(to, value).sendAsync();
    }

    /**
     * Get the address of the account used for transactions (from credentials)
     * @return the account address
     */
    public String getAccountAddress() {
        return credentials.getAddress();
    }

    /**
     * Transfer tokens to an address
     * Note: This transfers from the configured account (credentials.getAddress()) to the specified address
     * @param to the address to transfer tokens to
     * @param value the amount of tokens to transfer
     * @return the transaction receipt
     */
    public CompletableFuture<TransactionReceipt> transfer(String to, BigInteger value) {
        if (contract == null) {
            throw new IllegalStateException("Contract not deployed or loaded");
        }
        String fromAddress = credentials.getAddress();
        log.info("Transferring {} tokens from {} to {} in contract at address: {}", value, fromAddress, to, contractAddress);
        
        // 先检查余额
        return balanceOf(fromAddress)
                .thenCompose(balance -> {
                    if (balance.compareTo(value) < 0) {
                        String errorMsg = String.format("Insufficient balance. From address: %s, Balance: %s, Required: %s", 
                                fromAddress, balance.toString(), value.toString());
                        log.error(errorMsg);
                        throw new IllegalStateException(errorMsg);
                    }
                    return contract.transfer(to, value).sendAsync();
                });
    }

    /**
     * Get the balance of an address
     * @param account the address to check balance for
     * @return the balance
     */
    public CompletableFuture<BigInteger> balanceOf(String account) {
        if (contract == null) {
            throw new IllegalStateException("Contract not deployed or loaded");
        }
        log.info("Getting balance for address {} in contract at address: {}", account, contractAddress);
        return contract.balanceOf(account).sendAsync();
    }

    /**
     * Approve a spender to spend tokens on behalf of the owner
     * @param spender the address to approve
     * @param value the amount of tokens to approve
     * @return the transaction receipt
     */
    public CompletableFuture<TransactionReceipt> approve(String spender, BigInteger value) {
        if (contract == null) {
            throw new IllegalStateException("Contract not deployed or loaded");
        }
        log.info("Approving {} tokens for spender {} in contract at address: {}", value, spender, contractAddress);
        return contract.approve(spender, value).sendAsync();
    }

    /**
     * Transfer tokens from one address to another (requires approval)
     * @param from the address to transfer from
     * @param to the address to transfer to
     * @param value the amount of tokens to transfer
     * @return the transaction receipt
     */
    public CompletableFuture<TransactionReceipt> transferFrom(String from, String to, BigInteger value) {
        if (contract == null) {
            throw new IllegalStateException("Contract not deployed or loaded");
        }
        log.info("Transferring {} tokens from {} to {} in contract at address: {}", value, from, to, contractAddress);
        return contract.transferFrom(from, to, value).sendAsync();
    }
}