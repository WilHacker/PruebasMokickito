package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// PASO 1: activar Mockito en JUnit 5.
// @ExtendWith(MockitoExtension.class) hace que Mockito procese las anotaciones
// @Mock de esta clase antes de cada test, sin tener que inicializarlas a mano.
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    // PASO 2: crear el mock.
    // @Mock crea una implementación falsa de PedidoRepository. No llama a
    // ninguna base de datos real: por defecto, todos sus métodos devuelven
    // valores "vacíos" (0, null, false...) hasta que los configuramos con "when(...)".
    @Mock
    private PedidoRepository repositoryMock;

    // La clase real que queremos probar, usando el mock como su dependencia.
    private PedidoService service;

    @BeforeEach
    void setUp() {
        // PASO 3: inyectar el mock en la clase bajo prueba.
        // Como PedidoService recibe la dependencia por constructor,
        // simplemente le pasamos el mock aquí.
        service = new PedidoService(repositoryMock);
    }

    @Test
    void calcularTotal_multiplicaPrecioUnitarioPorCantidad() {
        // PASO 4 (Arrange): programar el comportamiento del mock con when(...).thenReturn(...).
        // "Cuando llamen a obtenerPrecioUnitario("teclado"), devuelve 50.0".
        when(repositoryMock.obtenerPrecioUnitario("teclado")).thenReturn(50.0);
        Pedido pedido = new Pedido("teclado", 3);

        // PASO 5 (Act): ejecutar el método real que queremos probar.
        double total = service.calcularTotal(pedido);

        // PASO 6 (Assert): comprobar el resultado.
        assertEquals(150.0, total);
    }

    @Test
    void calcularTotal_lanzaExcepcion_siCantidadEsCero() {
        Pedido pedidoInvalido = new Pedido("mouse", 0);

        // No hace falta configurar el mock aquí: la validación ocurre
        // antes de que el servicio llegue a usar el repository.
        assertThrows(IllegalArgumentException.class, () -> service.calcularTotal(pedidoInvalido));
    }

    @Test
    void confirmarPedido_llamaAGuardarConElPedidoCorrecto() {
        Pedido pedido = new Pedido("monitor", 1);

        service.confirmarPedido(pedido);

        // PASO 7 (Verify): en vez de comprobar un valor de retorno,
        // comprobamos que el mock fue llamado como esperábamos.
        // Útil para métodos void, donde no hay nada que "assertEquals".
        verify(repositoryMock).guardar(pedido);
    }
}
