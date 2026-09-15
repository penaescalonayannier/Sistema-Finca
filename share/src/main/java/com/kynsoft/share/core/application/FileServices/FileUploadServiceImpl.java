package com.kynsoft.share.core.application.FileServices;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class FileUploadServiceImpl implements IFileUploadService {

    private final RestTemplate restTemplate;

    @Value("${FileServices.upload.url:http://localhost:8097/api/files/upload}")
    private String uploadUrl;

    public FileUploadServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public UploadResponse uploadJasperToS3(byte[] jasperBytes, String fileName,String folderPath) {
        // Crear el cuerpo de la solicitud multipart
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(jasperBytes) {
            @Override
            public String getFilename() {
                return fileName + ".jasper";
            }
        });
        body.add("folderPath", folderPath);
        body.add("objectId", fileName);

        // Crear la cabecera con tipo de contenido multipart/form-data
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Enviar la petición y obtener la respuesta bloqueante
        ResponseEntity<Map> response = restTemplate.exchange(
                uploadUrl, HttpMethod.POST, requestEntity, Map.class
        );

        // Extraer solo `data` y mapear a `UploadResponse`
        if (response.getBody() != null) {
            Map<String, Object> bodyResponse = (Map<String, Object>) response.getBody().get("body");
            if (bodyResponse != null && bodyResponse.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) bodyResponse.get("data");
                return new UploadResponse((String) data.get("url"), (String) data.get("id")); // ✅ Mapea a `UploadResponse`
            }
        }

        throw new RuntimeException("❌ No se pudo extraer `url` e `id` de la respuesta");
    }
}