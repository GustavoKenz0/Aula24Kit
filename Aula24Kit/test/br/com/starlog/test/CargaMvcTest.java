package br.com.starlog.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import br.com.starlog.model.Carga;
import br.com.starlog.controller.CargaController;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SUÍTE DE TESTES TDD • AULA 24 (STARLOG LOGISTICS)
 * Permite validar a arquitetura sem abrir janelas gráficas!
 */
public class CargaMvcTest {

    @Test
    @DisplayName("TDD 01: Model deve calcular seguro padrão de 2% para carga normal")
    void deveCalcularSeguroPadrao() {
        Carga c = new Carga("CARG-01", "GERAL", 50.0, 1000.0);
        assertEquals(20.0, c.calcularSeguro(), 0.001);
    }

    @Test
    @DisplayName("TDD 02: Model deve aplicar sobretaxa de R$ 150 para carga perigosa")
    void deveAplicarSobretaxaCargaPerigosa() {
        Carga c = new Carga("CARG-02", "PERIGOSA", 20.0, 1000.0);
        // 2% de 1000 = 20 + 150 taxa = 170
        assertEquals(170.0, c.calcularSeguro(), 0.001);
    }

    @Test
    @DisplayName("TDD 03: Model deve aplicar sobretaxa para carga com peso superior a 100kg")
    void deveAplicarSobretaxaCargaPesada() {
        Carga c = new Carga("CARG-03", "ALIMENTAR", 120.0, 1000.0);
        assertEquals(170.0, c.calcularSeguro(), 0.001);
    }

    @Test
    @DisplayName("TDD 04: Fail-Fast - Construtor deve barrar peso zero ou negativo")
    void deveBarrarPesoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Carga("CARG-04", "GERAL", 0.0, 500.0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Carga("CARG-04", "GERAL", -10.0, 500.0);
        });
    }

    @Test
    @DisplayName("TDD 05: Fail-Fast - Construtor deve barrar código vazio ou em branco")
    void deveBarrarCodigoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Carga("   ", "GERAL", 10.0, 500.0);
        });
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t\n"})
    @DisplayName("Model deve rejeitar código nulo, vazio ou em branco")
    void deveBarrarCodigoNuloOuEmBranco(String codigo) {
        assertThrows(IllegalArgumentException.class,
                () -> new Carga(codigo, "GERAL", 10.0, 500.0));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t\n"})
    @DisplayName("Model deve rejeitar categoria nula, vazia ou em branco")
    void deveBarrarCategoriaInvalida(String categoria) {
        assertThrows(IllegalArgumentException.class,
                () -> new Carga("CARG-01", categoria, 10.0, 500.0));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -50.0, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
    @DisplayName("Model deve rejeitar valor declarado não positivo ou não finito")
    void deveBarrarValorDeclaradoInvalido(double valor) {
        assertThrows(IllegalArgumentException.class,
                () -> new Carga("CARG-01", "GERAL", 10.0, valor));
    }

    @ParameterizedTest
    @ValueSource(doubles = {Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
    @DisplayName("Model deve rejeitar peso não finito")
    void deveBarrarPesoNaoFinito(double peso) {
        assertThrows(IllegalArgumentException.class,
                () -> new Carga("CARG-01", "GERAL", peso, 500.0));
    }

    @Test
    @DisplayName("Carga de exatamente 100 kg deve manter apenas o seguro base")
    void naoDeveAplicarSobretaxaNoLimiteDePeso() {
        Carga c = new Carga("CARG-01", "GERAL", 100.0, 1000.0);
        assertEquals(20.0, c.calcularSeguro(), 0.001);
    }

    @Test
    @DisplayName("Carga perigosa e pesada deve receber a sobretaxa uma única vez")
    void naoDeveDuplicarSobretaxa() {
        Carga c = new Carga("CARG-01", "PERIGOSA", 120.0, 1000.0);
        assertEquals(170.0, c.calcularSeguro(), 0.001);
    }

    @Test
    @DisplayName("Model deve normalizar código e categoria antes de calcular seguro")
    void deveNormalizarCodigoECategoria() {
        Carga c = new Carga(" carg-01 ", " perigosa ", 20.0, 1000.0);
        assertEquals("CARG-01", c.getCodigoRastreio());
        assertEquals("PERIGOSA", c.getCategoria());
        assertEquals(170.0, c.calcularSeguro(), 0.001);
    }

    @Test
    @DisplayName("TDD 06: Controller deve iniciar com repositório vazio e sem cargas no relatório")
    void controllerDeveIniciarSemCargas() {
        CargaController controller = new CargaController();
        assertEquals(0, controller.getRepositorio().size());
        assertEquals("Nenhuma carga registrada na esteira.", controller.gerarTextoRelatorio());
    }
}
