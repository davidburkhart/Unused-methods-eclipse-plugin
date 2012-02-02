package unused.methods;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.junit.Before;
import org.junit.Rule;

/**
 * Do not use classes from jdt in example code as no jre or classpath is set up
 * in test project!!!
 */
public class PdeTestCaseWithTestProject {

	@Rule
	public ProjectInWorkspace project = new ProjectInWorkspace().withJavaNature();

	@Before
	public void createTestJavaProject() throws CoreException, IOException {
		project.createFolder("src");
		project.createFolder("src/test");
		project.createFile("src/test/Movie.java", new ResourceAsString("/test/Movie.java_").read());
		project.createFile("src/test/Actor.java", new ResourceAsString("/test/Actor.java_").read());
		project.asJavaProject();
	}
}
