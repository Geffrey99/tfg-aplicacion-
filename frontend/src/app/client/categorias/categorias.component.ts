import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { NgxPaginationModule } from 'ngx-pagination';

import { CategoryService } from '../../services/features/category.service';
import { Category } from '../../interface/Category';

@Component({
  selector: 'app-categorias',
  standalone: true,
  imports: [CommonModule, RouterModule, NgxPaginationModule],
  templateUrl: './categorias.component.html',
  styleUrl: './categorias.component.css',
})
export class CategoriasComponent implements OnInit {

  category: Category | undefined;
  p: number = 1; // Inicializo la variable de la pagina en 1 como valor por defecto

  private imageBaseUrl = 'https://hhreformas.es/api/product-images/';
  constructor(
    private route: ActivatedRoute,
    private categoryService: CategoryService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const categoryName = params['name'];
      this.categoryService.getCategories().subscribe(categories => {
        this.category = categories.find(c => c.name === categoryName);
      });
    });
  }

  getFullImageUrl(photoUrl: string): string {
    return photoUrl ? `${this.imageBaseUrl}${photoUrl}` : 'assets/okOk.svg';
  }

}
