package org.wjnovoam.junit5app.ejemplos.models;

import org.junit.jupiter.api.Test;
import org.wjnovoam.junit5app.ejemplos.exceptions.DineroInsuficienteException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {

    @Test
    void testNombreCuenta() {
        Cuenta cuenta = new Cuenta("Andres", new BigDecimal("11235776.1435435"));
        //cuenta.setPersona("William novoa");
        String esperado = "Andres";
        String real = cuenta.getPersona();
        assertNotNull(real, () -> "La cuenta no puede ser nula");
        assertEquals(esperado, real, () -> "El nombre de la cuenta no es lo que se esperaba " + "se esperaba: " + esperado + " sin embargo fue " + real);
        assertTrue(real.equals("Andres"), () -> "Nombre cuenta esperado debe ser igual al real");
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
        cuenta.debito(new BigDecimal("100"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testCreditoCuenta() {
        Cuenta cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
        cuenta.credito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testDineroInsuficienteExceptionsCuenta() {
        Cuenta cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal(1500));
        });
        String actual = exception.getMessage();
        String esperado = "Dinero insuficiente";
        assertEquals(esperado, actual);
    }

    @Test
    void testTransferirDineroCuentas() {
        Cuenta cuenta1 = new Cuenta("William", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.8989"));

        Banco banco = new Banco();
        banco.setNombre("Banco de san martin");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));
        assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
        assertEquals("3000", cuenta1.getSaldo().toPlainString());
    }

    @Test
    void testRelacionBancoCuentas() {
        Cuenta cuenta1 = new Cuenta("William", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.8989"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        banco.setNombre("Banco de san martin");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));

        assertAll(
                () -> assertEquals("1000.8989", cuenta2.getSaldo().toPlainString()
                        , () -> "El valor de la saldo de la cuenta2 no es esperado"),
                () -> assertEquals("3000", cuenta1.getSaldo().toPlainString()
                        , () -> "El valor de la saldo de la cuenta1 no es esperado"),
                () -> assertEquals(2, banco.getCuentas().size()
                        , () -> "El banco no tienes las cuentas esperadas"),
                () -> assertEquals("Banco de san martin", cuenta2.getBanco().getNombre()
                        , () -> "El nombre del banco no es el esperado"),
                () -> assertEquals("Andres", banco.getCuentas().stream()
                                .filter((c) -> c.getPersona().equals("Andres")).findFirst().get().getPersona()
                        , () -> "El nombre de la cuenta que eesta en el  banco no coincide"),
                () -> assertTrue(banco.getCuentas().stream()
                                .anyMatch((c) -> c.getPersona().equals("Andres"))
                        , () -> "El nombre no coincide con ninguno de las cuentas")
        );
    }
}
