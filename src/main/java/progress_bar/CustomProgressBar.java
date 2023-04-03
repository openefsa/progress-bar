package progress_bar;


import java.util.ArrayList;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;

public class CustomProgressBar implements IProgressBar {
	
	private static final Logger LOGGER = LogManager.getLogger(CustomProgressBar.class);

	private Composite parent;
	private ProgressBar progressBar;
	private Collection<ProgressListener> listeners;
	
	private int done = 0;
	private double doneFract = 0;  // used to manage fractional progresses
	private int progressLimit = 100;  // set this to limit the bar progress
	private double progressStep = 1;  // progress gained by a single operation step
	
	public CustomProgressBar( Composite parent, int style ) {
		this.parent = parent;
		this.listeners = new ArrayList<>();
		InitialiseGraphics( style );
	}
	
	/**
	 * Creates all the graphics for the progress bar
	 * @param parentShell
	 */
	public void InitialiseGraphics ( int style ) {

		// progress bar
		this.progressBar = new ProgressBar( this.parent, style );
		this.progressBar.setMaximum( 100 );

		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.CENTER;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		this.progressBar.setLayoutData( gridData );
	}
	
	/**
	 * Get the inner progress bar
	 */
	@Override
	public ProgressBar getProgressBar() {
		return this.progressBar;
	}
	
	/**
	 * Add a progress to the progress bar. The current progress
	 * is added to the last progress. If a double < 1 is passed
	 * we accumulate the progresses until we reach an integer, in order
	 * to set the progress bar progresses
	 * @param progress
	 */
	@Override
	public void addProgress ( double progress ) {

		// add to the done fract the double progress
		this.doneFract = this.doneFract + progress;

		// when we reach the 1 with the done progress
		// we can refresh the progress bar adding
		// the integer part of the doneFract
		if ( this.doneFract >= 1 ) {
			
			setProgress ( this.done + (int) this.doneFract );

			// reset the doneFract double
			this.doneFract = 0;
		}
	}
	
	/**
	 * Set the progress of the progress bar
	 * @param percent
	 */
	public void setProgress ( double percent ) {

		if ( percent >= 100 ) {
			this.done = 100;
		}
		else if ( percent < 0 ) {
			this.done = 0;
		}
		else {
			this.done = (int) percent;
		}
		
		// limit progress if required
		if ( this.done > this.progressLimit ) {
			this.done = this.progressLimit;
		}

		for ( ProgressListener listener : this.listeners ) {
			listener.progressChanged(this.done, this.progressLimit);
		}
		
		refreshProgressBar( this.done );
	}
	
	/**
	 * Refresh the progress bar state
	 */
	public void refreshProgressBar ( final int done ) {

		if ( this.progressBar.isDisposed() )
			return;

		Display disp = this.progressBar.getDisplay();

		if ( disp.isDisposed() )
			return;

		disp.asyncExec( new Runnable() {
			@Override
			public void run ( ) {

				if ( CustomProgressBar.this.progressBar.isDisposed() )
					return;

				CustomProgressBar.this.progressBar.setSelection( done );
				CustomProgressBar.this.progressBar.update();
			}
		} );

		try {
			// set a small value! Otherwise the bar will slow all the Tool if often called
			Thread.sleep(11);  
		}
		catch (InterruptedException e) {
			LOGGER.error("There was a problem with the thread ", e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Set a maximum limit for the progress
	 * @param progressLimit
	 */
	public void setProgressLimit(int progressLimit) {
		this.progressLimit = progressLimit;
	}
	
	/**
	 * Remove the limit of progress if it was set
	 * with {@link #setProgressLimit(int)}
	 */
	public void removeProgressLimit() {
		this.progressLimit = 100;
	}
	

	/**
	 * Check if the progress bar is filled at 100%
	 * @return
	 */
	public boolean isCompleted() {
		return this.done >= this.progressLimit;
	}
	
	/**
	 * Set how much should the progress bar increase its
	 * value each operation step. Used with operations that
	 * have several steps to automatize the progress increase
	 * for each step by calling {@link #nextStep()}
	 * @param progressStep
	 */
	public void setProgressStep(double progressStep) {
		this.progressStep = progressStep;
	}
	
	/**
	 * Increase the progress bar according to the
	 * {@link #progressStep} variable
	 */
	public void nextStep() {
		addProgress( this.progressStep );
	}
	
	public boolean isDisposed() {
		return this.progressBar.isDisposed();
	}
	
	public Display getDisplay() {
		return this.progressBar.getDisplay();
	}

	@Override
	public void setLabel(String label) {}

	@Override
	public void close() {}

	@Override
	public void open() {}

	@Override
	public void addProgressListener(ProgressListener listener) {
		this.listeners.add( listener );
	}

	@Override
	public void stop( Exception exception ) {
		for ( ProgressListener listener : this.listeners ) {
			listener.progressStopped( exception );
		}
	}

	@Override
	public void fillToMax() {
		setProgress( this.progressLimit );
	}
}
