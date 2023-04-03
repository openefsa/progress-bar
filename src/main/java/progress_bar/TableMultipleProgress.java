package progress_bar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import messages.Messages;

/**
 * Table used to show several processes progresses. See
 * {@link FormMultipleProgress} to get more details.
 * 
 * @author avonva
 *
 */
public class TableMultipleProgress {
	
	private static final Logger LOGGER = LogManager.getLogger(TableMultipleProgress.class);

	private Composite parent;
	private Table table;

	public TableMultipleProgress(Composite parent) {
		this.parent = parent;
		createTable();
	}

	/**
	 * Create the table
	 * 
	 * @author shahaal
	 * @author avonva
	 */
	private void createTable() {

		// create table
		this.table = new Table(this.parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		this.table.setHeaderVisible(true);
		this.table.setLinesVisible(true);

		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		this.table.setLayoutData(data);

		// col titles
		String[] titles = { Messages.getString("ProgressTable.TaskCol"),
				Messages.getString("ProgressTable.ProgressCol"), Messages.getString("ProgressTable.StatusCol") };

		// add 3 columns and set name
		for (int i = 0; i < 3; i++) {
			TableColumn col = new TableColumn(this.table, SWT.NONE);
			col.setText(titles[i]);
		}
	}

	/**
	 * Set the table input
	 * 
	 * @param tasks
	 */
	public TableRow addRow(String taskName) {

		// for each step add a record with bar
		TableRow row = new TableRow(this.table, taskName);

		this.table.getColumn(0).pack();
		this.table.getColumn(1).setWidth(128);
		this.table.getColumn(2).setWidth(256);

		LOGGER.debug("Table row ", row);
		return row;
	}

	/**
	 * Class which represents a row in the table
	 * 
	 * @author avonva
	 *
	 */
	public static class TableRow {

		// status of the progress
		public static String READY = Messages.getString("ProgressTable.Ready");
		public static String ONGOING = Messages.getString("ProgressTable.Ongoing");
		public static String COMPLETED = Messages.getString("ProgressTable.Completed");
		public static String ABORTED = Messages.getString("ProgressTable.Aborted");

		private Shell shell;
		private String name;
		private String status;
		private CustomProgressBar bar;
		private Table table;
		private TableItem row;
		private TableEditor editor;

		public TableRow(Table table, String name) {
			this.name = name;
			this.status = READY;
			this.table = table;
			this.shell = table.getShell();
			display();
		}

		public void display() {

			this.row = new TableItem(this.table, SWT.NONE);
			this.row.setText(0, this.name);

			// add progress bar
			this.bar = new CustomProgressBar(this.table, SWT.NONE);

			this.bar.addProgressListener(new ProgressListener() {

				@Override
				public void progressChanged(double currentProgress, double maxProgress) {

					if (maxProgress == currentProgress) {
						setStatus(COMPLETED);
					} else {
						setStatus(ONGOING);
					}
				}

				@Override
				public void progressChanged(double currentProgress) {
				}

				@Override
				public void progressStopped(Exception e) {

					// if aborted
					setStatus(ABORTED + ": " + e.getMessage());
				}

				@Override
				public void progressCompleted() {
				}
			});

			this.editor = new TableEditor(this.table);
			this.editor.grabHorizontal = this.editor.grabVertical = true;
			this.editor.setEditor(this.bar.getProgressBar(), this.row, 1);

			setStatus(this.status);
		}

		public TableItem getRow() {
			return this.row;
		}

		public CustomProgressBar getBar() {
			return this.bar;
		}

		public TableEditor getEditor() {
			return this.editor;
		}

		public void setStatus(final String status) {

			// guarantee that we are using the ui thread
			// since this method is called by other threads
			this.shell.getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					TableRow.this.status = status;
					TableRow.this.row.setText(2, status);
				}
			});
		}
	}
}
