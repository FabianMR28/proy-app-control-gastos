package pe.edu.utp.control_gastos_app.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    private String descripcion;
    private Double monto;
    private LocalDateTime fecha;
}
