package unused.methods.core;

import static java.util.Collections.singletonList;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Before;
import org.junit.Rule;

/**
 * Do not use classes from jdt in example code as no jre or classpath is set up
 * in test project!!!
 */
public abstract class PdeTestCaseWithTestProject {

	@Rule
	public ProjectInWorkspace project = new ProjectInWorkspace()
			.withJavaNature();

	@Before
	public void createTestJavaProject() throws CoreException, IOException {
		project.createFolder("src");
		project.createFolder("src/test");
		for (String file : getTestFiles()) {
			project.createFile("src" + file,
					new ResourceAsString(file + "_").read());
		}
		project.asJavaProject();
	}

	protected abstract String[] getTestFiles();

	protected List<IMethod> calculateUnusedMethods() throws JavaModelException {
		List<IJavaElement> projects = singletonList((IJavaElement) project
				.asJavaProject());
		FindUnusedMethodsJob finder = new FindUnusedMethodsJob(projects);
		finder.run(new NullProgressMonitor());
		return finder.getUnusedMethods();
	}

}
