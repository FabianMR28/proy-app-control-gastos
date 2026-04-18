package pe.edu.utp.control_gastos_app.repository;

import pe.edu.utp.control_gastos_app.model.Configuracion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracionRepository extends JpaRepository<Configuracion, Long> {
}