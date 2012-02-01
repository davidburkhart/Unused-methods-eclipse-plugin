package unused.methods;

import static org.eclipse.core.resources.ResourcesPlugin.FAMILY_AUTO_BUILD;
import static org.eclipse.core.resources.ResourcesPlugin.FAMILY_AUTO_REFRESH;
import static org.eclipse.core.resources.ResourcesPlugin.FAMILY_MANUAL_BUILD;
import static org.eclipse.core.runtime.jobs.Job.getJobManager;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
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
		// ResourcesPlugin.getWorkspace().build(FULL_BUILD, new
		// NullProgressMonitor());
		// waitForBuild();
	}

	private void waitForBuild() {
		boolean retry = true;
		while (retry) {
			try {
				getJobManager().join(FAMILY_AUTO_REFRESH, new NullProgressMonitor());
				getJobManager().join(FAMILY_AUTO_BUILD, new NullProgressMonitor());
				getJobManager().join(FAMILY_MANUAL_BUILD, new NullProgressMonitor());
				retry = false;
			} catch (Exception exc) {
				// ignore and retry
			}
		}
		System.out.println(" OK.");
	}
}
