/**
 * @author Brandon Nsidinanya 2018
 * A simple music player program using an external library to play MP3 files
 * *NOTE* I'm working on an auto-play feature but I currently do not have a solution 
 */

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.*;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class MusicPlayer extends JFrame{
	
	//dimensions of the music player window
	private static final int WIDTH = 400;
	private static final int HEIGHT = 170;
	
	private FileInputStream fis;
	private JPanel mainPanel;
	private JButton prev, play, next, add, remove, shuffle;
	private JLabel search, current, title;
	private JTextField searchField;
	private List<Song> playList;
	private Player player;
	private int index;
	
	public MusicPlayer() {
		super("Brandon's Music Player");
		
		//creating a panel to use and absolute layout design
		mainPanel = new JPanel(null);
		
		//instantiating the play list
		playList = new ArrayList<Song>();
		
		//instantiating the buttons
		prev = new JButton("PREV");
		play = new JButton("PLAY");
		next = new JButton("NEXT");
		add = new JButton("ADD");
		remove = new JButton("DEL");
		shuffle = new JButton("SHUF");
		
		//instantiating the labels
		search = new JLabel("Search:  ");
		current = new JLabel("Currently Playing:");
		title = new JLabel("NO DATA");
		
		//instantiating TextField
		searchField = new JTextField(12);
		
		//sets up the window and the positioning of the components
		createGUI();
		
		//adds action listeners to every button component present in window
		addActionListeners();
	}

	/**
	 * Creates the main window of the music player. Sets up the positions and dimensions 
	 * of each component present on the window
	 */
	private void createGUI() {
		setSize(new Dimension(WIDTH, HEIGHT));
		getContentPane().add(mainPanel);
		
		search.setBounds(70, 15, 50, 10);
		mainPanel.add(search);
		
		searchField.setBounds(120, 10, 200, 20);
		mainPanel.add(searchField);
		
		current.setBounds(20, 30, WIDTH - 40, 20);
		current.setHorizontalAlignment(SwingConstants.CENTER);
		mainPanel.add(current);
		
		title.setBounds(20, 47, WIDTH - 40, 20);
		title.setHorizontalAlignment(SwingConstants.CENTER);
		mainPanel.add(title);
		
		prev.setBounds(100, 70, WIDTH / 6, 25);
		mainPanel.add(prev);
		
		play.setBounds(100 + WIDTH / 6, 70, WIDTH / 6, 25);
		mainPanel.add(play);
		
		next.setBounds(99 + WIDTH / 3, 70, WIDTH / 6, 25);
		mainPanel.add(next);
		
		remove.setBounds(100, 95, WIDTH / 2 / 3, 25);
		mainPanel.add(remove);
		
		add.setBounds(100 + WIDTH / 6, 95, WIDTH / 6, 25);
		mainPanel.add(add);
		
		shuffle.setBounds(99 + WIDTH / 3, 95, WIDTH / 6, 25);
		mainPanel.add(shuffle);
	}
	
	/**
	 * Add actionListener objects to each component present on the window
	 */
	private void addActionListeners() {
		add.addActionListener(new AddActionList());
		play.addActionListener(new PlayActionList());
		next.addActionListener(new NextActionList());
		prev.addActionListener(new PrevActionList());
		remove.addActionListener(new RemoveActionList());
		shuffle.addActionListener(new ShufActionList());
		searchField.addActionListener(new SearchFieldActionList());
	}

	/**
	 * Updates the song title displayed on the music player
	 * @param title The name of the song that is currently playing
	 */
	private void updateTitle(String title) {
		this.title.setText(title);
	}
	
	/**
	 * Begins playing music within a different Thread so that the user may
	 * still interact with the music player's window while music is playing
	 */
	private void playMusic() {
		new Thread() {
			public void run() {
				if(player != null) {
					try {
						player.play();
					} catch (JavaLayerException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	/**
	 * This method must be called before each time we call the playMusic method.
	 * Prepares the music player before playing an MP3 file
	 */
	private void playMusicPrep() {
		try {
			updateTitle(playList.get(index).getName());
			fis = new FileInputStream(playList.get(index).getFile());
			player = new Player(fis);
			playList.get(index).setSongLeft(fis.available());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//class for the add button on the music player
	private class AddActionList implements ActionListener{
		
		//Allows the user to select an MP3 file, and if this is the first song to be added
		//to the play list, then the song will play automatically, else, the song will be stored
		//in the play list to be played later.
		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("Select an MP3 file");
			fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				String songTitle = fc.getSelectedFile().getName();
				if(!songTitle.substring(songTitle.length() - 4).equals(".mp3")) 
					JOptionPane.showMessageDialog(null, "You must select an MP3 file",
							"Error", JOptionPane.ERROR_MESSAGE);
				else {
					String songName = fc.getSelectedFile().getName();
					if(isSongPresent(songName.substring(0, songName.length() - 4)))
						JOptionPane.showMessageDialog(null, "Song already present "
								+ "in playlist!", "Song Present", JOptionPane.ERROR_MESSAGE);
					else
						addSong(fc.getSelectedFile());
				}
			}
		}
		
		/**
		 * Creates a new Song object and adds it to the play list. If playList.size() == 1 after
		 * a call to this method, then the song will play, else it will be added to the playList.
		 * @param file The file object of the MP3 file that will be added to the play list
		 */
		private void addSong(File file) {
			Song newSong = new Song(file, file.getName().substring(0, file.getName().length() - 4));
			playList.add(newSong);
			if(playList.size() == 1) {
				playMusicPrep();
				playMusic();
			} else 
				JOptionPane.showMessageDialog(null, "Song has been added to playlist!",
						"Song Added", JOptionPane.NO_OPTION);
		}
		
		/**
		 * Checks the playList to see if a certain song is currently present in the play list
		 * @param songTitle The name of the song that we are looking for
		 * @return true if the song is present in the play list, false otherwise
		 */
		private boolean isSongPresent(String songTitle) {
			for(Song song: playList) 
				if(song.getName().equals(songTitle))
					return true;
			return false;
		}
	}
	
	//class for playing and pausing the current song
	private class PlayActionList implements ActionListener {
		
		private boolean isPlaying = true;
		private long songLeft; //keeps track of the position that we paused at in the song
		
		//If isPlaying is true, then the song will be paused when a call to this function is made,
		//If isPlaying is false, then the song will play from the previous location where the song was paused at
		//If a song is complete, then you must manually move on to another song using the previous and next buttons
		public void actionPerformed(ActionEvent arg0) {
			if(player != null && playList.size() > 0) {
				if(isPlaying) {
					if(!player.isComplete()) {
						try {
							songLeft = fis.available();
						} catch (IOException e) {
							e.printStackTrace();
						}
						isPlaying = false;
						player.close();
					} else
						JOptionPane.showMessageDialog(null, "Song is complete. Select a new song", 
								"Song Complete", JOptionPane.ERROR_MESSAGE);
				} else {
					try {
						isPlaying = true;
						fis = new FileInputStream(playList.get(index).getFile());
						fis.skip((long)(playList.get(index).getSongLeft() - songLeft));
						player = new Player(fis);
						playMusic();
					} catch (IOException | JavaLayerException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	
	//class to handle moving onto the next song in the play list
	private class NextActionList implements ActionListener {
		
		//plays the next song in the play list. if the last song is playing in the play list, the play list returns 
		//to the first song in the playList. This method removes and creates the actionListener for the 
		//play button in order to accurately pause and play each song that we move to
		public void actionPerformed(ActionEvent arg0) {
			if(playList.size() > 0) {
				index++;
				if(index == playList.size())
					index = 0;
				if(player != null) {
					player.close();
					playMusicPrep();
					play.removeActionListener(play.getActionListeners()[0]);
					play.addActionListener(new PlayActionList());
					playMusic();
				}
			}
		}
		
	}
	
	//class to handle moving onto the previous song in the plya list
	private class PrevActionList implements ActionListener {
		
		//plays the previous song in the play list. if the first song is playing in the play list, the play list returns 
		//to the last song in the playList. This method removes and creates the actionListener for the 
		//play button in order to accurately pause and play each song that we move to
		public void actionPerformed(ActionEvent arg0) {
			if(playList.size() > 0) {
				index--;
				if(index == -1)
					index = playList.size() - 1;
				if(player != null) {
					player.close();
					playMusicPrep();
					play.removeActionListener(play.getActionListeners()[0]);
					play.addActionListener(new PlayActionList());
					playMusic();
				}
			}
		}
	}
	
	
	//class to handle removing songs from play list
	//this class opens a separate window with a list of all songs currently present in the play list 
	private class RemoveActionList implements ActionListener {
		
		//removes song from the play list. if the song removed is the current song, then we move on to
		//the next song. if playList.size() == 0 after this method call, then music ceases to play
		public void actionPerformed(ActionEvent arg0) {
			if(playList.size() == 0) {
				JOptionPane.showMessageDialog(null, "There is no song to delete!", "No Song "
						+ "Present", JOptionPane.ERROR_MESSAGE);
			} else {
				JFrame frame = new JFrame("Select a song to remove");
				JList<String> list = new JList<>();
				JScrollPane panel = new JScrollPane(list);
				DefaultListModel<String> dlm = new DefaultListModel<>();
				list.setModel(dlm);
				list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				for(Song song: playList)
					dlm.addElement("Remove " + song.getName());
				list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
					
					//removes the selected song from the list on the window and from the play list
					public void valueChanged(ListSelectionEvent e) {
						int removeIndex = list.getSelectedIndex();
						if(removeIndex != -1) {
							playList.remove(removeIndex); 
							if(removeIndex < index)
								index--;
							else if(removeIndex == index) {
								if(index == playList.size())
									index = 0;
								if(player != null)
									player.close();
								if(playList.size() > 0) {
									playMusicPrep();
									playMusic();
								} else
									updateTitle("NO DATA");
							}
							dlm.removeElementAt(removeIndex);
							JOptionPane.showMessageDialog(null, "Song has been removed", 
									"Song Removed", JOptionPane.NO_OPTION);
						}
					}
					
				});
				frame.getContentPane().add(panel);
				frame.setSize(new Dimension(400, 120));
				frame.setVisible(true);
				frame.setResizable(false);
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			}
		}
	}
	
	//class that handles shuffling the play list
	private class ShufActionList implements ActionListener {
		
		//shuffles the playList and notifies the user that the play list has been shuffled
		public void actionPerformed(ActionEvent arg0) {
			if(playList.size() > 0) {
				shuffle(new Random());
				JOptionPane.showMessageDialog(null, "Playlist has been shuffled", 
						"Shuffled", JOptionPane.NO_OPTION);
			}
		}
		
		/**
		 * Shuffles the play list and keeps track of which song currently playing
		 * @param rand A random object to randomly choose a position to move a selected song to
		 */
		private void shuffle(Random rand) {
			for(int i = 0; i < playList.size(); i++) {
				int newIndex = rand.nextInt(playList.size());
				Song temp = playList.get(i);
				playList.set(i, playList.get(newIndex));
				playList.set(newIndex, temp);
				if(index == i) {
					index = newIndex;
				} else if(index == newIndex) {
					index = i;
				}
			}
		}
	}
	
	//class that handles searching the songs via the search field on the music player
	private class SearchFieldActionList implements ActionListener {
		
		//plays a song that is selected from the list of songs currently present in the play list
		//any song that contains the same sequence of keys typed in the search field will be displayed
		//in a list and will be played when clicked on
		public void actionPerformed(ActionEvent arg0) {
			String text = searchField.getText();
			JFrame frame = new JFrame("Select a song");
			JList<String> list = new JList<>();
			DefaultListModel<String> dlm = new DefaultListModel<>();
			JScrollPane panel = new JScrollPane(list);
			list.setModel(dlm);
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			for(Song song: playList)
				if(song.getName().toLowerCase().contains(text))
					dlm.addElement(song.getName());
			
			list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				
				//plays the song that is selected
				public void valueChanged(ListSelectionEvent arg0) {
					index = findSongName(list.getSelectedValue());
					if(player != null)
						player.close();
					playMusicPrep();
					playMusic();
				}
				
			});
			
			frame.getContentPane().add(panel);
			frame.setSize(new Dimension(400, 120));
			frame.setVisible(true);
			frame.setResizable(false);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
		
		/**
		 * Finds the position of a certain song that is currently in the play list
		 * @param songName The name of the song that is being searched
		 * @return The position of the song in the play list
		 */
		private int findSongName(String songName) {
			int songIndex = 0;
			while(!songName.equals(playList.get(songIndex).getName()))
				songIndex++;
			return songIndex;
		}
	}
}
