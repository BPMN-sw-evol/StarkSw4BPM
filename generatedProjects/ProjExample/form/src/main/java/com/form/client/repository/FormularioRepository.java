package com.form.client.repository;

import com.form.client.model.Formulario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FormularioRepository extends JpaRepository<Formulario, Long> {
    Optional<Formulario> findByNombre(String nombre);

}
