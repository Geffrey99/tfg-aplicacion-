package com.webG.controller;

public class ProductDTO {
	private Long id;
    private String name;
    private double price;
    private String photoUrl; // Aquí almacenaríamos la URL de la imagen
    private String description;
    private int stock;
    private CategoryDTO category; 

	public ProductDTO(Long id, String name, double price, String photoUrl, String description, int stock, CategoryDTO category) {
		super();
		this.id = id;
		this.name = name;
		this.price = price;
		this.photoUrl = photoUrl;
		this.description = description;
		this.stock = stock;
		this.category = category;
		
	}
	public ProductDTO() {
		// TODO Auto-generated constructor stub
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getPhotoUrl() {
		return photoUrl;
	}
	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getStock() {
		return stock;
	}
	public void setStock(int stock) {
		this.stock = stock;
	}
	public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }
}
