package pe.edu.utp.control_gastos_app.service;

import pe.edu.utp.control_gastos_app.model.Categoria;
import pe.edu.utp.control_gastos_app.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository repository;

    public List<Categoria> listar() {
        return repository.findAll();
    }
}