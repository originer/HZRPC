package hzr.spring.provider.mode;

import lombok.Data;

import java.util.Date;

/**
 * @author Zz
 **/
@Data
public class ServiceResult {
    private String result;
    private boolean status;
    private Long  consumTime;
}
