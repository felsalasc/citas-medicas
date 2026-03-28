package com.centromedico.cita.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.centromedico.cita.model.Cita;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/citas")
public class CitaController {

    private List<Cita> listaCitas = new ArrayList<>();

    public CitaController() {
        listaCitas.add(new Cita(
                1L,
                "Juan Perez",
                "Dra. Soto",
                "Medicina General",
                "2026-04-10",
                "09:00",
                "PROGRAMADA"
        ));

        listaCitas.add(new Cita(
                2L,
                "Ana Rojas",
                "Dr. Muñoz",
                "Pediatria",
                "2026-04-10",
                "10:00",
                "CANCELADA"
        ));

        listaCitas.add(new Cita(
                3L,
                "Pedro Silva",
                "Dra. Diaz",
                "Traumatologia",
                "2026-04-11",
                "11:30",
                "PROGRAMADA"
        ));
    }

    @GetMapping
    public List<Cita> obtenerTodas() {
        return listaCitas;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        for (Cita cita : listaCitas) {
            if (cita.getId().equals(id)) {
                return ResponseEntity.ok(cita);
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "No se encontró la cita con id " + id));
    }

    @GetMapping("/paciente/{paciente}")
    public ResponseEntity<?> obtenerPorPaciente(@PathVariable String paciente) {
        if (paciente.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El nombre del paciente no puede estar vacío"));
        }

        List<Cita> resultado = new ArrayList<>();

        for (Cita cita : listaCitas) {
            if (cita.getPaciente().equalsIgnoreCase(paciente)) {
                resultado.add(cita);
            }
        }

        if (resultado.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No se encontraron citas para el paciente " + paciente));
        }

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/medico/{medico}")
    public ResponseEntity<?> obtenerPorMedico(@PathVariable String medico) {
        if (medico.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El nombre del médico no puede estar vacío"));
        }

        List<Cita> resultado = new ArrayList<>();

        for (Cita cita : listaCitas) {
            if (cita.getMedico().equalsIgnoreCase(medico)) {
                resultado.add(cita);
            }
        }

        if (resultado.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No se encontraron citas para el médico " + medico));
        }

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<?> obtenerPorFecha(@PathVariable String fecha) {
        if (!fechaValida(fecha)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La fecha debe tener formato YYYY-MM-DD"));
        }

        List<Cita> resultado = new ArrayList<>();

        for (Cita cita : listaCitas) {
            if (cita.getFecha().equals(fecha)) {
                resultado.add(cita);
            }
        }

        if (resultado.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No se encontraron citas para la fecha " + fecha));
        }

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> obtenerPorEstado(@PathVariable String estado) {
        if (!estadoValido(estado)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Estado no válido. Use PROGRAMADA, CANCELADA o COMPLETADA"));
        }

        List<Cita> resultado = new ArrayList<>();

        for (Cita cita : listaCitas) {
            if (cita.getEstado().equalsIgnoreCase(estado)) {
                resultado.add(cita);
            }
        }

        if (resultado.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No se encontraron citas con estado " + estado));
        }

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/disponibilidad")
    public List<Map<String, String>> obtenerDisponibilidad() {
        List<Map<String, String>> disponibilidad = new ArrayList<>();

        disponibilidad.add(crearHorario("Dra. Soto", "Medicina General", "2026-04-10", "09:00"));
        disponibilidad.add(crearHorario("Dra. Soto", "Medicina General", "2026-04-10", "11:00"));
        disponibilidad.add(crearHorario("Dr. Muñoz", "Pediatria", "2026-04-10", "12:00"));
        disponibilidad.add(crearHorario("Dra. Diaz", "Traumatologia", "2026-04-11", "10:30"));

        return disponibilidad;
    }

    @GetMapping("/disponibilidad/{medico}")
    public ResponseEntity<?> obtenerDisponibilidadPorMedico(@PathVariable String medico) {
        if (medico.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El nombre del médico no puede estar vacío"));
        }

        List<Map<String, String>> disponibilidad = new ArrayList<>();

        if (medico.equalsIgnoreCase("Dra. Soto")) {
            disponibilidad.add(crearHorario("Dra. Soto", "Medicina General", "2026-04-10", "09:00"));
            disponibilidad.add(crearHorario("Dra. Soto", "Medicina General", "2026-04-10", "11:00"));
        } else if (medico.equalsIgnoreCase("Dr. Muñoz")) {
            disponibilidad.add(crearHorario("Dr. Muñoz", "Pediatria", "2026-04-10", "12:00"));
        } else if (medico.equalsIgnoreCase("Dra. Diaz")) {
            disponibilidad.add(crearHorario("Dra. Diaz", "Traumatologia", "2026-04-11", "10:30"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No existe disponibilidad registrada para el médico " + medico));
        }

        return ResponseEntity.ok(disponibilidad);
    }

    private boolean estadoValido(String estado) {
        return estado.equalsIgnoreCase("PROGRAMADA")
                || estado.equalsIgnoreCase("CANCELADA")
                || estado.equalsIgnoreCase("COMPLETADA");
    }

    private boolean fechaValida(String fecha) {
        return fecha.matches("\\d{4}-\\d{2}-\\d{2}");
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