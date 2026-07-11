package pe.edu.utp.control_gastos_app.service;

import org.springframework.stereotype.Service;
import pe.edu.utp.control_gastos_app.model.Role;
import pe.edu.utp.control_gastos_app.repository.RoleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }
}