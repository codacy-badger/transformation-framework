package io.github.ust.edmm.model.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import io.github.ust.edmm.core.parser.Entity;
import io.github.ust.edmm.core.parser.EntityGraph;
import io.github.ust.edmm.core.parser.MappingEntity;
import io.github.ust.edmm.core.parser.support.GraphHelper;
import io.github.ust.edmm.model.Operation;
import io.github.ust.edmm.model.Property;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public abstract class ModelEntity extends DescribableElement {

    public static Attribute<String> EXTENDS = new Attribute<>("extends", String.class);
    public static Attribute<Property> PROPERTIES = new Attribute<>("properties", Property.class);
    public static Attribute<Operation> OPERATIONS = new Attribute<>("operations", Operation.class);

    private boolean transformed = false;

    public ModelEntity(MappingEntity entity) {
        super(entity);
    }

    public Optional<String> getExtends() {
        return Optional.ofNullable(get(EXTENDS));
    }

    public Map<String, Property> getProperties() {
        EntityGraph graph = entity.getGraph();
        Map<String, Property> result = new HashMap<>();
        // Resolve the chain of types
        MappingEntity typeRef = GraphHelper.findTypeEntity(graph, entity).
                orElseThrow(() -> new IllegalStateException("A component must be an instance of an existing type"));
        List<MappingEntity> typeChain = GraphHelper.resolveInheritanceChain(graph, typeRef);
        // Create property objects for all available assignments
        Optional<Entity> propertiesEntity = entity.getChild(PROPERTIES);
        propertiesEntity.ifPresent(value -> populateProperties(result, value));
        // Update current map by property definitions
        for (MappingEntity typeEntity : typeChain) {
            propertiesEntity = typeEntity.getChild(PROPERTIES.getName());
            propertiesEntity.ifPresent(value -> populateProperties(result, value));
        }
        return result;
    }

    public Optional<Property> getProperty(String name) {
        return Optional.ofNullable(getProperties().get(name));
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> getProperty(Attribute<T> attribute) {
        Optional<Property> property = getProperty(attribute.getName());
        return (Optional<T>) property.map(Property::getValue);
    }

    public Map<String, Operation> getOperations() {
        EntityGraph graph = entity.getGraph();
        Map<String, Operation> result = new HashMap<>();
        // Resolve the chain of types
        MappingEntity typeRef = GraphHelper.findTypeEntity(graph, entity).
                orElseThrow(() -> new IllegalStateException("A component must be an instance of an existing type"));
        List<MappingEntity> typeChain = GraphHelper.resolveInheritanceChain(graph, typeRef);
        // Create property objects for all available assignments
        Optional<Entity> operationsEntity = entity.getChild(OPERATIONS);
        operationsEntity.ifPresent(value -> populateOperations(result, value));
        // Update current map by property definitions
        for (MappingEntity typeEntity : typeChain) {
            operationsEntity = typeEntity.getChild(OPERATIONS.getName());
            operationsEntity.ifPresent(value -> populateOperations(result, value));
        }
        return result;
    }

    public Optional<Operation> getOperation(String name) {
        return Optional.ofNullable(getOperations().get(name));
    }

    public boolean isTransformed() {
        return transformed;
    }

    public void setTransformed(boolean transformed) {
        this.transformed = transformed;
    }

    private void populateProperties(Map<String, Property> result, Entity entity) {
        Set<Entity> children = entity.getChildren();
        for (Entity child : children) {
            MappingEntity propertyEntity = (MappingEntity) child;
            Property property = new Property(propertyEntity, this.entity);
            result.put(property.getName(), property);
        }
    }

    private void populateOperations(Map<String, Operation> result, Entity entity) {
        Set<Entity> children = entity.getChildren();
        for (Entity child : children) {
            MappingEntity operationEntity = (MappingEntity) child;
            Operation operation = new Operation(operationEntity, this.entity);
            result.put(operation.getName(), operation);
        }
    }
}