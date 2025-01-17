package io.github.edmm.plugins.ansible;

import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.visitor.VisitorHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnsibleLifecycle extends AbstractLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnsibleLifecycle.class);
    public static final String FILE_NAME = "deployment.yml";

    private final TransformationContext context;

    public AnsibleLifecycle(TransformationContext context) {
        this.context = context;
    }

    @Override
    public void transform() {
        LOGGER.info("Begin transformation to Ansible...");
        AnsibleVisitor visitor = new AnsibleVisitor(context);
        VisitorHelper.visit(context.getModel().getComponents(), visitor);
        visitor.populateAnsibleFile();
        LOGGER.info("Transformation to Ansible successful");
    }
}
