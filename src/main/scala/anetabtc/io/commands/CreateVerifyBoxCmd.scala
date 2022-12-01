package anetabtc.io.commands

import anetabtc.io.helpers.AUtils
import org.ergoplatform.appkit.InputBoxesSelectionException.{NotEnoughCoinsForChangeException, NotEnoughErgsException, NotEnoughTokensException}
import org.ergoplatform.appkit.{Address, BlockchainContext, BoxOperations, ErgoClient, ErgoId, ErgoValue, InputBoxesSelectionException, Parameters, SecretString}
import org.ergoplatform.appkit.cli.AppContext
import org.ergoplatform.appkit.commands.{AddressPType, Cmd, CmdDescriptor, CmdParameter, LongPType, RunWithErgoClient}
import org.ergoplatform.appkit.config.ErgoToolConfig

case class CreateVerifyBoxCmd(
                             toolConf: ErgoToolConfig,
                             name: String,
                             receiverAddress: Address,
                             tokenAmount: Long
                             ) extends Cmd with RunWithErgoClient {

    override def runWithClient(ergoClient: ErgoClient, runCtx: AppContext): Unit = {
        val console = runCtx.console
        val nodeConf = runCtx.toolConf.getNode

        val result: String = try {
            ergoClient.execute((ctx: BlockchainContext) => {
                val prover = AUtils.getProver(ctx, nodeConf.getWallet.getMnemonic)
                val sender = prover.getEip3Addresses.get(0)
                val receiver = sender

                val boxOp = BoxOperations
                        .createForSender(sender, ctx)
                        .withAmountToSpend(Parameters.MinFee)
                        .withFeeAmount(Parameters.MinFee)

                val boxesToSpend = boxOp.loadTop()

                val txB = ctx.newTxBuilder()
                val verifyBox = txB.outBoxBuilder()
                        .value(Parameters.MinFee)
                        .contract(receiver.toErgoContract)
                        .registers(
                            ErgoValue.of(receiverAddress.getPublicKey),
                            ErgoValue.of(tokenAmount))
                        .build()

                val tx = txB
                        .boxesToSpend(boxesToSpend)
                        .outputs(verifyBox)
                        .fee(Parameters.MinFee)
                        .sendChangeTo(sender.getErgoAddress)
                        .build()

                val signed = prover.sign(tx)

                if (!runCtx.isDryRun) ctx.sendTransaction(signed)
                signed.getOutputsToSpend.get(0).getId.toString
            })
        } catch {
            case _: NotEnoughErgsException => "Not enough ERGs"
            case _: NotEnoughTokensException => "Not enough tokens"
            case _: NotEnoughCoinsForChangeException => "Not enough coins for change"
        }

        console.println(s"""
                           |{
                           |    "result": "$result"
                           |}
                           |""".stripMargin)
        System.exit(1)
    }
}

object CreateVerifyBoxCmd extends CmdDescriptor(
    name = "verify",
    cmdParamSyntax = "<receiverAddress> <tokenAmount>",
    description = "Create an verify box for minting"){

    override val parameters: Seq[CmdParameter] = Array(
        CmdParameter("receiverAddress", AddressPType,
            "receiver address"),
        CmdParameter("tokenAmount", LongPType,
            "token amount")
    )

    override def createCmd(ctx: AppContext): Cmd = {
        val Seq(
            recevierPK: Address,
            tokenAmount: Long,
        ) = ctx.cmdParameters

        CreateVerifyBoxCmd(ctx.toolConf, name, recevierPK, tokenAmount)
    }
}
