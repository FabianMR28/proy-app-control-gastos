package pe.edu.utp.control_gastos_app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.utp.control_gastos_app.model.Gasto;
import pe.edu.utp.control_gastos_app.repository.GastoRepository;

import java.util.List;

@Service
public class GastoService {

    @Autowired
    private GastoRepository repository;

    public void guardar(Gasto gasto) {
        repository.save(gasto);
    }

    public List<Gasto> listar() {
        return repository.findAll();
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    public Double totalGastos() {
        return repository.findAll()
                .stream()
                .mapToDouble(Gasto::getMonto)
                .sum();
    }

    public List<Gasto> filtrar(String texto, String categoria) {

        if ((texto == null || texto.isEmpty()) && (categoria == null || categoria.isEmpty())) {
            return repository.findAll();
        }

        if (texto != null && !texto.isEmpty() && categoria != null && !categoria.isEmpty()) {
            return repository.findByDescripcionContainingIgnoreCaseAndCategoriaNombreContainingIgnoreCase(texto, categoria);
        }

        if (texto != null && !texto.isEmpty()) {
            return repository.findByDescripcionContainingIgnoreCase(texto);
        }

        return repository.findByCategoriaNombreContainingIgnoreCase(categoria);
    }

}
