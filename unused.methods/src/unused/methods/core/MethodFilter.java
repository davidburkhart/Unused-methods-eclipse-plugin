package unused.methods.core;

import org.eclipse.jdt.core.dom.IMethodBinding;

public interface MethodFilter {

	boolean accept(MethodWithBinding method, IMethodBinding binding);

}
