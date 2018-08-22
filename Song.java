/**
 * @author Brandon Nsidinanya
 * A class to store useful information for a song in a play list
 */

import java.io.File;

public class Song {
		
		private File file;
		private String songName;
		private long songLeft;
		
		public Song(File file, String songName) {
			this.file = file;
			this.songName = songName;
		}
		
		public File getFile() {
			return file;
		}
		
		public String getName() {
			return songName;
		}
		
		public void setName(String songName) {
			this.songName = songName;
		}
	
		public String toString() {
			return songName;
		}
		
		public void setSongLeft(long songLeft) {
			this.songLeft = songLeft;
		}
		
		public long getSongLeft() {
			return songLeft;
		}
		
		public boolean equals(Object o) {
			boolean result = o instanceof Song;
			if(result) {
				Song otherSong = (Song) o;
				result = file.getName().equals(otherSong.file.getName());
			}
			return result;	
		}
	}
