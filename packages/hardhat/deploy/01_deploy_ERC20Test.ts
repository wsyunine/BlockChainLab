import { HardhatRuntimeEnvironment } from "hardhat/types";
import { DeployFunction } from "hardhat-deploy/types";
import { Contract } from "ethers";

/**
 * Deploys an ERC20 contract named "ERC20WSY202330552841" using the deployer account.
 *
 * @param hre HardhatRuntimeEnvironment object.
 */
const deployERC20WSY202330552841: DeployFunction = async function (hre: HardhatRuntimeEnvironment) {
  const { deployer } = await hre.getNamedAccounts();
  const { deploy } = hre.deployments;

  // Deploy ERC20 contract with the constructor arguments (name, symbol)
  await deploy("ERC20WSY202330552841", {
    from: deployer,
    args: [],  // Token name and symbol
    log: true,
    autoMine: true,
  });

  // Get the deployed contract to interact with it after deployment
  const erc20Contract = await hre.ethers.getContract<Contract>("ERC20WSY202330552841", deployer);

  console.log("👋 Token deployed at:", erc20Contract.address);

  // You can also add further interactions with the deployed contract if needed
};

export default deployERC20WSY202330552841;

// Assign the custom tag to this deployment script
deployERC20WSY202330552841.tags = ["ERC20WSY202330552841"];