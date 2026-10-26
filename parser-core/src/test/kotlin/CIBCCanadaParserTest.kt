import com.pennywiseai.parser.core.TransactionType
import com.pennywiseai.parser.core.bank.BankParserFactory
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class CIBCCanadaParserTest {
    @Test fun purchase() {
        val result = BankParserFactory.parse("Ashish, you spent $2.00 at GOOGLE *SERVICES with your card ending in 1202.", "242226", 123L)
        assertNotNull(result)
        assertEquals(BigDecimal("2.00"), result!!.amount)
        assertEquals(TransactionType.EXPENSE, result.type)
        assertEquals("GOOGLE *SERVICES", result.merchant)
        assertEquals("1202", result.accountLast4)
        assertEquals("CAD", result.currency)
        assertTrue(result.isFromCard)
    }
    @Test fun refund() {
        val result = BankParserFactory.parse("Ashish, $2.00 has been credited by GOOGLE *SERVICES to your card ending in 1202.", "242226", 123L)
        assertNotNull(result)
        assertEquals(TransactionType.INCOME, result!!.type)
        assertEquals(BigDecimal("2.00"), result.amount)
        assertEquals("GOOGLE *SERVICES", result.merchant)
    }
    @Test fun ignoreUnrelated() {
        assertNull(BankParserFactory.parse("Your verification code is 123456", "242226", 123L))
        assertNull(BankParserFactory.parse("Ashish, you spent $2.00 at GOOGLE with your card ending in 1202.", "999999", 123L))
    }
}
