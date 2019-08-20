package io.github.edmm.model.component;

import java.util.Optional;

import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.support.Attribute;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public class Dbms extends SoftwareComponent {

    public static Attribute<Integer> PORT = new Attribute<>("port", Integer.class);
    public static Attribute<String> ROOT_PASSWORD = new Attribute<>("root_password", String.class);

    public Dbms(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    public Optional<Integer> getPort() {
        return getProperty(PORT);
    }

    public Optional<String> getRootPassword() {
        return getProperty(ROOT_PASSWORD);
    }
}