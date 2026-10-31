package com.example.kontenin.presentation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PaymentValidatorTest {

    @Test
    fun `parseDibayar handles empty and digit strings correctly`() {
        assertEquals(0L, PaymentValidator.parseDibayar(""))
        assertEquals(0L, PaymentValidator.parseDibayar("   "))
        assertEquals(0L, PaymentValidator.parseDibayar("0"))
        assertEquals(500_000L, PaymentValidator.parseDibayar("500000"))
        assertEquals(1_500_000L, PaymentValidator.parseDibayar("1.500.000"))
        assertEquals(2_000_000L, PaymentValidator.parseDibayar("Rp 2.000.000"))
        assertNull(PaymentValidator.parseDibayar("-50000"))
        assertNull(PaymentValidator.parseDibayar("999999999999999999999999999"))
    }

    @Test
    fun `validateDibayar within 0 to fee is valid`() {
        val fee = 2_000_000L
        assertNull(PaymentValidator.validateDibayar(0L, fee))
        assertNull(PaymentValidator.validateDibayar(500_000L, fee))
        assertNull(PaymentValidator.validateDibayar(2_000_000L, fee))
    }

    @Test
    fun `validateDibayar exceeding fee returns ExceedsFee error`() {
        val fee = 2_000_000L
        val error = PaymentValidator.validateDibayar(2_000_001L, fee)
        assertEquals(PaymentValidationError.ExceedsFee, error)
    }

    @Test
    fun `validateDibayar negative returns Negative error`() {
        val fee = 2_000_000L
        val error = PaymentValidator.validateDibayar(-1L, fee)
        assertEquals(PaymentValidationError.Negative, error)
    }

    @Test
    fun `validateDibayarText validates text inputs and overflow`() {
        val fee = 3_000_000L
        assertNull(PaymentValidator.validateDibayarText("", fee))
        assertNull(PaymentValidator.validateDibayarText("0", fee))
        assertNull(PaymentValidator.validateDibayarText("1.500.000", fee))
        assertNull(PaymentValidator.validateDibayarText("3.000.000", fee))

        assertEquals(
            PaymentValidationError.ExceedsFee,
            PaymentValidator.validateDibayarText("3.500.000", fee)
        )
        assertEquals(
            PaymentValidationError.Overflow,
            PaymentValidator.validateDibayarText("99999999999999999999999999999", fee)
        )
        assertEquals(
            PaymentValidationError.Negative,
            PaymentValidator.validateDibayarText("-100", fee)
        )
    }

    @Test
    fun `isMarkPaidVisible shows only when unpaid and fee is greater than zero`() {
        // Belum lunas dan fee > 0 -> true
        assertTrue(PaymentValidator.isMarkPaidVisible(fee = 2_000_000L, dibayar = 500_000L))
        assertTrue(PaymentValidator.isMarkPaidVisible(fee = 2_000_000L, dibayar = 0L))

        // Lunas -> false
        assertFalse(PaymentValidator.isMarkPaidVisible(fee = 2_000_000L, dibayar = 2_000_000L))

        // Fee 0 -> false (EC-8 & testing step 4)
        assertFalse(PaymentValidator.isMarkPaidVisible(fee = 0L, dibayar = 0L))
    }

    @Test
    fun `isLunas correctly evaluates paid status`() {
        assertTrue(PaymentValidator.isLunas(fee = 2_000_000L, dibayar = 2_000_000L))
        assertTrue(PaymentValidator.isLunas(fee = 2_000_000L, dibayar = 2_500_000L))
        assertTrue(PaymentValidator.isLunas(fee = 0L, dibayar = 0L))

        assertFalse(PaymentValidator.isLunas(fee = 2_000_000L, dibayar = 1_999_999L))
        assertFalse(PaymentValidator.isLunas(fee = 2_000_000L, dibayar = 0L))
    }
}
