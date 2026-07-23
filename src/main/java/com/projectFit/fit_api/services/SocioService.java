package com.projectFit.fit_api.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.projectFit.fit_api.dto.SocioRequestDTO;
import com.projectFit.fit_api.dto.SocioResponseDTO;
import com.projectFit.fit_api.entity.Rol;
import com.projectFit.fit_api.entity.Socio;
import com.projectFit.fit_api.exception.BusinessException;
import com.projectFit.fit_api.exception.ResourceNotFoundException;
import com.projectFit.fit_api.mappers.SocioMapper;
import com.projectFit.fit_api.repository.RolRepository;
import com.projectFit.fit_api.repository.SocioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SocioService {

    private final SocioRepository socioRepository;
    private final SocioMapper socioMapper;
    private final RolRepository rolRepository;

    //CREATE SOCIO desde panel
    public SocioResponseDTO crearSocio(SocioRequestDTO socioRequestDTO){
        if (socioRepository.existsByDni(socioRequestDTO.getDni())) {
            throw new BusinessException("Ya existe un socio con ese DNI");
        }
        if (socioRepository.existsByEmail(socioRequestDTO.getEmail())) {
            throw new BusinessException("Ya existe un socio con ese email");
        }

        Socio socio = socioMapper.toEntity(socioRequestDTO);
        //Buscamos el rol "SOCIO"
        Rol rolSocio = rolRepository.findByNombreRol("SOCIO")
                .orElseThrow(() -> new ResourceNotFoundException("Rol por defecto no configurado"));
        socio.setRol(rolSocio);
        Socio socioGuardado = socioRepository.save(socio);
        return socioMapper.toResponse(socioGuardado);
    }

    //CREATE O GET SOCIO - LOG IN
    public Socio obtenerOCrearSocio(Jwt jwt) {
        String auth0Id = jwt.getSubject();
        String email = jwt.getClaimAsString("email");

        return socioRepository.findByAuth0Id(auth0Id)
                .orElseGet(() -> {
                    //Se busca el rol socio por defecto
                    Rol rolSocio = rolRepository.findByNombreRol("SOCIO")
                            .orElseThrow(() -> new ResourceNotFoundException("Rol por defecto no configurado"));
                    Socio nuevo = new Socio();
                    nuevo.setAuth0Id(auth0Id);
                    nuevo.setEmail(email);
                    nuevo.setRol(rolSocio);
                    return socioRepository.save(nuevo);
                });
    }
    //CHANGE ROL
    public void cambiarRol(Long id, String nuevoRol){
        Socio socioExistente = socioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Socio no encontrado"));
        Rol rolNuevo = rolRepository.findByNombreRol(nuevoRol)
                .orElseThrow(() -> new ResourceNotFoundException("El rol ingresado no existe"));

        socioExistente.setRol(rolNuevo);
        socioRepository.save(socioExistente);
    }

    //UPDATE SOCIO
    public SocioResponseDTO actualizarSocio(Long id, SocioRequestDTO socioRequestDTO){
        Socio socioExistente = socioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Socio no encontrado"));
        socioExistente.setNombre(socioRequestDTO.getNombre());
        socioExistente.setApellido(socioRequestDTO.getApellido());
        socioExistente.setTelefono(socioRequestDTO.getTelefono());
        socioExistente.setFechaNacimiento(socioRequestDTO.getFechaNacimiento());
        socioExistente.setEmail(socioRequestDTO.getEmail());
        Socio socioGuardado = socioRepository.save(socioExistente);
        return socioMapper.toResponse(socioGuardado);
    }

    //DELETE SOCIO
    public void eliminarSocio(Long id) {
        Socio socioExistente = socioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Socio no encontrado"));
        socioRepository.delete(socioExistente);
    }

    //GET ID SOCIO
    public SocioResponseDTO obtenerPorId(Long id) {
        Socio socioExistente = socioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Socio no encontrado"));
        return socioMapper.toResponse(socioExistente);
    }

    //GET ALL SOCIOS
    public List<SocioResponseDTO> obtenerTodos() {
        return socioRepository.findAll()
                .stream()
                .map(socioMapper::toResponse)
                .collect(Collectors.toList());
    }

}
