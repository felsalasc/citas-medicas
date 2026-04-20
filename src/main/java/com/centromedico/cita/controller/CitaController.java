package com.centromedico.cita.controller;

import com.centromedico.cita.model.Cita;
import com.centromedico.cita.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/citas")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @GetMapping
    public List<Cita> obtenerTodas() {
        return citaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return citaService.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "No se encontró la cita con id " + id)));
    }

    @GetMapping("/paciente/{paciente}")
    public ResponseEntity<?> obtenerPorPaciente(@PathVariable String paciente) {
        List<Cita> resultado = citaService.buscarPorPaciente(paciente);
        if (resultado.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No se encontraron citas para el paciente " + paciente));
        }
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/medico/{medico}")
    public ResponseEntity<?> obtenerPorMedico(@PathVariable String medico) {
        List<Cita> resultado = citaService.buscarPorMedico(medico);
        if (resultado.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No se encontraron citas para el médico " + medico));
        }
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<?> obtenerPorFecha(@PathVariable String fecha) {
        List<Cita> resultado = citaService.buscarPorFecha(fecha);
        if (resultado.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No se encontraron citas para la fecha " + fecha));
        }
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> obtenerPorEstado(@PathVariable String estado) {
        List<Cita> resultado = citaService.buscarPorEstado(estado);
        if (resultado.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No se encontraron citas con estado " + estado));
        }
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/disponibilidad")
    public List<Map<String, String>> obtenerDisponibilidad() {
        List<Map<String, String>> disponibilidad = new ArrayList<>();

        disponibilidad.add(crearHorario("Dra. Soto", "Medicina General", "2026-04-20", "09:00"));
        disponibilidad.add(crearHorario("Dra. Soto", "Medicina General", "2026-04-20", "11:00"));
        disponibilidad.add(crearHorario("Dr. Muñoz", "Pediatria", "2026-04-21", "10:00"));
        disponibilidad.add(crearHorario("Dra. Diaz", "Traumatologia", "2026-04-22", "12:00"));

        return disponibilidad;
    }

    @GetMapping("/disponibilidad/{medico}")
    public ResponseEntity<?> obtenerDisponibilidadPorMedico(@PathVariable String medico) {
        List<Map<String, String>> disponibilidad = new ArrayList<>();

        if (medico.equalsIgnoreCase("Dra. Soto")) {
            disponibilidad.add(crearHorario("Dra. Soto", "Medicina General", "2026-04-20", "09:00"));
            disponibilidad.add(crearHorario("Dra. Soto", "Medicina General", "2026-04-20", "11:00"));
        } else if (medico.equalsIgnoreCase("Dr. Muñoz")) {
            disponibilidad.add(crearHorario("Dr. Muñoz", "Pediatria", "2026-04-21", "10:00"));
        } else if (medico.equalsIgnoreCase("Dra. Diaz")) {
            disponibilidad.add(crearHorario("Dra. Diaz", "Traumatologia", "2026-04-22", "12:00"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No existe disponibilidad registrada para el médico " + medico));
        }

        return ResponseEntity.ok(disponibilidad);
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Cita cita) {
        cita.setEstado("PROGRAMADA");
        return citaService.agendar(cita)
                .<ResponseEntity<?>>map(nueva -> ResponseEntity.status(HttpStatus.CREATED).body(nueva))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Ya existe una cita para ese médico en esa fecha y hora")));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Cita cita) {
        return citaService.actualizar(id, cita)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "No se encontró la cita con id " + id)));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable Long id) {
        return citaService.cancelar(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "No se encontró la cita con id " + id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (citaService.eliminar(id)) {
            return ResponseEntity.ok(Map.of("mensaje", "Cita eliminada correctamente"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "No se encontró la cita con id " + id));
    }

    private Map<String, String> crearHorario(String medico, String especialidad, String fecha, String hora) {
        Map<String, String> horario = new LinkedHashMap<>();
        horario.put("medico", medico);
        horario.put("especialidad", especialidad);
        horario.put("fecha", fecha);
        horario.put("hora", hora);
        return horario;
    }
}