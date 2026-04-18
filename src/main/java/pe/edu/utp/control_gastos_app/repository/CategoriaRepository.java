package pe.edu.utp.control_gastos_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.control_gastos_app.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
