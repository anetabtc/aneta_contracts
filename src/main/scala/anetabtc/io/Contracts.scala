package anetabtc.io

import org.ergoplatform.appkit.{BlockchainContext, ConstantsBuilder, ErgoContract, ErgoProver, SecretString}


class Contracts(blockchainContext: BlockchainContext, prover: ErgoProver) {

    lazy val MintContract: ErgoContract = generateMintSmartContract()

    def generateMintSmartContract(): ErgoContract = {
        val contract = blockchainContext.compileContract(
            ConstantsBuilder.create()
            .item("vaultPK", prover.getEip3Addresses.get(0).getPublicKey)
            .build(),
            Scripts.MintProtectionScript
        )
        contract
    }

}
