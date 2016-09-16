package net.akehurst.example.xtext.vertx.language;

import org.eclipse.xtext.xtext.generator.model.project.StandardProjectConfig;
import org.eclipse.xtext.xtext.generator.model.project.SubProjectConfig;

public class ProjectConfig extends StandardProjectConfig {
	@Override
	protected String computeName(final SubProjectConfig project) {
		if (project == this.getGenericIde()) {
			return this.getBaseName();
		}
		return super.computeName(project);
	}
}
