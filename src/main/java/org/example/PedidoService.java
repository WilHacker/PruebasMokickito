package org.example;

// Esta es la clase que vamos a probar. Depende de PedidoRepository,
// pero no crea esa dependencia ella misma: la recibe por constructor
// (inyección de dependencias). Eso es lo que permite reemplazarla
// por un mock en el test, sin tocar esta clase.
public class PedidoService {

    private final PedidoRepository repository;

    public PedidoService(PedidoRepository repository) {
        this.repository = repository;
    }

    public double calcularTotal(Pedido pedido) {
        if (pedido.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
        double precioUnitario = repository.obtenerPrecioUnitario(pedido.getProducto());
        return precioUnitario * pedido.getCantidad();
    }

    public void confirmarPedido(Pedido pedido) {
        repository.guardar(pedido);
    }
}
