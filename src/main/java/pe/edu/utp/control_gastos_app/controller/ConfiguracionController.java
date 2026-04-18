package pe.edu.utp.control_gastos_app.controller;

import pe.edu.utp.control_gastos_app.model.Configuracion;
import pe.edu.utp.control_gastos_app.repository.ConfiguracionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class ConfiguracionController {

    @Autowired
    private ConfiguracionRepository repository;

    @PostMapping("/guardar-saldo")
    public String guardarSaldo(@RequestParam Double saldo) {

        Configuracion config = repository.findAll()
                .stream()
                .findFirst()
                .orElse(null);

        if (config == null) {
            config = new Configuracion();
        }

        config.setSaldoInicial(saldo);

        repository.save(config);

        return "redirect:/";
    }
}