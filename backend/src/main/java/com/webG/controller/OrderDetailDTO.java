package com.webG.controller;
public class OrderDetailDTO {
    private Long productId;
    private int cantidad;
    private double precio;
	public OrderDetailDTO(Long productId, int cantidad, double precio) {
		super();
		this.productId = productId;
		this.cantidad = cantidad;
		this.precio = precio;
	}
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public int getCantidad() {
		return cantidad;
	}
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
	public double getPrecio() {
		return precio;
	}
	public void setPrecio(double precio) {
		this.precio = precio;
	}
	public OrderDetailDTO() {
		super();
	}
	

}