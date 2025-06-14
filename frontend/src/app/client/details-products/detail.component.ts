import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule, ViewportScroller } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';

import { CartService } from '../../services/features/cart.service';
import { HeaderComponent } from "../../shared/header/header.component";
import { PhotoProductService } from '../../services/PhotoProduct.service';
import { Product } from '../../interface/Product';
import { ProductService } from '../../services/features/product.service';
import { CartItem } from '../../interface/cart';

@Component({
  selector: 'app-detail',
  standalone: true,
  providers: [ProductService, PhotoProductService, CommonModule],
  templateUrl: './detail.component.html',
  styleUrl: './detail.component.css',
  imports: [HttpClientModule, CommonModule, HeaderComponent]
})

export class DetailComponent implements OnInit {
  productId = 0;
  product?: Product;
  products: Product[] = [];
  private imageBaseUrl = 'https://hhreformas.es/api/product-images/';

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private productService: ProductService,
    private PhotoProductService: PhotoProductService,
    private viewportScroller: ViewportScroller,
    private cartService:CartService
  ) { }

  ngOnInit(): void {
    // Obtener el ID del producto de los parámetros de la URL
    const id = this.route.snapshot.paramMap.get('id');
    this.productId = id ? +id : 0;

    // Desplazarse hacia arriba
    this.viewportScroller.scrollToPosition([0, 0]);


    // Cargar el producto correspondiente al ID
    this.loadProduct();

    this.productService.getAllProducts().subscribe(data => {
      this.products = data.map(product => {
        product.photoUrl = product.photoUrl;  // Este campo ya debería contener el nombre de archivo
        return product;
      });


    });

    const routeParams = this.route.snapshot.paramMap;
    const productIdFromRoute = Number(routeParams.get('productId'));

    // Encuentra el producto que corresponde con el id proporcionado en ruta.
    this.product = this.products.find(product => product.id === productIdFromRoute);
  }

  addToCart(product?: Product): void {
    if (!product || product.id == null) {
      window.alert('Error: Producto no disponible.');
      return;
    }
    // Comprueba si hay stock disponible antes de añadir al carrito
    if (product.stock > 0) {
      this.cartService.addToCart(product);
      window.alert('Ok, producto añadido al carrito');
    } else {
      window.alert('Error: No hay stock disponible.');
    }
  }

  loadProduct(): void {
    this.productService.getProductById(this.productId)
      .subscribe(product => {
        this.product = product;
        console.log(this.product);
      });
  }

  getFullImageUrl(photoUrl: string): string {
    return photoUrl ? `${this.imageBaseUrl}${photoUrl}` : 'assets/okOk.svg';
  }

  loadSelectedProduct(productId: number): void {
    this.productService.getProductById(productId)
      .subscribe(product => {
        this.product = product;
        this.productId = productId;
        this.viewportScroller.scrollToPosition([0, 0]);
        console.log('Producto seleccionado:', this.product);
        // Actualiza la URL con el ID del producto seleccionado
        this.router.navigate(['../', productId], { relativeTo: this.route });
      });
  }
}
