package progress_bar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

public class IndeterminateProgressDialog extends Dialog {
	
	private Shell shell;
	private ProgressBar progressBar;
	private String progressLabel;
	
	public IndeterminateProgressDialog(Shell parent, int style, String progressLabel) {
		super(parent, style);
		this.progressLabel = progressLabel;
	}
	
	public IndeterminateProgressDialog(Shell parent, int style) {
		super(parent, style);
	}
	
	public IndeterminateProgressDialog(Shell parent) {
		super(parent);
	}
	
	/**
	 * Creates all the graphics for the progress bar
	 * @param parentShell
	 */
	public void createContents(Shell shell) {

		Composite composite = new Composite(shell , SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		if (this.progressLabel != null) {
			Label label = new Label(composite, SWT.NONE);
			label.setText(this.progressLabel);
		}
		
		// progress bar
		this.progressBar = new ProgressBar(composite, SWT.HORIZONTAL | SWT.INDETERMINATE);

		Monitor primary = getParent().getMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle pict = shell.getBounds();
		int x = bounds.x + (bounds.width - pict.width) / 2;
		int y = bounds.y + (bounds.height - pict.height) / 2;

		shell.setLocation(x, y);
	}
	
	public ProgressBar getProgressBar() {
		return this.progressBar;
	}

	/**
	 * Open the progress bar dialog
	 */
	public void open() {
		
		this.shell = new Shell(getParent(), getStyle());
		this.shell.setSize(300, 130);
		this.shell.setLayout(new FillLayout());
		
		createContents(this.shell);
		
		this.shell.pack();
		this.shell.open();
	}
	
	/**
	 * Close the progress bar dialog
	 */
	public void close() {
		this.shell.close();
	}
}
