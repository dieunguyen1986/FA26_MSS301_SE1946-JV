package fu.ats.dto;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter@Setter
@NoArgsConstructor@AllArgsConstructor
@Builder
public class ResponseApi {
    private HttpStatus code;
    private String message;
    private Object data;
}
