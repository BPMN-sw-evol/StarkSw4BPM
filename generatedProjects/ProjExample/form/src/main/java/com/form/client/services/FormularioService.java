package com.form.client.services;

import com.form.client.dto.FormularioDTO;
import com.form.client.model.Formulario;
import com.form.client.repository.FormularioRepository;
import org.springframework.stereotype.Service;

@Service
public class FormularioService {

    private final FormularioRepository formularioRepository;

    public FormularioService(FormularioRepository formularioRepository) {
        this.formularioRepository = formularioRepository;
    }

    public Formulario guardarFormulario(FormularioDTO dto) {
        Formulario formulario = new Formulario();
        formulario.setNombre(dto.getNombre());
        formulario.setEdad(dto.getEdad());
        formulario.setFechaNacimiento(dto.getFechaNacimiento());
        formulario.setCorreo(dto.getCorreo());
        formulario.setActivo(dto.getActivo());

        return formularioRepository.save(formulario);
    }
}

