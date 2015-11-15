package unused.methods.ui;

import static org.eclipse.jface.window.Window.OK;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.SelectionDialog;

import unused.methods.UnusedMethodsPlugin;
import unused.methods.core.UnusedMethodAnnotationPreference;
import unused.methods.core.UnusedMethodsPreferences;

public class UnusedMethodsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private UnusedMethodsPreferences preferences;
	private TableViewer tableViewer;

	public UnusedMethodsPreferencePage() {
		super("Unused Methods");
	}

	@Override
	public void init(IWorkbench workbench) {
		preferences = new UnusedMethodsPreferences();
	}

	@Override
	public boolean performOk() {
		try {
			preferences.storeAndFlush();
		} catch (Exception e) {
			setErrorMessage("Could not store preferences: " + e.getMessage());
			return false;
		}
		return super.performOk();
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		Composite tableComposite = new Composite(composite, SWT.NONE);
		tableViewer = createTableViewer(tableComposite);
		tableViewer.setInput(preferences.getPreferences());
		tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite buttonComposite = createButtons(composite);
		buttonComposite.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false));

		Label explanationLabel = new Label(composite, SWT.WRAP);
		explanationLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		explanationLabel.setText("Methods marked with any of the annotations above will never be marked as unused.\n"
				+ "If 'Strongly ignored' is set, also calls from these methods to other methods\n"
				+ "will be ignored, that means if a method marked with such an annotation is the\n"
				+ "only caller of a method the called method will be marked as unused!");

		return composite;
	}

	private Composite createButtons(Composite parent) {
		Composite buttonComposite = new Composite(parent, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(1, false));

		Button addButton = createAddButton(buttonComposite);
		addButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		final Button removeButton = createRemoveButton(buttonComposite);
		removeButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		return buttonComposite;
	}

	private Button createAddButton(Composite buttonComposite) {
		Button addButton = new Button(buttonComposite, SWT.NONE);
		addButton.setText("Add");
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				try {
					IType type = askUserForAnnotationType();
					if (type != null) {
						preferences.add(type.getFullyQualifiedName('.'));
						tableViewer.setInput(preferences.getPreferences());
					}
				} catch (JavaModelException e) {
					String pluginId = UnusedMethodsPlugin.getPluginId();
					Status status = new Status(IStatus.ERROR, pluginId, e.getMessage(), e);
					ErrorDialog.openError(getShell(), "Error", "Error selecting annotations", status);
				}
			}

			private IType askUserForAnnotationType() throws JavaModelException {
				IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
				IRunnableContext context = new ProgressMonitorDialog(getShell());
				int style = IJavaElementSearchConstants.CONSIDER_ANNOTATION_TYPES;
				SelectionDialog dialog = JavaUI.createTypeDialog(getShell(), context, scope, style, true);
				dialog.setTitle("Choose annotation");
				dialog.setMessage("Methods having this annotation will be ignored from unused methods search");
				return dialog.open() == OK ? (IType) dialog.getResult()[0] : null;
			}
		});
		return addButton;
	}

	private Button createRemoveButton(Composite buttonComposite) {
		final Button removeButton = new Button(buttonComposite, SWT.NONE);
		removeButton.setText("Remove");
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				preferences.removeAll(getSelectedItems());
				tableViewer.setInput(preferences.getPreferences());
			}
		});
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				removeButton.setEnabled(!getSelectedItems().isEmpty());
			}
		});
		return removeButton;
	}

	private TableViewer createTableViewer(Composite parent) {
		int style = SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION;
		TableViewer viewer = new TableViewer(parent, style);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);

		TableViewerColumn annotationColumn = createAnnotationColumn(viewer);
		TableViewerColumn stronglyIgnoredColumn = createStronglyIgnoredColumn(viewer);

		TableColumnLayout layout = new TableColumnLayout();
		parent.setLayout(layout);
		layout.setColumnData(annotationColumn.getColumn(), new ColumnWeightData(75));
		layout.setColumnData(stronglyIgnoredColumn.getColumn(), new ColumnWeightData(25));
		return viewer;
	}

	private TableViewerColumn createStronglyIgnoredColumn(TableViewer viewer) {
		TableViewerColumn stronglyIgnoredColumn = new TableViewerColumn(viewer, SWT.CENTER);
		stronglyIgnoredColumn.getColumn().setText("Strongly ignored");
		stronglyIgnoredColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				UnusedMethodAnnotationPreference entry = (UnusedMethodAnnotationPreference) element;
				return entry.isStronglyIgnored() ? "yes" : "no";
			}
		});
		stronglyIgnoredColumn.setEditingSupport(editStronglyIgnored(stronglyIgnoredColumn));
		return stronglyIgnoredColumn;
	}

	private TableViewerColumn createAnnotationColumn(TableViewer viewer) {
		TableViewerColumn annotationColumn = new TableViewerColumn(viewer, SWT.NONE);
		annotationColumn.getColumn().setText("Annotation");
		annotationColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				UnusedMethodAnnotationPreference entry = (UnusedMethodAnnotationPreference) element;
				return entry.getFullyQualifiedName();
			}
		});
		return annotationColumn;
	}

	private EditingSupport editStronglyIgnored(final TableViewerColumn stronglyIgnoredColumn) {
		return new EditingSupport(stronglyIgnoredColumn.getViewer()) {
			@Override
			protected void setValue(Object element, Object value) {
				UnusedMethodAnnotationPreference entry = (UnusedMethodAnnotationPreference) element;
				entry.setStronglyIgnored((Boolean) value);
				stronglyIgnoredColumn.getViewer().refresh(element);
			}

			@Override
			protected Object getValue(Object element) {
				UnusedMethodAnnotationPreference entry = (UnusedMethodAnnotationPreference) element;
				return entry.isStronglyIgnored();
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return new CheckboxCellEditor();
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		};
	}

	private List<UnusedMethodAnnotationPreference> getSelectedItems() {
		List<UnusedMethodAnnotationPreference> selectedAnnotations = new LinkedList<UnusedMethodAnnotationPreference>();
		ISelection selection = tableViewer.getSelection();
		if (!selection.isEmpty() && selection instanceof StructuredSelection) {
			StructuredSelection structuredSelection = (StructuredSelection) selection;
			@SuppressWarnings("unchecked")
			Iterator<UnusedMethodAnnotationPreference> iterator = structuredSelection.iterator();
			while (iterator.hasNext()) {
				UnusedMethodAnnotationPreference next = iterator.next();
				selectedAnnotations.add(next);
			}
		}
		return selectedAnnotations;
	}
}
