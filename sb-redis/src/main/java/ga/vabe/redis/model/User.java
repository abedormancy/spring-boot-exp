package ga.vabe.redis.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class User {

    public static final User EMPTY = new User();

    private String id;

    private String name;

    private Integer age;

}
