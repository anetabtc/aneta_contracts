import anetabtc.io.Scripts
import org.ergoplatform.compiler.ErgoScalaCompiler._
import org.ergoplatform.playgroundenv.utils.ErgoScriptCompiler
import org.ergoplatform.playground._
import org.ergoplatform.{DataInput, ErgoAddressEncoder, Pay2SHAddress}


///////////////////////////////////////////////////////////////////////////////////
// Prepare A Test Scenario //
///////////////////////////////////////////////////////////////////////////////////
// Create a simulated blockchain (aka "Mockchain")
val blockchainSim = newBlockChainSimulationScenario("Minting Scenario")

// Define an receiver (with a wallet tied to said Party)
val userParty = blockchainSim.newParty("user")
val userFunds = MinErg * 10000
userParty.generateUnspentBoxes(userFunds)

// Verifier
val verifyParty = blockchainSim.newParty("verifier")
val verifierFunds = MinErg * 10000
verifyParty.generateUnspentBoxes(verifierFunds)

// Initial
val initialParty = blockchainSim.newParty("initial")
val initialFunds = MinErg * 10000
initialParty.generateUnspentBoxes(initialFunds)

// Vault
val vaultParty = blockchainSim.newParty("vault")

// Define test tokens
val anetaBTC = blockchainSim.newToken("anetaBTC")


///////////////////////////////////////////////////////////////////////////////////
// Create Minting Contract //
///////////////////////////////////////////////////////////////////////////////////
val MintProtectionScript: String = Scripts.MintProtectionScript
val vaultPK = vaultParty.wallet.getAddress.pubKey

val mintingContract = ErgoScriptCompiler.compile(
    Map(
        "vaultPK" -> vaultPK,
    ),
    MintProtectionScript)

val contractAddress = Pay2SHAddress(mintingContract.ergoTree)(new ErgoAddressEncoder(ErgoAddressEncoder.MainnetNetworkPrefix))
println("Minting Contract Address: " + contractAddress)
println("-----------")


// Initial Mint - Issuing AnetaBTC
val issuingBox = Box(
    value = MinErg * 1000,
    token = (anetaBTC, 1000000L),
    script = mintingContract
)

// Create the issuing transaction
// We also need to set the fee which we set to the minimum, and any change will get sent back to the buyer
val issuingTransaction = Transaction(
    inputs = initialParty.selectUnspentBoxes(toSpend = initialFunds),
    outputs = List(issuingBox),
    fee = MinTxFee,
    sendChangeTo = initialParty.wallet.getAddress
)
// Print the issuing transaction
println(issuingTransaction)
println("---------------------")

// The Buyer signs the deposit transaction
val issuingTransactionSigned = initialParty.wallet.sign(issuingTransaction)

// Submit the transaction to the mockchain and print buyer assets
blockchainSim.send(issuingTransactionSigned)
initialParty.printUnspentAssets()
println("---------------------")

val verifyBoxRegisters = Map(
    R4 -> userParty.wallet.getAddress.pubKey,
    R5 -> 10000L,
)

// Verifying - Verify BTC Transaction
val verifyBox = Box(
    value = MinErg,
    registers = verifyBoxRegisters,
    script = mintingContract
)

// Create the verifying transaction
// We also need to set the fee which we set to the minimum, and any change will get sent back to the buyer
val verifyingTransactions = Transaction(
    inputs       = verifyParty.selectUnspentBoxes(toSpend = verifierFunds),
    outputs      = List(verifyBox),
    fee          = MinTxFee,
    sendChangeTo = verifyParty.wallet.getAddress
)
// Print the issuing transaction
println(verifyingTransactions)
println("---------------------")

// The Verifier signs the deposit transaction
val verifyingTransactionsSigned = verifyParty.wallet.sign(verifyingTransactions)

// Submit the transaction to the mockchain and print buyer assets
blockchainSim.send(verifyingTransactionsSigned)
verifyParty.printUnspentAssets()
println("---------------------")


// Create an output box with the users funds to be locked until the validation

val mintBox = Box(
    value = MinErg,
    tokens = List(anetaBTC -> 10000L),
    script = contract(userParty.wallet.getAddress.pubKey)
)

val tx = Transaction(
    inputs = List(issuingTransactionSigned.outputs(0)),
    dataInputs = List(DataInput(verifyingTransactionsSigned.outputs(0).id)),
    outputs = List(mintBox),
    fee = MinTxFee,
    sendChangeTo = initialParty.wallet.getAddress
)

println("Mint transaction: ")
println(tx)
println("---------------------")

val signed = vaultParty.wallet.sign(tx)
blockchainSim.send(signed)

initialParty.printUnspentAssets()
userParty.printUnspentAssets()
println("---------------------")
