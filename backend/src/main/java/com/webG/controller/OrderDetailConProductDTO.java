package com.webG.controller;


public class OrderDetailConProductDTO {

	private Long orderDetailId;
    private Long productId;
    private String productName;
    private String productDescription;
    private double productPrice;
    private int cantidad;
    private double precio;
	public Long getOrderDetailId() {
		return orderDetailId;
	}
	public void setOrderDetailId(Long orderDetailId) {
		this.orderDetailId = orderDetailId;
	}
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductDescription() {
		return productDescription;
	}
	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}
	public double getProductPrice() {
		return productPrice;
	}
	public void setProductPrice(double productPrice) {
		this.productPrice = productPrice;
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

	public OrderDetailConProductDTO(Long orderDetailId, Long productId, String productName, String productDescription, double productPrice, int cantidad, double precio) {
	    this.orderDetailId = orderDetailId;
	    this.productId = productId;
	    this.productName = productName;
	    this.productDescription = productDescription;
	    this.productPrice = productPrice;
	    this.cantidad = cantidad;
	    this.precio = precio;
	}

}