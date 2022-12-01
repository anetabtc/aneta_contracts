package anetabtc.io.commands

import org.ergoplatform.appkit._
import org.ergoplatform.appkit.cli.AppContext
import org.ergoplatform.appkit.commands.{AddressPType, Cmd, CmdDescriptor, CmdParameter, RunWithErgoClient}
import org.ergoplatform.appkit.config.ErgoToolConfig

case class TokensAmountCmd(toolConf: ErgoToolConfig,
                           name: String,
                           address: Address
                       ) extends Cmd with RunWithErgoClient {

    override def runWithClient(ergoClient: ErgoClient, runCtx: AppContext): Unit = {
        val console = runCtx.console
        val tokenId = ErgoId.create(runCtx.toolConf.getParameters.get("tokenId"))

        val tokenAmount = ergoClient.execute((ctx: BlockchainContext) => {
            val boxes = ctx.getUnspentBoxesFor(address, 0, 100)
            var total: Long = 0
            boxes.forEach((box: InputBox)=>{
                box.getTokens.forEach((tokenIns:ErgoToken)=>{
                    if (tokenIns.getId == tokenId)
                        total = total + tokenIns.getValue
                })
            })
            total
        })

        console.println(
            s"""
               |{
               | "tokenAmount": $tokenAmount
               |}
               |""".stripMargin
            )
        System.exit(1)

    }
}

object TokensAmountCmd extends CmdDescriptor(
        name = "getTokensAmount",
        cmdParamSyntax = "<address>",
        description = "Get teBTC tokens amount at some address") {

    override val parameters: Seq[CmdParameter] = Array(
        CmdParameter("address", AddressPType,
            "address")
    )

    override def createCmd(ctx: AppContext): Cmd = {
        val Seq(
            address: Address,
        ) = ctx.cmdParameters

        TokensAmountCmd(ctx.toolConf, name, address)
    }
}
