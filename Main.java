import javax.swing.*;

public class Main {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MusicPlayer mc = new MusicPlayer();
				mc.setVisible(true);
				mc.setLocationRelativeTo(null);
				mc.setResizable(false);
				mc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});
	}
}
