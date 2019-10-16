package progress_bar;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;


public class FormProgressBar implements IProgressBar {

	private Shell shell;
	private Shell currentShell;
	private int style;
	private CustomProgressBar progressBar;
	private Label label;
	private String title;

	private boolean opened;        // if the bar is opened or not
	/**
	 * Constructor, Initialise the progress bar
	 * @param shell the shell where to create the progress bar
	 * @param title the title of the progress bar
	 * @param cancelEnabled if the cancel button should be inserted or not
	 */
	public FormProgressBar(Shell shell , String title, boolean cancelEnabled, int style) {

		this.opened = false;

		this.shell = shell;
		this.title = title;
		this.style = style;
		this.InitialiseGraphics(shell, style);
	}

	@Override
	public ProgressBar getProgressBar() {
		return this.progressBar.getProgressBar();
	}

	/**
	 * Initialise the progress bar without cancel button
	 * @param shell the shell where to create the progress bar
	 * @param title the title of the progress bar
	 */
	public FormProgressBar(Shell shell, String title) {
		this(shell, title, false, SWT.TITLE | SWT.PRIMARY_MODAL);
	}

	/**
	 * Reset the progress bar graphics content
	 */
	public void reset() {

		this.shell.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				InitialiseGraphics (FormProgressBar.this.shell, FormProgressBar.this.style);
			}
		});

	}

	/**
	 * Creates all the graphics for the progress bar
	 * @param parentShell
	 */
	public void InitialiseGraphics (Shell parentShell, int style) {

		this.currentShell = new Shell(parentShell, style);
		this.currentShell.setText(this.title);
		this.currentShell.setSize(350, 100);
		this.currentShell.setLayout(new GridLayout(1, true));

		// label for the title
		this.label = new Label(this.currentShell, SWT.NONE);
		this.label.setText(this.title);

		// progress bar
		this.progressBar = new CustomProgressBar(this.currentShell, SWT.SMOOTH);
		
		Monitor primary = parentShell.getMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle pict = this.currentShell.getBounds();
		int x = bounds.x + (bounds.width - pict.width) / 2;
		int y = bounds.y + (bounds.height - pict.height) / 2;
		this.currentShell.setLocation(x, y);
	}

	/**
	 * Set the location of the progress bar
	 * @param x
	 * @param y
	 */
	public void setLocation (int x, int y) {
		this.currentShell.setLocation(x, y);
	}

	/**
	 * Get the location of the progress bar
	 */
	public Point getLocation () {
		return this.currentShell.getLocation();
	}

	/**
	 * Show the progress bar
	 */
	@Override
	public void open () {
		this.opened = true;

		if (!this.shell.isDisposed()) {
			this.shell.getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					FormProgressBar.this.currentShell.open();
				}
			});
		}
	}


	/**
	 * Close the progress bar
	 */
	@Override
	public void close () {

		// set the opened state accordingly
		this.opened = false;

		if (this.progressBar.isDisposed())
			return;

		Display disp = this.progressBar.getDisplay();
		if (disp.isDisposed())
			return;
		disp.asyncExec(new Runnable() {
			@Override
			public void run () {
				if (FormProgressBar.this.currentShell.isDisposed())
					return;
				FormProgressBar.this.currentShell.close();
			}
		});
	}

	/**
	 * Set a maximum limit for the progress
	 * @param progressLimit
	 */
	public void setProgressLimit(int progressLimit) {
		this.progressBar.setProgressLimit(progressLimit);
	}

	public void removeProgressLimit() {
		this.progressBar.removeProgressLimit();
	}

	/**
	 * Set how much should the progress bar increase its
	 * value each operation step. Used with operations that
	 * have several steps to automatize the progress increase
	 * for each step by calling {@link #nextStep()}
	 * @param progressStep
	 */
	public void setProgressStep(double progressStep) {
		this.progressBar.setProgressStep(progressStep);
	}

	/**
	 * Increase the progress bar according to the
	 * {@link #progressStep} variable
	 */
	public void nextStep() {
		this.progressBar.nextStep();
	}

	/**
	 * Add a progress to the progress bar. The current progress
	 * is added to the last progress. If a double < 1 is passed
	 * we accumulate the progresses until we reach an integer, in order
	 * to set the progress bar progresses
	 * @param progress
	 */
	@Override
	public void addProgress (double progress) {
		this.progressBar.addProgress(progress);
	}

	/**
	 * Set the progress of the progress bar
	 * @param percent
	 */
	public void setProgress (double percent) {
		this.progressBar.setProgress(percent);
	}

	/**
	 * Set the bar to 100%
	 */
	@Override
	public void fillToMax() {
		this.progressBar.fillToMax();
	}

	/**
	 * Set the label of the progress bar
	 * @param text
	 */
	@Override
	public void setLabel (String text) {

		if (this.progressBar.isDisposed())
			return;

		// open if necessary
		if (!isOpened())
			open();

		Display disp = this.progressBar.getDisplay();

		if (disp.isDisposed())
			return;

		disp.asyncExec(new Runnable() {
			@Override
			public void run () {

				if (FormProgressBar.this.progressBar.isDisposed())
					return;

				if (FormProgressBar.this.label != null) {
					FormProgressBar.this.label.setText(text);
					FormProgressBar.this.progressBar.getProgressBar().getParent().layout();
				}
			}
		});
	}

	/**
	 * Get if the progress bar is open or not
	 * @return
	 */
	public boolean isOpened() {
		return this.opened;
	}

	/**
	 * Check if the progress bar is filled at 100%
	 * @return
	 */
	public boolean isCompleted() {
		return this.progressBar.isCompleted();
	}

	@Override
	public void addProgressListener(ProgressListener listener) {
		this.progressBar.addProgressListener(listener);
	}

	@Override
	public void stop(Exception e) {
		this.progressBar.stop(e);
	}
}
