package org.wjnovoam.junit5app.ejemplos.models;

import org.junit.jupiter.api.Test;
import org.wjnovoam.junit5app.ejemplos.exceptions.DineroInsuficienteException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {

    @Test
    void testNombreCuenta() {
        Cuenta cuenta = new Cuenta("Andres",  new BigDecimal("11235776.1435435"));
        //cuenta.setPersona("William novoa");
        String esperado = "Andres";
        String real = cuenta.getPersona();
        assertNotNull(real);
        assertEquals(esperado, real);
        assertTrue(real.equals("Andres"));
    }

    @Test
    void testSaldoCuenta() {
        Cuenta cuenta = new Cuenta("Andres", new BigDecimal("11235776.1435435"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(11235776.1435435, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testReferenciaCuenta() {
        Cuenta cuenta = new Cuenta("Jhon Doe", new BigDecimal("9889.34234"));
        Cuenta cuenta2 = new Cuenta("Jhon Doe", new BigDecimal("9889.34234"));

//      assertNotEquals(cuenta, cuenta2);
        assertEquals(cuenta, cuenta2);
    }

    @Test
    void testDebitoCuenta() {
         Cuenta cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
         cuenta.debito( new BigDecimal("100"));
         assertNotNull(cuenta.getSaldo());
         assertEquals(900, cuenta.getSaldo().intValue());
         assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testCreditoCuenta() {
        Cuenta cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
        cuenta.credito( new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testDineroInsuficienteExceptionsCuenta() {
        Cuenta cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
        Exception exception = assertThrows(DineroInsuficienteException.class, ()->{
            cuenta.debito(new BigDecimal(1500));
        });
        String actual = exception.getMessage();
        String esperado = "Dinero insuficiente";
        assertEquals(esperado, actual);
    }
}
