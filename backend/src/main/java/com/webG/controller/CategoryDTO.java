package com.webG.controller;

import com.webG.entity.Category;
//**Abstracción de la entidad de base de datos (`Category`)
public class CategoryDTO {
	
	private Long id;
    private String name;

    // Constructor que acepta un objeto Category
    public CategoryDTO(Category category) {
        if (category != null) {
            this.id = category.getId();
            this.name = category.getName();
        }
    }
    
	public CategoryDTO(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
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
    
}