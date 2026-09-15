package com.kynsoft.report.applications.query.report.estadoCuenta.export;

import com.kynsoft.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class Response implements IResponse {
    private StreamingResponseBody outputStream;
}
