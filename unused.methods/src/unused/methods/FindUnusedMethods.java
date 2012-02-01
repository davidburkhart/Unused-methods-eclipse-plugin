package unused.methods;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;

public class FindUnusedMethods extends Job {

	private final List<IJavaProject> javaProjects;
	private UnusedMethodsCollector collector;
	private List<IMethod> result;

	public FindUnusedMethods(List<IJavaProject> javaProjects) {
		super("Find unused methods");
		this.javaProjects = javaProjects;
		setUser(true);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		calculate(monitor);
		return Status.OK_STATUS;
	}

	public List<IMethod> getUnusedMethods() {
		return result;
	}

	void calculate(IProgressMonitor monitor) {
		collector = new UnusedMethodsCollector();

		int totalWork = javaProjects.size() + 1;
		monitor.beginTask("Searching java projects for unused methods", totalWork);

		for (IJavaProject javaProject : javaProjects) {
			monitor.subTask("Collecting methods from " + javaProject.getElementName());
			tryCollectFrom(javaProject);
			monitor.worked(1);
		}

		monitor.subTask("Analyzing methods for usage");
		result = collector.computeResult();
		monitor.worked(1);

		monitor.done();
	}

	private void tryCollectFrom(IJavaProject javaProject) {
		try {
			collectFrom(javaProject);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private void collectFrom(IJavaProject javaProject) throws JavaModelException {
		if (javaProject.getProject().isOpen() && !javaProject.isOpen()) {
			javaProject.open(new NullProgressMonitor());
		}
		if (javaProject.isOpen()) {
			new CollectUnusedMethodsFrom(javaProject, collector).run();
			printPackageInfos(javaProject);
		}
	}

	private void printPackageInfos(IJavaProject javaProject) throws JavaModelException {
		IPackageFragment[] packages = javaProject.getPackageFragments();
		for (IPackageFragment mypackage : packages) {
			System.out.println("Package " + mypackage.getElementType() + ": " + mypackage.getElementName());
		}
	}
}
