package pe.edu.utp.control_gastos_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.control_gastos_app.model.Gasto;

import java.util.List;

public interface GastoRepository extends JpaRepository<Gasto, Long> {

    List<Gasto> findByCategoriaNombreContainingIgnoreCase(String categoria);

    List<Gasto> findByDescripcionContainingIgnoreCase(String texto);

    List<Gasto> findByDescripcionContainingIgnoreCaseAndCategoriaNombreContainingIgnoreCase(String texto, String categoria);
}
