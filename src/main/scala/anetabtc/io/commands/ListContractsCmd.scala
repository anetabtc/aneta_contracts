package anetabtc.io.commands

import anetabtc.io.Contracts
import anetabtc.io.helpers.AUtils
import org.ergoplatform.appkit._
import org.ergoplatform.appkit.cli.AppContext
import org.ergoplatform.appkit.commands._
import org.ergoplatform.appkit.config.ErgoToolConfig

case class ListContractsCmd(toolConf: ErgoToolConfig,
                            name: String,
                        ) extends Cmd with RunWithErgoClient {

    override def runWithClient(ergoClient: ErgoClient, runCtx: AppContext): Unit = {
        val console = runCtx.console
        val nodeConf = runCtx.toolConf.getNode
        ergoClient.execute((ctx: BlockchainContext) => {
            val prover = AUtils.getProver(ctx, nodeConf.getWallet.getMnemonic)
            val contracts = new Contracts(ctx, prover)

            console.println(
                s"""
                   |{
                   |    "MintContract": "${contracts.MintContract.toAddress}"
                   |}""".stripMargin)
        })
        System.exit(1)
    }
}

object ListContractsCmd extends CmdDescriptor(
    name = "contracts",
    cmdParamSyntax = "",
    description = "List all contracts addresses"){

    override def createCmd(ctx: AppContext): Cmd = {
        val Seq(
        ) = ctx.cmdParameters

        ListContractsCmd(ctx.toolConf, name)
    }
}

