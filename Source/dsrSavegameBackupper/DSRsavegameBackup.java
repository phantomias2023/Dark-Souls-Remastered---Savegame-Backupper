package dsrSavegameBackupper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Dark Souls Remastered Backupper 0.2a
 * 
 * Simple program that picks the Dark Souls : Remastered savegame on the PC
 * (default folder only) and backs it up - every five minutes. Each Backup has a
 * individual date and time written into its program extension.
 * 
 * @author samuelH
 *
 *         last edited 2021-09-14
 */
public class DSRsavegameBackup {

	static DSRsavegameWindow window;

	// default Path for DS:R savegames on windows (+ an individual profile
	// directory)
	static Path defaultPath = Paths.get(System.getProperty("user.home") + "/Documents/NBGI/DARK SOULS REMASTERED");
	static Path savegame;
	static Path backups;

	// time in ms between backups, default = 300000 (5 min)
	static int backupInterval = 300_000;

	// number of maximum Backups - default is -1, which is infinite
	static int maxBackups = -1;

	// runs while true
	static boolean programRunning = true;

	// checks if the path of the savegame is valid
	static boolean checkPath = false;
	static boolean startBackup = false;

	public static void main(String[] args) {

		window = new DSRsavegameWindow();

		// Searches Savegame
		savegame = getSavegame();

		// Creates Backup Directory
		backups = createBackupDirectory(savegame);

		// checks if backup directory exists, then copys save into directory
		if (Files.exists(backups)) {
			System.out.println("All clear!");

			System.out.println("Press 'Start'");
			/**
			 * Loop that creates backup in a determined intervall. Thread sleeps during
			 * cooldown.
			 */
			while (programRunning) {
				if (checkPath && startBackup) {
					createBackup(savegame, backups);
					try {
						// Backup interval in ms
						Thread.sleep(backupInterval);

					} catch (InterruptedException e) {
						System.out.println("Problem with thread pausing occurred. Backup aborted.");
						programRunning = false;
					}
				} else {
					System.out.print("");
				}
			}
		}
	}

	/**
	 * Creates a Backup of the savegame file in the backup directory - with a date-
	 * and timestamp written into the file extension, aborts program if exception
	 * thrown
	 * 
	 * @param savegame - the DS:R savegame file loaded into a Path object
	 * @param backups  - the backup-directory as a Path object
	 */
	private static void createBackup(Path savegame, Path backups) {

		try {
			// Copys savegame into backup directory, with an consecutive index ".bckXX"
			Files.copy(savegame,
					Paths.get(backups.toString() + "\\" + savegame.getFileName() + ".bck_"
							+ new SimpleDateFormat("yyyy_MM_dd-HH_mm").format(new Date())),
					StandardCopyOption.REPLACE_EXISTING);
			System.out.println("Backup Successful...");
			if (backupInterval > 60_000) {

				System.out.println("Next Backup in " + TimeUnit.MILLISECONDS.toMinutes(backupInterval) + " Minutes..");
			} else {
				System.out.println("Next Backup in One Minute or less..");
			}
			if (maxBackups != -1) {
				if (backups.toFile().listFiles().length > maxBackups) {
					backups.toFile().listFiles()[0].delete();
				}
			}
			// logBackup(backups);
		} catch (IOException e) {
			System.out.println("Error while backing up save");
			System.out.println("Program aborted!");
			programRunning = false;
		}

	}

	/**
	 * Creates a directory for all further backup in the same player profile
	 * savegame directory.
	 *
	 * @param savegame - the DS:R savegame file loaded into a Path object
	 * @return the backup-directory as a Path object
	 */

	 static Path createBackupDirectory(Path savegame) {
		Path backupDirectory = Paths.get(savegame.getParent() + "\\Backups");
		if (!Files.isDirectory(backupDirectory)) {
			try {
				Files.createDirectory(backupDirectory);
			} catch (IOException e) {
				System.out.println("Error: couldn't create Backup directory");
			}

		}
		if (Files.isDirectory(backupDirectory)) {
			System.out.println("Created Backup Directory..");
		}
		return backupDirectory;
	}

	/**
	 * Looks for a DS:R savegame file in the default savegame directory and loads it
	 * into a Path object.
	 * 
	 * @return the current DS:R savegame in the default location or null
	 */
	static Path getSavegame() {
		Path savegame = null;

		// current version querk, only works if only a single steam profile is used to
		// play DS:R on the same machine. will fix later.
		if (defaultPath.toFile().listFiles().length > 1) {
			System.out.println("The Default Savegame directory has more than one player profile directory.");
			System.out.println("These profile directories are named numerical, e.g. \"0123456789\".");
			System.out.println("Please choose the Savegame you want to backup.");

			return savegame;
		}

		Path saveDirectory = defaultPath.toFile().listFiles()[0].toPath();
		// test if directory was found
		if (Files.isDirectory(saveDirectory)) {
			System.out.println("Savegame directory found...");

			// searches for the current save and writes location into Path object
			for (File f : saveDirectory.toFile().listFiles()) {
				if (f.isFile() && f.getName().equals("DRAKS0005.sl2")) {
					savegame = f.toPath();
					System.out.println("Savegame found...");
					checkPath = true;
					window.pathField.setText(savegame.toString());
				}
			}
		}
		if (savegame == null) {
			System.out.println("Savegame not found..");
			System.out.println("Select DS:R Savegame!");
			savegame = window.getSavegame();
		}
		return savegame;
	}

	/**
	 * Some Getters and Setters
	 * 
	 */
	public static void setBackupInterval(int backupInterval) {
		DSRsavegameBackup.backupInterval = backupInterval;
	}

	public static void setMaxBackups(int maxBackups) {
		DSRsavegameBackup.maxBackups = maxBackups;
	}

	public static void setProgramRunning(boolean programRunning) {
		DSRsavegameBackup.programRunning = programRunning;
	}

	public static void setCheckPath(boolean checkPath) {
		DSRsavegameBackup.checkPath = checkPath;
	}

	public static void setStartBackup(boolean startBackup) {
		DSRsavegameBackup.startBackup = startBackup;
	}

}
