package com.projectFit.fit_api.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.projectFit.fit_api.dto.SocioRequestDTO;
import com.projectFit.fit_api.dto.SocioResponseDTO;
import com.projectFit.fit_api.entity.Socio;
import com.projectFit.fit_api.exception.BusinessException;
import com.projectFit.fit_api.exception.ResourceNotFoundException;
import com.projectFit.fit_api.mappers.SocioMapper;
import com.projectFit.fit_api.repository.SocioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

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

    //CREATE SOCIO desde panel
    public SocioResponseDTO crearSocio(SocioRequestDTO socioRequestDTO){
        if (socioRepository.existsByDni(socioRequestDTO.getDni())) {
            throw new BusinessException("Ya existe un socio con ese DNI");
        }
        if (socioRepository.existsByEmail(socioRequestDTO.getEmail())) {
            throw new BusinessException("Ya existe un socio con ese email");
        }

        Socio socio = socioMapper.toEntity(socioRequestDTO);
        socio.setQrCode(UUID.randomUUID().toString());
        Socio socioGuardado = socioRepository.save(socio);
        return socioMapper.toResponse(socioGuardado);
    }

    //CREATE O GET SOCIO - LOG IN
    public Socio obtenerOCrearSocio(Jwt jwt) {
        String auth0Id = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String nombre = jwt.getClaimAsString("given_name");
        String apellido = jwt.getClaimAsString("family_name");

        return socioRepository.findByAuth0Id(auth0Id)
                .orElseGet(() -> {
                    Socio nuevo = new Socio();
                    nuevo.setAuth0Id(auth0Id);
                    nuevo.setEmail(email);
                    nuevo.setNombre(nombre != null ? nombre : email);
                    nuevo.setApellido(apellido != null ? apellido : "");
                    nuevo.setQrCode(UUID.randomUUID().toString());
                    nuevo.setRol("SOCIO");
                    return socioRepository.save(nuevo);
                });
    }
    //CHANGE ROL
    public void cambiarRol(Long socioId, String nuevoRol) {
        if (!nuevoRol.equals("ADMIN") &&
                !nuevoRol.equals("RECEPCIONISTA") &&
                !nuevoRol.equals("SOCIO")) {
            throw new BusinessException("Rol inválido");
        }
        Socio socio = socioRepository.findById(socioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Socio no encontrado"));
        socio.setRol(nuevoRol);
        socioRepository.save(socio);
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

    //GENERAR IMAGEN QR
    public byte[] generarImagenQr(String auth0Id) throws Exception {
        Socio socio = socioRepository.findByAuth0Id(auth0Id)
                .orElseThrow(() -> new ResourceNotFoundException("Socio no encontrado"));

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(
                socio.getQrCode(),
                BarcodeFormat.QR_CODE, 300, 300);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        return outputStream.toByteArray();
    }
}
