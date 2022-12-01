package anetabtc.io


object Scripts {

  lazy val MintProtectionScript: String =
    s"""
         {
           // Constants are: verify_contract_id, vaultPK
           val verifyBox = INPUTS(0)

           val defined = allOf(
             Coll(
               verifyBox.R4[SigmaProp].isDefined,
               verifyBox.R5[Long].isDefined,
             )
           )

           if (defined){
             // Get inputs from the verifyTransaction
             val verified_senderPK = verifyBox.R4[SigmaProp].get // minter
             val verified_value = verifyBox.R5[Long].get // value

             sigmaProp(
               allOf(
                 Coll(
                   OUTPUTS(0).tokens(0)._2 == verified_value,
                   OUTPUTS(0).propositionBytes == verified_senderPK.propBytes,
                 )
               )
             ) && vaultPK
           } else sigmaProp(false)
         }
        """.stripMargin

  //TODO
  lazy val RedeemScript: String =
    s"""
       // Constants are:
       """.stripMargin

}
// 8,890.00000001
// 8,890.00010001