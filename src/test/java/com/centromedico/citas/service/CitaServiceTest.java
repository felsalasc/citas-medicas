package com.centromedico.citas.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.centromedico.cita.model.Cita;
import com.centromedico.cita.repository.CitaRepository;
import com.centromedico.cita.service.CitaService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CitaServiceTest {

    @Mock
    private CitaRepository citaRepository;

    @InjectMocks
    private CitaService citaService;

    private Cita cita;

    @BeforeEach
    void setUp() {
        cita = new Cita();
        cita.setId(1L);
        cita.setPaciente("Ana Torres");
        cita.setMedico("Dr. Gomez");
        cita.setEspecialidad("Medicina General");
        cita.setFecha("2026-05-02");
        cita.setHora("10:30");
        cita.setEstado("PROGRAMADA");
    }

    @Test
    void testListarTodas() {
        List<Cita> expected = Arrays.asList(cita);

        when(citaRepository.findAll()).thenReturn(expected);

        List<Cita> resultado = citaService.listarTodas();

        assertEquals(expected, resultado);
        verify(citaRepository).findAll();
    }

    @Test
    void testBuscarPorId() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));

        Optional<Cita> resultado = citaService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(cita, resultado.get());
        verify(citaRepository).findById(1L);
    }

    @Test
    void testAgendarCitaDisponible() {
        when(citaRepository.existsByMedicoIgnoreCaseAndFechaAndHora(
                cita.getMedico(),
                cita.getFecha(),
                cita.getHora()
        )).thenReturn(false);

        when(citaRepository.save(cita)).thenReturn(cita);

        Optional<Cita> resultado = citaService.agendar(cita);

        assertTrue(resultado.isPresent());
        assertEquals(cita, resultado.get());
        verify(citaRepository).save(cita);
    }

    @Test
    void testAgendarCitaDuplicada() {
        when(citaRepository.existsByMedicoIgnoreCaseAndFechaAndHora(
                cita.getMedico(),
                cita.getFecha(),
                cita.getHora()
        )).thenReturn(true);

        Optional<Cita> resultado = citaService.agendar(cita);

        assertTrue(resultado.isEmpty());
        verify(citaRepository, never()).save(any());
    }

    @Test
    void testActualizarCitaExistente() {
        Cita datos = new Cita();
        datos.setPaciente("Carlos Mendez");
        datos.setMedico("Dra. Ruiz");
        datos.setEspecialidad("Pediatría");
        datos.setFecha("2026-05-03");
        datos.setHora("12:00");
        datos.setEstado("PROGRAMADA");

        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(citaRepository.save(cita)).thenReturn(cita);

        Optional<Cita> resultado = citaService.actualizar(1L, datos);

        assertTrue(resultado.isPresent());
        assertEquals("Carlos Mendez", resultado.get().getPaciente());
        assertEquals("Dra. Ruiz", resultado.get().getMedico());
        assertEquals("Pediatría", resultado.get().getEspecialidad());
        assertEquals("2026-05-03", resultado.get().getFecha());
        assertEquals("12:00", resultado.get().getHora());
        verify(citaRepository).save(cita);
    }

    @Test
    void testCancelarCita() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(citaRepository.save(cita)).thenReturn(cita);

        Optional<Cita> resultado = citaService.cancelar(1L);

        assertTrue(resultado.isPresent());
        assertEquals("CANCELADA", resultado.get().getEstado());
        verify(citaRepository).save(cita);
    }

    @Test
    void testEliminarCitaExistente() {
        when(citaRepository.existsById(1L)).thenReturn(true);

        boolean resultado = citaService.eliminar(1L);

        assertTrue(resultado);
        verify(citaRepository).deleteById(1L);
    }

    @Test
    void testEliminarCitaNoExistente() {
        when(citaRepository.existsById(1L)).thenReturn(false);

        boolean resultado = citaService.eliminar(1L);

        assertFalse(resultado);
        verify(citaRepository, never()).deleteById(anyLong());
    }
}