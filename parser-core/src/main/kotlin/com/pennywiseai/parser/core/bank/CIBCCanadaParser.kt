package com.pennywiseai.parser.core.bank

import com.pennywiseai.parser.core.TransactionType
import java.math.BigDecimal

/** CIBC Canada credit-card purchase and credit SMS alerts from short code 242226. */
class CIBCCanadaParser : BankParser() {
    override fun getBankName() = "CIBC Canada"
    override fun getCurrency() = "CAD"
    override fun canHandle(sender: String) = sender.trim() == "242226"

    private val purchase = Regex("""\byou\s+spent\s+\$([0-9,]+(?:\.[0-9]{2})?)\s+at\s+(.+?)\s+with\s+your\s+card\s+ending\s+in\s+(\d{4})\b""", RegexOption.IGNORE_CASE)
    private val credit = Regex("""\$([0-9,]+(?:\.[0-9]{2})?)\s+has\s+been\s+credited\s+by\s+(.+?)\s+to\s+your\s+card\s+ending\s+in\s+(\d{4})\b""", RegexOption.IGNORE_CASE)
    private fun matched(message: String) = purchase.find(message) ?: credit.find(message)

    override fun isTransactionMessage(message: String) = !isNonTransactionMessage(message) && matched(message) != null
    override fun extractAmount(message: String): BigDecimal? = matched(message)?.groupValues?.get(1)?.replace(",", "")?.toBigDecimalOrNull()
    override fun extractTransactionType(message: String): TransactionType? = when {
        purchase.containsMatchIn(message) -> TransactionType.EXPENSE
        credit.containsMatchIn(message) -> TransactionType.INCOME
        else -> null
    }
    override fun extractMerchant(message: String, sender: String): String? = matched(message)?.groupValues?.get(2)?.trim()
    override fun extractAccountLast4(message: String): String? = matched(message)?.groupValues?.get(3)
    override fun detectIsCard(message: String) = true
}
