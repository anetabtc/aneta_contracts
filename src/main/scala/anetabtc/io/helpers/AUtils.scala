package anetabtc.io.helpers

import org.ergoplatform.appkit.{BlockchainContext, ErgoProver, SecretString}

object AUtils {

    def getProver(ctx: BlockchainContext, mnemonic: String): ErgoProver = {
        getProver(ctx, SecretString.create(mnemonic))
    }

    def getProver(ctx: BlockchainContext, mnemonic: SecretString): ErgoProver = {
        ctx.newProverBuilder().withMnemonic(mnemonic, SecretString.create("")).withEip3Secret(0).build()
    }


}
