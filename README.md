# AnetaBTC

## Introduction


## Installation

Installation of Contracts on your computer is simple, as there are some requirements that need to be located inside your local machine before building the project.

The instructions below should work on POSIX OSes (i.e. Linux, MacOS) and were tested on
MacOS.

### Build Contracts From Source Code

#### 1. Clone Contracts repository

You need to have [git](https://git-scm.com/) installed on your system.
Open Terminal and use the following commands to clone the Contracts source code.
```
$ git clone https://github.com/anetabtc/contracts.git
Cloning into 'contracts'...
remote: Enumerating objects: 112, done.
remote: Counting objects: 100% (112/112), done.
remote: Compressing objects: 100% (51/51), done.
remote: Total 112 (delta 36), reused 97 (delta 23), pack-reused 0
Receiving objects: 100% (112/112), 19.29 KiB | 3.86 MiB/s, done.
Resolving deltas: 100% (36/36), done.
```
Once the clone operation is complete you can compile contracts.

#### 2. Compile Java Executable

In order to compile Contracts you need to have SBT [installed](https://www.scala-sbt.org/download.html) on your system. 

Run the following commands to build a Java fat jar file with contracts and all the
dependencies needed to run it using the `java` launcher.
```shell
$ cd contracts
$ sbt assembly
```
you should see the output which looks like the following
```
[info] welcome to sbt 1.5.5 (GraalVM Community Java 11.0.16)
[info] loading global plugins from /Users/ozhek/.sbt/1.0/plugins
[info] loading settings for project contracts-build from plugins.sbt ...
[info] loading project definition from /Users/ozhek/Developer/contracts/project
[info] loading settings for project contracts from build.sbt ...
[info] set current project to anetabtc.io (in build file:/Users/ozhek/Developer/contracts/)
[info] Strategy 'discard' was applied to 41 files (Run the task at debug level to see details)
[warn] Ignored unknown package option FixedTimestamp(Some(1262304000000))
[success] Total time: 10 s, completed Oct 23, 2022, 9:54:33 PM

```

Now run the compiled java executable using the following command which runs the contracts application.
Note, you should run this command while in the root folder of the cloned ErgoTool repository.

```
java -jar target/scala-2.12/anetabtc-tool-0.1.jar
Please specify command name and parameters.

Usage:
ergotool [options] action [action parameters]

Available actions:
  contracts 
        List all contracts addresses
  freezeCoin <amountToFreeze>
        freeze your ERGs
  help <commandName>
        prints usage help for a command
  listBoxes <address>
        List all boxes for address
  mint <receiverAddress> <tokenAmount> <verifyBox>
        claim an AnetaBTC tokens
  verify <receiverAddress> <tokenAmount>
        Create an verify box for minting

Options:
  --conf
        configuration file path relative to the local directory (Example: `--conf ergo_tool.json`
  --dry-run
        Forces the command to report what will be done by the operation without performing the actual operation.
  --limit-list
        Specifies a number of items in the output list.
  --print-json
        Forces the commands to print json instead of table rows.

 
```

## Actions

##### *Beforehand, specify parameters.tokenId in [config file](ergo_tool_config.json)*

### Mint Scenario
1. Compile contracts addresses(for now only Mint Contract) run:
    ```
      java -jar target/scala-2.12/anetabtc-tool-0.1.jar contracts
    ```
2. Send token specified in config along with some ERGs to the address you got from step 1;


3. Create verify box using:
    ```
      java -jar target/scala-2.12/anetabtc-tool-0.1.jar verify <receiverAddress> <tokenAmount>
    ```
   Note: 


4. Mint for user using verifyBoxId got from step 3:
    ```
   java -jar target/scala-2.12/anetabtc-tool-0.1.jar mint <receiverAddress> <tokenAmount> <verifyBoxId>
    ```

### Additional

1. To get total teBTC amount for an address run:
```
  java -jar target/scala-2.12/anetabtc-tool-0.1.jar getTokensAmount <address> 
```

## Contributions

All kinds of contributions are welcome. Feel free to file a PR with fixes of
typos, documentation out of sync, or reach out to (willie[at]tca.io) about something you believe should be fixed.
If you want to start hacking on the code, you can file an issue with the description of
what you want to do. If you are interested in joining the the development team and helping out with future developments, get in touch with a direct message (info[at]anetabtc.io).


## References

- [Ergo](https://ergoplatform.org/)
- [Ergo Appkit](https://github.com/ergoplatform/ergo-appkit)
- [Introduction to Appkit](https://ergoplatform.org/en/blog/2019_12_03_top5/)
- [Appkit Examples](https://github.com/ApexTheory/appkit-by-example)
- [ErgoTool](https://github.com/ergoplatform/ergo-tool)
- [Bitcoinlib documentation](https://github.com/1200wd/bitcoinlib)
