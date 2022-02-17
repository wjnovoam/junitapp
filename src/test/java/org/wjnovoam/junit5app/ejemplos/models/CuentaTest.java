package org.wjnovoam.junit5app.ejemplos.models;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.wjnovoam.junit5app.ejemplos.exceptions.DineroInsuficienteException;

import java.math.BigDecimal;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {

    Cuenta cuenta;

    @BeforeEach
    void initMetodoTest(){
        this.cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
        System.out.println("Iniciando el metodo");
    }

    @AfterEach
    void finalMetodoTest(){
        System.out.println("Finalizando el metodo de la prueba");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicializacdo la clase test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando el test");
    }

    @Test
    @DisplayName("Probando nombre de la cuenta!")
    void testNombreCuenta() {
        //cuenta.setPersona("William novoa");
        String esperado = "Andres";
        String real = cuenta.getPersona();
        assertNotNull(real, () -> "La cuenta no puede ser nula");
        assertEquals(esperado, real, () -> "El nombre de la cuenta no es lo que se esperaba " + "se esperaba: " + esperado + " sin embargo fue " + real);
        assertTrue(real.equals("Andres"), () -> "Nombre cuenta esperado debe ser igual al real");
    }

    @Test
    @DisplayName("Probando que el saldo de la cuenta corriente, que no sea null, mayor que cero, valor esperado.")
    void testSaldoCuenta() {
        cuenta = new Cuenta("Andres", new BigDecimal("11235776.1435435"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(11235776.1435435, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Testeando referencias que sean iguales con el metodo equals")
    void testReferenciaCuenta() {
        cuenta = new Cuenta("Jhon Doe", new BigDecimal("9889.34234"));
        Cuenta cuenta2 = new Cuenta("Jhon Doe", new BigDecimal("9889.34234"));

//      assertNotEquals(cuenta, cuenta2);
        assertEquals(cuenta, cuenta2);
    }

    @Test
    @DisplayName("Haciendo validacion del saldo de una cuenta debito")
    void testDebitoCuenta() {
        cuenta.debito(new BigDecimal("100"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    @DisplayName("Haciendo validacion del saldo de una cuenta credito")
    void testCreditoCuenta() {
        cuenta.credito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    @DisplayName("Validar si una cuenta tiene saldo suficiente para una transferencia")
    void testDineroInsuficienteExceptionsCuenta() {
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal(1500));
        });
        String actual = exception.getMessage();
        String esperado = "Dinero insuficiente";
        assertEquals(esperado, actual);
    }

    @Test
    @DisplayName("Validando transferencia de dinero entre cuentas ")
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
    @DisplayName("Probando relaciones entre las cuentas y el banco con assertALL")
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

    @Test
    @Disabled
    @DisplayName("Test utilizando @Disabled")
    void testDesabilitado() {
        assertTrue(true);
    }

    @Test
    @EnabledOnOs(OS.WINDOWS) // -> Solo se ejecuta en windows
    void testSoloWindows() {
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC}) //Se ejecuta solo en LINUX, MAC
    void testSoloLinuxMac() {

    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testNoWindows() {

    }

    @Test
    @EnabledOnJre(JRE.JAVA_8)
    void testSoloJdk8() {
    }

    @Test
    @EnabledOnJre(JRE.JAVA_15)
    void testSoloJdk15() {
    }

    @Test
    @DisabledOnJre(JRE.JAVA_8)
    void testDesabilitadoParaJdk8() {
    }

    //Habilitar si existe cierta propiedad del sistema


    @Test
    void imprimirSystemProperties() {
        Properties properties = System.getProperties();
        properties.forEach((key, value)-> System.out.println(key + " : "+ value));
    }

    @Test
    @EnabledIfSystemProperty(named = "java.version", matches = "1.8.0_202")
    void testJavaVersion() {
    }

    @Test
    @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
    void testSolo64() {
    }

    @Test
    @EnabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
    void testNo64() {
    }
}
