package dsrSavegameBackupper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;




/**
 * Simple GUI for the DS:R Savegame Backupper. Pretty ugly, but it does its job.
 * Since I suck at Swing, most code is picked from various websites.
 * 
 * @author samue
 *
 */
public class DSRsavegameWindow implements ActionListener, ChangeListener {

	JTextField pathField;
	JSlider interval;
	JComboBox<Integer> maxBackupChooser;
	JButton start;
	JButton abort;
	JLabel maxBackupLabel;
	JLabel intervalLabel;
	JButton openFile;

	public DSRsavegameWindow() {

		/*
		 * Panel and Frame, pretty self explanitory
		 */
		JPanel panel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();

		JFrame frame = new JFrame("Dark Souls: Remastered Savegame Backupper 0.2a");
		frame.setSize(720, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		/*
		 * Textfield that shows the Path including an FileChooser to choose the savegame
		 * Program 'should' choose the correct savegame on its own, but maybe user needs
		 * to select it manually
		 */

		JLabel pathLabel = new JLabel("DS:R Savegame Path:");
		openFile = new JButton("Choose Savegame..");
		openFile.addActionListener(this);
		pathField = new JTextField("Select DS:R Savegame...");
		pathField.setBackground(new Color(255, 255, 255));
		if (DSRsavegameBackup.savegame != null) {
			// check to Prevent NullPointerExeption
			pathField.setText(DSRsavegameBackup.savegame.toString());
		}
		pathField.setEditable(false);

		/*
		 * Slider for choosing the time interval. Default is backup every 5 minutes.
		 */

		interval = new JSlider(JSlider.HORIZONTAL, 0, 90,
				(int) TimeUnit.MILLISECONDS.toMinutes(DSRsavegameBackup.backupInterval));
		interval.setMinorTickSpacing(5);
		interval.setMajorTickSpacing(15);
		interval.setPaintTicks(true);
		interval.setPaintLabels(true);
		interval.addChangeListener(this);
		intervalLabel = new JLabel("Backup Interval (in Minutes): " + interval.getValue());

		/*
		 * Dropdown Menu to choose the maximum number of individual backups- the default
		 * is an unlimited number, which could crowd the drive.
		 */
		maxBackupLabel = new JLabel("Maximum Number of Backups (Default: Unlimited)");
		Integer[] maxBackups = { null, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		maxBackupChooser = new JComboBox<Integer>(maxBackups);
		maxBackupChooser.setSelectedIndex(0);
		maxBackupChooser.addActionListener(this);

		/*
		 * Outputs console text into a textfield. Completely taken from another website.
		 */
		JTextArea console = new JTextArea(8,1);
		PrintStream printStream = new PrintStream(new DSRsavegameConsoleOutput(console));
		System.setOut(printStream);
		System.setErr(printStream);
		JScrollPane scrollConsole = new JScrollPane (console, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		
		start = new JButton("Start");
		abort = new JButton("Abort");
		start.addActionListener(this);
		abort.addActionListener(this);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(pathLabel, gbc);

		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = 1;
		panel.add(pathField, gbc);

		gbc.gridwidth = 1;
		gbc.insets = new Insets(0, 5, 0, 5);
		gbc.gridx = 2;
		panel.add(openFile, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.insets = new Insets(25, 0, 0, 0);
		panel.add(intervalLabel, gbc);

		gbc.insets = new Insets(25, 5, 0, 5);
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		panel.add(interval, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		panel.add(maxBackupLabel, gbc);

		gbc.insets = new Insets(25, 5, 0, 5);
		gbc.gridwidth = 1;
		gbc.gridx = 2;
		gbc.gridy = 3;
		panel.add(maxBackupChooser, gbc);

		gbc.fill = GridBagConstraints.PAGE_START;
		gbc.gridx = 0;
		gbc.gridy = 4;
		panel.add(start, gbc);

		gbc.fill = GridBagConstraints.PAGE_END;
		gbc.gridx = 1;
		gbc.gridy = 4;
		panel.add(abort, gbc);

		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 3;
		gbc.gridheight = 2;
		panel.add(scrollConsole, gbc);

	
		frame.setVisible(true);
		frame.getContentPane().add(panel);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String com = e.getActionCommand();

		/*
		 * Controls the FileChooser. After Pressing the button user can choose a .sl2
		 * file on their hard drive. savegame- Path - file in DSRsamegameBackup will be
		 * overwritten with chosen file.
		 */
		if (com.equals("Choose Savegame..")) {
			JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView());
			j.setFileFilter(new FileNameExtensionFilter("DSR Savegames", "sl2"));
			int r = j.showOpenDialog(null);
			if (r == JFileChooser.APPROVE_OPTION) {
				DSRsavegameBackup.savegame = j.getSelectedFile().toPath();
				pathField.setText(DSRsavegameBackup.savegame.toString());
				if (DSRsavegameBackup.savegame.toString().contains(".sl2")) {
					DSRsavegameBackup.setCheckPath(true);
					System.out.println("Savegame selected..");
					DSRsavegameBackup.backups = DSRsavegameBackup.createBackupDirectory(DSRsavegameBackup.savegame);
				}
			}
		}

		/*
		 * Starts the backups and grays out most of the menue.
		 * 
		 * 
		 */
		if (com.equals("Start")) {
			this.pathField.setEnabled(false);
			this.interval.setEnabled(false);
			this.maxBackupChooser.setEnabled(false);
			this.start.setEnabled(false);
			this.openFile.setEnabled(false);
			this.abort.setText("Close");
			System.out.println("Starting Backups at chosen Interval..");
			DSRsavegameBackup.setStartBackup(true);
		}

		// aborts
		if (com.equals("Abort") || com.equals("Close")) {
			DSRsavegameBackup.setProgramRunning(false);
			System.exit(0);
		}

		// updates maxBackups-variable, if other value is chosen
		if (com.equals("comboBoxChanged")) {
			if (maxBackupChooser.getSelectedItem() == null) {
				DSRsavegameBackup.setMaxBackups(-1);
			} else {
				DSRsavegameBackup.setMaxBackups((int) maxBackupChooser.getSelectedItem());
			}
		}

	}

	@Override
	public void stateChanged(ChangeEvent e) {

		if (!interval.getValueIsAdjusting()) {
			if (interval.getValue() <= 0) {
				interval.setValue(5);
			}
			DSRsavegameBackup.setBackupInterval((int) TimeUnit.MINUTES.toMillis(interval.getValue()));
		}
		intervalLabel.setText("Backup Interval (in Minutes): " + interval.getValue());

	}

	/*
	 * If the getSavegame-method of DSRsavegameBackup cant find the savegame
	 * automatically, user has to choose manually. Overwrites savegame - Path -
	 * Object in DSRsavegameBackup
	 */
	public Path getSavegame() {
		Path savegame = null;

		JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView());
		j.setFileFilter(new FileNameExtensionFilter("DSR Savegames", "sl2"));
		int r = j.showOpenDialog(null);
		if (r == JFileChooser.APPROVE_OPTION) {
			savegame = j.getSelectedFile().toPath();
			if (savegame.toString().contains(".sl2")) {
				DSRsavegameBackup.savegame = savegame;
				pathField.setText(DSRsavegameBackup.savegame.toString());
				DSRsavegameBackup.setCheckPath(true);
				System.out.println("Savegame selected..");
			} else {
				System.out.println("Error - the selected file is not a valid DS:R Savegame");
				return null;
			}
		}

		return savegame;
	}

}
