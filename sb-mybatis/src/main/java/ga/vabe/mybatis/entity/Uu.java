package ga.vabe.mybatis.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author abe
 * @since 2020-03-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ToString
public class Uu implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String firstName;

    private String lastName;

    private LocalDateTime ts;


}
