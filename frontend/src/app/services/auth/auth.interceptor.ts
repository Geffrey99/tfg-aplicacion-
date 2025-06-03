import { inject } from '@angular/core';
import {HttpRequest, HttpHandlerFn, HttpInterceptorFn} from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req: HttpRequest<any>, next: HttpHandlerFn) => {
  const token = localStorage.getItem('token'); // Obtener el token almacenado

  if (token) {
    // Clonar la solicitud con el token añadido al encabezado Authorization
    const clonedReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
    return next(clonedReq); // Pasar la solicitud clonada al siguiente handler
  }

  return next(req); // Pasar la solicitud original al siguiente handler si no hay token
};
