package pe.edu.utp.control_gastos_app.controller;

import pe.edu.utp.control_gastos_app.model.Gasto;
import pe.edu.utp.control_gastos_app.service.CategoriaService;
import pe.edu.utp.control_gastos_app.service.GastoService;
import pe.edu.utp.control_gastos_app.service.SaldoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
public class GastoController {

    @Autowired
    private GastoService gastoService;

    @Autowired
    private SaldoService saldoService;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping("/")
    public String listar(Model model) {
        model.addAttribute("gastos", gastoService.listar());
        model.addAttribute("saldo", saldoService.calcularSaldo());
        return "gastos";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("gasto", new Gasto());
        model.addAttribute("categorias", categoriaService.listar());
        return "nuevo-gasto";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Gasto gasto) {
        gasto.setFecha(LocalDateTime.now());
        gastoService.guardar(gasto);
        return "redirect:/";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        gastoService.eliminar(id);
        return "redirect:/";
    }

    @GetMapping("/historial")
    public String historial(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) String categoria,
            Model model) {

        model.addAttribute("gastos", gastoService.filtrar(texto, categoria));
        model.addAttribute("categorias", categoriaService.listar());

        model.addAttribute("texto", texto);
        model.addAttribute("categoriaSeleccionada", categoria);

        return "historial";
    }

    @GetMapping("/analisis")
    public String analisis(Model model) {

        Double total = gastoService.totalGastos();
        Double mes = gastoService.gastosMes();
        Double semana = gastoService.gastosSemana();

        var categoriaTop = gastoService.categoriaMayorGasto();
        var categorias = gastoService.gastosPorCategoria();

        // Estado financiero (simple)
        String estado = "Regular";
        if (total < 100) estado = "Muy bien";
        else if (total < 300) estado = "Bien";
        else if (total < 600) estado = "Regular";
        else if (total < 1000) estado = "Mal";
        else estado = "Crítico";

        model.addAttribute("total", total);
        model.addAttribute("mes", mes);
        model.addAttribute("semana", semana);
        model.addAttribute("categoriaTop", categoriaTop);
        model.addAttribute("categorias", categorias);
        model.addAttribute("estado", estado);

        return "analisis";
    }


}