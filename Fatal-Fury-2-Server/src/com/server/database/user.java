package com.server.database;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;

@Entity(name = "USER")
public class user implements Serializable {

    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "NOMBRE", nullable = false,length = 50)
    private String nombre;

    @Column(name = "APELLIDOS", length = 50, nullable = false)
    private String apellidos;

    @Column(name = "DIRECCION", length = 150, nullable = false)
    private String direccion;

    @Column(name = "EMAIL", length = 100, unique = true, nullable = false)
    private String email;

    @Version
    @Column(name = "TELEFONO", unique = true, nullable = false)
    private long telefono;

    public user() {}

    public user(String dni, String nombre, String apellidos, String direccion, String email, int telefono) {
        super();
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.direccion = direccion;
        this.email = email;
        this.telefono = telefono;
    }



    public String getDni() {
        return this.dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return this.apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDireccion() {
        return this.direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getTelefono() {
        return this.telefono;
    }

    public void setTelefono(long telefono) {
        this.telefono = telefono;
    }

    public List<formarParte> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<formarParte> cuentas) {
        this.cuentas = cuentas;
    }

    @Override
    public String toString() {
        return "cliente [dni=" + dni + ", nombre=" + nombre + ", apellidos=" + apellidos + ", direccion=" + direccion
                + ", email=" + email + ", telefono=" + telefono + "]";
    }
}
