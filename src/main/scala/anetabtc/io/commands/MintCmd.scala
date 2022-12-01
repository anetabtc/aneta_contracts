package anetabtc.io.commands

import anetabtc.io.Contracts
import anetabtc.io.helpers.AUtils
import org.ergoplatform.appkit.InputBoxesSelectionException.{NotEnoughCoinsForChangeException, NotEnoughErgsException, NotEnoughTokensException}
import org.ergoplatform.appkit._
import org.ergoplatform.appkit.cli.AppContext
import org.ergoplatform.appkit.commands._
import org.ergoplatform.appkit.config.ErgoToolConfig

import java.util
import scala.collection.JavaConverters.seqAsJavaListConverter

// Mint creates transaction from SC1 to user, containing corresponding anetaBTC
// relyBoxId stands for vault's TX out box, that contains BTC tx information
// it used as data input

case class MintCmd(toolConf: ErgoToolConfig,
                   name: String,
                   receiverAddress: Address,
                   tokenAmount: Long,
                   verifyBoxId: String,
                  ) extends Cmd with RunWithErgoClient {

  override def runWithClient(ergoClient: ErgoClient, runCtx: AppContext): Unit = {
    val console = runCtx.console
    val nodeConf = runCtx.toolConf.getNode

    val result = try{
      ergoClient.execute((ctx: BlockchainContext) => {
        val tokenId: String = toolConf.getParameters.get("tokenId")

        // Prover to sign transaction
        val prover = AUtils.getProver(ctx, nodeConf.getWallet.getMnemonic)

        val contracts = new Contracts(ctx, prover)

        // Get the contract address
        val sender = contracts.MintContract.toAddress
        val amountToSend: Long = Parameters.MinFee
        val txFee: Long = Parameters.MinFee

        val tokens = List(new ErgoToken(tokenId, tokenAmount))

        val boxOp = BoxOperations
                .createForSender(sender, ctx)
                .withTokensToSpend(tokens.asJava)
                .withFeeAmount(txFee)

        val boxesToSpend = boxOp.loadTop()
        val verifyBox = ctx.getBoxesById(verifyBoxId)

        boxesToSpend.add(0, verifyBox(0))

        val txB = ctx.newTxBuilder()
        val mintingBox = txB.outBoxBuilder()
                .value(amountToSend)
                .tokens(tokens.head)
                .contract(receiverAddress.toErgoContract)
                .build()

        val tx = txB.boxesToSpend(boxesToSpend)
                .outputs(mintingBox)
                .fee(txFee)
                .sendChangeTo(sender.getErgoAddress)
                .build()

        val signed = prover.sign(tx)

        if (runCtx.isDryRun)
          signed.toJson(true)
        else {
          val txId = ctx.sendTransaction(signed)
          txId
        }
      })
    } catch {
      case _: NotEnoughErgsException => "Not enough ERGs"
      case _: NotEnoughTokensException => "Not enough tokens"
      case _: NotEnoughCoinsForChangeException => "Not enough coins for change"
      case e: Exception => e.getMessage
    }

    console.println(
      s"""
         |{
         |  "result": $result,
         |}
         |""".stripMargin)
    System.exit(1)
  }
}

object MintCmd extends CmdDescriptor(
  name = "mint",
  cmdParamSyntax = "<receiverAddress> <tokenAmount> <verifyBox>",
  description = "claim an AnetaBTC tokens"){
  override val parameters: Seq[CmdParameter] = Array(
    CmdParameter("receiverAddress", AddressPType,
      "receiver address"),
    CmdParameter("tokenAmount", LongPType,
      "token amount"),
    CmdParameter("verifyBox", StringPType, "verify box id"),
  )


  override def createCmd(ctx: AppContext): Cmd = {
    val Seq(
      receiverAddress: Address,
      tokenAmount: Long,
      verifyBoxId: String,
    ) = ctx.cmdParameters

    MintCmd(ctx.toolConf, name, receiverAddress, tokenAmount, verifyBoxId)
  }
}

