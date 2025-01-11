package service.ms_search_engine.data;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BaseAuthRequestData extends BaseRequestData {
    @NotBlank
    private String serverTokenFromMember;
}
