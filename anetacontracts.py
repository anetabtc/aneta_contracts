from bitcoinlib.services.services import Service, ServiceError
import os

# Function for eventual use in our Relay Smart Contract.

def getBTCInformation(txID):
  srv = Service(network="testnet")
  transaction = False
  while not transaction:
      transaction = srv.gettransaction(txID)
  txInfo = {
          "version": transaction.version,
          "network": transaction.network,
          "size": transaction.size,
          "block_height": transaction.block_height,
          "block_hash": transaction.block_hash,
          "input_total": transaction.input_total,
          "output_total": transaction.output_total
        }
  return txID, txInfo

def makeToken(txID):
    tokenID, tokenInfo = getBTCInformation(txID)
    pathToIssueToken = "/path/appkit-by-example"
    os.chdir(pathToIssueToken)
    os.system(f'sbt \'runMain anetabtc.io.commands.mint.IssueToken "{tokenID}" "{tokenInfo}"\' ')

makeToken("d4c512c32f2741a7589487021983d2e40d088388ca3e417fdd6b3ac02d26e2e5")
