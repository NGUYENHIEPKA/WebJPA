package vn.HiepKa.configs;

import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.sitemesh.config.ConfigurableSiteMeshFilter;

public class MySiteMeshFilter extends ConfigurableSiteMeshFilter {

	@Override
	protected void applyCustomConfiguration(SiteMeshFilterBuilder builder) {
		builder.addDecoratorPath("/*", "/web.jsp")
				.addDecoratorPath("/admin/*", "/admin.jsp")
				.addDecoratorPath("/user/*", "/user.jsp");
	}

}