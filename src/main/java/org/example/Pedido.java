package org.example;

// Objeto simple de datos: representa un pedido de "cantidad" unidades de "producto".
public class Pedido {

    private final String producto;
    private final int cantidad;

    public Pedido(String producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public String getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }
}
