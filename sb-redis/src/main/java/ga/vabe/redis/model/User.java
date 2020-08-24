package ga.vabe.redis.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * <b>description:</b><br/>
 * user domain
 * @author Abe
 */
@Data
@ToString
@NoArgsConstructor
public class User implements Serializable {

    private static final User EMPTY = new User();

    private String id;

    private String name;

    private Integer age;

    public static User empty() {
        return EMPTY;
    }

}
