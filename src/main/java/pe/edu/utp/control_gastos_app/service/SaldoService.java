package pe.edu.utp.control_gastos_app.service;


import pe.edu.utp.control_gastos_app.model.Configuracion;
import pe.edu.utp.control_gastos_app.repository.ConfiguracionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaldoService {

    @Autowired
    private GastoService gastoService;

    @Autowired
    private ConfiguracionRepository configRepository;

    public Double calcularSaldo() {

        Configuracion config = configRepository.findAll()
                .stream()
                .findFirst()
                .orElse(null);

        Double saldoInicial = (config != null) ? config.getSaldoInicial() : 0.0;

        return saldoInicial - gastoService.totalGastos();
    }
}