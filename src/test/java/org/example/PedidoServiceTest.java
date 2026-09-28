package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// ¿Qué es un "mock"? Es un objeto falso que imita a otro objeto real
// (aquí, PedidoRepository) pero sin hacer nada de verdad: no se conecta
// a ninguna base de datos, no llama a ningún servicio externo. Nosotros
// le decimos exactamente qué debe responder cuando alguien lo use.
//
// ¿Para qué sirve? Para probar PedidoService de forma aislada: si el test
// fallara, sabríamos que el error está en PedidoService y no en la base
// de datos real (que aquí ni siquiera existe).

// @ExtendWith(MockitoExtension.class) le dice a JUnit 5 que active Mockito
// para esta clase de test. Gracias a esto, Mockito busca los campos
// marcados con @Mock (más abajo) y los crea automáticamente antes de
// cada test, sin que tengamos que escribir código para inicializarlos.
@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    // @Mock crea una versión "de mentira" de PedidoRepository.
    // Por defecto, si llamamos a un método de este mock sin configurarlo,
    // devuelve un valor vacío (0, null, false...). Nosotros configuramos
    // el comportamiento que necesitemos en cada test con when(...).
    @Mock
    private PedidoRepository repositoryMock;

    // Esta es la clase real que queremos probar de verdad.
    // No lleva @Mock porque no la estamos simulando: queremos que su
    // código se ejecute tal cual, solo que usando el mock como dependencia.
    private PedidoService service;

    // @BeforeEach hace que este método se ejecute antes de CADA @Test
    // de esta clase, para partir siempre de un estado limpio.
    @BeforeEach
    void setUp() {
        // Creamos PedidoService pasándole el mock por el constructor.
        // Así, cuando PedidoService use "repository" por dentro,
        // en realidad estará usando nuestro repositoryMock.
        service = new PedidoService(repositoryMock);
    }

    @Test
    void calcularTotal_multiplicaPrecioUnitarioPorCantidad() {
        // --- ARRANGE (preparar) ---
        // when(...).thenReturn(...) es la forma de "programar" el mock:
        // "Cuando alguien llame a obtenerPrecioUnitario("teclado"),
        // el mock debe responder 50.0, como si esa fuera la respuesta
        // que vendría de la base de datos real."
        when(repositoryMock.obtenerPrecioUnitario("teclado")).thenReturn(50.0);
        Pedido pedido = new Pedido("teclado", 3);

        // --- ACT (ejecutar) ---
        // Llamamos al método real que queremos probar. Por dentro, este
        // método llamará a repository.obtenerPrecioUnitario(...), y como
        // repository es en realidad nuestro mock, recibirá el 50.0 que
        // configuramos arriba.
        double total = service.calcularTotal(pedido);

        // --- ASSERT (comprobar) ---
        // Verificamos que el resultado sea el esperado: 50.0 (precio) x 3
        // (cantidad) = 150.0. Si PedidoService tuviera un error de cálculo,
        // este assertEquals fallaría y el test nos avisaría.
        assertEquals(150.0, total);
    }

    @Test
    void calcularTotal_lanzaExcepcion_siCantidadEsCero() {
        Pedido pedidoInvalido = new Pedido("mouse", 0);

        // Aquí no hace falta configurar el mock con when(...), porque
        // PedidoService valida la cantidad ANTES de llegar a usar el
        // repository. Es decir, el mock ni siquiera llega a ser llamado.
        //
        // assertThrows comprueba que, al ejecutar ese código, se lance
        // la excepción indicada (IllegalArgumentException). Si no se
        // lanza ninguna excepción, o se lanza una distinta, el test falla.
        assertThrows(IllegalArgumentException.class, () -> service.calcularTotal(pedidoInvalido));
    }

    @Test
    void confirmarPedido_llamaAGuardarConElPedidoCorrecto() {
        Pedido pedido = new Pedido("monitor", 1);

        service.confirmarPedido(pedido);

        // El método guardar(...) de PedidoRepository es "void": no devuelve
        // nada, así que no podemos comprobar un resultado con assertEquals.
        // En su lugar, usamos verify(...) para comprobar que el mock
        // SÍ fue llamado, y exactamente con el argumento "pedido" que
        // esperábamos. Si PedidoService se olvidara de llamar a guardar(),
        // o llamara con otro pedido distinto, este verify fallaría.
        verify(repositoryMock).guardar(pedido);
    }
}
