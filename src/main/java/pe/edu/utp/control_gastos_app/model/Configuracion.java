package pe.edu.utp.control_gastos_app.model;

import jakarta.persistence.*;

@Entity
public class Configuracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double saldoInicial;

    // Getters y Setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Double getSaldoInicial() { return saldoInicial; }

    public void setSaldoInicial(Double saldoInicial) { this.saldoInicial = saldoInicial; }
}