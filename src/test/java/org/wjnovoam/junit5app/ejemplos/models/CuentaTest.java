package org.wjnovoam.junit5app.ejemplos.models;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.wjnovoam.junit5app.ejemplos.exceptions.DineroInsuficienteException;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

class CuentaTest {

    Cuenta cuenta;

    private TestInfo testInfo;
    private TestReporter testReporter;

    @BeforeEach
    void initMetodoTest(TestInfo testInfo, TestReporter testReporter) {
        this.cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));

        this.testInfo = testInfo;
        this.testReporter = testReporter;

        System.out.println("Iniciando el metodo");
        testReporter.publishEntry("Ejecutando: " + testInfo.getDisplayName() + " " + testInfo.getTestMethod().get().getName() + " con las etiquetas "+ testInfo.getTags());
    }

    @AfterEach
    void finalMetodoTest() {
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

    @Tag("cuenta")
    @Nested
    @DisplayName("Probando atributos de la cuenta corriente")
    class CuentaTestNombreSaldo {
        @Test
        @DisplayName("El nombre de la cuenta!")
        void testNombreCuenta() {
            System.out.println(testInfo.getTags());
            String esperado = "Andres";
            String real = cuenta.getPersona();
            assertNotNull(real, () -> "La cuenta no puede ser nula");
            assertEquals(esperado, real, () -> "El nombre de la cuenta no es lo que se esperaba " + "se esperaba: " + esperado + " sin embargo fue " + real);
            assertTrue(real.equals("Andres"), () -> "Nombre cuenta esperado debe ser igual al real");
        }

        @Test
        @DisplayName("El saldo, que no sea null, mayor que cero, valor esperado.")
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
    }

    @Nested
    class CuentaOperaciones {
        @Tag("cuenta")
        @Test
        @DisplayName("Haciendo validacion del saldo de una cuenta debito")
        void testDebitoCuenta() {
            cuenta.debito(new BigDecimal("100"));
            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue());
            assertEquals("900.12345", cuenta.getSaldo().toPlainString());
        }

        @Tag("cuenta")
        @Test
        @DisplayName("Haciendo validacion del saldo de una cuenta credito")
        void testCreditoCuenta() {
            cuenta.credito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(1100, cuenta.getSaldo().intValue());
            assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
        }

        @Tag("cuenta")
        @Tag("banco")
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
    }

    @Tag("cuenta")
    @Tag("error")
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

    @Tag("cuenta")
    @Tag("banco")
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

    @Nested
    class SistemaOperativoTest {
        @Test
        @EnabledOnOs(OS.WINDOWS)
            // -> Solo se ejecuta en windows
        void testSoloWindows() {
        }

        @Test
        @EnabledOnOs({OS.LINUX, OS.MAC})
            //Se ejecuta solo en LINUX, MAC
        void testSoloLinuxMac() {

        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testNoWindows() {

        }
    }

    @Nested
    class JavaVersionTest {
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
    }

    @Nested
    class SystemPropertiesTest {
        @Test
            //Habilitar si existe cierta propiedad del sistema
        void imprimirSystemProperties() {
            Properties properties = System.getProperties();
            properties.forEach((key, value) -> System.out.println(key + " : " + value));
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

        @Test
        @EnabledIfSystemProperty(named = "user.name", matches = "WPOSS")
        void testUsername() {
        }

        @Test
        @EnabledIfSystemProperty(named = "ENV", matches = "dev")
        //Nuestras propias properties
        void testDev() {
        }
    }

    @Nested
    class VariableAmbienteTest {
        //Variables del sistema operativo o de la ejecucion del proyecto
        @Test
        void imprimirVariablesAmbiente() {
            final Map<String, String> getenv = System.getenv();
            getenv.forEach((key, value) -> System.out.println(key + " = " + value));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk1.8.0_202.*")
        void testJavaHome() {

        }

        @Test
        @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "8")
        void testProcesadores() {

        }

        @Test
        @EnabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "DEV")
        void testEnv() {

        }

        @Test
        @DisabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "prod")
        void testEnvProdDisabled() {

        }
    }


    //Es para evaluar expresion true o false de manera programatica,
    //ejecutar un test si un servicio o microservicio esta activo
    @Test
    @DisplayName("Test saldo cuenta dev")
    void testSaldoCuentaDev() {

        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumeTrue(esDev); // omitir o desabilitar un lo siguiente del test
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Test saldo cuenta dev 2")
    void testSaldoCuentaDev2() {

        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumingThat(esDev, () -> {
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        }); // omitir o desabilitar una parte de un metodo

        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @RepeatedTest(5)
    @DisplayName("Mensaje de repeticion con metodo @RepeatedTest Ejemplo 1")
    void testRepetirEjemplo1() {
        cuenta.debito(new BigDecimal("100"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @RepeatedTest(value = 5, name = "{displayName} - Repetición numero {currentRepetition} de {totalRepetitions}")
    @DisplayName("Mensaje de repeticion con metodo @RepeatedTest Ejemplo 2")
    void testRepetirEjemplo2(RepetitionInfo info) {

        if(info.getCurrentRepetition() == 3){
            System.out.println(" ->>>>>>>>> Estamos en la repeticion "+ info.getCurrentRepetition());
        }
        cuenta.debito(new BigDecimal("100"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Tag("param")
    @Nested
    class PruebasParametrizadasTest{
        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @ValueSource(strings = {"100","200","300","500", "700", "1000"})
        void testDebitoCuentaParametrizadoConString(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            System.out.println("cuenta = " + cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @ValueSource(ints = {100,200,400,500,600,1000})
        void testDebitoCuentaParametrizadoConInt(int monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            System.out.println("cuenta = " + cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"1,100","2,200","3,300","4,500", "5,700", "6,1000"})
        void testDebitoCuentaParametrizadoConCsvSource(String index, String monto) {
            System.out.println(index+ " -> "+ monto);
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            System.out.println("cuenta = " + cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"200,100,John,Andres","250,200,Pepe,Pepe","300,300,Maria,maria","510,500,Juan,Juan", "750,700,Lucas,Lucas", "1000,1000,Cata,Cata"})
        void testDebitoCuentaParametrizadoConCsvSource2(String saldo, String monto, String esperado, String actual) {
            System.out.println(saldo+ " -> "+ monto);
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);
            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado,actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data.csv")
        void testDebitoCuentaParametrizadoConCsvFileSource(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            System.out.println("cuenta = " + cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data2.csv")
        void testDebitoCuentaParametrizadoConCsvFileSource2(String saldo, String monto, String esperado, String actual) {
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);

            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado,actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }
    }

    @Tag("param")
    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @MethodSource("montoList")
    void testDebitoCuentaParametrizadoConMethodSource(String monto) {
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        System.out.println("cuenta = " + cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    static List<String> montoList(){
        return Arrays.asList("100","200","300","500", "700", "1000");
    }

    @Nested
    @Tag("timeout")
    class EjemploTiempoTest{
        //Falla la prueba cuando se pasa cierta cantidad de tiempo (para pruebas muy pesadas, o pruebas de integracion)
        @Test
        @Timeout(1)
        void testPruebaTimeout() throws InterruptedException {
            TimeUnit.SECONDS.sleep(1); //Haciendo que genere un timpo por defecto
        }

        @Test
        @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
        void testPruebaTimeout2() throws InterruptedException {
            TimeUnit.SECONDS.sleep(1); //Haciendo que genere un timpo por defecto
        }

        @Test
        void testTimeoutAssertions() {
            assertTimeout(Duration.ofSeconds(5),()->{
                TimeUnit.MILLISECONDS.sleep(4000); //Haciendo que genere un timpo por defecto
            }); //Tiempo que va a esperar
        }
    }

}
