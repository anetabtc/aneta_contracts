package anetabtc.io

import anetabtc.io.commands.{CreateVerifyBoxCmd, ListContractsCmd, MintCmd, TokensAmountCmd}
import org.ergoplatform.appkit.cli.{CliApplication, TestConsole, Console}
import org.ergoplatform.appkit.commands.CmdDescriptor
import org.ergoplatform.appkit.RestApiErgoClient

import java.io.{BufferedReader, OutputStream, PrintStream, Reader}

object Main extends CliApplication{
  override def commands: Seq[CmdDescriptor] = super.commands ++ Array(
    MintCmd, ListContractsCmd, CreateVerifyBoxCmd, TokensAmountCmd
  )

  /** Main entry point of console application. */
  def main(args: Array[String]): Unit = {
    val console = new TestConsole(new BufferedReader(Reader.nullReader()), System.out)
//    val console = Console.instance
    run(args, console, clientFactory = { ctx =>
      val explorerUrl = RestApiErgoClient.getDefaultExplorerUrl(ctx.networkType)
      RestApiErgoClient.create(ctx.apiUrl, ctx.networkType, ctx.apiKey, explorerUrl)
    })
  }
}
