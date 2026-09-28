package org.example;

// Esta es la dependencia externa (p.ej. una BD) que en el test NO queremos usar de verdad.
// Por eso la dejamos como interfaz: en el test la sustituimos por un "mock" con Mockito.
public interface PedidoRepository {

    double obtenerPrecioUnitario(String producto);

    void guardar(Pedido pedido);
}
