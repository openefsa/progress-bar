package progress_bar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import messages.Messages;
import progress_bar.TableMultipleProgress.TableRow;

/**
 * Form which displays several progress bars related to
 * several background processes. For each progress,
 * create a row in the table by using {@link #addRow(String)}.
 * This method returns the created row, from which it is
 * possible to access the progress bar using {@link TableRow#getBar()}.
 * This progress bar can be shared in threads and updated
 * directly by them using {@link IProgressBar#addProgress(double)}.
 * 
 * @author avonva
 * @author shahaal
 *
 */
public class FormMultipleProgress {

	private Shell shell;
	private TableMultipleProgress table;
	private Shell dialog;
	private Button okBtn;
	private Listener closeListener;
	
	public FormMultipleProgress( Shell shell ) {
		this.shell = shell;
		init();
	}

	/**
	 * Initialise graphics
	 */
	public void init() {
		
		this.dialog = new Shell( this.shell, SWT.TITLE | SWT.RESIZE | SWT.BORDER | SWT.APPLICATION_MODAL );

		this.dialog.setLayout( new GridLayout( 1, false ) );

		this.dialog.setSize( 500, 300 );

		// do not close this window until finished
		this.closeListener = new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				arg0.doit = false;
				return;
			}
		};
		
		// block closure of window
		this.dialog.addListener( SWT.Close, this.closeListener );
		
		this.table = new TableMultipleProgress ( this.dialog );
		
		this.okBtn = new Button ( this.dialog, SWT.NONE );
		this.okBtn.setText( Messages.getString( "ProgressTable.CloseBtn" ) );
		this.okBtn.setEnabled( false );

		this.okBtn.setLayoutData( new GridData(SWT.CENTER, SWT.CENTER, true, false) );
		
		// close if clicked
		this.okBtn.addSelectionListener( new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				FormMultipleProgress.this.dialog.close();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {}
		});
	}
	
	/**
	 * Open and visualize the window
	 */
	public void open() {
		this.dialog.open();
	}
	
	/**
	 * Add a row to the table
	 * @param taskName
	 * @return
	 */
	public TableRow addRow ( String taskName ) {
		return this.table.addRow(taskName);
	}

	/**
	 * Make the dialog closeable by the user
	 */
	public void done() {
		this.dialog.removeListener( SWT.Close, this.closeListener );
		this.okBtn.setEnabled( true );
	}
}
