package com.centromedico.cita.service;

import com.centromedico.cita.model.Cita;
import com.centromedico.cita.repository.CitaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CitaService {

    private final CitaRepository citaRepository;

    public CitaService(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    public List<Cita> listarTodas() {
        return citaRepository.findAll();
    }

    public Optional<Cita> buscarPorId(Long id) {
        return citaRepository.findById(id);
    }

    public List<Cita> buscarPorPaciente(String paciente) {
        return citaRepository.findByPacienteIgnoreCase(paciente);
    }

    public List<Cita> buscarPorMedico(String medico) {
        return citaRepository.findByMedicoIgnoreCase(medico);
    }

    public List<Cita> buscarPorFecha(String fecha) {
        return citaRepository.findByFecha(fecha);
    }

    public List<Cita> buscarPorEstado(String estado) {
        return citaRepository.findByEstadoIgnoreCase(estado);
    }

    public Optional<Cita> agendar(Cita cita) {
        boolean existe = citaRepository.existsByMedicoIgnoreCaseAndFechaAndHora(
                cita.getMedico(), cita.getFecha(), cita.getHora()
        );

        if (existe) {
            return Optional.empty();
        }

        return Optional.of(citaRepository.save(cita));
    }

    public Optional<Cita> actualizar(Long id, Cita datos) {
        return citaRepository.findById(id).map(cita -> {
            cita.setPaciente(datos.getPaciente());
            cita.setMedico(datos.getMedico());
            cita.setEspecialidad(datos.getEspecialidad());
            cita.setFecha(datos.getFecha());
            cita.setHora(datos.getHora());
            cita.setEstado(datos.getEstado());
            return citaRepository.save(cita);
        });
    }

    public Optional<Cita> cancelar(Long id) {
        return citaRepository.findById(id).map(cita -> {
            cita.setEstado("CANCELADA");
            return citaRepository.save(cita);
        });
    }

    public boolean eliminar(Long id) {
        if (citaRepository.existsById(id)) {
            citaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}