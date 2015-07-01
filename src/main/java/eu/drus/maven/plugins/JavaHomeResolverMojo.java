package eu.drus.maven.plugins;

import java.util.Properties;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.toolchain.Toolchain;
import org.apache.maven.toolchain.ToolchainManager;
import org.apache.maven.toolchain.java.DefaultJavaToolChain;
import org.sonatype.plexus.build.incremental.BuildContext;

@Mojo(name = "resolve", defaultPhase = LifecyclePhase.VALIDATE, threadSafe = true)
public class JavaHomeResolverMojo extends AbstractMojo {

    @Component
    private ToolchainManager toolchainManager;

    @Parameter(defaultValue = "${session}", readonly = false, required = true)
    private MavenSession session;

    @Parameter(defaultValue = "${project}", readonly = false, required = true)
    private MavenProject project;

    @Component
    private BuildContext buildContext;

    private DefaultJavaToolChain getToolchain() {
        DefaultJavaToolChain javaToolchain = null;
        if (toolchainManager != null) {
            final Toolchain tc = toolchainManager.getToolchainFromBuildContext("jdk", session);
            if (tc.getClass().equals(DefaultJavaToolChain.class)) {
                // can this ever NOT happen?
                javaToolchain = (DefaultJavaToolChain) tc;
            }
        }
        return javaToolchain;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        String javaHome = null;
        final DefaultJavaToolChain tc = getToolchain();
        if (tc != null) {
            getLog().info("Toolchain in javahome-resolver-maven-plugin: " + tc);
            // we are interested in JAVA_HOME for given jdk
            javaHome = tc.getJavaHome();
        } else {
            javaHome = System.getenv("JAVA_HOME");

            if (javaHome == null) {
                getLog().error("No toolchain configured. No JAVA_HOME configured");
                return;
            }

            getLog().error("No toolchain in javahome-resolver-maven-plugin. Using default JDK[" + javaHome + "]");
        }

        final Properties properties = project.getProperties();

        // expose javaHome to the project
        properties.setProperty("javaHome", javaHome);

        getLog().info("javaHome: " + javaHome);

        // expose javaHome to the build context

        // we have somehow to get all outputDirs (or at least all resources
        // which are enabled for filtering) and tell m2e to refresh the
        // generated files using buildContext.refresh
    }
}
