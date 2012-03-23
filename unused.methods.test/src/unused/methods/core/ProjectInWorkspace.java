package unused.methods.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.rules.ExternalResource;

public class ProjectInWorkspace extends ExternalResource {

	private final String projectName;
	private final List<String> natures = new LinkedList<String>();
	private IProject tempProject;

	public ProjectInWorkspace() {
		this.projectName = "testproject";
	}

	public ProjectInWorkspace withJavaNature() {
		natures.add(JavaCore.NATURE_ID);
		return this;
	}

	public IFile createFile(String fileName, String contents) throws CoreException {
		IPath path = new Path(projectName + "/" + fileName);
		IFile tempFile = getWorkspaceRoot().getFile(path);
		create(tempFile, contents);
		return tempFile;
	}

	public IFolder createFolder(String pathString) throws CoreException {
		IPath path = new Path(pathString);
		IFolder folder = tempProject.getFolder(path);
		folder.create(false, false, new NullProgressMonitor());
		return folder;
	}

	@Override
	protected void before() throws Throwable {
		tempProject = createAndOpenProject(projectName);
		setupNatures(tempProject);
	}

	@Override
	protected void after() {
		try {
			Thread.sleep(1000);
			delete(tempProject);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	private void delete(IResource resource) throws CoreException {
		if (resource.exists()) {
			resource.delete(true, new NullProgressMonitor());
		}
	}

	private IProject createAndOpenProject(String testProjectName) throws CoreException {
		IProject project = getWorkspaceRoot().getProject(testProjectName);
		project.create(new NullProgressMonitor());
		project.open(new NullProgressMonitor());
		return project;
	}

	private void setupNatures(IProject project) throws CoreException {
		IProjectDescription description = project.getDescription();
		description.setNatureIds(natures.toArray(new String[natures.size()]));
		project.setDescription(description, new NullProgressMonitor());
	}

	private void create(IFile file, String contents) throws CoreException {
		byte[] bytes = contents.getBytes();
		InputStream content = new ByteArrayInputStream(bytes);
		file.create(content, true, new NullProgressMonitor());
	}

	IJavaProject asJavaProject() throws JavaModelException {
		IJavaProject javaProject = JavaCore.create(tempProject);
		javaProject.open(new NullProgressMonitor());
		return javaProject;
	}
}
