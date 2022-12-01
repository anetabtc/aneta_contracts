//package anetabtc.io.commands
//
//import anetabtc.io.Contracts
//import org.ergoplatform.appkit._
//import org.ergoplatform.appkit.cli.AppContext
//import org.ergoplatform.appkit.commands._
//import org.ergoplatform.appkit.config.ErgoToolConfig
//
//import scala.collection.JavaConverters._
//
//
//// Creates transaction from SC2 to the vault, that contains corresponding ERG
//// infoBoxId stands for users's TX out box, that contains aBTC sent information
//// it used as data input
//
//case class RedeemCmd (
//                       toolConf: ErgoToolConfig,
//                       name: String,
//                       vaultOrUserAddress: Address,
//                       amountToSend: Long,
//                       verifyBoxId: ErgoId
//                     ) extends Cmd with RunWithErgoClient {
//
//  override def runWithClient(ergoClient: ErgoClient, runCtx: AppContext): Unit = {
//    val console = runCtx.console
//    ergoClient.execute((ctx: BlockchainContext) => {
//      val prover = ctx.newProverBuilder().build()
//
//      // Get the contract address
//      val contracts = new Contracts(ctx)
//      val sender = contracts.RedeemSmartContract.toAddress
//      val txFee: Long = Parameters.MinFee
//
//      val boxOp = BoxOperations
//        .createForSender(sender, ctx)
//        .withAmountToSpend(amountToSend)
//        .withFeeAmount(txFee)
//
//      val redeemDataInputBox = ctx.getBoxesById(verifyBoxId.toString)
//      val receiver = vaultOrUserAddress
//      val boxes = boxOp.loadTop()
//
//      val txB = ctx.newTxBuilder()
//      val out = txB.outBoxBuilder()
//        .value(amountToSend)
//        .contract(receiver.toErgoContract)
//        .build()
//
//      val tx = txB
//        .withDataInputs(redeemDataInputBox.toList.asJava)
//        .boxesToSpend(boxes)
//        .outputs(out)
//        .fee(txFee)
//        .sendChangeTo(sender.getErgoAddress)
//        .build()
//
//      val signed = prover.sign(tx)
//      if (runCtx.isDryRun){
//        console.println(signed.toJson(true))
//      } else {
//        val txId = ctx.sendTransaction(signed)
//        console.println(txId)
//      }
//    })
//  }
//}
//
//object RedeemCmd extends CmdDescriptor(
//  name = "redeem",
//  cmdParamSyntax = "<vaultOrUserAddress> <amountToSend> <verifyBoxId>",
//  description = "redeem collateral ergs"){
//  override val parameters: Seq[CmdParameter] = Array(
//    CmdParameter("vaultOrUserAddress", AddressPType,
//      "receiver address"),
//    CmdParameter("amountToSend", LongPType,
//      "collateral amount"),
//    CmdParameter("verifyBoxId", ErgoIdPType,
//      "verify box id"),
//  )
//
//  override def createCmd(ctx: AppContext): Cmd = {
//    val Seq(
//    vaultOrUserAddress: Address,
//    tokenAmount: Long,
//    verifyBoxId: ErgoId
//    ) = ctx.cmdParameters
//
//    RedeemCmd(ctx.toolConf, name, vaultOrUserAddress, tokenAmount, verifyBoxId)
//  }
//}
