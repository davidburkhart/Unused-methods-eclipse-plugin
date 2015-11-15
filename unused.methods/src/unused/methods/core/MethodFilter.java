package unused.methods.core;

import java.util.List;

import org.eclipse.jdt.core.dom.IMethodBinding;

public interface MethodFilter {

	boolean accept(IMethodBinding binding, List<IMethodBinding> overriddenMethods);

}
