package unused.methods;

import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IWorkspaceRoot;

public class ClearProblemMarkersHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkspaceRoot root = getWorkspace().getRoot();
		UnusedMethodsMarker.clear(root);
		return null;
	}
}
