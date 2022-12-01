package anetabtc.io.helpers

import org.ergoplatform.appkit.SecretString
import org.ergoplatform.appkit.cli.Console

class ExConsole() extends Console {
    override def print(s: String): Console = ???

    override def println(s: String): Console = ???

    override def readLine(): String = ???

    override def readLine(prompt: String): String = ???

    override def readPassword(): SecretString = ???

    override def readPassword(prompt: String): SecretString = ???
}
