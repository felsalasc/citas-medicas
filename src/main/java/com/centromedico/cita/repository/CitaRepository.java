package com.centromedico.cita.repository;

import com.centromedico.cita.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByPacienteIgnoreCase(String paciente);
    List<Cita> findByMedicoIgnoreCase(String medico);
    List<Cita> findByFecha(String fecha);
    List<Cita> findByEstadoIgnoreCase(String estado);
    boolean existsByMedicoIgnoreCaseAndFechaAndHora(String medico, String fecha, String hora);
}