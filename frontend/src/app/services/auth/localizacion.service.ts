import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Observable} from 'rxjs';

import { Provincia } from '../../interface/user';

import { Localidad } from '../../interface/user';


@Injectable({ providedIn: 'root' })

export class LocalizacionService {
  constructor(private http: HttpClient) {}

  private apiUrl = 'https://hhreformas.es/api/localizacion';

  obtenerProvincias(): Observable<Provincia[]> {
    return this.http.get<Provincia[]>(`${this.apiUrl}/provincias`);
  }


  obtenerLocalidadesPorProvincia(provinciaId: number): Observable<Localidad[]> {
    return this.http.get<Localidad[]>(`${this.apiUrl}/provincias/${provinciaId}/localidades`);
  }


}
