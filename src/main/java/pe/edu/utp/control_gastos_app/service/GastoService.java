package pe.edu.utp.control_gastos_app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.utp.control_gastos_app.model.Gasto;
import pe.edu.utp.control_gastos_app.repository.GastoRepository;

import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GastoService {

    @Autowired
    private GastoRepository repository;

    // 🔹 GUARDAR
    public void guardar(Gasto gasto) {
        repository.save(gasto);
    }

    // 🔹 LISTAR
    public List<Gasto> listar() {
        return repository.findAll();
    }

    // 🔹 ELIMINAR
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    // 🔹 FILTROS (historial)
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

    // 🔹 TOTAL GENERAL
    public Double totalGastos() {
        return repository.findAll()
                .stream()
                .mapToDouble(Gasto::getMonto)
                .sum();
    }

    // 🔹 GASTOS DEL MES
    public Double gastosMes() {
        return repository.findAll().stream()
                .filter(g -> g.getFecha().getMonth() == LocalDateTime.now().getMonth()
                        && g.getFecha().getYear() == LocalDateTime.now().getYear())
                .mapToDouble(Gasto::getMonto)
                .sum();
    }

    // 🔹 GASTOS DE LA SEMANA
    public Double gastosSemana() {
        WeekFields weekFields = WeekFields.ISO;

        int semanaActual = LocalDateTime.now().get(weekFields.weekOfWeekBasedYear());
        int añoActual = LocalDateTime.now().getYear();

        return repository.findAll().stream()
                .filter(g -> g.getFecha().getYear() == añoActual &&
                        g.getFecha().get(weekFields.weekOfWeekBasedYear()) == semanaActual)
                .mapToDouble(Gasto::getMonto)
                .sum();
    }

    // 🔹 GASTOS POR CATEGORÍA
    public Map<String, Double> gastosPorCategoria() {
        return repository.findAll().stream()
                .collect(Collectors.groupingBy(
                        g -> g.getCategoria() != null
                                ? g.getCategoria().getNombre()
                                : "Sin categoría",
                        Collectors.summingDouble(Gasto::getMonto)
                ));
    }

    // 🔹 CATEGORÍA CON MÁS GASTO
    public Map.Entry<String, Double> categoriaMayorGasto() {
        return gastosPorCategoria().entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);
    }

    // 🔹 ESTADO FINANCIERO
    public String estadoFinanciero() {
        double total = totalGastos();

        if (total < 100) return "Muy bien";
        if (total < 300) return "Bien";
        if (total < 600) return "Regular";
        if (total < 1000) return "Mal";
        return "Crítico";
    }
}