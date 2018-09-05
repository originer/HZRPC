package hzr.spring.provider.model;

import lombok.Data;

/**
 * @author Zz
 **/
@Data
public class ServiceResult {
    private String result;
    private boolean status;
    private Long  consumTime;
}
