package com.webG.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String apellido;

    private String email;

    private String password;

    private String direccionEnvio;

    private String metodoPago;

    @ManyToOne
    @JoinColumn(name = "id_localidad")
    private Localidad localidad;

    private String rol; 

    public Usuario() {
    }

    public Usuario(String nombre, String apellido, String email, String password, String direccionEnvio, String metodoPago,Localidad localidad, String rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.direccionEnvio = direccionEnvio;
        this.metodoPago = metodoPago;
        this.localidad = localidad;
        this.rol = rol; // Agregar el rol
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDireccionEnvio() {
        return direccionEnvio;
    }

    public void setDireccionEnvio(String direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Localidad getLocalidad() {
        return localidad;
    }

    public void setLocalidad(Localidad localidad) {
        this.localidad = localidad;
    }
    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
    //@Override
   // public String toString() {
    //    return "Usuario{id=" + id + ", nombre='" + nombre + "', apellido='" + apellido + "', email='" + email + "}";
    //}

    @Override
    public String toString() {
        String localidadNombre = (localidad != null) ? localidad.getNombre() : "Sin localidad";
        String provinciaNombre = (localidad != null && localidad.getProvincia() != null) ? localidad.getProvincia().getNombre() : "Sin provincia";

        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", direccionEnvio='" + direccionEnvio + '\'' +
                ", metodoPago='" + metodoPago + '\'' +
                ", localidad=" + localidadNombre +
                ", provincia=" + provinciaNombre +
                ", rol='" + rol + '\'' +
                '}';
    }

}

